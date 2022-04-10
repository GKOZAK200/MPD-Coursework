// Name: Georgios Kozakos   Matric Number: S2003684

package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoadworksActivity extends AppCompatActivity {
    public static ArrayList<Roadwork> RoadworksArrayList = null;
    private final Handler mHandler = new Handler();
    private final Runnable updateList = new Runnable() {
        @Override
        public void run() {
            if (internetAvailable()) {
                RoadworksArrayList.removeAll(RoadworksArrayList);
                Toast.makeText(RoadworksActivity.this, "Updating list", Toast.LENGTH_SHORT).show();
                RoadworksActivity.BackgroundProcessThread thread = new RoadworksActivity.BackgroundProcessThread();
                thread.start();
                mHandler.postDelayed(this, 180000);
            } else {
                Toast.makeText(RoadworksActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    };
    BottomNavigationView bottomNavigationView;
    private ListView roadworksList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoadworksArrayList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadworks);
        Log.e("MyTag", "in onCreate");
        // Set up the raw links to the graphical components
        Log.e("MyTag", "after startButton");
        roadworksList = (ListView) findViewById(R.id.roadworksList);
        roadworksList.setOnItemClickListener((adapterView, view, position, id) -> {

        });
        updateList.run();
        //new BackgroundProcess().execute();
        initSearchView();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.roadworks);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.roadworks:
                    startActivity(new Intent(getApplicationContext(), RoadworksActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.plannedRoadworks:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.currentIncidents:
                    startActivity(new Intent(getApplicationContext(), CurrentIncidentsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.journeyPlanner:
                    startActivity(new Intent(getApplicationContext(), JourneyPlannerActivity.class));
                    overridePendingTransition(0, 0);
                    return true;

            }
            return false;
        });
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream(); // Opens connection to URL
        } catch (IOException e) {
            return null;
        }
    }

    private void setUpOnclickListener() {
        roadworksList.setOnItemClickListener((adapterView, view, position, l) -> {
            Roadwork selectedRoadwork = (Roadwork) (roadworksList.getItemAtPosition(position));
            Intent showDetails = new Intent(getApplicationContext(), DetailRoadworkActivity.class);
            showDetails.putExtra("link", selectedRoadwork.getLink());
            startActivity(showDetails);
            searchView.clearFocus();
        });
    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.roadworkSearchView);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mHandler.removeCallbacks(updateList);
                searchThread thread = new searchThread(s);
                thread.start();
                return false;
            }
        });
    }

    public boolean internetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    class BackgroundProcessThread extends Thread {

        @Override
        public void run() {
            Roadwork roadwork = null;

            try {
                // Roadworks Link
                String urlSourceRoadworks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
                URL url = new URL(urlSourceRoadworks);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false); // No support for XML namespaces

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false; // To detect whether in an item or not

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) { // Loop until end of document
                    if (eventType == XmlPullParser.START_TAG) { // If a start tag is detected
                        if (xpp.getName().equalsIgnoreCase("item")) { // If the start tag is for an item
                            insideItem = true; // Inside item
                            roadwork = new Roadwork();
                        } else if (xpp.getName().equalsIgnoreCase("title")) { // If the start tag is for a title
                            if (insideItem) { // If we are inside an item
                                roadwork.setTitle(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                roadwork.setDescription(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                roadwork.setLink(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("georss:point")) {
                            if (insideItem) {
                                roadwork.setCoords(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                roadwork.setPubDate(xpp.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                            RoadworksArrayList.add(roadwork);
                            assert roadwork != null;
                            Log.e("MyTag", "Roadwork is " + roadwork);
                        }
                    }
                    eventType = xpp.next(); // Go to next event
                }
            } catch (IOException | XmlPullParserException ignored) {
            }
            runOnUiThread(() -> {
                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                for (int i = 0; i < RoadworksArrayList.size(); i++) {
                    titles.add(RoadworksArrayList.get(i).getTitle());
                    descriptions.add(RoadworksArrayList.get(i).getDescription());
                    RoadworksArrayList.get(i).getStartDate();
                    RoadworksArrayList.get(i).getStartTime();
                    RoadworksArrayList.get(i).getEndDate();
                    RoadworksArrayList.get(i).getEndTime();
                    RoadworksArrayList.get(i).getDays();
                    RoadworksArrayList.get(i).getDates();


                }

                RoadworkAdapter adapter = new RoadworkAdapter(getApplicationContext(), 0, RoadworksArrayList);

                roadworksList.setAdapter(adapter);

                setUpOnclickListener();
            });
        }
    }

    class searchThread extends Thread {
        final String s;

        searchThread(String s) {
            this.s = s;
        }

        @Override
        public void run() {
            String currentSearchText = s;
            ArrayList<Roadwork> filteredRoadworks = new ArrayList<>();

            for (Roadwork roadwork : RoadworksArrayList) {
                if (roadwork.getTitle().toLowerCase().contains(s.toLowerCase())) {
                    filteredRoadworks.add(roadwork);
                }

                List<Date> dates = roadwork.getDates();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                for (int i = 0; i < dates.size(); i++) {
                    Date lDate = (Date) dates.get(i);
                    String ds = sdf.format(lDate);
                    if (ds.toLowerCase().startsWith(s.toLowerCase())) {
                        if (!filteredRoadworks.contains(roadwork)) {
                            filteredRoadworks.add(roadwork);
                        }
                    }
                }
            }
            runOnUiThread(() -> {
                RoadworkAdapter adapter = new RoadworkAdapter(getApplicationContext(), 0, filteredRoadworks);
                roadworksList.setAdapter(adapter);
            });
        }
    }
}
