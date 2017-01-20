package udacity.com.capstone.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import udacity.com.capstone.BaseFragment;
import udacity.com.capstone.R;
import udacity.com.capstone.data.ReCallPreference;
import udacity.com.capstone.utils.ClickSpan;
import udacity.com.capstone.utils.Constants;
import udacity.com.capstone.utils.Utility;

public class WhatToRememberFragment extends BaseFragment implements RecognitionListener {


    ObjectAnimator animation;
    SpeechRecognizer speech = null;
    RecordComplete mListener;
    @BindView(R.id.txt_what)
    TextView txtData;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    int recordTime = 0;
    String LOG_TAG = WhatToRememberFragment.class.getSimpleName();

    public static WhatToRememberFragment newInstance() {
        WhatToRememberFragment fragment = new WhatToRememberFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_what, container, false);
        ButterKnife.bind(this, rootView);


        initData();
        initAnimation();
        speechBehind();
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
        int value = ReCallPreference.getInstance(getActivity()).getInt(ReCallPreference.WHAT_TIME);
        value = value == 0 ? Constants.MIN_VALUE : value;
        recordTime = value * 1000;
        setText(value);


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

    @Override
    public void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

    }

    private Intent recognizerIntent;

    private void speechBehind() {
        speech = SpeechRecognizer.createSpeechRecognizer(getActivity());
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getActivity().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

    }


    private void record() {
        CountDownTimer countDowntimer = new CountDownTimer(recordTime, 500) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                speech.stopListening();


            }
        };
        countDowntimer.start();
        animation.start();
        speech.startListening(recognizerIntent);


    }


    @Override
    public void onBeginningOfSpeech() {
        Log.e(LOG_TAG, "onBeginningOfSpeech");

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        showLoadingDialog();
        Log.e(LOG_TAG, "onEndOfSpeech");

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        Toast.makeText(getActivity(), errorCode + " " + errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {

    }

    @Override
    public void onPartialResults(Bundle arg0) {

    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {

    }

    @Override
    public void onResults(Bundle results) {
        hideLoadingDialog();
        Log.e(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.e(LOG_TAG, matches.get(0));

        mListener.onRecordComplete(matches.get(0));
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }


    public String getErrorText(int errorCode) {
        int message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = R.string.error_audio_error;
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = R.string.error_client;
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = R.string.error_permission;
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = R.string.error_network;
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = R.string.error_timeout;
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = R.string.error_no_match;
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = R.string.error_busy;
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = R.string.error_server;
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = R.string.error_timeout;
                break;
            default:
                message = R.string.error_understand;
                break;
        }
        return getString(message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecordComplete) {
            mListener = (RecordComplete) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface RecordComplete {
        void onRecordComplete(String what);
    }
}
