package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

/**
 * Created by Emanuele on 07/01/2018 at 17:57 at 20:21!
 */

@SuppressLint("ValidFragment")
public class PdfRenderFragment extends Fragment{

    private Activity activity;
    private String fileName;
    private int savingMode;

    @SuppressLint("ValidFragment")
    protected PdfRenderFragment(Activity activity, String fileName, int savingMode){
        this.activity = activity;
        this.fileName = fileName;
        this.savingMode = savingMode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pdf_render_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savingMode == Communication.CACHED){
            PDFView pdfView = activity.findViewById(R.id.pdf_render);
            pdfView.fromFile(new File(activity.getCacheDir().getPath().concat("/").concat(fileName)))
                    .enableDoubletap(true)
                    .swipeHorizontal(true)
                    .load();
        }

        if (savingMode == Communication.DOWNLOADED){
            final PDFView pdfView = activity.findViewById(R.id.pdf_render);
            pdfView.fromFile(new File(activity.getFilesDir().getPath().concat("/").concat(fileName)))
                    .enableDoubletap(true)
                    .swipeHorizontal(true)
                    .load();
        }

    }
}
