package com.example.universalplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView batteryTextView;
    ProgressBar progressBar;
    ImageView robotImageView;
    TextView connectionStatusTextView;
    Button informationImageView;
    String nameOfUser;
    TextInputEditText name;
    String TAG="MainActivity";

    private Fragment homeFrag;
    private Fragment mapFrag;
    private Fragment infoFrag;
    private Fragment accFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mAuth = FirebaseAuth.getInstance();
        name=findViewById(R.id.fullName);

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            homeFrag = new HomeFragment();
            mapFrag = new MapFragment();
            infoFrag = new InfoFragment();
            accFrag = new AccountFragment();
        }

        ft.add(R.id.fragment_container, homeFrag, "H");
        ft.commit();
        Log.d(TAG,"in on create");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nameOfUser = extras.getString("fullName");
            Log.d(TAG,"full name is --->"+name);
        }

    } // end of onCreate

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            //a user is logged in
        }
        else{
            startActivity(new Intent(this,SignInActivity.class));
            finish();
        }
    } // end of onStart


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            //show the selected item in the nav bar
            switch (item.getItemId()) {
                case R.id.homeButton:
                    // check if the fragment is already in container
                    if (homeFrag.isAdded()) {
                        ft.show(homeFrag);
                    } else {
                        // fragment needs to be added to frame container
                        ft.add(R.id.fragment_container, homeFrag, "H");
                    }
                    // Hide fragment
                    if (mapFrag.isAdded()) { ft.hide(mapFrag); }
                    // Hide fragment
                    if (infoFrag.isAdded()) { ft.hide(infoFrag); }
                    //Hide fragment
                    if (accFrag.isAdded()) { ft.hide(accFrag); }
                    // Commit changes
                    ft.commit();
                    break;
                case R.id.mapButton:
                    if (mapFrag.isAdded()) {
                        ft.show(mapFrag);
                    }
                    else { ft.add(R.id.fragment_container, mapFrag, "M"); }
                    if (homeFrag.isAdded()) { ft.hide(homeFrag); }
                    if (infoFrag.isAdded()) { ft.hide(infoFrag); }
                    if (accFrag.isAdded()) { ft.hide(accFrag); }
                    ft.commit();
                    break;
                case R.id.informationButton:
                    if (infoFrag.isAdded()) {
                        ft.show(infoFrag);
                    }
                    else { ft.add(R.id.fragment_container, infoFrag, "I"); }
                    if (homeFrag.isAdded()) { ft.hide(homeFrag); }
                    if (mapFrag.isAdded()) { ft.hide(mapFrag); }
                    if (accFrag.isAdded()) { ft.hide(accFrag); }
                    ft.commit();
                    break;
                case R.id.accountButton:
                    if (accFrag.isAdded()) {
                        ft.show(accFrag);
                    }
                    else { ft.add(R.id.fragment_container, accFrag, "A"); }
                    if (homeFrag.isAdded()) { ft.hide(homeFrag); }
                    if (infoFrag.isAdded()) { ft.hide(infoFrag); }
                    if (mapFrag.isAdded()) { ft.hide(mapFrag); }
                    ft.commit();
            }//end of switch

            return true;
        }
    };



} // end of class MainActivity
