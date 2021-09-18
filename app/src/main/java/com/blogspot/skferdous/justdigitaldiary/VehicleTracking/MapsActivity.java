package com.blogspot.skferdous.justdigitaldiary.VehicleTracking;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.VehicleModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener {

    private static int REQUEST_CODE = 101;
    private LocationManager manager;

    private Marker myMarker, shapla, golap, rojoni;
    Map<String, LatLng> vehicleList = new HashMap<>();
    List<String> nameList = new ArrayList<>();
    LatLng lat;

    private GoogleMap mMap;
    private LocationRequest locationRequest = new LocationRequest();
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        reference = FirebaseDatabase.getInstance().getReference("Vehicle Admin").child("Vehicle");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000); //1 sec
        locationRequest.setInterval(2000); //2 sec
        locationRequest.setSmallestDisplacement(1); //1 meter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE);
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    try {
                        Log.d("check", locationResult.getLastLocation().getLatitude() + "Latitude");
                        LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                        if (mMap != null) {
                            if (myMarker == null) {
                                MarkerOptions options = new MarkerOptions().position(latLng).title("My Marker");
                                myMarker=mMap.addMarker(options);
                            } else {
                                myMarker.setPosition(latLng);
                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                    } catch (Exception e) {
                        Log.d("LocationResultError", e.getMessage());
                    }
                }
            }, Looper.myLooper());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12f));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    VehicleModel model = snapshot.getValue(VehicleModel.class);
                    LatLng latLng = new LatLng(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude()));
                    vehicleList.put(model.getVehicleName(), latLng);
                    nameList.add(model.getVehicleName());
                    Log.d("name", model.getVehicleName());
                }
                setMap(vehicleList, nameList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setMap(Map<String, LatLng> vehicleList, List<String> nameList) {
        for (int i = 0; i < vehicleList.size(); i++) {
            if (mMap != null) {
                switch (nameList.get(i)) {
                    case "Shapla":
                        if (shapla == null) {
                            MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                    .title(nameList.get(i));
                            shapla = mMap.addMarker(options);
                        } else {
                            shapla.setPosition(vehicleList.get(nameList.get(i)));
                        }
                        break;
                    case "Kapotakkho":
                        //Log.d("Map", vehicleList.get(nameList.get(i)) + " " + nameList.get(i));
                        if (golap == null) {
                            MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                    .title(nameList.get(i));
                            golap = mMap.addMarker(options);
                        } else {
                            golap.setPosition(vehicleList.get(nameList.get(i)));
                        }
                        break;
                }
            }
        }
        vehicleList.clear();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}