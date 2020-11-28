package com.example.activitytracker.Dashboard.model;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitytracker.Dashboard.DashboardMainActivity;
import com.example.activitytracker.Dashboard.Note_details;
import com.example.activitytracker.R;
import com.example.activitytracker.Reminder.Remindermain;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireBaseAdapter extends FirestoreRecyclerAdapter<Note, FireBaseAdapter.NoteHolder> {


    public FireBaseAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }
///Missing Doc ID Misssing
    @Override
    protected void onBindViewHolder(@NonNull NoteHolder noteHolder, int i, @NonNull Note note) {
   /*     noteViewHolder.noteTitle.setText(note.getTitle());
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
        });*/
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
        return new NoteHolder(view);
    }

    class NoteHolder extends RecyclerView.ViewHolder{

    TextView noteTitle,noteContent;
    View view;
    CardView mCardView;
    public NoteHolder(@NonNull View itemView) {
        super(itemView);
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
}
