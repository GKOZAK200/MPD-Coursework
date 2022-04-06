package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlannedRoadworkAdapter extends ArrayAdapter<PlannedRoadwork>
{

    public PlannedRoadworkAdapter(Context context, int resource, List<PlannedRoadwork> plannedRoadworkList)
    {
        super(context,resource,plannedRoadworkList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PlannedRoadwork plannedRoadwork = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell, parent, false);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.titleName);
        TextView startDateView = (TextView) convertView.findViewById(R.id.startDateView);
        TextView endDateView = (TextView) convertView.findViewById(R.id.endDateView);

        titleView.setText(plannedRoadwork.getTitle());
        startDateView.setText("From: "+plannedRoadwork.getStartDate());
        endDateView.setText("To: "+plannedRoadwork.getEndDate());


        return convertView;
    }
}