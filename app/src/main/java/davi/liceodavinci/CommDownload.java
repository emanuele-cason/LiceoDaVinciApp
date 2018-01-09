package davi.liceodavinci;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class CommDownload extends AsyncTask<Communication, Integer, String> {

    private Activity activity;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog progressDialog;
    private boolean permanent;
    private SwipeRefreshLayout layout;
    private Communication comm;

    public CommDownload(Activity activity, SwipeRefreshLayout layout, ProgressDialog progressDialog, boolean permanent) {
        this.activity = activity;
        this.progressDialog = progressDialog;
        this.permanent = permanent;
        this.layout = layout;
    }

    @Override
    protected String doInBackground(Communication... comms) {
        this.comm = comms[0];
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
                path = activity.getFilesDir().getPath().concat("/").concat(comms[0].getName());
            else path = activity.getCacheDir().getPath().concat("/").concat(comms[0].getName());

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
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
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
        }else {
            ((FragmentActivity)activity)
                    .getSupportFragmentManager()
                    .beginTransaction().addToBackStack("pdf-render")
                    .replace(R.id.empty_frame, new PdfRenderFragment(activity, comm.getName(), Communication.CACHED))
                    .commit();
        }
    }

    private void downloadFailed() {
        Snackbar snackbar = Snackbar
                .make(layout, "Impossibile salvare il comunicato", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}