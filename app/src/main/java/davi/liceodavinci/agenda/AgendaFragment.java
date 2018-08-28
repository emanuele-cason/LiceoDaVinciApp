package davi.liceodavinci.agenda;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import davi.liceodavinci.ConfigurationManager;
import davi.liceodavinci.OnFetchCompleteListener;
import davi.liceodavinci.R;

/**
 * Created by Emanuele on 06/05/2018 at 20:05!
 * https://github.com/prolificinteractive/material-calendarview
 */

@SuppressLint("ValidFragment")
public class AgendaFragment extends Fragment {

    private Activity activity;
    private MaterialCalendarView calendar;
    private RecyclerView eventsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public AgendaFragment(Activity activity){
       this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.agenda_actionbar_menu, menu);

        final MenuItem selectToday = menu.findItem(R.id.selectToday);
        selectToday.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (calendar != null){
                    setToToday();
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agenda_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.calendar = getActivity().findViewById(R.id.calendarView);
        this.eventsRecyclerView = getActivity().findViewById(R.id.agenda_recyclerview);
        this.swipeRefreshLayout = getActivity().findViewById(R.id.agenda_swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setToToday();

        this.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {

                Calendar beforeDate = (Calendar)date.getCalendar().clone(); // All'ultimo giorno del mese prossimo
                beforeDate.add(Calendar.MONTH, 1);
                beforeDate.set(Calendar.DAY_OF_MONTH,1);
                beforeDate.set(Calendar.HOUR_OF_DAY, 0);
                beforeDate.set(Calendar.MINUTE, 0);
                beforeDate.set(Calendar.SECOND, 0);

                Calendar afterDate = (Calendar)date.getCalendar().clone(); // Si prende gli eventi dal primo giorno del mese corrente
                afterDate.add(Calendar.DAY_OF_MONTH,-1);
                afterDate.set(Calendar.HOUR_OF_DAY, 23);
                afterDate.set(Calendar.MINUTE, 59);
                afterDate.set(Calendar.SECOND, 58);

                fetchEvents(null, null, ((int)(afterDate.getTimeInMillis()/1000L)), ((int)(beforeDate.getTimeInMillis()/1000L)));
            }
        });
    }

    private void setToToday(){
        fetchAndStoreCurrentMonthEvents();
        setResult(sortByBeginDate(ConfigurationManager.getIstance().getEvents()));

        calendar.setCurrentDate(new Date(System.currentTimeMillis()));
        calendar.setSelectedDate(new Date(System.currentTimeMillis()));
    }

    private void fetchEvents(final List<String> filterTitle, final List<String> filterContent, final int after, final int before){
        swipeRefreshLayout.setRefreshing(true);

        try {
            new AgendaDataFetcher(activity, this).fetchEvents(filterTitle, filterContent, before, after, new OnFetchCompleteListener<List<Event>>() {
                @Override
                public void onSuccess(final List<Event> result) {
                    new Handler(activity.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            setResult(sortByBeginDate(result));
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar snackbar = Snackbar
                            .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG)
                            .setAction("RIPROVA", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    fetchEvents(filterTitle, filterContent, before, after);
                                }
                            });
                    snackbar.show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndStoreCurrentMonthEvents(){

        Calendar afterDate = Calendar.getInstance(); // Si prende gli eventi dal primo giorno del mese corrente
        afterDate.set(Calendar.DAY_OF_MONTH, 1);
        afterDate.set(Calendar.HOUR_OF_DAY, 0);
        afterDate.set(Calendar.MINUTE, 0);
        afterDate.set(Calendar.SECOND, 0);

        Calendar beforeDate = Calendar.getInstance(); // All'ultimo giorno del mese prossimo
        beforeDate.add(Calendar.MONTH, 2);
        beforeDate.set(Calendar.DAY_OF_MONTH,1);
        beforeDate.set(Calendar.HOUR_OF_DAY, 0);
        beforeDate.set(Calendar.MINUTE, 0);
        beforeDate.set(Calendar.SECOND, 0);

        try {
            new AgendaDataFetcher(activity, this).fetchEvents(null, null, (int)(beforeDate.getTimeInMillis()/1000L), (int)(afterDate.getTimeInMillis()/1000L), new OnFetchCompleteListener<List<Event>>() {
                @Override
                public void onSuccess(List<Event> result) {
                    ConfigurationManager.getIstance().saveEvents(result);
                }

                @Override
                public void onFailure(Exception e) {
                    if (ConfigurationManager.getIstance().getEvents() != null) {
                        Snackbar snackbar = Snackbar
                                .make(activity.findViewById(R.id.main_frame), "Errore di connessione. Impossibile aggiornare l'agenda", Snackbar.LENGTH_LONG)
                                .setAction("RIPROVA", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        fetchAndStoreCurrentMonthEvents();
                                    }
                                });
                        snackbar.show();
                    } else{
                        Snackbar snackbar = Snackbar
                                .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG)
                                .setAction("RIPROVA", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        fetchAndStoreCurrentMonthEvents();
                                    }
                                });
                        snackbar.show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setResult(List<Event> events) {
        swipeRefreshLayout.setRefreshing(false);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        if ((events != null) && (events.size() != 0))
            eventsRecyclerView.setAdapter(new AgendaCardAdapter(activity, events));
    }

    private List<Event> sortByBeginDate(List<Event> events){
        if (!(events == null)) if (!events.isEmpty()) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event e1, Event e2) {
                    return e1.getBeginCalendar().getTime().compareTo(e2.getBeginCalendar().getTime());
                }
            });
        }

        return events;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
