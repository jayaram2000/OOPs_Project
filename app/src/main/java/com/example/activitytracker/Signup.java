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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Signup extends Fragment {
    EditText personFullName,personEmailAddress,personPass,personConfPass,phoneCountryCode,phoneNumber;
    Button regsiterAccountBtn;
    Boolean isDataValid = false;
    FirebaseAuth fAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        personFullName = v.findViewById(R.id.registerFullName);
        personEmailAddress = v.findViewById(R.id.registerEmail);
        personPass = v.findViewById(R.id.regsiterPass);
        personConfPass = v.findViewById(R.id.retypePass);
        phoneCountryCode = v.findViewById(R.id.countryCode);
        phoneNumber = v.findViewById(R.id.registerPhoneNumber);

        fAuth = FirebaseAuth.getInstance();
        regsiterAccountBtn = v.findViewById(R.id.registerBtn);

        regsiterAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData(personFullName);
                validateData(personEmailAddress);
                validateData(personPass);
                validateData(personConfPass);
                validateData(phoneCountryCode);
                validateData(phoneNumber);


                if(!personPass.getText().toString().equals(personConfPass.getText().toString())){
                    isDataValid = false;
                    personConfPass.setError("Password Do not Match");
                }else {
                    isDataValid = true;
                }

       if(isDataValid){
            // proceed with the registration of the user
    try {
        fAuth.createUserWithEmailAndPassword(personEmailAddress.getText().toString(),personPass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getActivity(),"Please check email and verify",Toast.LENGTH_SHORT).show();
                FirebaseUser user = authResult.getUser();

                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                        Intent EmailVerify = new Intent(getActivity(), VerifyEmail.class);
                        startActivity(EmailVerify);



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Erro occurred while sending email verification",Toast.LENGTH_SHORT).show();

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error ! Failed to add user ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    catch (Exception e)
    {
        Toast.makeText(getContext(),"Couldn't Register the user",Toast.LENGTH_SHORT).show();
    }

        }
            }
        });

        return v;
    }

    private void validateData(EditText field) {
        if(field.getText().toString().isEmpty()){
            isDataValid = false;
            field.setError("Required Field.");
        }else {
            isDataValid = true;
        }
    }
}