// Name: Georgios Kozakos   Matric Number: S2003684

package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class CurrentIncidentMapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(googleMap -> {

            LatLng place = new LatLng(DetailCurrentIncidentActivity.getLatitude(), DetailCurrentIncidentActivity.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(place).title(DetailCurrentIncidentActivity.getLatitude() + " " + DetailCurrentIncidentActivity.getLongitude()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15f));
        });

        return view;
    }
}