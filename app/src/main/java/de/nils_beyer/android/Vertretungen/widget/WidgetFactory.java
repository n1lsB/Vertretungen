package de.nils_beyer.android.Vertretungen.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.util.DateParser;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Entry;

/**
 * Created by nbeye on 03. Feb. 2017.
 */

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private StudentStorage.source selectedSource;
    private ArrayList<Group> klasses = new ArrayList<>();
    private Context context = null;


    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
    }


    @Override
    public int getCount() {
        int count = 0;
        for (Group k : klasses) {
            count += k.replacements.length;
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
            if (count + k.replacements.length > pos) {
                return k.replacements[pos-count];
            }
            count += k.replacements.length;
        }
        return null;
    }

    private Group getKlasseAt(int pos) {
        int count = 0;
        for (Group k : klasses) {
            if (count + k.replacements.length > pos) {
                return k;
            }
            count += k.replacements.length;
        }
        return null;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_row_layout);


        Entry r = getReplacement(position);

        remoteView.setTextViewText(R.id.widget_row_type, r.type);
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

        Intent onClickIntent = new Intent(context, DetailActivity.class);
        switch (selectedSource) {
            case Today:
                onClickIntent.putExtra(DetailActivity.ARG_DATE_EXTRA, StudentStorage.getDateToday(context));
                break;
            case Tomorrow:
                onClickIntent.putExtra(DetailActivity.ARG_DATE_EXTRA, StudentStorage.getDateTomorrow(context));
                break;
        }
        onClickIntent.putExtra(DetailActivity.ARG_KLASSE_EXTRA, (Serializable) getKlasseAt(position));

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
        ArrayList<Group> input = new ArrayList<>();
        if (!StudentStorage.containsData(context)) {
            return;
        }
        if (DateParser.sameDay(StudentStorage.getDateToday(context), new Date())) {
            input = StudentStorage.getToday(context);
            selectedSource = StudentStorage.source.Today;
        } else {
            input = StudentStorage.getTomorrow(context);
            selectedSource = StudentStorage.source.Tomorrow;
        }

        StudentStorage.sort(context, input);

        if (MarkedKlasses.hasMarked(context)) {
            klasses = new ArrayList<Group>();
            for (Group k : input) {
                if (MarkedKlasses.isMarked(context, k.name)) {
                    klasses.add(k);
                }
            }
        } else {
            klasses = input;
        }
    }

    @Override
    public void onDestroy() {
    }


}
