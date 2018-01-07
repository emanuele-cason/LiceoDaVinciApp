package davi.liceodavinci;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Emanuele on 01/01/2018 at 15:21.
 */

public class CommCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Communication[] communications;
    private Activity activity;
    private SwipeRefreshLayout layout;

    protected CommCardAdapter(Activity activity, SwipeRefreshLayout layout, Communication[] communications) {
        this.communications = communications;
        this.activity = activity;
        this.layout = layout;
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
        if (communications[position].getId() != 0)
            ((Item) holder).idTV.setText(String.valueOf(communications[position].getId()));
        else ((Item) holder).idTV.setText("?");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSS");
        try {
            Date date = format.parse(communications[position].getData());
            ((Item) holder).dataTV.setText(String.format("%td-%<tM-%<tY\n%<tH:%<tM", date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return communications.length;
    }

    private class Item extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTV;
        TextView idTV;
        TextView dataTV;
        CardView recycler_row;

        Item(View itemView) {
            super(itemView);
            recycler_row = (CardView) itemView.findViewById(R.id.com_cardview);
            nameTV = (TextView) itemView.findViewById(R.id.com_card_name);
            idTV = (TextView) itemView.findViewById(R.id.com_card_id);
            dataTV = (TextView) itemView.findViewById(R.id.com_card_data);
            recycler_row.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ProgressDialog progressDialog;

            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Apertura in corso");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);

            final CommDownload downloadTask = new CommDownload(activity, layout, progressDialog, false);
            downloadTask.execute(communications[this.getLayoutPosition()]);

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });
        }
    }
}