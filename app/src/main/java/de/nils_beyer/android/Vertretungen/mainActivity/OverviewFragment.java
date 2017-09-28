package de.nils_beyer.android.Vertretungen.mainActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.data.Group;


public class OverviewFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    // Bundle String for Datamodel.source
    private static String ARG_SOURCE = "ARG_SOURCE_ID";
    private static String ARG_DACTIVITY = "ARG_DACTIVITY";

    // Data Model
    private StudentStorage.source source;

    // Data
    ArrayList<Group> groupArrayList;
    Date immediacy;
    Date date;

    // View References
    private TextView noReplcaements;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OverviewAdapter overviewAdapter;
    private ViewGroup container;
    private View rootView;
    private RecyclerView recyclerView;
    private OverviewSectionsAdapter.DownloadingActivity downloadingActivity;

    public OverviewFragment() {
        super();
    }

    public static OverviewFragment getIntance(Context c, StudentStorage.source source) {

        // Put StudentStorage.source in the bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SOURCE, source);



        // Create the Fragment and return it.
        OverviewFragment fragment = new OverviewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        swipeRefreshLayout.removeAllViews();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() instanceof OverviewSectionsAdapter.DownloadingActivity) {
            downloadingActivity = (OverviewSectionsAdapter.DownloadingActivity) getActivity();
        }

        source = (StudentStorage.source) getArguments().getSerializable(ARG_SOURCE);
        switch (source) {
            case Today:
                overviewAdapter = new OverviewAdapter(getContext(), StudentStorage.getTodaySource(getContext()));
                break;
            case Tomorrow:
                overviewAdapter = new OverviewAdapter(getContext(), StudentStorage.getTomorrowSource(getContext()));
                break;
        }

        this.container = container;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        noReplcaements = (TextView) rootView.findViewById(R.id.text_noreplacemants);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity activity = (MainActivity) getActivity();

                if (activity != null) {
                    activity.requestData();
                }
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(overviewAdapter);

        updateData();

        if (groupArrayList.size() == 0) {
            if (immediacy == null) {
                noReplcaements.setText(getString(R.string.no_data_downloaded));
            } else {
                noReplcaements.setText(getString(R.string.no_data));
            }

            noReplcaements.setVisibility(View.VISIBLE);
        } else {
            noReplcaements.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void updateData() {
        if (downloadingActivity.isDownloading()) {
            showSwipeRefreshLayout();
        } else {
            resetSwipeRefreshLayout();
        }

        switch (source) {
            case Today:
                groupArrayList = StudentStorage.getToday(getContext());
                date = StudentStorage.getDateToday(getContext());
                immediacy = StudentStorage.getImmediacityToday(getContext());
                break;
            case Tomorrow:
                groupArrayList = StudentStorage.getTomorrow(getContext());
                date = StudentStorage.getDateTomorrow(getContext());
                immediacy = StudentStorage.getImmediacityTomorrow(getContext());
                break;
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    void resetSwipeRefreshLayout() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    void showSwipeRefreshLayout() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }


}