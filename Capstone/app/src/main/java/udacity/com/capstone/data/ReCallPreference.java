package udacity.com.capstone.data;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by techjini on 14/12/15.
 */
public class ReCallPreference {
    private static ReCallPreference sInstance;
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor sEditor;
    public static String FIRST_LAUNCH = "first_launch";
    public static String WHAT_TIME = "what_time";

    public static String WHERE_TIME = "where_time";

    private ReCallPreference(Context context) {
        sPref = context.getSharedPreferences("udacity.com.captsone", Context.MODE_PRIVATE);
        sEditor = sPref.edit();
    }

    public static ReCallPreference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ReCallPreference(context);
        }
        return sInstance;
    }

    public void clearData() {
        sEditor.clear().commit();

    }

    public boolean getBoolean(String key) {
        return sPref.getBoolean(key, false);

    }

    public void setBoolean(String key, boolean value) {
        sEditor.putBoolean(key, value).commit();
    }

    public int getInt(String key) {
        return sPref.getInt(key, 0);
    }

    public long getLong(String key) {
        return sPref.getLong(key, 0);
    }

    public void setInt(String key, int value) {
        sEditor.putInt(key, value).commit();
    }

    public void setLong(String key, long value) {
        sEditor.putLong(key, value).commit();

    }

    public void setString(String key, String value) {
        sEditor.putString(key, value).apply();
    }


    public void remove(String key) {
        sEditor.remove(key).commit();
    }

    public String getString(String key) {
        return sPref.getString(key, "");
    }
}
