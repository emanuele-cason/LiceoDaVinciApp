package davi.liceodavinci.schedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import davi.liceodavinci.ConfigurationManager;
import davi.liceodavinci.MainActivity;
import davi.liceodavinci.R;

/**
 * Created by Emanuele on 07/02/2018 at 16:36!
 */

@SuppressLint("ValidFragment")
public class ScheduleFragment extends Fragment {

    public static final int PERSONAL_SCHEDULE = 0;
    public static final int CLASSES_SCHEDULE = 1;
    public static final int PROFS_SCHEDULE = 2;

    private Activity activity;
    ScheduleFragment thisFragment;
    private LinearLayout scheduleContainer;
    private LinearLayout profSelectorBar;
    private int currentSchedule = 0;
    private List<String> classes;
    private String[] classesNum = {"1", "2", "3", "4", "5"};

    @SuppressLint("ValidFragment")
    public ScheduleFragment(Activity activity) {
        this.activity = activity;
        this.thisFragment = this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        switch (currentSchedule) {
            case PERSONAL_SCHEDULE: {
                menu.clear();
                break;
            }
            case CLASSES_SCHEDULE: {
                final ScheduleDataFetcher scheduleDataFetcher = new ScheduleDataFetcher(activity, this);
                try {
                    scheduleDataFetcher.fetchClassesList();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                inflater.inflate(R.menu.schedule_actionbar, menu);

                MenuItem itemSpinner1 = menu.findItem(R.id.first_spinner);
                final Spinner firstSpinner = (Spinner) MenuItemCompat.getActionView(itemSpinner1);

                final ArrayAdapter<String> firstAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_class_style, classesNum);
                firstAdapter.setDropDownViewResource(R.layout.spinner_class_dropdown_style);
                firstSpinner.setAdapter(firstAdapter);

                MenuItem itemSpinner2 = menu.findItem(R.id.second_spinner);
                final Spinner secondSpinner = (Spinner) MenuItemCompat.getActionView(itemSpinner2);

                firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        if (ConfigurationManager.getIstance().getClassesListFromSavedJSON() == null) {
                            Snackbar snackbar = Snackbar
                                    .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }

                        List<String> sections = new ArrayList<>();
                        for (String cl : ConfigurationManager.getIstance().getClassesListFromSavedJSON())
                            if (cl.contains(firstSpinner.getSelectedItem().toString()))
                                sections.add(String.valueOf(cl.charAt(1)));

                        java.util.Collections.sort(sections);

                        final ArrayAdapter<String> secondAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_class_style, sections);
                        secondAdapter.setDropDownViewResource(R.layout.spinner_class_dropdown_style);
                        secondSpinner.setAdapter(secondAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            scheduleContainer.setVisibility(View.GONE);
                        } catch (Exception ignored) {
                        }

                        renderSchedule(firstSpinner.getSelectedItem().toString().concat(secondSpinner.getSelectedItem().toString()));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                break;
            }
            case PROFS_SCHEDULE: {
                menu.clear();
                break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scheduleContainer = activity.findViewById(R.id.schedule_scrollview);
        profSelectorBar = activity.findViewById(R.id.schedule_prof_selection_bar);

        AHBottomNavigation bar = activity.findViewById(R.id.bottom_navigation);
        bar.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        AHBottomNavigationItem personal = new AHBottomNavigationItem(
                "Personale",
                R.drawable.ic_personal,
                R.color.colorPrimary
        );
        AHBottomNavigationItem classes = new AHBottomNavigationItem(
                "Classi",
                R.drawable.ic_classes,
                R.color.colorPrimary
        );
        AHBottomNavigationItem profs = new AHBottomNavigationItem(
                "Docenti",
                R.drawable.ic_com_profs,
                R.color.colorPrimary
        );
        bar.addItems(Arrays.asList(personal, classes, profs));
        bar.setAccentColor(Color.parseColor("#3F51B5"));
        bar.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        bar.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                currentSchedule = position;
                getActivity().invalidateOptionsMenu();

                switch (position) {
                    case PERSONAL_SCHEDULE: {
                        ((MainActivity) activity).getSupportActionBar().setTitle("Orario personale");
                        profSelectorBar.setVisibility(View.GONE);

                        try {
                            scheduleContainer.setVisibility(View.GONE);
                        } catch (Exception ignored) {
                        }

                        break;
                    }
                    case CLASSES_SCHEDULE: {
                        ((MainActivity) activity).getSupportActionBar().setTitle("Orario classe");
                        profSelectorBar.setVisibility(View.GONE);

                        try {
                            scheduleContainer.setVisibility(View.GONE);
                        } catch (Exception ignored) {
                        }

                        break;
                    }
                    case PROFS_SCHEDULE: {
                        ((MainActivity) activity).getSupportActionBar().setTitle("Orario docente");
                        profSelectorBar.setVisibility(View.VISIBLE);
                        List<Prof> profList = ConfigurationManager.getIstance().getProfsListFromSavedJSON();

                        try {
                            scheduleContainer.setVisibility(View.GONE);
                        } catch (Exception ignored) {
                        }

                        try {
                            new ScheduleDataFetcher(activity, thisFragment).fetchProfsList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (profList != null) prepareProfsSelector(profList);

                        break;
                    }
                }
                return true;
            }
        });
    }

    public void fetchClassesComplete(List<String> result) {
        if (ConfigurationManager.getIstance().getClassesListFromSavedJSON() == null){
            ConfigurationManager.getIstance().saveClassesList(result);
            getActivity().invalidateOptionsMenu();
        }
        ConfigurationManager.getIstance().saveClassesList(result);
    }

    public void fetchProfsListComplete(List<Prof> result) {
        if (ConfigurationManager.getIstance().getClassesListFromSavedJSON() == null){
            ConfigurationManager.getIstance().saveProfsList(result);
            prepareProfsSelector(result);
        }
        ConfigurationManager.getIstance().saveProfsList(result);
    }

    public void fetchClassComplete(List<ScheduleEvent> events, String classId) {
        if (ConfigurationManager.getIstance().getScheduleListFromSavedJSON(classId) == null) {
            ConfigurationManager.getIstance().saveSchedule(events, classId);
            renderSchedule(classId);
        }
        ConfigurationManager.getIstance().saveSchedule(events, classId);
    }

    public void fetchProfComplete(List<ScheduleEvent> events, Prof prof){
        if (ConfigurationManager.getIstance().getScheduleListFromSavedJSON(prof) == null) {
            ConfigurationManager.getIstance().saveSchedule(events, prof);
            renderSchedule(prof);
        }
        ConfigurationManager.getIstance().saveSchedule(events, prof);
    }

    public void prepareProfsSelector(final List<Prof> profsList){

        if (currentSchedule != PROFS_SCHEDULE) return;
        if (profsList == null) return;
        List<String> profsListString = new ArrayList<>();

        Collections.sort(profsList, new Comparator<Prof>() {
            @Override
            public int compare(Prof s1, Prof s2) {
                return String.valueOf(s1.getSurname()).compareTo(s2.getSurname());
            }
        });

        for (Prof prof : profsList){
            if (prof != null) profsListString.add(prof.getSurname().concat(" ").concat(prof.getName()));
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_class_style, profsListString);
        final Spinner profsSpinner = activity.findViewById(R.id.schedule_prof_spinner);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_class_dropdown_style);
        profsSpinner.setAdapter(spinnerAdapter);

        profsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                renderSchedule(profsList.get(profsSpinner.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void renderSchedule(final String classId) {

        if (currentSchedule != CLASSES_SCHEDULE) return;

        RecyclerView[] scheduleRecyclerViews = new RecyclerView[6];
        scheduleRecyclerViews[ScheduleEvent.MON] = getActivity().findViewById(R.id.schedule_mon_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.TUE] = getActivity().findViewById(R.id.schedule_tue_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.WED] = getActivity().findViewById(R.id.schedule_wed_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.THU] = getActivity().findViewById(R.id.schedule_thu_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.FRI] = getActivity().findViewById(R.id.schedule_fri_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.SAT] = getActivity().findViewById(R.id.schedule_sat_recyclerview);

        AHBottomNavigation bar = activity.findViewById(R.id.bottom_navigation);

        ScheduleDataFetcher scheduleDataFetcher = new ScheduleDataFetcher(activity, thisFragment);
        try {
            scheduleDataFetcher.fetchClassSchedule(String.valueOf(classId.charAt(0)), String.valueOf(classId.charAt(1)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < scheduleRecyclerViews.length; i++) {

            List<ScheduleEvent> scheduleActivities = ConfigurationManager.getIstance().getScheduleListFromSavedJSON(classId);

            if (scheduleActivities != null) {
                scheduleActivities = getDayOfWeekSchedule(scheduleActivities, i);
                if (scheduleActivities.size() > 0) {
                    scheduleRecyclerViews[i].setLayoutManager(new LinearLayoutManager(getActivity()) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    });

                    scheduleRecyclerViews[i].setVisibility(View.VISIBLE);
                    scheduleContainer.setVisibility(View.VISIBLE);
                    ScheduleCardAdapter adapter = new ScheduleCardAdapter(getActivity(), scheduleActivities, CLASSES_SCHEDULE);
                    scheduleRecyclerViews[i].setAdapter(adapter);
                    runLayoutAnimation(scheduleRecyclerViews[i]);
                }else {
                    scheduleRecyclerViews[i].setVisibility(View.INVISIBLE);
                }

                scheduleRecyclerViews[i].setPadding(0, 0, 0, bar.getHeight());
            }
        }
    }

    public void renderSchedule(final Prof prof){

        if (currentSchedule != PROFS_SCHEDULE) return;

        RecyclerView[] scheduleRecyclerViews = new RecyclerView[6];
        scheduleRecyclerViews[ScheduleEvent.MON] = getActivity().findViewById(R.id.schedule_mon_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.TUE] = getActivity().findViewById(R.id.schedule_tue_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.WED] = getActivity().findViewById(R.id.schedule_wed_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.THU] = getActivity().findViewById(R.id.schedule_thu_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.FRI] = getActivity().findViewById(R.id.schedule_fri_recyclerview);
        scheduleRecyclerViews[ScheduleEvent.SAT] = getActivity().findViewById(R.id.schedule_sat_recyclerview);

        AHBottomNavigation bar = activity.findViewById(R.id.bottom_navigation);

        try {
            new ScheduleDataFetcher(activity, thisFragment).fetchProfSchedule(prof);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < scheduleRecyclerViews.length; i++) {

            List<ScheduleEvent> scheduleActivities = ConfigurationManager.getIstance().getScheduleListFromSavedJSON(prof);

            if (scheduleActivities != null) {
                scheduleActivities = getDayOfWeekSchedule(scheduleActivities, i);
                if (scheduleActivities.size() > 0) {
                    scheduleRecyclerViews[i].setLayoutManager(new LinearLayoutManager(getActivity()) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    });

                    scheduleRecyclerViews[i].setVisibility(View.VISIBLE);
                    scheduleContainer.setVisibility(View.VISIBLE);
                    ScheduleCardAdapter adapter = new ScheduleCardAdapter(getActivity(), scheduleActivities, PROFS_SCHEDULE);
                    scheduleRecyclerViews[i].setAdapter(adapter);
                    runLayoutAnimation(scheduleRecyclerViews[i]);
                }else {
                    scheduleRecyclerViews[i].setVisibility(View.INVISIBLE);
                }

                scheduleRecyclerViews[i].setPadding(0, 0, 0, bar.getHeight());
            }
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private List<ScheduleEvent> getDayOfWeekSchedule(List<ScheduleEvent> scheduleActivities, int dayOfWeek) {

        List<ScheduleEvent> resultSchedule = new ArrayList<>();
        for (ScheduleEvent anyActivity : scheduleActivities) {
            if (anyActivity.getGiorno() == dayOfWeek) {
                resultSchedule.add(anyActivity);
            }
        }

        Collections.sort(resultSchedule, new Comparator<ScheduleEvent>() {
            @Override
            public int compare(ScheduleEvent s1, ScheduleEvent s2) {
                return Integer.valueOf(s1.getHourNum()).compareTo(s2.getHourNum());
            }
        });

        return resultSchedule;
    }
}