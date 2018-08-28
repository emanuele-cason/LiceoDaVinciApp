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

        Log.d("storing loadcomm", communication.getName());

        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getCommList() == null)) {
            communications = getCommList();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    if (comm.getStatus() == Communication.DOWNLOADED) communication.setStatus(Communication.DOWNLOADED);
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

        Log.d("storing cachedel", "-");

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

    public void setCommStatus(Communication.LocalCommunication communication, int status){

        Log.d("storing commstatus", communication.getName());

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

    void setCommNotificationEnabled(int commType, boolean enabled){
        Log.d("storing data notific", String.valueOf(commType).concat(" --> ").concat(String.valueOf(enabled)));

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

        Log.d("getting data notific", String.valueOf(commType));

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

        Log.d("storing comm list", "-");

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(communications);
        prefsEditor.putString(activity.getString(R.string.stored_comm_list_key), json);
        prefsEditor.apply();
    }

    public List<Communication.LocalCommunication> getCommList() {

        Log.d("getting comm list", "-");

        String json = sharedPreferences.getString(activity.getString(R.string.stored_comm_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Communication.LocalCommunication>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveClassesList(List<Pair<Integer, String>> classes){

        Log.d("storing classes list", "-");

        List<String> classesStr = new ArrayList<>();

        for (Pair<Integer, String> classId : classes){
            classesStr.add(classId.first.toString().concat(classId.second));
        }

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(classesStr);
        prefsEditor.putString(activity.getString(R.string.stored_class_list_key), json);
        prefsEditor.apply();
    }

    public List<Pair<Integer, String>> getClassesList(){

        Log.d("getting classes list", "-");

        String json = sharedPreferences.getString(activity.getString(R.string.stored_class_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>(){}.getType();
        if (gson.fromJson(json, listType) == null) return null;

        List<Pair<Integer, String>> classes = new ArrayList<>();
        List<String> classesStr = gson.fromJson(json, listType);
        for (String classId : classesStr){
            classes.add(new Pair<Integer, String>(Integer.parseInt(String.valueOf(classId.charAt(0))), String.valueOf(classId.charAt(1))));
        }

        return classes;
    }

    public void saveSchedule(List<ScheduleEvent> scheduleActivities, Pair<Integer, String> classId){

        Log.d("storing class schedule", String.valueOf(classId.first.toString().concat(classId.second)));

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleActivities);
        prefsEditor.putString(classId.first.toString().concat(classId.second).toLowerCase(), json);
        prefsEditor.apply();
    }

    public void saveSchedule(List<ScheduleEvent> scheduleActivities, Prof prof){

        Log.d("storing prof schedule", String.valueOf(prof.getSurname()));

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleActivities);
        prefsEditor.putString((prof.getSurname().concat(prof.getName())).toLowerCase(), json);
        prefsEditor.apply();
    }

    public List<ScheduleEvent> getScheduleList(Pair<Integer, String> classId){

        Log.d("getting data notific", String.valueOf(classId.first.toString().concat(classId.second)));

        String json = sharedPreferences.getString(classId.first.toString().concat(classId.second).toLowerCase(), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public List<ScheduleEvent> getScheduleList(Prof prof){

        Log.d("getting prof schedule", String.valueOf(prof.getSurname()));

        String json = sharedPreferences.getString((prof.getSurname().concat(prof.getName())).toLowerCase(), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveProfsList (List<Prof> profsList){

        Log.d("storing profs list", "-");

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profsList);
        prefsEditor.putString(activity.getString(R.string.stored_profs_list_key), json);
        prefsEditor.apply();
    }

    public List<Prof> getProfsList(){

        Log.d("getting prof list", "-");

        String json = sharedPreferences.getString(activity.getString(R.string.stored_profs_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Prof>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveMyStatus(Pair<Integer, String> classId){

        Log.d("storing user status", "class");

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(classId);
        prefsEditor.putString(activity.getString(R.string.stored_status_key), json);
        prefsEditor.putString(activity.getString(R.string.stored_user_status_type), STATUS_STUDENT);
        prefsEditor.apply();
    }

    public void saveMyStatus(Prof prof){

        Log.d("storing user status", "prof");

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(prof);
        prefsEditor.putString(activity.getString(R.string.stored_status_key), json);
        prefsEditor.putString(activity.getString(R.string.stored_user_status_type), STATUS_PROF);
        prefsEditor.apply();
    }

    public Object getMyStatus(){

        Log.d("getting user status", "-");

        String json = sharedPreferences.getString(activity.getString(R.string.stored_status_key), "");
        if (json == null) return null;
        Gson gson = new Gson();

        if (sharedPreferences.getString(activity.getString(R.string.stored_user_status_type), "").equals(STATUS_STUDENT)){
            Type classType = new TypeToken<Pair<Integer, String>>() {
            }.getType();
            return gson.fromJson(json, classType);
        }

        if (sharedPreferences.getString(activity.getString(R.string.stored_user_status_type), "").equals(STATUS_PROF)){
            Type profType = new TypeToken<Prof>() {
            }.getType();
            return gson.fromJson(json, profType);
        }

        return null;
    }

    public void saveEvents(List<Event> events){

        Log.d("storing events", String.valueOf(events.size()));

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(events);
        prefsEditor.putString(activity.getString(R.string.stored_events), json);
        prefsEditor.apply();
    }

    public List<Event> getEvents(){

        Log.d("getting events list", "-");

        String json = sharedPreferences.getString(activity.getString(R.string.stored_events), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Event>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }
}
