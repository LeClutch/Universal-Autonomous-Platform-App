package com.example.universalplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String TAG="SignInActivity";
    EditText signInEmail;
    EditText signInPassword;
    Button signInButton;
    ProgressBar progressBar;
    Button signUpButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mAuth = FirebaseAuth.getInstance();

        // ask user to enter email and password

        initializeUI();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUserAccount();
            }
        });

        signUpButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Direct the user to signUpActivity
                Intent intent = new Intent(SignInActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }




    public void signInUserAccount() {


//        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = signInEmail.getText().toString();
        password = signInPassword.getText().toString();


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
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignInActivity.this, "Successfully signed in", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "sign in with Email:success");
                            // THIS LINE IS CAUSING AN ISSUE
                            //progressBar.setVisibility(View.GONE);

                            //Direct the user to the mainActivity after successfully signing in
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);



                            FirebaseUser user = mAuth.getCurrentUser();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // progressBar.setVisibility(View.GONE);
                        }

                        // ...
                    }
                });

    }

    public void initializeUI(){
        signInEmail=findViewById(R.id.signInEmail);
        signInPassword= findViewById(R.id.signInPassword);
        signInButton=findViewById(R.id.signInButton);
       // progressBar=findViewById(R.id.progressBar);
        signUpButton2=findViewById(R.id.signUpButton2);
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            //a user is logged in, direct them to the MainActivity
            startActivity(new Intent(this,MainActivity.class));
            // So user does not just come back by using back button
            finish();
        }


    }


}
