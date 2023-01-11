package com.vid.allvideodownloader.activity;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.vid.allvideodownloader.AdError;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String LOG_TAG = "AppOpenManager";
    private static final String AD_UNIT_ID = "ca-app-pub-68959573532223023/3473201416";
    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private Activity currentActivity;
    private static boolean isShowingAd = false;

    private final SplashScreen myApplication;

    /**
     * Constructor
     * @param myApplication
     */
    @SuppressLint("NewApi")
    public AppOpenManager(SplashScreen myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }


    /**
     * Request an ad
     */
    public void fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAppOpenAdLoaded(AppOpenAd ad) {
                        AppOpenManager.this.appOpenAd = ad;
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */

                };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback=new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    AppOpenManager.this.appOpenAd=null;
                    isShowingAd=false;
                    fetchAd();



                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Toast.makeText(currentActivity, "Somthing Worng !!!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onAdShowedFullScreenContent() {
                      isShowingAd=true;
                }
            };


        } else {
            Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;

    }
    /** LifecycleObserver methods */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.d(LOG_TAG, "onStart");
    }

    public class FullScreenContentCallback {
        public void onAdDismissedFullScreenContent() {
            AppOpenManager.this.appOpenAd=null;
            isShowingAd=false;
            fetchAd();
        }

        public void onAdFailedToShowFullScreenContent(AdError adError) {
            Toast.makeText(currentActivity, "Somthing Worng !!!", Toast.LENGTH_SHORT).show();
        }

        public void onAdShowedFullScreenContent() {
            isShowingAd=true;
        }

    }

}