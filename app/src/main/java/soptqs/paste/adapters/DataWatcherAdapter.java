package soptqs.paste.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import soptqs.paste.R;
import soptqs.paste.database.CardItem;
import soptqs.paste.views.FlexibleTextView;

/**
 * Created by S0ptq on 2018/2/11.
 */

public class DataWatcherAdapter extends RecyclerView.Adapter<DataWatcherAdapter.ViewHolder> {

    private List<CardItem> cardItemList;

    public DataWatcherAdapter(List<CardItem> cardItemList1) {
        cardItemList = cardItemList1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_database, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardItem cardItem = cardItemList.get(position);
        holder.num.setText(Integer.toString(cardItem.getTopbg()));
        holder.packageName.setText(cardItem.getName());
        holder.time.setText(cardItem.getTime());
        holder.content.setText(cardItem.getContent());
    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        FlexibleTextView num;
        TextView packageName;
        TextView time;
        TextView content;

        public ViewHolder(View view) {
            super(view);
            num = view.findViewById(R.id.item_data_id);
            packageName = view.findViewById(R.id.item_data_packageName);
            time = view.findViewById(R.id.item_data_time);
            content = view.findViewById(R.id.item_data_content);
        }
    }

}
