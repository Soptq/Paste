package soptqs.paste.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.yhao.floatwindow.FloatWindow;
import soptqs.paste.R;
import soptqs.paste.activities.MainActivity;
import soptqs.paste.services.FloatButtonOrientationService;
import soptqs.paste.utils.PreferenceUtils;

/**
 * Created by S0ptq on 2018/2/6.
 */

public class EntranceFragment extends BaseFragment {

    private SwitchCompat method0Button;
    private View method3Card;
    private View method4Card;
    private TextView alphaText;
    private TextView sizeText;
    private View method0Card;
    private SwitchCompat method1Button;
    private AppCompatSeekBar alphaSet;
    private AppCompatSeekBar sizeSet;


    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        findViews(view);
        initViews();
        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.fragment_2);
    }

    private void findViews(View view){
        method0Button = view.findViewById(R.id.entrance_method0_button);
        method0Card = view.findViewById(R.id.entrance_method0_view);
        method1Button = view.findViewById(R.id.entrance_method1_button);
        method3Card = view.findViewById(R.id.entrance_method3_card);
        method4Card = view.findViewById(R.id.entrance_method4_card);
        alphaText = view.findViewById(R.id.entrance_setting_alpha_textview);
        alphaSet = view.findViewById(R.id.entrance_setting_alpha_seekbar);
        sizeText = view.findViewById(R.id.entrance_setting_size_textview);
        sizeSet = view.findViewById(R.id.entrance_setting_size_seekbar);
    }

    private void initViews(){

        method0Button.setChecked(prefs.getBoolean(PreferenceUtils.PREF_FLOAT_BUTTON, false));
        method1Button.setChecked(true);
        method1Button.setEnabled(false);

        method0Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_FLOAT_BUTTON, b).apply();
                if (b) {
                    method0Button.setEnabled(false);
                    new MaterialDialog.Builder(EntranceFragment.this.getContext())
                            .title(getResources().getString(R.string.restart_content))
                            .content(getResources().getString(R.string.restart_to_aplay))
                            .positiveText(Html.fromHtml(getResources().getString(R.string.dialog_confirm)))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent mStartActivity = new Intent(EntranceFragment.this.getContext(), MainActivity.class);
                                    int mPendingIntentId = 123456;
                                    PendingIntent mPendingIntent = PendingIntent.getActivity(EntranceFragment.this.getContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager mgr = (AlarmManager) EntranceFragment.this.getContext().getSystemService(Context.ALARM_SERVICE);
                                    assert mgr != null;
                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                    ToastUtils.setBgColor(ContextCompat.getColor(EntranceFragment.this.getContext(), R.color.colorgreen));
                                    ToastUtils.setMsgColor(Color.WHITE);
                                    ToastUtils.showLong(R.string.restarting);
                                    System.exit(0);
                                }
                            })
                            .show();
                } else {
                    try {
                        FloatWindow.destroy();
                        EntranceFragment.this.getContext().stopService(new Intent(EntranceFragment.this.getContext(),
                                FloatButtonOrientationService.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        method0Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                method0Button.setChecked(!method0Button.isChecked());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    method3Card.setVisibility(View.VISIBLE);
                    method3Card.invalidate();
                    method3Card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtils.setBgColor(ContextCompat.getColor(EntranceFragment.this.getContext(), R.color.colorRed));
                                ToastUtils.setMsgColor(Color.WHITE);
                                ToastUtils.showLong(R.string.no_voice_input);
                            }
                        }
                    });
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    method4Card.setVisibility(View.VISIBLE);
                    method4Card.invalidate();
                    method4Card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivity(intent);
                        }
                    });
                }
            }
        }, 1000);

        alphaText.setText(String.valueOf(prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_ALPHA, 100)));
        alphaText.setAlpha((float) prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_ALPHA, 100) / 100);

        alphaSet.setProgress(prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_ALPHA, 100));
        alphaSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alphaText.setText(String.valueOf(i));
                alphaText.setAlpha((float) prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_ALPHA, 100) / 100);
                prefs.edit().putInt(PreferenceUtils.PREF_FLOATBUTTON_ALPHA, i).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(), R.string.restart_to_aplay, Toast.LENGTH_SHORT).show();
            }
        });

        sizeText.setText(String.valueOf(prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_SIZE, 50)));

        sizeSet.setProgress(prefs.getInt(PreferenceUtils.PREF_FLOATBUTTON_SIZE, 50));
        sizeSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sizeText.setText(String.valueOf(i));
                prefs.edit().putInt(PreferenceUtils.PREF_FLOATBUTTON_SIZE, i).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(), R.string.restart_to_aplay, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
