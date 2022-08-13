package de.nils_beyer.android.Vertretungen.mainActivity;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.account.Dataset;
import de.nils_beyer.android.Vertretungen.account.StudentAccount;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;


public class OverviewFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    // Bundle String for Datamodel.source
    public static final String ARG_GROUP_COLLECTION = "GROUP_COLLECTION";
    public static final String ARG_POSITION = "ARG_POSITION";

    // Data Model
    private GroupCollection groupCollection;
    private int position;

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

    public static OverviewFragment getIntance(Context c, GroupCollection collection, int pos) {

        // Put StudentStorage.source in the bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_GROUP_COLLECTION, collection);
        bundle.putInt(ARG_POSITION, pos);

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

        if (savedInstanceState == null) {
            position = getArguments().getInt(ARG_POSITION);
            groupCollection = (GroupCollection) getArguments().getSerializable(ARG_GROUP_COLLECTION);
        } else {
            position = savedInstanceState.getInt(ARG_POSITION);
            groupCollection = (GroupCollection) savedInstanceState.getSerializable(ARG_GROUP_COLLECTION);
        }


        // TODO handle motd better
        Dataset dataset;
        switch (position) {
            case 1:
                dataset = StudentAccount.StudentDatasets.Today;
                break;
            case 2:
                dataset = StudentAccount.StudentDatasets.Tomorrow;
                break;
            default:
                throw new UnsupportedOperationException("Position unknown");
        }

        overviewAdapter = new OverviewAdapter(getContext(), groupCollection, dataset);

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

        updateData(groupCollection);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_GROUP_COLLECTION, groupCollection);
        outState.putInt(ARG_POSITION, position);
    }

    public int getPosition() {
        return position;
    }

    public void updateData(GroupCollection collection) {
        if (downloadingActivity.isDownloading()) {
            showSwipeRefreshLayout();
        } else {
            resetSwipeRefreshLayout();
        }
        groupCollection = collection;

        if (groupCollection.getGroupArrayList().size() == 0) {
            if (groupCollection.getImmediacity() == null) {
                noReplcaements.setText(getString(R.string.no_data_downloaded));
            } else {
                noReplcaements.setText(getString(R.string.no_data));
            }

            noReplcaements.setVisibility(View.VISIBLE);
        } else {
            noReplcaements.setVisibility(View.GONE);
        }

        overviewAdapter.update(collection);
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