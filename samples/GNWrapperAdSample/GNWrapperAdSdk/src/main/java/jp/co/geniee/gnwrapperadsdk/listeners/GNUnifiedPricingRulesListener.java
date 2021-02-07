package jp.co.geniee.gnwrapperadsdk.listeners;

import jp.co.geniee.gnwrapperadsdk.params.GNUPRParams;

public interface GNUnifiedPricingRulesListener {
    void onComplete(GNUPRParams gnUPRParams);

    void onError();
}
