package de.nils_beyer.android.Vertretungen.preferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.nils_beyer.android.Vertretungen.R;

public class MarkedCoursesActivity extends AppCompatActivity {

    EditText klasse;
    EditText kurs;
    Button btn;
    Toolbar toolbar;
    RecyclerView recyclerView;
    MarkedCoursesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_courses);
        toolbar = (Toolbar) findViewById(R.id.marked_courses_toolbar);
        toolbar.setTitle(getString(R.string.marked_courses_title));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setHomeButtonEnabled(true);

        klasse = (EditText) findViewById(R.id.courses_klasse);
        kurs = (EditText) findViewById(R.id.courses_kurs);
        btn = (Button) findViewById(R.id.courses_btn);
        recyclerView = (RecyclerView) findViewById(R.id.courses_listview);
        adapter = new MarkedCoursesAdapter(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!klasse.getText().toString().isEmpty() && !kurs.getText().toString().isEmpty() &&
                        !MarkedCourses.isMarked(getApplicationContext(), klasse.getText().toString(), kurs.getText().toString())) {
                    MarkedCourses.setMarked(MarkedCoursesActivity.this, klasse.getText().toString(), kurs.getText().toString(), true);
                    adapter.add(MarkedCourses.handleInputString(klasse.getText().toString()) + "\\" +
                            MarkedCourses.handleInputString(kurs.getText().toString()));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marked_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.marked_courses_menu_remove_all:
                MarkedCourses.removeAll(getApplicationContext());
                adapter.clear();
                return true;
        }

        return false;
    }
}
