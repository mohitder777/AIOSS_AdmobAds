package com.vid.allvideodownloader.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.vid.allvideodownloader.R;
import com.vid.allvideodownloader.databinding.ActivityGalleryBinding;
import com.vid.allvideodownloader.fragment.AllinOneGalleryFragment;
import com.vid.allvideodownloader.fragment.FBDownloadedFragment;
import com.vid.allvideodownloader.fragment.InstaDownloadedFragment;
import com.vid.allvideodownloader.fragment.LikeeDownloadedFragment;
import com.vid.allvideodownloader.fragment.RoposoDownloadedFragment;
import com.vid.allvideodownloader.fragment.SharechatDownloadedFragment;
import com.vid.allvideodownloader.fragment.SnackVideoDownloadedFragment;
import com.vid.allvideodownloader.fragment.TikTokDownloadedFragment;
import com.vid.allvideodownloader.fragment.TwitterDownloadedFragment;
import com.vid.allvideodownloader.fragment.WhatsAppDowndlededFragment;
import com.vid.allvideodownloader.util.AppLangSessionManager;
import com.vid.allvideodownloader.util.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.vid.allvideodownloader.activity.MainActivity.idBanner;
import static com.vid.allvideodownloader.activity.MainActivity.idInter;
import static com.vid.allvideodownloader.util.Utils.createFileFolder;

public class GalleryActivity  extends AppCompatActivity {
    GalleryActivity activity;
    ActivityGalleryBinding binding;
    InterstitialAd mInterstitialAd;
    AppLangSessionManager appLangSessionManager;
    FirebaseDatabase firebaseDatabase;
    boolean gaInter;
    boolean gaNative;
    boolean gaBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference adsBoolean = firebaseDatabase.getReference("galleryAds");

        adsBoolean.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Tagga",""+snapshot.child("gaInter").getValue());
                Log.i("Tagga",""+snapshot.child("gaBanner").getValue());

                gaInter = (boolean) snapshot.child("gaInter").getValue();
                gaBanner = (boolean) snapshot.child("gaBanner").getValue();

                if (gaInter == true){
                    InterstitialAdsINIT();
                }

                if (gaBanner == true){
                    LoadBannerAd();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Tag",error.getMessage());
            }
        });

        activity = this;

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());
        initViews();
    }

    private void LoadBannerAd() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(idBanner);
        binding.adView.loadAd(new AdRequest.Builder().build());

    }

    private void InterstitialAdsINIT() {
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(idInter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }



    public void initViews() {
        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);
        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        for (int i = 0; i < binding.tabs.getTabCount(); i++) {
            TextView tv=(TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab,null);
            binding.tabs.getTabAt(i).setCustomView(tv);
        }

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        createFileFolder();
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new AllinOneGalleryFragment(Utils.ROOTDIRECTORYJOSHSHOW), "Josh");
        adapter.addFragment(new AllinOneGalleryFragment(Utils.ROOTDIRECTORYCHINGARISHOW), "Chingari");
        adapter.addFragment(new AllinOneGalleryFragment(Utils.ROOTDIRECTORYMITRONSHOW), "Mitron");
        adapter.addFragment(new SnackVideoDownloadedFragment(), "Snack Video");
        adapter.addFragment(new SharechatDownloadedFragment(), "Sharechat");
        adapter.addFragment(new RoposoDownloadedFragment(), "Roposo");
        adapter.addFragment(new InstaDownloadedFragment(), "Instagram");
        adapter.addFragment(new WhatsAppDowndlededFragment(), "Whatsapp");
        adapter.addFragment(new TikTokDownloadedFragment(), "TikTok");
        adapter.addFragment(new FBDownloadedFragment(), "Facebook");
        adapter.addFragment(new TwitterDownloadedFragment(), "Twitter");
        adapter.addFragment(new LikeeDownloadedFragment(), "Likee");
        adapter.addFragment(new AllinOneGalleryFragment(Utils.ROOTDIRECTORYMXSHOW), "MXTakaTak");
        adapter.addFragment(new AllinOneGalleryFragment(Utils.ROOTDIRECTORYMOJSHOW), "Moj");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

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
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd!=null){
            mInterstitialAd.show();
        }
    }
}
