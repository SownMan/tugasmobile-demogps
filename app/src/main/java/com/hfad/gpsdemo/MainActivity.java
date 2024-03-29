package com.hfad.gpsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 100;
    TextView tv_lat, tv_lon, tv_altidude, tv_accuracy, tv_speed,tv_sensor, tv_updates,tv_address;

    Switch sw_locationupdates, sw_gps;

    //variable to remember if we are tracking laocation or not
    boolean updateOn = false;

    //location request config file for all setting relatd to FusedLocatinProviderClient

    LocationRequest locationRequest;

    //google's API for location services.hampir semua function pada app ini menggunakan kelas dibawah
    FusedLocationProviderClient fusedLocationProviderClient;
    //test123dwadwadw
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // give each UI variable a value
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altidude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);


        // set all propertis of locationrequest

        locationRequest = new LocationRequest();

        //how often does the default location check occur?
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        //how often does the location check occur when set to the null //request update
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    // most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensor");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + Wifi");
                }

            }
        });
        updateGPS();
    }// end on create method

        private void updateGPS(){
            //get permission form the user to track GPS
            // get the current locatioon from the fused client
            // update the UI - i.e. set all properties in their associated text view items

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                //user provide the permission
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //we got permission.Put the values of location. into the UI components
                        updateUIValues(location);

                    }
                });
            }
            else{
                //permission  not granted yet.

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);

                }
            }
        }

    private void updateUIValues(Location location) {
        //update all of the text view objects with a new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            tv_altidude.setText(String.valueOf(location.getAltitude()));
        }
        else{
            tv_altidude.setText("Not Available");
        }

        if (location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else{
            tv_speed.setText("Not Available");
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateGPS();
            }else{
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }}
