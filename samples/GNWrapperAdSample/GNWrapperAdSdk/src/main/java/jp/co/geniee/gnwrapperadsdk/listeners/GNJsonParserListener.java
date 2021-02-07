package jp.co.geniee.gnwrapperadsdk.listeners;

import jp.co.geniee.gnwrapperadsdk.params.GNWrapperParams;
import jp.co.geniee.gnwrapperadsdk.enums.GNErrorCode;

public interface GNJsonParserListener {
    void onJsonParseComplete(GNWrapperParams GNWrapperParams);

    void onJsonParseError(GNErrorCode GNErrorCode);
}
