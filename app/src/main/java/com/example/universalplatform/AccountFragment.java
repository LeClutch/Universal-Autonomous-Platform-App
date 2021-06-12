package com.example.universalplatform;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    Activity context;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser appUser = auth.getCurrentUser();
    String email;
    String thePassword;
    // Edit button

    Dialog mDialog;
    String TAG = "accountFragment";

    ProgressDialog pd;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        //init the progress dialog
        pd = new ProgressDialog(getActivity());
/*
    //receive password from SignupActivity
        Bundle bundle = getArguments();
        String message = bundle.getString("password");
       thePassword=message;

*/

// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();
        Button signOutButton = (Button) context.findViewById(R.id.signOutButton);

        Button editEmail = (Button) context.findViewById(R.id.editEmailButton);
        Button editPassword = (Button) context.findViewById(R.id.editPasswordButton);

        //    EditText emailTextBox = (EditText) context.findViewById((R.id.editEmailButton);
        //     EditText passwordTextBox = (EditText) context.findViewById(R.id.editPasswordButton);


/*


        //must use getView() in fragments
        Button editEmail = (Button) context.findViewById(R.id.editEmailButton);
        Button editPassword = (Button) context.findViewById(R.id.editPasswordButton);
        EditText currentEmail = (EditText) context.findViewById(R.id.editEmailAddress);
        EditText currentPassword = (EditText) context.findViewById(R.id.editPassword);

        //get current email and password
        email =currentEmail.getText().toString();
        password = currentPassword.getText().toString();
*/


        if (appUser != null) {
            email = appUser.getEmail();


            //when user clicks on edit email
            editEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                showChangeEmailDialog();
                }


            });

            editPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangePasswordDialog();
                }
            });


        } else {
            Log.d(TAG, "no user registered");
            // No user is signed in
        }


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignInActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
            }
        });


    }

    private void showChangePasswordDialog() {
        //inflate layout for dialog
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_password, null);


        final EditText currentPass = view.findViewById(R.id.currentPassword);
        final EditText newPass = view.findViewById(R.id.newPassword);
        final Button updatePassButton = view.findViewById(R.id.updatePasswordButton);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set view to dialog
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                String oldPassword = currentPass.getText().toString().trim();
                String newPassword = newPass.getText().toString().trim();

                if (TextUtils.isEmpty(oldPassword)) {
                    Toast.makeText(getActivity(), "Enter your current password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6) {
                    Toast.makeText(getActivity(), "Password should be atleast 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                updatePassword(oldPassword, newPassword);
            }
        });

    }

    private void updatePassword(String oldPassword, final String newPassword) {
        pd.show();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassword);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //successfully authenticated, begin update
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //password is now updated

                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Password is updated", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed updating password, give a reason
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //unsuccesful authentication, give a reason
                        pd.dismiss();
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showChangeEmailDialog(){
        //inflate layout for dialog
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_email, null);


        final EditText currentPass = view.findViewById(R.id.currentPassword2);
        final EditText newEmail = view.findViewById(R.id.newEmailInput);
        final Button updateEmailButton = view.findViewById(R.id.updateEmailButton);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set view to dialog
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                String currentPassword = currentPass.getText().toString().trim();
                String newEmailString = newEmail.getText().toString().trim();

                if (TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(getActivity(), "Enter your current password", Toast.LENGTH_SHORT).show();
                   return;
                }

                if (newEmailString.length() < 6) {
                   Toast.makeText(getActivity(), "Password should be atleast 6 characters", Toast.LENGTH_SHORT).show();
                  return;
                }
                dialog.dismiss();
                updateEmail(currentPassword, newEmailString);
            }
        });

    }

    private void updateEmail(final String currentPassword, final String newEmail) {
        pd.show();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), currentPassword);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //successfully authenticated, begin update
                        user.updateEmail(newEmail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Email is now updated

                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Email is updated", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed updating email, give a reason
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //unsuccesful authentication, give a reason
                        pd.dismiss();
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
