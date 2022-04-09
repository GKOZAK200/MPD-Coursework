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

public class CurrentIncidentsActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private ListView currentIncidentsList;
    private Button startButton;
    private String result = "";
    private String url1="";
    private String urlSourceCurrentIncidents="https://trafficscotland.org/rss/feeds/currentincidents.aspx"; // Planned Roadworks Link
    public static ArrayList<CurrentIncident> CurrentIncidentsArrayList = null;
    private String currentSearchText = "";
    private SearchView searchView;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        CurrentIncidentsArrayList = new ArrayList<CurrentIncident>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_incidents);
        Log.e("MyTag","in onCreate");
        // Set up the raw links to the graphical components
        //rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        //startButton = (Button)findViewById(R.id.startButton);
        //startButton.setOnClickListener(this);
        Log.e("MyTag","after startButton");
        currentIncidentsList = (ListView) findViewById(R.id.currentIncidentsList);
        currentIncidentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
        BackgroundProcessThread thread = new BackgroundProcessThread();
        thread.start();
        //new BackgroundProcess().execute();
        initSearchView();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.currentIncidents);

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

    class BackgroundProcessThread extends Thread {
        Exception exception = null;

        @Override
        public void run(){
            CurrentIncident currentIncident = null;

            try {
                URL url = new URL(urlSourceCurrentIncidents);

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
                            currentIncident = new CurrentIncident();
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")) { // If the start tag is for a title
                            if (insideItem) { // If we are inside an item
                                currentIncident.setTitle(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")) {
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
                    }

                    else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                            CurrentIncidentsArrayList.add(currentIncident);
                            Log.e("MyTag", "Current Incident is " + currentIncident.toString());
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> titles = new ArrayList<String>();
                    ArrayList<String> descriptions = new ArrayList<String>();
                    for (int i=0; i < CurrentIncidentsArrayList.size(); i++){
                        titles.add(CurrentIncidentsArrayList.get(i).getTitle());
                        descriptions.add(CurrentIncidentsArrayList.get(i).getDescription());
                    }

                    CurrentIncidentAdapter adapter = new CurrentIncidentAdapter(getApplicationContext(), 0 , CurrentIncidentsArrayList);

                    currentIncidentsList.setAdapter(adapter);

                    setUpOnclickListener();
                }
            });
        }
    }

    public class BackgroundProcess extends AsyncTask<Integer, Integer, Exception> {
        ProgressDialog progressDialog = new ProgressDialog(CurrentIncidentsActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Fetching Current Incidents from Traffic Scotland"); // Set message
            progressDialog.show(); // Shows the message while fetching data
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            CurrentIncident currentIncident = null;

            try {
                URL url = new URL(urlSourceCurrentIncidents);

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
                            currentIncident = new CurrentIncident();
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")) { // If the start tag is for a title
                            if (insideItem) { // If we are inside an item
                                currentIncident.setTitle(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")) {
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
                    }

                    else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                            CurrentIncidentsArrayList.add(currentIncident);
                            Log.e("MyTag", "Current Incident is " + currentIncident.toString());
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
            for (int i=0; i < CurrentIncidentsArrayList.size(); i++){
                titles.add(CurrentIncidentsArrayList.get(i).getTitle());
                descriptions.add(CurrentIncidentsArrayList.get(i).getDescription());
            }

            CurrentIncidentAdapter adapter = new CurrentIncidentAdapter(getApplicationContext(), 0 , CurrentIncidentsArrayList);

            currentIncidentsList.setAdapter(adapter);

            progressDialog.dismiss(); // Gets rid of fetching data message
            setUpOnclickListener();
        }
    }

    private void setUpOnclickListener() {
        currentIncidentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CurrentIncident selectedCurrentIncident = (CurrentIncident) (currentIncidentsList.getItemAtPosition(position));
                Intent showDetails = new Intent(getApplicationContext(), DetailCurrentIncidentActivity.class);
                showDetails.putExtra("link", selectedCurrentIncident.getLink());
                startActivity(showDetails);
                searchView.clearFocus();
            }
        });
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSourceCurrentIncidents)).start();

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

            CurrentIncidentsActivity.this.runOnUiThread(new Runnable()
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
        searchView = (SearchView) findViewById(R.id.currentIncidentSearchView);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s){
                searchThread thread = new searchThread(s);
                thread.start();
                return false;

            }
        });
    }

    class searchThread extends Thread{
        String s;

        searchThread(String s){
            this.s = s;
        }

        @Override
        public void run(){
            currentSearchText = s;
            ArrayList<CurrentIncident> filteredCurrentIncidents = new ArrayList<CurrentIncident>();

            for(CurrentIncident currentIncident: CurrentIncidentsArrayList)
            {
                if(currentIncident.getTitle().toLowerCase().contains(s.toLowerCase()))
                {
                    filteredCurrentIncidents.add(currentIncident);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CurrentIncidentAdapter adapter = new CurrentIncidentAdapter(getApplicationContext(), 0, filteredCurrentIncidents);
                    currentIncidentsList.setAdapter(adapter);
                }
            });
        }
    }
}

