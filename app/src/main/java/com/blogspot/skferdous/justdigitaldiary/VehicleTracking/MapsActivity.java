package com.blogspot.skferdous.justdigitaldiary.VehicleTracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;
import static com.blogspot.skferdous.justdigitaldiary.VehicleTracking.VehicleLocationService.START_LOCATION_SERVICE;
import static com.blogspot.skferdous.justdigitaldiary.VehicleTracking.VehicleLocationService.STOP_LOCATION_SERVICE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener {

    private static int REQUEST_CODE = 101;

    private Marker myMarker, shapla, golap, rojoni, voirab;
    private LatLng prevLatlng;
    Map<String, LatLng> vehicleList = new HashMap<>();
    List<String> nameList = new ArrayList<>();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference reference;

    private GoogleMap mMap;
    private LocationRequest locationRequest = new LocationRequest();
    private FusedLocationProviderClient fusedLocationClient;
    String vName = "";

    private ImageButton serviceControl;
    private Spinner vehicleSpinner;
    private TextView activeVName;
    private LinearLayout driverLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        serviceControl = findViewById(R.id.serviceControl);
        activeVName = findViewById(R.id.activeVName);
        driverLayout = findViewById(R.id.driverLayout);
        vehicleSpinner = findViewById(R.id.vehicleSpinner);

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout = findViewById(R.id.coordinator);
        if (!isConnected()) {
            Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                        return;
                    }).show();
        }

        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (vehicleSpinner.getSelectedItemPosition() > 0) {
                    try {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vehicle Admin").child("Vehicle");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    VehicleModel model = snapshot.getValue(VehicleModel.class);
                                    if (model != null) {
                                        if (model.getVehicleName().equals(vehicleSpinner.getSelectedItem())) {
                                            LatLng latLng = new LatLng(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude()));
                                            if (mMap != null) {

                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));

                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } catch (Exception e) {
                        Log.d("searchError", e.getMessage());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences preferences = getSharedPreferences("driver", MODE_PRIVATE);
        if (preferences.getBoolean("vehicle", false)) {
            //Log.d("preference", "state " + preferences.getBoolean("vehicle", false));
            driverLayout.setVisibility(View.VISIBLE);
            serviceControl.setVisibility(View.VISIBLE);
            activeVName.setVisibility(View.VISIBLE);
            try {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vehicle Admin").child("Vehicle").child(Objects.requireNonNull(auth.getUid()));
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        VehicleModel model = snapshot.getValue(VehicleModel.class);
                        if (model != null) {
                            vName = (model.getVehicleName());
                        }
                        activeVName.setText(vName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.d("profileError", e.getMessage());
            }
            //profile.setVisibility(View.VISIBLE);
            //Log.d("active", vName + "2");

            if (isServiceRunning()) {
                serviceControl.setImageResource(R.drawable.ic_clear);
                serviceControl.setOnClickListener(v -> {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, REQUEST_CODE);
                    } else {
                        startLocationService();
                        serviceControl.setImageResource(R.drawable.ic_clear);
                        if (isServiceRunning()) {
                            stopLocationService();
                            serviceControl.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        }
                    }
                });
            } else {
                serviceControl.setOnClickListener(v -> {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, REQUEST_CODE);
                    } else {
                        startLocationService();
                        serviceControl.setImageResource(R.drawable.ic_clear);
                        if (isServiceRunning()) {
                            stopLocationService();
                            serviceControl.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        }
                    }
                });
            }
        } else {
            myLocation();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void myLocation() {
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(250); //.25 sec
        locationRequest.setInterval(500); //.5 sec
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
                        //Log.d("check", locationResult.getLastLocation().getLatitude() + "Latitude");
                        LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                        if (mMap != null) {
                            if (myMarker == null) {
                                MarkerOptions options = new MarkerOptions().position(latLng).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.per_loc));
                                myMarker = mMap.addMarker(options);
                            } else {
                                myMarker.setPosition(latLng);
                            }

                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                    } catch (Exception e) {
                        Log.d("LocationResultError", e.getMessage());
                    }
                }
            }, Looper.myLooper());
        }
    }

    private boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (VehicleLocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isServiceRunning()) {
            Intent intent = new Intent(MapsActivity.this, VehicleLocationService.class);
            intent.setAction(START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(getApplicationContext(), "Service started!", Toast.LENGTH_LONG).show();
        }
    }

    private void stopLocationService() {
        if (isServiceRunning()) {
            Intent intent = new Intent(MapsActivity.this, VehicleLocationService.class);
            intent.setAction(STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(getApplicationContext(), "Service stopped!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private String myName() {
        String name = "";
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vehicle Admin").child("Vehicle").child(Objects.requireNonNull(auth.getUid()));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    VehicleModel model = snapshot.getValue(VehicleModel.class);
                    if (model != null) {
                        vName = (model.getVehicleName());
                    }
                    //activeVName.setText(vName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.d("profileError", e.getMessage());
        }
        name = vName;
        return name;
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.2250903, 89.1229433), 13f));

        reference = FirebaseDatabase.getInstance().getReference("Vehicle Admin").child("Vehicle");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    VehicleModel model = snapshot.getValue(VehicleModel.class);
                    LatLng latLng = new LatLng(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude()));
                    vehicleList.put(model.getVehicleName(), latLng);
                    nameList.add(model.getVehicleName());

                }
                setMap(vehicleList, nameList);
                double distance = 0.0;
                //distance = distance + Distance(prevLatlng.latitude, prevLatlng.longitude, vehicleList.get(myName()).latitude, vehicleList.get(myName()).longitude);

                Log.d("distance", distance+" m");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private double Distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void setMap(Map<String, LatLng> vehicleList, List<String> nameList) {
        try {
            if (vehicleList.size() > 0 && nameList.size() > 0) {
                for (int i = 0; i < vehicleList.size(); i++) {
                    switch (nameList.get(i)) {
                        case "Shapla":
                            if (nameList.get(i).equals(myName())) {
                                if (shapla == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.per_loc));
                                    shapla = mMap.addMarker(options);
                                } else {
                                    shapla.setPosition(vehicleList.get(nameList.get(i)));
                                }
                                prevLatlng = new LatLng(vehicleList.get(nameList.get(i)).latitude, vehicleList.get(nameList.get(i)).longitude);
                            } else {
                                if (shapla == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i));
                                    shapla = mMap.addMarker(options);
                                } else {
                                    shapla.setPosition(vehicleList.get(nameList.get(i)));
                                }
                            }
                            break;

                        case "Kapotakkho":
                            //Log.d("Map", vehicleList.get(nameList.get(i)) + " " + nameList.get(i));
                            if (nameList.get(i).equals(myName())) {
                                if (golap == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.per_loc));
                                    golap = mMap.addMarker(options);
                                } else {
                                    golap.setPosition(vehicleList.get(nameList.get(i)));
                                }
                                prevLatlng = new LatLng(vehicleList.get(nameList.get(i)).latitude, vehicleList.get(nameList.get(i)).longitude);
                            } else {
                                if (golap == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i));
                                    golap = mMap.addMarker(options);
                                } else {
                                    golap.setPosition(vehicleList.get(nameList.get(i)));
                                }
                            }
                            break;

                        case "Rojoni Gandha":
                            //Log.d("Map", vehicleList.get(nameList.get(i)) + " " + nameList.get(i));
                            if (nameList.get(i).equals(myName())) {
                                if (rojoni == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.per_loc));
                                    rojoni = mMap.addMarker(options);
                                } else {
                                    rojoni.setPosition(vehicleList.get(nameList.get(i)));
                                }
                                prevLatlng = new LatLng(vehicleList.get(nameList.get(i)).latitude, vehicleList.get(nameList.get(i)).longitude);
                            } else {
                                if (rojoni == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i));
                                    rojoni = mMap.addMarker(options);
                                } else {
                                    rojoni.setPosition(vehicleList.get(nameList.get(i)));
                                }
                            }
                            break;

                        case "Voirab":
                            //Log.d("Map", vehicleList.get(nameList.get(i)) + " " + nameList.get(i));
                            if (nameList.get(i).equals(myName())) {
                                if (voirab == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.per_loc));
                                    voirab = mMap.addMarker(options);
                                } else {
                                    voirab.setPosition(vehicleList.get(nameList.get(i)));
                                }
                                prevLatlng = new LatLng(vehicleList.get(nameList.get(i)).latitude, vehicleList.get(nameList.get(i)).longitude);
                            } else {
                                if (voirab == null) {
                                    MarkerOptions options = new MarkerOptions().position(vehicleList.get(nameList.get(i)))
                                            .title(nameList.get(i));
                                    voirab = mMap.addMarker(options);
                                } else {
                                    voirab.setPosition(vehicleList.get(nameList.get(i)));
                                }
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("setMapError", e.getMessage());
        }
        vehicleList.clear();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}