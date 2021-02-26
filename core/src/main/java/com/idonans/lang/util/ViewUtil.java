package com.idonans.lang.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

import com.idonans.lang.Constants;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * View 相关辅助类
 */
public class ViewUtil {

    private ViewUtil() {
    }

    @Nullable
    public static Disposable onClick(View view, View.OnClickListener listener) {
        return onClick(view, Constants.VIEW_CLICK_THROTTLE_MS, listener);
    }

    @Nullable
    public static Disposable onClick(View view, long throttleMs, View.OnClickListener listener) {
        if (view == null) {
            Timber.e("view is null");
            return null;
        }

        Observable<Object> observable = RxView.clicks(view);
        if (throttleMs > 0) {
            observable = observable.throttleFirst(throttleMs, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
        }
        return observable.subscribe(o -> {
            if (listener != null) {
                listener.onClick(view);
            }
        });
    }

    /**
     * 类似 return view.findViewById(id1).findViewById(id2)...findViewById(idn);
     */
    @Nullable
    public static <T extends View> T findViewById(View view, @IdRes int... ids) {
        if (view == null) {
            Timber.e("view is null");
            return null;
        }

        if (ids == null || ids.length <= 0) {
            Timber.e("invalid ids");
            return null;
        }

        View targetView = view;
        for (int id : ids) {
            targetView = targetView.findViewById(id);

            if (targetView == null) {
                Timber.e("invalid id: %s", id);
                return null;
            }
        }
        return (T) targetView;
    }

    /**
     * 将文本绘制在指定区域内(仅支持单行的形式), 可指定对齐方式
     */
    public static void drawText(Canvas canvas, String text, Paint paint, RectF area, int gravity) {
        float textWith = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        Rect areaIn = new Rect();
        area.round(areaIn);
        Rect areaOut = new Rect();

        GravityCompat.apply(
                gravity,
                (int) Math.ceil(textWith),
                (int) Math.ceil(textHeight),
                areaIn,
                areaOut,
                ViewCompat.LAYOUT_DIRECTION_LTR);

        canvas.drawText(text, areaOut.left, areaOut.top - fontMetrics.ascent, paint);
    }

    public static boolean setPaddingIfChanged(View view, int left, int top, int right, int bottom) {
        if (view == null) {
            Timber.e("view is null");
            return false;
        }

        if (view.getPaddingLeft() != left
                || view.getPaddingTop() != top
                || view.getPaddingRight() != right
                || view.getPaddingBottom() != bottom) {
            view.setPadding(left, top, right, bottom);
            return true;
        }

        return false;
    }

    public static boolean setVisibilityIfChanged(View view, int visibility) {
        if (view == null) {
            Timber.e("view is null");
            return false;
        }

        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
            return true;
        }
        return false;
    }

    public static boolean requestParentDisallowInterceptTouchEvent(View view) {
        return requestParentDisallowInterceptTouchEvent(view, true);
    }

    public static boolean requestParentDisallowInterceptTouchEvent(View view, boolean disallowIntercept) {
        if (view == null) {
            Timber.e("view is null");
            return false;
        }
        ViewParent viewParent = view.getParent();
        if (viewParent == null) {
            Timber.e("view parent is null");
            return false;
        }
        viewParent.requestDisallowInterceptTouchEvent(disallowIntercept);
        return true;
    }

}
