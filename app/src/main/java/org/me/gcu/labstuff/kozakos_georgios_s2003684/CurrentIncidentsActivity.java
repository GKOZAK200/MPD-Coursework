package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CurrentIncidentsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_incidents);
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
}