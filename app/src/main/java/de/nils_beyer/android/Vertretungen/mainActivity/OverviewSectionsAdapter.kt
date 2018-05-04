package de.nils_beyer.android.Vertretungen.mainActivity


import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup



import de.nils_beyer.android.Vertretungen.util.DateParser
import de.nils_beyer.android.Vertretungen.account.Dataset
import de.nils_beyer.android.Vertretungen.account.Account


internal class OverviewSectionsAdapter(private val context: Context, fm: FragmentManager, private val downloadingActivity: DownloadingActivity, private var account : Account<out Dataset>) : FragmentStatePagerAdapter(fm) {

    private var fragments = arrayOfNulls<OverviewFragment>(count)

    override fun getItem(position: Int): Fragment? {
        return OverviewFragment.getIntance(context, account.getAvailableDatasets()[position].getData(context))
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val of = super.instantiateItem(container, position) as OverviewFragment
        fragments[position] = of

        checkIfDownloading()

        return of
    }

    override fun getCount(): Int = account.getAvailableDatasets().size

    override fun getPageTitle(position: Int): CharSequence? {
        val date = account.getAvailableDatasets()[position].getData(context).date

        if (date == null) {
            return "Ansicht " + position
        } else {
            return DateParser.parseDateToShortString(context, date)
        }
    }


    override fun notifyDataSetChanged() {
        // Update Fragments
        fragments.forEachIndexed { index, overviewFragment ->
            overviewFragment?.updateData(account.getAvailableDatasets()[index].getData(context))
        }

        super.notifyDataSetChanged()
    }

    /**
     * Apply the Adapter to a new account and enforce a complete reload of the fragments
     * @param account the account which data should now be displayed.
     */
    fun update(account: Account<*>) {
        this.account = account;
        // When the new account has the same amount of tabs as the previous one,
        // we can update each tab so that the fragment stays in place.
        if (fragments.size == account.getAvailableDatasets().size) {
            fragments.forEachIndexed { index, overviewFragment ->
                overviewFragment?.updateData(account.getAvailableDatasets()[index].getData(context))
            }
        } else {
            // if not, we have to create a new array of fragments where we store
            // the fragments
            val fragmentsCopy = arrayOfNulls<OverviewFragment>(account.getAvailableDatasets().size)
            fragments.forEachIndexed { index, overviewFragment ->
                if (index < account.getAvailableDatasets().size)
                    fragmentsCopy[index] = overviewFragment
            }
            fragments = fragmentsCopy
        }

        notifyDataSetChanged()
    }

    fun getDataset(position : Int) : Dataset {
        if (position < 0 || position >= account.getAvailableDatasets().size) {
            throw RuntimeException("no groupcollection at index ${position} available.")
        }
        return account.getAvailableDatasets()[position]
    }

    fun hideDownloading() {
        // Hide Spinning Button
        fragments.forEach { it?.resetSwipeRefreshLayout() }
    }

    fun showDownloading() {
        // Enable Spinning Button
        fragments.forEach { it?.showSwipeRefreshLayout() }
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