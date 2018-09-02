package davi.liceodavinci.communications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import davi.liceodavinci.ConfigurationManager;
import davi.liceodavinci.OnFetchCompleteListener;
import davi.liceodavinci.R;

/**
 * Created by Emanuele on 30/12/2017 at 23:25 at 20:20 at 20:21!
 */

@SuppressLint("ValidFragment")
public class CommunicationsFragment extends Fragment {

    private int section = Communication.COMM_STUDENTS;

    private Activity activity;
    private RecyclerView commRecyclerView;
    private SwipeRefreshLayout swipeRefreshCom;
    private SearchView searchView;
    private List<Communication.LocalCommunication> communications;

    @SuppressLint("ValidFragment")
    public CommunicationsFragment(Activity activity, int section) {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commRecyclerView = activity.findViewById(R.id.com_recyclerview);
        swipeRefreshCom = activity.findViewById(R.id.com_swipe_refresh_layout);
        swipeRefreshCom.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (section <= Communication.COMM_PROFS) fetch();
                if (section == Communication.COMM_SAVED) fetchSavedComms();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("res", String.valueOf(section));

        if (section < Communication.COMM_SAVED) fetch();
        if (section == Communication.COMM_SAVED) fetchSavedComms();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.comm_actionbar_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                setResult(communications, query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setResult(communications, newText, false);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (communications != null) {
            switch (id) {
                case (R.id.action_sort_by_name): {
                    if (!communications.isEmpty()) {
                        Collections.sort(communications, new Comparator<Communication.LocalCommunication>() {
                            @Override
                            public int compare(Communication.LocalCommunication c1, Communication.LocalCommunication c2) {
                                return c1.getNameFormatted().compareTo(c2.getNameFormatted());
                            }
                        });
                    }
                    break;
                }

                case (R.id.action_sort_by_date): {
                    if (!communications.isEmpty()) {
                        Collections.sort(communications, new Comparator<Communication.LocalCommunication>() {
                            @Override
                            public int compare(Communication.LocalCommunication c1, Communication.LocalCommunication c2) {
                                return c2.getDataObject().compareTo(c1.getDataObject());
                            }
                        });
                    }
                    break;
                }

                case (R.id.action_sort_by_id): {
                    if (!communications.isEmpty()) {
                        Collections.sort(communications, new Comparator<Communication.LocalCommunication>() {
                            @Override
                            public int compare(Communication.LocalCommunication c1, Communication.LocalCommunication c2) {
                                return Integer.valueOf(c2.getId()).compareTo(c1.getId());
                            }
                        });
                    }
                    break;
                }
            }

            setResult(this.communications, searchView.getQuery().toString(), false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void fetchSavedComms() {
        List<Communication.LocalCommunication> communications = new ArrayList<>();
        LinearLayout nothingHere = activity.findViewById(R.id.nothing_here_layout);

        for (File file : new File(activity.getFilesDir().getPath()).listFiles()) {
            if (file.getName().endsWith(".pdf")) {
                communications.add(new Communication(file.getName(), "", "", "").new LocalCommunication(new Communication(file.getName(), "", "", "")));
            }
        }

        if (communications.size() == 0) {
            nothingHere.setVisibility(View.VISIBLE);
            swipeRefreshCom.setRefreshing(false);
        } else {
            nothingHere.setVisibility(View.GONE);
            this.communications = mergeCommWithSPref(communications);
            setResult(this.communications, null, true);
        }
    }

    protected void fetch() {
        swipeRefreshCom.setRefreshing(true);
        CommDataFetcher df = new CommDataFetcher(this, activity);

        try {
            df.fetchCommunicationsJson(section, new OnFetchCompleteListener<List<Communication>>() {
                @Override
                public void onSuccess(final List<Communication> result) {
                    for (int i = 0; i < result.size(); i++) {
                        result.get(i).setUrl(result.get(i).getUrl().replaceAll(" ", "%20"));
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fetchComplete(result);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    swipeRefreshCom = activity.findViewById(R.id.com_swipe_refresh_layout);
                    if (swipeRefreshCom != null) {
                        new Handler(activity.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshCom.setRefreshing(false);
                            }
                        });
                        Snackbar snackbar = Snackbar
                                .make(activity.findViewById(R.id.main_frame), "Errore di connessione", Snackbar.LENGTH_LONG)
                                .setAction("RIPROVA", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        fetch();
                                    }
                                });
                        snackbar.show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void updateItem(Communication.LocalCommunication communication) {
        for (Communication.LocalCommunication comm : this.communications) {
            if (comm.getName().equals(communication.getName())) {
                commRecyclerView.getAdapter().notifyItemChanged(this.communications.indexOf(comm), comm);
                this.communications.set(this.communications.indexOf(comm), communication);
            }
        }
    }

    private void fetchComplete(List<Communication> communications) {
        List<Communication.LocalCommunication> localCommunications = new ArrayList<>();

        for (Communication comm : communications)
            localCommunications.add(comm.new LocalCommunication(comm));

        this.communications = mergeCommWithSPref(localCommunications);
        String searchQuery = String.valueOf((searchView != null) ? searchView.getQuery() : null);
        setResult(this.communications, searchQuery, true);
    }

    private void setResult(List<Communication.LocalCommunication> communications, String query, boolean animate) {
        commRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        if ((communications != null) && (communications.size() != 0)) {
            commRecyclerView.setAdapter(new CommCardAdapter(activity, this, searchByName(communications, query), section));
        }
        if (animate) runLayoutAnimation(commRecyclerView);
        swipeRefreshCom.setRefreshing(false);
    }

    private List<Communication.LocalCommunication> mergeCommWithSPref(List<Communication.LocalCommunication> queryResult) {
        List<Communication.LocalCommunication> result = new ArrayList<>();

        if (ConfigurationManager.getIstance().getCommList() != null) {
            for (Communication.LocalCommunication queryComm : queryResult) {
                for (Communication.LocalCommunication savedComm : ConfigurationManager.getIstance().getCommList()) {
                    if (queryComm.getName().equals(savedComm.getName())) {
                        result.add(savedComm);
                        break;
                    }
                }
                if (result.size() == 0 || (!result.get(result.size() - 1).getName().equals(queryComm.getName())))
                    result.add(queryComm);
            }
        } else return queryResult;

        return result;
    }

    private List<Communication.LocalCommunication> searchByName(List<Communication.LocalCommunication> communications, String query) {
        ArrayList<Communication.LocalCommunication> result = new ArrayList<>();

        if (query == null) return communications;
        if (query.isEmpty()) return communications;

        for (Communication.LocalCommunication comm : communications) {
            if (comm.getName().toLowerCase().contains(query.toLowerCase())) {
                result.add(comm);
            }
        }

        return result;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_in);

        recyclerView.setLayoutAnimation(controller);
        if (recyclerView.getAdapter() != null) recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}