package jp.co.geniee.gnwrapperadsdk.banner;

import android.content.Context;
import android.os.Handler;

import java.util.Map;

import jp.co.geniee.gnwrapperadsdk.listeners.GNHBAdapterListener;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNHBParams;
import jp.co.geniee.gnwrapperadsdk.utils.StringUtils;

public abstract class GNHBBannerAdapter {
    private static final String TAG = "GNHBAdapter";

    public GNHBParams gnHBParams;
    public Map<String, String> hbRequestParams;
    public GNHBAdapterListener gnHBAdapterListener;

    public abstract void init(Context context,
                                        Handler handler,
                                        GNWrapperAdBanner gnWrapperAdBanner,
                                        GNHBParams gnHBParams,
                                        GNHBAdapterListener gnHBAdapterListener);

    public abstract void load();

    public abstract boolean isShowInSDKView();

    public abstract boolean isNullOrEmptyAllRequestParams();

    public boolean isNullOrEmptyRequestParams(String keyName) {
        if (StringUtils.isNullOrEmpty(hbRequestParams.get(keyName))) {
            Log.w(TAG,  "Missing Param: " + keyName);
            return true;
        }
        return false;
    }

    public abstract void show();

    public abstract void hide();

    public abstract void destroy();
}
