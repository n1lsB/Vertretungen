package de.nils_beyer.android.Vertretungen.mainActivity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.Date;

import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.util.DateParser;
import de.nils_beyer.android.Vertretungen.R;


class OverviewSectionsAdapter extends FragmentPagerAdapter {

    private OverviewFragment f0;
    private OverviewFragment f1;
    private Context context;
    private DownloadingActivity downloadingActivity;

    OverviewSectionsAdapter(Context c, FragmentManager fm, DownloadingActivity _downloadingActivity) {
        super(fm);
        context = c;
        downloadingActivity = _downloadingActivity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                f0 = OverviewFragment.getIntance(context, StudentStorage.source.Today);
                return f0;
            case 1:
                f1 = OverviewFragment.getIntance(context, StudentStorage.source.Tomorrow);
                return f1;
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment of = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case 0:
                f0 = (OverviewFragment) of;
                break;
            case 1:
                f1 = (OverviewFragment) of;
                break;
        }

        checkIfDownloading();

        return of;
    }

    @Override
    public int getCount() {
        // Heute und Morgen => 2
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final Date dateToday = StudentStorage.getDateToday(context);
        final Date dateTomorrow = StudentStorage.getDateTomorrow(context);

        switch (position) {
            case 0:
                if (dateToday == null)
                    return context.getString(R.string.today);
                else
                    return DateParser.parseDateToShortString(context, dateToday);

            case 1:
                if (dateTomorrow == null)
                    return context.getString(R.string.tomorrow);
                else
                    return DateParser.parseDateToShortString(context, dateTomorrow);
        }
        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        // Update Fragments
        if (f0 != null) {
            f0.updateData();
        }

        if (f1 != null) {
            f1.updateData();
        }

        super.notifyDataSetChanged();
    }

    public void hideDownloading() {
        // Hide Spinning Button
        if (f0 != null) {
            f0.resetSwipeRefreshLayout();
        }

        if (f1 != null) {
            f1.resetSwipeRefreshLayout();
        }
    }

    public void showDownloading() {
        // Enable Spinning Button


        if (f0 != null) {
            f0.showSwipeRefreshLayout();
        }

        if (f1 != null) {
            f1.showSwipeRefreshLayout();
        }
    }

    private void checkIfDownloading() {
        if (downloadingActivity.isDownloading()) {
            showDownloading();
        } else {
            hideDownloading();
        }
    }

    public interface DownloadingActivity {
        boolean isDownloading();
    }
}