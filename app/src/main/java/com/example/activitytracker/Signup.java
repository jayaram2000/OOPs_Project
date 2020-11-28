package com.example.activitytracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Signup extends Fragment {
    EditText personFullName, personEmailAddress, personPass, personConfPass, phoneCountryCode, phoneNumber;
    TextView profession;
    Spinner profession_spinner;
    Button regsiterAccountBtn;
    Boolean isDataValid = false;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;


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
        profession = v.findViewById(R.id.profession);
        profession_spinner = (Spinner) v.findViewById(R.id.profession_spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,
                getResources().getStringArray(R.array.professions));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profession_spinner.setAdapter(myAdapter);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
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

                String email = personEmailAddress.getText().toString();
                String fullName = personFullName.getText().toString();
                String phoneNo = phoneCountryCode.getText().toString() + phoneNumber.getText().toString();
                String Profession = profession_spinner.getSelectedItem().toString();

                if (Profession.equals("Select")) {
                    isDataValid = false;
                    profession.setError("Required Field.");
                } else {
                    isDataValid = true;
                }

                if (!personPass.getText().toString().equals(personConfPass.getText().toString())) {
                    isDataValid = false;
                    personConfPass.setError("Password Do not Match");
                } else {
                    isDataValid = true;
                }

                if (isDataValid) {

                    try {
                        fAuth.createUserWithEmailAndPassword(personEmailAddress.getText().toString(), personPass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(getActivity(), "Please check email and verify", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getActivity(), "Error occurred while sending email verification", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userID);
                                Map<String, Object> User = new HashMap<>();
                                User.put("fullName", fullName);
                                User.put("email", email);
                                User.put("phoneNumber", phoneNo);
                                User.put("profession", Profession);

                                documentReference.set(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "OnSuccess: user details are saved for " + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "OnFailure: " + e.toString());
                                    }
                                });

                                List<String> titles = new ArrayList<>();
                                List<String> content = new ArrayList<>();

                                switch (Profession) {
                                    case "Home-Maker":
                                        titles.add("Groceries");
                                        content.add("1. Bread\n2. Milk\n3. Eggs");
                                        titles.add("Kitchen utensils");
                                        content.add("1. Spoon\n2. Bottles\n3. Plates");
                                        titles.add("Home Maintenance");
                                        content.add("1. Fused lights\n2. Cracked window\n3. No Water");
                                        break;
                                    case "Working Professional":
                                        titles.add("Groceries");
                                        content.add("1. Bread\n2. Milk\n3. Eggs");
                                        titles.add("Bills");
                                        content.add("1. Electricity\n2. Rent\n3. Water");
                                        titles.add("Work");
                                        content.add("1. Complete Task 1\n2. Complete Task 2\n3. Complete Task 3");
                                        break;
                                    case "Student":
                                        titles.add("Food Left");
                                        content.add("1. Bread\n2. Milk\n3. Eggs");
                                        titles.add("Stationary");
                                        content.add("1. Pencil\n2. Pen\n3. Notebooks");
                                        titles.add("Textbooks");
                                        content.add("1. Maths\n2. CS\n3. English");
                                        break;
                                    case "Other":
                                        break;

                                }

                                for (int i = 0; i < 3; i++) {
                                    DocumentReference doc = fstore.collection("notes").document(user.getUid()).collection("usernotes").document(titles.get(i));
                                    Map<String, Object> newnote = new HashMap<>();

                                    newnote.put("title", titles.get(i));
                                    newnote.put("content", content.get(i));

                                    doc.set(newnote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "OnSuccess: Default notes are saved");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG", "OnFailure: " + e.toString());
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error! Failed to add user :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Couldn't Register the user", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        return v;
    }

    private void validateData(EditText field) {
        if (field.getText().toString().isEmpty()) {
            isDataValid = false;
            field.setError("Required Field.");
        } else {
            isDataValid = true;
        }
    }
}