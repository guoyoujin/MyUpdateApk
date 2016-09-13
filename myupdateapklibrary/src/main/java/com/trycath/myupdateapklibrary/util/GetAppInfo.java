package com.trycath.myupdateapklibrary.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-08-26 12:12
 * @version: V1.0 <描述当前版本功能>
 */

public class GetAppInfo {
    private static final String TAG = "GetAppInfo";
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appName = pi.applicationInfo.loadLabel(context.getPackageManager()).toString();
            if (appName == null || appName.length() <= 0) {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
    public static int getVersionCode(Context context) {
        int versionCode;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            if(versionCode>0){
                return versionCode;
            }else{
                return 1;  
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
            return 1;
        }
    }

    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    public static Drawable getAppIcon(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }
    public static int getAppIconId(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
            return info.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return 0;
    }


    public static String getAPKPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return appInfo.packageName;
        }
        return null;
    }

   
    public static int compareVersionCode(int oldVersionCode, int newVersionCode) {
        int res = 0;
        Log.d(TAG,"oldVersionCode===="+oldVersionCode+"====newVersionCode==="+newVersionCode);
        if(oldVersionCode>newVersionCode){
            res = 1;
        }else if(oldVersionCode == newVersionCode){
            res = 0;
        }else{
            res = -1;
        }
        return res;
    }
}