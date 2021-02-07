package jp.co.geniee.gnhbadapter;

import android.content.Context;
import android.os.Handler;

import org.prebid.mobile.BannerAdUnit;
import org.prebid.mobile.BannerBaseAdUnit;
import org.prebid.mobile.Host;
import org.prebid.mobile.OnCompleteListener2;
import org.prebid.mobile.PrebidMobile;
import org.prebid.mobile.ResultCode;
import org.prebid.mobile.Signals;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import jp.co.geniee.gnwrapperadsdk.banner.GNHBBannerAdapter;
import jp.co.geniee.gnwrapperadsdk.banner.GNWrapperAdBanner;
import jp.co.geniee.gnwrapperadsdk.listeners.GNHBAdapterListener;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNHBParams;

public class GNHBPrebidBannerAdapter extends GNHBBannerAdapter {
    private static final String TAG = "GNHBPrebidAdapter";

    private static final String PREBID_SERVER_HOST_TYPE = "prebid_server_host_type";
    private static final String PREBID_SERVER_HOST_URL = "prebid_server_host_url";
    private static final String PREBID_SERVER_ACCOUNT_ID = "prebid_server_account_id";
    private static final String CONFIG_ID = "config_id";
    private static final String AD_SIZE = "ad_size";

    private BannerAdUnit adUnit;

    @Override
    public void init(Context context, Handler handler,
                     GNWrapperAdBanner gnWrapperAdBanner,
                     GNHBParams gnHBParams,
                     GNHBAdapterListener gnHBAdapterListener) {
        this.gnHBParams = gnHBParams;
        this.hbRequestParams = this.gnHBParams.getHbRequestParams();
        this.gnHBAdapterListener = gnHBAdapterListener;
        if (!isNullOrEmptyAllRequestParams()) {
            gnHBAdapterListener.onError();
            return;
        }

        switch (Objects.requireNonNull(hbRequestParams.get(PREBID_SERVER_HOST_TYPE))) {
            case "APPNEXUS":
                PrebidMobile.setPrebidServerHost(Host.APPNEXUS);
                break;
            case "RUBICON":
                PrebidMobile.setPrebidServerHost(Host.RUBICON);
                break;
            case "CUSTOM":
                Host.CUSTOM.setHostUrl(hbRequestParams.get(PREBID_SERVER_HOST_URL));
                PrebidMobile.setPrebidServerHost(Host.CUSTOM);
                break;
        }
        PrebidMobile.setPrebidServerAccountId(hbRequestParams.get(PREBID_SERVER_ACCOUNT_ID));
        int width = 0;
        int height = 0;

        String[] wAndH = Objects.requireNonNull(hbRequestParams.get(AD_SIZE)).split("x");;
        width = Integer.parseInt(wAndH[0]);
        height = Integer.parseInt(wAndH[1]);

        adUnit = new BannerAdUnit(Objects.requireNonNull(hbRequestParams.get(CONFIG_ID)), width, height);

        BannerBaseAdUnit.Parameters parameters = new BannerBaseAdUnit.Parameters();
        parameters.setApi(Collections.singletonList(Signals.Api.MRAID_2));
        adUnit.setParameters(parameters);
        //        adUnit.setUserKeyword("my_key", "my_value");
//        BannerBaseAdUnit.Parameters parameters = new BannerBaseAdUnit.Parameters();
//        parameters.setApi(Arrays.asList(new Signals.Api(6, 7)));
//// alternate representation using an enum parameters.setApi(Arrays.asList(Signals.Api.MRAID_3, Signals.Api.OMID_1));
//
//        adUnit.setParameters(parameters);
    }

    @Override
    public void load() {
        adUnit.fetchDemand(new OnCompleteListener2() {
            @Override
            public void onComplete(ResultCode resultCode, Map<String, String> unmodifiableMap) {
                Log.d(TAG, resultCode.name());
                if (resultCode == ResultCode.SUCCESS) {
                    Log.d(TAG, unmodifiableMap.toString());
                    gnHBParams.setBidPrice(Double.parseDouble(Objects.requireNonNull(unmodifiableMap.get("hb_pb"))));
                    gnHBParams.setBidderName(Objects.requireNonNull(unmodifiableMap.get("hb_bidder")));
                    gnHBParams.setHbResponseParams(unmodifiableMap);
                    gnHBAdapterListener.onComplete(gnHBParams);
                } else {
                    Log.w(TAG, "Prebid Error:" + resultCode.name());
                    gnHBAdapterListener.onError();
                }
            }
        });
    }

    @Override
    public boolean isNullOrEmptyAllRequestParams() {
        if (isNullOrEmptyRequestParams(PREBID_SERVER_HOST_TYPE)) {
            return false;
        } else if (Objects.equals(hbRequestParams.get(PREBID_SERVER_HOST_TYPE), "ANY")) {
            if (isNullOrEmptyRequestParams(PREBID_SERVER_HOST_URL)) {
                return false;
            }
        }
        return  (!isNullOrEmptyRequestParams(PREBID_SERVER_ACCOUNT_ID)) &&
                (!isNullOrEmptyRequestParams(CONFIG_ID)) &&
                (!isNullOrEmptyRequestParams(AD_SIZE));
    }

    @Override
    public void show() {
        // Nothing
    }

    @Override
    public void hide() {
        // Nothing
    }

    @Override
    public void destroy() {
        // Nothing
    }

    @Override
    public boolean isShowInSDKView() {
        return false;
    }
}
