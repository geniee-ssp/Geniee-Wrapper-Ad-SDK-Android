package jp.co.geniee.gnwrapperadsdk.banner;

import android.content.Context;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import java.util.Map;

import jp.co.geniee.gnwrapperadsdk.enums.GNErrorCode;
import jp.co.geniee.gnwrapperadsdk.listeners.GNHeaderBiddingListener;
import jp.co.geniee.gnwrapperadsdk.listeners.GNJsonParserListener;
import jp.co.geniee.gnwrapperadsdk.listeners.GNUnifiedPricingRulesListener;
import jp.co.geniee.gnwrapperadsdk.listeners.GNWrapperAdBannerListener;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNCustomTargetingParams;
import jp.co.geniee.gnwrapperadsdk.params.GNHBParams;
import jp.co.geniee.gnwrapperadsdk.params.GNProcessCompletedParams;
import jp.co.geniee.gnwrapperadsdk.params.GNUPRParams;
import jp.co.geniee.gnwrapperadsdk.params.GNWrapperParams;
import jp.co.geniee.gnwrapperadsdk.parsers.GNJsonParser;
import jp.co.geniee.gnwrapperadsdk.viewable.GNViewable;

public class GNWrapperAdBanner extends RelativeLayout implements GNJsonParserListener, GNHeaderBiddingListener, GNUnifiedPricingRulesListener {
    private static final String TAG = "GNWrapperAd";
    private static final int VIEWABLE_RATIO = 50;
    private final Context context;
    private final Handler handler = new Handler();
    private ViewTreeObserver viewTreeObserver;
    private GNWrapperParams gnWrapperParams;
    private GNWrapperAdBannerListener gnWrapperAdBannerListener;
    private GNCustomTargetingParams gnWrapperCustomTargeting;
    private GNProcessCompletedParams gnProcessCompletedParams;
    private GNAutoRefresh gnAutoRefresh;
    private GNHeaderBidding gnHeaderBidding;
    private boolean isViewableLayout;
    private boolean isWindowFocus;

    public GNWrapperAdBanner(@NonNull Context context,@NonNull GNWrapperAdBannerListener gnWrapperAdBannerListener) {
        super(context);
        this.context = context;
        this.gnWrapperAdBannerListener = gnWrapperAdBannerListener;
    }

    public void initAndLoad(@NonNull final String gnGBRemoteConfigValue) {
        Log.i(TAG, "GNWrapperBannerAd: initAndLoad");
        if (viewTreeObserver == null) {
            viewTreeObserver = GNWrapperAdBanner.this.getViewTreeObserver();
        }
        gnWrapperCustomTargeting = new GNCustomTargetingParams();
        gnProcessCompletedParams = new GNProcessCompletedParams();
        gnWrapperParams = new GNWrapperParams();

        if (gnGBRemoteConfigValue.isEmpty()) {
            Log.w(TAG, "gnGBRemoteConfigValue is Nothing");
            return;
        }
        GNJsonParser gnJsonParser = new GNJsonParser(this);
        gnJsonParser.getJsonParamWithString(gnGBRemoteConfigValue);
    }

    public boolean isShowInSDKView()  {
        return gnHeaderBidding.isShowInSDKView();
    }

    public void show() {
        Log.i(TAG, "GNWrapperBannerAd: show");
        gnHeaderBidding.show();
    }
    
    public void destroy() {
        Log.i(TAG, "GNWrapperBannerAd: Destroy");
        gnHeaderBidding.destroy();
        gnAutoRefresh.destroy();
        viewTreeObserver.removeOnWindowFocusChangeListener(onWindowFocusChangeListener);
        viewTreeObserver.removeOnDrawListener(onDrawListener);
    }

    private synchronized void addGNWrapperCustomTargeting(String key, String value) {
        Log.d(TAG, "GNWrapperBannerAd: addGNWrapperCustomTargeting");
        gnWrapperCustomTargeting.put(key, value);
        Log.d(TAG, "key:" + key + " value:" + gnWrapperCustomTargeting.get(key));
    }

