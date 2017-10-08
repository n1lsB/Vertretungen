package de.nils_beyer.android.Vertretungen.download;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import de.nils_beyer.android.Vertretungen.account.StudentAccount;
import de.nils_beyer.android.Vertretungen.account.TeacherAccount;
import de.nils_beyer.android.Vertretungen.data.Entry;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.data.TeacherEntry;
import de.nils_beyer.android.Vertretungen.data.TeacherGroup;
import de.nils_beyer.android.Vertretungen.storage.StudentStorage;
import de.nils_beyer.android.Vertretungen.storage.TeacherStorage;
import de.nils_beyer.android.Vertretungen.widget.VertretungenWidgetProvider;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 */
public class TeacherDownloadService extends IntentService {
    private static final String TAG = StudentDownloadService.class.getSimpleName();


    public static final String PENDING_RESULT_EXTRA = "pending_result_intent";
    public static final String KLASSE_TODAY_KEY = "KLASSE_TODAY_KEY";
    public static final String KLASSE_TOMORROW_KEY = "KLASSE_TOMORROW_KEY";


    public static final String URL_TODAY = "https://burgaugymnasium.de/vertretungsplan/lul-dummy/heute/subst_001.htm";
    public static final String URL_TOMORROW = "https://burgaugymnasium.de/vertretungsplan/lul-dummy/morgen/subst_001.htm";

    public static final int RESULT_CODE = 0;
    public static final int ERROR_CODE = 2;

