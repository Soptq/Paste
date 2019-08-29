package soptqs.paste.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import soptqs.paste.R;
import soptqs.paste.database.AppItem;
import soptqs.paste.database.DataProcess;

/**
 * Created by S0ptq on 2018/2/26.
 */

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {
    private List<AppItem> appItemList;

    public BlackListAdapter(List<AppItem> appItem) {
        appItemList = appItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    //TODO:黑名单搜索后的勾选有问题，需要查bug;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AppItem appItem = appItemList.get(position);
        holder.icon.setImageDrawable(appItem.getImage());
        holder.name.setText(appItem.getName());
        holder.pakagename.setText(appItem.getPakageName());
        holder.checkBox.setChecked(DataProcess.checkBL(appItem.getPakageName()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataProcess.processBL(b, appItem.getPakageName());
            }
        });
        holder.itemApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView pakagename;
        CheckBox checkBox;
        View itemApp;

        public ViewHolder(View view) {
            super(view);
            itemApp = view;
            icon = view.findViewById(R.id.bl_app_icon);
            name = view.findViewById(R.id.bl_app_name);
            pakagename = view.findViewById(R.id.bl_app_pakaname);
            checkBox = view.findViewById(R.id.bl_checkbox);
        }
    }
}
