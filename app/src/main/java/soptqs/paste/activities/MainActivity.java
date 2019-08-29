package soptqs.paste.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;
import com.yhao.floatwindow.*;
import soptqs.paste.R;
import soptqs.paste.adapters.SimplePagerAdapter;
import soptqs.paste.database.DataProcess;
import soptqs.paste.fragments.AppearanceFragment;
import soptqs.paste.fragments.EntranceFragment;
import soptqs.paste.fragments.HelpFragment;
import soptqs.paste.fragments.SettingsFragment;
import soptqs.paste.services.ClipBoardListener;
import soptqs.paste.services.FloatButtonOrientationService;
import soptqs.paste.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S0ptq on 2018/2/5.
 * 主页面
 *
 * TODO:1.按钮换为 material switcher   2.开关关闭动画
 */

public class MainActivity extends AppCompatActivity {

    private static final String START_KEY = "isFirst";
    private DrawerLayout drawerLayout;
    private View appIcon;
    private LottieAnimationView animationView;
    private FloatingActionButton fabShow;
    private SwitchCompat mainToggle;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private NavigationView navigationView;
//    private ImageView header;
//    private View headerLayout;

    private SharedPreferences prefs;

    private int[] imageResId = {
            R.drawable.ic_action_home,
            R.drawable.ic_style,
            R.drawable.ic_action_process_start,
            R.drawable.ic_lightbulb_outline
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        findViews();
        initViews();
    }

    private void initMainToggle() {
        mainToggle.setChecked(prefs.getBoolean(PreferenceUtils.PREF_Toggle, false));
        if (!mainToggle.isChecked()) {

        }

        mainToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mainToggle.setEnabled(false);
                prefs.edit().putBoolean(PreferenceUtils.PREF_Toggle, b).apply();
                if (b) {
                    Intent intent = new Intent(MainActivity.this, ClipBoardListener.class);
                    MainActivity.this.startService(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, ClipBoardListener.class);
                    MainActivity.this.stopService(intent);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainToggle.setEnabled(true);
                    }
                }, 500);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, BillingActivity.class));
                } else {
                    new MaterialDialog.Builder(MainActivity.this)
                            .title(getResources().getString(R.string.why_title))
                            .content(Html.fromHtml(getResources().getString(R.string.why_content)))
                            .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                            .show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //
                } else {
                    new MaterialDialog.Builder(MainActivity.this)
                            .title(getResources().getString(R.string.base_permission))
                            .content(Html.fromHtml(getResources().getString(R.string.base_permission_sub)))
                            .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                            .show();
                }
            }
            default:
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_syncDatabase:
                DataProcess.autoClear(MainActivity.this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void intro() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean(START_KEY, true);
        if (firstStart) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(START_KEY, false);
            editor.apply();
            prefs.edit().putInt(PreferenceUtils.TEMP_DATA, (int) (Math.random() * 1000000000) * 2 + 1).apply();
            startActivity(new Intent(MainActivity.this, MainIntroActivity.class));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final List<String> permissionsList = new ArrayList<>();
                if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || !addPermission(permissionsList, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 2);
                }
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            return shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }

    private void findViews(){
        mainToggle = findViewById(R.id.settingServiceToggle);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabShow = findViewById(R.id.fab_show);
        drawerLayout = findViewById(R.id.main_drawer);
        navigationView = findViewById(R.id.nav_view);
        animationView = findViewById(R.id.icon_anim);
//        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
//        header = headerLayout.findViewById(R.id.hearder_bigpic);
    }

    private void initViews(){
        //        Lottie动画
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationView.useHardwareAcceleration(true);
                animationView.playAnimation();
                animationView.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        appIcon = findViewById(R.id.icon);
                        ValueAnimator alphaAnim = ObjectAnimator.ofFloat(appIcon, "alpha", 1f, 0f);
                        alphaAnim.setDuration(300);
                        alphaAnim.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                appIcon.setVisibility(View.GONE);
                            }
                        }, 300);
                        intro();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        }, 1000);
        initMainToggle();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_bill:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (ContextCompat.checkSelfPermission(MainActivity.this,
                                        Manifest.permission.READ_PHONE_STATE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.READ_PHONE_STATE},
                                            1);
                                } else {
                                    startActivity(new Intent(MainActivity.this, BillingActivity.class));
                                }
                            }
                        }, 300);
                        break;
                    case R.id.nav_about:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            }
                        }, 300);
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        viewPager.setAdapter(new SimplePagerAdapter(this, viewPager, getSupportFragmentManager(),
                new SettingsFragment(),
                new AppearanceFragment(),
                new EntranceFragment(),
                new HelpFragment()));
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < imageResId.length; i++) {
//            mTitleIcons[i]和mTitleNames[i]是放图片和文字的资源的数组
            tabLayout.getTabAt(i).setIcon(imageResId[i]);
//            这个是设置选中和没选中的文字的颜色
//            tablayout.setTabTextColors( Color.parseColor("#666666"),Color.parseColor("#ff6b00"));
        }
//        数据库维护
        DataProcess.autoClear(MainActivity.this);
        fabShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("soptq.intent.SHOW_POPUPWINDOW");
                sendBroadcast(intent);
            }
        });
        initFloatButton();
    }

    private void initFloatButton(){
        if (prefs.getBoolean(PreferenceUtils.PREF_Toggle, false) && prefs.getBoolean(PreferenceUtils.PREF_FLOAT_BUTTON, false)) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.mipmap.float_button);
            float alpha = (float) prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_ALPHA, 100) / 100;
            float size = (float) prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_SIZE, 50) / 500;
            if (size < 0.04f) size = 0.04f;
            imageView.setAlpha(alpha);
            //Todo:悬浮窗记录屏幕位置
            //TODO:横屏更新xy
            try {
                FloatWindow
                        .with(getApplicationContext())
                        .setView(imageView)
                        .setWidth(Screen.width, size) //设置悬浮控件宽高
                        .setHeight(Screen.width, size)
                        .setX(Screen.width, 0.8f)
                        .setY(Screen.height, 0.3f)
                        .setMoveType(MoveType.slide,-50,-50)
                        .setMoveStyle(200, new AccelerateInterpolator())
                        .setViewStateListener(mViewStateListener)
                        .setPermissionListener(mPermissionListener)
                        .setDesktopShow(true)
                        .build();

                startService(new Intent(MainActivity.this, FloatButtonOrientationService.class));

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent("soptq.intent.SHOW_POPUPWINDOW");
                        sendBroadcast(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onFail() {

        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {

        }

        @Override
        public void onShow() {

        }

        @Override
        public void onHide() {

        }

        @Override
        public void onDismiss() {

        }

        @Override
        public void onMoveAnimStart() {

        }

        @Override
        public void onMoveAnimEnd() {

        }

        @Override
        public void onBackToDesktop() {

        }
    };
}
