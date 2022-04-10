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

public class MainActivity extends AppCompatActivity {
    public static ArrayList<PlannedRoadwork> PlannedRoadworksArrayList = null;
    private final Handler mHandler = new Handler();
    private final Runnable updateList = new Runnable() {
        @Override
        public void run() {
            if (internetAvailable()) {
                PlannedRoadworksArrayList.removeAll(PlannedRoadworksArrayList);
                Toast.makeText(MainActivity.this, "Updating list", Toast.LENGTH_SHORT).show();
                BackgroundProcessThread thread = new BackgroundProcessThread();
                thread.start();
                mHandler.postDelayed(this, 180000);
            } else {
                Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    };
    BottomNavigationView bottomNavigationView;
    private ListView plannedRoadworksList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PlannedRoadworksArrayList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MyTag", "in onCreate");
        // Set up the raw links to the graphical components
        Log.e("MyTag", "after startButton");
        plannedRoadworksList = (ListView) findViewById(R.id.plannedRoadworksList);
        plannedRoadworksList.setOnItemClickListener((adapterView, view, position, id) -> {

        });
        updateList.run();
        //new BackgroundProcess().execute();
        initSearchView();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.plannedRoadworks);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.roadworks:
                    mHandler.removeCallbacks(updateList);
                    startActivity(new Intent(getApplicationContext(), RoadworksActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.plannedRoadworks:
                    mHandler.removeCallbacks(updateList);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.currentIncidents:
                    mHandler.removeCallbacks(updateList);
                    startActivity(new Intent(getApplicationContext(), CurrentIncidentsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.journeyPlanner:
                    mHandler.removeCallbacks(updateList);
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
        plannedRoadworksList.setOnItemClickListener((adapterView, view, position, l) -> {
            PlannedRoadwork selectedPlannedRoadwork = (PlannedRoadwork) (plannedRoadworksList.getItemAtPosition(position));
            Intent showDetails = new Intent(getApplicationContext(), DetailActivity.class);
            showDetails.putExtra("link", selectedPlannedRoadwork.getLink());
            startActivity(showDetails);
            searchView.clearFocus();
        });
    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.plannedRoadworkSearchView);


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
            PlannedRoadwork plannedRoadwork = null;

            try {
                // Planned Roadworks Link
                String urlSourcePlannedRoadworks = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
                URL url = new URL(urlSourcePlannedRoadworks);

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
                            plannedRoadwork = new PlannedRoadwork();
                        } else if (xpp.getName().equalsIgnoreCase("title")) { // If the start tag is for a title
                            if (insideItem) { // If we are inside an item
                                plannedRoadwork.setTitle(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                plannedRoadwork.setDescription(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                plannedRoadwork.setLink(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("georss:point")) {
                            if (insideItem) {
                                plannedRoadwork.setCoords(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                plannedRoadwork.setPubDate(xpp.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                            PlannedRoadworksArrayList.add(plannedRoadwork);
                            assert plannedRoadwork != null;
                            Log.e("MyTag", "Planned Roadwork is " + plannedRoadwork);
                        }
                    }
                    eventType = xpp.next(); // Go to next event
                }
            } catch (IOException | XmlPullParserException ignored) {
            }
            runOnUiThread(() -> {
                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                for (int i = 0; i < PlannedRoadworksArrayList.size(); i++) {
                    titles.add(PlannedRoadworksArrayList.get(i).getTitle());
                    descriptions.add(PlannedRoadworksArrayList.get(i).getDescription());
                    PlannedRoadworksArrayList.get(i).getStartDate();
                    PlannedRoadworksArrayList.get(i).getStartTime();
                    PlannedRoadworksArrayList.get(i).getEndDate();
                    PlannedRoadworksArrayList.get(i).getEndTime();
                    PlannedRoadworksArrayList.get(i).getDays();
                    PlannedRoadworksArrayList.get(i).getDates();


                }

                PlannedRoadworkAdapter adapter = new PlannedRoadworkAdapter(getApplicationContext(), 0, PlannedRoadworksArrayList);

                plannedRoadworksList.setAdapter(adapter);

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
            ArrayList<PlannedRoadwork> filteredPlannedRoadworks = new ArrayList<>();

            for (PlannedRoadwork plannedRoadwork : PlannedRoadworksArrayList) {
                if (plannedRoadwork.getTitle().toLowerCase().contains(s.toLowerCase())) {
                    filteredPlannedRoadworks.add(plannedRoadwork);
                }

                List<Date> dates = plannedRoadwork.getDates();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                for (int i = 0; i < dates.size(); i++) {
                    Date lDate = (Date) dates.get(i);
                    String ds = sdf.format(lDate);
                    if (ds.toLowerCase().startsWith(s.toLowerCase())) {
                        if (!filteredPlannedRoadworks.contains(plannedRoadwork)) {
                            filteredPlannedRoadworks.add(plannedRoadwork);
                        }
                    }
                }

            }
            runOnUiThread(() -> {
                PlannedRoadworkAdapter adapter = new PlannedRoadworkAdapter(getApplicationContext(), 0, filteredPlannedRoadworks);
                plannedRoadworksList.setAdapter(adapter);
            });

        }
    }
}
