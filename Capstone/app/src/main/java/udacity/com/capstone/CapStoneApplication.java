package udacity.com.capstone;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by techjini on 18/1/17.
 */

public class CapStoneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
