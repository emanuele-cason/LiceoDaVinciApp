package davi.liceodavinci.agenda;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.R;

/**
 * Created by Emanuele on 02/06/2018 at 20:49!
 */

public class AgendaCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> events = new ArrayList<>();
    private Activity activity;

    AgendaCardAdapter(Activity activity, List<Event> events) {
        this.activity = activity;
        this.events = events;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.agenda_recyclerview_item, parent, false);

        return new Item(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        ((Item)holder).titleTV.setText(events.get(position).getTitle());
        ((Item)holder).timeTV.setText(format.format(events.get(position).getBeginCalendar().getTime()).concat(" - ").concat(format.format(events.get(position).getEndCalendar().getTime())));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

class Item extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView titleTV;
    TextView timeTV;
    CardView recycler_row;

    Item(View itemView) {
        super(itemView);
        recycler_row = itemView.findViewById(R.id.event_cardview);
        titleTV = itemView.findViewById(R.id.agenda_event_title);
        timeTV = itemView.findViewById(R.id.agenda_event_time);
        recycler_row.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}