package com.example.universalplatform;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Integer.parseInt;

public class InfoFragment extends Fragment {


    Activity context;
    private DatabaseReference referencePlatformWeight;
    private DatabaseReference referencePlatformEvent;
    private DatabaseReference referencePlatformBattery;
    private DatabaseReference platformRange;
    private String weightText;
    TextView weight;
    private String eventText;
    TextView event;
    private String batteryText;
    TextView range;
    ProgressBar progressBar;
    private String rangeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        referencePlatformWeight = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("weight");
        referencePlatformEvent = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("event");
        referencePlatformBattery = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("battery");
        platformRange = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("range");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weight = (TextView)  context.findViewById(R.id.weightTextView);
        event = (TextView) context.findViewById(R.id.eventNameText);
        progressBar = (ProgressBar) context.findViewById(R.id.batteryProgressBar);
        range = (TextView) context.findViewById(R.id.rangeTextView);

        referencePlatformWeight.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                weightText = snapshot.getValue(Long.class).toString();
                weight.setText("Carried Weight: " + weightText + " Kg(s)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referencePlatformEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventText = snapshot.getValue(String.class);
                event.setText("Device: " + eventText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referencePlatformBattery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                batteryText = snapshot.getValue(Long.class).toString();

                // set progress bar
                progressBar.setProgress(parseInt(batteryText));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        platformRange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rangeText = snapshot.getValue(Long.class).toString();

                range.setText("Range: "+rangeText+" km");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}