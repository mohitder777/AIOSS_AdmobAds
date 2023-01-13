package com.vid.allvideodownloader.activity;

import static com.vid.allvideodownloader.activity.MainActivity.idInter;
import static com.vid.allvideodownloader.activity.MainActivity.idNative;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.LoadingAds;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.vid.allvideodownloader.R;
import com.vid.allvideodownloader.databinding.ActivityAllGamesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllGamesActivity extends AppCompatActivity {
    ActivityAllGamesBinding binding;
    AllGamesActivity activity;
    UnifiedNativeAd admobNativeAD;
    InterstitialAd mInterstitialAd;
    FirebaseDatabase firebaseDatabase;
    boolean gameBanner;
    boolean gameInter;
    boolean gameNative;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_games);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference adsBoolean = firebaseDatabase.getReference("AllGameAds");

        adsBoolean.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Taggame",""+snapshot.child("gameInter").getValue());
                Log.i("Taggame",""+snapshot.child("gameNative").getValue());

                gameInter = (boolean) snapshot.child("gameInter").getValue();
                gameNative = (boolean) snapshot.child("gameNative").getValue();

                if (gameInter == true){
                    ShowInterstitialAd();
                }

                if (gameNative == true){
                    binding.myTemplate.setVisibility(View.VISIBLE);
                    LoadNativeAd();
                }else{
                    binding.myTemplate.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("Tag",error.getMessage());
            }
        });


        activity = this;
        initViews();
    }

    private void ShowInterstitialAd() {
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(idInter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onBackPressed() {

            if (mInterstitialAd != null){
                mInterstitialAd.show();
            }

        super.onBackPressed();
    }

    public void LoadNativeAd() {
        {
            binding.myTemplate.setVisibility(View.GONE);

            LoadingAds loadingAds = new LoadingAds(this);
            loadingAds.startLoadingDialog();

            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            AdLoader adLoader = new AdLoader.Builder(activity,idNative).withAdListener(new AdListener(){
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            loadingAds.dismissDialog();
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            loadingAds.dismissDialog();
                        }
                    })
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            if (admobNativeAD != null) {
                                admobNativeAD.destroy();
                            }
                            admobNativeAD = unifiedNativeAd;

                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder()
                                    .withCallToActionBackgroundColor
                                            (new ColorDrawable(ContextCompat.getColor(activity, R.color.colorAccent)))
                                    .build();
                            binding.myTemplate.setStyles(styles);
                            binding.myTemplate.setNativeAd(unifiedNativeAd);
                            binding.myTemplate.setBackground(getResources().getDrawable(R.drawable.rectangle_white));
                            binding.myTemplate.setVisibility(View.VISIBLE);


                        }
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());



        }
    }


    private void initViews() {
        binding.imBack.setOnClickListener(v -> onBackPressed());
        binding.RL2048.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, GamesPlayActivity.class);
                i.putExtra("url","2048");
                startActivity(i);
            }
        });
        binding.RLHelix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, GamesPlayActivity.class);
                i.putExtra("url","Helix");
                startActivity(i);
            }
        });
    }
}