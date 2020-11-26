package com.example.activitytracker.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.activitytracker.MainActivity;
import com.example.activitytracker.R;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class DashboardMainActivity extends AppCompatActivity {
    Button signout;
    EditText name;
    //Get users details to acces the database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);

        Intent login_details = getIntent();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        signout = findViewById(R.id.signout);
        name=findViewById(R.id.name);
        name.setText(firebaseUser.getEmail().toString());
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getApplication()), gso);
              //name.setText(mGoogleSignInClient.getInstanceId());

                mGoogleSignInClient.signOut();
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();
            }
        });

    }
}