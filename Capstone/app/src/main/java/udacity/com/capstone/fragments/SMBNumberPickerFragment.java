package udacity.com.capstone.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import udacity.com.capstone.R;
import udacity.com.capstone.RecordingActivity;

import static udacity.com.capstone.utils.Constants.MAX_VALUE;
import static udacity.com.capstone.utils.Constants.MIN_VALUE;


/**
 * Created by techjini on 4/2/16.
 */
public class SMBNumberPickerFragment extends DialogFragment {

    OnValueSelectListener mOnValueSelectListener;


    public static SMBNumberPickerFragment newInstance() {
        SMBNumberPickerFragment frag = new SMBNumberPickerFragment();
        return frag;
    }

    public void setOnValueSelectListener(OnValueSelectListener mOnValueSelectListener) {
        this.mOnValueSelectListener = mOnValueSelectListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_AlertDialogStyle);

        View npView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_time_picker, null);

        dialog.setView(npView);
        dialog.setTitle(R.string.title_picker);
        final NumberPicker picker = (NumberPicker) npView.findViewById(R.id.numberPicker);
        final String[] valueSet = new String[MAX_VALUE-MIN_VALUE ];

        for (int i = MIN_VALUE; i <MAX_VALUE; i ++) {

            valueSet[i-MIN_VALUE] = String.valueOf(i);
        }
        picker.setDisplayedValues(valueSet);
        picker.setMaxValue(valueSet.length - 1);

        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog pickerDialog = (AlertDialog) dialogInterface;
                final NumberPicker picker = (NumberPicker) pickerDialog.findViewById(R.id.numberPicker);
                mOnValueSelectListener.onValueSelect( Integer.parseInt(picker.getDisplayedValues()[picker.getValue()]));
            }
        });

        return dialog.create();
    }

    public interface OnValueSelectListener {
        void onValueSelect(int value);
    }
}
