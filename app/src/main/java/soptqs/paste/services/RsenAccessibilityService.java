package soptqs.paste.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityOptions;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.blankj.utilcode.util.ToastUtils;
import soptqs.paste.R;
import soptqs.paste.activities.UniversalCopyActivity;
import soptqs.paste.nodes.CopyNode;
import soptqs.paste.utils.PreferenceUtils;

import java.util.ArrayList;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;

/**
 * Created by S0ptq on 2018/2/7.
 */

public class RsenAccessibilityService extends AccessibilityService {

    private static volatile CharSequence foregroundPackageName;
    private static volatile CharSequence foregroundClassName;
    private SharedPreferences prefs;
    private IntentFilter intentFilter;
    private AccessibilityReceiver accessibilityReceiver;
    private Rect rect;
    //    String back;
//    String home;
//    String recent;
    private int retryTimes = 0;

    public static String foregroundPackageName() {
        return String.valueOf(foregroundPackageName);
    }

    public static String foregroundClassName() {
        return String.valueOf(foregroundClassName);
    }

    public static String getVitualNavigationKey(Context paramContext, String paramString1, String paramString2, String paramString3) {
        try {
            Resources packageManager = paramContext.getPackageManager().getResourcesForApplication(paramString2);
            String key = packageManager.getString(packageManager.getIdentifier(paramString1, "string", paramString2));
            return key;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return paramString3;
    }

    //TODO: release的时候取消掉下面的注释，debug的时候加上。AS 3.1.0 下面的代码debug时要报错

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && prefs.getBoolean(PreferenceUtils.PREF_SHOW_NAV_BUTTON, true)) {
            info.flags = AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
                    | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        } else {
            info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        }
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
                | TYPE_VIEW_LONG_CLICKED
                | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 10;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            AccessibilityButtonController mAccessibilityButtonController = getAccessibilityButtonController();
//            mAccessibilityButtonController.registerAccessibilityButtonCallback(
//                    new AccessibilityButtonController.AccessibilityButtonCallback() {
//                        @Override
//                        public void onClicked(AccessibilityButtonController controller) {
//                            super.onClicked(controller);
//                            if (FloatWindow.isShown) {
//                                return;
//                            }
//                            Intent intent = new Intent("soptq.intent.SHOW_POPUPWINDOW");
//                            RsenAccessibilityService.this.sendBroadcast(intent);
//                        }
//
//                        @Override
//                        public void onAvailabilityChanged(AccessibilityButtonController controller, boolean available) {
//                            super.onAvailabilityChanged(controller, available);
//                            // 辅助功能可用性改变的回调。返回了辅助功能底部按钮是否可用的布尔值，和按钮控制器
//                            // available = true 表示该按钮对本服务可用
//                            // available = false 是由于设备显示了按钮，或按钮被分配到另一个服务或其他原因。
//                        }
//                    });
//        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            foregroundPackageName = accessibilityEvent.getPackageName();
            foregroundClassName = accessibilityEvent.getClassName();
        }

//        if ((accessibilityEvent.getEventType() == TYPE_VIEW_CLICKED)
//                && ("com.android.systemui".equals(accessibilityEvent.getPackageName()))) {
//            if (TextUtils.isEmpty(accessibilityEvent.getContentDescription())) {
//                return;
//            }
//            if (!TextUtils.isEmpty(home)
//                    && accessibilityEvent.getContentDescription().equals(home)) {
//                if (FloatWindow.isShown) FloatWindow.hidePopupWindow();
//            } else if (!TextUtils.isEmpty(recent)
//                    && accessibilityEvent.getContentDescription().equals(recent)) {
//                if (FloatWindow.isShown) FloatWindow.hidePopupWindow();
//            }
//        }
    }

    @Override
    public void onCreate() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("soptq.intent.UNIVERSE_COPY");
        intentFilter.addAction("soptq.intent.ACCESSIBILITY_PASTE");
        accessibilityReceiver = new AccessibilityReceiver();
        registerReceiver(accessibilityReceiver, intentFilter);
//        back = getVitualNavigationKey(this, "accessibility_back",
//                "com.android.systemui", "");
//        home = getVitualNavigationKey(this, "accessibility_home",
//                "com.android.systemui", "");
//        recent = getVitualNavigationKey(this, "accessibility_recent",
//                "com.android.systemui", "");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(accessibilityReceiver);
        super.onDestroy();
    }

