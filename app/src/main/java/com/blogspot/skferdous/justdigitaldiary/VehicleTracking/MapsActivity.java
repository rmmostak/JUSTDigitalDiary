package com.blogspot.skferdous.justdigitaldiary.VehicleTracking;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Model.VehicleModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

/*
    private LocationManager manager;
    private ArrayList<LatLng> latLngArrayList = new ArrayList();
    private final int MIN_TIME = 1000; //1sec
    private final int MIN_DISTANCE = 1; //1meter

    Marker myMarker;
    Double last = 0.0;*/


    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        reference = FirebaseDatabase.getInstance().getReference("Vehicle Admin").child("Vehicle");

        //manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000); //1 sec
        locationRequest.setInterval(2000); //2 sec
        locationRequest.setSmallestDisplacement(1); //1 meter

        //Toast.makeText(MapsActivity.this, "Size: "+latLngArrayList.size(), Toast.LENGTH_LONG).show();

        //readChanges();
    }

    /*    private void readChanges() {
        try {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        VehicleModel model = snapshot.getValue(VehicleModel.class);
                        if (model != null) {
                            LatLng latLng = new LatLng(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude()));
                            myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(model.getVehicleName()));

                            */

    /*if (latLng.latitude != last) {
                                myMarker.remove();
                                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(model.getVehicleName()));

                            } else {
                                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(model.getVehicleName()));
                            }*/

    /*

                            mMap.setMinZoomPreference(14);
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            mMap.getUiSettings().setAllGesturesEnabled(true);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            if (latLng.latitude != last) {
                                myMarker.remove();
                                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(model.getVehicleName()));
                            }
                            last = Double.parseDouble(model.getLatitude());

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }*/


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.setMinZoomPreference(12);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setAllGesturesEnabled(true);

        /*for (int i = 0; i < latLngArrayList.size(); i++) {
            myMarker.setPosition(new LatLng(latLngArrayList.get(i).latitude, latLngArrayList.get(i).longitude));
            myMarker = mMap.addMarker(new MarkerOptions().title("Marker Title"));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }*/

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        myMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    /*    private void setMarker(LatLng latLng, String vehicleName) {
        myMarker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
        myMarker = mMap.addMarker(new MarkerOptions().title(vehicleName));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }*/

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}