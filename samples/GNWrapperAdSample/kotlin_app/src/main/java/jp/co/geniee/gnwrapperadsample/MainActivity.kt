package jp.co.geniee.gnwrapperadsample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AppEventListener
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
    private var adManagerAdView: AdManagerAdView? = null
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var gnWrapperAdBanner: GNWrapperAdBanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) { }
        /*
        //  TODO: If you want to debug your code, please refer to the URL below and enter your Test device ID.
        //  EN: https://developers.google.com/ad-manager/mobile-ads-sdk/android/test-ads#enable_test_devices
        //  JP: https://developers.google.com/ad-manager/mobile-ads-sdk/android/test-ads?hl=ja#enable_test_devices
        */
        val testDeviceIds = listOf("ADD_YOUR_TEST_DEVICE_ID")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        // Change the GNLogLevel if you want logcat to display the GNWrapperAdSDK debug code
        GNWrapperAdSDK.setLogLevel(GNLogLevel.INFO)

        /*
        // Make this setting to run the GNWrapperAdSDK in test mode.
        // TODO: Be sure to remove it when releasing
         */
        GNWrapperAdSDK.setTestMode(true)

        PrebidMobile.setApplicationContext(applicationContext)

        val adView = findViewById<FrameLayout>(R.id.ad_view_layout)

        adManagerAdView = AdManagerAdView(this)
        adManagerAdView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")

                adManagerAdView!!.visibility = View.VISIBLE
                AdViewUtils.findPrebidCreativeSize(adManagerAdView, object : PbFindSizeListener {
                    override fun success(width: Int, height: Int) {
                        Log.d(TAG, "Resize Ad: width: $width height: $height")
                        adManagerAdView!!.setAdSizes(AdSize(width, height))
                    }

                    override fun failure(error: PbFindSizeError) {
                        Log.d("MyTag", "error: $error")
                    }
                })
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "onAdFailedToLoad")
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed")
            }
        }

        adManagerAdView!!.appEventListener = this
        adView.addView(adManagerAdView)

        gnWrapperAdBanner = GNWrapperAdBanner(this)
        gnWrapperAdBanner!!.setGnWrapperAdBannerListener(this)
        adView.addView(gnWrapperAdBanner)


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
                        var gnGNRemoteConfigValue = firebaseRemoteConfig!!.getString(GN_WRAPPER_CONFIG)
                        Log.d(TAG, "gnGNRemoteConfigValue: $gnGNRemoteConfigValue")

                        gnGNRemoteConfigValue = "{\"unit_id\":\"/9116787/1385323\",\"timeout\":0,\"is_refresh\":false,\"refresh_interval\":0,\"use_upr\":false,\"upr_settings\":{},\"use_hb\":false,\"hb_list\":[]}"


                        // Ad Load
                        gnWrapperAdBanner!!.load(gnGNRemoteConfigValue)
                    } else {
                        Log.w(TAG, "FirebaseRemoteConfig is failed Exception: " + task.exception)
                    }
                }
    }

    override fun onComplete(adUnitId: String?, gnCustomTargetingParams: GNCustomTargetingParams) {
        if (adManagerAdView!!.adUnitId == null) {
            adManagerAdView!!.adUnitId = adUnitId
            adManagerAdView!!.setAdSizes(AdSize.BANNER)
        }
        val adRequestBuilder = AdManagerAdRequest.Builder()
        for (key in gnCustomTargetingParams.keys) {
            Log.d(TAG, "key: " + key + " value: " + gnCustomTargetingParams[key])
            adRequestBuilder.addCustomTargeting(key, gnCustomTargetingParams[key])
        }
        val adRequest = adRequestBuilder.build()
        adManagerAdView!!.loadAd(adRequest)
    }

    override fun onError(gnErrorCode: GNErrorCode) {
        Log.w(TAG, "GNWrapperError: " + gnErrorCode.message)
    }

    override fun onAppEvent(s: String, s1: String) {
        if (s == "pubmaticdm") {
            Log.d(TAG, " GNWrapperAd.isShowInSDKView: " + gnWrapperAdBanner!!.isShowInSDKView)
            if (gnWrapperAdBanner!!.isShowInSDKView) {
                adManagerAdView!!.visibility = View.INVISIBLE
                gnWrapperAdBanner!!.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adManagerAdView != null) {
            adManagerAdView!!.destroy()
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
