package soptqs.paste.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import org.angmarch.views.NiceSpinner;
import org.litepal.crud.DataSupport;
import soptqs.paste.R;
import soptqs.paste.adapters.DataWatcherAdapter;
import soptqs.paste.database.CardItem;
import soptqs.paste.database.ClipSaves;
import soptqs.paste.database.DataProcess;
import soptqs.paste.utils.AppUtils;
import soptqs.paste.utils.PreferenceUtils;
import soptqs.paste.utils.SimpleDateFormatThreadSafe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S0ptq on 2018/2/11.
 */

public class QuickLook extends AppCompatActivity {

    final static int PERMISSION = 1;

    View toolBarView;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CircleImageView imgPermission;
    CircleImageView imgDatabase;
    CircleImageView imgService;
    CircleImageView imgCloud;
    View viewPermission;
    View viewDatabase;
    View viewCloud;
    View viewService;
    CircleImageView obj;
    SharedPreferences prefs;

    int mcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_look);
        Intent intent = getIntent();
        final String bgColor = intent.getStringExtra("bgColor");
        final String title = intent.getStringExtra("title");
        final int category = intent.getIntExtra("category", 0);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mcategory = category;
        imgPermission = findViewById(R.id.img_permission);
        imgDatabase = findViewById(R.id.img_database);
        imgService = findViewById(R.id.img_service);
        imgCloud = findViewById(R.id.img_cloud);
        iconProcess(category);
        toolbar = findViewById(R.id.toolbar);
        toolBarView = findViewById(R.id.toolbar_view);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(title);
        viewPermission = findViewById(R.id.contentPermission);
        viewDatabase = findViewById(R.id.contentDatabase);
        viewService = findViewById(R.id.contentService);
        viewCloud = findViewById(R.id.contentCloud);
        Transition transition = getWindow().getSharedElementEnterTransition();
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                performAnim(obj, bgColor);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        switch (category) {
            case 1:
                initContentPermission();
                break;
            case 2:
                initContentDatabase();
                break;
            case 3:
                initContentService();
                break;
            case 4:
                initContentCloud();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (mcategory) {
            case 1:
                ImageView permissionAccessibilityImage = findViewById(R.id.setting_permission_access);
                ImageView permissionFloatWindowImage = findViewById(R.id.setting_permission_float);
                Boolean accessIsPermitted = false;
                Boolean floatIsPermitted = false;

                if (AppUtils.isAccessibilitySettingsOn(this)) accessIsPermitted = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) floatIsPermitted = true;
                } else floatIsPermitted = true;

                if (!accessIsPermitted) {
                    permissionAccessibilityImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorRed)));
                } else
                    permissionAccessibilityImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorgreen)));

                if (!floatIsPermitted) {
                    permissionFloatWindowImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorRed)));
                } else
                    permissionFloatWindowImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorgreen)));
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    private void initContentPermission() {
        viewPermission.setVisibility(View.VISIBLE);
        viewPermission.invalidate();
        View permissionAccessibility = findViewById(R.id.permission_access);
        View permissionFloatWindow = findViewById(R.id.permission_floatwindow);

        permissionAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        permissionFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.setBgColor(ContextCompat.getColor(QuickLook.this, R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.toast_tip1);
                }
            }
        });

    }

    private void initContentDatabase() {
        viewDatabase.setVisibility(View.VISIBLE);
        viewDatabase.invalidate();
        CardView deleteDatabase = findViewById(R.id.database_delete_database);
        NiceSpinner autocleanSpinner = findViewById(R.id.database_auto_clear_spinner);
        final SimpleDateFormatThreadSafe sdf = new SimpleDateFormatThreadSafe("yyyy-MM-dd");
        final List<CardItem> cardItemList = new ArrayList<>();
        new Thread(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                final List<ClipSaves> clipSavesList = DataSupport.order("time desc").limit(20).find(ClipSaves.class);
                for (ClipSaves clipSaves : clipSavesList) {
                    i++;
                    String name = clipSaves.getPakageName();
                    try {
                        name = QuickLook.this.getPackageManager().getApplicationLabel(QuickLook.this.getPackageManager().getApplicationInfo(name, 0)).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    CardItem item = new CardItem(name, sdf.format(Long.parseLong(clipSaves.getTime())),
                            null, clipSaves.getContent(), i, 0, false,
                            null, null, false);
                    cardItemList.add(item);
                }
            }
        }).start();
        RecyclerView databaseRecyclerView = findViewById(R.id.database_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        databaseRecyclerView.setLayoutManager(layoutManager);
        final DataWatcherAdapter dataWatcherAdapter = new DataWatcherAdapter(cardItemList);
        databaseRecyclerView.setAdapter(dataWatcherAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.database_recyclerView_view).setVisibility(View.VISIBLE);
                findViewById(R.id.database_recyclerView_view).invalidate();
                dataWatcherAdapter.notifyItemChanged(0);
            }
        }, 1000);

        deleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(QuickLook.this)
                        .title(getResources().getString(R.string.database_dialog_warning))
                        .content(Html.fromHtml(getResources().getString(R.string.database_dialog_content)))
                        .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DataProcess.deleteAllData(QuickLook.this);
                                cardItemList.clear();
                                dataWatcherAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });

        ArrayAdapter<CharSequence> autoCleanAdapter = ArrayAdapter.createFromResource(this, R.array.array_auto_clear, android.R.layout.simple_spinner_item);
        autoCleanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autocleanSpinner.setAdapter(autoCleanAdapter);
        int index = 0;
        switch (prefs.getString(PreferenceUtils.PREF_AUTOCLEAR, "forever")) {
            case "3":
                index = 0;
                break;
            case "7":
                index = 1;
                break;
            case "30":
                index = 2;
                break;
            case "forever":
                index = 3;
                break;
            default:
                break;
        }
        autocleanSpinner.setSelectedIndex(index);
        autocleanSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        prefs.edit().putString(PreferenceUtils.PREF_AUTOCLEAR, "3").apply();
                        break;
                    case 1:
                        prefs.edit().putString(PreferenceUtils.PREF_AUTOCLEAR, "7").apply();
                        break;
                    case 2:
                        prefs.edit().putString(PreferenceUtils.PREF_AUTOCLEAR, "30").apply();
                        break;
                    case 3:
                        prefs.edit().putString(PreferenceUtils.PREF_AUTOCLEAR, "forever").apply();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initContentService() {
        viewService.setVisibility(View.VISIBLE);
        viewService.invalidate();
    }

    private void initContentCloud() {
        viewCloud.setVisibility(View.VISIBLE);
        viewCloud.invalidate();
    }

    private void iconProcess(int i) {
        switch (i) {
            case 1:
                imgDatabase.setVisibility(View.GONE);
                imgService.setVisibility(View.GONE);
                imgCloud.setVisibility(View.GONE);
                obj = imgPermission;
                break;
            case 2:
                imgPermission.setVisibility(View.GONE);
                imgService.setVisibility(View.GONE);
                imgCloud.setVisibility(View.GONE);
                obj = imgDatabase;
                break;
            case 3:
                imgPermission.setVisibility(View.GONE);
                imgDatabase.setVisibility(View.GONE);
                imgCloud.setVisibility(View.GONE);
                obj = imgService;
                break;
            case 4:
                imgPermission.setVisibility(View.GONE);
                imgDatabase.setVisibility(View.GONE);
                imgService.setVisibility(View.GONE);
                obj = imgCloud;
                break;
            default:
                break;
        }
    }

    private void performAnim(CircleImageView obj, final String bgColor) {
        float finalRadius = (float) Math.hypot(toolBarView.getWidth(), toolBarView.getHeight());
        final Animator anim = ViewAnimationUtils.createCircularReveal(
                toolBarView,
                (obj.getLeft() + obj.getRight()) / 2,
                (obj.getTop() + obj.getBottom()) / 2,
                (float) obj.getWidth() / 2,
                finalRadius);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                toolBarView.setBackgroundResource(Integer.valueOf(bgColor));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //show views
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                anim.start();
            }
        }, 200);
    }
}
