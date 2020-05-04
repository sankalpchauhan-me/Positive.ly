package me.sankalpchauhan.positively;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;

import me.sankalpchauhan.positively.config.DefaultPrefSettings;
import timber.log.Timber;

public class PositivelyApp extends Application {
    private static PositivelyApp instance;

    public static PositivelyApp getInstance(){
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        //Initialization
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        DefaultPrefSettings.init(this);
        MobileAds.initialize(this);
    }
}
