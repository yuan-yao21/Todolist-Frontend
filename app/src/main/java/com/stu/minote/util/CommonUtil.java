package com.stu.minote.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.stu.minote.app.MyApplication;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
    /**
     * 测量View的宽高
     *
     * @param view View
     */
    public static void measureWidthAndHeight(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 检测测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }


    /**
     * 添加布尔值到sp
     *
     * @param key
     * @param value
     */
    public static void putBoolean2SP(String key, boolean value) {
        MyApplication.getmSharedPreference().edit().putBoolean(key, value)
                .commit();
    }

    public static void putString2SP(String key, String value) {
        MyApplication.getmSharedPreference().edit().putString(key, value)
                .commit();
    }

    public static void putFloat2SP(String key, float value) {
        MyApplication.getmSharedPreference().edit().putFloat(key, value)
                .commit();
    }

    public static void putInteger2SP(String key, int value) {
        MyApplication.getmSharedPreference().edit().putInt(key, value).commit();
    }

    public static String getStringFromSP(String key) {
        return MyApplication.getmSharedPreference().getString(key, "");
    }

    public static String getStringFromSP(String key, String defaultValue) {
        return MyApplication.getmSharedPreference().getString(key, defaultValue);
    }

    public static float getFloatFromSP(String key) {
        return MyApplication.getmSharedPreference().getFloat(key, 3.0f);
    }

    public static boolean getBooleanFromSP(String key) {
        return MyApplication.getmSharedPreference().getBoolean(key, false);
    }

    public static boolean getBooleanFromSP(String key, boolean defaultValue) {
        return MyApplication.getmSharedPreference().getBoolean(key, defaultValue);
    }

    public static void putLong2SP(String key, long value) {
        MyApplication.getmSharedPreference().edit().putLong(key, value)
                .commit();
    }

    public static long getLongFromSP(String key) {
        return MyApplication.getmSharedPreference().getLong(key, 0);
    }

    public static void putInt2SP(String key, int value) {
        MyApplication.getmSharedPreference().edit().putInt(key, value).commit();
    }

    public static int getIntFromSP(String key) {
        return MyApplication.getmSharedPreference().getInt(key, 0);
    }

    public static int getIntFromSP(String key, int defaultValue) {
        return MyApplication.getmSharedPreference().getInt(key, defaultValue);
    }

    private static InputMethodManager imm;

    // 显示输入法
    public static void show(Context context, View focusView) {
        imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏输入法
     *
     * @param context
     */
    public static void hide(Context context) {
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null && view.getWindowToken() != null) {
            imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 调用该方法；键盘若显示则隐藏; 隐藏则显示
     *
     * @param context
     */
    public static void toggle(Context context) {
        imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    //得到外部储存sdcard的状态
    private static String sdcard = Environment.getExternalStorageState();
    //外部储存sdcard存在的情况
    private static String state = Environment.MEDIA_MOUNTED;

    static File file = Environment.getExternalStorageDirectory();
    static StatFs statFs = new StatFs(file.getPath());

    /**
     * 计算Sdcard的剩余大小
     *
     * @return MB
     */
    public static long getAvailableSize() {
        if (sdcard.equals(state)) {
            //获得Sdcard上每个block的size
            long blockSize = statFs.getBlockSize();
            //获取可供程序使用的Block数量
            long blockavailable = statFs.getAvailableBlocks();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal = blockSize * blockavailable / 1000 / 1000;
            return blockavailableTotal;
        } else {
            return -1;
        }
    }

    public static String timeStamp2Date(Date date, String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /*

     * 将时间戳转换为时间

     */

    public static String stampToDate(String s, String format) {
        String res;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        long lt = new Long(s);

        Date date = new Date(lt);

        res = simpleDateFormat.format(date);

        return res;

    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Commonutil", "时间转换错误");
        }

        long ts = date.getTime();

        res = String.valueOf(ts);

        return res;

    }

    /**
     * 获取当前apk的版本号
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取当前apk的版本名
     *
     * @param context 上下文
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionName
            versionName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
//    /**
//     * 获取当前apk的版本名
//     *
//     * @param context 上下文
//     * @return
//     */
//    public static String getVersionString(Context context) {
//        return context.getResources().getString(R.string.app_name) + "  " + getVersionName(context) + "  Android";
//    }

    /**
     * 将dp值转换为px值
     *
     * @param dipValue dp值
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        } catch (Exception e) {
            return (int) dipValue;
        }
    }
}
