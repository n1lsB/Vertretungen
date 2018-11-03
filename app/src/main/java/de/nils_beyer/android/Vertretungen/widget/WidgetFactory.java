package de.nils_beyer.android.Vertretungen.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Date;

import de.nils_beyer.android.Vertretungen.account.Account;
import de.nils_beyer.android.Vertretungen.account.AccountSpinner;
import de.nils_beyer.android.Vertretungen.account.AvailableAccountsKt;
import de.nils_beyer.android.Vertretungen.account.StudentAccount;
import de.nils_beyer.android.Vertretungen.account.TeacherAccount;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.data.TeacherEntry;
import de.nils_beyer.android.Vertretungen.preferences.MarkedTeacher;
import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.util.DateParser;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;
import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Entry;

/**
 * WidgetFactory used to create ListView for Vertretungen-Entries.
 * Created by nbeyer on 03. Feb. 2017.
 */

class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    public static final String EXTRA_ACCOUNT_ORDINAL = "WidgetFactory.EXTRA_ACCOUNT_ORDINAL";

    private ArrayList<Group> klasses = new ArrayList<>();
    private GroupCollection groupCollection;
    private Context context = null;
    private int accountOrdinal;


    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
        accountOrdinal = intent.getIntExtra(EXTRA_ACCOUNT_ORDINAL, -1);
    }


    @Override
    public int getCount() {
        int count = 0;
        for (Group k : klasses) {
            count += k.replacements.size();
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Entry getReplacement(int pos) {
        int count = 0;
        for (Group k : klasses) {
            if (count + k.replacements.size() > pos) {
                return k.replacements.get(pos-count);
            }
            count += k.replacements.size();
        }
        return null;
    }

    private Group getKlasseAt(int pos) {
        int count = 0;
        for (Group k : klasses) {
            if (count + k.replacements.size() > pos) {
                return k;
            }
            count += k.replacements.size();
        }
        return null;
    }

    /*
     * Similar to getView of Adapter where instead of View
     * we return RemoteViews
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_row_layout);


        Entry r = getReplacement(position);

        if (r instanceof TeacherEntry) {
            TeacherEntry tr = (TeacherEntry) r;
            remoteView.setTextViewText(R.id.widget_row_type, r.vertretungsart);
            remoteView.setTextViewText(R.id.widget_row_group_name, getKlasseAt(position).name);
            remoteView.setTextViewText(R.id.widget_row_original, r.originalSubject);
            remoteView.setTextViewText(R.id.widget_row_replacement, r.modifiedSubject);
            remoteView.setTextViewText(R.id.widget_row_information, r.information);
            remoteView.setTextViewText(R.id.widget_row_time, String.format("%s. Stunde, %s", r.time, r.room));
            remoteView.setTextViewText(R.id.widget_row_klasse, tr.klasse);
            remoteView.setTextViewText(R.id.widget_row_old_teacher, tr.teacherOld);
            remoteView.setTextViewText(R.id.widget_row_new_teacher, tr.teacherOld);

            remoteView.setOnClickFillInIntent(R.id.widget_row_rootlayout, new Intent());
        } else {
            remoteView.setTextViewText(R.id.widget_row_type, r.vertretungsart);
            remoteView.setTextViewText(R.id.widget_row_group_name, getKlasseAt(position).name);
            remoteView.setTextViewText(R.id.widget_row_original, r.originalSubject);
            remoteView.setTextViewText(R.id.widget_row_replacement, r.modifiedSubject);
            remoteView.setTextViewText(R.id.widget_row_information, r.information);
            remoteView.setTextViewText(R.id.widget_row_time, String.format("%s. Stunde, %s", r.time, r.room));
            remoteView.setOnClickFillInIntent(R.id.widget_row_rootlayout, new Intent());

            remoteView.setViewVisibility(R.id.widget_row_klasse, View.GONE);
            remoteView.setViewVisibility(R.id.widget_row_new_teacher, View.GONE);
            remoteView.setViewVisibility(R.id.widget_row_old_teacher, View.GONE);

            if (r.originalSubject.equals(" ") && r.modifiedSubject.equals(" ")) {
                remoteView.setViewVisibility(R.id.widget_row_original, View.GONE);
                remoteView.setViewVisibility(R.id.widget_row_replacement, View.GONE);
                remoteView.setViewVisibility(R.id.widget_row_arrow, View.GONE);
            } else {
                remoteView.setViewVisibility(R.id.widget_row_original, View.VISIBLE);
                remoteView.setViewVisibility(R.id.widget_row_replacement, View.VISIBLE);
                remoteView.setViewVisibility(R.id.widget_row_arrow, View.VISIBLE);
            }
        }

        // Determinate the position of the Group inside the GroupCollection
        int gcposition = groupCollection.getGroupArrayList().indexOf(getKlasseAt(position));

        Intent onClickIntent = DetailActivity.Companion.getStartIntent(context, groupCollection, gcposition);


        remoteView.setOnClickFillInIntent(R.id.widget_row_rootlayout, onClickIntent);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        Log.d("Factory", "onDataSetChanged: ");

        Account account = AvailableAccountsKt.getAvailableAccounts()[accountOrdinal];
        Date dateToday = account.getAvailableDatasets()[0].getData(context).getDate();
        if (DateParser.after(dateToday, new Date()) || DateParser.sameDay(dateToday, new Date())) {
            groupCollection = account.getAvailableDatasets()[0].getData(context);
        } else {
            groupCollection = account.getAvailableDatasets()[1].getData(context);
        }

        StudentStorage.sort(context, groupCollection.getGroupArrayList());


        klasses = new ArrayList<>();
        boolean hasMarked = false;
        // TODO Implement marking classes/teachers inside account
        if (account == StudentAccount.INSTANCE) {
            hasMarked = MarkedKlasses.hasMarked(context);
        } else if (account == TeacherAccount.INSTANCE) {
            hasMarked = MarkedTeacher.hasMarked(context);
        }
        if (hasMarked) {
            for (Group k : groupCollection.getGroupArrayList()) {
                if (MarkedKlasses.isMarked(context, k.name) || MarkedTeacher.isMarked(context, k.name)) {
                    klasses.add(k);
                }
            }
        } else {
            for (Group g : groupCollection.getGroupArrayList()) {
                klasses.add(g);
            }
        }
    }

    @Override
    public void onDestroy() {
    }


}
