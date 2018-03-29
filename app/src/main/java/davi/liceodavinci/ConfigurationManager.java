package davi.liceodavinci;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.communications.Communication;
import davi.liceodavinci.schedule.Prof;
import davi.liceodavinci.schedule.ScheduleEvent;


/**
 * Created by Emanuele on 16/01/2018 at 17:24!
 */

public class ConfigurationManager {
    private static ConfigurationManager configurationManager;
    private SharedPreferences sharedPreferences;
    private Activity activity;

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

        if (!(getCommListFromSavedJSON() == null)) {
            communications = getCommListFromSavedJSON();
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

        if (!(getCommListFromSavedJSON() == null)) {
            communications = getCommListFromSavedJSON();
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

        if (!(getCommListFromSavedJSON() == null)) {
            communications = getCommListFromSavedJSON();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getStatus() == Communication.CACHED) {
                    comm.setStatus(Communication.REMOTE);
                }
            }
        }

        saveCommJSONFromList(communications);
    }

    public void setCommStatus(Communication.LocalCommunication communication, int status){
        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getCommListFromSavedJSON() == null)) {
            communications = getCommListFromSavedJSON();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    comm.setStatus(status);
                    break;
                }
            }
        }

        saveCommJSONFromList(communications);
    }

    public boolean getCommNotificationEnabled(int commType) {
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

    public List<Communication.LocalCommunication> getCommListFromSavedJSON() {
        String json = sharedPreferences.getString(activity.getString(R.string.stored_comm_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Communication.LocalCommunication>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveClassesList(List<String> classes){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(classes);
        prefsEditor.putString(activity.getString(R.string.stored_class_list_key), json);
        prefsEditor.apply();
    }

    public List<String> getClassesListFromSavedJSON(){
        String json = sharedPreferences.getString(activity.getString(R.string.stored_class_list_key), "");
        Gson gson = new Gson();
        return gson.fromJson(json, List.class);
    }

    public void saveSchedule(List<ScheduleEvent> scheduleActivities, String key){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleActivities);
        prefsEditor.putString(key.toLowerCase(), json);
        prefsEditor.apply();
    }

    public void saveSchedule(List<ScheduleEvent> scheduleActivities, Prof prof){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleActivities);
        prefsEditor.putString((prof.getSurname().concat(prof.getName())).toLowerCase(), json);
        prefsEditor.apply();
    }

    public List<ScheduleEvent> getScheduleListFromSavedJSON(String key){
        String json = sharedPreferences.getString(key.toLowerCase(), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public List<ScheduleEvent> getScheduleListFromSavedJSON(Prof prof){
        String json = sharedPreferences.getString((prof.getSurname().concat(prof.getName())).toLowerCase(), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public void saveProfsList (List<Prof> profsList){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profsList);
        prefsEditor.putString(activity.getString(R.string.stored_profs_list_key), json);
        prefsEditor.apply();
    }

    public List<Prof> getProfsListFromSavedJSON(){
        String json = sharedPreferences.getString(activity.getString(R.string.stored_profs_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Prof>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }
}
