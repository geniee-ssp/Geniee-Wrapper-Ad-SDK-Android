package jp.co.geniee.gnwrapperadsdk.viewable;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.utils.SizeConvertUtils;

import static android.content.Context.WINDOW_SERVICE;

public class GNViewable {
    private static final String TAG = "GNViewable";

    public static boolean isViewable(Context context, int[] viewPoint, int width, int height, int inViewRatio) {
        int totalRightPoint;
        int totalBottomPoint;
        int layoutAreaWidth;
        int layoutAreaHeight;
        int viewWidth = SizeConvertUtils.convertPx2Dp(width, context);
        int viewHeight = SizeConvertUtils.convertPx2Dp(height, context);
        int viewLeft = SizeConvertUtils.convertPx2Dp(viewPoint[0], context);
        int viewTop = SizeConvertUtils.convertPx2Dp(viewPoint[1], context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getRealMetrics(displayMetrics);

        int displayWidth = SizeConvertUtils.convertPx2Dp(displayMetrics.widthPixels, context);
        int displayHeight = SizeConvertUtils.convertPx2Dp(displayMetrics.heightPixels, context);

        totalRightPoint = viewWidth + viewLeft;

        if (displayWidth < totalRightPoint) {
            if (viewLeft < 0) {
                layoutAreaWidth = displayWidth;
            } else {
                layoutAreaWidth = displayWidth - viewLeft;
            }
        } else {
            if (viewLeft < 0) {
                layoutAreaWidth = totalRightPoint;
            } else {
                layoutAreaWidth = viewWidth;
            }
        }
        totalBottomPoint = viewHeight + viewTop;
        if (displayHeight < totalBottomPoint) {
            if (viewTop < 0) {
                layoutAreaHeight = displayHeight;
            } else {
                layoutAreaHeight = displayHeight - viewTop;
            }
        } else {
            if (viewTop < 0) {
                layoutAreaHeight = totalBottomPoint;
            } else {
                layoutAreaHeight = viewHeight;
            }
        }
        boolean result = layoutAreaHeight > 0 && layoutAreaWidth > 0 && layoutAreaWidth * layoutAreaHeight >= viewWidth * viewHeight * inViewRatio / 100;

        // Debug log
        Log.d(TAG, "viewable: " + result +
                " displayWidth: " + displayWidth +
                " displayHeight: " + displayHeight +
                " viewWidth: " + viewWidth +
                " viewHeight: " + viewHeight +
                " viewLeft: " + viewLeft +
                " viewTop: " + viewTop +
                " layoutAreaWidth: " + layoutAreaWidth +
                " layoutAreaHeight: " + layoutAreaHeight +
                " layoutArea: " + layoutAreaWidth * layoutAreaHeight +
                " viewableLimitSize: " +  viewWidth * viewHeight * inViewRatio/100);

        return result;
    }
}
