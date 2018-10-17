package davi.liceodavinci.schedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.ConfigurationManager;
import davi.liceodavinci.R;

/**
 * Created by Emanuele on 15/03/2018 at 18:51!
 */

public class ScheduleCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScheduleEvent> scheduleEvents = new ArrayList<>();
    private Activity activity;
    private int section;
    private boolean personal;
    private OnDataSetChanged callback;

    ScheduleCardAdapter(Activity activity, List<ScheduleEvent> scheduleEvents, int section, boolean personal, OnDataSetChanged callback) {
        this.activity = activity;
        this.section = section;
        this.personal = personal;
        this.callback = callback;

        //Questi cicli for annidati servono a skippare gli spazi vuoti in una colonna, se la prima ora è vuota ad esempio essa viene indicata con una card vuota
        for (int i = 0; i < scheduleEvents.get(scheduleEvents.size() - 1).getHourNum() + scheduleEvents.get(scheduleEvents.size() - 1).getDuration(); i++) {
            boolean done = false;

            for (ScheduleEvent scheduleEvent : scheduleEvents) {
                if ((scheduleEvent.getHourNum()) == i) {
                    this.scheduleEvents.add(scheduleEvent);
                    done = true;
                    i = scheduleEvent.getHourNum() + scheduleEvent.getDuration() - 1;
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

        if (scheduleEvents.get(position).getSubject().equals("")) {
            ((ViewHolder) holder).cardCell.setVisibility(View.INVISIBLE);
        }

        if (scheduleEvents.get(position).getNotes() != null && personal) ((ViewHolder)holder).dot.setVisibility(View.VISIBLE);

        if (section == ScheduleFragment.CLASSES_SCHEDULE)
            if (personal && scheduleEvents.get(position).getCustomName() != null) ((ViewHolder) holder).titleTextView.setText(scheduleEvents.get(position).getCustomName());
            else ((ViewHolder) holder).titleTextView.setText(scheduleEvents.get(position).getSubject());
        if (section == ScheduleFragment.PROFS_SCHEDULE) {
            String title = "";
            if (!scheduleEvents.get(position).getClassId().equals("")) {
                title = title.concat(scheduleEvents.get(position).getClassId());
                if (!scheduleEvents.get(position).getClassroom().equals(""))
                    title = title.concat(" - ".concat(scheduleEvents.get(position).getClassroom()));
            } else {
                if (!scheduleEvents.get(position).getSubject().equals(""))
                    title = title.concat(scheduleEvents.get(position).getSubject());
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
        ImageView dot;

        ViewHolder(View itemView) {
            super(itemView);
            cardCell = itemView.findViewById(R.id.schedule_cardview);
            titleTextView = itemView.findViewById(R.id.schedule_activity_title);
            dot = itemView.findViewById(R.id.schedule_activity_dot);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final MaterialDialog dialog =
                    new MaterialDialog.Builder(activity)
                            .customView(R.layout.schedule_dialog, true)
                            .build();

            final EditText title = (EditText) dialog.findViewById(R.id.title);
            final ImageButton edit = (ImageButton) dialog.findViewById(R.id.edit);
            TextView profOrClassFieldName = (TextView) dialog.findViewById(R.id.prof_or_class_field_name);
            TextView profOrClass = (TextView) dialog.findViewById(R.id.prof_or_class);
            TextView classroom = (TextView) dialog.findViewById(R.id.classroom);
            TextView duration = (TextView) dialog.findViewById(R.id.duration);
            TextView notesFieldName = (TextView) dialog.findViewById(R.id.notes_field_name);
            EditText notes = (EditText)dialog.findViewById(R.id.notes);

            final int position = this.getLayoutPosition();
            final String titleText = scheduleEvents.get(position).getSubject();
            final String notesText = scheduleEvents.get(position).getNotes();

            if(personal){
                edit.setVisibility(View.VISIBLE);
            }
            else {
                edit.setVisibility(View.GONE);
                notesFieldName.setVisibility(View.GONE);
                notes.setVisibility(View.GONE);
            }

            if (personal && (scheduleEvents.get(position).getNotes() != null)){
                notesFieldName.setVisibility(View.VISIBLE);
                notes.setVisibility(View.VISIBLE);

                notes.setText(scheduleEvents.get(position).getNotes());
            }

            if (personal && (scheduleEvents.get(position).getCustomName() != null)) {
                title.setText(scheduleEvents.get(position).getCustomName());
            } else title.setText(titleText);

            edit.setOnClickListener(view13 -> {

                PopupMenu popup = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    popup = new PopupMenu(activity, edit, Gravity.END);
                } else popup = new PopupMenu(activity, edit);

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.schedule_edit_personal, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(menuItem -> {

                    if (menuItem.getItemId() == R.id.add_note) {
                        notesFieldName.setVisibility(View.VISIBLE);
                        notes.setVisibility(View.VISIBLE);
                        notes.setEnabled(true);
                        notes.setBackground(null);
                        notes.requestFocus();
                        dialog.setActionButton(DialogAction.POSITIVE, "SALVA");
                        dialog.setActionButton(DialogAction.NEGATIVE, "REIMPOSTA");

                        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                        final View negative = dialog.getActionButton(DialogAction.NEGATIVE);

                        positive.setEnabled(false);

                        positive.setOnClickListener(view1 -> {
                            notes.setEnabled(false);
                            notes.setBackgroundColor(Color.TRANSPARENT);
                            negative.setVisibility(View.GONE);
                            positive.setVisibility(View.GONE);

                            ScheduleEvent editEvent = scheduleEvents.get(position);
                            editEvent.setNotes(notes.getText().toString());
                            if (notes.getText().toString().equals(""))editEvent.setNotes(null);
                            if (section == ScheduleFragment.CLASSES_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Pair<Integer, String>) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }
                            if (section == ScheduleFragment.PROFS_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Prof) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }

                            dialog.dismiss();
                            callback.onDataChanged();
                        });

                        negative.setOnClickListener(view12 -> {
                            notesFieldName.setVisibility(View.GONE);
                            notes.setVisibility(View.GONE);
                            negative.setVisibility(View.GONE);
                            positive.setVisibility(View.GONE);

                            ScheduleEvent editEvent = scheduleEvents.get(position);
                            editEvent.setNotes(null);
                            if (section == ScheduleFragment.CLASSES_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Pair<Integer, String>) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }
                            if (section == ScheduleFragment.PROFS_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Prof) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }

                            dialog.dismiss();
                            callback.onDataChanged();
                        });

                        notes.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if (!notes.getText().equals(notesText)) {
                                    positive.setEnabled(true);
                                }
                            }
                        });
                    }

                    if (menuItem.getItemId() == R.id.edit_name){
                        title.setEnabled(true);
                        title.setBackground(null);
                        title.requestFocus();
                        dialog.setActionButton(DialogAction.POSITIVE, "SALVA");
                        dialog.setActionButton(DialogAction.NEGATIVE, "REIMPOSTA");

                        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                        final View negative = dialog.getActionButton(DialogAction.NEGATIVE);

                        positive.setEnabled(false);

                        positive.setOnClickListener(view1 -> {
                            title.setEnabled(false);
                            title.setBackgroundColor(Color.TRANSPARENT);
                            negative.setVisibility(View.GONE);
                            positive.setVisibility(View.GONE);

                            ScheduleEvent editEvent = scheduleEvents.get(position);
                            editEvent.setCustomName(title.getText().toString());
                            if (section == ScheduleFragment.CLASSES_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Pair<Integer, String>) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }
                            if (section == ScheduleFragment.PROFS_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Prof) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }

                            dialog.dismiss();
                            callback.onDataChanged();
                        });

                        negative.setOnClickListener(view12 -> {
                            title.setText(titleText);
                            title.setEnabled(false);
                            title.setBackgroundColor(Color.TRANSPARENT);
                            negative.setVisibility(View.GONE);
                            positive.setVisibility(View.GONE);

                            ScheduleEvent editEvent = scheduleEvents.get(position);
                            editEvent.setCustomName(null);
                            if (section == ScheduleFragment.CLASSES_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Pair<Integer, String>) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }
                            if (section == ScheduleFragment.PROFS_SCHEDULE) {
                                ConfigurationManager.getIstance().editSchedule((Prof) ConfigurationManager.getIstance().getMyStatus(), scheduleEvents.get(position), editEvent);
                            }

                            dialog.dismiss();
                            callback.onDataChanged();
                        });

                        title.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if (!title.getText().equals(titleText)) {
                                    positive.setEnabled(true);
                                }
                            }
                        });
                    }
                    return false;
                });
            });

            if (section == ScheduleFragment.CLASSES_SCHEDULE) {
                profOrClassFieldName.setText("Docente");
                profOrClass.setText(scheduleEvents.get(this.getLayoutPosition()).getProfSurname().concat(" ").concat(scheduleEvents.get(this.getLayoutPosition()).getProfName()));
            }

            if (section == ScheduleFragment.PROFS_SCHEDULE) {
                profOrClassFieldName.setText("Classe");
                profOrClass.setText(scheduleEvents.get(this.getLayoutPosition()).getClassId());
            }

            classroom.setText(scheduleEvents.get(this.getLayoutPosition()).getClassroom());
            duration.setText(String.valueOf(scheduleEvents.get(this.getLayoutPosition()).getDuration()).concat(" h"));

            if (profOrClass.getText().equals("")) profOrClass.setText("-");
            if (classroom.getText().equals("")) classroom.setText("-");
            if (duration.getText().equals("")) classroom.setText("-");

            dialog.show();
        }
    }
}