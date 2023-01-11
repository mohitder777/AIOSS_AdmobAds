package com.vid.allvideodownloader;

import static com.vid.allvideodownloader.activity.SplashScreen.idBanner;
import static com.vid.allvideodownloader.activity.SplashScreen.idInter;
import static com.vid.allvideodownloader.activity.SplashScreen.idNative;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vid.allvideodownloader.databinding.ActivityStart2Binding;
import com.vid.allvideodownloader.databinding.ActivityStartBinding;
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

public class StartActivity extends AppCompatActivity {

    TextView btnStartapp;
    ImageView rateApp, shareApp, btnprivacy;
    InterstitialAd mInterstitialAd;
    FirebaseDatabase firebaseDatabase;
    TemplateView my_template;
    RelativeLayout nativeAd;
    AdView bannerView;
    boolean startBanner;
    boolean startInter;
    boolean startNative;
    private ActivityStart2Binding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_start2);

        btnStartapp = (TextView) findViewById(R.id.btnStartapp);
        rateApp = (ImageView) findViewById(R.id.rateApp);
        shareApp = (ImageView) findViewById(R.id.shareApp);
        btnprivacy = (ImageView) findViewById(R.id.btnprivacy);
        bannerView = (AdView) findViewById(R.id.adView);
        my_template = (TemplateView) findViewById(R.id.my_template);
        nativeAd = (RelativeLayout) findViewById(R.id.nativeAd);
        FirebaseApp.initializeApp(StartActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference adsBoolean = firebaseDatabase.getReference("startActivity");
        adsBoolean.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("TagBannerB", ""+snapshot.child("banner").getValue());
                Log.i("TagInterstitialB", ""+snapshot.child("inter").getValue());
                Log.i("TagNativeB", ""+snapshot.child("native").getValue());

                startBanner = (boolean) snapshot.child("banner").getValue();
                startInter = (boolean) snapshot.child("inter").getValue();
                startNative = (boolean) snapshot.child("native").getValue();

                if (startBanner == true){
                    loadBannerAds();
                }

                if (startInter == true){
                    loadInterstitialAds();
                }

                if (startNative == true){
                    loadNativeAds();
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("Tag",error.getMessage());
            }
        });
        btnStartapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show();
                    } else {
                        startActivity(new Intent(StartActivity.this, LanguageActivity.class));
                    }
                } else {
                    startActivity(new Intent(StartActivity.this, LanguageActivity.class));
                }

            }

        });
        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share App");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm using this app, it's allow to download video from all social media platform https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                    startActivity(Intent.createChooser(shareIntent, "Choose one"));
                } catch (Exception e) {
                    Toast.makeText(StartActivity.this, "Error..", Toast.LENGTH_SHORT).show();

                }
            }
        });
        btnprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivacyDialpg();
            }
        });
    }

    private void showPrivacyDialpg() {
        final Dialog dialog = new Dialog(StartActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loan_custom_dialog);

        AppCompatButton btn_agree = (AppCompatButton) dialog.findViewById(R.id.btn_agree);
        TextView tv_notnow = (TextView) dialog.findViewById(R.id.tv_notnow);
        TextView tv_detail = (TextView) dialog.findViewById(R.id.tv_detail);
        TextView tv_detail1 = (TextView) dialog.findViewById(R.id.tv_detail1);
        TextView tv_detail2 = (TextView) dialog.findViewById(R.id.tv_detail2);

        String mystring = new String("Welcome to use " + getString(R.string.app_name) + " Services! In order to protect your personal rights and interests,Please carefully read all terms of our Term of Use and Privacy Policy");
        SpannableString content = new SpannableString(mystring);
        //  content.setSpan(new UnderlineSpan(), mystring.lastIndexOf("Privacy"), mystring.length(), 0);
        // content.setSpan(new ForegroundColorSpan(0xFF3700B3), mystring.lastIndexOf("Privacy"), mystring.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_detail.setText(content);
        tv_detail1.setText("This Privacy Policy describes the information collected by us through our mobile applications and how we use that information.Also our product category is Photography,Video, Entertainment, Tools, Utility, Personalisation.");
        tv_detail2.setText("Your continued use of any Application,the Services or the Site following the posting of any modifications to this Policy will constitute your acceptance of the revised Policy.Please note that none of our employees or agents has the authority to vary any of our policies.");

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_notnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finishAffinity();
                System.exit(0);
            }
        });
        dialog.show();
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
                AdView adView = new AdView(StartActivity.this);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(idBanner);
                bannerView.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(StartActivity.this, idNative);
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
    public void loadInterstitialAds() {
        DatabaseReference Inter = firebaseDatabase.getReference("interId");
        Inter.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                mInterstitialAd = new InterstitialAd(StartActivity.this);
                mInterstitialAd.setAdUnitId(idInter);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                       startActivity(new Intent(StartActivity.this, LanguageActivity.class));
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