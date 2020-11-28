package com.example.activitytracker.Dashboard;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activitytracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class add_notes extends AppCompatActivity {
    FirebaseFirestore fStore;
    EditText noteTitle,noteContent;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);
        user = FirebaseAuth.getInstance().getCurrentUser();





        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitle.getText().toString();
                String content =noteContent.getText().toString();
                if(title.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Title field can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(content.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Content can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference doc = fStore.collection("notes").document(user.getUid()).collection("usernotes").document(title);
                Map<String,Object> newnote = new HashMap<>();
                newnote.put("title",title);
                newnote.put("content",content);
                doc.set(newnote).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Note saved",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),DashboardMainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Couldn't save please try again",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}