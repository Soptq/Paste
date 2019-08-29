package soptqs.paste.fragments;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import james.colorpickerdialog.dialogs.ColorPickerDialog;
import james.colorpickerdialog.dialogs.PreferenceDialog;
import org.angmarch.views.NiceSpinner;
import org.litepal.crud.DataSupport;
import soptqs.paste.R;
import soptqs.paste.activities.BlackListActivity;
import soptqs.paste.activities.QuickLook;
import soptqs.paste.database.DataProcess;
import soptqs.paste.database.RegexSaves;
import soptqs.paste.services.ClipBoardListener;
import soptqs.paste.utils.AppUtils;
import soptqs.paste.utils.PreferenceUtils;
import soptqs.paste.utils.SU;
import soptqs.paste.views.ColorImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by S0ptq on 2018/2/5.
 */

public class SettingsFragment extends BaseFragment {

    private static final String START_KEY = "isFirstToStart";
    private final int MODE_ROOT = 0;
    private final int MODE_ACCESSIBILITY = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ColorImageView customColor;
    private NiceSpinner colorMethodSpinner;
    private SwitchCompat colorfulButton;
    private CircleImageView imgPermission;
    private CircleImageView imgDatabasen;
    private CircleImageView imgService;
    private CircleImageView imgCloud;
    private SwitchCompat autoBootButton;
    private View restartService;
    private TextView cardNumDisplay;
    private View showNotiView;
    private SwitchCompat showNotiButton;
    private View notiClipView;
    private SwitchCompat notiClipButton;
    private SwitchCompat notiShowButton;
    private ImageView permissionStatus;
    private TextView floatAlphaText;
    private SwitchCompat qrDotSwitch;
    private SwitchCompat navButton;
    private SwitchCompat disableAdsSwitch;
    private View enableShare;
    private SwitchCompat enableShareButton;
    private TextView compressorText;
    private AppCompatSeekBar compressor;
    private ImageView shareAbout;
    private ImageView compressorAbout;
    private View regexBlocking;
    private View smsEnabler;
    private View blAppView;
    private View colorfulView;
    private View customColorView;
    private NiceSpinner workMode;
    private View viewPermission;
    private View viewDatabase;
    private View viewServicen;
    private View viewCloud;
    private View autoBootView;
    private View cardNum;
    private View notiShow;
    private NiceSpinner cardMode;
    private ImageView cardModeAbout;
    private AppCompatSeekBar floatAlphaSet;
    private View qrbg;
    private View qrDotView;
    private View navButtonView;
    private View disableAdsView;
    private SwitchCompat smsSwitcher;
    private View tipsView;
    private SwitchCompat tipSwitch;


    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        findViews(view);
        initViews();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    final Uri uri = intent.getData();
                    assert uri != null;
                    prefs.edit().putString(PreferenceUtils.PREF_QR_BG, uri.toString()).apply();
                    ToastUtils.setBgColor(ContextCompat.getColor(Objects.requireNonNull(this.getContext()), R.color.colorgreen));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.saved);
                }
            }
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Boolean accessIsPermitted = false;
        Boolean floatIsPermitted = false;
        if (AppUtils.isAccessibilitySettingsOn(Objects.requireNonNull(getContext()))) accessIsPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(getContext())) {
                floatIsPermitted = true;
            }
        }
        if (!(accessIsPermitted && floatIsPermitted)) {
            permissionStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_black_24dp));
            permissionStatus.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorRed)));
        } else {
            permissionStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
            permissionStatus.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorgreen)));
        }
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.fragment_1);
    }

    private void intro() {
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean(START_KEY, true);
        if (firstStart) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(START_KEY, false);
            editor.apply();
            new MaterialDialog.Builder(getContext())
                    .title(getResources().getString(R.string.first_start_service_title))
                    .content(getResources().getString(R.string.first_start_service_content))
                    .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                    .show();
        }
    }

    private void getPermission(String permission) {
        ActivityCompat.requestPermissions(Objects.requireNonNull(this.getActivity()), new String[]{permission}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    prefs.edit().putBoolean(PreferenceUtils.PREF_SMS_ENABLE, false).apply();
                    smsSwitcher.setChecked(false);
                } else {
                    prefs.edit().putBoolean(PreferenceUtils.PREF_SMS_ENABLE, true).apply();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void findViews(View view){
        swipeRefreshLayout = view.findViewById(R.id.setting_fragment_refresh);
        colorfulView = view.findViewById(R.id.setting_colorful_view);
        colorfulButton = view.findViewById(R.id.setting_colorful_switch);
        customColorView = view.findViewById(R.id.customColorView);
        customColor = view.findViewById(R.id.customColor);
        colorMethodSpinner = view.findViewById(R.id.setting_color_picking_method);
        workMode = view.findViewById(R.id.setting_mode);
        viewPermission = view.findViewById(R.id.view_permission);
        imgPermission = view.findViewById(R.id.img_permission);
        viewDatabase = view.findViewById(R.id.view_database);
        imgDatabasen = view.findViewById(R.id.img_database);
        viewServicen = view.findViewById(R.id.view_service);
        imgService = view.findViewById(R.id.img_service);
        viewCloud = view.findViewById(R.id.view_cloud);
        imgCloud = view.findViewById(R.id.img_cloud);
        autoBootView = view.findViewById(R.id.setting_autoBoot_view);
        autoBootButton = view.findViewById(R.id.setting_autoBoot_button);
        restartService = view.findViewById(R.id.setting_restart_service);
        cardNum = view.findViewById(R.id.setting_cardnum);
        cardNumDisplay = view.findViewById(R.id.setting_cardnum_display);
        showNotiView = view.findViewById(R.id.setting_enable_noti_entry_view);
        showNotiButton = view.findViewById(R.id.setting_enable_noti_entry_button);
        notiClipView = view.findViewById(R.id.setting_noticlip_view);
        notiClipButton = view.findViewById(R.id.setting_noticlip_button);
        permissionStatus = view.findViewById(R.id.img_permission_status);
        notiShow = view.findViewById(R.id.setting_show_noti);
        notiShowButton = view.findViewById(R.id.setting_noti_show_button);
        cardMode = view.findViewById(R.id.setting_card_mode);
        cardModeAbout = view.findViewById(R.id.card_mode_about);
        floatAlphaText = view.findViewById(R.id.floatwindow_alpha_textview);
        floatAlphaSet = view.findViewById(R.id.floatwindow_alpha_seekbar);
        qrbg = view.findViewById(R.id.setting_qr);
        qrDotView = view.findViewById(R.id.setting_qr_dot);
        qrDotSwitch = view.findViewById(R.id.setting_qr_dot_switch);
        navButtonView = view.findViewById(R.id.setting_nav_button_view);
        navButton = view.findViewById(R.id.setting_nav_button_switch);
        disableAdsView = view.findViewById(R.id.setting_disable_ads);
        disableAdsSwitch = view.findViewById(R.id.setting_disable_ads_switch);
        blAppView = view.findViewById(R.id.setting_bl_app_view);
        enableShare = view.findViewById(R.id.setting_share_enable_view);
        enableShareButton = view.findViewById(R.id.setting_share_enable_button);
        compressor = view.findViewById(R.id.setting_share_compressor);
        compressorText = view.findViewById(R.id.setting_share_compressor_text);
        shareAbout = view.findViewById(R.id.card_share_tip);
        compressorAbout = view.findViewById(R.id.compressor_about);
        regexBlocking = view.findViewById(R.id.setting_rg);
        smsEnabler = view.findViewById(R.id.setting_sms_enable);
        smsSwitcher = view.findViewById(R.id.setting_sms_enable_switch);
        tipsView = view.findViewById(R.id.setting_tips);
        tipSwitch = view.findViewById(R.id.setting_tips_switch);
    }

    private void initViews(){
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorAPP),
                getResources().getColor(R.color.colorWhite),
                getResources().getColor(R.color.readerColor),
                getResources().getColor(R.color.colorPrimaryDark));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DataProcess.maintainDataBase(getContext());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });


        Boolean accessIsPermitted = false;
        Boolean floatIsPermitted = false;
        if (AppUtils.isAccessibilitySettingsOn(getContext())) accessIsPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (Settings.canDrawOverlays(getContext())) floatIsPermitted = true;
        }
        if (!(accessIsPermitted && floatIsPermitted)) {
            permissionStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_black_24dp));
            permissionStatus.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorRed)));
        }

        colorfulButton.setChecked(prefs.getBoolean(PreferenceUtils.PREF_COLORFUL, true));
        colorfulButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_COLORFUL, b).apply();
                if (!colorfulButton.isChecked()) {
                    colorMethodSpinner.setEnabled(false);
                    colorMethodSpinner.setTintColor(R.color.colorgrey);
                    colorMethodSpinner.setHintTextColor(getResources().getColor(R.color.colorgrey));
                    colorMethodSpinner.setTextColor(getResources().getColor(R.color.colorgrey));
                }else {
                    colorMethodSpinner.setEnabled(true);
                    colorMethodSpinner.setTintColor(R.color.colorBlack);
                    colorMethodSpinner.setHintTextColor(getResources().getColor(R.color.colorBlack));
                    colorMethodSpinner.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }
        });

        colorfulView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorfulButton.setChecked(!colorfulButton.isChecked());
            }
        });

        if (!colorfulButton.isChecked()) {
            colorMethodSpinner.setEnabled(false);
            colorMethodSpinner.setTintColor(R.color.colorgrey);
            colorMethodSpinner.setHintTextColor(getResources().getColor(R.color.colorgrey));
            colorMethodSpinner.setTextColor(getResources().getColor(R.color.colorgrey));
        }

        customColor.setColor(prefs.getInt(PreferenceUtils.PREF_CUSTOM_COLOR, Color.BLACK));
        customColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog(getContext())
                        .setDefaultPreference(Color.BLACK)
                        .setPreference(prefs.getInt(PreferenceUtils.PREF_CUSTOM_COLOR, Color.BLACK))
                        .setListener(new PreferenceDialog.OnPreferenceListener<Integer>() {
                            @Override
                            public void onPreference(PreferenceDialog dialog, Integer preference) {
                                prefs.edit().putInt(PreferenceUtils.PREF_CUSTOM_COLOR, preference).apply();
                                customColor.setColor(preference);
                            }

                            @Override
                            public void onCancel(PreferenceDialog dialog) {
                            }
                        })
                        .show();
            }
        });

        ArrayAdapter<CharSequence> colorMethodAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_color_methods, android.R.layout.simple_spinner_item);
        colorMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorMethodSpinner.setAdapter(colorMethodAdapter);
        colorMethodSpinner.setSelectedIndex(prefs.getInt(PreferenceUtils.PREF_COLOR_METHOD, 0));
        colorMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.edit().putInt(PreferenceUtils.PREF_COLOR_METHOD, i).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (prefs.getInt(PreferenceUtils.PREF_WORK_MODE, 1) == MODE_ROOT && !SU.isRooted()) {
            prefs.edit().putInt(PreferenceUtils.PREF_WORK_MODE, 1).apply();
        }

        ArrayAdapter<CharSequence> workModeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_mode, android.R.layout.simple_spinner_item);
        workModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workMode.setAdapter(workModeAdapter);
        workMode.setSelectedIndex(prefs.getInt(PreferenceUtils.PREF_WORK_MODE, 1));
        workMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == MODE_ROOT) {
                    Boolean isRoot = SU.isRooted();
                    if (!isRoot){
                        ToastUtils.setBgColor(ContextCompat.getColor(SettingsFragment.this.getContext(), R.color.colorRed));
                        ToastUtils.setMsgColor(Color.WHITE);
                        ToastUtils.showLong(R.string.noRoot);
                    }else prefs.edit().putInt(PreferenceUtils.PREF_WORK_MODE, i).apply();
                }else prefs.edit().putInt(PreferenceUtils.PREF_WORK_MODE, i).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        viewPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuickLook.class);
                intent.putExtra("bgColor", String.valueOf(R.color.colorPurple));
                intent.putExtra("title", getResources().getString(R.string.quicklook_permission));
                intent.putExtra("category", 1);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), imgPermission, imgPermission.getTransitionName());
                startActivity(intent, options.toBundle());
            }
        });

        viewDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuickLook.class);
                intent.putExtra("bgColor", String.valueOf(R.color.colorCyan));
                intent.putExtra("title", getResources().getString(R.string.quicklook_database));
                intent.putExtra("category", 2);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), imgDatabasen, imgDatabasen.getTransitionName());
                startActivity(intent, options.toBundle());
            }
        });

        viewServicen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuickLook.class);
                intent.putExtra("bgColor", String.valueOf(R.color.colorTeal));
                intent.putExtra("title", getResources().getString(R.string.quick_service));
                intent.putExtra("category", 3);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), imgService, imgService.getTransitionName());
                startActivity(intent, options.toBundle());
            }
        });

        viewCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuickLook.class);
                intent.putExtra("bgColor", String.valueOf(R.color.readerColor));
                intent.putExtra("title", getResources().getString(R.string.quick_cloud));
                intent.putExtra("category", 4);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), imgCloud, imgCloud.getTransitionName());
                startActivity(intent, options.toBundle());
            }
        });

        autoBootButton.setChecked(prefs.getBoolean(PreferenceUtils.PREF_AUTOBOOT, true));
        autoBootButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_AUTOBOOT, b).apply();
                Log.e("autoboot", "onCheckedChanged: " + prefs.getBoolean(PreferenceUtils.PREF_AUTOBOOT, true));
            }
        });

        autoBootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoBootButton.setChecked(!autoBootButton.isChecked());
            }
        });

        restartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopIntent = new Intent(getContext(), ClipBoardListener.class);
                getContext().stopService(stopIntent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent startIntent = new Intent(getContext(), ClipBoardListener.class);
                        getContext().startService(startIntent);
                    }
                }, 1000);
                ToastUtils.setBgColor(ContextCompat.getColor(SettingsFragment.this.getContext(), R.color.colorgreen));
                ToastUtils.setMsgColor(Color.WHITE);
                ToastUtils.showLong(R.string.restart);
            }
        });

        cardNumDisplay.setText(String.valueOf(prefs.getLong(PreferenceUtils.PREF_CARDNUM, 20)));
        cardNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppUtils.isPro(getContext())) {
                    ToastUtils.setBgColor(ContextCompat.getColor(SettingsFragment.this.getContext(), R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.isnotpro);
                } else {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.appearance_cardnum_dialog_title)
                            .content(Html.fromHtml(getString(R.string.appearance_cardnum_dialog_content)))
                            .inputRange(1, 18)
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .input("Custom", null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    prefs.edit().putLong(PreferenceUtils.PREF_CARDNUM, Long.valueOf(input.toString())).apply();
                                    cardNumDisplay.setText(input);
                                }
                            })
                            .show();
                }
            }
        });

        notiShowButton.setChecked(prefs.getBoolean(PreferenceUtils.PREF_NOTI_SHOW, true));
        if (!notiShowButton.isChecked()) {
            showNotiButton.setChecked(false);
            notiClipButton.setChecked(false);
            showNotiButton.setEnabled(false);
            notiClipButton.setEnabled(false);
            showNotiView.setEnabled(false);
            notiClipView.setEnabled(false);
        }
        notiShowButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_NOTI_SHOW, b).apply();
                if (!b) {
                    showNotiButton.setChecked(false);
                    notiClipButton.setChecked(false);
                    showNotiButton.setEnabled(false);
                    notiClipButton.setEnabled(false);
                    restartService.setEnabled(false);
                    showNotiView.setEnabled(false);
                    notiClipView.setEnabled(false);
                    Intent stopIntent = new Intent(getContext(), ClipBoardListener.class);
                    getContext().stopService(stopIntent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent startIntent = new Intent(getContext(), ClipBoardListener.class);
                            getContext().startService(startIntent);
                        }
                    }, 500);
                } else {
                    showNotiView.setEnabled(true);
                    notiClipView.setEnabled(true);
                    showNotiButton.setEnabled(true);
                    notiClipButton.setEnabled(true);
                    restartService.setEnabled(true);
                    showNotiButton.setChecked(true);
                    Intent stopIntent = new Intent(getContext(), ClipBoardListener.class);
                    getContext().stopService(stopIntent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent startIntent = new Intent(getContext(), ClipBoardListener.class);
                            getContext().startService(startIntent);
                        }
                    }, 500);
                }
            }
        });

        notiShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notiShowButton.setChecked(!notiShowButton.isChecked());
            }
        });

        showNotiButton.setChecked(prefs.getBoolean(PreferenceUtils.PREF_NOTIENTRY, true));
        showNotiButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_NOTIENTRY, b).apply();
                if (!b) notiClipButton.setChecked(false);
                Intent stopIntent = new Intent(getContext(), ClipBoardListener.class);
                getContext().stopService(stopIntent);
                Intent startIntent = new Intent(getContext(), ClipBoardListener.class);
                getContext().startService(startIntent);
            }
        });

        showNotiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotiButton.setChecked(!showNotiButton.isChecked());
            }
        });

        notiClipButton.setChecked(prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false));
        notiClipButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_NOTICLIP, b).apply();
                if (b) showNotiButton.setChecked(true);
                Intent stopIntent = new Intent(getContext(), ClipBoardListener.class);
                getContext().stopService(stopIntent);
                Intent startIntent = new Intent(getContext(), ClipBoardListener.class);
                getContext().startService(startIntent);
            }
        });

        notiClipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notiClipButton.setChecked(!notiClipButton.isChecked());
            }
        });

        ArrayAdapter<CharSequence> cardModeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_card_mode, android.R.layout.simple_spinner_item);
        cardModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardMode.setAdapter(cardModeAdapter);
        cardMode.setSelectedIndex(prefs.getInt(PreferenceUtils.PREF_CARD_PREFER, 0));
        cardMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.edit().putInt(PreferenceUtils.PREF_CARD_PREFER, i).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cardModeAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.card_mode_about))
                        .content(getResources().getString(R.string.card_mode_about_content))
                        .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                        .show();
            }
        });


        floatAlphaText.setText(String.valueOf(prefs.getInt(PreferenceUtils.PREF_FLOATWINDOW_ALPHA, 100)));
        floatAlphaText.setAlpha((float) prefs.getInt(PreferenceUtils.PREF_FLOATWINDOW_ALPHA, 100) / 100);

        floatAlphaSet.setProgress(prefs.getInt(PreferenceUtils.PREF_FLOATWINDOW_ALPHA, 100));
        floatAlphaSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                floatAlphaText.setText(String.valueOf(i));
                floatAlphaText.setAlpha((float) prefs.getInt(PreferenceUtils.PREF_FLOATWINDOW_ALPHA, 100) / 100);
                prefs.edit().putInt(PreferenceUtils.PREF_FLOATWINDOW_ALPHA, i).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        qrbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        qrDotSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_QR_DOT, true));
        qrDotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_QR_DOT, b).apply();
            }
        });

        qrDotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrDotSwitch.setChecked(!qrDotSwitch.isChecked());
            }
        });

        navButton.setChecked(prefs.getBoolean(PreferenceUtils.PREF_SHOW_NAV_BUTTON, true));
        navButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_SHOW_NAV_BUTTON, b).apply();
            }
        });

        navButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navButton.setChecked(!navButton.isChecked());
                ToastUtils.setBgColor(ContextCompat.getColor(SettingsFragment.this.getContext(), R.color.colorRed));
                ToastUtils.setMsgColor(Color.WHITE);
                ToastUtils.showLong(R.string.nav_botton_helper);
            }
        });

        disableAdsSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_DISABLE_ADS, false));
        disableAdsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (b) {
                    new MaterialDialog.Builder(SettingsFragment.this.getContext())
                            .title(R.string.setting_disable_ads_dialog_title)
                            .content(R.string.setting_disable_ads_dialog_content)
                            .positiveText(R.string.setting_disable_ads_dialog_continue)
                            .negativeText(R.string.setting_disable_ads_dialog_cancel)
                            .negativeFocus(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    prefs.edit().putBoolean(PreferenceUtils.PREF_DISABLE_ADS, b).apply();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    disableAdsSwitch.setChecked(false);
                                    ToastUtils.setBgColor(ContextCompat.getColor(SettingsFragment.this.getContext(), R.color.colorgreen));
                                    ToastUtils.setMsgColor(Color.WHITE);
                                    ToastUtils.showLong(R.string.pay_succ);
                                }
                            })
                            .show();
                } else {
                    prefs.edit().putBoolean(PreferenceUtils.PREF_DISABLE_ADS, b).apply();
                }
            }
        });

        disableAdsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableAdsSwitch.setChecked(!disableAdsSwitch.isChecked());
            }
        });

        blAppView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BlackListActivity.class);
                startActivity(intent);
            }
        });

        enableShareButton.setChecked(prefs.getBoolean(PreferenceUtils.PREF_ENABLE_SHARE, true));
        enableShareButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_ENABLE_SHARE, b).apply();
            }
        });

        enableShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableShareButton.setChecked(!enableShareButton.isChecked());
            }
        });

        compressorText.setText(String.valueOf(prefs.getInt(PreferenceUtils.PREF_COMPRESSOR_INT, 100)));

        compressor.setProgress(prefs.getInt(PreferenceUtils.PREF_COMPRESSOR_INT, 100));
        compressor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                compressorText.setText(String.valueOf(i));
                prefs.edit().putInt(PreferenceUtils.PREF_COMPRESSOR_INT, i).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        shareAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.share_about))
                        .content(getResources().getString(R.string.share_about_sub))
                        .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                        .show();
            }
        });

        compressorAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.compressor_about))
                        .content(getResources().getString(R.string.compressor_about_sub))
                        .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                        .show();
            }
        });

        regexBlocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> regexList = new ArrayList<>();
                ArrayList<Integer> indicesList = new ArrayList<>();
                final List<RegexSaves> regexSaves = DataSupport.findAll(RegexSaves.class);
                for (int i = 0; i < regexSaves.size(); i++) {
                    regexList.add(regexSaves.get(i).getRegex());
                    if (regexSaves.get(i).getEnable()) {
                        indicesList.add(i);
                    }
                }
                final String[] regexArray = regexList.toArray(new String[regexList.size()]);
                Integer[] indicesArray = indicesList.toArray(new Integer[indicesList.size()]);
                new MaterialDialog.Builder(SettingsFragment.this.getContext())
                        .title(R.string.setting_regex_title)
                        .items(regexArray)
                        .itemsCallbackMultiChoice(indicesArray, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                RegexSaves regexSaves1 = new RegexSaves();
                                regexSaves1.setToDefault("isEnable");
                                regexSaves1.updateAll("id >= ?", "0");
                                for (int i = 0; i < which.length; i++) {
                                    RegexSaves regexSaves2 = new RegexSaves();
                                    regexSaves2.setEnable(true);
                                    regexSaves2.update(regexSaves.get(which[i]).getId());
                                }
                                return false;
                            }
                        })
                        .neutralText(R.string.dialog_regex_add)
                        .positiveText(R.string.dialog_confirm)
                        .negativeText(R.string.dialog_regex_delete)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                        .title(R.string.dialog_regex_add)
                                        .input(null, null, new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                RegexSaves regexSaves1 = new RegexSaves();
                                                regexSaves1.setRegex(input.toString());
                                                regexSaves1.save();
                                                regexBlocking.performClick();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                        .title(R.string.dialog_regex_delete)
                                        .items(regexArray)
                                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                                for (int i = 0; i < which.length; i++) {
                                                    Log.e("delete", "onSelection: " + i);
                                                    DataSupport.delete(RegexSaves.class, regexSaves.get(which[i]).getId());
                                                }
                                                return false;
                                            }
                                        })
                                        .positiveText(R.string.dialog_confirm)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .show();
            }
        });

        smsSwitcher.setChecked(prefs.getBoolean(PreferenceUtils.PREF_SMS_ENABLE, false));
        smsSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    prefs.edit().putBoolean(PreferenceUtils.PREF_SMS_ENABLE, false).apply();
                } else {
                    if (ContextCompat.checkSelfPermission(SettingsFragment.this.getContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                        getPermission(Manifest.permission.RECEIVE_SMS);
                    } else {
                        prefs.edit().putBoolean(PreferenceUtils.PREF_SMS_ENABLE, true).apply();
                    }
                }
            }
        });

        smsEnabler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsSwitcher.setChecked(!smsSwitcher.isChecked());
            }
        });

        tipSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_TIPS_ENABLE, true));
        tipSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_TIPS_ENABLE, b).apply();
            }
        });

        tipsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipSwitch.setChecked(!tipSwitch.isChecked());
            }
        });
    }
}
