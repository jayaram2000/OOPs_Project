package com.example.activitytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.activitytracker.Dashboard.DashboardMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Button verifybtn,resendbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        verifybtn=findViewById(R.id.verifybtn);
        resendbtn=findViewById(R.id.resendbtn);

        resendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Verification Email sent again", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Email not verified"+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Task<Void> verified =firebaseAuth.getCurrentUser().reload();
               {
                    Intent dashboard = new Intent(getApplication(), MainActivity.class);
                    startActivity(dashboard);
                }
            }
        });

    }
}