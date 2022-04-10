// Name: Georgios Kozakos   Matric Number: S2003684

package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DetailCurrentIncidentActivity extends AppCompatActivity {
    private static double Latitude;
    private static double Longitude;
    CurrentIncident selectedCurrentIncident;

    public static double getLatitude() {
        return Latitude;
    }

    public static double getLongitude() {
        return Longitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSelectedCurrentIncident();
        setValues();


        Fragment fragment = new CurrentIncidentMapFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.mapView, fragment).commit();
    }

    private void getSelectedCurrentIncident() {
        Intent previousIntent = getIntent();
        String link = previousIntent.getStringExtra("link");

        for (CurrentIncident currentIncident : CurrentIncidentsActivity.CurrentIncidentsArrayList) {
            if (currentIncident.getLink().equals(link)) {
                selectedCurrentIncident = currentIncident;
            }
        }
    }

    private void setValues() {
        TextView title = (TextView) findViewById(R.id.titleName);
        TextView description = (TextView) findViewById(R.id.descriptionView);
        TextView pubDate = (TextView) findViewById(R.id.pubDateView);

        String title1 = selectedCurrentIncident.getTitle();
        title.setText(title1);

        String DescriptionText = selectedCurrentIncident.getDescription();
        DescriptionText = DescriptionText.replace("<br />", "\n");
        description.setText(DescriptionText);

        pubDate.setText("Publish date: " + selectedCurrentIncident.getPubDate());

        String[] CoordsArray = selectedCurrentIncident.getCoordsArray();
        Latitude = Double.parseDouble(CoordsArray[0]);
        Longitude = Double.parseDouble(CoordsArray[1]);
    }

}