package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Emanuele on 30/12/2017 at 23:25.
 */

@SuppressLint("ValidFragment")
public class CommunicationsFragment extends Fragment {

    private int section = 0;
    private Activity activity;
    private RecyclerView commRecyclerView;
    private SwipeRefreshLayout swipeRefreshCom;

    @SuppressLint("ValidFragment")
    public CommunicationsFragment(Activity activity, int section){
        this.activity = activity;
        this.section = section;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.communications_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        commRecyclerView = (RecyclerView) activity.findViewById(R.id.com_recyclerview);
        swipeRefreshCom = (SwipeRefreshLayout) activity.findViewById(R.id.com_swipe_refresh_layout);
        swipeRefreshCom.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (section < 3) fetch();
                if (section == 3) fetchSavedComms();
            }
        });

        if (section < 3) fetch();
        if (section == 3) fetchSavedComms();
    }
    
    private void fetchSavedComms(){
        List<Communication> communications = new ArrayList<>();
        for (File file:new File(activity.getFilesDir().getPath()).listFiles()){
            if (file.getName().endsWith(".pdf")){
                communications.add(new Communication(file.getName(), "", "", ""));
            }
        }

        Communication [] communicationsArr = new Communication[communications.size()];
        communicationsArr = communications.toArray(communicationsArr);
        fetchComplete(communicationsArr);
    }

    private void fetch(){
        swipeRefreshCom.setRefreshing(true);
        DataFetcher df = new DataFetcher(this, activity);
        try {
            df.fetchCommunicationsJson(section);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void fetchComplete(Communication[] communications) {
        commRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        commRecyclerView.setAdapter(new CommCardAdapter(activity,this, swipeRefreshCom, new LinkedList<>(Arrays.asList(communications)), section));
        swipeRefreshCom.setRefreshing(false);
    }

    protected void fetchFailed(){
        swipeRefreshCom.setRefreshing(false);
        Snackbar snackbar = Snackbar
                .make(swipeRefreshCom, "Errore di connessione", Snackbar.LENGTH_LONG)
                .setAction("RIPROVA", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetch();
                    }
                });
        snackbar.show();
    }
}
