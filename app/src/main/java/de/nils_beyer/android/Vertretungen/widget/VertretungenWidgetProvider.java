package de.nils_beyer.android.Vertretungen.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Date;

import de.nils_beyer.android.Vertretungen.account.AccountSpinner;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.util.DateParser;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.mainActivity.MainActivity;
import de.nils_beyer.android.Vertretungen.R;


public class VertretungenWidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_ACCOUNT_ORDINAL = "VertretungenWidgetProvider.EXTRA_ACCOUNT_ORDINAL";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);

            // Use a special WidgetConfigurationStorage, because
            // AppWidgetManager cannot store persistently the configuration
            // a device reboot will delete the configuration
            int accountId = WidgetConfigurationStorage.getConfig(context, appWidgetId);

            AccountSpinner.Account account;
            // WidgetConfigurationStorage will return -1 if there is no configuration stored.
            if (accountId == -1) {
                account = null;
            } else {
                account = AccountSpinner.Account.values()[accountId];
            }


            // Check if Stored Data is not null or account is not set
            // e.g. because there is no configuration stored
            if (account == null || !AccountSpinner.containsData(context, account)) {
                // When we don't have Data to present
                // Show an error to the user
                // because we cannot delete the AppWidget
                // programmatically
                RemoteViews errorViews = new RemoteViews(context.getPackageName(), R.layout.widget_error_layout);
                appWidgetManager.updateAppWidget(appWidgetId, errorViews);
            } else {
                RemoteViews remoteViews = createWidget(context, account, appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview);
            }
        }
    }

    public RemoteViews createWidget(Context context, AccountSpinner.Account account, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent serviceIntent =  new Intent(context, WidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.putExtra(WidgetFactory.EXTRA_ACCOUNT_ORDINAL, account.ordinal());
        // when intents are compared, the extras are ignored, so we need to
        // embed the extras into the data so that the extras will not be ignored
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteViews.setRemoteAdapter(R.id.widget_listview, serviceIntent);

        if (AccountSpinner.containsData(context, account)) {
            Date dateToday = AccountSpinner.getToday(context, account).getDate();
            Date dateTomorrow = AccountSpinner.getTomorrow(context, account).getDate();
            if (DateParser.after(dateToday, new Date()) || DateParser.sameDay(dateToday, new Date())) {
                remoteViews.setTextViewText(R.id.widget_title_text, "Vertretungen (" + DateParser.parseDateToShortString(context, dateToday) + ")");
            } else {
                remoteViews.setTextViewText(R.id.widget_title_text, "Vertretungen (" + DateParser.parseDateToShortString(context, dateTomorrow) + ")");
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
