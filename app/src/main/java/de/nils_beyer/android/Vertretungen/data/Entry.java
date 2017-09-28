package de.nils_beyer.android.Vertretungen.data;

import java.io.Serializable;

/**
 * Created by nbeye on 07.11.2016.
 */
public class Entry implements Serializable{
    public String originalSubject;
    public String modifiedSubject;
    public String type;
    public String time;
    public String information;
    public String room;
    public String reference;

    public static class Builder {
        String originalSubject;
        String modifiedSubject;
        String type;
        String time;
        String information;
        String room;
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
            r.type = type;
            r.room = room;
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
}
