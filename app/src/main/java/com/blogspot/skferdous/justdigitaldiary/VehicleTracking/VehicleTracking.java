package com.blogspot.skferdous.justdigitaldiary.VehicleTracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.blogspot.skferdous.justdigitaldiary.Model.VehicleModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;

public class VehicleTracking extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double lat = 0.0, llong = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_tracking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MapView();
    }

    public void MapView() {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vehicle Admin").child("Vehicle");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        VehicleModel model = snapshot.getValue(VehicleModel.class);
                        if (model != null) {
                            if (lat - (Double.parseDouble(model.getLatitude())) != 0 && llong - (Double.parseDouble(model.getLongitude())) != 0) {
                                LatLng latLng = new LatLng(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude()));
                                setMarker(latLng);
                            }
                        }
                        lat = Double.parseDouble(model.getLatitude());
                        llong = Double.parseDouble(model.getLongitude());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(VehicleTracking.this, databaseError.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Title");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);
    }
}
