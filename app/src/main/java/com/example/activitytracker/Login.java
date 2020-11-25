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
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class Login extends Fragment implements OnConnectionFailedListener {
    EditText loginEmail,editTextTextPassword;
    Button loginBtn;
    SignInButton gsign;
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private static final int SIGN_IN= 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn=v.findViewById(R.id.loginBtn);
        loginEmail=v.findViewById(R.id.loginEmail);
        editTextTextPassword=v.findViewById(R.id.editTextTextPassword);
        gsign=v.findViewById(R.id.gsign);
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
         mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()),gso);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        gsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                singIn();
            }
        });

        return v;
    }

    private void singIn() {

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
            // Signed in successfully, show authenticated UI.

            Intent login = new Intent(getActivity(), DashboardMainActivity.class);
            startActivity(login);
            String email=account.getEmail().toString();


            // passs intent for dashboardd
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();

            //updateUI(null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Toast.makeText(getActivity(),connectionResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
    }
}