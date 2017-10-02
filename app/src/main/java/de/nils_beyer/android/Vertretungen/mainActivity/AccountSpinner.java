package de.nils_beyer.android.Vertretungen.mainActivity;

import android.accounts.OnAccountsUpdateListener;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.download.StudentDownloadService;
import de.nils_beyer.android.Vertretungen.download.TeacherDownloadService;
import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.storage.TeacherStorage;

/**
 * Created by nbeye on 02. Okt. 2017.
 */

public class AccountSpinner extends AppCompatSpinner {
    private enum Account {Teacher, Student};

    private AccountSpinner.Account selectedAccount;
    private onAccountChangeListener accountChangeListener;

    public AccountSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setup(onAccountChangeListener onAccountChangeListener) {
        accountChangeListener = onAccountChangeListener;
    }

    private void init() {
        ArrayList<String> accountList = new ArrayList<>();
        accountList.add(getContext().getString(R.string.account_name_student));
        accountList.add(getContext().getString(R.string.account_name_teacher));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.account_spinner_list_item,
                accountList
        );
        setAdapter(arrayAdapter);

        setSelection(0);
        selectedAccount = Account.Student;

        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((String) getSelectedItem()).equals(getContext().getString(R.string.account_name_student))) {
                    selectedAccount = Account.Student;
                } else {
                    selectedAccount = Account.Teacher;
                }

                if (accountChangeListener != null) {
                    accountChangeListener.onAccountChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    public GroupCollection getToday() {
        switch(selectedAccount) {
            case Teacher:
                return TeacherStorage.getTodaySource(getContext());
            case Student:
                return StudentStorage.getTodaySource(getContext());
        }
        return null;
    }

    public GroupCollection getTomorrow() {
        switch(selectedAccount) {
            case Teacher:
                return TeacherStorage.getTomorrowSource(getContext());
            case Student:
                return StudentStorage.getTomorrowSource(getContext());
        }
        return null;
    }

    public void startDownload(PendingIntent pendingIntent) {
        Intent startIntent;
        switch (selectedAccount) {
            case Student:
                startIntent = new Intent(getContext(), StudentDownloadService.class);
                break;
            case Teacher:
                startIntent = new Intent(getContext(), TeacherDownloadService.class);
                break;
            default:
                return;
        }
        startIntent.putExtra(StudentDownloadService.PENDING_RESULT_EXTRA, pendingIntent);
        getContext().startService(startIntent);
    }

    public interface onAccountChangeListener {
        void onAccountChanged();
    }
}