    private synchronized void executeGNWrapperAdCallback() {
        Log.d(TAG, "GNWrapperBannerAd: executeGNWrapperAdCallback");
        if (gnProcessCompletedParams.isAllProcessCompleted()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                        if (gnWrapperCustomTargeting == null || gnWrapperCustomTargeting.size() == 0) {
                            Log.i(TAG, "Process Complete. gnWrapperCustomTargeting size: 0");
                        }

                        Log.d(TAG, "GNWrapperBannerAd: Complete");
                        gnWrapperAdBannerListener.onComplete(gnWrapperParams.getAdUnit(), gnWrapperCustomTargeting);
                        if (gnWrapperParams.isRefresh()) {
                            if(gnAutoRefresh == null) {
                                gnAutoRefresh = new GNAutoRefresh(handler, GNWrapperAdBanner.this, gnWrapperParams);
                                isWindowFocus = hasWindowFocus();
                                viewTreeObserver.addOnWindowFocusChangeListener(onWindowFocusChangeListener);
                                viewTreeObserver.addOnDrawListener(onDrawListener);
                            }
                            gnAutoRefresh.start();
                        }

                    }
            });
        }
    }

    private void executeUPRProcessComplete() {
        gnProcessCompletedParams.uprProcessComplete();
        Log.d(TAG, "UPR process complete");
        executeGNWrapperAdCallback();
    }

    private void executeHBProcessComplete() {
        gnProcessCompletedParams.hbProcessComplete();
        Log.d(TAG, "HB process complete");
        executeGNWrapperAdCallback();
    }

    protected void executeAllProcess() {
        Log.d(TAG, "UseUPR: " + gnWrapperParams.getUseUPR());
        if (gnWrapperParams.getUseUPR()) {
            GNUnifiedPricingRules gnUnifiedPricingRules = new GNUnifiedPricingRules(GNWrapperAdBanner.this, gnWrapperParams.getGnUPRParams());
            gnUnifiedPricingRules.execute();
        } else {
            executeUPRProcessComplete();
        }
        Log.d(TAG, "UseHB: " + gnWrapperParams.getUseHB());
        if (gnWrapperParams.getUseHB()) {
            if (gnHeaderBidding == null) {
                gnHeaderBidding = new GNHeaderBidding(context, this, this, this.gnWrapperParams,this.gnWrapperParams.getTimeout());
            }
            gnHeaderBidding.loadAdapter();
        } else {
            executeHBProcessComplete();
        }
    }

    private synchronized void viewableCheck() {
        if (isWindowFocus && isViewableLayout && getVisibility() == VISIBLE) {
            gnAutoRefresh.restart();
        } else {
            gnAutoRefresh.stop();
        }
    }

    private final ViewTreeObserver.OnWindowFocusChangeListener onWindowFocusChangeListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
        @Override
        public void onWindowFocusChanged(boolean isWindowFocus) {
            if (gnAutoRefresh != null) {
                Log.d(TAG, "isWindowFocus: " + isWindowFocus);
                GNWrapperAdBanner.this.isWindowFocus = isWindowFocus;
                viewableCheck();
            }
        }
    };

    private final ViewTreeObserver.OnDrawListener onDrawListener = new ViewTreeObserver.OnDrawListener() {
        @Override
        public void onDraw() {
            int[] viewPoints =  new int[2];
            getLocationInWindow(viewPoints);
            boolean isViewableLayout = GNViewable.isViewable(context, viewPoints, getWidth(), getHeight(), VIEWABLE_RATIO);
            if (GNWrapperAdBanner.this.isViewableLayout != isViewableLayout) {
                GNWrapperAdBanner.this.isViewableLayout = isViewableLayout;
                Log.d(TAG, "isViewableLayout: " + isViewableLayout);
            }
            viewableCheck();
        }
    };

    @Override
    public void onJsonParseComplete(GNWrapperParams gnWrapperParams) {
        this.gnWrapperParams = gnWrapperParams;
        executeAllProcess();
    }

    @Override
    public void onJsonParseError(GNErrorCode GNErrorCode) {
        executeUPRProcessComplete();
        executeHBProcessComplete();
    }

    @Override
    public void onFinish(GNHBParams winHBParams) {
        if (winHBParams == null) {
            Log.i(TAG, "There was no successful bid for header bidding");
        } else {
            Log.i(TAG, "Header bidding won by : " + winHBParams.getHbName());
            Map<String, String> hbRequestParams = winHBParams.getHbResponseParams();
            for (String key: hbRequestParams.keySet()) {
                addGNWrapperCustomTargeting(key, hbRequestParams.get(key));
            }
        }
        executeHBProcessComplete();
    }

    @Override
    public void onComplete(GNUPRParams gnUPRParams) {
        addGNWrapperCustomTargeting(gnUPRParams.getUPRKey(), gnUPRParams.getUPRValue());
        executeUPRProcessComplete();
    }

    @Override
    public void onError() {
        executeUPRProcessComplete();
    }
}
