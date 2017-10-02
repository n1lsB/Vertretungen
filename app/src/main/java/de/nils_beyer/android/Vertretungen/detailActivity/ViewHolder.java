package de.nils_beyer.android.Vertretungen.detailActivity;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Entry;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.preferences.MarkedCourses;

/**
 * Created by nbeye on 02. Okt. 2017.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private Group group;

    private TextView text_original;
    private TextView text_time;
    private TextView text_replacement;
    private TextView text_info;
    private TextView text_type;
    private TextView text_room;
    private TextView text_arrow;
    private CardView overview_card;
    private TextView text_reference;
    private ImageView marked_cours;

    public ViewHolder(View v, Context context, Group group) {
        super(v);

        this.context = context;
        this.group = group;

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
        text_time.setText(String.format(context.getString(R.string.placeholder_time), r.time));
        text_replacement.setText(r.modifiedSubject);
        text_info.setText(r.information);
        text_type.setText(r.type);
        text_room.setText(r.room);

        if  (
                MarkedCourses.isMarked(context, group.name, r.originalSubject) ||
                        MarkedCourses.isMarked(context, group.name, r.modifiedSubject)
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
            text_reference.setText(String.format(context.getString(R.string.placeholder_reference), r.reference));
        } else {
            text_reference.setVisibility(View.GONE);
        }

    }
}