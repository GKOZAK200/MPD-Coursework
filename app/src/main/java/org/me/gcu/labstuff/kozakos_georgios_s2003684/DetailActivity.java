package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        int position = previousIntent.getIntExtra("position", 0);
        System.out.println(position);
        selectedPlannedRoadwork = MainActivity.PlannedRoadworksArrayList.get(position);
    }

    private void setValues()
    {
        TextView title = (TextView) findViewById(R.id.titleName);
        TextView description = (TextView) findViewById(R.id.descriptionView);
        TextView pubDate = (TextView) findViewById(R.id.pubDateView);

        title.setText(selectedPlannedRoadwork.getTitle());

        String DescriptionText =  selectedPlannedRoadwork.getDescription();
        DescriptionText = DescriptionText.replace("<br />", "\n");
        description.setText(DescriptionText);

        pubDate.setText("Publish date: "+selectedPlannedRoadwork.getPubDate());
    }
}