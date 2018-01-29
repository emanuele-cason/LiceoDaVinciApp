package davi.liceodavinci;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Emanuele on 16/01/2018 at 17:24!
 */

class ConfigurationManager {
    private static ConfigurationManager configurationManager;
    private SharedPreferences sharedPreferences;
    private Activity activity;

    ConfigurationManager(Activity activity) {
        this.activity = activity;
        configurationManager = this;
        this.sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    static ConfigurationManager getIstance() {
        return configurationManager;
    }

    void loadCommunication(Communication.LocalCommunication communication) {
        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    communications.remove(communications.indexOf(comm));
                    break;
                }

            }

        }

        communications.add(communication);
        saveJSONFromList(communications);
    }

    void removeCommunication(Communication.LocalCommunication communication) {
        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    communications.remove(comm);
                    break;
                }
            }
        }

        saveJSONFromList(communications);
    }

    void setCacheDeleted() {
        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getStatus() == Communication.CACHED) {
                    comm.setStatus(Communication.REMOTE);
                }
            }
        }

        saveJSONFromList(communications);
    }

    void setCommStatus(Communication.LocalCommunication communication, int status){
        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getName().equals(communication.getName())) {
                    comm.setStatus(status);
                    break;
                }
            }
        }

        saveJSONFromList(communications);
    }

    boolean getCommNotificationEnabled(int commType) {
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

    private void saveJSONFromList(List<Communication.LocalCommunication> communications) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(communications);
        prefsEditor.putString(activity.getString(R.string.stored_comm_list_key), json);
        prefsEditor.apply();
    }

    List<Communication.LocalCommunication> getListFromSavedJSON() {
        String json = sharedPreferences.getString(activity.getString(R.string.stored_comm_list_key), "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Communication.LocalCommunication>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }
}
