package de.nils_beyer.android.Vertretungen;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
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
import de.nils_beyer.android.Vertretungen.data.DataModel;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.Entry;
import de.nils_beyer.android.Vertretungen.widget.VertretungenWidgetProvider;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 */
public class DownloadIntentService extends IntentService {
    private static final String TAG = DownloadIntentService.class.getSimpleName();


    public static final String PENDING_RESULT_EXTRA = "pending_result_intent";
    public static final String KLASSE_TODAY_KEY = "KLASSE_TODAY_KEY";
    public static final String KLASSE_TOMORROW_KEY = "KLASSE_TOMORROW_KEY";


    public static final String URL_TODAY = "https://www.burgaugymnasium.de/vertretungsplan/sus/heute/subst_001.htm";
    public static final String URL_TOMORROW = "https://www.burgaugymnasium.de/vertretungsplan/sus/morgen/subst_001.htm";

    public static final int RESULT_CODE = 0;
    public static final int ERROR_CODE = 2;

    public DownloadIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);
        try {
            try {

                Intent result = new Intent();

                String HTML_today = downloadHTMLFile(URL_TODAY);
                ArrayList<Group> dataSetToday = parseHTMLFile(HTML_today);


                String HTML_tomorrow = downloadHTMLFile(URL_TOMORROW);
                ArrayList<Group> dataSetTomorrow = parseHTMLFile(HTML_tomorrow);

                result.putParcelableArrayListExtra(KLASSE_TODAY_KEY, dataSetToday);
                result.putParcelableArrayListExtra(KLASSE_TOMORROW_KEY, dataSetTomorrow);

                Date dateToday = readDate(HTML_today);
                Date dateTomorrow = readDate(HTML_tomorrow);
                Date immediacyToday = readImmediacy(HTML_today);
                Date immediacyTomorrow = readImmediacy(HTML_tomorrow);

                DataModel.save(getApplicationContext(), dataSetToday, dataSetTomorrow, dateToday, dateTomorrow, immediacyToday, immediacyTomorrow);
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


    protected String downloadHTMLFile(String url) throws  Exception{
        HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(url).openConnection();
        urlConnection.setRequestProperty("Authorization", StudentAccount.generateHTTPHeaderAuthorization(getApplicationContext()));

        try {
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

    protected ArrayList<Group> parseHTMLFile(String html) {
        try {
            Map<String, Group> klasseMap = new HashMap<String, Group>();

            Document document = Jsoup.parse(html, "UTF-8");
            document.outputSettings().charset("UTF-8");
            Elements rows = document.getElementsByTag("tr");
            for (Element row : rows) {
                if (row.children().size() != 8) {
                    continue;
                }

                if (!(row.attr("class").contains("even") || row.attr("class").contains("odd"))) {
                    continue;
                }

                String name = row.child(1).text();
                String time = row.child(0).text();
                String original = "";
                String modified = row.child(4).text();
                String room = row.child(5).text();
                String type = "";
                String info = row.child(6).text();

                ArrayList<String> klassenNames = parseKlassenName(name);
                for (String klasse : klassenNames) {
                    if (klasse == null) {
                        continue;
                    }

                    Group k = klasseMap.get(klasse);
                    if (k == null) {
                        k = new Group();
                        k.name = klasse;
                        klasseMap.put(klasse, k);
                    }

                    Entry.Builder builder = new Entry.Builder()
                        .setType(type)
                        .setTime(time)
                        .setOriginalSubject(original)
                        .setModifiedSubject(modified)
                        .setRoom(room)
                        .setInformation(info);

                    if (!klasse.equals(name)) {
                        builder.setReference(name);
                    } else {
                        builder.setReference(null);
                    }

                    k.add(builder.build());
                }
            }
            ArrayList<Group> klassenArrayList = new ArrayList<Group>(klasseMap.values());

            DataModel.sort(getApplication(), klassenArrayList);

            return klassenArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<Group>();
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

    protected ArrayList<String> parseKlassenName(String _input) {
        ArrayList<String> classes = new ArrayList<String>();

        classes.add(_input);

        if (_input.contains("EP")) {
            if (!classes.contains("EP"))
                classes.add("EP");
        }

        if (_input.contains("Q1")) {
            if (!classes.contains("Q1"))
                classes.add("Q1");
        }

        if (_input.contains("Q2")) {
            if (!classes.contains("Q2"))
                classes.add("Q2");
        }

        if (_input.contains("Q12")) {
            if (!classes.contains("Q2"))
                classes.add("Q2");
            if (!classes.contains("Q1"))
                classes.add("Q1");
        }

        Pattern pattern = Pattern.compile("\\d[a-z]+");
        Matcher matcher = pattern.matcher(_input);
        while (matcher.find())
        {
            String otherClass = matcher.group(0);
            int length = otherClass.length();
            for (int i = 1; i < length; i++) {
                String composedName = String.valueOf(otherClass.charAt(0)) + String.valueOf(otherClass.charAt(i));
                if (!classes.contains(composedName))
                    classes.add(composedName);
            }
        }

        return classes;
    }
}
