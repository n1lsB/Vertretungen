package de.nils_beyer.android.Vertretungen;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Date;
import de.nils_beyer.android.Vertretungen.data.DataModel;



class OverviewSectionsAdapter extends FragmentStatePagerAdapter {

    private OverviewFragment f0;
    private OverviewFragment f1;
    Context context;

    OverviewSectionsAdapter(Context c, FragmentManager fm) {
        super(fm);
        context = c;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                f0 = OverviewFragment.getIntance(context, DataModel.source.Today);
                return f0;
            case 1:
                f1 = OverviewFragment.getIntance(context, DataModel.source.Tomorrow);
                return f1;
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        // Heute und Morgen => 2
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final Date dateToday = DataModel.getDateToday(context);
        final Date dateTomorrow = DataModel.getDateTomorrow(context);

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
        // Disable Spinning Button
        if (f0 != null)
            f0.resetSwipeRefreshLayout();

        if (f1 != null) {
            f1.resetSwipeRefreshLayout();
        }

        super.notifyDataSetChanged();
    }
}