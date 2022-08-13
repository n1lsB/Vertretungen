
package de.nils_beyer.android.Vertretungen.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.account.AccountSpinner;
import de.nils_beyer.android.Vertretungen.account.AvailableAccountsKt;


public class WidgetConfigureActivity extends AppCompatActivity  implements View.OnClickListener{

    private AccountSpinner accountSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configure);
        setResult(RESULT_CANCELED);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.widget_configure_toolbar_titel));
        setSupportActionBar(toolbar);

        accountSpinner = (AccountSpinner) findViewById(R.id.widget_config_accountSpinner);
        accountSpinner.setViewConfig(AccountSpinner.ViewConfig.SHOW_REGISTERED);

        if (accountSpinner.hasOnlyUnregistered(getApplicationContext())) {
            Toast.makeText(this, getString(R.string.widget_configure_no_account), Toast.LENGTH_LONG).show();
            finish();
        }

        Button applyButton = (Button) findViewById(R.id.widget_config_apply_button);
        applyButton.setOnClickListener(this);
    }

    @Override
    /**
     * Method that gets executed when applyButton is clicked
     */
    public void onClick(View v) {
        Bundle extras = getIntent().getExtras();
        int mAppWidgetId;

        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
            );
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

            Bundle widget_extras = new Bundle();
            final int selectedAccountId = AvailableAccountsKt.getAccountID(accountSpinner.getSelectedAccount());
            widget_extras.putInt(VertretungenWidgetProvider.EXTRA_ACCOUNT_ORDINAL, selectedAccountId);
            appWidgetManager.updateAppWidgetOptions(mAppWidgetId, widget_extras);

            // Store Configuration to WidgetStorage, because AppWidgetManager is
            // reset after a System reboot
            WidgetConfigurationStorage.setConfig(getApplicationContext(), mAppWidgetId, selectedAccountId);

            VertretungenWidgetProvider.updateWidgetData(getApplicationContext());

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }

}
