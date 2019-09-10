package com.pato.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

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
        mCoursesLayoutMgr = new GridLayoutManager(this, 2);  //Courses Layout manager-Using Grid Layout.

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
            handleSelection("Don't you think you've shared enough. ");

        } else if (id == R.id.nav_send) {
            handleSelection("Send");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleSelection(String message) {
        //a snackbar needs a view from the current activity.
        View view = findViewById(R.id.recycler_list_items);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
