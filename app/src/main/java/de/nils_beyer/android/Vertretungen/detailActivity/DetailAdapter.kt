package de.nils_beyer.android.Vertretungen.detailActivity

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.Group
import de.nils_beyer.android.Vertretungen.data.TeacherEntry

class DetailAdapter(private val c: Context, private val group: Group) : RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(group.replacements[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (group.replacements[position]) {
            is TeacherEntry -> TEACHER_VIEW_TYPE
            else -> STUDENT_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return group.replacements.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TEACHER_VIEW_TYPE -> {
                val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recyclerview_detail_layout_teacher, parent, false)
                TeacherViewHolder(v, c, group)
            }
            else -> {
                val v3 = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recyclerview_detail_layout, parent, false)
                ViewHolder(v3, c, group)
            }
        }
    }

    companion object {
        private const val TEACHER_VIEW_TYPE = 1
        private const val STUDENT_VIEW_TYPE = 0
    }
}