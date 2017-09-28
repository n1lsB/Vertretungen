package de.nils_beyer.android.Vertretungen.detailActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Group;


public class DetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Group group;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(Group group) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = (Group) getArguments().get(ARG_PARAM1);
//            String klassName = getArguments().getString(ARG_PARAM1);
//            int sourceOrdinal = getArguments().getInt(ARG_PARAM2);
//            DataModel.source source = DataModel.source.values()[sourceOrdinal];
//            ArrayList<Group> list;
//
//            switch (source) {
//                case Today:
//                        list = DataModel.getToday(getContext());
//                    break;
//                case Tomorrow:
//                        list = DataModel.getTomorrow(getContext());
//                    break;
//            }
//
//            for (Group k : list) {
//                if (k.name == klassName) {
//                    group = k;
//                    break;
//                }
//            }
//
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview_detail);
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new DetailAdapter(this.getContext(), group));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_detail, container, false);
    }
}
