package de.nils_beyer.android.Vertretungen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.nils_beyer.android.Vertretungen.account.StudentAccount;
import de.nils_beyer.android.Vertretungen.download.DownloadIntentService;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button btn_login;
    TextView text_error;
    View contentView;
    ProgressBar progressbar;

    public boolean canClose = false;
    public static String KEY_REGISTERED = "REGISTERED_KEY_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 23)
            getWindow().setStatusBarColor(getResources().getColor(R.color.login_color, getTheme()));

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        btn_login = (Button) findViewById(R.id.login_btn_login);
        text_error = (TextView) findViewById(R.id.login_error);
        contentView = findViewById(R.id.activity_login);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                progressbar.setVisibility(View.VISIBLE);
                btn_login.setEnabled(false);
                checkPassword(new CheckPasswordCallback() {
                    @Override
                    public void onResult(boolean accepted) {
                        progressbar.setVisibility(View.GONE);
                        btn_login.setEnabled(true);
                        if (accepted) {
                            StudentAccount.register(getApplicationContext(), username.getText().toString(), password.getText().toString());

                            canClose = true;
                            finish();
                        } else {
                            text_error.setVisibility(View.VISIBLE);
                            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        progressbar.setVisibility(View.GONE);
                        btn_login.setEnabled(true);
                        Snackbar.make(contentView, getString(R.string.io_error), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
        if (!canClose) {
            finishAffinity();
        } else {
            super.finish();
        }
    }


    protected void checkPassword(final CheckPasswordCallback checkPasswordCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(DownloadIntentService.URL_TODAY).openConnection();
                    urlConnection.setRequestProperty("Authorization", StudentAccount.generateHTTPHeaderAuthorization(
                            username.getText().toString(),
                            password.getText().toString()
                        )
                    );


                    final int responseCode = urlConnection.getResponseCode();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseCode == 200) {
                                checkPasswordCallback.onResult(true);
                            } else {
                                checkPasswordCallback.onResult(false);
                            }
                        }
                    });


                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            checkPasswordCallback.onError(e);
                        }
                    });
                }
            }
        }).start();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private interface CheckPasswordCallback {
        void onResult(boolean accepted);
        void onError(Exception e);
    }
}
