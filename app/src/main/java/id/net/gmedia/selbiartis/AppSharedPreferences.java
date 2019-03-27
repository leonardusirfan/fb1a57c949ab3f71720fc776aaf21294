package id.net.gmedia.selbiartis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPreferences {
    private static final String LOGIN_PREF = "login_status";
    private static final String FCM_PREF = "fcm_id";

    private static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoggedIn(Context context){
        return getPreferences(context).getBoolean(LOGIN_PREF, false);
    }

    public static String getFcmId(Context context){
        return getPreferences(context).getString(FCM_PREF, "");
    }

    public static void setFcmId(Context context, String fcm){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(FCM_PREF, fcm);
        editor.apply();
    }

    public static void setLoggedIn(Context context, boolean login){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, login);
        editor.apply();
    }
}
