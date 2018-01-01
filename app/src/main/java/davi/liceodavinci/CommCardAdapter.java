package davi.liceodavinci;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Emanuele on 01/01/2018 at 15:21.
 */

public class CommCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Communication[] communications;

    protected CommCardAdapter(Communication[] communications) {
        this.communications = communications;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.com_recyclerview_item, parent, false);

        return new Item(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Item) holder).nameTV.setText(communications[position].getName());
        if (communications[position].getId()!=0)((Item) holder).idTV.setText(String.valueOf(communications[position].getId()));
        else ((Item) holder).idTV.setText("?");
    }

    @Override
    public int getItemCount() {
        return communications.length;
    }

    private class Item extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTV;
        TextView idTV;
        CardView recycler_row;

        Item(View itemView) {
            super(itemView);
            recycler_row = (CardView) itemView.findViewById(R.id.com_cardview);
            nameTV = (TextView) itemView.findViewById(R.id.com_card_name);
            idTV = (TextView) itemView.findViewById(R.id.com_card_id);
            recycler_row.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}