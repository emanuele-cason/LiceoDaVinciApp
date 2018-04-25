package davi.liceodavinci.schedule;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.ConfigurationManager;
import davi.liceodavinci.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Emanuele on 31/12/2017 at 16:23 at 20:23!
 */

/*
    * Questa classe fa le richieste all'api e le restituisce sotto forma di oggetto JSON, section è il parametro
    * che specifica la sezione dei comunicati (studenti --> 0; genitori --> 1; docenti --> 2). Una volta eseguito
    * il fetch, se questo avrà funzionato verrà chiamato il metodo fetchCommComplete(JSONArray result), altrimenti
     * fetchCommFailed().
     *
     * La stringa thisIsOnlyATest è una prova, rappresenta un Json sotto forma di stringa, esempio di array restituito
     * dall'API. Da rimuovere e rimpiazzare con la stringa commentata (responseBody.string() --> il vero risultato dell'api).
    * */

class ScheduleDataFetcher {

    private ScheduleFragment scheduleFragment;
    private final String requestUrls[] = new String[4];
    private OkHttpClient client = new OkHttpClient();
    private Activity activity;

    private final int GET_CLASSES = 0;
    private final int GET_CLASS = 1;
    private final int GET_PROFS = 2;
    private final int GET_PROF = 3;

    ScheduleDataFetcher(Activity activity, ScheduleFragment scheduleFragment) {
        this.activity = activity;
        this.scheduleFragment = scheduleFragment;

        requestUrls[GET_CLASSES] = "http://www.liceodavinci.tv/api/classi";
        requestUrls[GET_CLASS] = "http://www.liceodavinci.tv/api/orario/classe/";
        requestUrls[GET_PROFS] = "http://www.liceodavinci.tv/api/docenti";
        requestUrls[GET_PROF] = "http://www.liceodavinci.tv/api/orario/docente";
    }

    void fetchProfsList() throws Exception {

        Log.d("fetching", "profs list");

        Request request = new Request.Builder()
                .url(requestUrls[GET_PROFS])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                fetchProfsFailed();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchProfsFailed();
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Prof>>() {
                    }.getType();
                    assert responseBody != null;
                    List<Prof> profsListAPI = gson.fromJson(responseBody.string(), listType);
                    fetchProfsComplete(profsListAPI);
                }
            }
        });
    }

    void fetchProfSchedule(final Prof prof) throws Exception {

        Log.d("fetching prof schedule", prof.getSurname());

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\"nome\": " + "\"" + prof.getName() + "\"" + ", \"cognome\": " + "\"" + prof.getSurname() + "\"" + "}");
        Request request = new Request.Builder()
                .url(requestUrls[GET_PROF])
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                fetchProfFailed(prof);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchProfFailed(prof);
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
                    }.getType();
                    assert responseBody != null;
                    List<ScheduleEvent> profScheduleAPI = gson.fromJson(responseBody.string(), listType);
                    fetchProfComplete(profScheduleAPI, prof);
                }
            }
        });
    }

    void fetchClassesList() throws Exception {

        Log.d("fetching", "classes list");

        Request request = new Request.Builder()
                .url(requestUrls[GET_CLASSES])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                fetchClassesFailed();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchClassesFailed();
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    assert response.body() != null;

                    Type listType = new TypeToken<List<String>>(){}.getType();
                    List<Pair<Integer,String>> classListAPI = new ArrayList<>();
                    List<String> classesStr = gson.fromJson(response.body().string(), listType);
                    for (String classId : classesStr){
                        classListAPI.add(new Pair<Integer, String>(Integer.parseInt(String.valueOf(classId.charAt(0))), String.valueOf(classId.charAt(1))));
                    }

                    fetchClassesComplete(classListAPI);
                }
            }
        });
    }

    void fetchClassSchedule(final Pair<Integer,String> classId) throws IOException {

        Log.d("fetching class schedule", classId.first.toString().concat(classId.second));

        Request request = new Request.Builder()
                .url(requestUrls[GET_CLASS].concat(classId.first.toString()).concat(classId.second))
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                fetchClassFailed(classId);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchClassFailed(classId);
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
                    }.getType();
                    assert responseBody != null;
                    List<ScheduleEvent> classScheduleAPI = gson.fromJson(responseBody.string(), listType);
                    fetchClassComplete(classScheduleAPI, classId);
                }
            }
        });
    }

    private void fetchClassesComplete(final List<Pair<Integer,String>> result) {

        if (ConfigurationManager.getIstance().getClassesList() == null && result != null) {
            ConfigurationManager.getIstance().saveClassesList(result);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.invalidateOptionsMenu();
                }
            });
        }

        ConfigurationManager.getIstance().saveClassesList(result);
    }

    private void fetchClassesFailed(){
        if (ConfigurationManager.getIstance().getClassesList() == null) {
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void fetchProfsComplete(final List<Prof> result) {

        if (ConfigurationManager.getIstance().getProfsList() == null) {
            ConfigurationManager.getIstance().saveProfsList(result);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scheduleFragment.prepareProfsSelector(result);
                }
            });
        }

        ConfigurationManager.getIstance().saveProfsList(result);
    }

    private void fetchProfsFailed(){
        if (ConfigurationManager.getIstance().getProfsList() == null) {
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void fetchClassComplete(final List<ScheduleEvent> result, final Pair<Integer,String> classId) {

        if (ConfigurationManager.getIstance().getScheduleList(classId) == null) {
            ConfigurationManager.getIstance().saveSchedule(result, classId);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scheduleFragment.renderSchedule(classId);
                }
            });
        }

        ConfigurationManager.getIstance().saveSchedule(result, classId);
    }

    private void fetchClassFailed(final Pair<Integer,String> classId) {

        if (ConfigurationManager.getIstance().getScheduleList(classId) == null) {
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG)
                    .setAction("RIPROVA", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                fetchClassSchedule(classId);
                            } catch (IOException e) {
                                fetchClassFailed(classId);
                            }
                        }
                    });
            snackbar.show();
        }
    }

    private void fetchProfComplete(final List<ScheduleEvent> result, final Prof prof) {

        if (ConfigurationManager.getIstance().getScheduleList(prof) == null) {
            ConfigurationManager.getIstance().saveSchedule(result, prof);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scheduleFragment.renderSchedule(prof);
                }
            });
        }

        ConfigurationManager.getIstance().saveSchedule(result, prof);
    }

    private void fetchProfFailed(final Prof prof) {

        if (ConfigurationManager.getIstance().getScheduleList(prof) == null) {
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG)
                    .setAction("RIPROVA", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                fetchProfSchedule(prof);
                            } catch (Exception e) {
                                fetchProfFailed(prof);
                            }
                        }
                    });
            snackbar.show();
        }
    }
}