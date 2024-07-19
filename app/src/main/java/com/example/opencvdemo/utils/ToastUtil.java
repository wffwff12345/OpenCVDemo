package com.example.opencvdemo.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * <p>Toast工具类</p>
 *
 */
public class ToastUtil {
    private static WeakReference<Toast> toastRef = null;

    /**
     * 显示Toast，如果当前有Toast在显示,则复用之前的Toast，之前Toast的剩余显示时间会被忽略，重新计时 {@link Toast#LENGTH_SHORT}
     */
    public static void showOne(Context context, String text) {
        Toast toast;
        if (toastRef != null && (toast = toastRef.get()) != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
        toastRef = new WeakReference<>(toast);
        toast.show();
    }

    /**
     * 显示Toast，如果当前有Toast在显示,则复用之前的Toast，之前Toast的剩余显示时间会被忽略，重新计时 {@link Toast#LENGTH_SHORT}
     */
    public static void showOne(Context context, int resId) {
        showOne(context, context.getResources().getString(resId));
    }

    /**
     * 普通的显示Toast，时间为 {@link Toast#LENGTH_SHORT}
     *
     * @param context
     * @param text
     */
    public static void show(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 普通的显示Toast，时间为 {@link Toast#LENGTH_SHORT}
     *
     * @param context
     * @param resId
     */
    public static void show(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
