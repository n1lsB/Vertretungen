package de.nils_beyer.android.Vertretungen.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Date;

import de.nils_beyer.android.Vertretungen.util.DateParser;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.mainActivity.MainActivity;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.DataModel;


public class VertretungenWidgetProvider extends AppWidgetProvider {

    public static final String DATA_SET_KEY = "DATA_SET_KEY";



    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = createWidget(context);

        updateWidgetNow(context, remoteViews);
        super.onReceive(context, intent);
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = createWidget(context);

        updateWidgetNow(context, remoteViews);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public RemoteViews createWidget(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent serviceIntent =  new Intent(context, WidgetService.class);

        remoteViews.setRemoteAdapter(R.id.widget_listview, serviceIntent);

        if (DataModel.containsData(context)) {
            if (DateParser.sameDay(DataModel.getDateToday(context), new Date())) {
                remoteViews.setTextViewText(R.id.widget_title_text, "Vertretungen (" + DateParser.parseDateToShortString(context, DataModel.getDateToday(context)) + ")");
            } else {
                remoteViews.setTextViewText(R.id.widget_title_text, "Vertretungen (" + DateParser.parseDateToShortString(context, DataModel.getDateTomorrow(context)) + ")");
            }
        }

        Intent openActivity = new Intent(context.getApplicationContext(), DetailActivity.class);
            openActivity.setAction("");
        PendingIntent clickPI = PendingIntent.getActivity(context.getApplicationContext(), 60,
                openActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_listview, clickPI);



        Intent refreshIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            refreshIntent.putExtra(MainActivity.RefreshKey, true);
            refreshIntent.setAction("refresh");
        PendingIntent clickPIRefresh = PendingIntent.getActivity(context.getApplicationContext(), 60, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_button_refresh, clickPIRefresh);

        Intent startApp = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent startPI = PendingIntent.getActivity(context.getApplicationContext(), 60, startApp, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_title_text, startPI);

        return remoteViews;
    }

    public void updateWidgetNow (Context context, RemoteViews remoteViews){
        ComponentName widgetComponent = new ComponentName(context, VertretungenWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        manager.updateAppWidget(widgetComponent, remoteViews);
        int[] ids = manager.getAppWidgetIds(widgetComponent);
        manager.notifyAppWidgetViewDataChanged(ids, R.id.widget_listview);

    }

    public static void updateWidgetData(Context context) {
        // Notify Widget
        Intent intent = new Intent(context, VertretungenWidgetProvider.class);
        context.sendBroadcast(intent);
    }
}
