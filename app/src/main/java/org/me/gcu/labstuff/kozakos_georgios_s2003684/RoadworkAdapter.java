// Name: Georgios Kozakos   Matric Number: S2003684

package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RoadworkAdapter extends ArrayAdapter<Roadwork>
{

    public RoadworkAdapter(Context context, int resource, List<Roadwork> roadworkList)
    {
        super(context,resource,roadworkList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Roadwork roadwork = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell, parent, false);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.titleName);
        TextView startDateView = (TextView) convertView.findViewById(R.id.startDateView);
        TextView endDateView = (TextView) convertView.findViewById(R.id.endDateView);

        titleView.setText(roadwork.getTitle());
        startDateView.setText("From: "+roadwork.getStartDate());
        endDateView.setText("To: "+roadwork.getEndDate());

        if (roadwork.getDays()<5){
            convertView.setBackgroundColor(Color.parseColor("#caffbf"));
        }
        else if (roadwork.getDays()<10){
            convertView.setBackgroundColor(Color.parseColor("#fdffb6"));
        }
        else if (roadwork.getDays()<15){
            convertView.setBackgroundColor(Color.parseColor("#ffd6a5"));
        }
        else{
            convertView.setBackgroundColor(Color.parseColor("#ffadad"));
        }


        return convertView;
    }
}