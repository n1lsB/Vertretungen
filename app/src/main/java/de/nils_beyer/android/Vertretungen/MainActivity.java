package de.nils_beyer.android.Vertretungen;

import android.app.PendingIntent;
import android.app.ProgressDialog;
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

import de.nils_beyer.android.Vertretungen.data.DataModel;
import de.nils_beyer.android.Vertretungen.preferences.MarkedCoursesActivity;

public class MainActivity extends AppCompatActivity implements ChromeCustomTabsFAB.TabActivity {

    public static int KlasseRequestCode = 6;
    public static String RefreshKey = "REFRESH_KEY";



    public static final String isDownloadingKey = "IS_DOWNLOADING_KEY";


    private OverviewSectionsAdapter mOverviewSectionsAdapter;


    private boolean isDownloading = false;

    private TabLayout tabLayout;
    private ProgressDialog progressDialog;
    private ChromeCustomTabsFAB fab;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Account.isRegistered(this)) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        mOverviewSectionsAdapter = new OverviewSectionsAdapter(getApplication(), getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mOverviewSectionsAdapter);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (ChromeCustomTabsFAB) findViewById(R.id.floatingActionButton);
        fab.setup(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().hasExtra(RefreshKey) && getIntent().getBooleanExtra(RefreshKey, false)) {
            getIntent().removeExtra(RefreshKey);
            requestData();
        }

        if (!DataModel.containsData(getApplicationContext())) {
                requestData();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mOverviewSectionsAdapter.notifyDataSetChanged();
    }

    public void requestData() {
        if (isDownloading || !Account.isRegistered(this)) {
            return;
        }

        showProgressDialog();
        isDownloading = true;



        PendingIntent pendingResult = createPendingResult(
                KlasseRequestCode, new Intent(), 0);
        Intent intent = new Intent(getApplicationContext(), DownloadIntentService.class);
        intent.putExtra(DownloadIntentService.PENDING_RESULT_EXTRA, pendingResult);

        startService(intent);
    }

    protected void showProgressDialog() {
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle(getString(R.string.loading_text));
        progressDialog.setMessage(getString(R.string.loading_subtext));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        //progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KlasseRequestCode ) {
            if (resultCode == DownloadIntentService.RESULT_CODE) {
                mOverviewSectionsAdapter.notifyDataSetChanged();

                isDownloading = false;
                hideProgressDialog();

                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.download_success), Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else if (resultCode == DownloadIntentService.ERROR_CODE) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.io_error), Snackbar.LENGTH_LONG);

                isDownloading = false;
                hideProgressDialog();
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
            case R.id.menu_item_marked_courses:
                startActivity(new Intent(this, MarkedCoursesActivity.class));
                return true;
            case R.id.menu_item_logout:
                Account.logout(this);
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean(isDownloadingKey)) {
            isDownloading = true;
            showProgressDialog();
        } else {
            isDownloading = false;
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(isDownloadingKey, isDownloading);
        super.onSaveInstanceState(outState);
    }

    @Override
    public DataModel.source getSelectedSource() {
        switch(tabLayout.getSelectedTabPosition()) {
            case 0:
                return DataModel.source.Today;
            case 1:
                return DataModel.source.Tomorrow;
            default:
                return null;
        }
    }
}
