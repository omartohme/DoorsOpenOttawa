package com.algonquinlive.tohm0011.omar.doorsopenottawa;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquinlive.tohm0011.omar.doorsopenottawa.model.Building;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.utils.HttpMethod;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.utils.RequestPackage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private Geocoder mGeocoder;

    private TextView detailsName;
    private TextView detailsDescription;
    private TextView detailsAddress;
    private Integer buildingID;

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
            Integer BuildingIDthatIuse = bundle.getInt("buildingID");

            buildingID = BuildingIDthatIuse;


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

    private void deleteBuilding(View uri) {

        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.DELETE );
        pkg.setUri( uri + "/" + buildingID );
        DoTask deleteTask = new DoTask();
        deleteTask.execute( pkg );
    }
    private class DoTask extends AsyncTask<RequestPackage, String, String> {



        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {


            if (result == null) {
                Toast.makeText(DetailsActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);

        deleteBuilding(findViewById(R.id.details_delete));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        return super.onOptionsItemSelected(item);
    }
}