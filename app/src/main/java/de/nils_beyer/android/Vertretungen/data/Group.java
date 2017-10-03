package de.nils_beyer.android.Vertretungen.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nbeye on 07.11.2016.
 */

public class Group implements Serializable, Parcelable{
    public String name;
    public ArrayList<Entry> replacements = new ArrayList<>();

    public void add(Entry r) {
        replacements.add(r);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Entry[] entry = new Entry[replacements.size()];
        entry = replacements.toArray(entry);
        dest.writeParcelableArray(entry, flags);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group[] newArray(int size) {
            return new Group[size];
        }
        public Group createFromParcel(Parcel in) {
            Group k = new Group();
            Parcelable[] parcelables = in.readParcelableArray(Entry.class.getClassLoader());
            k.replacements = new ArrayList<>();
            for (Parcelable p : parcelables) {
                k.replacements.add((Entry) p);
            }
            k.name = in.readString();
            return k;
        }
    };


}
