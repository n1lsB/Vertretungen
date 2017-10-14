package de.nils_beyer.android.Vertretungen.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Date;

import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.util.DateParser;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.mainActivity.MainActivity;
import de.nils_beyer.android.Vertretungen.R;


public class VertretungenWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = createWidget(context);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    public RemoteViews createWidget(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent serviceIntent =  new Intent(context, WidgetService.class);

        remoteViews.setRemoteAdapter(R.id.widget_listview, serviceIntent);

        if (StudentStorage.containsData(context)) {
            if (DateParser.sameDay(StudentStorage.getDateToday(context), new Date())) {
                remoteViews.setTextViewText(R.id.widget_title_text, "Vertretungen (" + DateParser.parseDateToShortString(context, StudentStorage.getDateToday(context)) + ")");
            } else {
                remoteViews.setTextViewText(R.id.widget_title_text, "Vertretungen (" + DateParser.parseDateToShortString(context, StudentStorage.getDateTomorrow(context)) + ")");
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

    public static void updateWidgetData(Context context) {
        // Notify Widget
        Intent intent = new Intent(context, VertretungenWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, VertretungenWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
