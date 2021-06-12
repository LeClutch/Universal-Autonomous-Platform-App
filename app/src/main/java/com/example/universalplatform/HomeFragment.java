package com.example.universalplatform;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Integer.parseInt;

public class HomeFragment extends Fragment {
    Activity context;
    String statusText;
    TextView status;
    ProgressBar progressBar;
    Button connectBtn;
    String connectBtnText;
    private DatabaseReference referencePlatformStatus;
    private DatabaseReference referencePlatformConnect;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        referencePlatformStatus = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("status");
        referencePlatformConnect = FirebaseDatabase.getInstance().getReference().child("platforms").child("p1").child("connect");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        status = (TextView) context.findViewById(R.id.connectionStatusTextView);
        progressBar = (ProgressBar) context.findViewById(R.id.batteryProgressBar);
        connectBtn = (Button) context.findViewById(R.id.connectButton);

        referencePlatformStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusText = snapshot.getValue(String.class);
                status.setText("Device " + statusText);
                if(statusText.equals("Connected")){
                    connectBtn.setText("DISCONNECT");
                }else if(statusText.equals("Disconnected")){
                    connectBtn.setText("CONNECT");
                }
                else {
                    status.setText("DEVICE STATUS");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               connectBtnText = connectBtn.getText().toString();
               if(connectBtn.getText().toString().equals("CONNECT")){
                   referencePlatformConnect.setValue(true);
                   if(statusText.equals("Connected")){
                       connectBtn.setText("DISCONNECT");
                   }
               }else if(connectBtn.getText().toString().equals("DISCONNECT")){
                   referencePlatformConnect.setValue(false);
                   if(statusText.equals("Disconnected")){
                       connectBtn.setText("CONNECT");
                   }
               }

            }
        });

    }
} // end of class HomeFragment