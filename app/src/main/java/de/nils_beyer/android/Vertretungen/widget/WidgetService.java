package de.nils_beyer.android.Vertretungen.widget;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by nbeye on 03. Feb. 2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Service", "onGetViewFactory: ");
        return new WidgetFactory(getApplicationContext(), intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "onGetViewFactory: ");
        return super.onBind(intent);
    }
}
