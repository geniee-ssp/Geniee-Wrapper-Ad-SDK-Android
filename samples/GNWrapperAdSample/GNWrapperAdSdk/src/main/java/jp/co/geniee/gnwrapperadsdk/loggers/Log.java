package jp.co.geniee.gnwrapperadsdk.loggers;

import java.io.PrintWriter;
import java.io.StringWriter;

import jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel;

import static jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel.DEBUG;
import static jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel.ERROR;
import static jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel.INFO;
import static jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel.NONE;
import static jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel.VERBOSE;
import static jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel.WARN;

/**
 * SDKのロガー
 */
public final class Log {
    /** ログレベル（デフォルト：INFO） */
    private static GNLogLevel GNLogLevel = INFO;

    /**
     * ログレベルを取得する
     * @return argLevel ログレベル
     */
    public static GNLogLevel getGNLogLevel() {
        return GNLogLevel;
    }

    /**
     * ログレベルを設定する
     * @param argLevel ログレベル
     */
    public static void setGNLogLevel(GNLogLevel argLevel) {
        GNLogLevel = argLevel;
    }

    /**
     * ログを出力するか判定する
     * @param argGNLogLevel 引数のログレベル
     */
    private static boolean canLogging(GNLogLevel argGNLogLevel) {
        // 設定されていない、またはNONEのとき出力しない
        if (GNLogLevel == null || GNLogLevel == NONE) {
            return true;
        }
        // 設定されているログレベル優先度が引数以下のとき出力する
        return (GNLogLevel.comparePriorityLevel(argGNLogLevel) > 0);
    }

    /**
     * Verboseログを出力する
     * @param tag タグ
     * @param message メッセージ
     */
    public static void v(String tag, String message) {
        if (canLogging(VERBOSE)) {
            return;
        }
        android.util.Log.v(tag, message);
    }

    /**
     * Debugログを出力する
     * @param tag タグ
     * @param message メッセージ
     **/
    public static void d(String tag, String message) {
        if (canLogging(DEBUG)) {
            return;
        }
        android.util.Log.d(tag, message);
    }

    /**
     * Infoログを出力する
     * @param tag タグ
     * @param message メッセージ
     */
    public static void i(String tag, String message) {
        if (canLogging(INFO)) {
            return;
        }
        android.util.Log.i(tag, message);
    }

    /**
     * Warnログを出力する
     * @param tag タグ
     * @param message メッセージ
     */
    public static void w(String tag, String message) {
        if (canLogging(WARN)) {
            return;
        }
        android.util.Log.w(tag, message);
    }

    /**
     * Errorログを出力する
     * @param tag タグ
     * @param message メッセージ
     */
    public static void e(String tag, String message) {
        if (canLogging(ERROR)) {
            return;
        }
        android.util.Log.e(tag, message);
    }

    /**
     * ErrorログでExceptionを出力する
     * @param tag タグ
     * @param exception Exception
     */
    public static void e(String tag, Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        Log.e(tag, sw.toString());
    }
}
