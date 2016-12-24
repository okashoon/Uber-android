package com.parse.starter;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class YourLocation extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;

    Button requestUberButton;
    TextView statusTextView;

    LatLng latLng;


    boolean requested = false;

    public void requestUber(View view){
        if (!requested ) {
            final ParseObject request = new ParseObject("Request");
            request.put("rider", ParseUser.getCurrentUser().getUsername());

            ParseGeoPoint geoPoint = new ParseGeoPoint(latLng.latitude,latLng.longitude);
            request.put("location", geoPoint);

            ParseACL acl = new ParseACL();
            acl.setPublicReadAccess(true);
            acl.setPublicWriteAccess(true);
            request.setACL(acl);
            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    requestUberButton.setText("Cancel UBER");
                    statusTextView.setText("Finding UBER driver");
                    requested = true;


                }
            });
        } else {

            requestUberButton.setText("Request UBER");
            statusTextView.setText("Ride cancelled");
            requested = false;

            ParseQuery<ParseObject> query =ParseQuery.getQuery("Request");
            query.whereEqualTo("rider",ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects){
                            object.deleteInBackground();
                        }
                    }
                }
            });


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_location);

        requestUberButton = (Button)findViewById(R.id.requestButton);
        statusTextView = (TextView) findViewById(R.id.statusTextView);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(),false);
        locationManager.requestLocationUpdates(provider,400,1,this);


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            latLng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Your position"));

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        mMap.clear();
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your position"));

        //update rider location whenever he moves
        if(requested) {
            final ParseGeoPoint geoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
            ParseQuery<ParseObject> query =ParseQuery.getQuery("Request");
            query.whereEqualTo("rider",ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects){
                            object.put("location", geoPoint);
                            object.saveInBackground();
                        }
                    }
                }
            });


        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
