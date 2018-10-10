package davi.liceodavinci.agenda;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by Emanuele on 02/06/2018 at 17:45!
 */

public class AgendaDataFetcher {

    private AgendaFragment agendaFragment;
    private final String requestUrl;
    private OkHttpClient client = new OkHttpClient();
    private Activity activity;

    AgendaDataFetcher(Activity activity, AgendaFragment agendaFragment) {
        this.activity = activity;
        this.agendaFragment = agendaFragment;

        requestUrl = "http://www.liceodavinci.tv/api/agenda";
    }

    void fetchEvents(List<String> titleFilter,
                     List<String> contentFilter,
                     int before,
                     int after,
                     final OnFetchCompleteListener <List<Event>> callback) throws JSONException {

        JSONObject body = new JSONObject();
        body.put("filtri_titolo", titleFilter);
        body.put("filtri_contenuto", contentFilter);
        body.put("prima", before);
        body.put("dopo", after);

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, body.toString());

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
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
                    Type listType = new TypeToken<ArrayList<Event>>() {
                    }.getType();
                    assert responseBody != null;
                    List<Event> eventList = gson.fromJson(responseBody.string(), listType);

                    callback.onSuccess(eventList);
                } catch (Exception e) {
                    Snackbar snackbar = Snackbar
                            .make(activity.findViewById(R.id.main_frame), "Oh oh! Il server ha qualcosa che non va :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}
