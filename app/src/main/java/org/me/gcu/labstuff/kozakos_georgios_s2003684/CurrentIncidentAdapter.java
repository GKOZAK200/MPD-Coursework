// Name: Georgios Kozakos   Matric Number: S2003684

package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CurrentIncidentAdapter extends ArrayAdapter<CurrentIncident> {

    public CurrentIncidentAdapter(Context context, int resource, List<CurrentIncident> currentIncidentList) {
        super(context, resource, currentIncidentList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CurrentIncident currentIncident = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell, parent, false);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.titleName);
        TextView startDateView = (TextView) convertView.findViewById(R.id.startDateView);
        TextView endDateView = (TextView) convertView.findViewById(R.id.endDateView);

        titleView.setText(currentIncident.getTitle());
        startDateView.setText("");
        endDateView.setText("");

        return convertView;
    }
}