package jp.co.geniee.gnwrapperadsdk.banner;

import android.content.Context;
import android.os.Handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import jp.co.geniee.gnwrapperadsdk.listeners.GNHBAdapterListener;
import jp.co.geniee.gnwrapperadsdk.listeners.GNHeaderBiddingListener;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNHBParams;
import jp.co.geniee.gnwrapperadsdk.params.GNWrapperParams;

public class GNHeaderBidding implements GNHBAdapterListener {
    private static final String TAG = "GNHeaderBidding";
    private final Context context;
    private final GNHeaderBiddingListener gnHeaderBiddingListener;
    private final GNWrapperParams gnWrapperParams;
    private final int timeout;
    private int hbCount;
    private int finishCount;
    private final GNWrapperAdBanner gnWrapperAdBanner;
    private GNHBParams winHBParams;
    private final Handler handler;
    private boolean isExecute;

    GNHeaderBidding(Context context, GNWrapperAdBanner gnWrapperAdBanner, GNHeaderBiddingListener gnHeaderBiddingListener, GNWrapperParams gnWrapperParams, double timeout) {
        this.context = context;
        this.gnWrapperAdBanner = gnWrapperAdBanner;
        this.gnHeaderBiddingListener = gnHeaderBiddingListener;
        this.gnWrapperParams = gnWrapperParams;
        this.timeout = (int) (timeout * 1000);
        handler = new Handler();
    }

    void loadAdapter() {
        isExecute = false;
        finishCount = 0;
        Map<String, GNHBParams>gnHBParamsCollection = gnWrapperParams.getGNHBParamsCollection();
        if (gnHBParamsCollection == null) {
            Log.w(TAG, "GNHBParamsCollection is Nothing");
            GNHeaderBidding.this.onError();
            return;
        }
        hbCount = gnWrapperParams.getGNHBParamsCollection().size();
        if (hbCount == 0) {
            Log.w(TAG, "GNHBParamsCollection's Count 0");
            GNHeaderBidding.this.onError();
            return;
        }

        handler.postDelayed(runnableForceExecute, timeout);
        for (String hbName: gnWrapperParams.getGNHBParamsCollection().keySet()) {
            if (gnWrapperParams.getHbParams(hbName).getAdapterClassObject() == null) {
                Runnable runnableLoadAdapter = new RunnableLoadAdapter(gnWrapperParams.getHbParams(hbName));
                new Thread(runnableLoadAdapter).start();
            } else {
                GNHeaderBidding.this.hide();
            }
        }
    }

    boolean isShowInSDKView()  {
        try {
            Method method = winHBParams.getAdapterClass().getMethod("isShowInSDKView");
            Object isShowInSDKView = method.invoke(winHBParams.getAdapterClassObject());
            if (isShowInSDKView == null) {
                Log.e(TAG, "isShowInSDKView is null");
                return false;
            }
            return  (boolean) isShowInSDKView;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, e);
            return false;
        }
    }

    void show() {
        try {
            Method method = winHBParams.getAdapterClass().getMethod("show");
            method.invoke(winHBParams.getAdapterClassObject());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, e);
        }
    }

    void hide() {
        try {
            Method method = winHBParams.getAdapterClass().getMethod("hide");
            method.invoke(winHBParams.getAdapterClassObject());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, e);
        }
    }

    void destroy() {
        try {
            Method method = winHBParams.getAdapterClass().getMethod("destroy");
            method.invoke(winHBParams.getAdapterClassObject());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, e);
        }
    }

    private synchronized void compareHB(GNHBParams gnHBParams) {
        if (isExecute) {
            Log.i(TAG, gnHBParams.getHbName() + " returned a response but timed out");
            return;
        }
        if (winHBParams == null || winHBParams.getBidPrice() < gnHBParams.getBidPrice()) {
            winHBParams = gnHBParams;
        }
    }

    private synchronized boolean checkFinishCount() {
        return finishCount >= hbCount;
    }

    private synchronized void   callBackFinish() {
        if (!isExecute) {
            gnHeaderBiddingListener.onFinish(winHBParams);
            isExecute = true;
        }
    }

    private final Runnable runnableForceExecute = new Runnable() {
        @Override
        public void run() {
            callBackFinish();
        }
    };

    @Override
    public void onComplete(GNHBParams gnHBParams) {
        Log.d(TAG, "HB Name: " + gnHBParams.getHbName());
        Log.d(TAG, gnHBParams.getHbName() + " bidder Won by: " + gnHBParams.getBidderName());
        Log.d(TAG, gnHBParams.getHbName() + " Bid price: " + gnHBParams.getBidPrice());
        compareHB(gnHBParams);
        finishCount++;
        if (checkFinishCount()) {
            callBackFinish();
        }
    }

    @Override
    public void onError() {
        finishCount++;
        Log.d(TAG, "finishCount: " + finishCount);
        if (checkFinishCount()) {
            callBackFinish();
        }
    }

    public class RunnableLoadAdapter implements Runnable {
        private final GNHBParams gnHBParams;

        public RunnableLoadAdapter(GNHBParams gnHBParams) {
            this.gnHBParams = gnHBParams;
        }

        public void run() {
            try {
                Class<?> adapterClass = Class.forName("jp.co.geniee.gnhbadapter.GNHB" + gnHBParams.getHbName() + "BannerAdapter");
                gnHBParams.setAdapterClass(adapterClass);
                Object adapterClassObject = adapterClass.newInstance();
                gnHBParams.setAdapterClassObject(adapterClassObject);
                Method init = adapterClass.getMethod("init",  Context.class, Handler.class, GNWrapperAdBanner.class, GNHBParams.class, GNHBAdapterListener.class);
                init.invoke(adapterClassObject, context, handler, gnWrapperAdBanner, gnHBParams, GNHeaderBidding.this);
                Method load = adapterClass.getMethod("load");
                load.invoke(adapterClassObject);
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "jp.co.geniee.gnhbadapter.GNHB" + gnHBParams.getHbName() + "Adapter Class is Nothing");
                GNHeaderBidding.this.onError();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                Log.e(TAG, e);
                GNHeaderBidding.this.onError();
            }
        }
    }


}
