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

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private ListView plannedRoadworksList;
    private Button startButton;
    private String result = "";
    private String url1="";
    private String urlSourcePlannedRoadworks="https://trafficscotland.org/rss/feeds/plannedroadworks.aspx"; // Planned Roadworks Link
    public static ArrayList<PlannedRoadwork> PlannedRoadworksArrayList = null;
    private String currentSearchText = "";
    private SearchView searchView;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        PlannedRoadworksArrayList = new ArrayList<PlannedRoadwork>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MyTag","in onCreate");
        // Set up the raw links to the graphical components
        //rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        //startButton = (Button)findViewById(R.id.startButton);
        //startButton.setOnClickListener(this);
        Log.e("MyTag","after startButton");
        plannedRoadworksList = (ListView) findViewById(R.id.plannedRoadworksList);
        plannedRoadworksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
        new BackgroundProcess().execute();
        initSearchView();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.plannedRoadworks);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
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

    public InputStream getInputStream(URL url)
    {
        try
            {
                return url.openConnection().getInputStream(); // Opens connection to URL
            }
            catch (IOException e){
                return null;
            }
        }

        public class BackgroundProcess extends AsyncTask<Integer, Integer, Exception> {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

            Exception exception = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.setMessage("Fetching Planned Roadworks from Traffic Scotland"); // Set message
                progressDialog.show(); // Shows the message while fetching data
            }

            @Override
            protected Exception doInBackground(Integer... integers) {
                PlannedRoadwork plannedRoadwork = null;

                try {
                    URL url = new URL(urlSourcePlannedRoadworks);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    factory.setNamespaceAware(false); // No support for XML namespaces

                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(getInputStream(url), "UTF_8");

                    boolean insideItem = false; // To detect whether in an item or not

                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT){ // Loop until end of document
                        if (eventType == XmlPullParser.START_TAG){ // If a start tag is detected
                            if (xpp.getName().equalsIgnoreCase("item")){ // If the start tag is for an item
                                insideItem = true; // Inside item
                                plannedRoadwork = new PlannedRoadwork();
                            }
                            else if (xpp.getName().equalsIgnoreCase("title")) { // If the start tag is for a title
                                if (insideItem) { // If we are inside an item
                                    plannedRoadwork.setTitle(xpp.nextText());
                                }
                            }
                                else if (xpp.getName().equalsIgnoreCase("description")) {
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
                            }

                        else if (eventType == XmlPullParser.END_TAG) {
                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = false;
                                PlannedRoadworksArrayList.add(plannedRoadwork);
                                Log.e("MyTag", "Planned Roadwork is " + plannedRoadwork.toString());
                            }
                        }
                        eventType = xpp.next(); // Go to next event
                    }
                }


                catch (MalformedURLException e){
                    exception = e;
                }
                catch (XmlPullParserException e){
                    exception = e;
                }
                catch (IOException e){
                    exception = e;
                }

                return exception;
            }

            @Override
            protected void onPostExecute(Exception s) {
                super.onPostExecute(s);

                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> descriptions = new ArrayList<String>();
                for (int i=0; i < PlannedRoadworksArrayList.size(); i++){
                    titles.add(PlannedRoadworksArrayList.get(i).getTitle());
                    descriptions.add(PlannedRoadworksArrayList.get(i).getDescription());
                    PlannedRoadworksArrayList.get(i).getStartDate();
                    PlannedRoadworksArrayList.get(i).getStartTime();
                    PlannedRoadworksArrayList.get(i).getEndDate();
                    PlannedRoadworksArrayList.get(i).getEndTime();
                    PlannedRoadworksArrayList.get(i).getDays();
                    PlannedRoadworksArrayList.get(i).getDates();


                }

                PlannedRoadworkAdapter adapter = new PlannedRoadworkAdapter(getApplicationContext(), 0 , PlannedRoadworksArrayList);

                plannedRoadworksList.setAdapter(adapter);

                progressDialog.dismiss(); // Gets rid of fetching data message
                setUpOnclickListener();
            }
        }

    private void setUpOnclickListener() {
        plannedRoadworksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PlannedRoadwork selectedPlannedRoadwork = (PlannedRoadwork) (plannedRoadworksList.getItemAtPosition(position));
                Intent showDetails = new Intent(getApplicationContext(), DetailActivity.class);
                showDetails.putExtra("link", selectedPlannedRoadwork.getLink());
                startActivity(showDetails);
                searchView.clearFocus();
            }
        });
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSourcePlannedRoadworks)).start();

    } //

    @Override
    public void onClick(View v)
    {
        Log.e("MyTag","in onClick");
        startProgress();
        Log.e("MyTag","after startProgress");
    }


    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag","after ready");
                //
                // Now read the data. Make sure that there are no specific hedrs
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                //
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception in run");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    rawDataDisplay.setText(result);
                }
            });
        }


        }

    private void initSearchView()
    {
        searchView = (SearchView) findViewById(R.id.plannedRoadworkSearchView);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                currentSearchText = s;
                ArrayList<PlannedRoadwork> filteredPlannedRoadworks = new ArrayList<PlannedRoadwork>();

                for(PlannedRoadwork plannedRoadwork: PlannedRoadworksArrayList)
                {
                    if(plannedRoadwork.getTitle().toLowerCase().contains(s.toLowerCase()))
                    {
                            filteredPlannedRoadworks.add(plannedRoadwork);
                    }

                    List<Date> dates = plannedRoadwork.getDates();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                   for(int i=0;i<dates.size();i++ ) {
                        Date lDate = (Date) dates.get(i);
                        String ds = sdf.format(lDate);
                        if (ds.toLowerCase().startsWith(s.toLowerCase())) {
                            if (!filteredPlannedRoadworks.contains(plannedRoadwork)) {
                                filteredPlannedRoadworks.add(plannedRoadwork);
                            }
                        }
                    }

                }
                    PlannedRoadworkAdapter adapter = new PlannedRoadworkAdapter(getApplicationContext(), 0, filteredPlannedRoadworks);
                    plannedRoadworksList.setAdapter(adapter);
                return false;

            }
        });
    }
}
