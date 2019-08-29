package soptqs.paste.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.blankj.utilcode.util.ToastUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.sumimakito.awesomeqr.AwesomeQRCode;
import com.jayway.jsonpath.JsonPath;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import org.litepal.crud.DataSupport;
import soptqs.paste.R;
import soptqs.paste.constants.RegexConstants;
import soptqs.paste.database.CardItem;
import soptqs.paste.database.ClipSaves;
import soptqs.paste.database.DataProcess;
import soptqs.paste.database.PhoneItem;
import soptqs.paste.floatwindow.FloatWindow;
import soptqs.paste.kotlin.MaterialPopMenuProvider;
import soptqs.paste.utils.*;
import soptqs.paste.utils.bilibili.BilibiliPicture;
import soptqs.paste.utils.bilibili.BilibiliTitle;
import soptqs.paste.utils.express.CheckExpressID;
import soptqs.paste.utils.express.ExpressTrack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by S0ptq on 2018/2/7.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    final static private String PURCHASE_CODE = "0xt1r2y3t2o1p2u3r2c1h2e3s2";
    final static private int CARD_1 = 0;
    final static private int CARD_2 = 1;

    private List<CardItem> carditem;
    private int position;
    private Thread thread;
    private Bitmap bitmap = null;

    public CardAdapter(List<CardItem> itemList){
        carditem = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        final View view;
        final ClipboardManager clipboard =(ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        if (prefs.getInt(PreferenceUtils.PREF_CARD_PREFER, 0) == CARD_1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cardview, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cardview_no_access, parent, false);
        }
        final ViewHolder holder = new ViewHolder(view);

        /**
         * 卡片单击事件
         */

        holder.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = holder.getAdapterPosition();
                List<ClipSaves> clipSaves = DataSupport.where("time = ?", carditem.get(position).getTime()).find(ClipSaves.class);
                if (clipSaves.size() != 0) {
                    if (!carditem.get(position).isImage()) {
                        if (clipSaves.get(0).getContent().contains("<@PasteResource@>")) return;

                        if (RegexConstants.isTopURL(carditem.get(position).getContent())) {
                            MaterialPopMenuProvider.urlMaterialPopUpMenu(holder.itemCard,
                                    parent.getContext(),
                                    clipSaves.get(0)
                            );
                        } else {
                            MaterialPopMenuProvider.normalMaterialPopUpMenu(holder.itemCard,
                                    parent.getContext(),
                                    holder.phoneRec,
                                    clipSaves.get(0)
                            );

                        }
                    } else {
                        MaterialPopMenuProvider.imageMaterialPopUpMenu(holder.itemCard,
                                parent.getContext(),
                                clipSaves.get(0)
                        );
                    }
                }

            }
        });

        /**
         * 卡片长按事件
         */

        holder.itemCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                position = holder.getAdapterPosition();
                CardItem card = carditem.get(position);
                String content = card.getContent();
                if (!card.isImage()) {
                    if (!prefs.getBoolean(PreferenceUtils.PREF_EDITMODE, false)) {
                        if (content.contains("<@PasteResource@>")) {
                            final Boolean tipsEnabled = prefs.getBoolean(PreferenceUtils.PREF_TIPS_ENABLE, true);
                            prefs.edit().putBoolean(PreferenceUtils.PREF_TIPS_ENABLE, false).apply();
                            content = content.replace("<@PasteResource@>", "");
                            content = content.replace("<@PasteResourceDivide@>", ",");
                            content = Arrays.asList(content.split(",")).get(0);
                            final String tempContent = content;
                            ClipData clipData = ClipData.newPlainText(null, content);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clipData);
                                PasteToastUtils.getPasteToastUtils().ToastShow(parent.getContext(), null);
                            }
                            FloatWindow.hidePopupWindow();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DataProcess.deleteFromBoardbyContent(tempContent, parent.getContext());
                                    prefs.edit().putBoolean(PreferenceUtils.PREF_TIPS_ENABLE, tipsEnabled).apply();
                                }
                            }, 500);
                        } else {
                            ClipData clipData = ClipData.newPlainText(null, content);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clipData);
                                PasteToastUtils.getPasteToastUtils().ToastShow(parent.getContext(), null);
                            }
                            FloatWindow.hidePopupWindow();
                        }
                    }
                } else {
                    if (content.contains(",")) {
                        ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(content.split(",")));
                        final ArrayList<Uri> uriArrayList = new ArrayList<>();
                        for (String string : Stringlist) {
                            String name = string + ".png";
                            File imagePath = new File(parent.getContext().getFilesDir(), name);
                            Uri uri = FileProvider.getUriForFile(parent.getContext(),
                                    "soptqs.paste.PasteFileProvider",
                                    imagePath);
                            uriArrayList.add(uri);
                        }
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        FloatWindow.hidePopupWindow();
                        holder.card.getContext().startActivity(Intent.createChooser(shareIntent,
                                holder.card.getContext().getResources().getText(R.string.card_menu_share)));
                    } else {
                        String name = content + ".png";
                        File imagePath = new File(parent.getContext().getFilesDir(), name);
                        Uri uri = FileProvider.getUriForFile(parent.getContext(),
                                "soptqs.paste.PasteFileProvider",
                                imagePath);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        FloatWindow.hidePopupWindow();
                        holder.card.getContext().startActivity(Intent.createChooser(shareIntent,
                                holder.card.getContext().getResources().getText(R.string.card_menu_share)));
                    }
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CardItem card = carditem.get(position);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(holder.card.getContext());
        resetView(holder.card.getContext(), holder);
        setContentEllipsize(card, holder);
        holder.name.setText(card.getName());
        holder.time.setText(AppUtils.getTimeAgo(Long.valueOf(card.getTime()), holder.card.getContext()));
        holder.name.setTextColor(PaletteUtils.getTextColor(card.getTopbg()));
        holder.time.setTextColor(PaletteUtils.getTextColor(card.getTopbg()));
        holder.category.setTextColor(PaletteUtils.getTextColor(card.getTopbg()));
        holder.icon.setBorderColor(holder.card.getContext().getResources().getColor(R.color.colorGreyBG));
        holder.icon.setCircleBackgroundColorResource(R.color.colorGreyBG);
        holder.icon.setImageDrawable(card.getImageid());
        holder.topbg.setBackgroundColor(card.getTopbg());
        if (!card.isImage()) {
            switch (RecognitionUtils.isWhat(card.getContent())) {
                case 0: // default
                    holder.category.setText(holder.card.getContext().getResources().getString(R.string.category_word));
                    holder.content.setText(card.getContent());
                    holder.content.setTypeface(Typeface.SANS_SERIF);
                    holder.content.setTextSize(14);
                    holder.content.setGravity(Gravity.TOP);
                    holder.content.setTextColor(Color.parseColor("#757575"));
                    holder.words.setTextColor(Color.parseColor("#757575"));
                    holder.bottombg.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    break;
                case 1: // color
                    String category = holder.card.getContext().getResources().getString(R.string.category_color);
                    holder.category.setText(category);
                    holder.content.setText(card.getContent());
                    holder.content.setTypeface(Typeface.SERIF);
                    holder.content.setTextSize(24);
                    holder.content.setGravity(Gravity.CENTER);
                    holder.content.setTextColor(PaletteUtils.getTextColor(Color.parseColor(card.getContent())));
                    holder.words.setTextColor(PaletteUtils.getTextColor(Color.parseColor(card.getContent())));
                    holder.bottombg.setBackgroundColor(Color.parseColor(card.getContent()));
                    holder.words.setBackgroundColor(Color.parseColor(card.getContent()));
                    break;
                case 2: // phone
                    final Handler handler = new Handler();
                    holder.category.setText(R.string.category_phone);
                    holder.content.setText(card.getContent());
                    holder.phoneRec.setVisibility(View.VISIBLE);
                    holder.phoneRec.invalidate();
                    holder.content.setTextSize(18);
                    holder.content.setGravity(Gravity.TOP);
                    holder.content.setTextColor(Color.parseColor("#757575"));
                    holder.words.setTextColor(Color.parseColor("#757575"));
                    holder.bottombg.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final PhoneItem phoneItem = RecognitionUtils.phoneRecog(card.getContent());
                            handler.post(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    try {
                                        if (phoneItem == null) return;
                                        holder.phoneRec.setText(phoneItem.getProvince() + " " + phoneItem.getCity() + " " + phoneItem.getSp());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }).start();
                    break;
                default:
                    break;
            }

            if (card.getContent().contains("<@PasteResource@>")) {
                doIfSMSCode(card.getContent(), holder);
            }
            if (card.isTranslated()) {
                doIfTranslated(card, holder);
            }
            if (RegexConstants.isTopURL(card.getContent())) {
                doInTopURL(prefs, card, holder);
            }
            if (RegexConstants.isExpress(card.getContent()) && !RegexConstants.isMobileSimple(card.getContent()) && !RegexConstants.isBilibiliAV(card.getContent())) {
                doInExpress(card, holder);
            }
            if (RegexConstants.isBilibiliAV(card.getContent())) {
                doInBilibili(card, holder);
            }

            String textsum = card.getWords() + holder.card.getContext().getResources().getString(R.string.word);
            holder.words.setText(textsum);
        } else {
            holder.content.setVisibility(View.GONE);
            holder.imageViewGroup.setVisibility(View.VISIBLE);
            holder.imageViewGroup.invalidate();
            holder.category.setText(holder.card.getContext().getResources().getText(R.string.image_word_single));
            holder.words.setBackgroundColor(holder.card.getContext().getResources().getColor(R.color.colorWhite));
            holder.words.bringToFront();
            holder.bottombg.setBackgroundColor(Color.parseColor("#FFFFFF"));
            doIfImage(card, holder);
        }
    }

    @Override
    public int getItemCount() {
        return carditem.size();
    }

    private void resetView(Context context, ViewHolder holder) {
        holder.icon.setVisibility(View.VISIBLE);
        holder.category.setVisibility(View.VISIBLE);
        holder.words.setVisibility(View.VISIBLE);
        holder.words.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        //提示文字
        holder.phoneRec.setVisibility(View.GONE);
        holder.phoneRec.setSingleLine(false);
        holder.phoneRec.setEllipsize(null);
        holder.phoneRec.setText("");
        //fab 按钮
        holder.share.setVisibility(View.GONE);
        holder.check.setVisibility(View.GONE);
        holder.wrong.setVisibility(View.GONE);
        //文字内容
        holder.content.setVisibility(View.VISIBLE);
        holder.content.invalidate();
        holder.content.setText("");
        //图片展示
        holder.imageViewGroup.removeAllViews();
        holder.imageViewGroup.setVisibility(View.GONE);
    }

    private void setContentEllipsize(CardItem card, final ViewHolder holder) {
        if (!card.isImage()) {
            ViewTreeObserver observer = holder.content.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = holder.content.getHeight() - holder.words.getHeight();
                    int maxLines = height / holder.content.getLineHeight();
                    holder.content.setMaxLines(maxLines - 1);
                    holder.content.setEllipsize(TextUtils.TruncateAt.END);
                    holder.content.getViewTreeObserver().removeGlobalOnLayoutListener(
                            this);
                }
            });
        }
    }

    private void doIfSMSCode(String code, ViewHolder holder) {
        code = code.replace("<@PasteResource@>", "");
        code = code.replace("<@PasteResourceDivide@>", ",");
        List<String> smsInfo = Arrays.asList(code.split(","));
        String smsContent = smsInfo.get(0);
        String smsSender = "";
        if (smsInfo.size() == 2)
            smsSender = smsInfo.get(1);
        String content = String.format(holder.card.getContext().getResources().getText(R.string.sms_content).toString(),
                "<h3>",
                smsContent,
                "</h3>",
                "<h3>",
                smsSender,
                "</h3>");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.content.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.content.setText(Html.fromHtml(content));
        }

    }

    private void doIfTranslated(CardItem card, ViewHolder holder) {
        holder.phoneRec.setVisibility(View.VISIBLE);
        holder.phoneRec.invalidate();
        holder.phoneRec.setText(card.getTranslation());
    }

    private void doInBilibili(final CardItem card, final ViewHolder holder) {
        final Handler uiHandler = new Handler(Looper.getMainLooper());
        holder.share.setVisibility(View.VISIBLE);
        holder.share.invalidate();
        holder.share.setImageDrawable(holder.card.getContext().getResources().getDrawable(R.mipmap.bilibili));
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                String url = "https://www.bilibili.com/video/" + card.getContent();
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                try {
                    holder.card.getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.setBgColor(ContextCompat.getColor(holder.card.getContext(), R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.webview_error);
                }
                FloatWindow.hidePopupWindow();
            }
        });
        try {
            final Handler handler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String title = BilibiliTitle.main(card.getContent());
                    Log.e("title", "run: " + title);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.phoneRec.setVisibility(View.VISIBLE);
                            holder.phoneRec.invalidate();
                            holder.phoneRec.setSingleLine(true);
                            holder.phoneRec.setEllipsize(TextUtils.TruncateAt.END);
                            holder.phoneRec.setText(title);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (NetHelper.isNetworkConnected(holder.card.getContext())) {
            holder.imageViewGroup.setVisibility(View.VISIBLE);
            holder.imageViewGroup.invalidate();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String urlString = BilibiliPicture.getBilibiliPic(card.getContent());
                    final ImageView imageView = new ImageView(holder.card.getContext());
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(24, 24, 24, 24);
                    layoutParams.gravity = Gravity.CENTER;
                    imageView.setLayoutParams(layoutParams);
                    imageView.setPadding(24, 24, 24, 24);
                    imageView.setElevation(10);
                    imageView.setBackgroundColor(holder.card.getContext().getResources().getColor(R.color.colorGreyBG));
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(urlString)
                                    .resize(600, 375)
                                    .centerInside()
                                    .into(imageView);
                            holder.imageViewGroup.addView(imageView);
                            holder.share.bringToFront();
                        }
                    });
                }
            }).start();
        }
    }

    private void doInTopURL(final SharedPreferences prefs, final CardItem card, final ViewHolder holder) {
        holder.share.setVisibility(View.VISIBLE);
        holder.share.invalidate();
        holder.share.setImageDrawable(holder.card.getContext().getResources().getDrawable(R.drawable.ic_share_black_24dp));

        try {
            String mainurl = holder.card.getContext().getResources().getString(R.string.main_url_pre) + "<b>" + RegexConstants.getMainUrl(card.getContent()) + "</b>";
            holder.phoneRec.setVisibility(View.VISIBLE);
            holder.phoneRec.invalidate();
            holder.phoneRec.setText(Html.fromHtml(mainurl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.share.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(final View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(holder.card.getContext())) {
                    String tryShorturl = RecognitionUtils.getShortURL(card.getContent());
                    if (tryShorturl == null) tryShorturl = card.getContent();
                    final String shorturl = tryShorturl;
                    final Handler handler = new Handler();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(holder.card.getContext());
                    LayoutInflater inflater = (LayoutInflater) holder.card.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    @SuppressLint("InflateParams") final View layout = inflater.inflate(R.layout.dialog_share, null);
                    final TextView surl = layout.findViewById(R.id.share_surl);
                    final ImageView qr = layout.findViewById(R.id.share_qr);
                    builder.setView(layout);
                    builder.setTitle(R.string.share_dialog_title);
                    builder.setPositiveButton(R.string.share_share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final ClipboardManager clipboard = (ClipboardManager) holder.card.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            try {
                                ClipData clipData = ClipData.newPlainText(null, shorturl);
                                assert clipboard != null;
                                clipboard.setPrimaryClip(clipData);
                                PasteToastUtils.getPasteToastUtils().ToastShow(holder.card.getContext(), null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        surl.setText(holder.card.getContext().getResources().getString(R.string.short_url_pre) + shorturl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new AwesomeQRCode.Renderer()
                            .contents(shorturl)
                            .size(500).margin(28)
                            .dotScale(0.5f)
                            .roundedDots(prefs.getBoolean(PreferenceUtils.PREF_QR_DOT, true))
                            .background(ImageUtils.centerSquareScaleBitmap(ImageUtils.getQrBg(holder.card.getContext())))
                            .renderAsync(new AwesomeQRCode.Callback() {
                                @Override
                                public void onRendered(AwesomeQRCode.Renderer renderer, final Bitmap bitmap) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            FloatWindow.hidePopupWindow();
                                            qr.setImageBitmap(bitmap);
                                            builder.setNegativeButton(R.string.share_save, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    ImageUtils.storeImage(bitmap, holder.card.getContext());
                                                }
                                            });
                                            final AlertDialog dialog = builder.create();
                                            try {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                                } else
                                                    dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                                                dialog.show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(AwesomeQRCode.Renderer renderer, Exception e) {
                                    FloatWindow.hidePopupWindow();
                                    e.printStackTrace();
                                }
                            });
                } else {
                    ToastUtils.setBgColor(ContextCompat.getColor(holder.card.getContext(), R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.cant_draw_overlay);
                }
            }
        });

    }

    private void doInExpress(final CardItem card, final ViewHolder holder) {
        DataProcess.createExpressSaves(card.getTime());
        int status = DataProcess.isExpressTracking(card.getTime());
        holder.check.setVisibility(View.VISIBLE);
        holder.wrong.setVisibility(View.VISIBLE);
        holder.phoneRec.setVisibility(View.VISIBLE);
        holder.check.invalidate();
        holder.wrong.invalidate();
        holder.phoneRec.invalidate();
        holder.phoneRec.setText(R.string.express1);
        final Handler handler = new Handler();
        //如果不需要查询
        if (status == -1) {
            holder.check.hide(false);
            holder.wrong.hide(false);
            holder.phoneRec.setVisibility(View.GONE);
            return;
        }
        //询问是否需要查询
        if (status == 0) {
            holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.check.hide(true);
                    holder.wrong.hide(true);
                    DataProcess.changeExpressStatus(card.getTime(), true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String expreeID;
                            try {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.phoneRec.setText(R.string.express3);
                                    }
                                });
                                CheckExpressID api = new CheckExpressID();
                                String data = api.getOrderTracesByJson(card.getContent());
                                expreeID = JsonPath.parse(data).read("$.Shippers[0].ShipperCode");
                                final String expreeName = JsonPath.parse(data).read("$.Shippers[0].ShipperName");
                                if (expreeID != null) {
                                    ExpressTrack track = new ExpressTrack();
                                    try {
                                        final String result = track.getOrderTracesByJson(expreeID, card.getContent());
                                        handler.post(new Runnable() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void run() {
                                                if ("none".equals(result)) {
                                                    holder.phoneRec.setText(R.string.express4);
                                                } else {
                                                    String expressData = "<b>" + expreeName + "</b> " + result;
                                                    holder.phoneRec.setText(Html.fromHtml(expressData));
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.phoneRec.setText(R.string.express2);
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.phoneRec.setText(R.string.express2);
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });
            holder.wrong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.check.hide(true);
                    holder.wrong.hide(true);
                    DataProcess.changeExpressStatus(card.getTime(), false);
                    holder.phoneRec.setVisibility(View.GONE);
                }
            });
        }
        //需要查询
        if (status == 1) {
            holder.check.hide(false);
            holder.wrong.hide(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String expreeID;
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.phoneRec.setText(R.string.express3);
                            }
                        });
                        CheckExpressID api = new CheckExpressID();
                        String data = api.getOrderTracesByJson(card.getContent());
                        expreeID = JsonPath.parse(data).read("$.Shippers[0].ShipperCode");
                        final String expreeName = JsonPath.parse(data).read("$.Shippers[0].ShipperName");
                        if (expreeID != null) {
                            ExpressTrack track = new ExpressTrack();
                            try {
                                final String result = track.getOrderTracesByJson(expreeID, card.getContent());
                                handler.post(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        if ("none".equals(result)) {
                                            holder.phoneRec.setText(R.string.express4);
                                        } else {
                                            String expressData = "<b>" + expreeName + "</b> " + result;
                                            holder.phoneRec.setText(Html.fromHtml(expressData));
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.phoneRec.setText(R.string.express2);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.phoneRec.setText(R.string.express2);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void doIfImage(CardItem card, ViewHolder holder) {
        long size = 0;
        if (card.getContent().contains(",")) {
            ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(card.getContent().split(",")));
            if (Stringlist.size() <= 3) {
                ImageView[] imageViews = new ImageView[Stringlist.size()];
                for (int i = 0; i < imageViews.length; i++) {
                    ImageView imageView = new ImageView(holder.card.getContext());
                    imageView = setImageView(imageView, i, holder.card.getContext(), imageViews.length);
                    @SuppressLint("SdCardPath") String path = "/data/data/soptqs.paste/files/" + Stringlist.get(i) + ".png";
                    File expressImage = new File(path);
                    bitmap = BitmapFactory.decodeFile(path, ImageUtils.getBitmapOption(2));
                    if (bitmap != null) {
                        int height = bitmap.getHeight();
                        int width = bitmap.getWidth();
                        bitmap.recycle();
                        float hw = (float) height / (float) width;
                        if (hw > 3 / 4) {
                            height = 600;
                            width = Math.round(height / hw);
                        } else {
                            width = 800;
                            height = Math.round(width * hw);
                        }
                        holder.imageViewGroup.addView(imageView);
                        Picasso.get()
                                .load(expressImage)
                                .resize(width, height)
                                .centerInside()
                                .placeholder(R.drawable.ic_placeholder)
                                .into(imageView);

                        try {
                            size = AppUtils.getFileSize(expressImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                ImageView[] imageViews = new ImageView[3];
                for (int i = 0; i < imageViews.length; i++) {
                    ImageView imageView = new ImageView(holder.card.getContext());
                    imageView = setImageView(imageView, i, holder.card.getContext(), imageViews.length);
                    @SuppressLint("SdCardPath") String path = "/data/data/soptqs.paste/files/" + Stringlist.get(i) + ".png";
                    File expressImage = new File(path);
                    bitmap = BitmapFactory.decodeFile(path, ImageUtils.getBitmapOption(2));
                    if (bitmap != null) {
                        int height = bitmap.getHeight();
                        int width = bitmap.getWidth();
                        bitmap.recycle();
                        float hw = (float) height / (float) width;
                        if (hw > 3 / 4) {
                            height = 600;
                            width = Math.round(height / hw);
                        } else {
                            width = 800;
                            height = Math.round(width * hw);
                        }
                        holder.imageViewGroup.addView(imageView);
                        Picasso.get()
                                .load(expressImage)
                                .resize(width, height)
                                .centerInside()
                                .placeholder(R.drawable.ic_placeholder)
                                .into(imageView);

                        try {
                            size = AppUtils.getFileSize(expressImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            String textsum = Stringlist.size() + " " + holder.card.getContext().getResources().getString(R.string.image_word_double) + "  " + AppUtils.byte2size(size);
            holder.words.setText(textsum);
        } else {
            @SuppressLint("SdCardPath") String path = "/data/data/soptqs.paste/files/" + card.getContent() + ".png";
            File expressImage = new File(path);
            ImageView imageView = new ImageView(holder.card.getContext());
            imageView = setImageView(imageView, 1, holder.card.getContext(), 1);
            bitmap = BitmapFactory.decodeFile(path, ImageUtils.getBitmapOption(2));
            if (bitmap != null) {
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                bitmap.recycle();
                float hw = (float) height / (float) width;
                if (hw > 3 / 4) {
                    height = 600;
                    width = Math.round(height / hw);
                } else {
                    width = 800;
                    height = Math.round(width * hw);
                }
                holder.imageViewGroup.addView(imageView);
                Picasso.get()
                        .load(expressImage)
                        .resize(width, height)
                        .centerInside()
                        .placeholder(R.drawable.ic_placeholder)
                        .into(imageView);
                try {
                    size = AppUtils.getFileSize(expressImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String textsum = "1 " + holder.card.getContext().getResources().getString(R.string.image_word_single) + "  " + AppUtils.byte2size(size);
            holder.words.setText(textsum);
        }
    }

    private ImageView setImageView(ImageView imageView, int i, Context context, int size) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(24, 24, 24, 24);
        layoutParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(layoutParams);
        imageView.setPadding(24, 24, 24, 24);
        imageView.setElevation((10 / size) * (i + 1));
        imageView.setBackgroundColor(context.getResources().getColor(R.color.colorGreyBG));
        return imageView;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View card;
        CardView itemCard;
        TextView name;
        TextView time;
        CircleImageView icon;
        TextView content;
        View topbg;
        View bottombg;
        TextView words;
        TextView category;
        TextView phoneRec;
        FloatingActionButton share;
        FloatingActionButton check;
        FloatingActionButton wrong;
        ViewGroup imageViewGroup;

        public ViewHolder(View view) {
            super(view);
            card = view;
            itemCard = view.findViewById(R.id.item_card);
            name = view.findViewById(R.id.card_name);
            time = view.findViewById(R.id.card_time);
            icon = view.findViewById(R.id.card_image);
            content = view.findViewById(R.id.card_content);
            topbg = view.findViewById(R.id.card_topbg);
            bottombg = view.findViewById(R.id.card_bottombg);
            words = view.findViewById(R.id.card_wordsnum);
            category = view.findViewById(R.id.card_category);
            phoneRec = view.findViewById(R.id.card_phoneRec);
            share = view.findViewById(R.id.card_share);
            check = view.findViewById(R.id.express_ok);
            wrong = view.findViewById(R.id.express_wrong);
            imageViewGroup = view.findViewById(R.id.imageGroup);
        }
    }

}
