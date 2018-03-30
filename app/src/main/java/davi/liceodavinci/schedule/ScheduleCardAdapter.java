package davi.liceodavinci.schedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.R;

/**
 * Created by Emanuele on 15/03/2018 at 18:51!
 */

public class ScheduleCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScheduleEvent> scheduleEvents = new ArrayList<>();
    private Activity activity;
    private int section;

    ScheduleCardAdapter(Activity activity, List<ScheduleEvent> scheduleEvents, int section) {
        this.activity = activity;
        this.section = section;

        //Questi cicli for annidati servono a skippare gli spazi vuoti in una colonna, se la prima ora è vuota ad esempio essa viene indicata con una card vuota
        for (int i = 0; i < scheduleEvents.get(scheduleEvents.size() - 1).getHourNum() + scheduleEvents.get(scheduleEvents.size() - 1).getDuration(); i++) {
            Log.d("line", String.valueOf(i));
            boolean done = false;

            for (ScheduleEvent scheduleEvent : scheduleEvents) {
                for (int d = 0; d < scheduleEvent.getDuration(); d++) {
                    if ((scheduleEvent.getHourNum() + d) == i) {
                        this.scheduleEvents.add(scheduleEvent);
                        done = true;
                        i = scheduleEvent.getHourNum() + scheduleEvent.getDuration();
                    }
                }
            }
            if (!done) this.scheduleEvents.add(new ScheduleEvent(i, "1"));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.schedule_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int margin = (int) activity.getResources().getDimension(R.dimen.schedule_event_margin);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (activity.getResources().getDimension(R.dimen.schedule_event_height) +
                        (scheduleEvents.get(position).getDuration() > 1 ? (margin) : 0)) * scheduleEvents.get(position).getDuration());
        layoutParams.setMargins(margin, margin, margin, margin);
        ((ViewHolder) holder).cardCell.setLayoutParams(layoutParams);

        if (scheduleEvents.get(position).getMateria() == null) {
            ((ViewHolder) holder).cardCell.setVisibility(View.INVISIBLE);
        }

        if (section == ScheduleFragment.CLASSES_SCHEDULE)
            ((ViewHolder) holder).titleTextView.setText(scheduleEvents.get(position).getMateria());
        if (section == ScheduleFragment.PROFS_SCHEDULE) {
            String title = "";
            if (scheduleEvents.get(position).getClasse() != null) {
                title = title.concat(scheduleEvents.get(position).getClasse());
                if (scheduleEvents.get(position).getAula() != null)
                    title = title.concat(" - ".concat(scheduleEvents.get(position).getAula()));
            } else {
                if (scheduleEvents.get(position).getMateria() != null)
                    title = title.concat(scheduleEvents.get(position).getMateria());
            }

            ((ViewHolder) holder).titleTextView.setText(title);
        }

    }

    @Override
    public int getItemCount() {
        return scheduleEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardCell;
        TextView titleTextView;

        ViewHolder(View itemView) {
            super(itemView);
            cardCell = itemView.findViewById(R.id.schedule_cardview);
            titleTextView = itemView.findViewById(R.id.schedule_activity_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}