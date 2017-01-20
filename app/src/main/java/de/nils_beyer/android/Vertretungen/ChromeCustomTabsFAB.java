package de.nils_beyer.android.Vertretungen;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import de.nils_beyer.android.Vertretungen.data.DataModel;


public final class ChromeCustomTabsFAB extends FloatingActionButton {
    TabActivity tabActivity;

    public ChromeCustomTabsFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setup(TabActivity tabActivity) {
        this.tabActivity = tabActivity;
        init();
    }


    private void init() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder customtabintent = new CustomTabsIntent.Builder();


                if (Build.VERSION.SDK_INT >= 23) {
                    customtabintent.setToolbarColor(getContext().getColor(R.color.colorPrimary));
                }

                customtabintent.setShowTitle(true);


                Bundle headerArgs = new Bundle();
                headerArgs.putString("Authorization", Account.generateHTTPHeaderAuthorization(getContext()));

                customtabintent.build().intent.putExtra(Browser.EXTRA_HEADERS, headerArgs);

                try {
                    if (tabActivity.getSelectedSource() == DataModel.source.Today) {
                        customtabintent.build().launchUrl(getContext(), Uri.parse(DownloadIntentService.URL_TODAY));
                    } else {
                        customtabintent.build().launchUrl(getContext(), Uri.parse(DownloadIntentService.URL_TOMORROW));
                    }
                } catch(Exception e) {
                    Toast.makeText(getContext(), "Kein Browser installiert", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public interface TabActivity {
        DataModel.source getSelectedSource();
    }
}
