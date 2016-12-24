package com.parse.starter;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverActivity extends AppCompatActivity implements LocationListener {

    ListView requestsList;
    ArrayList<Map<String,String>> requests;
    SimpleAdapter adapter;

    Location location;
    LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        locationManager = (LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(),false);
        locationManager.requestLocationUpdates(provider,400,1,this);

        location = locationManager.getLastKnownLocation(provider);



        requestsList = (ListView)findViewById(R.id.requestsListView);

        requests =  new ArrayList<>();

        adapter = new SimpleAdapter(this,requests,android.R.layout.simple_list_item_2,new String[]{"rider","distance"},
                                                    new int[]{android.R.id.text1,android.R.id.text2});
        requestsList.setAdapter(adapter);

        requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(),ViewRiderLocation.class);
                i.putExtra("userName", requests.get(position).get("rider"));
                i.putExtra("lat", requests.get(position).get("lat"));
                i.putExtra("lng", requests.get(position).get("lng"));
                startActivity(i);
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.findInBackground(new FindCallback<ParseObject> () {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                requests.clear();

                for (ParseObject object : objects) {


                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("rider",object.getString("rider"));

                    ParseGeoPoint riderLocation = object.getParseGeoPoint("location");
                    map.put("lat",Double.toString(riderLocation.getLatitude()));
                    map.put("lng",Double.toString(riderLocation.getLongitude()));

                    if (location != null) {
                        ParseGeoPoint driverLocation = new ParseGeoPoint(location.getLatitude(),location.getLongitude());
                        double distance = riderLocation.distanceInKilometersTo(driverLocation);

                        map.put("distance", Double.toString(distance)+" Kilometers");
                        requests.add(map);
                    } else {
                        Log.i("aaa", " location is null");
                    }


                }
                adapter.notifyDataSetChanged();

            }
        });



    }

    @Override
    public void onLocationChanged(final Location location) {

        this.location = location;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.findInBackground(new FindCallback<ParseObject> () {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                requests.clear();

                for (ParseObject object : objects) {

                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("rider",object.getString("rider"));

                    ParseGeoPoint riderLocation = object.getParseGeoPoint("location");
                    map.put("lat",Double.toString(riderLocation.getLatitude()));
                    map.put("lng",Double.toString(riderLocation.getLongitude()));

                    ParseGeoPoint driverLocation = new ParseGeoPoint(location.getLatitude(),location.getLongitude());
                    double distance = riderLocation.distanceInKilometersTo(driverLocation);

                    map.put("distance", String.format("%.2f",distance)+" Kilometers");
                    requests.add(map);

                }
                adapter.notifyDataSetChanged();

            }
        });

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
