package de.nils_beyer.android.Vertretungen.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.nils_beyer.android.Vertretungen.DateParser;
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
    private ArrayList<String> klasseNames = new ArrayList<>();
    private ArrayList<Replacements> replacements= new ArrayList<>();

    private Context context = null;


    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
    }


    @Override
    public int getCount() {
        return replacements.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
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


        Replacements r = replacements.get(position);

        remoteView.setTextViewText(R.id.widget_row_type, r.type);
        remoteView.setTextViewText(R.id.widget_row_klasse, klasseNames.get(position));
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

        Intent onClickIntent = new Intent(context, MainActivity.class);


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
        } else {
            input = DataModel.getTomorrow(context);
        }

        Collections.sort(input, new Comparator<Klasse>() {
            @Override
            public int compare(Klasse o1, Klasse o2) {
                boolean o1Marked = MarkedKlasses.isMarked(context, o1.name);
                boolean o2Marked = MarkedKlasses.isMarked(context, o2.name);

                if (o1Marked && !o2Marked) {
                    return -1;
                } else if (!o1Marked && o2Marked) {
                    return 1;
                } else {
                    return o1.name.compareTo(o2.name);
                }
            }
        });

        klasseNames.clear();
        replacements.clear();
        Log.d("Factory", "onDataSetChanged: " + MarkedKlasses.hasMarked(context));
        for (Klasse k : input) {

            if (MarkedKlasses.isMarked(context, k.name) || !MarkedKlasses.hasMarked(context)) {
                for (Replacements r : k.replacements) {
                    klasseNames.add(k.name);
                    replacements.add(r);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
    }


}
