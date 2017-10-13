package de.nils_beyer.android.Vertretungen.account;

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
    private enum Account {Teacher, Student}
    public enum ViewConfig {SHOW_REGISTERED, SHOW_UNREGISTERED}

    private AccountSpinner.Account selectedAccount;
    private onAccountChangeListener accountChangeListener;
    private ViewConfig viewConfig = ViewConfig.SHOW_REGISTERED;

    public AccountSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setup(onAccountChangeListener onAccountChangeListener) {
        accountChangeListener = onAccountChangeListener;
    }

    public void setViewConfig(ViewConfig config) {
        viewConfig = config;
        init();
    }

    public void init() {
        ArrayList<String> accountList = getAvaibleAccountNames();


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                accountList
        );
        setAdapter(arrayAdapter);

        setSelection(0);
        if (accountList.size() > 0) {
            if ((accountList.get(0)).equals(getContext().getString(R.string.account_name_student))) {
                selectedAccount = Account.Student;
            } else {
                selectedAccount = Account.Teacher;
            }
        } else {
            selectedAccount = Account.Student;
        }

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
                ArrayList<String> accountList = new ArrayList<>();
                final String student_account_name = getContext().getString(R.string.account_name_student);
                final String teacher_account_name = getContext().getString(R.string.account_name_teacher);


                boolean studentReg = StudentAccount.isRegistered(getContext());
                if (studentReg && viewConfig == ViewConfig.SHOW_REGISTERED ||
                        !studentReg && viewConfig == ViewConfig.SHOW_UNREGISTERED) {
                    accountList.add(student_account_name);
                }

                boolean teacherReg = TeacherAccount.isRegistered(getContext());
                if (teacherReg && viewConfig == ViewConfig.SHOW_REGISTERED ||
                        !teacherReg && viewConfig == ViewConfig.SHOW_UNREGISTERED) {
                    accountList.add(teacher_account_name);
                }
            }
        });
    }


    public void checkAccountAvaibility() {
        ArrayList<String> accountList = getAvaibleAccountNames();
        for (int i = 0; i < accountList.size(); i++) {
            if (getAdapter().getCount() - 1 < i) {
                init();
                break;
            }

            if (!accountList.get(i).equals(getAdapter().getItem(i))) {
                init();
                break;
            }
        }
    }

    private ArrayList<String> getAvaibleAccountNames() {
        ArrayList<String> accountList = new ArrayList<>();
        final String student_account_name = getContext().getString(R.string.account_name_student);
        final String teacher_account_name = getContext().getString(R.string.account_name_teacher);
        final boolean studentReg = StudentAccount.isRegistered(getContext());
        final boolean teacherReg = TeacherAccount.isRegistered(getContext());



        if (viewConfig == ViewConfig.SHOW_REGISTERED) {
            if (teacherReg) {
                accountList.add(teacher_account_name);
            }
            if (studentReg) {
                accountList.add(student_account_name);
            }
        } else if (viewConfig == ViewConfig.SHOW_UNREGISTERED) {
            if (!studentReg) {
                accountList.add(student_account_name);
            }
            if (!teacherReg) {
                accountList.add(teacher_account_name);
            }
        } else {
            throw new RuntimeException("not yet implemented");
        }

        return accountList;
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

    public boolean containsData() {
        switch(selectedAccount) {
            case Teacher:
                return TeacherStorage.containsData(getContext());
            case Student:
                return StudentStorage.containsData(getContext());
        }
        return false;
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

    public static boolean hasOnlyUnregistered(Context c) {
        return !StudentAccount.isRegistered(c) && !TeacherAccount.isRegistered(c);
    }

    public static boolean hasOnlyRegistered(Context c) {
        return StudentAccount.isRegistered(c) && TeacherAccount.isRegistered(c);
    }

    public boolean tryRegister(final String username, final String password) throws Exception{
        final Account account = this.selectedAccount;
        switch (account) {
            case Student:
                StudentDownloadService.downloadHTMLFile(getContext(), StudentDownloadService.URL_TODAY, username, password);
                StudentAccount.register(getContext(), username, password);
                return true;
            case Teacher:
                TeacherDownloadService.downloadHTMLFile(getContext(), TeacherDownloadService.URL_TODAY, username, password);
                TeacherAccount.register(getContext(), username, password);
                return true;
            default:
                return false;
        }
    }

    public void logout() {
        switch(selectedAccount) {
            case Student:
                StudentAccount.logout(getContext());
                break;
            case Teacher:
                TeacherAccount.logout(getContext());
                break;
        }
    }

    public String getUrlToday() {
        switch(selectedAccount) {
            case Student:
                return StudentDownloadService.URL_TODAY;
            case Teacher:
                return TeacherDownloadService.URL_TODAY;
            default:
                return "https://burgaugymnasium.de";
        }
    }

    public String getUrlTomorrow() {
        switch(selectedAccount) {
            case Student:
                return StudentDownloadService.URL_TOMORROW;
            case Teacher:
                return TeacherDownloadService.URL_TOMORROW;
            default:
                return "https://burgaugymnasium.de";
        }
    }

    public String getHTTPHeaderAuthorization() {
        switch(selectedAccount) {
            case Student:
                return StudentAccount.generateHTTPHeaderAuthorization(getContext());
            case Teacher:
                return TeacherAccount.generateHTTPHeaderAuthorization(getContext());
            default:
                return "";
        }
    }
}
