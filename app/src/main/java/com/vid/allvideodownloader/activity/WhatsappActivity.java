package com.vid.allvideodownloader.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.vid.allvideodownloader.R;

import com.vid.allvideodownloader.databinding.ActivityWhatsappBinding;
import com.vid.allvideodownloader.fragment.WhatsappImageFragment;
import com.vid.allvideodownloader.fragment.WhatsappVideoFragment;
import com.vid.allvideodownloader.util.AppLangSessionManager;
import com.vid.allvideodownloader.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.databinding.DataBindingUtil.inflate;
import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.vid.allvideodownloader.activity.MainActivity.idBanner;
import static com.vid.allvideodownloader.activity.MainActivity.idInter;
import static com.vid.allvideodownloader.util.Utils.createFileFolder;

public class WhatsappActivity extends AppCompatActivity {
    private ActivityWhatsappBinding binding;
    private WhatsappActivity activity;
    InterstitialAd mInterstitialAd;

    private File[] allfiles;
    private ArrayList<Uri> fileArrayList=new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    AppLangSessionManager appLangSessionManager;
    boolean whatsBanner;
    boolean whatsInter;
    boolean whatsNative;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_whatsapp);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference adsBoolean = firebaseDatabase.getReference("AdsWhatsapp");

        adsBoolean.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("TagWhat",""+snapshot.child("whatsBanner").getValue());
                Log.i("TagWhat",""+snapshot.child("whatsInterstitial").getValue());

                whatsBanner = (boolean) snapshot.child("whatsBanner").getValue();
                whatsInter = (boolean) snapshot.child("whatsInterstitial").getValue();

                if (whatsBanner == true){
                    loadBannerAds();
                }

                if (whatsInter == true){
                    loadInterstitialAds();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("Tag",error.getMessage());
            }
        });



        activity = this;
        createFileFolder();
        initViews();

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());

    }

    private void loadInterstitialAds() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(idInter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    private void loadBannerAds() {
        AdView adView = new AdView(WhatsappActivity.this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(idBanner);
        binding.adView.loadAd(new AdRequest.Builder().build());
    }


    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }



    private void initViews() {

        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (SDK_INT <= Build.VERSION_CODES.Q) {
            setupViewPager(binding.viewpager);
            binding.tabs.setupWithViewPager(binding.viewpager);
            binding.imBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            for (int i = 0; i < binding.tabs.getTabCount(); i++) {
                TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                binding.tabs.getTabAt(i).setCustomView(tv);
            }

            binding.LLOpenWhatsapp.setOnClickListener(v -> {
                Utils.OpenApp(activity, "com.whatsapp");
            });
            fileArrayList = new ArrayList<>();


        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (getContentResolver().getPersistedUriPermissions().size() > 0) {
                    new LoadAllFiles().execute();

                    binding.parmisionTV.setVisibility(View.GONE);
                } else {
                    binding.parmisionTV.setVisibility(View.VISIBLE);
                }
            } else {

                for (int i = 0; i < binding.tabs.getTabCount(); i++) {
                    TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                    binding.tabs.getTabAt(i).setCustomView(tv);
                }
            }
            binding.parmisionTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            StorageManager sm = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);
                            Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                            String startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
                            Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
                            String scheme = uri.toString();
                            scheme = scheme.replace("/root/", "/document/");
                            scheme += "%3A" + startDir;
                            uri = Uri.parse(scheme);
                            intent.putExtra("android.provider.extra.INITIAL_URI", uri);
                            startActivityForResult(intent, 2001);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }
            });
        }

    }


    @Override
    public void onBackPressed() {

            if(mInterstitialAd != null){
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");

            }
            super.onBackPressed();
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new WhatsappImageFragment(), getResources().getString(R.string.images));
        adapter.addFragment(new WhatsappVideoFragment(), getResources().getString(R.string.videos));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 2001 && resultCode == RESULT_OK) {
                Uri dataUri = data.getData();
                if (dataUri.toString().contains(".Statuses")) {
                    getContentResolver().takePersistableUriPermission(dataUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        new LoadAllFiles().execute();
                    }
                }else{
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    class LoadAllFiles extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... furl) {
            DocumentFile documentFile = DocumentFile.fromTreeUri(activity, activity.getContentResolver().getPersistedUriPermissions().get(0).getUri());
            for (DocumentFile file : documentFile.listFiles()) {
                if (file.isDirectory()) {

                } else {
                    if (!file.getName().equals(".nomedia")) {

                        fileArrayList.add(file.getUri());
                    }
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... progress) {

        }
        @Override
        protected void onPostExecute(String fileUrl) {
            binding.parmisionTV.setVisibility(View.GONE);
            ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            adapter.addFragment(new WhatsappQImageFragment(fileArrayList), getResources().getString(R.string.images));
            adapter.addFragment(new WhatsappQVideoFragment(fileArrayList), getResources().getString(R.string.videos));
            binding.viewpager.setAdapter(adapter);
            binding.viewpager.setOffscreenPageLimit(1);
            binding.tabs.setupWithViewPager(binding.viewpager);
            binding.parmisionTV.setVisibility(View.GONE);
            for (int i = 0; i < binding.tabs.getTabCount(); i++) {
                TextView tv=(TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab,null);
                binding.tabs.getTabAt(i).setCustomView(tv);
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            binding.parmisionTV.setVisibility(View.GONE);
        }
    }



}
