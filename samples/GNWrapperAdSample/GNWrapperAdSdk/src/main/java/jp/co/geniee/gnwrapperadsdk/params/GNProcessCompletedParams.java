package jp.co.geniee.gnwrapperadsdk.params;

public class GNProcessCompletedParams {
    boolean isUPRProcessCompleted;

    boolean isHBProcessCompleted;

    public void uprProcessComplete() {
        isUPRProcessCompleted = true;
    }

    public void resetUPRProcessComplete() {
        isUPRProcessCompleted = false;
    }

    public void hbProcessComplete() {
        isHBProcessCompleted = true;
    }

    public void resetHBProcessComplete() {
        isHBProcessCompleted = false;
    }

    public void allProcessComplete() {
        uprProcessComplete();
        hbProcessComplete();
    }

    public void resetAllProcessComplete() {
        resetUPRProcessComplete();
        resetHBProcessComplete();
    }

    public boolean isAllProcessCompleted() {
        return isUPRProcessCompleted && isHBProcessCompleted;
    }
}
