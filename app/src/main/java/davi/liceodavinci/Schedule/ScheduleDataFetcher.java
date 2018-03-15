package davi.liceodavinci.Schedule;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    private final String requestUrls[] = new String[3];
    private OkHttpClient client = new OkHttpClient();
    private Activity activity;

    private final int GET_CLASSES = 0;
    private final int GET_CLASS = 1;
    private final int GET_PROFS = 2;

    ScheduleDataFetcher(Activity activity, ScheduleFragment scheduleFragment) {
        this.activity = activity;
        this.scheduleFragment = scheduleFragment;

        requestUrls[GET_CLASSES] = "http://www.liceodavinci.tv/api/classi";
        requestUrls[GET_CLASS] = "http://www.liceodavinci.tv/api/orario/classe/";
        requestUrls[GET_PROFS] = "http://www.liceodavinci.tv/api/docenti";
    }

    void fetchClassesList() throws Exception {
        Request request = new Request.Builder()
                .url(requestUrls[GET_CLASSES])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    assert response.body() != null;
                    List<String> classListAPI = gson.fromJson(response.body().string(), List.class);
                    fetchClassesComplete(classListAPI);
                }
            }
        });
    }

    void fetchClassSchedule(final String classNum, final String classSection) throws IOException {

        Request request = new Request.Builder()
                .url(requestUrls[GET_CLASS].concat(classNum).concat(classSection))
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                fetchClassFailed(classNum.concat(classSection));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchClassFailed(classNum.concat(classSection));
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<ScheduleActivity>>() {
                    }.getType();
                    assert responseBody != null;
                    List<ScheduleActivity> classScheduleAPI = gson.fromJson(responseBody.string(), listType);
                    fetchClassComplete(classScheduleAPI, classNum.concat(classSection));
                }
            }
        });
    }

    private void fetchClassComplete(final List<ScheduleActivity> result, final String classId) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scheduleFragment.fetchClassComplete(result, classId);
            }
        });
    }

    private void fetchClassFailed(final String classId){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scheduleFragment.renderSchedule(classId);
            }
        });
    }

    private void fetchClassesComplete(final List<String> result) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scheduleFragment.fetchClassesComplete(result);
            }
        });
    }
}
