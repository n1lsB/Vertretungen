package de.nils_beyer.android.Vertretungen.mainActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import de.nils_beyer.android.Vertretungen.account.AccountSpinner;
import de.nils_beyer.android.Vertretungen.account.AvailableAccountsKt;
import de.nils_beyer.android.Vertretungen.account.Dataset;
import de.nils_beyer.android.Vertretungen.download.DownloadResultCodes;
import de.nils_beyer.android.Vertretungen.InfoActivity;
import de.nils_beyer.android.Vertretungen.LoginActivity;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.events.EventsDownloadService;
import de.nils_beyer.android.Vertretungen.widget.VertretungenWidgetProvider;

public class MainActivity extends AppCompatActivity implements ChromeCustomTabsFAB.TabActivity, OverviewSectionsAdapter.DownloadingActivity, AccountSpinner.onAccountChangeListener{

    public static int KlasseRequestCode = 6;
    public static String RefreshKey = "REFRESH_KEY";



    public static final String isDownloadingKey = "IS_DOWNLOADING_KEY";

    private OverviewSectionsAdapter mOverviewSectionsAdapter;
    private boolean isDownloading = false;
    private TabLayout tabLayout;
    private ChromeCustomTabsFAB fab;
    private AccountSpinner accountSpinner;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        accountSpinner = (AccountSpinner) findViewById(R.id.main_account_spinner);
        accountSpinner.setup(this);

        mOverviewSectionsAdapter = new OverviewSectionsAdapter(getApplication(),
                getSupportFragmentManager(),
                this,
                accountSpinner.getSelectedAccount());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mOverviewSectionsAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        // automatically go to the second tab
        tabLayout.getTabAt(1).select();

        fab = (ChromeCustomTabsFAB) findViewById(R.id.floatingActionButton);
        fab.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update AccountSpinner in case the
        // user added another account
        accountSpinner.updateAccountSpinner();

        invalidateOptionsMenu();

        // NotifyDataSetChanged in order to refresh
        // order of Klasses (e.g. in case of a new marked item)
        update();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Update the Widget in case the
        // date changed
        VertretungenWidgetProvider.updateWidgetData(getApplicationContext());

        if (accountSpinner.hasOnlyUnregistered(getApplicationContext())) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // Update AccountSpinner in case the
        // user added another account
        accountSpinner.updateAccountSpinner();

        if (getIntent().hasExtra(RefreshKey) && getIntent().getBooleanExtra(RefreshKey, false)) {
            getIntent().removeExtra(RefreshKey);
            requestData();
        }

        if (!accountSpinner.getSelectedAccount().containsData(this)) {
            requestData();
        }
    }

    public boolean requestData() {
        if (isDownloading) {
            return false;
        }

        // Download new events
        EventsDownloadService.startDownload(this, createPendingResult(0, new Intent(), 0));

        isDownloading = true;
        mOverviewSectionsAdapter.showDownloading();

        // Start Downloading Service
        PendingIntent pendingResult = createPendingResult(
                KlasseRequestCode, new Intent(), 0);
        accountSpinner.getSelectedAccount().startDownload(this, pendingResult);

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == KlasseRequestCode ) {
            if (resultCode == DownloadResultCodes.RESULT_SUCCESS.ordinal()) {
                mOverviewSectionsAdapter.hideDownloading();
                //update(); we don't need to update here, because onStart() automatically calls update
                isDownloading = false;

                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.download_success), Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else if (resultCode == DownloadResultCodes.RESULT_ERROR.ordinal()) {
                mOverviewSectionsAdapter.hideDownloading();
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.io_error), Snackbar.LENGTH_LONG);

                isDownloading = false;
                snackbar.show();
            } else if (resultCode == DownloadResultCodes.RESULT_AUTHENTICATION_ERROR.ordinal()) {
                mOverviewSectionsAdapter.hideDownloading();
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.authentication_error), Snackbar.LENGTH_LONG);

                final int accountId = data.getIntExtra(DownloadResultCodes.RESULT_AUTHENTICATION_ERROR.getAdditionalDataKey(), -1);
                if (accountId != -1) {
                    snackbar.setAction(getString(R.string.authentication_error_reregister), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AvailableAccountsKt.getAvailableAccounts()[accountId].logout(getApplicationContext());
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            invalidateOptionsMenu();
                        }
                    });
                }
                isDownloading = false;
                snackbar.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isDownloading = savedInstanceState.getBoolean(isDownloadingKey);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(isDownloadingKey, isDownloading);
        super.onSaveInstanceState(outState);
    }

    @Override
    /**
     * Returns the origin URL of the selected tab. Used for the chrome custom tab button
     */
    public String getSelectedURL() {
        Dataset d = mOverviewSectionsAdapter.getDataset(tabLayout.getSelectedTabPosition());
        if (d != null) {
            return d.getURL();
        } else {
            return null;
        }
    }

    @Override @NotNull
    /**
     * Returns the HTTP Header authorization property for login. Used for the chrome custom tab button.
     */
    public String getHttpHeaderAuthorization() {
        return accountSpinner.getSelectedAccount().generateHTTPHeaderAuthorization(this);
    }


    public void update() {
        mOverviewSectionsAdapter.update(
                accountSpinner.getSelectedAccount());
    }

    @Override
    public boolean isDownloading() {
        return isDownloading;
    }

    @Override
    public void onAccountChanged() {
        if (!accountSpinner.getSelectedAccount().containsData(this)) {
            requestData();
        }
        update();
    }
}
