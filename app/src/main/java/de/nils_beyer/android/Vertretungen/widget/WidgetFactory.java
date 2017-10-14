package de.nils_beyer.android.Vertretungen.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Date;

import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.util.DateParser;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Entry;

/**
 * WidgetFactory used to create ListView for Vertretungen-Entries.
 * Created by nbeyer on 03. Feb. 2017.
 */

class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Group> klasses = new ArrayList<>();
    private GroupCollection groupCollection;
    private Context context = null;


    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
    }


    @Override
    public int getCount() {
        int count = 0;
        for (Group k : klasses) {
            count += k.replacements.size();
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Entry getReplacement(int pos) {
        int count = 0;
        for (Group k : klasses) {
            if (count + k.replacements.size() > pos) {
                return k.replacements.get(pos-count);
            }
            count += k.replacements.size();
        }
        return null;
    }

    private Group getKlasseAt(int pos) {
        int count = 0;
        for (Group k : klasses) {
            if (count + k.replacements.size() > pos) {
                return k;
            }
            count += k.replacements.size();
        }
        return null;
    }

    /*
     * Similar to getView of Adapter where instead of View
     * we return RemoteViews
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_row_layout);


        Entry r = getReplacement(position);

        remoteView.setTextViewText(R.id.widget_row_type, r.vertretungsart);
        remoteView.setTextViewText(R.id.widget_row_klasse, getKlasseAt(position).name);
        remoteView.setTextViewText(R.id.widget_row_original, r.originalSubject);
        remoteView.setTextViewText(R.id.widget_row_replacement, r.modifiedSubject);
        remoteView.setTextViewText(R.id.widget_row_information, r.information);
        remoteView.setTextViewText(R.id.widget_row_time, String.format("%s. Stunde, %s", r.time, r.room));
        remoteView.setOnClickFillInIntent(R.id.widget_row_rootlayout, new Intent());


        if (r.originalSubject.equals(" ") && r.modifiedSubject.equals(" ")) {
            remoteView.setViewVisibility(R.id.widget_row_original, View.GONE);
            remoteView.setViewVisibility(R.id.widget_row_replacement, View.GONE);
            remoteView.setViewVisibility(R.id.widget_row_arrow, View.GONE);
        } else {
            remoteView.setViewVisibility(R.id.widget_row_original, View.VISIBLE);
            remoteView.setViewVisibility(R.id.widget_row_replacement, View.VISIBLE);
            remoteView.setViewVisibility(R.id.widget_row_arrow, View.VISIBLE);
        }

        Intent onClickIntent = DetailActivity.getStartIntent(context, groupCollection, position);


        remoteView.setOnClickFillInIntent(R.id.widget_row_rootlayout, onClickIntent);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        Log.d("Factory", "onDataSetChanged: ");
        if (!StudentStorage.containsData(context)) {
            return;
        }
        if (DateParser.sameDay(StudentStorage.getDateToday(context), new Date())) {
            groupCollection = StudentStorage.getTodaySource(context);
        } else {
            groupCollection = StudentStorage.getTomorrowSource(context);
        }

        StudentStorage.sort(context, groupCollection.getGroupArrayList());

        klasses = new ArrayList<>();
        if (MarkedKlasses.hasMarked(context)) {
            for (Group k : groupCollection.getGroupArrayList()) {
                if (MarkedKlasses.isMarked(context, k.name)) {
                    klasses.add(k);
                }
            }
        } else {
            for (Group g : groupCollection.getGroupArrayList()) {
                klasses.add(g);
            }
        }
    }

    @Override
    public void onDestroy() {
    }


}
