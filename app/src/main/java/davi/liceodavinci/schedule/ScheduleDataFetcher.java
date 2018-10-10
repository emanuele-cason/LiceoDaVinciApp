package davi.liceodavinci.schedule;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.OnFetchCompleteListener;
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

    void fetchProfsList(final OnFetchCompleteListener<List<Prof>> callback) {

        Request request = new Request.Builder()
                .url(requestUrls[GET_PROFS])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Unexpected code " + response));
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Prof>>() {
                    }.getType();
                    assert responseBody != null;
                    List<Prof> profsListAPI = gson.fromJson(responseBody.string(), listType);

                    callback.onSuccess(profsListAPI);
                } catch (Exception e) {
                    Snackbar snackbar = Snackbar
                            .make(activity.findViewById(R.id.main_frame), "Oh oh! Il server ha qualcosa che non va :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    void fetchProfSchedule(final Prof prof, final OnFetchCompleteListener<List<ScheduleEvent>> callback) {

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
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Unexpected code " + response));
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
                    }.getType();
                    assert responseBody != null;
                    List<ScheduleEvent> profScheduleAPI = gson.fromJson(responseBody.string(), listType);
                    callback.onSuccess(profScheduleAPI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void fetchClassesList(final OnFetchCompleteListener<List<Pair<Integer, String>>> callback) {

        Request request = new Request.Builder()
                .url(requestUrls[GET_CLASSES])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Unexpected code " + response));
                    }

                    Gson gson = new Gson();
                    assert response.body() != null;

                    Type listType = new TypeToken<List<String>>() {
                    }.getType();
                    List<Pair<Integer, String>> classListAPI = new ArrayList<>();
                    List<String> classesStr = gson.fromJson(response.body().string(), listType);
                    for (String classId : classesStr) {
                        classListAPI.add(new Pair<Integer, String>(Integer.parseInt(String.valueOf(classId.charAt(0))), String.valueOf(classId.charAt(1))));
                    }

                    callback.onSuccess(classListAPI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void fetchClassSchedule(final Pair<Integer, String> classId, final OnFetchCompleteListener<List<ScheduleEvent>> callback) {

        Request request = new Request.Builder()
                .url(requestUrls[GET_CLASS].concat(classId.first.toString()).concat(classId.second))
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Unexpected code " + response));
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<ScheduleEvent>>() {
                    }.getType();
                    assert responseBody != null;
                    List<ScheduleEvent> classScheduleAPI = gson.fromJson(responseBody.string(), listType);
                    callback.onSuccess(classScheduleAPI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}