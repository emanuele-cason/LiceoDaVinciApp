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

public class ConfigurationManager {
    private static ConfigurationManager configurationManager;
    private SharedPreferences sharedPreferences;
    private Activity activity;

    private final String STORED_COMM_KEY = "comm-stored-list";

    ConfigurationManager(Activity activity) {
        this.activity = activity;
        configurationManager = this;
        this.sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    protected static ConfigurationManager getIstance() {
        return configurationManager;
    }

    protected void addCommunication(Communication.CommunicationStored communication) {

        List<Communication.CommunicationStored> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.CommunicationStored comm : communications) {
                if (comm.getName().equals(communication.getName())) return;
            }

        }

        communications.add(communication);
        saveJSONFromList(communications);
    }

    protected void removeCommunication(Communication.CommunicationStored communication) {

        List<Communication.CommunicationStored> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.CommunicationStored comm : communications) {
                if (comm.getName().equals(communication.getName())) communications.remove(comm);
            }
        }
    }

    protected void setCommSeen(Communication.CommunicationStored communication, boolean seen) {

        List<Communication.CommunicationStored> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.CommunicationStored comm : communications) {
                if (comm.getName().equals(communication.getName())) comm.setSeen(seen);
            }
            saveJSONFromList(communications);
        }
    }

    private void saveJSONFromList(List<Communication.CommunicationStored> communications) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(communications);
        prefsEditor.putString(STORED_COMM_KEY, json);
        prefsEditor.apply();
    }

    protected List<Communication.CommunicationStored> getListFromSavedJSON() {
        String json = sharedPreferences.getString(STORED_COMM_KEY, "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Communication.CommunicationStored>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }
}
