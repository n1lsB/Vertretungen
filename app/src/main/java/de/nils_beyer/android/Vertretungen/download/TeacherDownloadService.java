package de.nils_beyer.android.Vertretungen.download;

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
import de.nils_beyer.android.Vertretungen.data.Entry;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.data.TeacherEntry;
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
    private static final String TAG = TeacherDownloadService.class.getSimpleName();


    public static final String PENDING_RESULT_EXTRA = "pending_result_intent";
    public static final String KLASSE_TODAY_KEY = "KLASSE_TODAY_KEY";
    public static final String KLASSE_TOMORROW_KEY = "KLASSE_TOMORROW_KEY";


    public static final String URL_TODAY = "https://www.burgaugymnasium.de/vertretungsplan/sus/heute/subst_001.htm";
    public static final String URL_TOMORROW = "https://www.burgaugymnasium.de/vertretungsplan/sus/morgen/subst_001.htm";

    public static final int RESULT_CODE = 0;
    public static final int ERROR_CODE = 2;

    public TeacherDownloadService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);

        Entry entry = new TeacherEntry.Builder()
                .setTeacherOld("HEI")
                .setTeacherNew("PUC")
                .setOriginalSubject("If")
                .setModifiedSubject("If")
                .setRoom("If128")
                .setTime("1-2")
                .setType("Vertretung")
                .build();

        Group g = new Group();
        g.name = "PUC";
        g.add(entry);

        ArrayList<Group> groupArrayList = new ArrayList<>();
        groupArrayList.add(g);

        Date today = new Date();
        GroupCollection groupCollection = new GroupCollection(today, today, groupArrayList);
        GroupCollection groupCollection2 = new GroupCollection(today, today, groupArrayList);

        TeacherStorage.save(getApplicationContext(), groupCollection, groupCollection2);

        try {
            reply.send(this, RESULT_CODE, new Intent());
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

}
