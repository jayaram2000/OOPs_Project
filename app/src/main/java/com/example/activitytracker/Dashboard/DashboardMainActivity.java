package com.example.activitytracker.Dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.activitytracker.Dashboard.model.Adapter;
import com.example.activitytracker.Dashboard.model.FireBaseAdapter;
import com.example.activitytracker.Dashboard.model.Note;
import com.example.activitytracker.MainActivity;
import com.example.activitytracker.R;
import com.example.activitytracker.Reminder.Remindermain;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DashboardMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    TextView name, email;
    RecyclerView noteLists;
    Adapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseUser firebaseUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteLists = findViewById(R.id.notelist);


        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        View headerView = nav_view.getHeaderView(0);
        name = headerView.findViewById(R.id.userDisplayName);
        email = headerView.findViewById(R.id.userDisplayEmail);

        DocumentReference doc = firebaseFirestore.collection("users").document(firebaseUser.getUid());

        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                name.setText(value.get("profession").toString());
                email.setText(value.get("email").toString());
            }
        });


        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("usernotes");


        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();


        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull Note note) {

                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final int code = getRandomColor();

                noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));
                String doc = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), Note_details.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("noteid", doc);
                        i.putExtra("code", code);
                        v.getContext().startActivity(i);
                    }
                });


                ImageView img = noteViewHolder.view.findViewById(R.id.menuIcon);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.setGravity(Gravity.START);


                        menu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + note.getTitle() + "\nContent: " + note.getContent());
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                startActivity(shareIntent);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference del = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("usernotes").document(doc);
                                del.delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error Failed to delete please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
                return new NoteViewHolder(view);
            }
        };

        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), add_notes.class));

            }

        });
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //set adapter was here
        noteLists.setAdapter(noteAdapter);



    }
    private void firebaseSearch(String s) {


        Query FireSearchQuery = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("usernotes").orderBy("title").startAt("s").endAt("s" + "\uf8ff");


        FirestoreRecyclerOptions<Note> Fsearch = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(FireSearchQuery, Note.class)
                .build();

       // noteAdapter.updateoptions(Fsearch);
       noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(Fsearch) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull Note note) {

                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final int code = getRandomColor();

                noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));
                String doc = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), Note_details.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("noteid", doc);
                        i.putExtra("code", code);
                        v.getContext().startActivity(i);
                    }
                });


                ImageView img = noteViewHolder.view.findViewById(R.id.menuIcon);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.setGravity(Gravity.START);
                        menu.getMenu().add("Set Reminder").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                                                          @Override
                                                                                          public boolean onMenuItemClick(MenuItem item) {

                                                                                              Intent reminder = new Intent(getApplicationContext(), Remindermain.class);
                                                                                              startActivity(reminder);
                                                                                              return false;
                                                                                          }

                                                                                      }
                        );

                        menu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + note.getTitle() + "\nContent: " + note.getContent());
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                startActivity(shareIntent);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference del = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("usernotes").document(doc);
                                del.delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error Failed to delete please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        menu.show();
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
                return new NoteViewHolder(view);
            }
        };

        Log.d("mudiyala", "firebaseSearch: evel");

        noteLists.setAdapter(noteAdapter);

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

               firebaseSearch(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //  adapter.getFilter().filter(newText);
                firebaseSearch(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNote: {
                startActivity(new Intent(getApplicationContext(), add_notes.class));

                break;
            }
            case R.id.logout: {
                FirebaseAuth.getInstance().signOut();
                Intent signout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(signout);
                finish();
            }
            case R.id.notes: {
                finish();
                startActivity(getIntent());

            }
        }
        return false;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("inside bind view", "onBindViewHolder: ");
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    private int getRandomColor() {

        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);

    }




    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

}
