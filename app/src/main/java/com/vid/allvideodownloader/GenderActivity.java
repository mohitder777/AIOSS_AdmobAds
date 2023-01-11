package com.vid.allvideodownloader;


import static com.vid.allvideodownloader.activity.SplashScreen.idBanner;
import static com.vid.allvideodownloader.activity.SplashScreen.idNative;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GenderActivity extends AppCompatActivity {

    TextView btnnextstep;
    FirebaseDatabase firebaseDatabase;
    TemplateView my_template;
    AdView bannerView;
    InterstitialAd mInterstitialAd;
    RadioButton radioFemale, radioMale;
    RadioGroup genderRadio;
    RelativeLayout nativeAd;
    boolean ganderBanner;
    boolean ganderInter;
    boolean ganderNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        genderRadio = (RadioGroup) findViewById(R.id.genderRadio);
        btnnextstep = (TextView) findViewById(R.id.btnnextstep);
        bannerView = (AdView) findViewById(R.id.adView);
        my_template = (TemplateView) findViewById(R.id.my_template);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        nativeAd = (RelativeLayout) findViewById(R.id.nativeAd);
        FirebaseApp.initializeApp(GenderActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference adsBoolean = firebaseDatabase.getReference("genderActivity");
        adsBoolean.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("TagBannerB", ""+snapshot.child("banner").getValue());
                Log.i("TagInterstitialB", ""+snapshot.child("inter").getValue());
                Log.i("TagNativeB", ""+snapshot.child("native").getValue());

                ganderBanner = (boolean) snapshot.child("banner").getValue();
                ganderInter = (boolean) snapshot.child("inter").getValue();
                ganderNative = (boolean) snapshot.child("native").getValue();

                if (ganderBanner == true){
                    loadBannerAds();
                }

                if (ganderInter == true){
                    loadInterstitialAds();
                }

                if (ganderNative == true){
                    loadNativeAds();
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("Tag",error.getMessage());
            }
        });


//        genderRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//
//            }
//        });
        btnnextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show();
                    } else {
                        startActivity(new Intent(GenderActivity.this, BaseActivity.class));
                    }
                } else {
                    startActivity(new Intent(GenderActivity.this, BaseActivity.class));
                }

            }
        });
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
                AdView adView = new AdView(GenderActivity.this);
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
                AdLoader.Builder builder = new AdLoader.Builder(GenderActivity.this, idNative);
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
                mInterstitialAd = new InterstitialAd(GenderActivity.this);
                mInterstitialAd.setAdUnitId(SplashScreen.idInter);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(GenderActivity.this, BaseActivity.class));
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