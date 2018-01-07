package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.pdfview.PDFView;

import java.io.File;

/**
 * Created by Emanuele on 07/01/2018 at 17:57.
 */

@SuppressLint("ValidFragment")
public class PdfRender extends Fragment{

    private Activity activity;
    private String fileName;

    @SuppressLint("ValidFragment")
    protected PdfRender(Activity activity, String fileName){
        this.activity = activity;
        this.fileName = fileName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pdf_render_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PDFView pdfView = (PDFView) activity.findViewById(R.id.pdf_render);
        pdfView.fromFile(new File(activity.getCacheDir().getPath().concat("/").concat(fileName)))
                .defaultPage(1)
                .showMinimap(true)
                .enableSwipe(false)
                .load();
    }
}
