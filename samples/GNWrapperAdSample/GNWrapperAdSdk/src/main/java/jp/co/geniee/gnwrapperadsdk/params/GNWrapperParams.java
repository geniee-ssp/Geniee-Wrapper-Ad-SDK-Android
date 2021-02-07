package jp.co.geniee.gnwrapperadsdk.params;

import java.util.HashMap;
import java.util.Map;

import jp.co.geniee.gnwrapperadsdk.loggers.Log;

public class GNWrapperParams {
    private static final String TAG = "GNWrapperParams";
    private static final int MIN_REFRESH_INTERVAL = 30;

    private String adUnit;
    private boolean useHB;
    private boolean useUPR;
    private boolean refresh;
    private double timeout;
    private double refreshInterval;
    private String hbCustomTargetingKey;
    private String hbCustomTargetingValue;

    private final Map<String, GNHBParams> gnGNHBParamsCollection;
    private GNUPRParams gnUPRParams;

    public GNWrapperParams() {
        gnGNHBParamsCollection = new HashMap<String, GNHBParams>();
        gnUPRParams = new GNUPRParams();
    }

    public String getAdUnit() {
        return adUnit;
    }

    public void setAdUnit(String adUnit) {
        this.adUnit = adUnit;
    }

    public boolean getUseHB() {
        return useHB;
    }

    public void setUseHB(boolean useHB) {
        this.useHB = useHB;
    }

    public double getTimeout() {
        return timeout;
    }

    public void setTimeout(double timeout) {
        this.timeout = timeout;
    }

    public String getHbCustomTargetingKey() {
        return hbCustomTargetingKey;
    }

    public void setHbCustomTargetingKey(String hbCustomTargetingKey) {
        this.hbCustomTargetingKey = hbCustomTargetingKey;
    }

    public String getHbCustomTargetingValue() {
        return hbCustomTargetingValue;
    }

    public void setHbCustomTargetingValue(String hbCustomTargetingValue) {
        this.hbCustomTargetingValue = hbCustomTargetingValue;
    }

    public Map<String, GNHBParams> getGNHBParamsCollection() {
        return gnGNHBParamsCollection;
    }

    public GNHBParams getHbParams(String hbName) {
        return gnGNHBParamsCollection.get(hbName);
    }

    public void addHbRequestParam(String hbName, GNHBParams gnHBParams) {
        gnGNHBParamsCollection.put(hbName, gnHBParams);
    }

    public void removeHbRequestParam(String hbName) {
        gnGNHBParamsCollection.remove(hbName);
    }

    public boolean getUseUPR() {
        return useUPR;
    }

    public void setUseUPR(boolean useUPR) {
        this.useUPR = useUPR;
    }

    public GNUPRParams getGnUPRParams() {
        return gnUPRParams;
    }

    public void setGnUPRParams(GNUPRParams gnUPRParams) {
        this.gnUPRParams = gnUPRParams;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setIsRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public double getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(double refreshInterval) {
        if (MIN_REFRESH_INTERVAL > refreshInterval) {
            Log.i(TAG, "The refresh interval is " + refreshInterval + " seconds, but the minimum refresh interval is " + MIN_REFRESH_INTERVAL +  "seconds.");
            this.refreshInterval = MIN_REFRESH_INTERVAL;
         } else  {
            this.refreshInterval = refreshInterval;
        }
    }
}
