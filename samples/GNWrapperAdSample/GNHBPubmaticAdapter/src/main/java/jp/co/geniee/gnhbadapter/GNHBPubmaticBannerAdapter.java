package jp.co.geniee.gnhbadapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.pubmatic.sdk.common.OpenWrapSDK;
import com.pubmatic.sdk.common.POBAdSize;
import com.pubmatic.sdk.common.POBError;
import com.pubmatic.sdk.common.models.POBApplicationInfo;
import com.pubmatic.sdk.openwrap.banner.POBBannerView;
import com.pubmatic.sdk.openwrap.core.POBBid;
import com.pubmatic.sdk.openwrap.core.POBBidEvent;
import com.pubmatic.sdk.openwrap.core.POBBidEventListener;
import com.pubmatic.sdk.openwrap.core.POBRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import jp.co.geniee.gnwrapperadsdk.banner.GNHBBannerAdapter;
import jp.co.geniee.gnwrapperadsdk.banner.GNWrapperAdBanner;
import jp.co.geniee.gnwrapperadsdk.GNWrapperAdSDK;
import jp.co.geniee.gnwrapperadsdk.listeners.GNHBAdapterListener;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNHBParams;
import jp.co.geniee.gnwrapperadsdk.utils.SizeConvertUtils;

public class GNHBPubmaticBannerAdapter extends GNHBBannerAdapter {
    private static final String TAG = "GNHBPrebidAdapter";

    private static final String APP_STORE_URL = "app_store_url";
    private static final String PUB_ID = "pub_id";
    private static final String PROFILE_ID = "profile_id";
    private static final String OPEN_WRAP_AD_UNIT_ID = "open_wrap_ad_unit_id";
    private static final String AD_SIZE = "ad_size";

    private POBBidEvent pobBidEvent;
    private POBBannerView pobBannerView;

