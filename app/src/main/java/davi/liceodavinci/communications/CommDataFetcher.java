package davi.liceodavinci.communications;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import davi.liceodavinci.OnFetchCompleteListener;
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
    * */

public class CommDataFetcher {

    private final String requestUrls[] = new String[3];
    private OkHttpClient client = new OkHttpClient();

    CommDataFetcher() {

        requestUrls[Communication.COMM_STUDENTS] = "http://www.liceodavinci.tv/api/comunicati/studenti";
        requestUrls[Communication.COMM_PARENTS] = "http://www.liceodavinci.tv/api/comunicati/genitori";
        requestUrls[Communication.COMM_PROFS] = "http://www.liceodavinci.tv/api/comunicati/docenti";
    }

    public void fetchCommunicationsJson(int section, final OnFetchCompleteListener<List<Communication>> callback) throws IOException {

        Request request = new Request.Builder()
                .url(requestUrls[section])
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Unexpected code " + response));
                    }else{
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Communication>>(){}.getType();
                        assert responseBody != null;
                        List<Communication> communicationsAPI = gson.fromJson(responseBody.string(), listType);

                        for(Communication comm : communicationsAPI)if(!comm.getName().endsWith(".pdf"))communicationsAPI.remove(comm);
                        callback.onSuccess(communicationsAPI);
                    }
                }catch (Exception ignored){}
            }
        });
    }
}
