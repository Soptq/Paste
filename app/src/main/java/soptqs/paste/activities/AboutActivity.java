package soptqs.paste.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.blankj.utilcode.util.ToastUtils;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import soptqs.paste.R;
import soptqs.paste.dialog.ChangelogDialog;
import soptqs.paste.utils.AppUtils;

/**
 * Created by S0ptq on 2018/2/16.
 *
 * "关于"页面。
 *
 * 2018年5月6日重构了一次，写完了注释。
 */

public class AboutActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View star;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private View changeLog;
    private View openSource;
    private View website;
    private View bugs;
    private View intro;
    private View androidLink;
    private View translateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        寻找控件
        findViews();
//        初始化控件
        initViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
//                购买页面通过读取IMEI号来生成激活码，所以需要电话权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    如果允许权限，则前往购买页面
                    startActivity(new Intent(AboutActivity.this, BillingActivity.class));
                } else {
//                    如果不允许，则构建一个dialog来说明为什么需要这个权限
                    new MaterialDialog.Builder(AboutActivity.this)
                            .title(getResources().getString(R.string.why_title))
                            .content(Html.fromHtml(getResources().getString(R.string.why_content)))
                            .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                            .show();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        侧边栏选择这个页面
        navigationView.setCheckedItem(R.id.nav_about);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                点击三杠打开侧边栏
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews(){
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.main_drawer);
        actionBar = getSupportActionBar();
        navigationView = findViewById(R.id.nav_view);
        changeLog = findViewById(R.id.about_updatelog);
        openSource = findViewById(R.id.about_opensource);
        website = findViewById(R.id.about_website);
        bugs = findViewById(R.id.about_bugs);
        star = findViewById(R.id.about_star);
        intro = findViewById(R.id.about_intro);
        androidLink = findViewById(R.id.about_android_links);
        translateView = findViewById(R.id.about_translation);
    }

    private void initViews(){
        setSupportActionBar(toolbar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
//                        300延时是为了让侧边栏收回
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(AboutActivity.this, MainActivity.class));
                            }
                        }, 300);
                        break;
                    case R.id.nav_bill:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (ContextCompat.checkSelfPermission(AboutActivity.this,
                                        Manifest.permission.READ_PHONE_STATE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AboutActivity.this,
                                            new String[]{Manifest.permission.READ_PHONE_STATE},
                                            1);
                                } else {
                                    startActivity(new Intent(AboutActivity.this, BillingActivity.class));
                                }
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

//        开源库信息
        openSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Notices notices = new Notices();
                notices.addNotice(new Notice("Android Open Source Project",
                        "https://developer.android.com/topic/libraries/support-library/index.html",
                        "Copyright (c) 2017 The Android Open Source Project",
                        new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("Android Support Libraries",
                        "https://developer.android.com/topic/libraries/support-library/index.html",
                        "Copyright (c) 2017 The Android Open Source Project",
                        new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("SwipeRecyclerView",
                        "https://github.com/yanzhenjie/SwipeRecyclerView",
                        "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION",
                        new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("TextFieldBoxes",
                        "https://github.com/HITGIF/TextFieldBoxes",
                        " TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION",
                        new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("android-issue-reporter",
                        "https://github.com/heinrichreimer/android-issue-reporter",
                        "Copyright (c) 2017 Jan Heinrich Reimer",
                        new MITLicense()));
                notices.addNotice(new Notice("material-intro",
                        "https://github.com/heinrichreimer/material-intro",
                        "Copyright (c) 2017 Jan Heinrich Reimer",
                        new MITLicense()));
                notices.addNotice(new Notice("LicensesDialog",
                        "https://github.com/PSDev/LicensesDialog",
                        "Copyright 2013 Philip Schiffer <admin@psdev.de>",
                        new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("material-dialogs",
                        "https://github.com/afollestad/material-dialogs",
                        "Copyright (c) 2014-2016 Aidan Michael Follestad",
                        new MITLicense()));
                notices.addNotice(new Notice("LitePal",
                        "https://github.com/LitePalFramework/LitePal",
                        "Copyright (C)  Tony Green, LitePal Framework Open Source Project",
                        new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("Android Links",
                        "https://github.com/android-links/AndroidLinks",
                        " ",
                        null));
                notices.addNotice(new Notice("MaterialPopupMenu",
                        "https://github.com/zawadz88/MaterialPopupMenu",
                        " ",
                        null));
                notices.addNotice(new Notice("android-inapp-billing-v3",
                        "https://github.com/anjlab/android-inapp-billing-v3",
                        "Copyright 2014 AnjLab",
                        new ApacheSoftwareLicense20()));
                new LicensesDialog.Builder(AboutActivity.this)
                        .setNotices(notices)
                        .build()
                        .show();
            }
        });

//        关于开发者的个人网站
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri uri = Uri.parse("http://soptq.me");
                intent.setData(uri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.setBgColor(ContextCompat.getColor(AboutActivity.this, R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.webview_error);
                }
            }
        });

//        帮助翻译
        translateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri uri = Uri.parse("https://www.transifex.com/soptq/paste/");
                intent.setData(uri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.setBgColor(ContextCompat.getColor(AboutActivity.this, R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.webview_error);
                }
            }
        });

//        反馈bug
        bugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this, IssuerReporterActivity.class);
                startActivityForResult(intent, 1);
            }
        });

//        重新显示介绍页面
        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this, MainIntroActivity.class);
                startActivity(intent);
            }
        });

//        安卓友链
        androidLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this, AndroidLinks.class);
                startActivity(intent);
            }
        });

//        给APP评分
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.openApplicationMarket(getPackageName(), "com.coolapk.market",
                        AboutActivity.this);
            }
        });

//        跟新日志
        changeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accentColor = ThemeSingleton.get().widgetColor;
                if (accentColor == 0) {
                    accentColor = ContextCompat.getColor(AboutActivity.this, R.color.colorAPP);
                }
                ChangelogDialog.create(false, accentColor).show(getSupportFragmentManager(), "changelog");
            }
        });
    }
}
