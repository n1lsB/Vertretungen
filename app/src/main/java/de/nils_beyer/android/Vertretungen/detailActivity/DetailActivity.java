package de.nils_beyer.android.Vertretungen.detailActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Date;

import de.nils_beyer.android.Vertretungen.DateParser;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;

public class DetailActivity extends AppCompatActivity {


    public static final String ARG_KLASSE_EXTRA = "ARG_KLASSE_EXTRA";
    public static final String ARG_DATE_EXTRA = "ARG_DATE_EXTRA";

    private Group group;
    private Date date;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = (Group) getIntent().getSerializableExtra(ARG_KLASSE_EXTRA);
        date = (Date) getIntent().getSerializableExtra(ARG_DATE_EXTRA);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(group.name);
        toolbar.setSubtitle(DateParser.parseDateToShortString(this, date));


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        getSupportActionBar().setHomeButtonEnabled(true);


        fragmentContainer = (FrameLayout) findViewById(R.id.detail_fragment_container);

        if(savedInstanceState == null) {
            DetailFragment detailFragment = DetailFragment.newInstance(group);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragmentContainer.getId(), detailFragment)
                    .commit();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);

        if (MarkedKlasses.isMarked(getApplication(), group.name)) {
            menu.findItem(R.id.menu_detail_star).setIcon(R.drawable.ic_star_white_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail_star:
                MarkedKlasses.setMarked(getApplication(), group.name, !MarkedKlasses.isMarked(getApplication(), group.name));
                if (MarkedKlasses.isMarked(getApplication(), group.name)) {
                    item.setIcon(R.drawable.ic_star_white_24dp);
                } else {
                    item.setIcon(R.drawable.ic_star_border_white_24dp);
                }
                return true;

        }
        return false;
    }



}