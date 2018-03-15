package davi.liceodavinci.Schedule;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import davi.liceodavinci.R;

/**
 * Created by Emanuele on 15/03/2018 at 18:51!
 */

public class ScheduleCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScheduleActivity> scheduleActivities;

    ScheduleCardAdapter(List<ScheduleActivity> scheduleActivities) {
        this.scheduleActivities = scheduleActivities;
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
        ((ViewHolder) holder).titleTextView.setText(scheduleActivities.get(position).getMateria());
    }

    @Override
    public int getItemCount() {
        return scheduleActivities.size();
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

