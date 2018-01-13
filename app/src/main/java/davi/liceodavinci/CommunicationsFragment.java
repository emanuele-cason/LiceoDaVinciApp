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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emanuele on 30/12/2017 at 23:25.
 */

@SuppressLint("ValidFragment")
public class CommunicationsFragment extends Fragment {

    private int section = Communication.COMM_STUDENTS;

    private Activity activity;
    private RecyclerView commRecyclerView;
    private SwipeRefreshLayout swipeRefreshCom;
    private List<Communication> communications;
    private SearchView searchView;

    @SuppressLint("ValidFragment")
    public CommunicationsFragment(Activity activity, int section){
        this.activity = activity;
        this.section = section;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.communications_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.comm_actionbar_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setResult(searchByName(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setResult(searchByName(newText));
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        commRecyclerView = (RecyclerView) activity.findViewById(R.id.com_recyclerview);
        swipeRefreshCom = (SwipeRefreshLayout) activity.findViewById(R.id.com_swipe_refresh_layout);
        swipeRefreshCom.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (section <= Communication.COMM_PROFS) fetch();
                if (section == Communication.COMM_SAVED) fetchSavedComms();
            }
        });

        if (section < Communication.COMM_SAVED) fetch();
        if (section == Communication.COMM_SAVED) fetchSavedComms();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private List<Communication> searchByName(String query){
        ArrayList<Communication> result = new ArrayList<>();
        if(query.equals("")) return communications;

        for(Communication comm: communications){
            if (comm.getName().toLowerCase().contains(query.toLowerCase())){
                result.add(comm);
            }
        }

        return result;
    }

    private void fetchSavedComms(){
        List<Communication> communications = new ArrayList<>();
        for (File file:new File(activity.getFilesDir().getPath()).listFiles()){
            if (file.getName().endsWith(".pdf")){
                communications.add(new Communication(file.getName(), "", "", ""));
            }
        }

        fetchComplete(communications);
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

    private void setResult(List<Communication> communications){
        if (searchView.getQuery().toString().equals("")){
            commRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            commRecyclerView.setAdapter(new CommCardAdapter(activity,this, swipeRefreshCom, communications, section));
            swipeRefreshCom.setRefreshing(false);
        }
        else {
            commRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            commRecyclerView.setAdapter(new CommCardAdapter(activity,this, swipeRefreshCom, searchByName(searchView.getQuery().toString()), section));
            swipeRefreshCom.setRefreshing(false);
        }
    }

    protected void fetchComplete(List<Communication> communications) {
        this.communications = communications;
        setResult(communications);
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
