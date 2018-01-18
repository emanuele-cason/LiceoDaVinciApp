package davi.liceodavinci;

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

class DataFetcher {

    private CommunicationsFragment communicationsFragment;
    private final String requestUrls[] = new String[3];
    private OkHttpClient client = new OkHttpClient();
    private Activity activity;


    DataFetcher(CommunicationsFragment communicationsFragment, Activity activity) {
        this.communicationsFragment = communicationsFragment;
        this.activity = activity;

        requestUrls[Communication.COMM_STUDENTS] = "http://www.liceodavinci.tv/api/comunicati/studenti";
        requestUrls[Communication.COMM_PARENTS] = "http://www.liceodavinci.tv/api/comunicati/genitori";
        requestUrls[Communication.COMM_PROFS] = "http://www.liceodavinci.tv/api/comunicati/docenti";
    }

    void fetchCommunicationsJson(int section) throws IOException {

        Request request = new Request.Builder()
                .url(requestUrls[section])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                fetchCommFailed();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchCommFailed();
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Communication>>(){}.getType();
                    assert responseBody != null;
                    List<Communication> communicationsAPI = gson.fromJson(responseBody.string(), listType);
                    fetchCommComplete(communicationsAPI);
                }
            }
        });
    }

    private void fetchCommFailed() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                communicationsFragment.fetchFailed();
            }
        });
    }

    private void fetchCommComplete(final List<Communication> result) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < result.size(); i++) {
                    result.get(i).setUrl(result.get(i).getUrl().replaceAll(" ", "%20"));
                }
                communicationsFragment.fetchComplete(result);
            }
        });
    }
}