    public TeacherDownloadService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);
        try {
            try {

                Intent result = new Intent();

                String HTML_today = downloadHTMLFile(URL_TODAY);
                ArrayList<? extends Group> dataSetToday = parseHTMLFile(HTML_today);


                String HTML_tomorrow = downloadHTMLFile(URL_TOMORROW);
                ArrayList<? extends Group> dataSetTomorrow = parseHTMLFile(HTML_tomorrow);

                result.putParcelableArrayListExtra(KLASSE_TODAY_KEY, dataSetToday);
                result.putParcelableArrayListExtra(KLASSE_TOMORROW_KEY, dataSetTomorrow);

                Date dateToday = readDate(HTML_today);
                Date dateTomorrow = readDate(HTML_tomorrow);
                Date immediacyToday = readImmediacy(HTML_today);
                Date immediacyTomorrow = readImmediacy(HTML_tomorrow);

                GroupCollection today = new GroupCollection(dateToday, immediacyToday, dataSetToday);
                GroupCollection tomorrow = new GroupCollection(dateTomorrow, immediacyTomorrow, dataSetTomorrow);

                TeacherStorage.save(getApplicationContext(), today, tomorrow);
                VertretungenWidgetProvider.updateWidgetData(this);


                reply.send(this, RESULT_CODE, result);
            } catch (Exception exc) {
                // could do better by treating the different sax/xml exceptions individually
                reply.send(ERROR_CODE);
            }
        } catch (PendingIntent.CanceledException exc) {
            Log.i(TAG, "reply cancelled", exc);
        }
    }

    protected String downloadHTMLFile(String url) throws Exception {
        return downloadHTMLFile(getApplicationContext(), url, TeacherAccount.getUserName(getApplicationContext()), TeacherAccount.getPassword(getApplicationContext()));
    }

    public static String downloadHTMLFile(Context c, String url, String username, String password) throws  Exception{
        HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(url).openConnection();
        urlConnection.setRequestProperty("Authorization", TeacherAccount.generateHTTPHeaderAuthorization(username, password));

        try {
            final int responseCode = urlConnection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HttpConection Response Code not 200");
            }
            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "ISO-8859-1"));
            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    protected ArrayList<? extends Group> parseHTMLFile(String html) {
        try {
            Map<String, TeacherGroup> teacherMap = new HashMap<>();

            Document document = Jsoup.parse(html, "UTF-8");
            document.outputSettings().charset("UTF-8");
            Elements rows = document.getElementsByTag("tr");
            for (Element row : rows) {
                if (row.children().size() != 10) {
                    continue;
                }

                if (!(row.attr("class").contains("even") || row.attr("class").contains("odd"))) {
                    continue;
                }

                String vertreter = row.child(0).text();
                String time = row.child(1).text();
                String klasse = row.child(2).text();
                String fach = row.child(3).text();
                String oldLehrer = row.child(4).text();
                String oldFach = row.child(5).text();
                String raum = row.child(6).text();
                String oldRaum = row.child(7).text();
                String info = row.child(8).text();

                TeacherEntry.Builder builder = new TeacherEntry.Builder();
                builder.setTeacherOld(oldLehrer);
                builder.setTeacherNew(vertreter);
                builder.setType("");
                builder.setTime(time);
                builder.setKla(klasse);
                builder.setOriginalSubject(oldFach);
                builder.setModifiedSubject(fach);
                builder.setRoom(raum);
                builder.setOldRoom(oldRaum);
                builder.setInformation(info);

                Entry entry = builder.build();

                // Für Vertreter
                Group g = teacherMap.get(vertreter);
                if (vertreter.equals("+") || vertreter.equals("---")) {

                } else {
                    if (g == null) {
                        TeacherGroup tg = new TeacherGroup();
                        tg.name = vertreter;
                        teacherMap.put(vertreter, tg);
                        g = tg;
                    }
                    g.add(entry);
                }



                // Für Verteteten Lehrer
                g = teacherMap.get(oldLehrer);
                if (oldLehrer.equals("+") || oldLehrer.equals("---") || oldLehrer.equals(" ") || vertreter.equals(oldLehrer)) {

                } else {
                    if (g == null) {
                        TeacherGroup tg = new TeacherGroup();
                        tg.name = oldLehrer;
                        teacherMap.put(oldLehrer, tg);
                        g = tg;
                    }
                    g.add(entry);
                }

                ArrayList<String> info_names = findTeacherNames(info);
                for (String name : info_names) {
                    if (!name.equals(vertreter) && !name.equals(oldLehrer)) {
                        Group group = teacherMap.get(name);
                        if (group == null) {
                            TeacherGroup tg = new TeacherGroup();
                            tg.name = name;
                            teacherMap.put(name, tg);
                            group = tg;
                        }
                        group.add(entry);
                    }
                }
            }
            ArrayList<TeacherGroup> klassenArrayList = new ArrayList<TeacherGroup>(teacherMap.values());
            TeacherStorage.sort(getApplicationContext(), klassenArrayList);
            return klassenArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    protected Date readDate(String html) {
        Document document = Jsoup.parse(html);

        Elements searchResults = document.getElementsByClass("mon_title");
        for (Element result : searchResults) {
            if (result.tagName() == "div") {
                String resultText = result.text();
                resultText = resultText.split(" ")[0];

                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = dateFormat.parse(resultText);
                    Log.d(TAG, "readDate: Datum gelesen");
                    return date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    protected Date readImmediacy(String html) {
        Document document = Jsoup.parse(html);
        Elements search_result = document.getElementsContainingText("Stand");

        for (Element element : search_result) {
            if (element.tagName().equals("p")) {
                String text_element = element.text();
                text_element = text_element.substring(text_element.indexOf("Stand: ") + 7);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                try {
                    return simpleDateFormat.parse(text_element);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }
        return null;
    }

    public static ArrayList<String> findTeacherNames(String _input) {
        ArrayList<String> names = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<=\\s|^)[A-ZÄÖÜ]{2,4}(?=\\s|$)");
        Matcher matcher = pattern.matcher(_input);
        while (matcher.find()) {
            String otherClass = matcher.group(0);
            if (otherClass.equalsIgnoreCase("GK") || otherClass.equalsIgnoreCase("LK")) {
                continue;
            } else {
                names.add(otherClass);
            }
        }

        return names;
    }
}
