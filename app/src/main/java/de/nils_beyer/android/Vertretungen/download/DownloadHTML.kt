package de.nils_beyer.android.Vertretungen.download

import de.nils_beyer.android.Vertretungen.account.TeacherAccount
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Throws(SecurityException::class, IllegalStateException::class)
fun downloadHTMLFileWithCredientials(url : String, username : String, password : String) : String  {
    // Setup HTTP connection
    val urlConnection = URL(url).openConnection() as HttpsURLConnection
    urlConnection.setRequestProperty("Authorization", TeacherAccount.generateHTTPHeaderAuthorization(username, password))

    // Get the server-side response code of the http request
    val httpStatusCode = urlConnection.responseCode

    // 401 - Unauthorized e.g. wrong credentials
    if (httpStatusCode == 401) {
        throw SecurityException("Unauthorized HTTP request")
    }

    if (httpStatusCode != 200) {
        throw IllegalStateException("HTTP respoonse code not 200")
    }

    // Read the content of the HTML file
    val br = BufferedReader(InputStreamReader(urlConnection.inputStream, "ISO-8859-1"))
    val html = br.use(BufferedReader::readText)


    return html;
}

@Throws(IllegalStateException::class)
fun downloadHTMLFileViaHTTP(url : String) : String {
    // Setup HTTP connection
    val urlConnection = URL(url).openConnection() as HttpURLConnection

    // Get the server-side response code of the http request
    val httpStatusCode = urlConnection.responseCode

    if (httpStatusCode != 200) {
        throw IllegalStateException("HTTP respoonse code not 200, but not 401")
    }

    // Read the content of the HTML file
    val br = BufferedReader(InputStreamReader(urlConnection.inputStream, "ISO-8859-1"))
    val html = br.use(BufferedReader::readText)

    return html;
}