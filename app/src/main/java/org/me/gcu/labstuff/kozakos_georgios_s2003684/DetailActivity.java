package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

// Name: Georgios Kozakos   Matric Number: S2003684

public class DetailActivity extends AppCompatActivity {
    private static double Latitude;
    private static double Longitude;
    PlannedRoadwork selectedPlannedRoadwork;

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
        getSelectedPlannedRoadwork();
        setValues();


        Fragment fragment = new MapFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.mapView, fragment).commit();
    }

    private void getSelectedPlannedRoadwork() {
        Intent previousIntent = getIntent();
        String link = previousIntent.getStringExtra("link");

        for (PlannedRoadwork plannedRoadwork : MainActivity.PlannedRoadworksArrayList) {
            if (plannedRoadwork.getLink().equals(link)) {
                selectedPlannedRoadwork = plannedRoadwork;
            }
        }
    }

    private void setValues() {
        TextView title = (TextView) findViewById(R.id.titleName);
        TextView description = (TextView) findViewById(R.id.descriptionView);
        TextView pubDate = (TextView) findViewById(R.id.pubDateView);

        String title1 = selectedPlannedRoadwork.getTitle();
        title.setText(title1);

        String DescriptionText = selectedPlannedRoadwork.getDescription();
        DescriptionText = DescriptionText.replace("<br />", "\n");
        description.setText(DescriptionText);

        pubDate.setText("Publish date: " + selectedPlannedRoadwork.getPubDate());

        String[] CoordsArray = selectedPlannedRoadwork.getCoordsArray();
        Latitude = Double.parseDouble(CoordsArray[0]);
        Longitude = Double.parseDouble(CoordsArray[1]);
    }

}