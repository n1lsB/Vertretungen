package de.nils_beyer.android.Vertretungen.events

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import de.nils_beyer.android.Vertretungen.download.downloadHTMLFileViaHTTP
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(Date())
private val eventsURL = "http://api.burgaugymnasium.de/calendar/index.php?format=html&startDate=$currentDate"

class EventsDownloadService : IntentService(EventsDownloadService::class.simpleName) {

    override fun onHandleIntent(intent: Intent?) {
        val htmlContent = downloadHTMLFileViaHTTP(eventsURL)
        val events = parseHTML(htmlContent)
        EventStorage.save(applicationContext, events)
    }

    private fun parseHTML(htmlContent : String) : List<Event> {
        val document = Jsoup.parse(htmlContent)
        val eventsInTable = document.getElementsByTag("tr")

        return eventsInTable.map {
            val dateDescription = it.child(0).text()
            val dates = parseDateDescription(dateDescription)

            var timeDescription = it.child(1).text()
            if (timeDescription.trim().isEmpty()) {
                timeDescription = null
            }
            val title = it.child(2).text()


            Event(dates.first, dates.second, timeDescription, title)
        }
    }

    private fun parseDateDescription(dateDescription : String) : Pair<Date, Date?> {
        val startDate : Date
        val endDate : Date?

        if (dateDescription.contains("-")) {
            val start = dateDescription.split("-")[0]
            val end = dateDescription.split("-")[1]

            val endCalendar = Calendar.getInstance()
            endCalendar.time = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).parse(end)

            val startCalendar = Calendar.getInstance()
            startCalendar.time = SimpleDateFormat("dd.MM", Locale.GERMANY).parse(start)

            if (startCalendar.get(Calendar.MONTH) > endCalendar.get(Calendar.MONTH)) {
                startCalendar.set(Calendar.YEAR, endCalendar.get(Calendar.YEAR) - 1)
            } else {
                startCalendar.set(Calendar.YEAR, endCalendar.get(Calendar.YEAR))
            }

            startDate = startCalendar.time
            endDate = endCalendar.time

            return Pair(startDate, endDate)
        } else {
            startDate = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).parse(dateDescription)
            return Pair(startDate, null)
        }
    }

    companion object {
        @JvmStatic
        fun startDownload(context : Context) {
            context.startService(Intent(context, EventsDownloadService::class.java))
        }
    }
}