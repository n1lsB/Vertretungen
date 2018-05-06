package de.nils_beyer.android.Vertretungen.detailActivity;

import android.content.Context;
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

    TextView text_original;
    private TextView text_time;
    TextView text_replacement;
    TextView text_info;
    private TextView text_type;
    private TextView text_room;
    TextView text_arrow;
    private TextView text_reference;
    private ImageView marked_cours;

    ViewHolder(View v, Context context, Group group) {
        super(v);

        this.context = context;
        this.group = group;

        text_original = (TextView) v.findViewById(R.id.text_original);
        text_time = (TextView) v.findViewById(R.id.text_event_time);
        text_replacement = (TextView) v.findViewById(R.id.text_modified);
        text_type = (TextView) v.findViewById(R.id.text_type);
        text_info = (TextView) v.findViewById(R.id.text_info);
        text_room = (TextView) v.findViewById(R.id.text_room);
        text_arrow = (TextView) v.findViewById(R.id.text_arrow);
        text_reference = (TextView) v.findViewById(R.id.text_reference);
        marked_cours = (ImageView) v.findViewById(R.id.icon_marked_courses);
    }

    public void bind(Entry r) {
        text_original.setText(r.originalSubject);
        text_time.setText(String.format(context.getString(R.string.placeholder_time), r.time));
        text_replacement.setText(r.modifiedSubject);
        text_info.setText(r.information);
        text_type.setText(r.vertretungsart);
        text_room.setText(r.room);

        if (r.oldRoom != null && !r.oldRoom.equals("") && !r.oldRoom.equals(" ")) {
            text_room.setText(String.format(context.getString(R.string.detail_placeholder_room), r.oldRoom, r.room));
        }

        if  (MarkedCourses.isMarked(context, group.name, r.originalSubject) ||
             MarkedCourses.isMarked(context, group.name, r.modifiedSubject) ||
             MarkedCourses.isKlausurMarked(context, group.name, r.information)) {
            marked_cours.setVisibility(View.VISIBLE);
        } else {
            marked_cours.setVisibility(View.GONE);
        }

        if (r.originalSubject.equals(" ") && r.modifiedSubject.equals(" ")) {
            text_original.setVisibility(View.GONE);
            text_replacement.setVisibility(View.GONE);
            text_arrow.setVisibility(View.GONE);
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