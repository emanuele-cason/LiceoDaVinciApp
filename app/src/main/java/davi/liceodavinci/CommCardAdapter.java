package davi.liceodavinci;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Emanuele on 01/01/2018 at 15:21.
 */

public class CommCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Communication> communications = new ArrayList<>();
    private Activity activity;
    private SwipeRefreshLayout layout;
    private int section;

    protected CommCardAdapter(Activity activity, CommunicationsFragment communicationsFragment, SwipeRefreshLayout layout, List<Communication> communications, int section) {
        this.communications = communications;
        this.activity = activity;
        this.layout = layout;
        this.section = section;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.com_recyclerview_item, parent, false);

        return new Item(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Item) holder).nameTV.setText(communications.get(position).getName());
        if (communications.get(position).getId() != 0)
            ((Item) holder).idTV.setText(String.valueOf(communications.get(position).getId()));
        else ((Item) holder).idTV.setText("?");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSS");
        try {
            Date date = format.parse(communications.get(position).getData());
            ((Item) holder).dataTV.setText(String.format("%td-%<tm-%<tY\n%<tH:%<tM", date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return communications.size();
    }

    private class Item extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTV;
        TextView idTV;
        TextView dataTV;
        CardView recycler_row;
        ImageButton download;

        Item(View itemView) {
            super(itemView);
            recycler_row = (CardView) itemView.findViewById(R.id.com_cardview);
            nameTV = (TextView) itemView.findViewById(R.id.com_card_name);
            idTV = (TextView) itemView.findViewById(R.id.com_card_id);
            dataTV = (TextView) itemView.findViewById(R.id.com_card_data);
            recycler_row.setOnClickListener(this);
            download = (ImageButton) itemView.findViewById(R.id.com_save_button);

            if (section == Communication.COMM_SAVED) {
                download.setImageResource(R.drawable.ic_delete);
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new File(activity.getFilesDir().getPath().concat("/").concat(communications.get(getLayoutPosition()).getName())).delete();
                        notifyItemRemoved(getLayoutPosition());
                        communications.remove(getLayoutPosition());
                        notifyItemRangeChanged(getLayoutPosition(), communications.size());
                    }
                });
            }

            if (section < Communication.COMM_SAVED) {
                download.setImageResource(R.drawable.ic_file_download);
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProgressDialog progressDialog;

                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage("Download in corso");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setCancelable(true);

                        final CommDownload downloadTask = new CommDownload(activity, layout, progressDialog, true);
                        downloadTask.execute(communications.get(getLayoutPosition()));

                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                downloadTask.cancel(true);
                            }
                        });
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if (section < Communication.COMM_SAVED) {
                ProgressDialog progressDialog;

                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Apertura in corso");
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(true);

                final CommDownload downloadTask = new CommDownload(activity, layout, progressDialog, false);
                downloadTask.execute(communications.get(this.getLayoutPosition()));

                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            }else if(section == Communication.COMM_SAVED) {
                ((FragmentActivity)activity)
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("pdf-render")
                        .replace(R.id.empty_frame, new PdfRenderFragment(activity, communications.get(this.getLayoutPosition()), Communication.DOWNLOADED))
                        .commit();
            }
        }
    }
}