    @Override
    public void init(Context context,
                     Handler handler,
                     GNWrapperAdBanner gnWrapperAdBanner,
                     GNHBParams gnHBParams,
                     GNHBAdapterListener gnHBAdapterListener) {
        this.gnHBParams = gnHBParams;
        this.hbRequestParams = this.gnHBParams.getHbRequestParams();

        for (String key: hbRequestParams.keySet()) {
            Log.d(TAG, "key: " + key + " value: " + hbRequestParams.get(key));
        }
        if (isNullOrEmptyAllRequestParams()) {
            gnHBAdapterListener.onError();
            return;
        }

        int width = 0;
        int height = 0;
/*            String[] wAndH = Objects.requireNonNull(hbRequestParams.get(AD_SIZE)).split("x");;

        width = Integer.parseInt(wAndH[0]);
        height = Integer.parseInt(wAndH[1]);*/

        //POBAdSize pobAdSize = new POBAdSize(width, height);
        POBAdSize pobAdSize = POBAdSize.BANNER_SIZE_320x50;
        if (!sizeCheck(pobAdSize)) {
            Log.w(TAG, "Banner Adsize does not match");
            return;
        }

        // A valid Play Store Url of an Android application is required.
        POBApplicationInfo appInfo = new POBApplicationInfo();
        try {
            appInfo.setStoreURL(new URL(hbRequestParams.get(APP_STORE_URL)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        OpenWrapSDK.setApplicationInfo(appInfo);

        pobBannerView = new POBBannerView(context);
        pobBannerView.setLayoutParams(new LinearLayout.LayoutParams(SizeConvertUtils.convertDp2Px(320, context), SizeConvertUtils.convertDp2Px(50, context)));

        handler.post(new Runnable() {
            @Override
            public void run() {
                gnWrapperAdBanner.addView(pobBannerView);
            }
        });

        pobBannerView.init(
                Objects.requireNonNull(hbRequestParams.get(PUB_ID)),
                Integer.parseInt(Objects.requireNonNull(hbRequestParams.get(PROFILE_ID))),
                Objects.requireNonNull(hbRequestParams.get(OPEN_WRAP_AD_UNIT_ID)),
                pobAdSize
        );

        pobBannerView.setBidEventListener(new POBBidEventListener() {
            @Override
            public void onBidReceived(@NonNull POBBidEvent pobBidEvent, @NonNull POBBid pobBid) {
                gnHBParams.setBidPrice(pobBid.getPrice());
                gnHBParams.setBidderName(pobBid.getPartnerName());
                GNHBPubmaticBannerAdapter.this.pobBidEvent = pobBidEvent;
                Map<String, String> params = pobBid.getTargetingInfo();
                gnHBParams.setHbResponseParams(params);
                Log.d(TAG, "Get Params: " + params);
                gnHBAdapterListener.onComplete(gnHBParams);
            }

            @Override
            public void onBidFailed(@NonNull POBBidEvent pobBidEvent, @NonNull POBError pobError) {
                Log.w(TAG, "Pubmatic Error:" + pobError.getErrorMessage());
                GNHBPubmaticBannerAdapter.this.pobBidEvent = pobBidEvent;
                gnHBAdapterListener.onError();
            }
        });
        pobBannerView.setListener(new POBBannerViewListener());

        if (GNWrapperAdSDK.getTestMode()) {
            POBRequest request = pobBannerView.getAdRequest();
            if(request != null){
                request.enableTestMode(true);
            }
        }
    }

    @Override
    public boolean isShowInSDKView() {
        return true;
    }

    @Override
    public void load() {
        pobBannerView.loadAd();
    }

    @Override
    public void show() {
        pobBannerView.setVisibility(View.VISIBLE);
        pobBidEvent.proceedToLoadAd();
    }

    @Override
    public void hide() {
        pobBannerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void destroy() {
        if (pobBannerView != null) {
            Log.d(TAG, "pobBannerView destroyed");
            pobBannerView.destroy();
        }
    }

    @Override
    public boolean isNullOrEmptyAllRequestParams() {
        return  (isNullOrEmptyRequestParams(APP_STORE_URL)) ||
                (isNullOrEmptyRequestParams(PUB_ID)) ||
                (isNullOrEmptyRequestParams(PROFILE_ID)) ||
                (isNullOrEmptyRequestParams(OPEN_WRAP_AD_UNIT_ID));
    }

    private boolean sizeCheck(POBAdSize pobAdSize) {
        return pobAdSize.equals(POBAdSize.BANNER_SIZE_320x50) ||
                pobAdSize.equals(POBAdSize.BANNER_SIZE_320x100) ||
                pobAdSize.equals(POBAdSize.BANNER_SIZE_300x250) ||
                pobAdSize.equals(POBAdSize.BANNER_SIZE_250x250) ||
                pobAdSize.equals(POBAdSize.BANNER_SIZE_468x60) ||
                pobAdSize.equals(POBAdSize.BANNER_SIZE_728x90) ||
                pobAdSize.equals(POBAdSize.BANNER_SIZE_120x600);
    }

    private static class POBBannerViewListener extends POBBannerView.POBBannerViewListener {
            private final String TAG = "POBBannerViewListener";

            // Callback method Notifies that an ad has been successfully loaded and rendered.
            @Override
            public void onAdReceived(POBBannerView view) {
                Log.d(TAG, "Ad Received");
            }

            // Callback method Notifies an error encountered while loading or rendering an ad.
            @Override
            public void onAdFailed(POBBannerView view, POBError error) {
                Log.w(TAG, error.toString());
            }

            // Callback method Notifies that the banner ad view will launch a dialog on top of the current view
            @Override
            public void onAdOpened(POBBannerView view) {
                Log.d(TAG, "Ad Opened");
            }

            // Callback method Notifies that the banner ad view has dismissed the modal on top of the current view
            @Override
            public void onAdClosed(POBBannerView view) {
                Log.d(TAG, "Ad Closed");
            }

            @Override
            public void onAppLeaving(POBBannerView view) {
                // Implement your custom logic
                Log.d(TAG, "Banner : App Leaving");
            }
        }
}
