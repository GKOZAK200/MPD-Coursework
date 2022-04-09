// Name: Georgios Kozakos   Matric Number: S2003684

package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JourneyPlannerActivity extends AppCompatActivity implements OnClickListener {
    private TextView rawDataDisplay;
    private ListView roadworksList;
    private Button startButton;
    private String result = "";
    private String url1 = "";
    private String urlSourceRoadworks = "https://trafficscotland.org/rss/feeds/roadworks.aspx"; // Roadworks Link
    private String urlSourcePlannedRoadworks = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx"; // Planned Roadworks Link
    public static ArrayList<Roadwork> RoadworksArrayList = null;
    private String departureSearchText = "";
    private String destinationSearchText = "";
    private String dateSearchText = "";
    private SearchView departureSearchView;
    private SearchView destinationSearchView;
    private SearchView dateSearchView;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoadworksArrayList = new ArrayList<Roadwork>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_planner);
        Log.e("MyTag", "in onCreate");
        // Set up the raw links to the graphical components
        //rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        //startButton = (Button)findViewById(R.id.startButton);
        //startButton.setOnClickListener(this);
        Log.e("MyTag", "after startButton");
        roadworksList = (ListView) findViewById(R.id.journeyPlannerList);
        roadworksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
        new BackgroundProcess().execute();
        initSearchView();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.journeyPlanner);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
            }
        });
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream(); // Opens connection to URL
        } catch (IOException e) {

    }return null;
}

    public class BackgroundProcess extends AsyncTask<Integer, Integer, Exception> {
        ProgressDialog progressDialog = new ProgressDialog(JourneyPlannerActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Fetching Roadworks from Traffic Scotland"); // Set message
            progressDialog.show(); // Shows the message while fetching data
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            Roadwork roadwork = null;

            try {
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
                            Log.e("MyTag", "Roadwork is " + roadwork.toString());
                        }
                    }
                    eventType = xpp.next(); // Go to next event
                }

                url = new URL(urlSourcePlannedRoadworks);

                factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false); // No support for XML namespaces

                xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                insideItem = false; // To detect whether in an item or not

                eventType = xpp.getEventType();

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
                            Log.e("MyTag", "Roadwork is " + roadwork.toString());
                        }
                    }
                    eventType = xpp.next(); // Go to next event
                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayList<String> titles = new ArrayList<String>();
            ArrayList<String> descriptions = new ArrayList<String>();
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

            progressDialog.dismiss(); // Gets rid of fetching data message
            setUpOnclickListener();
        }
    }

    private void setUpOnclickListener() {
        roadworksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Roadwork selectedRoadwork = (Roadwork) (roadworksList.getItemAtPosition(position));
                Intent showDetails = new Intent(getApplicationContext(), DetailJourneyPlannerActivity.class);
                showDetails.putExtra("link", selectedRoadwork.getLink());
                startActivity(showDetails);
                departureSearchView.clearFocus();
                destinationSearchView.clearFocus();
                dateSearchView.clearFocus();
            }
        });
    }

    public void startProgress() {
        // Run network access on a separate thread;
        new Thread(new Task(urlSourceRoadworks)).start();

    } //

    @Override
    public void onClick(View v) {
        Log.e("MyTag", "in onClick");
        startProgress();
        Log.e("MyTag", "after startProgress");
    }


    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag", "after ready");
                //
                // Now read the data. Make sure that there are no specific hedrs
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                //
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            JourneyPlannerActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    rawDataDisplay.setText(result);
                }
            });
        }


    }

    private void initSearchView() {
        departureSearchView = (SearchView) findViewById(R.id.departureCitySearchView);
        destinationSearchView = (SearchView)  findViewById(R.id.destinationSearchView);
        dateSearchView = (SearchView)  findViewById(R.id.dateSearchView);


        departureSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                departureSearchText = s;
                ArrayList<Roadwork> filteredRoadworks = new ArrayList<Roadwork>();

                for(Roadwork roadwork: RoadworksArrayList) {
                    if (roadwork.getTitle().toLowerCase().contains(destinationSearchText.toLowerCase()) && roadwork.getTitle().toLowerCase().contains(departureSearchText.toLowerCase())) {
                        List<Date> dates = roadwork.getDates();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                        for (int i = 0; i < dates.size(); i++) {
                            Date lDate = (Date) dates.get(i);
                            String ds = sdf.format(lDate);
                            if (ds.toLowerCase().contains(dateSearchText.toLowerCase())) {
                                if (!filteredRoadworks.contains(roadwork)) {
                                    filteredRoadworks.add(roadwork);
                                }
                            }
                        }
                    }
                }
                RoadworkAdapter adapter = new RoadworkAdapter(getApplicationContext(), 0, filteredRoadworks);
                roadworksList.setAdapter(adapter);

                return false;
            }
        });
        destinationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                destinationSearchText = s;
                ArrayList<Roadwork> filteredRoadworks = new ArrayList<Roadwork>();

                for(Roadwork roadwork: RoadworksArrayList) {
                    if (roadwork.getTitle().toLowerCase().contains(destinationSearchText.toLowerCase()) && roadwork.getTitle().toLowerCase().contains(departureSearchText.toLowerCase())) {
                        List<Date> dates = roadwork.getDates();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                        for (int i = 0; i < dates.size(); i++) {
                            Date lDate = (Date) dates.get(i);
                            String ds = sdf.format(lDate);
                            if (ds.toLowerCase().contains(dateSearchText.toLowerCase())) {
                                if (!filteredRoadworks.contains(roadwork)) {
                                    filteredRoadworks.add(roadwork);
                                }
                            }
                        }
                    }
                }
                RoadworkAdapter adapter = new RoadworkAdapter(getApplicationContext(), 0, filteredRoadworks);
                roadworksList.setAdapter(adapter);

                return false;
            }
        });
        dateSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                dateSearchText = s;
                ArrayList<Roadwork> filteredRoadworks = new ArrayList<Roadwork>();

                for(Roadwork roadwork: RoadworksArrayList) {
                    if (roadwork.getTitle().toLowerCase().contains(destinationSearchText.toLowerCase()) && roadwork.getTitle().toLowerCase().contains(departureSearchText.toLowerCase())) {
                        List<Date> dates = roadwork.getDates();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                        for (int i = 0; i < dates.size(); i++) {
                            Date lDate = (Date) dates.get(i);
                            String ds = sdf.format(lDate);
                            if (ds.toLowerCase().contains(dateSearchText.toLowerCase())) {
                                if (!filteredRoadworks.contains(roadwork)) {
                                    filteredRoadworks.add(roadwork);
                                }
                            }
                        }
                    }
                }
                RoadworkAdapter adapter = new RoadworkAdapter(getApplicationContext(), 0, filteredRoadworks);
                roadworksList.setAdapter(adapter);

                return false;
            }
        });
    }
}
