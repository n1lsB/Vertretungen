package de.nils_beyer.android.Vertretungen.detailActivity;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.Entry;
import de.nils_beyer.android.Vertretungen.preferences.MarkedCourses;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private Context c;
    private Group group;

    public DetailAdapter(Context c, Group k) {
        this.c = c;
        this.group = k;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_original;
        TextView text_time;
        TextView text_replacement;
        TextView text_info;
        TextView text_type;
        TextView text_room;
        TextView text_arrow;
        CardView overview_card;
        TextView text_reference;
        ImageView marked_cours;

        public ViewHolder(View v) {
            super(v);

            text_original = (TextView) v.findViewById(R.id.text_original);
            text_time = (TextView) v.findViewById(R.id.text_time);
            text_replacement = (TextView) v.findViewById(R.id.text_modified);
            text_type = (TextView) v.findViewById(R.id.text_type);
            text_info = (TextView) v.findViewById(R.id.text_info);
            text_room = (TextView) v.findViewById(R.id.text_room);
            text_arrow = (TextView) v.findViewById(R.id.text_arrow);
            text_reference = (TextView) v.findViewById(R.id.text_reference);
            overview_card = (CardView) v.findViewById(R.id.overview_card);
            marked_cours = (ImageView) v.findViewById(R.id.icon_marked_courses);
        }

        public void bind(Entry r) {
            text_original.setText(r.originalSubject);
            text_time.setText(String.format(c.getString(R.string.placeholder_time), r.time));
            text_replacement.setText(r.modifiedSubject);
            text_info.setText(r.information);
            text_type.setText(r.type);
            text_room.setText(r.room);

            if  (
                    MarkedCourses.isMarked(c, group.name, r.originalSubject) ||
                    MarkedCourses.isMarked(c, group.name, r.modifiedSubject)
                ) {
                marked_cours.setVisibility(View.VISIBLE);
            } else {
                marked_cours.setVisibility(View.GONE);
            }

            if (r.originalSubject.equals(" ") && r.modifiedSubject.equals(" ")) {
                text_original.setVisibility(View.GONE);
                text_replacement.setVisibility(View.GONE);
                text_arrow.setVisibility(View.GONE);
            } else {

            }

            if(r.information.equals(" ")) {
                text_info.setVisibility(View.GONE);
            } else {
                text_info.setVisibility(View.VISIBLE);
            }

            if (r.hasReference()) {
                text_reference.setVisibility(View.VISIBLE);
                text_reference.setText(String.format(c.getString(R.string.placeholder_reference), r.reference));
            } else {
                text_reference.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(group.replacements[position]);
    }

    @Override
    public int getItemCount() {
        return group.replacements.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_detail_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
}