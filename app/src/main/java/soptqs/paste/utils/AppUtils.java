package soptqs.paste.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import soptqs.paste.R;
import soptqs.paste.database.AppItem;
import soptqs.paste.services.RsenAccessibilityService;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by S0ptq on 2018/2/7.
 */

public class AppUtils extends Activity {

    final static String TAG = "AppUtils";

    public static String getTimeAgo(long t1, Context context) {
        long hour = 0;
        long day = 0;
        long year = 0;
        long mouth = 0;
        long t2 = System.currentTimeMillis();
        long minute = (t2 - t1) / 60000;
        if (minute > 60){
            hour = minute / 60;
        }
        if (hour >= 24) {
            day =(hour / 24);
        }
        if (day > 30){
            mouth = day / 30;
        }
        if (mouth > 12){
            year = mouth / 12;
        }
        if (year > 0){
            return year + context.getResources().getString(R.string.year) + context.getResources().getString(R.string.ago);
        }else if (mouth > 0 ){
            return mouth + context.getResources().getString(R.string.month) + context.getResources().getString(R.string.ago);
        }else  if (day > 0){
            return day + context.getResources().getString(R.string.day) + context.getResources().getString(R.string.ago);
        }else  if (hour > 0 ){
            return hour + context.getResources().getString(R.string.hour) + context.getResources().getString(R.string.ago);
        }else if (minute > 0){
            return minute + context.getResources().getString(R.string.minute) + context.getResources().getString(R.string.ago);
        } else return context.getResources().getString(R.string.now);
    }

    public static Boolean isTimeOut(long t1, Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long t2 = System.currentTimeMillis();
        long day = 0;
        day = (t2 - t1) / 86400000;
        if ("forever".equals(prefs.getString(PreferenceUtils.PREF_AUTOCLEAR, "forever"))) {
            return false;
        } else {
            int timeout = Integer.parseInt(prefs.getString(PreferenceUtils.PREF_AUTOCLEAR, "forever"));
            return Integer.parseInt(String.valueOf(day)) > timeout;
        }
    }

    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + RsenAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    /**
     * Get activity instance from desired context.
     */
    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }

    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        String imei = null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        try {
            assert telephonyManager != null;
            imei = telephonyManager.getDeviceId();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return imei;
    }

    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    public static boolean isPro(Context context) {
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int code = prefs.getInt(PreferenceUtils.TEMP_DATA, 3);
        return code % 2 == 0;
    }

    public static List<AppItem> scanLocalInstallAppList(PackageManager packageManager) {
        List<AppItem> myAppInfos = new ArrayList<>();
        try {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                //过滤掉系统app
//            if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
//                continue;
//            }
                AppItem myAppInfo = new AppItem();
                myAppInfo.setPakageName(packageInfo.packageName);
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }
                myAppInfo.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                myAppInfo.setImage(packageInfo.applicationInfo.loadIcon(packageManager));
                myAppInfos.add(myAppInfo);
            }
        } catch (Exception e) {
            Log.e(TAG, "===============获取应用包信息失败");
        }
        return myAppInfos;
    }

    /**
     * 收起通知栏
     *
     * @param context
     */
    public static void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            collapse = statusBarManager.getClass().getMethod("collapsePanels");
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void openApplicationMarket(String appPackageName, String marketPackageName,
                                             Context context) {
        try {
            String url = "market://details?id=" + appPackageName;
            Intent localIntent = new Intent(Intent.ACTION_VIEW);

            if (marketPackageName != null) {
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setPackage(marketPackageName);
            }
            openLink(context, localIntent, url, true);
        } catch (Exception e) {
            e.printStackTrace();
            openApplicationMarketForLinkBySystem(appPackageName, context);
        }
    }

    public static void openApplicationMarketForLinkBySystem(String packageName, Context context) {
        String url = "http://www.coolapk.com/apk/" + packageName;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        openLink(context, intent, url, false);
    }

    public static void openLink(Context context, Intent intent, String link, boolean isThrowException) {
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW);
        }

        try {
            intent.setData(Uri.parse(link));
            context.startActivity(intent);
        } catch (Exception e) {
            if (isThrowException) {
                throw e;
            } else {
                e.printStackTrace();
            }
        }
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 复制内容
     *
     * @param context
     * @param str
     */
    public static void copy(Context context, String str) {
        if (str != null) {
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(null, str));
        }
    }

    public static Boolean isNightMode() {
        return false;
    }

    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return (int) Math.ceil((double) (25.0f * context.getResources().getDisplayMetrics().density));
    }

    public static long getFileSize(File file) throws Exception {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        return size;
    }

    public static String byte2size(long size) {
        if (size < 1024) {
            return size + " B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return size + " KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return size + " MB";
        } else {
            size = size / 1024;
        }
        return size + " GB";
    }

}
