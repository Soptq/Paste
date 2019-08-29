package soptqs.paste.floatwindow;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.blankj.utilcode.util.ToastUtils;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mikepenz.itemanimators.AlphaInAnimator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import org.angmarch.views.NiceSpinner;
import org.litepal.crud.DataSupport;
import soptqs.paste.Paste;
import soptqs.paste.R;
import soptqs.paste.adapters.CardAdapter;
import soptqs.paste.database.CardItem;
import soptqs.paste.database.ClipSaves;
import soptqs.paste.database.DataProcess;
import soptqs.paste.dialog.CardEditDialog;
import soptqs.paste.dialog.CardSearchDialog;
import soptqs.paste.services.RsenAccessibilityService;
import soptqs.paste.snaphelper.GravitySnapHelper;
import soptqs.paste.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FloatWindow {
    private static final String AdmobID = "";
    private static final String LOG_TAG = "FloatWindow";
    private static final String TAG = "FloatWindow";
    public static Boolean isShown = false;
    private static String curPackName = null;
    @SuppressLint("StaticFieldLeak")
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static ArrayList<Integer> access = new ArrayList<>();

    private static int getAccess() {
        return access.get(0);
    }

    public static void showPopupWindow(final Context context) {
        if (isShown) {
            return;
        }
        isShown = true;

        SharedPreferences prefs;
        Context mContext = context.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        if (prefs.getInt(PreferenceUtils.PREF_CARD_PREFER, 0) == 0
                || prefs.getInt(PreferenceUtils.PREF_CARD_PREFER, 0) == 1) {
            mView = setUpView(context);
        } else {
            mView = setUpViewVertical(context);
        }
        final WindowManager.LayoutParams params;
        params = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        params.dimAmount = 0.5f;

        params.flags = flags;
        params.format = PixelFormat.TRANSLUCENT;
        params.alpha = (float) prefs.getInt(PreferenceUtils.PREF_FLOATWINDOW_ALPHA, 100) / 100;

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        params.gravity = Gravity.CENTER | Gravity.BOTTOM;
        params.windowAnimations = R.style.CardAnimation;

        mWindowManager.addView(mView, params);
    }

    public static void hidePopupWindow() {
        if (isShown && null != mView) {
            mWindowManager.removeView(mView);
            isShown = false;
        }

    }

    private static View setUpView(final Context context) {
        final List<CardItem> cardItemList = new ArrayList<>();
        final SharedPreferences prefs;
        cardItemList.clear();
        access.add(0);

        @SuppressLint("InflateParams") final View view = LayoutInflater.from(context).inflate(R.layout.float_window,
                null);
        final View emptyView = view.findViewById(R.id.floatwindow_empty);
        final SwipeMenuRecyclerView recyclerView = view.findViewById(R.id.card_recyclerview);
        final CardAdapter adapter = new CardAdapter(cardItemList);
        final NiceSpinner cardSpinner = view.findViewById(R.id.card_spinner);
        prefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        final ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);

        initView(view, prefs, recyclerView, context, clipboard);

        ArrayAdapter<CharSequence> cardAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.array_card, android.R.layout.simple_spinner_item);
        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardSpinner.setAdapter(cardAdapter);
        cardSpinner.setSelectedIndex(0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setAdapter(adapter);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new AlphaInAnimator());

        SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.START);
        snapHelperStart.attachToRecyclerView(recyclerView);

        if (adapter.getItemCount() != 0) {
            emptyView.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        adapter.notifyItemChanged(msg.arg1);
                        if (adapter.getItemCount() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                        break;
                    default:
                        break;
                }
            }

        };

        final int cardCount = getCardCount(prefs);
        if (cardCount > 20) {
            List<ClipSaves> clipSavesList = DataSupport.order("time desc").limit(20).find(ClipSaves.class);
            loadData(20, context, cardItemList, mHandler, view, clipSavesList, access);
        } else {
            List<ClipSaves> clipSavesList = DataSupport.order("time desc").limit(cardCount).find(ClipSaves.class);
            loadData(cardCount, context, cardItemList, mHandler, view, clipSavesList, access);
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            Boolean isSlidingToLast;
            List<ClipSaves> clipSavesList;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    // 判断是否滚动到底部，并且是向右滚动
                    if (totalItemCount <= 0) return;
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        if (cardSpinner.getSelectedIndex() == 0) {
                            if (totalItemCount + 20 > cardCount) {
                                Log.e(TAG, "num: " + (cardCount - totalItemCount));
                                if (cardCount - totalItemCount >= 0) {
                                    clipSavesList = DataSupport.order("time desc").offset(totalItemCount).limit(cardCount - totalItemCount).find(ClipSaves.class);
                                    loadData(cardCount - totalItemCount, context, cardItemList, mHandler, view, clipSavesList, access);
                                }
                            } else {
                                List<ClipSaves> clipSavesList = DataSupport.order("time desc").offset(adapter.getItemCount()).limit(20).find(ClipSaves.class);
                                loadData(20, context, cardItemList, mHandler, view, clipSavesList, access);
                            }
                        }
                    }
                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                //大于0表示正在向右滚动
                // 小于等于0表示停止或向左滚动
                isSlidingToLast = dx > 0;
            }
        });


        prefs.edit().putBoolean(PreferenceUtils.PREF_EDITMODE, false).apply();
        recyclerView.setItemViewSwipeEnabled(true);
        recyclerView.setLongPressDragEnabled(false);
        recyclerView.setOnItemStateChangedListener(new OnItemStateChangedListener() {
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                int mposition = viewHolder.getAdapterPosition();
                View childView = recyclerView.getLayoutManager().findViewByPosition(mposition);
                if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                    childView.setBackgroundResource(R.drawable.card_sharp);
                } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                    if (childView != null) {
                        childView.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                    }
                }
            }
        });

        recyclerView.setOnItemMoveListener(new OnItemMoveListener() {

            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();
                Collections.swap(cardItemList, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                if (prefs.getBoolean(PreferenceUtils.PREF_EDITMODE, false)) {
//                    DataProcess.moveSaves(cardItemList.get(fromPosition-1).getTime(), cardItemList.get(toPosition-1).getTime());
                    return true;
                } else {
                    adapter.notifyItemChanged(fromPosition);
                }
                return false;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                if (prefs.getBoolean(PreferenceUtils.PREF_EDITMODE, false)) {
                    int position = srcHolder.getAdapterPosition();
                    DataProcess.deleteFromBoard(cardItemList.get(position).getTime(), context);
                    cardItemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (adapter.getItemCount() != 0) {
                        emptyView.setVisibility(View.INVISIBLE);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    FloatWindow.hidePopupWindow();
                    Boolean isfirst = intro(prefs, view.getContext());
                    if (!isfirst) {
                        final int position = srcHolder.getAdapterPosition();
                        if (!cardItemList.get(position).isImage()) {
                            String content = cardItemList.get(position).getContent();
                            if (content.contains("<@PasteResource@>")) {
                                content = content.replace("<@PasteResource@>", "");
                                content = content.replace("<@PasteResourceDivide@>", ",");
                                content = Arrays.asList(content.split(",")).get(0);
                            }
                            ClipData clipData = ClipData.newPlainText(null, content);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clipData);
                            }

                            Intent intent = new Intent("soptq.intent.ACCESSIBILITY_PASTE");
                            context.sendBroadcast(intent);
                        } else {
                            if (cardItemList.get(position).getContent().contains(",")) {
                                ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(cardItemList.get(position).getContent().split(",")));
                                final ArrayList<Uri> uriArrayList = new ArrayList<>();
                                for (String string : Stringlist) {
                                    String name = string + ".png";
                                    File imagePath = new File(context.getFilesDir(), name);
                                    Uri uri = FileProvider.getUriForFile(context,
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
                                context.startActivity(Intent.createChooser(shareIntent,
                                        context.getResources().getText(R.string.card_menu_share)));
                            } else {
                                String name = cardItemList.get(position).getContent() + ".png";
                                File imagePath = new File(context.getFilesDir(), name);
                                Uri uri = FileProvider.getUriForFile(context,
                                        "soptqs.paste.PasteFileProvider",
                                        imagePath);
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                shareIntent.setType("image/*");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                FloatWindow.hidePopupWindow();
                                context.startActivity(Intent.createChooser(shareIntent,
                                        context.getResources().getText(R.string.card_menu_share)));
                            }
                        }
                    }
                }
            }
        });

        cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, final View view, int i, long l) {
                switch (i) {
                    case 0:
                        cardItemList.clear();
                        adapter.notifyDataSetChanged();
                        List<ClipSaves> clipSavesList = DataSupport.order("time desc").limit(20).find(ClipSaves.class);
                        loadData(20, context, cardItemList, mHandler, view, clipSavesList, access);
                        if (adapter.getItemCount() != 0) {
                            emptyView.setVisibility(View.INVISIBLE);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1:
                        cardItemList.clear();
                        adapter.notifyDataSetChanged();
                        clipSavesList = DataSupport.where("isCollection = ?", "1").order("time desc").find(ClipSaves.class);
                        loadData(clipSavesList.size(), context, cardItemList, mHandler, view, clipSavesList, access);
                        if (adapter.getItemCount() != 0) {
                            emptyView.setVisibility(View.INVISIBLE);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    private static View setUpViewVertical(final Context context) {
        return null;
    }

    private static Boolean intro(SharedPreferences preferences, Context context) {
        boolean FirstStart;
        final String START_KEY = "isFirstToStart";
        FirstStart = preferences.getBoolean(START_KEY, true);
        if (FirstStart && AppUtils.isAccessibilitySettingsOn(context)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(START_KEY, false);
            editor.apply();
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(Html.fromHtml(context.getResources().getString(R.string.permission_title2)));
            builder.setMessage(Html.fromHtml(context.getResources().getString(R.string.permission_content2)));
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            final AlertDialog dialog = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            dialog.show();
        }
        return FirstStart;
    }

    private static void loadData(final int cardCount, final Context context, final List<CardItem> cardItemList,
                                 final Handler mHandler, final View view, final List<ClipSaves> clipSavesList, ArrayList<Integer> access) {
        access.set(0, access.get(0) + 1);
        final int key = access.get(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= cardCount - 1; i++) {
                    if (getAccess() > key) {
                        Log.e(TAG, "run: reture");
                        return;
                    }
                    if (i == clipSavesList.size()) return;

                    final ClipSaves clipSaves = clipSavesList.get(i);
                    if (clipSaves.getContent() != null && clipSaves.getContent().length() > 0) {
                        Drawable icon;
                        String name;
                        if (clipSaves.isShared()) {
                            Boolean isImage = false;
                            name = context.getResources().getString(R.string.card_menu_share);
                            icon = context.getResources().getDrawable(R.mipmap.ic_share);
                            Palette palette = PaletteUtils.getPalette(Paste.getContext(), ImageUtils.drawableToBitmap(icon));
                            Palette.Swatch swatch = PaletteUtils.getSwatch(view.getContext(), palette);
                            if (clipSaves.isImage()) {
                                isImage = true;
                            }
                            CardItem item = new CardItem(name, clipSaves.getTime(), icon,
                                    clipSaves.getContent(), swatch.getRgb(), clipSaves.getContent().length(),
                                    clipSaves.isTranslated(), clipSaves.getTranslation(),
                                    clipSaves.getPakageName(), isImage);
                            cardItemList.add(item);
                            try {
                                Message message = new Message();
                                message.arg1 = i;
                                message.what = 0;
                                mHandler.sendMessage(message);
                                if (i <= 5) {
                                    Thread.currentThread().sleep(200);
                                } else {
                                    Thread.currentThread().sleep(100);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(clipSaves.getPakageName(), 0);
                                icon = context.getPackageManager().getApplicationIcon(clipSaves.getPakageName());
                                name = context.getPackageManager().getApplicationLabel(applicationInfo).toString();
                            } catch (PackageManager.NameNotFoundException e) {
                                icon = context.getResources().getDrawable(R.mipmap.ic_hires);
                                name = "UNKNOWN";
                            }
                            Palette palette = PaletteUtils.getPalette(Paste.getContext(), ImageUtils.drawableToBitmap(icon));
                            Palette.Swatch swatch = PaletteUtils.getSwatch(view.getContext(), palette);
                            CardItem item = new CardItem(name, clipSaves.getTime(), icon,
                                    clipSaves.getContent(), swatch.getRgb(),
                                    clipSaves.getContent().length(), clipSaves.isTranslated(),
                                    clipSaves.getTranslation(), clipSaves.getPakageName(),
                                    false);
                            cardItemList.add(item);
                            try {
                                Message message = new Message();
                                message.arg1 = i;
                                message.arg2 = key;
                                message.what = 0;
                                mHandler.sendMessage(message);
                                if (i <= 5) {
                                    Thread.currentThread().sleep(200);
                                } else {
                                    Thread.currentThread().sleep(100);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();

    }

    private static int getCardCount(SharedPreferences prefs) {
        int cardCount;
        if (prefs.getLong(PreferenceUtils.PREF_CARDNUM, 3) > Integer.MAX_VALUE) {
            cardCount = Integer.MAX_VALUE;
        } else {
            cardCount = Integer.parseInt(String.valueOf(prefs.getLong(PreferenceUtils.PREF_CARDNUM, 20)));
        }
        int dataCount = DataSupport.count(ClipSaves.class);
        if (dataCount > cardCount) {
            return cardCount;
        }
        return dataCount;
    }

    private static void initView(final View view, final SharedPreferences prefs, final SwipeMenuRecyclerView recyclerView,
                                 final Context context, final ClipboardManager clipboard) {
        final FloatingActionButton fabAdd = view.findViewById(R.id.float_add);
        Switch cardDelete = view.findViewById(R.id.card_delete);
        final View editmode = view.findViewById(R.id.edit_mode);
        AdView mAdView = view.findViewById(R.id.adView);
        final ImageView lock = view.findViewById(R.id.floatwindow_lock);
        final ImageView append = view.findViewById(R.id.floatwindow_append);
        final ImageView universeCopy = view.findViewById(R.id.floatwindow_universe_copy);
        final ImageView search = view.findViewById(R.id.floatwindow_search);
        FrameLayout topArea = view.findViewById(R.id.floatwindow_top_area);
        ImageView closeWindow = view.findViewById(R.id.floatwindow_close);

        //处理横屏竖屏模式
        if (!AppUtils.isScreenOriatationPortrait(context)) {
            fabricTrack("Horizon");
            topArea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0));
            closeWindow.setVisibility(View.VISIBLE);
            closeWindow.invalidate();
            closeWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hidePopupWindow();
                }
            });
        } else {
            fabricTrack("Vertical");
            topArea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 9));
        }

        fabAdd.hide(false);
        editmode.setVisibility(View.INVISIBLE);
        editmode.setAlpha(0f);

        cardDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_EDITMODE, b).apply();
                if (b) {
                    fabAdd.hide(true);
                    recyclerView.setLongPressDragEnabled(true);
                    editmode.setVisibility(View.VISIBLE);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(editmode, "alpha", 0f, 1f)
                    );
                    animatorSet.setDuration(100);
                    animatorSet.start();
                } else {
                    fabAdd.show(true);
                    recyclerView.setLongPressDragEnabled(false);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(editmode, "alpha", 1f, 0f)
                    );
                    animatorSet.setDuration(100);
                    animatorSet.start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editmode.setVisibility(View.INVISIBLE);
                        }
                    }, 100);
                }
            }
        });

        if (AppUtils.isPro(context) || prefs.getBoolean(PreferenceUtils.PREF_DISABLE_ADS, false)) {
            mAdView.setVisibility(View.GONE);
        } else {
            MobileAds.initialize(context, AdmobID);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        switch (prefs.getInt(PreferenceUtils.PREF_SERVICE_MODE, 2)) {
            case 3:
                lock.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorTeal)));
                break;
            case 4:
                append.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorTeal)));
                break;
            default:
                break;
        }

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                if (prefs.getInt(PreferenceUtils.PREF_SERVICE_MODE, 2) != 3) {
                    lock.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorTeal)));
                    if (prefs.getInt(PreferenceUtils.PREF_SERVICE_MODE, 2) == 4) {
                        append.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorBlack)));
                        ClipData clipData = ClipData.newPlainText(null, prefs.getString(PreferenceUtils.PREF_TEMP_APPEND, ""));
                        assert clipboard != null;
                        clipboard.setPrimaryClip(clipData);
                    }
                    prefs.edit().putInt(PreferenceUtils.PREF_SERVICE_MODE, 3).apply();
                    if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                        String addedText = manager.getPrimaryClip().getItemAt(0).getText().toString();
                        prefs.edit().putString(PreferenceUtils.PREF_LOCK_WHAT, addedText).apply();
                    }
                    ToastUtils.setBgColor(context.getResources().getColor(R.color.colorgreen));
                    ToastUtils.setMsgColor(context.getResources().getColor(R.color.colorWhite));
                    ToastUtils.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 100);
                    ToastUtils.showLong(R.string.lock_noti);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 64);
                        }
                    }, 300);
                } else {
                    lock.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorBlack)));
                    prefs.edit().putInt(PreferenceUtils.PREF_SERVICE_MODE, 2).apply();
                }
            }
        });

        append.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getInt(PreferenceUtils.PREF_SERVICE_MODE, 2) != 4) {
                    append.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorTeal)));
                    if (prefs.getInt(PreferenceUtils.PREF_SERVICE_MODE, 2) == 3) {
                        lock.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorBlack)));
                    }
                    prefs.edit().putInt(PreferenceUtils.PREF_SERVICE_MODE, 4).apply();
                    ToastUtils.setBgColor(context.getResources().getColor(R.color.colorgreen));
                    ToastUtils.setMsgColor(context.getResources().getColor(R.color.colorWhite));
                    ToastUtils.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 100);
                    ToastUtils.showLong(R.string.append_noti);
                    FloatWindow.hidePopupWindow();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 64);
                        }
                    }, 300);
                } else {
                    append.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorBlack)));
                    prefs.edit().putInt(PreferenceUtils.PREF_SERVICE_MODE, 2).apply();
                    ClipData clipData = ClipData.newPlainText(null, prefs.getString(PreferenceUtils.PREF_TEMP_APPEND, ""));
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clipData);
                    PasteToastUtils.getPasteToastUtils().ToastShow(context, null);
                    FloatWindow.hidePopupWindow();

                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatWindow.hidePopupWindow();
                Intent intent1 = new Intent(view.getContext(), CardSearchDialog.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent1);
            }
        });

        if (!AppUtils.isAccessibilitySettingsOn(context)) {
            universeCopy.setVisibility(View.GONE);
        } else {
            universeCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FloatWindow.hidePopupWindow();
                    Intent intent = new Intent("soptq.intent.UNIVERSE_COPY");
                    context.sendBroadcast(intent);
                }
            });
        }

        view.setFocusableInTouchMode(true);

        // 点击窗口外部区域可消除
        final View popupWindowView = view.findViewById(R.id.popup_window);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setOnTouchListener(new View.OnTouchListener() {

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        Rect rect = new Rect();
                        popupWindowView.getGlobalVisibleRect(rect);
                        if (!rect.contains(x, y)) {
                            FloatWindow.hidePopupWindow();
                        }
                        return false;
                    }
                });
            }
        }, 1000);

        // 点击back键可消除
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("Keycode", "onKey: " + keyCode);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        FloatWindow.hidePopupWindow();
                        return true;
                }
                try {
                    if (!curPackName.equals(RsenAccessibilityService.foregroundPackageName())) {
                        FloatWindow.hidePopupWindow();
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        //搜索事件
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabAdd.show(true);
                fabAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FloatWindow.hidePopupWindow();
                        Intent intent = new Intent(context, CardEditDialog.class);
                        intent.putExtra("category", 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }
        }, 1000);
    }

    private static void fabricTrack(String orien) {
        // TODO: Use your own attributes to track content views in your app
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("FloatWindow")
                .putContentType("Activity")
                .putContentId("0001")
                .putCustomAttribute("Screen Orientation", orien));
    }

}
