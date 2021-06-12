package com.example.universalplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String TAG="SignupActivity";
    EditText writtenEmail;
    EditText writtenPassword;
    Button signUpButton;
    ProgressBar progressBar;
    TextInputEditText fullName;
    private DatabaseReference reference;
    private String currentUser;
    String email, password;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // ask user to enter email and password

        initializeUI();
        //save user input

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final AccountFragment myFragment = new AccountFragment();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Bundle b = new Bundle();
                b.putString("password", writtenPassword.getText().toString());
                myFragment.setArguments(b);
                fragmentTransaction.add(R.id.frameLayout, myFragment).commit();
                */
                signUpUserAccount();
            }
        });

    }




    public void signUpUserAccount() {


        progressBar.setVisibility(View.VISIBLE);


        email = writtenEmail.getText().toString();
        password = writtenPassword.getText().toString();
        name = fullName.getText().toString();


    //send password info to fragment AccountFragment

        // invalid cases
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }


        //sign up new users
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignupActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "createUserWithEmail:success");
                            progressBar.setVisibility(View.GONE);

                            //Direct the user to the mainActivity after successfully signing up
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            //save full name
                            intent.putExtra("fullName",name);
                            startActivity(intent);



                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            reference = FirebaseDatabase.getInstance().getReference().child("users");
                            reference.setValue(currentUser);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        // ...
                    }
                });

    }

    public void initializeUI(){
        writtenEmail=findViewById(R.id.editEmailAddress);
        writtenPassword= findViewById(R.id.editPassword);
        signUpButton=findViewById(R.id.signUpButton);
       // progressBar=findViewById(R.id.progressBar);
        fullName=findViewById(R.id.fullName);

    }


   /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            //a user is logged in, direct them to the MainActivity
            startActivity(new Intent(this,MainActivity.class));
        }
    }
*/

}