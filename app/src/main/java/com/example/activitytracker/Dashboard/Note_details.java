package com.example.activitytracker.Dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activitytracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Note_details extends AppCompatActivity {

    EditText title,content;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent data = getIntent();
        String docId=data.getStringExtra("noteid");

        firebaseFirestore =FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();



        title=findViewById(R.id.noteDetailsTitle);
        content=findViewById(R.id.contentid);
        content.setMovementMethod(new ScrollingMovementMethod());

        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title_fire = title.getText().toString();
                String content_fire = content.getText().toString();
                if (title_fire.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Title field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (content_fire.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Content can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference doc = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("usernotes").document(docId);
                Map<String, Object> newnote = new HashMap<>();
                newnote.put("title", title_fire);
                newnote.put("content", content_fire);

                doc.update(newnote).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), DashboardMainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Couldn't save please try again", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}