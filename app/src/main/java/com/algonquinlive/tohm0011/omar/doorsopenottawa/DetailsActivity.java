package com.algonquinlive.tohm0011.omar.doorsopenottawa;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private Geocoder mGeocoder;

    private TextView detailsName;
    private TextView detailsDescription;
    private TextView detailsAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGeocoder = new Geocoder( this, Locale.CANADA);
        detailsName = (TextView) findViewById(R.id.detailsName);
        detailsDescription = (TextView) findViewById(R.id.detailsDescription);
        detailsAddress = (TextView) findViewById(R.id.detailsAddress);

        // Get the bundle of extras that was sent to this activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String displayedNameBuilding = bundle.getString("name");
            String displayedDescriptionBuilding = bundle.getString("description");
            String displayedAddressBuilding = bundle.getString("address");

            detailsName.setText(displayedNameBuilding);
            detailsDescription.setText(displayedDescriptionBuilding);
            detailsAddress.setText(displayedAddressBuilding);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String mapAddress = bundle.getString("address");
            pin(mapAddress);
        }

    }

    private void pin(String locationName) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll,14f));
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }
}