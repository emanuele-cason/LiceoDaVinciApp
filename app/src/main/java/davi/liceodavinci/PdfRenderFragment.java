package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

/**
 * Created by Emanuele on 07/01/2018 at 17:57 at 20:21!
 */

@SuppressLint("ValidFragment")
public class PdfRenderFragment extends Fragment {

    private Activity activity;
    private Communication.CommunicationStored communication;

    @SuppressLint("ValidFragment")
    protected PdfRenderFragment(Activity activity, Communication.CommunicationStored communication) {
        this.activity = activity;
        this.communication = communication;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.comm_pdf_viewer_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pdf_render_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (communication.getStatus() == Communication.CACHED) {
            PDFView pdfView = activity.findViewById(R.id.pdf_render);
            pdfView.fromFile(new File(activity.getCacheDir().getPath().concat("/").concat(communication.getName())))
                    .enableDoubletap(true)
                    .swipeHorizontal(true)
                    .load();
        }

        if (communication.getStatus() == Communication.DOWNLOADED) {
            final PDFView pdfView = activity.findViewById(R.id.pdf_render);
            pdfView.fromFile(new File(activity.getFilesDir().getPath().concat("/").concat(communication.getName())))
                    .enableDoubletap(true)
                    .swipeHorizontal(true)
                    .load();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.pdf_menu_share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("Liceo \"L. Da Vinci\" - Comunicato %d: %s", communication.getId(), communication.getUrl()));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        if (item.getItemId() == R.id.pdf_menu_open_with){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(activity.getCacheDir().getPath().concat("/").concat(communication.getName()))), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                Snackbar snackbar = Snackbar
                        .make(activity.getCurrentFocus(), "Nessuna applicazione installata può aprire questo tipo di file", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        return false;
    }
}
