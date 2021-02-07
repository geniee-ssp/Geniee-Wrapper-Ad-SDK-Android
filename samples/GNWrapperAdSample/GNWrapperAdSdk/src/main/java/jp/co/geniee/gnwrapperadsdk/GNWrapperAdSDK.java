package jp.co.geniee.gnwrapperadsdk;

import jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;

public class GNWrapperAdSDK {
    private static final String TAG = "GNWrapperAdSDK";
    private static boolean testMode;

    public static void setLogLevel(GNLogLevel GNLogLevel) {
        Log.setGNLogLevel(GNLogLevel);
    }

    public static GNLogLevel getLogLevel() {
        return Log.getGNLogLevel();
    }

    public static void setTestMode(boolean testMode) {
        GNWrapperAdSDK.testMode = testMode;
    }


    public static boolean getTestMode() {
        return GNWrapperAdSDK.testMode;
    }
}
