package de.nils_beyer.android.Vertretungen.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nbeye on 07.11.2016.
 */

public class Group implements Serializable, Parcelable {
    public String name;
    public Entry[] replacements = new Entry[0];

    public void add(Entry r) {
        Entry[] newReplacements = new Entry[replacements.length + 1];
        for (int i = 0; i < replacements.length; i++) {
            newReplacements[i] = replacements[i];
        }

        newReplacements[replacements.length] = r;

        replacements = newReplacements;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(replacements);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group[] newArray(int size) {
            return new Group[size];
        }
        public Group createFromParcel(Parcel in) {
            Group k = new Group();
            k.replacements = (Entry[]) in.readSerializable();
            k.name = in.readString();

            return k;
        }
    };


}
