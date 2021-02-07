package jp.co.geniee.gnwrapperadsdk.enums;

import android.os.Build;

public enum GNLogLevel {
    /** NONE */
    NONE(0),
    /** VERBOSE */
    VERBOSE(1),
    /** DEBUG */
    DEBUG(2),
    /** INFO */
    INFO(3),
    /** WARN */
    WARN(4),
    /** ERROR */
    ERROR(5);

    /**
     * 優先レベル
     */
    private int priorityLevel;

    /**
     * コンストラクタ
     * @param priorityLevel 優先レベル
     */
    GNLogLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    /**
     * 優先レベルを比較する
     * @param GNLogLevel ログレベル
     * @return 比較結果
     */
    public int comparePriorityLevel(GNLogLevel GNLogLevel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Integer.compare(this.priorityLevel, GNLogLevel.priorityLevel);
        } else {
            return (this.priorityLevel - GNLogLevel.priorityLevel);
        }
    }
}
