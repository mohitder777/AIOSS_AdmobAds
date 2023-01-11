package com.vid.allvideodownloader.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.vid.allvideodownloader.StartActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.vid.allvideodownloader.R;
import com.vid.allvideodownloader.util.AppLangSessionManager;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class SplashScreen extends AppCompatActivity {
    SplashScreen activity;
    Context context;
    AppUpdateManager appUpdateManager;
    AppLangSessionManager appLangSessionManager;
    AppOpenManager appOpenManager;


    public static String idBanner = "";
    public static String idInter = "";
    public static String idNative = "";
    static FirebaseDatabase firebaseDatabase;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = activity = this;
        appUpdateManager = AppUpdateManagerFactory.create(context);
        appLangSessionManager = new AppLangSessionManager(activity);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        getAds();




        appOpenManager = new AppOpenManager(SplashScreen.this);
        if (appOpenManager.isAdAvailable()){

        }


        UpdateApp();

        setLocale(appLangSessionManager.getLanguage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        appLangSessionManager = new AppLangSessionManager(activity);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, IMMEDIATE, activity, 101);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void HomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, StartActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);

    }

    public void UpdateApp() {
        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, IMMEDIATE, activity, 101);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    HomeScreen();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                HomeScreen();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode != RESULT_OK) {
                HomeScreen();
            } else {
                HomeScreen();
            }
        }
    }


    public void setLocale(String lang) {
        if (lang.equals("")){
            lang="en";
        }
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);



    }

    private void getAds() {
        DatabaseReference Banner = firebaseDatabase.getReference("bannerId");
        Banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idBanner = snapshot.getValue(String.class);
                Log.i("TagBanner",idBanner);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("TagBanner",error.getMessage());
            }
        });

        DatabaseReference Inter = firebaseDatabase.getReference("interId");
        Inter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idInter = snapshot.getValue(String.class);
                Log.i("TagInter",idInter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("TagInter",error.getMessage());
            }
        });

        DatabaseReference Native = firebaseDatabase.getReference("nativeId");
        Native.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idNative = snapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("TagNative",error.getMessage());
            }
        });

    }


}
