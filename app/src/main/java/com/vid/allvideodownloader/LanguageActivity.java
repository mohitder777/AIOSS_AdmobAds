package com.vid.allvideodownloader;

import static com.vid.allvideodownloader.activity.SplashScreen.idInter;
import static com.vid.allvideodownloader.activity.SplashScreen.idNative;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vid.allvideodownloader.R;
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


import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity {

    TextView btnnextstep;
    RecyclerView languageRecycler;
    List<LanguageModal> languageList;
    LanguageAdapter languageAdapter;
    FirebaseDatabase firebaseDatabase;
    TemplateView my_template;
    AdView bannerView;
    InterstitialAd mInterstitialAd;
    RelativeLayout nativeAd;
    boolean langBanner;
    boolean langinter;
    boolean langNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        btnnextstep = (TextView) findViewById(R.id.btnnextstepL);
        languageRecycler = (RecyclerView) findViewById(R.id.languageRecycler);
        bannerView = (AdView) findViewById(R.id.adView);
        my_template = (TemplateView) findViewById(R.id.my_template);
        nativeAd = (RelativeLayout) findViewById(R.id.nativeAd);
        FirebaseApp.initializeApp(LanguageActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference adsBoolean = firebaseDatabase.getReference("langActivity");
        adsBoolean.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("TagBannerB", ""+snapshot.child("banner").getValue());
                Log.i("TagInterstitialB", ""+snapshot.child("inter").getValue());
                Log.i("TagNativeB", ""+snapshot.child("native").getValue());

                langBanner = (boolean) snapshot.child("banner").getValue();
                langinter = (boolean) snapshot.child("inter").getValue();
                langNative = (boolean) snapshot.child("native").getValue();

                if (langBanner == true){
                    loadBannerAds();
                }

                if (langinter == true){
                    loadInterstitialAds();
                }

                if (langNative == true){
                    loadNativeAds();
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("Tag",error.getMessage());
            }
        });

        languageRecycler.setHasFixedSize(true);
        languageRecycler.setLayoutManager(new LinearLayoutManager(LanguageActivity.this));
        languageList = new ArrayList<>();
        languageList.add(new LanguageModal("English"));
        languageList.add(new LanguageModal("Hindi"));
        languageAdapter = new LanguageAdapter(LanguageActivity.this, languageList);
        languageRecycler.setAdapter(languageAdapter);
        btnnextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show();
                    } else {
                        startActivity(new Intent(LanguageActivity.this, GenderActivity.class));
                    }
                } else {
                    startActivity(new Intent(LanguageActivity.this, GenderActivity.class));
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
                AdView adView = new AdView(LanguageActivity.this);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(SplashScreen.idBanner);
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
                AdLoader.Builder builder = new AdLoader.Builder(LanguageActivity.this, idNative);
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
                mInterstitialAd = new InterstitialAd(LanguageActivity.this);
                mInterstitialAd.setAdUnitId(idInter);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(LanguageActivity.this, GenderActivity.class));
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