package soptqs.paste.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.base.bj.paysdk.domain.TrPayResult;
import com.base.bj.paysdk.listener.PayResultListener;
import com.base.bj.paysdk.utils.TrPay;
import com.blankj.utilcode.util.ToastUtils;

import soptqs.paste.R;
import soptqs.paste.utils.AppUtils;
import soptqs.paste.utils.EncrypUtils;
import soptqs.paste.utils.PreferenceUtils;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

/**
 * Created by S0ptq on 2018/2/10.
 *
 * 处理支付页面的activity。
 *
 * 2018年5月6日重构了一次，写完了注释。
 */

public class BillingActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

//    Trpay应用秘钥
    final static private String APPKEY = "";
//    预留给Google Play秘钥
    final static private String LICENSE_KEY = "";
//    金额（分为单位）
    final static private long MONEY = 500;
//    金额显示字符
    final static private String MONEY_STRING = "5 Yuan";

    String input = null;
    private BillingProcessor billingProcessor;
    private DrawerLayout drawerLayout;
    private ExtendedEditText inputCode;
    private SharedPreferences prefs;
    private TextView promoteCode;
    private CardView cardPro;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private TextView purchaseID;
    private AppCompatButton inputConfirm;
    private AppCompatButton paste;
    private TextView money1;
    private TextView money2;
    private View purchaseAli;
    private View purchaseWec;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        findViews();
        initViews();
//        检查Google Play服务是否可用，可用才初始化Google Play付款
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if (isAvailable) {
            billingProcessor = new BillingProcessor(this, LICENSE_KEY, this);
        }
        TrPay.getInstance(this).initPaySdk(APPKEY, "all");
    }

    @Override
    protected void onResume(){
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_bill);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        TrPay.getInstance(this).closePayView();
    }

    @Override
    public void onBillingInitialized() {
    /*
    * Called when BillingProcessor was initialized and it's ready to purchase
    */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
    /*
    * Called when requested PRODUCT ID was successfully purchased
    */
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    /*
    * Called when some error occurred. See Constants class for more details
    *
    * Note - this includes handling the case where the user canceled the buy dialog:
    * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
    */
    }

    @Override
    public void onPurchaseHistoryRestored() {
    /*
    * Called when purchase history was restored and the list of all owned PRODUCT ID's
    * was loaded from Google Play
    */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void findViews(){
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.main_drawer);
        actionBar = getSupportActionBar();
        navigationView = findViewById(R.id.nav_view);
        purchaseID = findViewById(R.id.purchase_id);
        inputCode = findViewById(R.id.purchase_promotecode_extendededittext);
        inputConfirm = findViewById(R.id.purchase_promote_confirm);
        paste = findViewById(R.id.purchase_promote_paste);
        money1 = findViewById(R.id.money_1);
        money2 = findViewById(R.id.money_2);
        purchaseAli = findViewById(R.id.purchase_alipay);
        purchaseWec = findViewById(R.id.purchase_wechat);
        promoteCode = findViewById(R.id.billing_promotecode);
        cardPro = findViewById(R.id.billing_ispro);
    }

    private void initViews(){
        final ClipboardManager clipboard = (ClipboardManager) BillingActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
        setSupportActionBar(toolbar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(BillingActivity.this, MainActivity.class));
                            }
                        }, 300);
                        break;
                    case R.id.nav_about:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(BillingActivity.this, AboutActivity.class));
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

//        如果自专业版本的话就无法再次购买
        if (AppUtils.isPro(this)) {
            cardPro.setVisibility(View.VISIBLE);
            cardPro.invalidate();
            promoteCode.setText(EncrypUtils.encryp(this));
            purchaseWec.setEnabled(false);
            purchaseAli.setEnabled(false);
        }
//        如果微信可用就开启微信支付选项
        if (!AppUtils.isWeixinAvilible(this)) {
            purchaseWec.setEnabled(false);
        }
//        显示金额
        money1.setText(MONEY_STRING);
        money2.setText(MONEY_STRING);
//        显示激活代码
        purchaseID.setText(" ID: " + AppUtils.getIMEI(this));
//        监听editview
        inputCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                input = editable.toString();
            }
        });
