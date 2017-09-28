package de.nils_beyer.android.Vertretungen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.nils_beyer.android.Vertretungen.data.DataModel;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;


public class OverviewFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    // Bundle String for Datamodel.source
    private static String ARG_SOURCE = "ARG_SOURCE_ID";
    private static String ARG_DACTIVITY = "ARG_DACTIVITY";

    // Data Model
    private DataModel.source source;    

    // Data
    ArrayList<Group> groupArrayList;
    Date immediacy;
    Date date;

    // View References
    private TextView noReplcaements;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OverviewAdapter overviewAdapter = new OverviewAdapter();
    private ViewGroup container;
    private View rootView;
    private RecyclerView recyclerView;
    private OverviewSectionsAdapter.DownloadingActivity downloadingActivity;

    public OverviewFragment() {
        super();
    }

    public static OverviewFragment getIntance(Context c, DataModel.source source) {

        // Put DataModel.source in the bundle
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

        source = (DataModel.source) getArguments().getSerializable(ARG_SOURCE);


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
                groupArrayList = DataModel.getToday(getContext());
                date = DataModel.getDateToday(getContext());
                immediacy = DataModel.getImmediacityToday(getContext());
                break;
            case Tomorrow:
                groupArrayList = DataModel.getTomorrow(getContext());
                date = DataModel.getDateTomorrow(getContext());
                immediacy = DataModel.getImmediacityTomorrow(getContext());
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

    private class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {


        class ViewHolder extends RecyclerView.ViewHolder {
                TextView className;
                TextView replacementCounter;
                CardView cardView;
                ImageView marked;

                ViewHolder(View v) {
                    super(v);
                    cardView = (CardView) v.findViewById(R.id.overview_card);
                    className = (TextView) v.findViewById(R.id.text_class_name);
                    replacementCounter = (TextView) v.findViewById(R.id.text_replacement_count);
                    marked = (ImageView) v.findViewById(R.id.image_class_marked);
                }

                void bind(final Group group) {
                    className.setText("Group " + group.name);
                    if (group.replacements.length > 1)
                        replacementCounter.setText(group.replacements.length + " Vertretungen");
                    else
                        replacementCounter.setText(group.replacements.length + " Vertretung");

                    marked.setVisibility(MarkedKlasses.isMarked(getActivity().getApplication(), group.name) ? View.VISIBLE : View.GONE);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OverviewFragment.this.getContext(), DetailActivity.class);
                            intent.putExtra(DetailActivity.ARG_KLASSE_EXTRA, (Serializable) group);
                            intent.putExtra(DetailActivity.ARG_DATE_EXTRA, date);
                            OverviewFragment.this.startActivity(intent);
                        }
                    });
                }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 0) {
                holder.cardView.setOnClickListener(null);
                holder.replacementCounter.setVisibility(View.GONE);
                holder.className.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                holder.cardView.setClickable(false);
                holder.marked.setVisibility(View.GONE);
                if (immediacy != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    holder.className.setText("Stand: " + DateParser.parseDateToShortString(getContext(), immediacy) + " " + simpleDateFormat.format(immediacy));
                }
                else {
                    holder.cardView.setVisibility(View.GONE);
                }
            } else {
                holder.cardView.setClickable(true);
                holder.cardView.setVisibility(View.VISIBLE);
                holder.replacementCounter.setVisibility(View.VISIBLE);
                holder.marked.setVisibility(View.VISIBLE);
                holder.className.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                holder.bind(OverviewFragment.this.groupArrayList.get(position - 1));


            }

        }

        @Override
        public int getItemCount() {
            if (OverviewFragment.this.groupArrayList.size() == 0) {
                return 0;
            } else {
                return OverviewFragment.this.groupArrayList.size() + 1;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_overview_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
    }
}