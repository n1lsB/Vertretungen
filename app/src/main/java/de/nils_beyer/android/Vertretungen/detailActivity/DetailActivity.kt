package de.nils_beyer.android.Vertretungen.detailActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout


import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.util.DateParser
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.Group

class DetailActivity : AppCompatActivity() {

    private var group: Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = intent.extras
        val gc = b!!.getSerializable(ARG_GROUPCOLLECTION) as GroupCollection
        val position = b.getInt(ARG_POSITION_INDEX)

        val date = gc.date
        group = gc.groupArrayList[position]


        setContentView(R.layout.activity_detail)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = group!!.name
        toolbar.subtitle = DateParser.parseDateToShortString(this, date)


        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_48dp)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar!!.setHomeButtonEnabled(true)


        val fragmentContainer = findViewById<View>(R.id.detail_fragment_container) as FrameLayout

        if (savedInstanceState == null) {
            val detailFragment = DetailFragment.newInstance(group)
            supportFragmentManager
                    .beginTransaction()
                    .add(fragmentContainer.id, detailFragment)
                    .commit()

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_detail, menu)

        if (group!!.isMarked(applicationContext)) {
            menu.findItem(R.id.menu_detail_star).setIcon(R.drawable.ic_star_white_24dp)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_detail_star -> {
                val marked = group!!.isMarked(applicationContext)
                group!!.setMarked(application, !marked)
                if (!marked) {
                    item.setIcon(R.drawable.ic_star_white_24dp)
                } else {
                    item.setIcon(R.drawable.ic_star_border_white_24dp)
                }
                return true
            }
        }
        return false
    }

    companion object {


        val ARG_GROUPCOLLECTION = "ARG_GROUPCOLLECTION"
        val ARG_POSITION_INDEX = "ARG_POSITION_INDEX"

        fun startActivity(c: Context, gc: GroupCollection, pos: Int) {
            val startActivity = getStartIntent(c, gc, pos)
            c.startActivity(startActivity)
        }

        fun getStartIntent(c: Context, gc: GroupCollection, pos: Int): Intent {
            val startActivity = Intent(c, DetailActivity::class.java)
            val b = Bundle()
            b.putSerializable(ARG_GROUPCOLLECTION, gc)
            b.putInt(ARG_POSITION_INDEX, pos)
            startActivity.putExtras(b)
            return startActivity
        }
    }


}
