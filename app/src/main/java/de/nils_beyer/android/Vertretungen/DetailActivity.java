package de.nils_beyer.android.Vertretungen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import de.nils_beyer.android.Vertretungen.data.Klasse;
import de.nils_beyer.android.Vertretungen.data.Replacements;
import de.nils_beyer.android.Vertretungen.preferences.MarkedCourses;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;

public class DetailActivity extends AppCompatActivity {


    public static final String ARG_KLASSE_EXTRA = "ARG_KLASSE_EXTRA";
    public static final String ARG_DATE_EXTRA = "ARG_DATE_EXTRA";
    private Klasse klasse;
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        klasse = (Klasse) getIntent().getSerializableExtra(ARG_KLASSE_EXTRA);
        date = (Date) getIntent().getSerializableExtra(ARG_DATE_EXTRA);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(klasse.name);
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_detail);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DetailAdapter());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);

        if (MarkedKlasses.isMarked(getApplication(), klasse.name)) {
            menu.findItem(R.id.menu_detail_star).setIcon(R.drawable.ic_star_white_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail_star:
                MarkedKlasses.setMarked(getApplication(), klasse.name, !MarkedKlasses.isMarked(getApplication(), klasse.name));
                if (MarkedKlasses.isMarked(getApplication(), klasse.name)) {
                    item.setIcon(R.drawable.ic_star_white_24dp);
                } else {
                    item.setIcon(R.drawable.ic_star_border_white_24dp);
                }
                return true;

        }
        return false;
    }


    private class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView text_original;
            TextView text_time;
            TextView text_replacement;
            TextView text_info;
            TextView text_type;
            TextView text_room;
            TextView text_arrow;
            CardView overview_card;
            TextView text_reference;
            ImageView marked_cours;

            public ViewHolder(View v) {
                super(v);

                text_original = (TextView) v.findViewById(R.id.text_original);
                text_time = (TextView) v.findViewById(R.id.text_time);
                text_replacement = (TextView) v.findViewById(R.id.text_modified);
                text_type = (TextView) v.findViewById(R.id.text_type);
                text_info = (TextView) v.findViewById(R.id.text_info);
                text_room = (TextView) v.findViewById(R.id.text_room);
                text_arrow = (TextView) v.findViewById(R.id.text_arrow);
                text_reference = (TextView) v.findViewById(R.id.text_reference);
                overview_card = (CardView) v.findViewById(R.id.overview_card);
                marked_cours = (ImageView) v.findViewById(R.id.icon_marked_courses);
            }

            public void bind(Replacements r) {
                text_original.setText(r.originalSubject);
                text_time.setText(String.format(getString(R.string.placeholder_time), r.time));
                text_replacement.setText(r.modifiedSubject);
                text_info.setText(r.information);
                text_type.setText(r.type);
                text_room.setText(r.room);

                if (MarkedCourses.isMarked(getApplicationContext(), klasse.name, r.originalSubject) ||
                        MarkedCourses.isMarked(getApplicationContext(), klasse.name, r.modifiedSubject)) {
                    marked_cours.setVisibility(View.VISIBLE);
                } else {
                    marked_cours.setVisibility(View.GONE);
                }

                if (r.originalSubject.equals(" ") && r.modifiedSubject.equals(" ")) {
                    text_original.setVisibility(View.GONE);
                    text_replacement.setVisibility(View.GONE);
                    text_arrow.setVisibility(View.GONE);
                } else {

                }

                if(r.information.equals(" ")) {
                    text_info.setVisibility(View.GONE);
                } else {
                    text_info.setVisibility(View.VISIBLE);
                }

                if (r.hasReference()) {
                    text_reference.setVisibility(View.VISIBLE);
                    text_reference.setText(String.format(getString(R.string.placeholder_reference), r.reference));
                } else {
                    text_reference.setVisibility(View.GONE);
                }

            }
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(DetailActivity.this.klasse.replacements[position]);
        }

        @Override
        public int getItemCount() {
            return DetailActivity.this.klasse.replacements.length;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_detail_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
    }
}
