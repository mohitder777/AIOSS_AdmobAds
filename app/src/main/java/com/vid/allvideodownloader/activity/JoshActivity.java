package com.vid.allvideodownloader.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.LoadingAds;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.vid.allvideodownloader.R;
import com.vid.allvideodownloader.api.CommonClassForAPI;
import com.vid.allvideodownloader.databinding.LayoutGlobalUiBinding;
import com.vid.allvideodownloader.util.AppLangSessionManager;
import com.vid.allvideodownloader.util.SharePrefs;
import com.vid.allvideodownloader.util.Utils;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.vid.allvideodownloader.activity.MainActivity.idInter;
import static com.vid.allvideodownloader.activity.MainActivity.idNative;
import static com.vid.allvideodownloader.util.Utils.ROOTDIRECTORYJOSH;
import static com.vid.allvideodownloader.util.Utils.createFileFolder;
import static com.vid.allvideodownloader.util.Utils.startDownload;

public class JoshActivity extends AppCompatActivity {
    LayoutGlobalUiBinding binding;
    JoshActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    AppLangSessionManager appLangSessionManager;
    boolean isvisible=false;
    UnifiedNativeAd admobNativeAD;
    InterstitialAd mInterstitialAd;
    FirebaseDatabase firebaseDatabase;
    boolean joshInter;
    boolean joshNative;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_global_ui);

  MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference adsBoolean = firebaseDatabase.getReference("joshAds");

        adsBoolean.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("TagJosh",""+snapshot.child("joshInter").getValue());
                Log.i("TagJosh",""+snapshot.child("joshNative").getValue());

                joshInter = (boolean) snapshot.child("joshInter").getValue();
                joshNative = (boolean) snapshot.child("joshNative").getValue();

                if (joshInter == true){
                    InterstitialAdsINIT();
                }

                if (joshNative == true){
                    binding.myNative.setVisibility(View.VISIBLE);
                    LoadNativeAd();
                }else{
                    binding.myNative.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("Tag",error.getMessage());
            }
        });

        activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();

        binding.imAppIcon.setImageDrawable(getResources().getDrawable(R.drawable.josh_logo));
        binding.tvAppName.setText(getResources().getString(R.string.josh_app_name));

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());

    }

    private void InterstitialAdsINIT() {

        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(idInter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }


    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
    }

    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.imInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isvisible==false){
                    binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
                    isvisible=true;
                }else{
                    binding.layoutHowTo.LLHowToLayout.setVisibility(View.GONE);
                    isvisible=false;
                }
            }
        });

        Glide.with(activity)
                .load(R.drawable.sc1)
                .into(binding.layoutHowTo.imHowto1);

        Glide.with(activity)
                .load(R.drawable.sc2)
                .into(binding.layoutHowTo.imHowto2);

        Glide.with(activity)
                .load(R.drawable.sc1)
                .into(binding.layoutHowTo.imHowto3);

        Glide.with(activity)
                .load(R.drawable.jo2)
                .into(binding.layoutHowTo.imHowto4);

        binding.layoutHowTo.tvHowToHeadOne.setVisibility(View.GONE);
        binding.layoutHowTo.LLHowToOne.setVisibility(View.GONE);
        binding.layoutHowTo.tvHowToHeadTwo.setText(getResources().getString(R.string.how_to_download));

        binding.layoutHowTo.tvHowTo1.setText(getResources().getString(R.string.open_josh));
        binding.layoutHowTo.tvHowTo3.setText(getResources().getString(R.string.cop_link_from_josh));
        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISSHOWHOWTOJOSH)) {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISSHOWHOWTOJOSH, false);
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.GONE);
        }


        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                Utils.showProgressDialog(activity);
                getJoshData();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });

        binding.LLOpenApp.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.eterno.shortvideos");
        });
    }

    private void getJoshData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            if (host.contains("myjosh")) {
                Utils.showProgressDialog(activity);
                new callGetJoshData().execute(binding.etText.getText().toString());
            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");
            if (CopyIntent.equals("")) {

                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("myjosh")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("myjosh")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("myjosh")) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class callGetJoshData extends AsyncTask<String, Void, Document> {
        Document JoshDoc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                JoshDoc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return JoshDoc;
        }

        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {
                String url = result.select("script[id=\"__NEXT_DATA__\"]").last().html();

                if (!url.equals("")) {
                    JSONObject jsonObject = new JSONObject(url);
                    VideoUrl = String.valueOf(jsonObject.getJSONObject("props")
                            .getJSONObject("pageProps").getJSONObject("detail")
                            .getJSONObject("data").
                                    getString("download_url"));
                    //Mp3 Available
                    String mp3Url = String.valueOf(jsonObject.getJSONObject("props")
                            .getJSONObject("pageProps").getJSONObject("detail")
                            .getJSONObject("data").getJSONObject("audio_track_meta").
                                    getString("url"));

                    VideoUrl = VideoUrl.replace("r4_wmj_480.mp4","r4_480.mp4");
                    startDownload(VideoUrl, ROOTDIRECTORYJOSH, activity, "josh_" + System.currentTimeMillis() + ".mp4");
                    VideoUrl = "";
                    binding.etText.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }





    //RewardedAds : End


    public void LoadNativeAd() {
        {
            binding.myNative.setVisibility(View.GONE);

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
                            binding.myNative.setStyles(styles);
                            binding.myNative.setNativeAd(unifiedNativeAd);
                            binding.myNative.setBackground(getResources().getDrawable(R.drawable.rectangle_white));
                            binding.myNative.setVisibility(View.VISIBLE);


                        }
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());



        }
    }
}