package de.nils_beyer.android.Vertretungen.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nbeye on 07.11.2016.
 */

public class Klasse implements Serializable, Parcelable {
    public String name;
    public Replacements[] replacements = new Replacements[0];

    public void add(Replacements r) {
        Replacements[] newReplacements = new Replacements[replacements.length + 1];
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

    public static final Parcelable.Creator<Klasse> CREATOR = new Parcelable.Creator<Klasse>() {
        public Klasse[] newArray(int size) {
            return new Klasse[size];
        }
        public Klasse createFromParcel(Parcel in) {
            Klasse k = new Klasse();
            k.replacements = (Replacements[]) in.readSerializable();
            k.name = in.readString();

            return k;
        }
    };


}
