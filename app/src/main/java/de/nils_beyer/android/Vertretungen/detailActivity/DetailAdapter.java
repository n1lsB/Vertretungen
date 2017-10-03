package de.nils_beyer.android.Vertretungen.detailActivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.Entry;
import de.nils_beyer.android.Vertretungen.data.TeacherEntry;

public class DetailAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int TEACHER_VIEW_TYPE = 1;
    private static final int STUDENT_VIEW_TYPE = 0;

    private Context c;
    private Group group;

    public DetailAdapter(Context c, Group k) {
        this.c = c;
        this.group = k;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(group.replacements.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Entry entry = group.replacements.get(position);
        if (entry instanceof TeacherEntry) {
            return TEACHER_VIEW_TYPE;
        } else {
            return STUDENT_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return group.replacements.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        switch (viewType) {
            case TEACHER_VIEW_TYPE:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_detail_layout_teacher, parent, false);
                vh = new TeacherViewHolder(v, c, group);
                break;
            case STUDENT_VIEW_TYPE:
                View v2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_detail_layout, parent, false);
                vh = new ViewHolder(v2, c, group);
                break;
            default:
                View v3 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_detail_layout, parent, false);
                vh = new ViewHolder(v3, c, group);
        }

        return vh;
    }
}