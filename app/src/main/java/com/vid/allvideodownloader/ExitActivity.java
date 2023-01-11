package com.vid.allvideodownloader;


import static com.vid.allvideodownloader.activity.SplashScreen.idBanner;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.vid.allvideodownloader.activity.SplashScreen;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExitActivity extends AppCompatActivity {
    TextView exitYes;
    TextView exitNo;
    ImageView exitRate;
    FirebaseDatabase firebaseDatabase;
    TemplateView my_template;
    AdView bannerView;
    InterstitialAd mInterstitialAd;
    RelativeLayout nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        exitYes = (TextView) findViewById(R.id.exitYes);
        exitNo = (TextView) findViewById(R.id.exitNo);
        exitRate = (ImageView) findViewById(R.id.exitRate);
        bannerView = (AdView) findViewById(R.id.adView);
        my_template = (TemplateView) findViewById(R.id.my_template);
        nativeAd = (RelativeLayout) findViewById(R.id.nativeAd);
        FirebaseApp.initializeApp(ExitActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        exitRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
        exitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExitActivity.this, BaseActivity.class));
            }
        });
        exitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show();
                    } else {
                        startActivity(new Intent(ExitActivity.this, ThankyouActivity.class));
                    }
                } else {
                   startActivity(new Intent(ExitActivity.this, ThankyouActivity.class));
                }
                startActivity(new Intent(ExitActivity.this, ThankyouActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public void loadBannerAds() {
        bannerView.setVisibility(View.VISIBLE);
        DatabaseReference Banner = firebaseDatabase.getReference("bannerId");
        Banner.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                AdView adView = new AdView(ExitActivity.this);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(idBanner);
                bannerView.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void loadNativeAds() {
        DatabaseReference Native = firebaseDatabase.getReference("nativeId");
        Native.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                AdLoader.Builder builder = new AdLoader.Builder(ExitActivity.this, SplashScreen.idNative);
                builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        my_template.setVisibility(View.VISIBLE);
                        NativeTemplateStyle thop_nativeTemplateStyle = new NativeTemplateStyle.Builder().build();
                        my_template.setStyles(thop_nativeTemplateStyle);
                        my_template.setNativeAd(unifiedNativeAd);
                    }
                }).build();

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        nativeAd.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }


                }).build();
                adLoader.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void loadInterstitialAds() {
        DatabaseReference Inter = firebaseDatabase.getReference("interId");
        Inter.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                mInterstitialAd = new InterstitialAd(ExitActivity.this);
                mInterstitialAd.setAdUnitId(SplashScreen.idInter);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(ExitActivity.this, ThankyouActivity.class));
                        requestNewInterstitial();
                    }
                });
                requestNewInterstitial();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}