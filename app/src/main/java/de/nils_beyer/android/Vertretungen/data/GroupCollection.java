package de.nils_beyer.android.Vertretungen.data;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nils on 28.09.17.
 */

public final class GroupCollection {
    private Date date;
    private Date immediacity;
    private ArrayList<Group> groupArrayList;


    public GroupCollection(Date date, Date immediacity, ArrayList<Group> groupArrayList) {
        this.date = date;
        this.immediacity = immediacity;
        this.groupArrayList = groupArrayList;
    }

    public Date getDate() {
        return date;
    }

    public Date getImmediacity() {
        return immediacity;
    }

    public ArrayList<Group> getGroupArrayList() {
        return groupArrayList;
    }
}