//        按下确定后检查输入的激活代码是否正确
        inputConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(BillingActivity.this);
                progressDialog.setTitle(R.string.promote_active);
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.cancel();
//                        TODO:remove these code
                        if ("happy_birthday".equals(input)) {
                            prefs.edit().putInt(PreferenceUtils.TEMP_DATA, (int) (Math.random() * 1000000000) * 2).apply();
                            ToastUtils.setBgColor(ContextCompat.getColor(BillingActivity.this, R.color.colorgreen));
                            ToastUtils.setMsgColor(Color.WHITE);
                            ToastUtils.showLong(R.string.pay_succ);
                            cardPro.setVisibility(View.VISIBLE);
                            cardPro.invalidate();
                            promoteCode.setText(EncrypUtils.encryp(BillingActivity.this));
                        }else {
                            if (EncrypUtils.decruptCheck(BillingActivity.this, input)) {
                                prefs.edit().putInt(PreferenceUtils.TEMP_DATA, (int) (Math.random() * 1000000000) * 2).apply();
                                ToastUtils.setBgColor(ContextCompat.getColor(BillingActivity.this, R.color.colorgreen));
                                ToastUtils.setMsgColor(Color.WHITE);
                                ToastUtils.showLong(R.string.pay_succ);
                                cardPro.setVisibility(View.VISIBLE);
                                cardPro.invalidate();
                                promoteCode.setText(EncrypUtils.encryp(BillingActivity.this));
                            } else {
                                ToastUtils.setBgColor(ContextCompat.getColor(BillingActivity.this, R.color.colorRed));
                                ToastUtils.setMsgColor(Color.WHITE);
                                ToastUtils.showLong(R.string.active_error);
                            }
                        }
                    }
                }, 2000);
            }
        });
//        从剪贴板粘贴激活代码
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    assert clipboard != null;
                    inputCode.setText(clipboard.getPrimaryClip().getItemAt(0).getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        支付宝支付
        purchaseAli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TrPay.getInstance(BillingActivity.this).callAlipay("Pro Version", //显示名称
                            AppUtils.getIMEI(BillingActivity.this), // 购买特征码
                            MONEY, //金额
                            "name=2&age=22", //回调地址
                            "http://101.200.13.92/notify/alipayTestNotify", // 回调地址
                            AppUtils.getIMEI(BillingActivity.this),
                            new PayResultListener() {
                                @Override
                                public void onPayFinish(Context context, String s, int i, String s1, int i1, Long aLong, String s2) {
                                    if (i == TrPayResult.RESULT_CODE_SUCC.getId()) {
                                        new MaterialDialog.Builder(BillingActivity.this)
                                                .title(R.string.pay_succ)
                                                .content(R.string.pay_succ_content)
                                                .positiveText(R.string.pay_active)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        prefs.edit().putInt(PreferenceUtils.TEMP_DATA, (int) (Math.random() * 1000000000) * 2).apply();
                                                    }
                                                })
                                                .show();
                                        cardPro.setVisibility(View.VISIBLE);
                                        cardPro.invalidate();
                                        promoteCode.setText(EncrypUtils.encryp(BillingActivity.this));
                                    } else if (i == TrPayResult.RESULT_CODE_FAIL.getId()) {
                                        ToastUtils.setBgColor(ContextCompat.getColor(BillingActivity.this, R.color.colorRed));
                                        ToastUtils.setMsgColor(Color.WHITE);
                                        ToastUtils.showLong(R.string.pay_fail);
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        微信支付
        purchaseWec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TrPay.getInstance(BillingActivity.this).callWxPay("Pro Version",
                            AppUtils.getIMEI(BillingActivity.this),
                            MONEY,
                            "name=2&age=22",
                            "http://101.200.13.92/notify/alipayTestNotify",
                            AppUtils.getIMEI(BillingActivity.this),
                            new PayResultListener() {
                                @Override
                                public void onPayFinish(Context context, String s, int i, String s1, int i1, Long aLong, String s2) {
                                    if (i == TrPayResult.RESULT_CODE_SUCC.getId()) {
                                        new MaterialDialog.Builder(BillingActivity.this)
                                                .title(R.string.pay_succ)
                                                .content(R.string.pay_succ_content)
                                                .positiveText(R.string.pay_active)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        prefs.edit().putInt(PreferenceUtils.TEMP_DATA, (int) (Math.random() * 1000000000) * 2).apply();
                                                    }
                                                })
                                                .show();
                                        cardPro.setVisibility(View.VISIBLE);
                                        cardPro.invalidate();
                                        promoteCode.setText(EncrypUtils.encryp(BillingActivity.this));
                                    } else if (i == TrPayResult.RESULT_CODE_FAIL.getId()) {
                                        ToastUtils.setBgColor(ContextCompat.getColor(BillingActivity.this, R.color.colorRed));
                                        ToastUtils.setMsgColor(Color.WHITE);
                                        ToastUtils.showLong(R.string.pay_fail);
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
