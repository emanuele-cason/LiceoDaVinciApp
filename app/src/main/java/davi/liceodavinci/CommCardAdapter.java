package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emanuele on 01/01/2018 at 15:21!
 */

public class CommCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Communication.LocalCommunication> communications = new ArrayList<>();
    private Activity activity;
    private int section;
    private CommunicationsFragment communicationsFragment;

    CommCardAdapter(Activity activity, CommunicationsFragment communicationsFragment, List<Communication.LocalCommunication> communications, int section) {
        this.communications = communications;
        this.activity = activity;
        this.section = section;
        this.communicationsFragment = communicationsFragment;
    }

    CommCardAdapter(Activity activity, CommunicationsFragment communicationsFragment, List<Communication.LocalCommunication> communications, int section, int startPosition) {
        this.communications = communications;
        this.activity = activity;
        this.section = section;
        this.communicationsFragment = communicationsFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.com_recyclerview_item, parent, false);

        return new Item(row);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Item) holder).nameTV.setText(communications.get(position).getNameFormatted());
        if (communications.get(position).getId() != 0)
            ((Item) holder).idTV.setText(String.valueOf(communications.get(position).getId()));
        else ((Item) holder).idTV.setText("?");

        ((Item) holder).dataTV.setText(String.format("%td %<tb %<tY\n%<tH:%<tM", communications.get(position).getDataObject()));

        if (!communications.get(position).isSeen())
            ((Item) holder).nameTV.setTypeface(Typeface.DEFAULT_BOLD);
        else ((Item) holder).nameTV.setTypeface(Typeface.DEFAULT);

        if ((communications.get(position).getStatus() == Communication.DOWNLOADED) && (section != Communication.COMM_SAVED)) {
            ((Item) holder).download.setImageResource(R.drawable.ic_done);
            ((Item) holder).download.setClickable(false);
            ((Item) holder).download.setBackground(null);
        }
        if ((communications.get(position).getStatus() != Communication.DOWNLOADED) && (section != Communication.COMM_SAVED))
            ((Item) holder).download.setImageResource(R.drawable.ic_file_download);
        if (section == Communication.COMM_SAVED)
            ((Item) holder).download.setImageResource(R.drawable.ic_delete);
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
            recycler_row = itemView.findViewById(R.id.com_cardview);
            nameTV = itemView.findViewById(R.id.com_card_name);
            idTV = itemView.findViewById(R.id.com_card_id);
            dataTV = itemView.findViewById(R.id.com_card_data);
            recycler_row.setOnClickListener(this);
            download = itemView.findViewById(R.id.com_save_button);

            if (section == Communication.COMM_SAVED) {
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new File(activity.getFilesDir(), communications.get(getLayoutPosition()).getName()).delete();
                        ConfigurationManager.getIstance().setCommStatus(communications.get(getLayoutPosition()), Communication.REMOTE);
                        notifyItemRemoved(getLayoutPosition());
                        communications.remove(getLayoutPosition());
                        notifyItemRangeChanged(getLayoutPosition(), communications.size());
                    }
                });

            }

            if (section <= Communication.COMM_PROFS) {
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final CommDownload downloadTask = new CommDownload(activity, communicationsFragment, communications.get(getLayoutPosition()), CommDownload.DOWNLOAD, false);
                        downloadTask.execute();
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if (section <= Communication.COMM_PROFS) {
                final CommDownload downloadTask = new CommDownload(activity, null, communications.get(this.getLayoutPosition()), CommDownload.CACHE, true);
                downloadTask.execute();
            } else if (section == Communication.COMM_SAVED) {
                ((FragmentActivity) activity)
                        .getFragmentManager()
                        .beginTransaction()
                        .addToBackStack("pdf-render")
                        .replace(R.id.main_frame, new PdfRenderFragment(activity, communications.get(this.getLayoutPosition())))
                        .commit();
            }

            communicationsFragment.setScrollPosition(this.getAdapterPosition());
        }
    }
}