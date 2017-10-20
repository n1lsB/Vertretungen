package de.nils_beyer.android.Vertretungen.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nbeye on 28. Sep. 2017.
 */

public class TeacherEntry extends Entry implements Parcelable {
    public String klasse;

    public static class Builder extends Entry.Builder {
        String klasse;


        public TeacherEntry.Builder setKla(String klasse) {
            this.klasse = klasse;
            return this;
        }

        public Entry build() {
            TeacherEntry teacherEntry = new TeacherEntry();
            teacherEntry.originalSubject = originalSubject;
            teacherEntry.modifiedSubject = modifiedSubject;
            teacherEntry.time = time;
            teacherEntry.information = information;
            teacherEntry.vertretungsart = type;
            teacherEntry.room = room;
            teacherEntry.oldRoom = oldRoom;
            teacherEntry.reference = reference;
            teacherEntry.teacherNew = this.teacherNew;
            teacherEntry.teacherOld = this.teacherOld;
            teacherEntry.klasse = this.klasse;
            return teacherEntry;
        }
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalSubject);
        dest.writeString(modifiedSubject);
        dest.writeString(vertretungsart);
        dest.writeString(time);
        dest.writeString(information);
        dest.writeString(room);
        dest.writeString(oldRoom);
        dest.writeString(reference);
        dest.writeString(teacherOld);
        dest.writeString(teacherNew);
        dest.writeString(klasse);
    }

    public static final Parcelable.Creator<TeacherEntry> CREATOR = new Parcelable.Creator<TeacherEntry>() {
        public TeacherEntry[] newArray(int size) {
            return new TeacherEntry[size];
        }
        public TeacherEntry createFromParcel(Parcel in) {
            TeacherEntry k = new TeacherEntry();
            k.originalSubject = in.readString();
            k.modifiedSubject = in.readString();
            k.vertretungsart = in.readString();
            k.time = in.readString();
            k.information = in.readString();
            k.room = in.readString();
            k.oldRoom = in.readString();
            k.reference = in.readString();
            k.teacherOld = in.readString();
            k.teacherNew = in.readString();
            k.klasse = in.readString();
            return k;
        }
    };
}
