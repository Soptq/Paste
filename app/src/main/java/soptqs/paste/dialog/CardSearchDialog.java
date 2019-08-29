package soptqs.paste.dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mikepenz.itemanimators.AlphaInAnimator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import soptqs.paste.R;
import soptqs.paste.adapters.SearchAdapter;
import soptqs.paste.database.CardItem;
import soptqs.paste.database.ClipSaves;
import soptqs.paste.database.DataProcess;
import soptqs.paste.utils.PasteToastUtils;

/**
 * Created by S0ptq on 2018/2/20.
 */

public class CardSearchDialog extends Activity {

    EditText searchContent;
    SwipeMenuRecyclerView recyclerView;
    String searchText;
    String name;

    List<CardItem> cardItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.dialog_card_search);
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        final View emptyView = findViewById(R.id.search_empty);
        searchContent = findViewById(R.id.card_search_edit);
        recyclerView = findViewById(R.id.card_search);

        String keyword = "%" + "" + "%";
        List<ClipSaves> clipSavesList1 = DataSupport.order("time desc").where("content like ?", keyword).find(ClipSaves.class);
        for (int i1 = 0; i1 <= clipSavesList1.size() - 1; i1++) {
            ClipSaves clipSaves = clipSavesList1.get(i1);
            if (clipSaves.getContent() != null && clipSaves.getContent().length() > 0) {
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(clipSaves.getPakageName(), 0);
                    name = getPackageManager().getApplicationLabel(applicationInfo).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    name = "UNKNOWN";
                    e.printStackTrace();
                }
                CardItem item = new CardItem(name, clipSaves.getTime(), null,
                        clipSaves.getContent(), 0, clipSaves.getContent().length(),
                        false, null, null, false);
                cardItemList.add(item);
            }
        }

        final SearchAdapter adapter = new SearchAdapter(cardItemList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new AlphaInAnimator());
        if (adapter.getItemCount() != 0) {
            emptyView.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
                SwipeMenuItem copyItem = new SwipeMenuItem(CardSearchDialog.this)
                        .setHeight(LinearLayout.LayoutParams.MATCH_PARENT)
                        .setWeight(width)
                        .setText(R.string.search_menu1)
                        .setTextColor(Color.WHITE)
                        .setBackground(R.color.colorgreen)
                        .setImage(R.drawable.ic_content_paste_black_24dp);
                swipeLeftMenu.addMenuItem(copyItem);
                SwipeMenuItem collectItem = new SwipeMenuItem(CardSearchDialog.this)
                        .setBackground(R.color.colorAPPDark)
                        .setImage(R.drawable.ic_star_black_24dp)
                        .setText(R.string.search_menu2)
                        .setTextColor(Color.WHITE)
                        .setHeight(LinearLayout.LayoutParams.MATCH_PARENT)
                        .setWeight(width);
                swipeRightMenu.addMenuItem(collectItem);
            }
        };
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                if (direction == 1) {
                    ClipData clipData = ClipData.newPlainText(null, cardItemList.get(adapterPosition).getContent());
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clipData);
                    PasteToastUtils.getPasteToastUtils().ToastShow(CardSearchDialog.this, null);
                } else {
                    DataProcess.dataCollect(cardItemList.get(adapterPosition).getTime());
                }
            }
        });

        recyclerView.setAdapter(adapter);

        searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchText = charSequence.toString();
                cardItemList.clear();
                String keyword = "%" + searchText + "%";
                List<ClipSaves> clipSavesList1 = DataSupport.order("time desc").where("content like ?", keyword).find(ClipSaves.class);
                for (int i3 = 0; i3 <= clipSavesList1.size() - 1; i3++) {
                    ClipSaves clipSaves = clipSavesList1.get(i3);
                    if (clipSaves.getContent() != null && clipSaves.getContent().length() > 0) {
                        try {
                            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(clipSaves.getPakageName(), 0);
                            name = getPackageManager().getApplicationLabel(applicationInfo).toString();
                        } catch (PackageManager.NameNotFoundException e) {
                            name = "UNKNOWN";
                            e.printStackTrace();
                        }
                        CardItem item = new CardItem(name, clipSaves.getTime(), null,
                                clipSaves.getContent(), 0, clipSaves.getContent().length(),
                                false, null, null, false);
                        cardItemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
                if (adapter.getItemCount() != 0) {
                    emptyView.setVisibility(View.INVISIBLE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
