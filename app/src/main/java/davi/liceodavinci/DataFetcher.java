package davi.liceodavinci;

import org.json.JSONArray;
import org.json.JSONException;

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

public class DataFetcher {

    /*
    * Questa classe fa le richieste all'api e le restituisce sotto forma di oggetto JSON, section è il parametro
    * che specifica la sezione dei comunicati (studenti --> 0; genitori --> 1; docenti --> 2). Una volta eseguito
    * il fetch, se questo avrà funzionato verrà chiamato il metodo fetchCommComplete(JSONArray result), altrimenti
     * fetchCommFailed().
    * */

    private final String requestUrls [] = {"https://www.foaas.com/operations","http://192.168.1.5:8080/api/comunicati/genitori","http://192.168.1.5:8080/api/comunicati/docenti"};
    OkHttpClient client = new OkHttpClient();

    protected void fetchCommunicationsJson(int section) throws IOException {

        Request request = new Request.Builder()
                .url(requestUrls[section])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                fetchCommFailed();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        fetchCommFailed();
                        throw new IOException("Unexpected code " + response);
                    }

                    String thisIsOnlyATest = "[ { \"Nome\": \"49.txt\", \"Data\": \"2017-12-31T13:53:57.418588286+01:00\", \"Tipo\": \"genitori\", \"URL\": \"http://liceodavinci.tv/sitoLiceo/images/comunicati/comunicati-genitori/49.txt\" }, { \"Nome\": \"84.txt\", \"Data\": \"2017-12-31T13:53:57.418588286+01:00\", \"Tipo\": \"genitori\", \"URL\": \"http://liceodavinci.tv/sitoLiceo/images/comunicati/comunicati-genitori/84.txt\" } ]";

                    JSONArray jsonResponse = null;
                    try {
                        jsonResponse = new JSONArray(thisIsOnlyATest/*responseBody.string()*/);
                    } catch (JSONException e) {
                        fetchCommFailed();
                    }

                    fetchCommComplete(jsonResponse);
                }
            }
        });
    }

    private void fetchCommFailed(){

    }

    private void fetchCommComplete(JSONArray result){

    }
}
