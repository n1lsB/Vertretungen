package de.nils_beyer.android.Vertretungen.widget;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.nils_beyer.android.Vertretungen.DateParser;
import de.nils_beyer.android.Vertretungen.DetailActivity;
import de.nils_beyer.android.Vertretungen.MainActivity;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.DataModel;
import de.nils_beyer.android.Vertretungen.data.Klasse;
import de.nils_beyer.android.Vertretungen.data.Replacements;

/**
 * Created by nbeye on 03. Feb. 2017.
 */

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private DataModel.source selectedSource;
    private ArrayList<Klasse> klasses = new ArrayList<>();
    private Context context = null;


    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
    }


    @Override
    public int getCount() {
        int count = 0;
        for (Klasse k : klasses) {
            count += k.replacements.length;
        }

        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Replacements getReplacement(int pos) {
        int count = 0;
        for (Klasse k : klasses) {
            if (count + k.replacements.length > pos) {
                return k.replacements[pos-count];
            }
            count += k.replacements.length;
        }
        return null;
    }

    private Klasse getKlasseAt(int pos) {
        int count = 0;
        for (Klasse k : klasses) {
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


        Replacements r = getReplacement(position);

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
                onClickIntent.putExtra(DetailActivity.ARG_DATE_EXTRA, DataModel.getDateToday(context));
                break;
            case Tomorrow:
                onClickIntent.putExtra(DetailActivity.ARG_DATE_EXTRA, DataModel.getDateTomorrow(context));
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
        ArrayList<Klasse> input = new ArrayList<>();
        if (!DataModel.containsData(context)) {
            return;
        }
        if (DateParser.sameDay(DataModel.getDateToday(context), new Date())) {
            input = DataModel.getToday(context);
            selectedSource = DataModel.source.Today;
        } else {
            input = DataModel.getTomorrow(context);
            selectedSource = DataModel.source.Tomorrow;
        }

        DataModel.sort(context, input);

        if (MarkedKlasses.hasMarked(context)) {
            klasses = new ArrayList<Klasse>();
            for (Klasse k : input) {
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
