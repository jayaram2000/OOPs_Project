package com.example.activitytracker.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activitytracker.Dashboard.model.Adapter;
import com.example.activitytracker.MainActivity;
import com.example.activitytracker.R;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    EditText name,email;
    RecyclerView noteLists;
    Adapter adapter;

    //Get users details to acces the database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);

        Intent login_details = getIntent();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteLists = findViewById(R.id.notelist);


        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        name=nav_view.findViewById(R.id.userDisplayName);
        email=nav_view.findViewById(R.id.userDisplayEmail);
        try{
            Log.d("uid", "onCreate: "+firebaseUser.getUid().toString());
            //TO DO  Add name and profession once database is connected
            //name.setText(firebaseUser.getDisplayName().toString());
            //email.setText(firebaseUser.getEmail().toString());
        }
        catch (Exception e)
        {
          //  Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("yo", "onCreate: "+e.getMessage());
        }

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();
        titles.add("Jayaram J");
        content.add("the myth the legenfd");

        titles.add("physics");
        content.add("t is a long established fact that a reader will be distracted by the");

        titles.add("Computer");
        content.add("using Lorem Ipsum is that it has a more-or-less normal distribution");
        titles.add("Jayaram J3");
        content.add("over the years, sometimes by accident, sometimes on purpose");

        adapter= new Adapter(titles,content);
        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.search_bar,menu);
       MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addNote:
            {
                startActivity(new Intent(getApplicationContext(),add_notes.class));
                break;
            }
            case R.id.logout:
            {
                FirebaseAuth.getInstance().signOut();
                Intent signout = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(signout);
                finish();
            }
            case R.id.notes:
            {
                break;
            }
        }
        return false;
    }
}