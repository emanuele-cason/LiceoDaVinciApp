package davi.liceodavinci.agenda;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        SimpleDateFormat datetime = new SimpleDateFormat("dd MMM (HH:mm)");
        SimpleDateFormat date = new SimpleDateFormat("dd MMM");

        ((Item)holder).titleTV.setText(events.get(position).getTitle());
        String period = "";

        //L'ORDINE DEGLI IF E' STRUTTURATO IN MODO DA SOVRASCRIVERE PERIOD SECONDO UNA SCALA DI PRIORITA'. NON VARIARNE L'ORDINE
        if ((events.get(position).getBeginCalendar().get(Calendar.DAY_OF_YEAR) == events.get(position).getEndCalendar().get(Calendar.DAY_OF_YEAR)) && (events.get(position).getBeginCalendar().get(Calendar.YEAR) == events.get(position).getEndCalendar().get(Calendar.YEAR))){
            //Se l'inizio e la fine di uno stesso evento sono in uno stesso giorno
            period = time.format(events.get(position).getBeginCalendar().getTime()).concat(" - ").concat(time.format(events.get(position).getEndCalendar().getTime()));
            if (time.format(events.get(position).getBeginCalendar().getTime()).equals("00:00")) period = "Fino alle ".concat(time.format(events.get(position).getEndCalendar().getTime()));
            if (time.format(events.get(position).getEndCalendar().getTime()).equals("23:59")) period = "Dalle ".concat(time.format(events.get(position).getBeginCalendar().getTime())).concat(" in poi");
            if ((time.format(events.get(position).getBeginCalendar().getTime()).equals("00:00")) && (time.format(events.get(position).getEndCalendar().getTime()).equals("23:59"))){
                //Se il giorno inizia con 00:00 e finisce con 23:59
                period = "Tutto il giorno";
            }
        }else {
            //Se inizio e fine dello stesso evento sono su giorni diversi - evento a cavallo di più giorni...
            if (time.format(events.get(position).getBeginCalendar().getTime()).equals("00:00")) period = date.format(events.get(position).getBeginCalendar().getTime());
            else period = datetime.format(events.get(position).getBeginCalendar().getTime());

            if (time.format(events.get(position).getEndCalendar().getTime()).equals("23:59")) period = period.concat(" - ").concat(date.format(events.get(position).getEndCalendar().getTime()));
            else period = period.concat(" - ").concat(datetime.format(events.get(position).getEndCalendar().getTime()));

            if ((!time.format(events.get(position).getBeginCalendar().getTime()).equals("00:00")) && (!time.format(events.get(position).getEndCalendar().getTime()).equals("23:59")))
                period = datetime.format(events.get(position).getBeginCalendar().getTime()).concat(" - ").concat(datetime.format(events.get(position).getEndCalendar().getTime()));
        }
        ((Item)holder).timeTV.setText(period);

        if ((position <= 0) || (events.get(position - 1).getBeginCalendar().get(Calendar.DATE) != (events.get(position).getBeginCalendar().get(Calendar.DATE)))){
            //Se non ce un evento precedente a quello corrente o se il giorno dell'evento precedente è diverso da quello del corrente --> visualizza giorno
            ((Item)holder).date.setVisibility(View.VISIBLE);
            ((Item)holder).day.setText(String.valueOf(events.get(position).getBeginCalendar().get(Calendar.DAY_OF_MONTH)));
            ((Item)holder).month.setText(new SimpleDateFormat("MMM").format(events.get(position).getBeginCalendar().getTime()));
        }else ((Item)holder).date.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return (events != null) ? events.size(): 0;
    }
}

class Item extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView titleTV;
    TextView timeTV;
    CardView recycler_row;
    LinearLayout date;
    TextView day;
    TextView month;

    Item(View itemView) {
        super(itemView);
        recycler_row = itemView.findViewById(R.id.event_cardview);
        titleTV = itemView.findViewById(R.id.agenda_event_title);
        timeTV = itemView.findViewById(R.id.agenda_event_time);
        recycler_row.setOnClickListener(this);
        date = itemView.findViewById(R.id.agenda_event_date);
        day = itemView.findViewById(R.id.agenda_event_day);
        month = itemView.findViewById(R.id.agenda_event_month);
    }

    @Override
    public void onClick(View view) {

    }
}