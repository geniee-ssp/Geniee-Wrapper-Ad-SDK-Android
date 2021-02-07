package jp.co.geniee.gnwrapperadsdk.banner;


import android.os.Handler;

import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNWrapperParams;

public class GNAutoRefresh {
    private static final String TAG = "GNAutoRefresh";
    private static final int CHANGE_SECOND = 1000;
    private static final int CHECK_INTERVAL = 1;

    private final Handler handler;
    private final GNWrapperAdBanner gnWrapperAdBanner;
    private final long refreshInterVal;
    private final long checkInterval;
    private long elapsedTime;
    private long startSysTime;
    private boolean refreshProcessing = false;

    GNAutoRefresh(Handler handler, GNWrapperAdBanner gnWrapperAdBanner, GNWrapperParams gnWrapperParams) {
        this.handler = handler;
        this.gnWrapperAdBanner = gnWrapperAdBanner;
        refreshInterVal = (long)gnWrapperParams.getRefreshInterval() * CHANGE_SECOND;
        Log.d(TAG, "refreshInterVal: " + refreshInterVal);
        checkInterval = CHECK_INTERVAL * CHANGE_SECOND;
    }

    void start() {
        if (!refreshProcessing) {
            elapsedTime = 0;
            refreshProcessing = true;
            startSysTime = System.currentTimeMillis();
            handler.removeCallbacks(runnableRefresh);
            handler.post(runnableRefresh);
        }
    }

    void restart() {
        if (!refreshProcessing) {
            refreshProcessing = true;
            startSysTime = System.currentTimeMillis();
            Log.d(TAG, "restart auto refresh. elapsedTime: " + elapsedTime);
            handler.post(runnableRefresh);
        }
    }

    void stop() {
        if (refreshProcessing) {
            refreshProcessing = false;
            elapsedTime = elapsedTime + (System.currentTimeMillis() - startSysTime);
            Log.d(TAG, "stop auto refresh. elapsedTime: " + elapsedTime);
            handler.removeCallbacks(runnableRefresh);
        }
    }

    void destroy() {
        refreshProcessing = false;
        elapsedTime = 0;
        handler.removeCallbacks(runnableRefresh);
        Log.d(TAG, "destroy  auto refresh");
    }

    private final Runnable runnableRefresh = new Runnable() {
        @Override
        public void run() {
            long nowSysTime = System.currentTimeMillis();
            Log.d(TAG, "elapsedTime: " + String.valueOf((nowSysTime - (startSysTime - elapsedTime))));
            if (refreshInterVal <= nowSysTime - (startSysTime - elapsedTime)) {
                refreshProcessing = false;
                gnWrapperAdBanner.executeAllProcess();
            } else {
                handler.postDelayed(runnableRefresh, checkInterval);
            }
        }
    };
}
