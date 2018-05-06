package de.nils_beyer.android.Vertretungen.events

import java.util.*

data class Event(val start : Date, val end : Date?, val timeInformation : String?, val title : String) {
    fun intersects(date : Date) : Boolean {
        if (start == date) {
            return true
        } else if (end != null) {
            if (end == start) {
                return true
            } else if (date.after(start) && date.before(end)) {
                return true
            }
        }
        return false
    }
}