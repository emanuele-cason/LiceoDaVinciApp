package davi.liceodavinci;

import android.app.Activity;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Emanuele on 31/12/2017 at 16:23.
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

public class DataFetcher {

    private CommunicationsFragment communicationsFragment;
    private final String requestUrls[] = {"http://192.168.1.5:8080/api/comunicati/studenti", "http://192.168.1.5:8080/api/comunicati/genitori", "http://192.168.1.5:8080/api/comunicati/docenti"};
    private OkHttpClient client = new OkHttpClient();
    private Activity activity;


    protected DataFetcher(CommunicationsFragment communicationsFragment, Activity activity) {
        this.communicationsFragment = communicationsFragment;
        this.activity = activity;
    }

    protected void fetchCommunicationsJson(int section) throws IOException {

        Request request = new Request.Builder()
                .url(requestUrls[section])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                fetchCommFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchCommFailed();
                        throw new IOException("Unexpected code " + response);
                    }

                    Gson gson = new Gson();
                    Communication communications[] = gson.fromJson(responseBody.string(), Communication[].class);
                    fetchCommComplete(communications);
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

    private void fetchCommComplete(final Communication[] result) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Communication comm:result) {
                    comm.setUrl(comm.getUrl().replaceAll(" ", "%20"));
                }
                communicationsFragment.fetchComplete(result);
            }
        });

    }
}
