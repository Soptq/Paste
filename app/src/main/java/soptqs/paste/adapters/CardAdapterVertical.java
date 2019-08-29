package soptqs.paste.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import soptqs.paste.R;
import soptqs.paste.database.CardItem;
import soptqs.paste.floatwindow.FloatWindow;
import soptqs.paste.utils.AppUtils;
import soptqs.paste.utils.PasteToastUtils;
import soptqs.paste.utils.PreferenceUtils;

public class CardAdapterVertical extends RecyclerView.Adapter<CardAdapterVertical.ViewHolder> {

    final static private String PURCHASE_CODE = "0xt1r2y3t2o1p2u3r2c1h2e3s2";

    private List<CardItem> cardItemList;

    private int position;
    private Thread thread;

    public CardAdapterVertical(List<CardItem> cardItems) {
        cardItemList = cardItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ClipboardManager clipboard = (ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardview_vertical, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                position = holder.getAdapterPosition();
                CardItem content = cardItemList.get(position);
                if (!content.isImage()) {
                    if (!prefs.getBoolean(PreferenceUtils.PREF_EDITMODE, false)) {
                        if (!content.getName().equals(PURCHASE_CODE)) {
                            thread = new Thread() {
                                public void run() {
                                    try {
                                        Looper.prepare();
                                        sleep(1000);
                                        Looper.loop();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();
                            ClipData clipData = ClipData.newPlainText(null, content.getContent());
                            try {
                                assert clipboard != null;
                                clipboard.setPrimaryClip(clipData);
                                PasteToastUtils.getPasteToastUtils().ToastShow(parent.getContext(), null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            FloatWindow.hidePopupWindow();
                        }
                    }
                } else {
                    if (content.getContent().contains(",")) {
                        ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(content.getContent().split(",")));
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
                        holder.cardview.getContext().startActivity(Intent.createChooser(shareIntent,
                                holder.cardview.getContext().getResources().getText(R.string.card_menu_share)));
                    } else {
                        String name = content.getContent() + ".png";
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
                        holder.cardview.getContext().startActivity(Intent.createChooser(shareIntent,
                                holder.cardview.getContext().getResources().getText(R.string.card_menu_share)));
                    }
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final CardItem cardItem = cardItemList.get(position);
        holder.content.setText(cardItem.getContent() + "\n");
        holder.appName.setText(cardItem.getName());
        holder.time.setText(AppUtils.getTimeAgo(Long.valueOf(cardItem.getTime()), holder.cardview.getContext()));
        holder.icon.setImageDrawable(cardItem.getImageid());
        holder.icon.setCircleBackgroundColorResource(R.color.colorGreyBG);
        holder.leftGround.setBackgroundColor(cardItem.getTopbg());

//        holder.more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!cardItem.isImage()) {
//                    if (RegexConstants.isTopURL(cardItem.getContent())) {
//                        MaterialPopMenuProvider.urlMaterialPopUpMenu(holder.more,
//                                holder.cardview.getContext(),
//                                cardItemList,
//                                position);
//                    } else {
////                        MaterialPopMenuProvider.normalMaterialPopUpMenu(holder.more,
////                                holder.cardview.getContext(),
////                                cardItemList,
////                                position,
////                                holder.content);
//                    }
//                } else {
//                    MaterialPopMenuProvider.imageMaterialPopUpMenu(holder.more,
//                            holder.cardview.getContext(),
//                            cardItemList,
//                            position);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardview;
        TextView appName;
        TextView time;
        TextView category;
        TextView content;
        ImageView more;
        View leftGround;
        CircleImageView icon;

        public ViewHolder(View view) {
            super(view);
            cardview = view;
            appName = view.findViewById(R.id.card_appname_vertical);
            time = view.findViewById(R.id.card_time_vertical);
            category = view.findViewById(R.id.card_category_vertical);
            content = view.findViewById(R.id.card_content_vertical);
            more = view.findViewById(R.id.card_more_vertical);
            icon = view.findViewById(R.id.card_icon_vertical);
            leftGround = view.findViewById(R.id.card_lg_vertical);
        }
    }
}
