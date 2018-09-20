package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.agenda.Event;
import davi.liceodavinci.communications.Communication;
import davi.liceodavinci.schedule.Prof;
import davi.liceodavinci.schedule.ScheduleEvent;


/**
 * Created by Emanuele on 16/01/2018 at 17:24!
 */

public class ConfigurationManager {
    @SuppressLint("StaticFieldLeak")
    private static ConfigurationManager configurationManager;
    private SharedPreferences sharedPreferences;
    private Activity activity;

    private final String STATUS_STUDENT = "student";
    private final String STATUS_PROF = "prof";

    ConfigurationManager(Activity activity) {
        this.activity = activity;
        configurationManager = this;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public static ConfigurationManager getIstance() {
        return configurationManager;
    }

    public void loadCommunication(Communication.LocalCommunication communication) {

        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getCommList() == null)) {
            communications = getCommList();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    if (comm.getStatus() == Communication.DOWNLOADED)
                        communication.setStatus(Communication.DOWNLOADED);
                    communications.remove(communications.indexOf(comm));
                    break;
                }
            }
        }

        communications.add(communication);
        saveCommJSONFromList(communications);
    }

    void removeCommunication(Communication.LocalCommunication communication) {
        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getCommList() == null)) {
            communications = getCommList();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    communications.remove(comm);
                    break;
                }
            }
        }

        saveCommJSONFromList(communications);
    }

    void setCacheDeleted() {

        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getCommList() == null)) {
            communications = getCommList();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getStatus() == Communication.CACHED) {
                    comm.setStatus(Communication.REMOTE);
                }
            }
        }

        saveCommJSONFromList(communications);
    }

    public void setCommStatus(Communication.LocalCommunication communication, int status) {

        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getCommList() == null)) {
            communications = getCommList();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    comm.setStatus(status);
                    break;
                }
            }
        }

        saveCommJSONFromList(communications);
    }

    void setCommNotificationEnabled(int commType, boolean enabled) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();

        switch (commType) {
            case Communication.COMM_STUDENTS: {
                prefsEditor.putBoolean(activity.getString(R.string.notifications_enabled_comm_students), enabled);
            }
            case Communication.COMM_PARENTS: {
                prefsEditor.putBoolean(activity.getString(R.string.notifications_enabled_comm_parents), enabled);
            }
            case Communication.COMM_PROFS: {
                prefsEditor.putBoolean(activity.getString(R.string.notifications_enabled_comm_profs), enabled);
            }
        }

        prefsEditor.apply();
    }

    boolean isNotificationEnabled(int commType) {

        switch (commType) {
            case Communication.COMM_STUDENTS: {
                return sharedPreferences.getBoolean(activity.getString(R.string.notifications_enabled_comm_students), false);
            }
            case Communication.COMM_PARENTS: {
                return sharedPreferences.getBoolean(activity.getString(R.string.notifications_enabled_comm_parents), false);
            }
            case Communication.COMM_PROFS: {
                return sharedPreferences.getBoolean(activity.getString(R.string.notifications_enabled_comm_profs), false);
            }
        }

        return false;
    }

    private void saveCommJSONFromList(List<Communication.LocalCommunication> communications) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(communications);
        prefsEditor.putString(activity.getString(R.string.stored_comm_list_key), json);
        prefsEditor.apply();
    }

    public List<Communication.LocalCommunication> getCommList() {

        String json = sharedPreferences.getString(activity.getString(R.string.stored_comm_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Communication.LocalCommunication>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveClassesList(List<Pair<Integer, String>> classes) {

        List<String> classesStr = new ArrayList<>();

        for (Pair<Integer, String> classId : classes) {
            classesStr.add(classId.first.toString().concat(classId.second));
        }

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(classesStr);
        prefsEditor.putString(activity.getString(R.string.stored_class_list_key), json);
        prefsEditor.apply();
    }

    public List<Pair<Integer, String>> getClassesList() {

        String json = sharedPreferences.getString(activity.getString(R.string.stored_class_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        if (gson.fromJson(json, listType) == null) return null;

        List<Pair<Integer, String>> classes = new ArrayList<>();
        List<String> classesStr = gson.fromJson(json, listType);
        for (String classId : classesStr) {
            classes.add(new Pair<Integer, String>(Integer.parseInt(String.valueOf(classId.charAt(0))), String.valueOf(classId.charAt(1))));
        }

        return classes;
    }

    public void saveSchedule(List<ScheduleEvent> scheduleActivities, Pair<Integer, String> classId, boolean overrideCustomization) {

        List<ScheduleEvent> scheduleEvents = scheduleActivities;

        if (getScheduleList(classId) != null) {
            int i = 0;
            for (ScheduleEvent newEvent : scheduleEvents) {
                for (ScheduleEvent oldEvent : getScheduleList(classId)) {
                    if (oldEvent.getId() == newEvent.getId() && !overrideCustomization)
                        scheduleEvents.set(i, oldEvent);
                }
                i++;
            }
        }

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleActivities);
        prefsEditor.putString(classId.first.toString().concat(classId.second).toLowerCase(), json);
        prefsEditor.apply();
    }

    public void saveSchedule(List<ScheduleEvent> scheduleActivities, Prof prof, boolean overrideCustomization) {

        List<ScheduleEvent> scheduleEvents = scheduleActivities;

        if (getScheduleList(prof) != null){
            int i = 0;
            for (ScheduleEvent newEvent : scheduleEvents) {
                for (ScheduleEvent oldEvent : getScheduleList(prof)) {
                    if (oldEvent.getId()== newEvent.getId() && !overrideCustomization)
                        scheduleEvents.set(i, oldEvent);
                }
                i++;
            }
        }

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleActivities);
        prefsEditor.putString((prof.getSurname().concat(prof.getName())).toLowerCase(), json);
        prefsEditor.apply();
    }

    public void editSchedule(Pair<Integer, String> classId, ScheduleEvent oldEvent, ScheduleEvent newEvent) {
        List<ScheduleEvent> scheduleEvents = new ArrayList<>(getScheduleList(classId));

        int i = 0;
        for (ScheduleEvent event : getScheduleList(classId)) {

            if (event.getId()== oldEvent.getId()) {
                Log.d(event.getSubject(), "woo");
                scheduleEvents.set(i, newEvent);
            }
            i++;
        }

        saveSchedule(scheduleEvents, classId, true);
    }

    public void editSchedule(Prof prof, ScheduleEvent oldEvent, ScheduleEvent newEvent) {
        List<ScheduleEvent> scheduleEvents = getScheduleList(prof);

        int i = 0;
        for (ScheduleEvent event : getScheduleList(prof)) {

            if (event.getId()== oldEvent.getId()) {
                Log.d(event.getSubject(), "woo");
                scheduleEvents.set(i, newEvent);
            }
            i++;
        }

        saveSchedule(scheduleEvents, prof, true);
    }

    public List<ScheduleEvent> getScheduleList(Pair<Integer, String> classId) {

        String json = sharedPreferences.getString(classId.first.toString().concat(classId.second).toLowerCase(), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public List<ScheduleEvent> getScheduleList(Prof prof) {

        String json = sharedPreferences.getString((prof.getSurname().concat(prof.getName())).toLowerCase(), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveProfsList(List<Prof> profsList) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profsList);
        prefsEditor.putString(activity.getString(R.string.stored_profs_list_key), json);
        prefsEditor.apply();
    }

    public List<Prof> getProfsList() {

        String json = sharedPreferences.getString(activity.getString(R.string.stored_profs_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Prof>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveMyStatus(Pair<Integer, String> classId) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(classId);
        prefsEditor.putString(activity.getString(R.string.stored_status_key), json);
        prefsEditor.putString(activity.getString(R.string.stored_user_status_type), STATUS_STUDENT);
        prefsEditor.apply();
    }

    public void saveMyStatus(Prof prof) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(prof);
        prefsEditor.putString(activity.getString(R.string.stored_status_key), json);
        prefsEditor.putString(activity.getString(R.string.stored_user_status_type), STATUS_PROF);
        prefsEditor.apply();
    }

    public Object getMyStatus() {

        String json = sharedPreferences.getString(activity.getString(R.string.stored_status_key), "");
        if (json == null) return null;
        Gson gson = new Gson();

        if (sharedPreferences.getString(activity.getString(R.string.stored_user_status_type), "").equals(STATUS_STUDENT)) {
            Type classType = new TypeToken<Pair<Integer, String>>() {
            }.getType();
            return gson.fromJson(json, classType);
        }

        if (sharedPreferences.getString(activity.getString(R.string.stored_user_status_type), "").equals(STATUS_PROF)) {
            Type profType = new TypeToken<Prof>() {
            }.getType();
            return gson.fromJson(json, profType);
        }

        return null;
    }

    public void saveEvents(List<Event> events) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(events);
        prefsEditor.putString(activity.getString(R.string.stored_events), json);
        prefsEditor.apply();
    }

    public List<Event> getEvents() {

        String json = sharedPreferences.getString(activity.getString(R.string.stored_events), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Event>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public String getStartupFragment() {

        return sharedPreferences.getString("startup_fragment", "0");
    }
}
