package jp.co.geniee.gnwrapperadsample;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.prebid.mobile.PrebidMobile;
import org.prebid.mobile.addendum.AdViewUtils;
import org.prebid.mobile.addendum.PbFindSizeError;

import jp.co.geniee.gnwrapperadsdk.GNWrapperAdSDK;
import jp.co.geniee.gnwrapperadsdk.banner.GNWrapperAdBanner;
import jp.co.geniee.gnwrapperadsdk.enums.GNErrorCode;
import jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel;
import jp.co.geniee.gnwrapperadsdk.listeners.GNWrapperAdBannerListener;
import jp.co.geniee.gnwrapperadsdk.params.GNCustomTargetingParams;

public class MainActivity extends AppCompatActivity implements GNWrapperAdBannerListener, AppEventListener {
    private static final String TAG = "MainActivity";
    private static final String GN_WRAPPER_CONFIG = "GNWrapperConfig_Android";
    private PublisherAdView mPublisherAdView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GNWrapperAdBanner gnWrapperAdBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GNWrapperAdSDK.setLogLevel(GNLogLevel.DEBUG);
        GNWrapperAdSDK.setTestMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        final RelativeLayout constraintLayout = findViewById(R.id.constraintLayout);

        PrebidMobile.setApplicationContext(getApplicationContext());

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "FirebaseRemoteConfig is Success");
                            String gnGNRemoteConfigValue = mFirebaseRemoteConfig.getString(GN_WRAPPER_CONFIG);
                            Log.d(TAG, "gnGNRemoteConfigValue: " + gnGNRemoteConfigValue);
                            gnWrapperAdBanner.initAndLoad(gnGNRemoteConfigValue);
                        } else {
                            Log.d(TAG, "FirebaseRemoteConfig is failed Exception: " + task.getException());
                        }
                    }
                });

        mPublisherAdView = new PublisherAdView(MainActivity.this);
        mPublisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                mPublisherAdView.setVisibility(View.VISIBLE);
                AdViewUtils.findPrebidCreativeSize(mPublisherAdView, new AdViewUtils.PbFindSizeListener() {
                    @Override
                    public void success(int width, int height) {
                        Log.d(TAG, "Resize Ad: width: " + width + " height: " + height);
                        mPublisherAdView.setAdSizes(new AdSize(width, height));
                    }
                    @Override
                    public void failure(@NonNull PbFindSizeError error) {
                        Log.d("MyTag", "error: " + error);
                    }
                });
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.d(TAG, "onAdFailedToLoad");
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        mPublisherAdView.setAppEventListener(this);
        mPublisherAdView.setId(View.generateViewId());
        constraintLayout.addView(mPublisherAdView);

        gnWrapperAdBanner = new GNWrapperAdBanner(this, this);
        gnWrapperAdBanner.setId(View.generateViewId());

        gnWrapperAdBanner.setLayoutParams(new LinearLayout.LayoutParams(convertDp2Px(320), convertDp2Px(50)));

        constraintLayout.addView(gnWrapperAdBanner);


        //setCenterConstraintSet(constraintLayout);
    }


    private void setCenterConstraintSet(ConstraintLayout constraintLayout) {

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.constrainWidth(mPublisherAdView.getId(),
                convertDp2Px(300));

        constraintSet.constrainHeight(mPublisherAdView.getId(),
                convertDp2Px(250));

        constraintSet.constrainWidth(gnWrapperAdBanner.getId(),
                convertDp2Px(300));

        constraintSet.constrainHeight(gnWrapperAdBanner.getId(),
                convertDp2Px(250));

        constraintSet.connect(
                mPublisherAdView.getId(),
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                0);

        constraintSet.connect(
                mPublisherAdView.getId(),
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT,
                0);

        constraintSet.connect(
                mPublisherAdView.getId(),
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT,
                0);

        constraintSet.connect(
                mPublisherAdView.getId(),
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                0);

        constraintSet.connect(
                gnWrapperAdBanner.getId(),
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                0);

        constraintSet.connect(
                gnWrapperAdBanner.getId(),
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT,
                0);

        constraintSet.connect(
                gnWrapperAdBanner.getId(),
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT,
                0);

        constraintSet.connect(
                gnWrapperAdBanner.getId(),
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                0);

        constraintSet.applyTo(constraintLayout);
        setContentView(constraintLayout);
    }

    public int convertDp2Px(float dp){
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    @Override
    public void onComplete(String adUnitId, GNCustomTargetingParams gnCustomTargetingParams) {
        if (mPublisherAdView.getAdUnitId() == null) {
            mPublisherAdView.setAdUnitId(adUnitId);
            mPublisherAdView.setAdSizes(AdSize.BANNER);
        }

        PublisherAdRequest.Builder adRequestBuilder = new PublisherAdRequest.Builder();
        for (String key: gnCustomTargetingParams.keySet()) {
            Log.d(TAG, "key: " + key + " value: " + gnCustomTargetingParams.get(key));
            adRequestBuilder.addCustomTargeting(key, gnCustomTargetingParams.get(key));
        }

        PublisherAdRequest adRequest = adRequestBuilder.build();

        mPublisherAdView.loadAd(adRequest);
    }

    @Override
    public void onError(GNErrorCode gnErrorCode) {
        Log.w(TAG, "GNWrapperError: " + gnErrorCode.getMessage());
    }

    @Override
    public void onAppEvent(String s, String s1) {
        if(s.equals("pubmaticdm")) {
            Log.d(TAG, " GNWrapperAd.isShowInSDKView: " +  gnWrapperAdBanner.isShowInSDKView());
            if (gnWrapperAdBanner.isShowInSDKView()) {
                mPublisherAdView.setVisibility(View.INVISIBLE);
                gnWrapperAdBanner.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPublisherAdView != null) {
            mPublisherAdView.destroy();
        }
        if (gnWrapperAdBanner != null) {
            gnWrapperAdBanner.destroy();
        }
    }
}
