package com.example.activitytracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activitytracker.Dashboard.DashboardMainActivity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class Login extends Fragment implements OnConnectionFailedListener {
    EditText loginEmail,editTextTextPassword;
    Button loginBtn;
    SignInButton gsign;
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private static final int SIGN_IN= 1;
    private FirebaseAuth mAuth,fAuth;
    Boolean isDataValid=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn=v.findViewById(R.id.loginBtn);
        loginEmail=v.findViewById(R.id.loginEmail);
        editTextTextPassword=v.findViewById(R.id.editTextTextPassword);
        gsign=v.findViewById(R.id.gsign);
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
         mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()),gso);

        mAuth = FirebaseAuth.getInstance();
        fAuth =FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData(loginEmail);
                validateData(editTextTextPassword);
                if(isDataValid)
                {
                    EmailSign();
                }

            }
        });

        gsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GsingIn();
            }
        });

        return v;
    }

    private void EmailSign() {
        mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), editTextTextPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                           updateUI(user);
                        } else {

                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }


                    }
                });
    }



    private void GsingIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            Log.d("suc", "onActivityResult: suc");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            firebaseAuthWithGoogle(account.getIdToken());

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();

            //updateUI(null);
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Signing success", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIGsign(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("failure", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(),"Authentication Failed.",Toast.LENGTH_SHORT);

                            updateUI(null);
                        }


                    }
                });
    }



    private void validateData(EditText field) {
        if(field.getText().toString().isEmpty()){
            isDataValid = false;
            field.setError("Required Field.");
        }else {
            isDataValid = true;
        }
    }
// Both method login provides a Firebase object
private void updateUIGsign(FirebaseUser user) {
     if(user!=null)
         {
             Intent dashboard = new Intent(getActivity(),DashboardMainActivity.class);
             startActivity(dashboard);
         }

}
    private void updateUI(FirebaseUser user) {

        if(user!=null && user.isEmailVerified())
        {
            Intent dashboard = new Intent(getActivity(),DashboardMainActivity.class);
            startActivity(dashboard);
        }
        else if(user==null)
        {
            Toast.makeText(getActivity(),"Please check your credentials or Register Account",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(),"Please Verify Email",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Toast.makeText(getActivity(),connectionResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
    }
}