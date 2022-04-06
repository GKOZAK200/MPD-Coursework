package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity
{
    PlannedRoadwork selectedPlannedRoadwork;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSelectedPlannedRoadwork();
        setValues();

    }

    private void getSelectedPlannedRoadwork()
    {
        Intent previousIntent = getIntent();
        String parsedStringLink = previousIntent.getStringExtra("link");
        //selectedPlannedRoadwork = MainActivity.PlannedRoadworksArrayList.get(parsedStringLink);
    }

    private void setValues()
    {
        TextView tv = (TextView) findViewById(R.id.titleName);

        //tv.setText(PlannedRoadwork.getTitle());
    }
}