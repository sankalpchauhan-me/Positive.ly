package me.sankalpchauhan.positively.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import timber.log.Timber;

public class DefaultPrefSettings {
    private static DefaultPrefSettings ourInstance = new DefaultPrefSettings();
    private final Object object = new Object();
    private SharedPreferences defaultPref;

    private DefaultPrefSettings() {
    }

    public static DefaultPrefSettings getInstance() {
        return ourInstance;
    }

    public static void init(Context context) {
        ourInstance.defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUserAnonymous() {
        synchronized (object) {
            return defaultPref.getBoolean("isAnonymous", false);
        }
    }

    public void setUserAnonymous(boolean value) {
        synchronized (object) {
            Timber.d("setUserAnonymous: " + value);
            SharedPreferences.Editor editor = defaultPref.edit();
            editor.putBoolean("isAnonymous", value);
            editor.apply();
        }
    }

    public String getUserEmail() {
        synchronized (object) {
            return defaultPref.getString("userEmail", "example@xyz.com");
        }
    }

    public void setUserEmail(String value) {
        synchronized (object) {
            SharedPreferences.Editor editor = defaultPref.edit();
            editor.putString("userEmail", value);
            editor.apply();
        }
    }

    public void removeUpdateEmail() {
        synchronized (object) {
            SharedPreferences.Editor editor = defaultPref.edit();
            editor.remove("updateEmail");
            editor.apply();
        }
    }

    public String getUpdateEmail() {
        synchronized (object) {
            return defaultPref.getString("updateEmail", null);
        }
    }
}
