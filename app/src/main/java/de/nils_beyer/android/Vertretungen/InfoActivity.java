package de.nils_beyer.android.Vertretungen;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.info_title));

        // Setup back icon
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Setup App Titel with Version
        TextView appName = (TextView) findViewById(R.id.info_app_name);
        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {}

        appName.setText(String.format(getString(R.string.info_app_with_version), version));

        // Setup BuildVariant Label
        TextView buildVariant = (TextView) findViewById(R.id.info_build_variant);
        if (version.endsWith("D")) {
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(Color.RED);

            toolbar.setBackgroundColor(Color.RED);
            buildVariant.setText(String.format(getString(R.string.info_warning_debug), "DEBUG"));
        } else if (version.endsWith("R")) {
            buildVariant.setText(String.format(getString(R.string.info_warning_debug), "RELEASE"));
        } else {
            buildVariant.setVisibility(View.GONE);
        }

        // Link to license Activity
        Button btn_license = (Button) findViewById(R.id.info_button_license);
        btn_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startActivity = new Intent(InfoActivity.this, LicenseActivity.class);
                startActivity(startActivity);
            }
        });

    }

}
