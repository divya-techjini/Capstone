package udacity.com.capstone.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import udacity.com.capstone.BaseFragment;
import udacity.com.capstone.R;
import udacity.com.capstone.data.ReCallPreference;
import udacity.com.capstone.data.provider.RecordContract;
import udacity.com.capstone.utils.ClickSpan;
import udacity.com.capstone.utils.Constants;
import udacity.com.capstone.utils.RecallWidgetProvider;
import udacity.com.capstone.utils.Utility;

public class WhereItIsFragment extends BaseFragment {

    @BindView(R.id.txt_where)
    TextView txtData;
    @BindView(R.id.txt_item_name)
    TextView txtItemName;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    int recordTime = 0;
    ObjectAnimator animation;
    String what;

    public static WhereItIsFragment newInstance(String what) {
        WhereItIsFragment fragment = new WhereItIsFragment();
        Bundle b = new Bundle();
        b.putString(Constants.EXTRA_ITEM_NAME, what);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_where, container, false);
        ButterKnife.bind(this, rootView);

        initData();
        initAnimation();
        return rootView;
    }


    private void initAnimation() {
        animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500);
        animation.setDuration(recordTime);
        animation.setInterpolator(new DecelerateInterpolator());

    }


    @OnClick(R.id.mic)
    public void onMicClick(View v) {
        record();
    }

    private void initData() {
        what = getArguments().getString(Constants.EXTRA_ITEM_NAME, getString(R.string.lbl_default_record_name));
        txtItemName.setText(getString(R.string.item_name).concat(what));
        int value = ReCallPreference.getInstance(getActivity()).getInt(ReCallPreference.WHERE_TIME);
        value = value == 0 ? Constants.MIN_VALUE : value;
        recordTime = value * 1000;
        setText(value);
        Utility.clickify(txtData, getString(R.string.lbl_here), spanListener);

    }

    ClickSpan.OnClickListener spanListener = new ClickSpan.OnClickListener() {
        @Override
        public void onClick() {
            SMBNumberPickerFragment fragment = SMBNumberPickerFragment.newInstance();
            fragment.setOnValueSelectListener(listener);
            fragment.show(getChildFragmentManager(), "dialog");
        }
    };

    SMBNumberPickerFragment.OnValueSelectListener listener = new SMBNumberPickerFragment.OnValueSelectListener() {
        @Override
        public void onValueSelect(int value) {
            ReCallPreference.getInstance(getActivity()).setInt(ReCallPreference.WHAT_TIME, value);
            recordTime = value * 1000;
            setText(value);
        }
    };

    private void setText(int value) {
        txtData.setText(Html.fromHtml(String.format(getString(R.string.lbl_item_what),
                String.valueOf(value), getString(R.string.lbl_sec))));
        Utility.clickify(txtData, getString(R.string.lbl_here), spanListener);
    }


    private void record() {
        final AudioRecorder recorder = new AudioRecorder(what);


        CountDownTimer countDowntimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {

                    recorder.stop();
                    animation.end();
                    addItem(what, recorder.finalPath);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        };
        countDowntimer.start();
        try {
            recorder.start();
            animation.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void addItem(String what, String where) {
        ContentValues values = new ContentValues();
        values.clear();
        values.put(RecordContract.NAME, what.trim());
        values.put(RecordContract.AUDIO_PATH, where);
        getActivity().getContentResolver().insert(RecordContract.CONTENT_URI, values);
        showSuccessDialog();
    }

    public void showSuccessDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialogStyle);
        ImageView img = new ImageView(getActivity());
        img.setImageResource(R.drawable.ic_done);
        dialog.setView(img);
        img.setPadding(30, 30, 30, 30);
        dialog.setMessage(R.string.lbl_success);
        final AlertDialog dd = (AlertDialog) dialog.show();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dd.dismiss();

            }
        });
        dd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dd.dismiss();
                Intent intent = new Intent(getActivity(), RecallWidgetProvider.class);
                intent.setAction(Constants.ACTION_ITEM_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
                int[] ids = {R.xml.widget_info};
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                getActivity().sendBroadcast(intent);
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        dd.show();

    }
}
