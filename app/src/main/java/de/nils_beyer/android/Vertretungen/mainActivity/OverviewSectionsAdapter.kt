package de.nils_beyer.android.Vertretungen.mainActivity


import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import de.nils_beyer.android.Vertretungen.R


import de.nils_beyer.android.Vertretungen.util.DateParser
import de.nils_beyer.android.Vertretungen.account.Dataset
import de.nils_beyer.android.Vertretungen.account.Account


internal class OverviewSectionsAdapter(private val context: Context, fm: FragmentManager, private val downloadingActivity: DownloadingActivity, private var account : Account<out Dataset>) : FragmentStatePagerAdapter(fm) {

    //private var fragments = arrayOfNulls<Fragment>(count);
    private var datasets = account.getAvailableDatasets().map { it.getData(context) }

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return EventListFragment();
        } else {
            return OverviewFragment.getIntance(context, datasets[position - 1], position)
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val of = super.instantiateItem(container, position)
        if (of is OverviewFragment) {
            checkIfDownloading()
        }
        return of
    }

    override fun getCount(): Int = account.getAvailableDatasets().size + 1 // plus one for the calendar view

    override fun getPageTitle(position: Int): CharSequence? {
        if (position == 0) {
            // calendar view
            return context.getString(R.string.tab_events_name)
        } else {
            // substitutions view
            val date = datasets[position - 1].date

            if (date == null) {
                return String.format(context.getString(R.string.tab_substitutions_name),
                        position - 1)
            } else {
                return DateParser.parseDateToShortString(context, date)
            }
        }
    }

    override fun getItemPosition(pFragment: Any): Int {
        if (pFragment is OverviewFragment) {
            val position = pFragment.position;
            pFragment.updateData(datasets[position - 1])
            return position
        } else if (pFragment is EventListFragment) {
            pFragment.update()
            return 0
        }
        return FragmentStatePagerAdapter.POSITION_NONE
    }


    /**
     * Apply the Adapter to a new account and enforce a complete reload of the fragments
     * @param account the account which data should now be displayed.
     */
    fun update(account: Account<*>) {
        this.account = account;
        datasets = account.getAvailableDatasets().map { it.getData(context) }
        // When the new account has the same amount of tabs as the previous one,
        // we can update each tab so that the fragment stays in place.
        /*if (fragments.size == count) {

        } else {
            // if not, we have to create a new array of fragments where we store
            // the fragments
            val fragmentsCopy = arrayOfNulls<Fragment>(count)
            fragments.forEachIndexed { index, overviewFragment ->
                if (overviewFragment is OverviewFragment && index < account.getAvailableDatasets().size)
                    fragmentsCopy[index] = overviewFragment
            }
            fragments = fragmentsCopy
        }*/

        notifyDataSetChanged()
    }

    fun getDataset(position : Int) : Dataset? {
        if (position - 1 < 0 || position - 1 >= account.getAvailableDatasets().size) {
            //throw RuntimeException("no groupcollection at index ${position} available.")
            return null;
        }
        return account.getAvailableDatasets()[position - 1]
    }

    fun hideDownloading() {
        // Hide Spinning Button
        /*fragments.forEach {
            if (it is OverviewFragment)
                it?.resetSwipeRefreshLayout()
        }*/
    }

    fun showDownloading() {
        // Enable Spinning Button
        /*fragments.forEach {
            if (it is OverviewFragment)
                it?.showSwipeRefreshLayout()
        }*/
    }

    private fun checkIfDownloading() {
        if (downloadingActivity.isDownloading) {
            showDownloading()
        } else {
            hideDownloading()
        }
    }

    interface DownloadingActivity {
        val isDownloading: Boolean
    }
}