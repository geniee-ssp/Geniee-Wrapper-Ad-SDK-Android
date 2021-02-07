package jp.co.geniee.gnwrapperadsdk.listeners;

import jp.co.geniee.gnwrapperadsdk.enums.GNErrorCode;
import jp.co.geniee.gnwrapperadsdk.params.GNCustomTargetingParams;

public interface GNWrapperAdBannerListener {
    void onComplete(String adUnitId, GNCustomTargetingParams gnCustomTargetingParams);

    void onError(GNErrorCode gnErrorCode);
}
