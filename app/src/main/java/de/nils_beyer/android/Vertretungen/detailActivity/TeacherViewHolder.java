package de.nils_beyer.android.Vertretungen.detailActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Entry;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.TeacherEntry;

/**
 * Created by nbeye on 02. Okt. 2017.
 */

class TeacherViewHolder extends ViewHolder {
    private TextView text_klasse;
    private TextView text_teacher_old;
    private TextView text_teacher_new;
    private String name;

    TeacherViewHolder(View v, Context context, Group group) {
        super(v, context, group);
        name = group.name;
        text_klasse = (TextView) v.findViewById(R.id.text_klasse);
        text_teacher_new = (TextView) v.findViewById(R.id.text_teacher_new);
        text_teacher_old = (TextView) v.findViewById(R.id.text_teacher_old);
    }

    @Override
    public void bind(Entry r) {
        super.bind(r);

        if (r instanceof TeacherEntry) {
            TeacherEntry teacherEntry = (TeacherEntry) r;

            text_klasse.setText(teacherEntry.klasse);
            text_teacher_old.setText(teacherEntry.teacherOld);
            text_teacher_new.setText(teacherEntry.teacherNew);
            if (teacherEntry.teacherNew.equals(name)) {
                text_teacher_new.setTextColor(Color.RED);
                text_teacher_old.setTextColor(Color.BLACK);
            } else if (teacherEntry.teacherOld.equals(name)) {
                text_teacher_old.setTextColor(Color.RED);
                text_teacher_new.setTextColor(Color.BLACK);
            } else {
                text_teacher_old.setTextColor(Color.BLACK);
                text_teacher_new.setTextColor(Color.BLACK);
            }

            text_original.setVisibility(View.VISIBLE);
            text_replacement.setVisibility(View.VISIBLE);
            text_arrow.setVisibility(View.VISIBLE);
        }

    }
}
