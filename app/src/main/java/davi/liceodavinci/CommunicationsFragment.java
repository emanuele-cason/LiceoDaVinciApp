package davi.liceodavinci;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by Emanuele on 30/12/2017 at 23:25.
 */

public class CommunicationsFragment extends Fragment {

    private int section = 0;
    private Communication [] communications;

    protected void setSection(int section) {
        this.section = section;
        DataFetcher df = new DataFetcher(this);
        try {
            df.fetchCommunicationsJson(section);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.communications_fragment, container, false);
    }

    protected void responseJson(Communication [] communication){
        this.communications = communication;
        Log.d("comm", String.valueOf(communication[0].getName()));
    }
}
