package jp.co.geniee.gnwrapperadsdk.params;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.HashMap;
import java.util.Map;

public class GNHBParams {
    private Map<String,String> hbRequestParams;
    private String hbName;
    private String bidderName;
    private double bidPrice;
    private Map<String, String> hbResponseParams;
    private Object adapterClassObject;
    private Class<?> adapterClass;

    public GNHBParams() {
        this.hbRequestParams = new HashMap<>();
        this.hbResponseParams = new HashMap<>();
    }

    public Map<String, String> getHbRequestParams() {
        return hbRequestParams;
    }

    public void setHbRequestParams(Map<String, String> hbRequestParams) {
        this.hbRequestParams = hbRequestParams;
    }

    public String getHbName() {
        return hbName;
    }

    public void setHbName(String hbName) {
        this.hbName = hbName;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Map<String, String> getHbResponseParams() {
        return hbResponseParams;
    }

    public void setHbResponseParams(Map<String, String> hbResponseParams) {
        this.hbResponseParams = hbResponseParams;
    }

    public Object getAdapterClassObject() {
        return adapterClassObject;
    }

    public void setAdapterClassObject(Object adapterClassObject) {
        this.adapterClassObject = adapterClassObject;
    }

    public Class<?> getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(Class<?> adapterClass) {
        this.adapterClass = adapterClass;
    }
}
