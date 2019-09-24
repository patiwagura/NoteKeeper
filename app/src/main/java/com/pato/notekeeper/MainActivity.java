package com.pato.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNotesLayoutMgr;
    private GridLayoutManager mCoursesLayoutMgr;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new note
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        //Set default preference values.
        // False :- only use default when preference value is not set.
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }


    private void initializeDisplayContent() {

        //Components we need for RecyclerView implementation / Initialization.
        // 1. LayoutManager
        // 2. Design Item-View Layout (used to style individual View-items of the recyclerView)
        // 3. Adapter.
        mRecyclerItems = (RecyclerView) findViewById(R.id.recycler_list_items);
        mNotesLayoutMgr = new LinearLayoutManager(this);  //Notes Layout Manager.
        //Courses Layout manager-Using Grid Layout. we specify the column_span using a resource. helps in code adaptability.
        mCoursesLayoutMgr = new GridLayoutManager(this,
                getResources().getInteger(R.integer.course_grid_span));

        //Initialize Notes-List and Notes_Adapter.
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);  //create the Recycler-Adapter.

        //Initialize courses and course-Adapter.
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);

        displayNotes();
    }

    private void displayNotes() {
        //method to display notes. Set-up NotesAdapter and the LayoutManager to display Notes.
        mRecyclerItems.setLayoutManager(mNotesLayoutMgr);
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_notes); //Highlight Notes as current selected-MenuItem.
    }

    private void displayCourses() {
        //method responsible to display Courses. set-up the Adapter and Layout manager to display courses.
        mRecyclerItems.setLayoutManager(mCoursesLayoutMgr);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_courses); //Highlight courses as current selected-MenuItem.
    }


    private void selectNavigationMenuItem(int mnuItemId) {
        //menu -groups allows a single item to be selected. In this case we want to highlight the current-selected menuItem.
        NavigationView myNavView = (NavigationView) findViewById(R.id.nav_view); //root element of navigation view.
        Menu myMenu = myNavView.getMenu(); //get menu contained within navigationView.
        myMenu.findItem(mnuItemId).setChecked(true); //display the menu as the current selected option.
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Notify RecyclerAdapter datachanged, refresh our data every time we resume activity.
        mNoteRecyclerAdapter.notifyDataSetChanged();

        //method to update the NavHeader, its called when user returns from SettingsScreen and every time we return to activity.
        updateNavHeader();
    }

    private void updateNavHeader() {
        //get a NavigationView reference.
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);

        //get textViews references from HeaderView.
        TextView txtUserName = (TextView)headerView.findViewById(R.id.text_user_name);
        TextView txtEmailAddress = (TextView)headerView.findViewById(R.id.text_email_address);

        //Reading values from SharedPreference we need a reference to SharedPreference
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        //Retrieve saved values from Preferences. pref_key :- key that was used to save the preference.
        // default_value :- default value in-case the user has not set value.
        String userName = pref.getString("user_display_name", "");
        String emailAddress = pref.getString("user_email_address", "");

        //set values to the TestViews
        txtUserName.setText(userName);
        txtEmailAddress.setText(emailAddress);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //show settings Activity.
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            //handle notes action.
            //handleSelection("Notes");
            displayNotes();
        } else if (id == R.id.nav_courses) {
            displayCourses();

        } else if (id == R.id.nav_share) {
            //handleSelection(getString(R.string.nav_share_message));
            //handleSelection(R.string.nav_share_message);
            handleShare();

        } else if (id == R.id.nav_send) {
            //handleSelection(getString(R.string.nav_send_message));
            handleSelection(R.string.nav_send_message);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        //method to interact with Social preferences
        View view = findViewById(R.id.recycler_list_items);
        Snackbar.make(view, "Share to - " +
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social",""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(int message_id) {
        //a snackbar needs a view from the current activity.
        View view = findViewById(R.id.recycler_list_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();
    }
}
