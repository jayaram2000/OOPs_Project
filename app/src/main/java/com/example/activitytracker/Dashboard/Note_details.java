package com.example.activitytracker.Dashboard;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.activitytracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Note_details extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private int notificationId = 1;

    String noteTitle = "<Note Title>", reminderTitle;
    private EditText dateField, timeField, message;
    private String date, time, frequency;


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

        date = "11/28/20";
        time = "5:13 PM";

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
        switch (item.getItemId()) {
            case R.id.reminder:
                openReminderDialog(item);
                break;
            case R.id.share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + title.getText().toString() + "\nContent: " + content.getText().toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

                break;
            case android.R.id.home: {
                onBackPressed();
            }

        }
            return super.onOptionsItemSelected(item);

    }
    public void openReminderDialog(MenuItem item){
        final MenuItem[] itemList = new MenuItem[1]; itemList[0] = item;

        AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mview = getLayoutInflater().inflate(R.layout.dialog, null);

        message = mview.findViewById(R.id.message);

        dateField = mview.findViewById(R.id.datefield);
        dateField.setText(date);
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
                date = simpleDateFormat.format(calendar.getTime());
                dateField.setText(date);
            }
        };
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Note_details.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeField = mview.findViewById(R.id.timefield);
        timeField.setText(time);
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                time = simpleDateFormat.format(calendar.getTime());
                timeField.setText(time);
            }
        };
        timeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Note_details.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        });

        final Spinner mspinner =  mview.findViewById(R.id.frequency);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.frequencyList));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinner.setAdapter(adapter);


        Button setBtn = mview.findViewById(R.id.setBtn);
        Button cancelBtn = mview.findViewById(R.id.cancelBtn);

        mbuilder.setView(mview).setTitle("Set Reminder");
        final AlertDialog alertDialog = mbuilder.create();

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frequency = mspinner.getSelectedItem().toString();

                Intent intent = new Intent(getApplication(), com.example.timer.AlarmReceiver.class);
                intent.putExtra("notificationId", notificationId);
                intent.putExtra("notificationTitle", reminderTitle);
                intent.putExtra("notificationMsg", message.getText().toString());

                pendingIntent = PendingIntent.getBroadcast(Note_details.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                alarmManager =  (AlarmManager) getSystemService(ALARM_SERVICE);
                if(frequency.equals("Do not Repeat")){
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                else if(frequency.equals("Daily")){
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                }
                else if(frequency.equals("Weekly")){
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, pendingIntent);
                }

                itemList[0].setIcon(R.drawable.ic_baseline_notifications_active_24);
                Toast.makeText(getApplicationContext(), "Reminder Set", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmManager.cancel(pendingIntent);
                itemList[0].setIcon(R.drawable.ic_baseline_notifications_24);
                Toast.makeText(getApplicationContext(), "Reminder Cancelled", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }


}