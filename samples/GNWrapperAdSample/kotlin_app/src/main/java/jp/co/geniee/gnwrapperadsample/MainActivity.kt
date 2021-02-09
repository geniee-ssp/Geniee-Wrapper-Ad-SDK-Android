package jp.co.geniee.gnwrapperadsample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.doubleclick.AppEventListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import jp.co.geniee.gnwrapperadsdk.GNWrapperAdSDK
import jp.co.geniee.gnwrapperadsdk.banner.GNWrapperAdBanner
import jp.co.geniee.gnwrapperadsdk.enums.GNErrorCode
import jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel
import jp.co.geniee.gnwrapperadsdk.listeners.GNWrapperAdBannerListener
import jp.co.geniee.gnwrapperadsdk.params.GNCustomTargetingParams
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.AdViewUtils.PbFindSizeListener
import org.prebid.mobile.addendum.PbFindSizeError

class MainActivity : AppCompatActivity(), GNWrapperAdBannerListener, AppEventListener {
    private var publisherAdView: PublisherAdView? = null
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var gnWrapperAdBanner: GNWrapperAdBanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GNWrapperAdSDK.setLogLevel(GNLogLevel.DEBUG)
        GNWrapperAdSDK.setTestMode(true)

        val adView = findViewById<FrameLayout>(R.id.ad_view_layout)
        PrebidMobile.setApplicationContext(applicationContext)

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
        firebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        firebaseRemoteConfig!!.fetchAndActivate()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "FirebaseRemoteConfig is Success")
                        val gnGNRemoteConfigValue = firebaseRemoteConfig!!.getString(GN_WRAPPER_CONFIG)
                        Log.d(TAG, "gnGNRemoteConfigValue: $gnGNRemoteConfigValue")
                        gnWrapperAdBanner!!.load(gnGNRemoteConfigValue)
                    } else {
                        Log.d(TAG, "FirebaseRemoteConfig is failed Exception: " + task.exception)
                    }
                }

        publisherAdView = PublisherAdView(this)
        publisherAdView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")

                publisherAdView!!.visibility = View.VISIBLE
                AdViewUtils.findPrebidCreativeSize(publisherAdView, object : PbFindSizeListener {
                    override fun success(width: Int, height: Int) {
                        Log.d(TAG, "Resize Ad: width: $width height: $height")
                        publisherAdView!!.setAdSizes(AdSize(width, height))
                    }

                    override fun failure(error: PbFindSizeError) {
                        Log.d("MyTag", "error: $error")
                    }
                })
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "onAdFailedToLoad")
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed")
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
        publisherAdView!!.appEventListener = this
        publisherAdView!!.id = View.generateViewId()
        adView.addView(publisherAdView)

        gnWrapperAdBanner = GNWrapperAdBanner(this)
        gnWrapperAdBanner!!.setGnWrapperAdBannerListener(this)
        gnWrapperAdBanner!!.id = View.generateViewId()
        adView.addView(gnWrapperAdBanner)
    }

    override fun onComplete(adUnitId: String?, gnCustomTargetingParams: GNCustomTargetingParams) {
        if (publisherAdView!!.adUnitId == null) {
            publisherAdView!!.adUnitId = adUnitId
            publisherAdView!!.setAdSizes(AdSize.BANNER)
        }
        val adRequestBuilder = PublisherAdRequest.Builder()
        for (key in gnCustomTargetingParams.keys) {
            Log.d(TAG, "key: " + key + " value: " + gnCustomTargetingParams.get(key))
            adRequestBuilder.addCustomTargeting(key, gnCustomTargetingParams.get(key))
        }
        val adRequest = adRequestBuilder.build()
        publisherAdView!!.loadAd(adRequest)
    }

    override fun onError(gnErrorCode: GNErrorCode) {
        Log.w(TAG, "GNWrapperError: " + gnErrorCode.message)
    }

    override fun onAppEvent(s: String, s1: String) {
        if (s == "pubmaticdm") {
            Log.d(TAG, " GNWrapperAd.isShowInSDKView: " + gnWrapperAdBanner!!.isShowInSDKView)
            if (gnWrapperAdBanner!!.isShowInSDKView) {
                publisherAdView!!.visibility = View.INVISIBLE
                gnWrapperAdBanner!!.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (publisherAdView != null) {
            publisherAdView!!.destroy()
        }
        if (gnWrapperAdBanner != null) {
            gnWrapperAdBanner!!.destroy()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val GN_WRAPPER_CONFIG = "GNWrapperConfig_Android"
    }
}
