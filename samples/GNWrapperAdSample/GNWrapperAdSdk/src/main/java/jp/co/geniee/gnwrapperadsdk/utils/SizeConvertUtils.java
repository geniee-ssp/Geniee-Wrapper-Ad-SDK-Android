package jp.co.geniee.gnwrapperadsdk.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class SizeConvertUtils {
    public static int convertPx2Dp(int px, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (px / metrics.density);
    }

    public static int convertDp2Px(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }
}
