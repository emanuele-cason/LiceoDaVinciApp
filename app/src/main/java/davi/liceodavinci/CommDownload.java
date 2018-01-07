package davi.liceodavinci;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class CommDownload extends AsyncTask<Communication, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog progressDialog;
    private boolean permanent;
    private SwipeRefreshLayout layout;

    public CommDownload(Context context, SwipeRefreshLayout layout, ProgressDialog progressDialog, boolean permanent) {
        this.context = context;
        this.progressDialog = progressDialog;
        this.permanent = permanent;
        this.layout = layout;
    }

    @Override
    protected String doInBackground(Communication... comms) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(comms[0].getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();

            String path;
            if (permanent)
                path = context.getFilesDir().getPath().concat("/").concat(comms[0].getName());
            else path = context.getCacheDir().getPath().concat("/").concat(comms[0].getName());

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
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        progressDialog.show();
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
        if (permanent) {
            Snackbar snackbar = Snackbar
                    .make(layout, "Il comunicato è stato salvato offline", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void downloadFailed() {
        Snackbar snackbar = Snackbar
                .make(layout, "Impossibile salvare il comunicato", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}