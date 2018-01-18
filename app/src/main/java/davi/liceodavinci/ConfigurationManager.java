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

    private final String STORED_COMM_KEY = "comm-stored-list";

    ConfigurationManager(Activity activity) {
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
                if (comm.getName().equals(communication.getName())){
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

    void setCacheDeleted(){
        List<Communication.LocalCommunication> communications = new ArrayList<>();

        if (!(getListFromSavedJSON() == null)) {
            communications = getListFromSavedJSON();
            for (Communication.LocalCommunication comm : communications) {
                if (comm.getStatus()==Communication.CACHED){
                    comm.setStatus(Communication.REMOTE);
                }
            }
        }

        saveJSONFromList(communications);
    }

    private void saveJSONFromList(List<Communication.LocalCommunication> communications) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(communications);
        prefsEditor.putString(STORED_COMM_KEY, json);
        prefsEditor.apply();
    }

    List<Communication.LocalCommunication> getListFromSavedJSON() {
        String json = sharedPreferences.getString(STORED_COMM_KEY, "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Communication.LocalCommunication>>() {}.getType();

        return gson.fromJson(json, listType);
    }
}
