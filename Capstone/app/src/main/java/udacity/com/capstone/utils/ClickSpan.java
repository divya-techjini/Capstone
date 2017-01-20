package udacity.com.capstone.utils;

import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by techjini on 20/1/17.
 */

public class ClickSpan extends ClickableSpan {

    private OnClickListener mListener;

    public ClickSpan(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View widget) {
        if (mListener != null) mListener.onClick();
    }

    public interface OnClickListener {
        void onClick();
    }
}



