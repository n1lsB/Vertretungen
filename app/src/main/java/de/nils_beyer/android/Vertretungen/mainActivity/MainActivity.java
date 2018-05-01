package de.nils_beyer.android.Vertretungen.mainActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.nils_beyer.android.Vertretungen.account.AccountSpinner;
import de.nils_beyer.android.Vertretungen.download.StudentDownloadService;
import de.nils_beyer.android.Vertretungen.InfoActivity;
import de.nils_beyer.android.Vertretungen.LoginActivity;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.account.StudentAccount;
import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.preferences.MarkedCoursesActivity;
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

        mOverviewSectionsAdapter = new OverviewSectionsAdapter(getApplication(), getSupportFragmentManager(), this, accountSpinner.getToday(), accountSpinner.getTomorrow());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mOverviewSectionsAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (ChromeCustomTabsFAB) findViewById(R.id.floatingActionButton);
        fab.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update AccountSpinner in case the
        // user added another account
        accountSpinner.checkAccountAvaibility();

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

        if (AccountSpinner.hasOnlyUnregistered(getApplicationContext())) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // Update AccountSpinner in case the
        // user added another account
        accountSpinner.checkAccountAvaibility();

        if (getIntent().hasExtra(RefreshKey) && getIntent().getBooleanExtra(RefreshKey, false)) {
            getIntent().removeExtra(RefreshKey);
            requestData();
        }

        if (!accountSpinner.containsData()) {
            requestData();
        }
    }

    public boolean requestData() {
        if (isDownloading) {
            return false;
        }

        isDownloading = true;
        mOverviewSectionsAdapter.showDownloading();

        // Start Downloading Service
        PendingIntent pendingResult = createPendingResult(
                KlasseRequestCode, new Intent(), 0);
        accountSpinner.startDownload(pendingResult);

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KlasseRequestCode ) {
            if (resultCode == StudentDownloadService.RESULT_CODE) {
                update();
                isDownloading = false;

                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.download_success), Snackbar.LENGTH_SHORT);
                snackbar.show();
                mOverviewSectionsAdapter.hideDownloading();
            } else if (resultCode == StudentDownloadService.ERROR_CODE) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.io_error), Snackbar.LENGTH_LONG);

                isDownloading = false;
                mOverviewSectionsAdapter.hideDownloading();
                snackbar.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if (AccountSpinner.hasOnlyRegistered(getApplicationContext())) {
            menu.removeItem(R.id.menu_item_login);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
            case R.id.menu_item_marked_courses:
                startActivity(new Intent(this, MarkedCoursesActivity.class));
                return true;
            case R.id.menu_item_logout:
                accountSpinner.logout();
                if (AccountSpinner.hasOnlyUnregistered(getApplicationContext())) {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                accountSpinner.init();
                invalidateOptionsMenu();
                return true;
            case R.id.menu_item_login:
                startActivity(new Intent(this, LoginActivity.class));
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
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                // Today
                return accountSpinner.getUrlToday();
            case 1:
                // Tomorrow
                return accountSpinner.getUrlTomorrow();
            default:
                return null;
        }
    }

    @Override
    /**
     * Returns the HTTP Header authorization property for login. Used for the chrome custom tab button.
     */
    public String getHttpHeaderAuthorization() {
        return accountSpinner.getHTTPHeaderAuthorization();
    }


    public void update() {
        mOverviewSectionsAdapter.update(accountSpinner.getToday(), accountSpinner.getTomorrow());
    }

    @Override
    public boolean isDownloading() {
        return isDownloading;
    }

    @Override
    public void onAccountChanged() {
        if (!accountSpinner.containsData()) {
            requestData();
        }
        update();
    }
}
