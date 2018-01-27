package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static davi.liceodavinci.Communication.CACHED;
import static davi.liceodavinci.Communication.DOWNLOADED;

class CommDownload extends AsyncTask<Void, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog progressDialog;
    private int savingMode;
    private boolean openOnFinish;
    private Communication.LocalCommunication communication;

    static final int DOWNLOAD = DOWNLOADED;
    static final int CACHE = CACHED;

    CommDownload(Activity activity, Communication.LocalCommunication communication, int savingMode, boolean openOnFinish) {
        this.activity = activity;
        this.savingMode = savingMode;
        this.openOnFinish = openOnFinish;
        this.communication = communication;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire(30*1000L /*10 minutes*/);

        progressDialog = new ProgressDialog(activity);

        if (savingMode == CACHE) progressDialog.setMessage("Apertura in corso...");
        else if (savingMode == DOWNLOAD)progressDialog.setMessage("Download in corso...");

        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });

        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {

        if (communication.getStatus() == savingMode){
            progressDialog.dismiss();
            return null;
        }

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(communication.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();

            String path;
            if (savingMode == DOWNLOAD) {
                path = new File(activity.getFilesDir(),communication.getName()).getPath();
                this.communication.setStatus(DOWNLOADED);
            } else {
                if (savingMode != CACHE) Log.d("Errore", "Il valore di savingMode non è valido");
                path = new File(activity.getCacheDir(),communication.getName()).getPath();
                this.communication.setStatus(CACHED);
            }

            File file = new File(path);

            input = connection.getInputStream();
            output = new FileOutputStream(file);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        progressDialog.dismiss();
        if (result != null)
            downloadFailed();
        else
            downloadComplete();
    }

    private void downloadComplete() {
        if (!openOnFinish) {
            if (savingMode == DOWNLOAD) {
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.main_frame), "Il comunicato è stato salvato offline", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else {
            ((FragmentActivity) activity)
                    .getFragmentManager()
                    .beginTransaction().addToBackStack("pdf-render")
                    .replace(R.id.main_frame, new PdfRenderFragment(activity, communication))
                    .commit();
        }

        ConfigurationManager.getIstance().loadCommunication(communication);
    }

    private void downloadFailed() {
        communication.setStatus(Communication.REMOTE);
        ConfigurationManager.getIstance().loadCommunication(communication);
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(R.id.main_frame), "Impossibile salvare il comunicato", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}