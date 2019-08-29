package soptqs.paste.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import soptqs.paste.R;
import soptqs.paste.database.CardItem;
import soptqs.paste.utils.SimpleDateFormatThreadSafe;

/**
 * Created by S0ptq on 2018/2/20.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<CardItem> cardItemList;
    private SimpleDateFormatThreadSafe sdf = new SimpleDateFormatThreadSafe("yyyy-MM-dd hh:mm");

    public SearchAdapter(List<CardItem> cardItemList1) {
        cardItemList = cardItemList1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardItem cardItem = cardItemList.get(position);
        holder.name.setText(cardItem.getName());
        holder.time.setText(sdf.format(Long.parseLong(cardItem.getTime())));
        holder.content.setText(cardItem.getContent());
    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView content;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.card_search_name);
            time = view.findViewById(R.id.card_search_time);
            content = view.findViewById(R.id.card_search_content);
        }
    }
}
