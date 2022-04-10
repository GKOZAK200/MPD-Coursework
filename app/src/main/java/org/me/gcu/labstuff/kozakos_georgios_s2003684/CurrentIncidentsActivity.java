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
import java.util.ArrayList;

public class CurrentIncidentsActivity extends AppCompatActivity {
    public static ArrayList<CurrentIncident> CurrentIncidentsArrayList = null;
    private final Handler mHandler = new Handler();
    private final Runnable updateList = new Runnable() {
        @Override
        public void run() {
            if (internetAvailable()) {
                CurrentIncidentsArrayList.removeAll(CurrentIncidentsArrayList);
                Toast.makeText(CurrentIncidentsActivity.this, "Updating list", Toast.LENGTH_SHORT).show();
                BackgroundProcessThread thread = new BackgroundProcessThread();
                thread.start();
                mHandler.postDelayed(this, 180000);
            } else {
                Toast.makeText(CurrentIncidentsActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    };
    BottomNavigationView bottomNavigationView;
    private ListView currentIncidentsList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CurrentIncidentsArrayList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_incidents);
        Log.e("MyTag", "in onCreate");
        // Set up the raw links to the graphical components
        Log.e("MyTag", "after startButton");
        currentIncidentsList = (ListView) findViewById(R.id.currentIncidentsList);
        currentIncidentsList.setOnItemClickListener((adapterView, view, position, id) -> {

        });
        updateList.run();
        //new BackgroundProcess().execute();
        initSearchView();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.currentIncidents);

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
        currentIncidentsList.setOnItemClickListener((adapterView, view, position, l) -> {
            CurrentIncident selectedCurrentIncident = (CurrentIncident) (currentIncidentsList.getItemAtPosition(position));
            Intent showDetails = new Intent(getApplicationContext(), DetailCurrentIncidentActivity.class);
            showDetails.putExtra("link", selectedCurrentIncident.getLink());
            startActivity(showDetails);
            searchView.clearFocus();
        });
    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.currentIncidentSearchView);


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
            CurrentIncident currentIncident = null;

            try {
                // Planned Roadworks Link
                String urlSourceCurrentIncidents = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
                URL url = new URL(urlSourceCurrentIncidents);

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
                            currentIncident = new CurrentIncident();
                        } else if (xpp.getName().equalsIgnoreCase("title")) { // If the start tag is for a title
                            if (insideItem) { // If we are inside an item
                                currentIncident.setTitle(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                currentIncident.setDescription(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                currentIncident.setLink(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("georss:point")) {
                            if (insideItem) {
                                currentIncident.setCoords(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                currentIncident.setPubDate(xpp.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                            CurrentIncidentsArrayList.add(currentIncident);
                            assert currentIncident != null;
                            Log.e("MyTag", "Current Incident is " + currentIncident);
                        }
                    }
                    eventType = xpp.next(); // Go to next event
                }
            } catch (IOException | XmlPullParserException ignored) {
            }
            runOnUiThread(() -> {
                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                for (int i = 0; i < CurrentIncidentsArrayList.size(); i++) {
                    titles.add(CurrentIncidentsArrayList.get(i).getTitle());
                    descriptions.add(CurrentIncidentsArrayList.get(i).getDescription());
                }

                CurrentIncidentAdapter adapter = new CurrentIncidentAdapter(getApplicationContext(), 0, CurrentIncidentsArrayList);

                currentIncidentsList.setAdapter(adapter);

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
            ArrayList<CurrentIncident> filteredCurrentIncidents = new ArrayList<>();

            for (CurrentIncident currentIncident : CurrentIncidentsArrayList) {
                if (currentIncident.getTitle().toLowerCase().contains(s.toLowerCase())) {
                    filteredCurrentIncidents.add(currentIncident);
                }
            }
            runOnUiThread(() -> {
                CurrentIncidentAdapter adapter = new CurrentIncidentAdapter(getApplicationContext(), 0, filteredCurrentIncidents);
                currentIncidentsList.setAdapter(adapter);
            });
        }
    }
}

