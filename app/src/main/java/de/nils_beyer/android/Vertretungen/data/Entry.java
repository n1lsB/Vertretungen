package de.nils_beyer.android.Vertretungen.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nbeye on 07.11.2016.
 */
public class Entry implements Serializable, Parcelable {
    public String originalSubject;
    public String modifiedSubject;
    public String vertretungsart;
    public String time;
    public String information;
    public String room;
    public String oldRoom;
    public String reference;

    public static class Builder {
        String originalSubject;
        String modifiedSubject;
        String type;
        String time;
        String information;
        String room;
        String oldRoom;
        String reference;

        public Builder setOriginalSubject(String _originalSubject) {
            originalSubject = _originalSubject;
            return this;
        }

        public Builder setType(String _type) {
            if (_type.length() == 0)
                return this;

            _type = _type.substring(0,1).toUpperCase() + _type.substring(1);
            type = _type;

            if (type.equals("Sondereins."))
                type = "Sondereinsatz";

            return this;
        }

        public Builder setRoom(String _room) {
            room = _room;
            return this;
        }

        public Builder setOldRoom(String _room) {
            oldRoom = _room;
            return this;
        }

        public Builder setModifiedSubject(String _modifiedSubject) {
            modifiedSubject = _modifiedSubject;
            return this;
        }

        public Builder setTime(String _time) {
            time = _time;
            return this;
        }

        public Builder setInformation(String _information) {
            information = _information;
            return this;
        }

        public Builder setReference(String _reference) {
            reference = _reference;
            return this;
        }

        public Entry build() {
            Entry r = new Entry();
            r.originalSubject = originalSubject;
            r.modifiedSubject = modifiedSubject;
            r.time = time;
            r.information = information;
            r.vertretungsart = type;
            r.room = room;
            r.oldRoom = oldRoom;
            r.reference = reference;
            return r;
        }
    }

    public boolean hasReference() {
        if (reference == null) {
            return false;
        } else {
            return !reference.isEmpty();
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
    }

    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
        public Entry createFromParcel(Parcel in) {
            Entry k = new Entry();
            k.originalSubject = in.readString();
            k.modifiedSubject = in.readString();
            k.vertretungsart = in.readString();
            k.time = in.readString();
            k.information = in.readString();
            k.room = in.readString();
            k.oldRoom = in.readString();
            k.reference = in.readString();
            return k;
        }
    };
}