//    @Override
//    protected boolean onKeyEvent(KeyEvent paramKeyEvent) {
//        if (paramKeyEvent == null) return false;
//        if (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_HOME) {
//            if (FloatWindow.isShown) FloatWindow.hidePopupWindow();
//        }
//        if (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_APP_SWITCH) {
//            if (FloatWindow.isShown) FloatWindow.hidePopupWindow();
//        }
//        return false;
//    }

    public void pasteToEditView() {
        AccessibilityNodeInfo rootInActiveWindow = this.getRootInActiveWindow();
        if (retryTimes < 10) {
            String packageName = null;
            if (rootInActiveWindow != null) {
                packageName = String.valueOf(rootInActiveWindow.getPackageName());
            }

            if (rootInActiveWindow == null || packageName != null
                    && packageName.contains("soptqs.paste")) {
                ++retryTimes;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pasteToEditView();
                    }
                }, 100);
                return;
            }
        }
        findEditViewAndPaste(rootInActiveWindow);
    }

    public ArrayList findEditViewAndPaste(AccessibilityNodeInfo accessibilityNodeInfo) {
        ArrayList<CopyNode> arrayList = new ArrayList();
        if (accessibilityNodeInfo == null) {
            return arrayList;
        }
        for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
            arrayList.addAll(findEditViewAndPaste(accessibilityNodeInfo.getChild(i)));
        }
        if (accessibilityNodeInfo.getClassName() != null
                && accessibilityNodeInfo.getClassName().equals("android.widget.EditText")) {
            if (accessibilityNodeInfo.isFocused()) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            }
            return arrayList;
        } else {
        }
        return arrayList;
    }

    public void copy() {
        AccessibilityNodeInfo rootInActiveWindow = this.getRootInActiveWindow();
        if (retryTimes < 10) {
            String packageName = null;
            if (rootInActiveWindow != null) {
                packageName = String.valueOf(rootInActiveWindow.getPackageName());
            }

            if (rootInActiveWindow == null || packageName != null
                    && packageName.contains("soptqs.paste")) {
                ++retryTimes;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        copy();
                    }
                }, 100);
                return;
            }
        }

        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels = displayMetrics.widthPixels;

        ArrayList<CopyNode> copyNodes = getCnode(rootInActiveWindow, widthPixels, heightPixels);
        if (copyNodes.size() > 0) {
            Intent intent = new Intent(this, UniversalCopyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putParcelableArrayListExtra("copy_nodes", copyNodes);
            startActivity(intent, ActivityOptions.makeCustomAnimation(getBaseContext(),
                    R.anim.fade_in, R.anim.fade_out).toBundle());
        } else {
            ToastUtils.setBgColor(getResources().getColor(R.color.colorRed));
            ToastUtils.showLong(R.string.universe_copy_no_content);
        }

        retryTimes = 0;
    }

    private ArrayList getCnode(AccessibilityNodeInfo accessibilityNodeInfo, int width, int height) {
        ArrayList<CopyNode> arrayList = new ArrayList();
        if (accessibilityNodeInfo == null) {
            return arrayList;
        }
        for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
            arrayList.addAll(getCnode(accessibilityNodeInfo.getChild(i), width, height));
        }
        if (accessibilityNodeInfo.getClassName() != null
                && accessibilityNodeInfo.getClassName().equals("android.webkit.WebView")) {
            return arrayList;
        } else {
            if (accessibilityNodeInfo.getContentDescription() != null) {
                rect = new Rect();
                accessibilityNodeInfo.getBoundsInScreen(rect);
                if (checkBound(rect, width, height)) {
                    arrayList.add(new CopyNode(rect,
                            accessibilityNodeInfo.getContentDescription().toString()));
                }
            }
            if (accessibilityNodeInfo.getText() != null) {
                rect = new Rect();
                accessibilityNodeInfo.getBoundsInScreen(rect);
                if (checkBound(rect, width, height)) {
                    arrayList.add(new CopyNode(rect, accessibilityNodeInfo.getText().toString()));
                }
            }
        }
        return arrayList;
    }

    private boolean checkBound(Rect var1, int var2, int var3) {
        return var1.bottom >= 0 && var1.right >= 0 && var1.top <= var3 && var1.left <= var2;
    }

    class AccessibilityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("soptq.intent.UNIVERSE_COPY".equals(intent.getAction())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        copy();
                    }
                }, 500);
            }
            if ("soptq.intent.ACCESSIBILITY_PASTE".equals(intent.getAction())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pasteToEditView();
                    }
                }, 500);
            }
        }
    }

}
