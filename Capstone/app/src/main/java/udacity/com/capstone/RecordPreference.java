package udacity.com.capstone;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by techjini on 14/12/15.
 */
public class RecordPreference {
    private static RecordPreference sInstance;
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor sEditor;
    public static final String CENTERR_STATUS = "centerr_status";
    public static final String LOGIN_USER = "login_user";
    public static final String CENTER_ID = "center_id";
    public static final String MERCHANT_ID = "merchant_id";
    public static final String LOGIN_NAME = "login_name";
    public static final String LOGIN_PHONE = "login_phone";
    public static final String LOGIN_PLAN = "login_plan";
    public static final String IS_HOME = "is_home";
    public static final String FIRST_LOGIN = "login_first";
    public static final String LOGIN_LOGO = "login_logo";
    public static final int USER_MERCHANT = 1;
    public static final int USER_STYLIST = 2;
    public static final String OPERATION_HOURS = "operation_hours";
    public static final String STYLIST_OUTLET = "stylist_outlets";


    private RecordPreference(Context context) {
        sPref = context.getSharedPreferences("com.stylemybody.android", Context.MODE_PRIVATE);
        sEditor = sPref.edit();
    }

    public static RecordPreference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RecordPreference(context);
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
