package jp.co.geniee.gnwrapperadsdk.listeners;

import jp.co.geniee.gnwrapperadsdk.params.GNHBParams;

public interface GNHBAdapterListener {
    void onComplete(GNHBParams gnHBParams);

    void onError();
}
