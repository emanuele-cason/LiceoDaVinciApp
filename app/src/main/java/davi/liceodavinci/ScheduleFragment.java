package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Emanuele on 07/02/2018 at 16:36!
 */

@SuppressLint("ValidFragment")
public class ScheduleFragment extends Fragment {

    private final int PERSONAL_SCHEDULE = 0;
    private final int CLASSES_SCHEDULE = 1;
    private final int PROFS_SCHEDULE = 2;

    private Activity activity;
    private int currentSchedule = 0;
    private List<String> classes;
    private String[] classesNum = {"1", "2", "3", "4", "5"};

    @SuppressLint("ValidFragment")
    ScheduleFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        classes = ConfigurationManager.getIstance().getClassesListFromSavedJSON();
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

                break;
            }
            case CLASSES_SCHEDULE: {
                final ScheduleDataFetcher scheduleDataFetcher = new ScheduleDataFetcher(activity, this);

                inflater.inflate(R.menu.schedule_actionbar, menu);

                MenuItem itemSpinner1 = menu.findItem(R.id.first_spinner);
                final Spinner firstSpinner = (Spinner) MenuItemCompat.getActionView(itemSpinner1);

                final ArrayAdapter<String> firstAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_style, classesNum);
                firstAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_style);
                firstSpinner.setAdapter(firstAdapter);

                MenuItem itemSpinner2 = menu.findItem(R.id.second_spinner);
                final Spinner secondSpinner = (Spinner) MenuItemCompat.getActionView(itemSpinner2);

                firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        List<String> sections = new ArrayList<>();
                        for (String cl : classes)
                            if (cl.contains(firstSpinner.getSelectedItem().toString()))
                                sections.add(String.valueOf(cl.charAt(1)));
                        java.util.Collections.sort(sections);

                        final ArrayAdapter<String> secondAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_style, sections);
                        secondAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_style);
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
                            scheduleDataFetcher.fetchClassSchedule(firstSpinner.getSelectedItem().toString(), secondSpinner.getSelectedItem().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                break;
            }
            case PROFS_SCHEDULE: {
                break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScheduleDataFetcher scheduleDataFetcher = new ScheduleDataFetcher(activity, this);
        try {
            scheduleDataFetcher.fetchClassesList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AHBottomNavigation bar = (AHBottomNavigation) activity.findViewById(R.id.bottom_navigation);
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
                        break;
                    }
                    case CLASSES_SCHEDULE: {
                        ((MainActivity) activity).getSupportActionBar().setTitle("Orario classe");
                        break;
                    }
                    case PROFS_SCHEDULE: {
                        ((MainActivity) activity).getSupportActionBar().setTitle("Orario docente");
                        break;
                    }
                }
                return true;
            }
        });
    }

    public void fetchClassesComplete(List<String> result) {
        ConfigurationManager.getIstance().saveClassesList(result);
    }

    public void fetchClassComplete(List<ScheduleActivity> activities, String classId) {
        ConfigurationManager.getIstance().saveSchedule(activities, classId);
        renderSchedule();
    }

    public void renderSchedule(){

        //controlla se la sezione (personale, classi, docente) è cambiata
        //se no si prende l'orario dalle shared
        //se non ce --> snackbar con errore connessione
    }
}
