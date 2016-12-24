package com.parse.starter;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.security.Provider;
import java.util.HashMap;
import java.util.List;

public class ViewRiderLocation extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    double lat;
    double lng;
    String userName;

    LatLng riderPosition;
    LatLng driverPosition;

    Location location;
    String provider;

    Marker driverMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rider_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userName = getIntent().getStringExtra("userName");
        String latString = getIntent().getStringExtra("lat");
        lat = Double.parseDouble(latString);
        String lngString = getIntent().getStringExtra("lng");
        lng = Double.parseDouble(lngString);

        Log.i("aaa","userName = " + userName + ", lat = " + lat + ", lng = " + lng);

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(),false);
        locationManager.requestLocationUpdates(provider,500,0,this);

        location = locationManager.getLastKnownLocation(provider);
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


        riderPosition = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(riderPosition).title(userName));

        driverPosition = new LatLng(location.getLatitude(),location.getLongitude());
        driverMarker = mMap.addMarker(new MarkerOptions().position(driverPosition).title("Me"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        final LatLngBounds bounds =builder.include(riderPosition)
                              .include(driverPosition)
                              .build();

        View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,200));
            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,5,5,0));

    }

    @Override
    public void onLocationChanged(final Location location) {

        this.location = location;
        driverPosition = new LatLng(location.getLatitude(),location.getLongitude());
       driverMarker.setPosition(driverPosition);

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
