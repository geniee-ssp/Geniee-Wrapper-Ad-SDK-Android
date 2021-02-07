package jp.co.geniee.gnwrapperadsdk.banner;

import jp.co.geniee.gnwrapperadsdk.utils.StringUtils;
import jp.co.geniee.gnwrapperadsdk.listeners.GNUnifiedPricingRulesListener;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNUPRParams;

public class GNUnifiedPricingRules {
    private static final String TAG = "GNUnifiedPricingRules";
    private final GNUPRParams gnUPRParams;
    private final GNUnifiedPricingRulesListener gnUnifiedPricingRulesListener;

    GNUnifiedPricingRules(GNUnifiedPricingRulesListener gnUnifiedPricingRulesListener, GNUPRParams gnUPRParams) {
        this.gnUnifiedPricingRulesListener = gnUnifiedPricingRulesListener;
        this.gnUPRParams = gnUPRParams;
    }

    void execute() {
        Runnable runnableCheckUPRParams = new Runnable() {
            @Override
            public void run() {
                if (checkUPRParams()) {
                    Log.i(TAG, "checkUPRParams Success!!");
                    GNUnifiedPricingRules.this.gnUnifiedPricingRulesListener.onComplete(GNUnifiedPricingRules.this.gnUPRParams);
                }
            }
        };
        new Thread(runnableCheckUPRParams).start();
    }

    private boolean checkUPRParams() {
        if (StringUtils.isNullOrEmpty(gnUPRParams.getUPRKey())) {
            Log.w(TAG, "UPRKey is Nothing");
            gnUnifiedPricingRulesListener.onError();
            return false;
        }
        if (StringUtils.isNullOrEmpty(gnUPRParams.getUPRValue())) {
            Log.w(TAG, "UPRValue is Nothing");
            gnUnifiedPricingRulesListener.onError();
            return false;
        }
        return true;
    }

}
