package de.nils_beyer.android.Vertretungen.data;

/**
 * Created by nbeye on 28. Sep. 2017.
 */

public class TeacherEntry extends Entry {
    public String teacherOld;
    public String teacherNew;

    public String getTeacherOld() {
        return teacherOld;
    }

    public String getTeacherNew() {
        return teacherNew;
    }

    public static class Builder extends Entry.Builder {
        String teacherOld;
        String teacherNew;

        public TeacherEntry.Builder setTeacherOld(String teacherOld) {
            this.teacherOld = teacherOld;
            return this;
        }

        public TeacherEntry.Builder setTeacherNew(String teacherNew) {
            this.teacherNew = teacherNew;
            return this;
        }

        public Entry build() {
            TeacherEntry teacherEntry = new TeacherEntry();
            teacherEntry.originalSubject = originalSubject;
            teacherEntry.modifiedSubject = modifiedSubject;
            teacherEntry.time = time;
            teacherEntry.information = information;
            teacherEntry.type = type;
            teacherEntry.room = room;
            teacherEntry.reference = reference;
            teacherEntry.teacherNew = this.teacherNew;
            teacherEntry.teacherOld = this.teacherOld;
            return teacherEntry;
        }
    }
}
