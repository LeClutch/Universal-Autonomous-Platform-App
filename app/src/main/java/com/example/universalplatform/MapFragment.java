package com.example.universalplatform;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    GoogleMap map;
    MapView mapView;
    boolean inDirection = false;


    String status;
    Button sendDestination;
    Button followME;
    Switch indoorOutdoor;
    boolean distanceSet = false;
    boolean setDistance = false;

    private DatabaseReference reference;
    private DatabaseReference referencePlatform;
    private DatabaseReference referencePlatformDLA;
    private DatabaseReference referencePlatformDLO;
    private DatabaseReference referencePlatformFollow;
    private DatabaseReference referencePlatformStatus;
    private DatabaseReference stopMovingPlatform;
    private DatabaseReference goToDestination;
    private LocationManager manager;


    private final int MIN_TIME = 500; // 1sec
    private final int MIN_DISTANCE = 1; // 1meter

    Marker myMarker;
    Marker platformMarker;
    private double destinationLatitude;
    private double destinationLongitude;

    private String currentUser;
    private byte [] listOfMarkersDestination ;
    MarkerOptions markerOptions = new MarkerOptions();

    Activity context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Get the current user UID from firebase
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser);  // Get current user to send info about user location
        referencePlatform = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1");  // Get current platform to read info about its location
        referencePlatformStatus = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("status");
        stopMovingPlatform = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("stop");
        goToDestination = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("go-to-destination");
        getLocationUpdates();
        readChanges(reference);
        readChangesPlatform(referencePlatform);

        return inflater.inflate(R.layout.fragment_map, container, false);

    }
    //to read user new location
    private void readChanges(DatabaseReference reference) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        MyLocation location = snapshot.getValue(MyLocation.class);
                        if (location != null) {
                            myMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                            float zoomLevel = 16.0f; //This goes up to 21
                            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
                        }
                    }catch (Exception e){
                        //Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //to read the changes in the platform location
    private void readChangesPlatform(DatabaseReference reference) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        MyLocation location = snapshot.getValue(MyLocation.class);
                        if (location != null) {
                            platformMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                            float zoomLevel = 16.0f; //This goes up to 21
                            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
                        }
                    }catch (Exception e){
                       // Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //this is to get the user location
    private void getLocationUpdates() {
        if(manager != null){
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else {
                    Toast.makeText(getActivity(), "No provider enebled", Toast.LENGTH_SHORT).show();
                }
            }else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }
        }
    }


    // for location permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates();
            }else {
                Toast.makeText(getActivity(),"Permission Required",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        indoorOutdoor = (Switch) context.findViewById(R.id.switchIndor);
        sendDestination = (Button)  context.findViewById(R.id.btnDirection);
        followME = (Button)  context.findViewById(R.id.btnFollow);

        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }


        //This is to check if the platform is connected to the app
        referencePlatformStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                status = snapshot.getValue(String.class);

                if(!status.equals("Connected")){
                    platformMarker.setVisible(false);
                    setDistance = false;
                }

                if(status.equals("Connected")){
                    setDistance = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        map = googleMap;
        LatLng sydney = new LatLng(45.49492920810111, -73.57746334332835);
        myMarker = map.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_person_icon_3)).title("You"));
        float zoomLevel = 10.0f;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
        platformMarker = map.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.rob_marker2)).title("Universal Platform"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.animateCamera(CameraUpdateFactory.newLatLng(sydney));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);


            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //Create marker
                    if(setDistance == true) {
                    //Set marker position, make it draggable and set title
                    markerOptions.position(latLng).draggable(true).title("Destination");
                    destinationLatitude = latLng.latitude;
                    destinationLongitude = latLng.longitude;
                    //Clear previous clicked position
                    map.clear();
                    distanceSet = true;
                    map.addMarker(markerOptions);
                    LatLng sydney = new LatLng(45.49492920810111, -73.57746334332835);
                    myMarker = map.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_person_icon_3)).title("You"));
                    platformMarker = map.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.rob_marker2)).title("Universal Platform"));
                    }
                }

            });






            sendDestination.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(distanceSet == true && sendDestination.getText().toString().equals("Go To Direction")) {
                    referencePlatformDLA = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("destination").child("latitude");
                    referencePlatformDLA.setValue(destinationLatitude);
                    referencePlatformDLO = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("destination").child("longitude");
                    referencePlatformDLO.setValue(destinationLongitude);
                    stopMovingPlatform.setValue(false);
                    goToDestination.setValue(true);
                    followME.setVisibility(View.INVISIBLE);
                    sendDestination.setBackgroundResource(R.drawable.rounded_button_3);
                    sendDestination.setText("STOP PLATFORM");
                    return;
                    }

                    if(distanceSet == true && sendDestination.getText().toString().equals("STOP PLATFORM")){
                        //send true to stop platform from moving
                        stopMovingPlatform.setValue(true);
                        goToDestination.setValue(false);
                        followME.setVisibility(View.VISIBLE);
                        sendDestination.setBackgroundResource(R.drawable.rounded_button);
                        sendDestination.setText("Go To Direction");
                        return;
                    }

                }
            });


        followME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(followME.getText().toString().equals("FOLLOW ME") ) {
                referencePlatformFollow = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("follow-user");
                referencePlatformFollow.setValue(true);
                stopMovingPlatform.setValue(false);
                followME.setText("STOP FOLLOWING ME");
                followME.setBackgroundResource(R.drawable.rounded_button_3);
                sendDestination.setVisibility(View.INVISIBLE);
                }
                else if (followME.getText().toString().equals("STOP FOLLOWING ME")){
                    referencePlatformFollow = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("follow-user");
                    referencePlatformFollow.setValue(false);
                    stopMovingPlatform.setValue(true);
                    followME.setBackgroundResource(R.drawable.rounded_button);
                    if(!indoorOutdoor.isChecked()) {
                        sendDestination.setVisibility(View.VISIBLE);
                    }
                    followME.setText("FOLLOW ME");
                }
            }
        });


        indoorOutdoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(indoorOutdoor.isChecked())
                    sendDestination.setVisibility(View.INVISIBLE);
                else if(!indoorOutdoor.isChecked()){
                    if (followME.getText().toString().equals("STOP FOLLOWING ME")) {
                        sendDestination.setVisibility(View.INVISIBLE);
                    }else{sendDestination.setVisibility(View.VISIBLE);}
                }
            }
        });



    }

    //  *********for user location information**********
    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            saveLocation(location);
        }else {
            Toast.makeText(getActivity(),"No Location", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation(Location location) {
        reference.setValue((location));   //write location value into database
    }
    //********end of methods for user location*****
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

    }


}