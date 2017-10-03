package de.nils_beyer.android.Vertretungen.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.nils_beyer.android.Vertretungen.preferences.MarkedTeacher;

/**
 * Created by nbeye on 02. Okt. 2017.
 */

public class TeacherGroup extends Group implements Serializable{
    @Override
    public void setMarked(Context c, boolean mark) {
        MarkedTeacher.setMarked(c, name, mark);
    }

    @Override
    public boolean isMarked(Context c) {
        return MarkedTeacher.isMarked(c, name);
    }
}
