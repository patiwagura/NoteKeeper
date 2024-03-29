package com.pato.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.pato.notekeeper.NoteKeeperDBContract.CourseInfoEntry;
import com.pato.notekeeper.NoteKeeperDBContract.NoteInfoEntry;
import com.pato.notekeeper.NoteKeeperProviderContract.Notes;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES = 0;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNotesLayoutMgr;
    private GridLayoutManager mCoursesLayoutMgr;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private NoteKeeperOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instance to DatabaseOpenHelper. Interacting with Database is resource intensive.
        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }


    private void initializeDisplayContent() {
        //Load data from the database.
        DataManager.loadFromDatabase(mDbOpenHelper);

        //Components we need for RecyclerView implementation / Initialization.
        // 1. LayoutManager
        // 2. Design Item-View Layout (used to style individual View-items of the recyclerView)
        // 3. Adapter.
        mRecyclerItems = (RecyclerView) findViewById(R.id.recycler_list_items);
        mNotesLayoutMgr = new LinearLayoutManager(this);  //Notes Layout Manager.
        //Courses Layout manager-Using Grid Layout. we specify the column_span using a resource. helps in code adaptability.
        mCoursesLayoutMgr = new GridLayoutManager(this,
                getResources().getInteger(R.integer.course_grid_span));


        //List<NoteInfo> notes = DataManager.getInstance().getNotes();  //initialize notes-List from dataManager.

        //Initialize NoteRecyclerAdapter.
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);  //create the Recycler-Adapter.

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
        //mNoteRecyclerAdapter.notifyDataSetChanged();

        //Load new data from Database.
        //loadNotes();
        //using initLoader will initialize the LoaderManager once, any repeated calls will call onLoadFinished() method without executing query.
        //using LoaderManager to load notes query loadNotes(). The query should be restarted on every resume to re-fetch data.
        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null, this);

        //method to update the NavHeader, its called when user returns from SettingsScreen and every time we return to activity.
        updateNavHeader();
    }

    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();  //get db connection.

        //columns to return from database.
        final String[] noteColumns = {NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry._ID};

        String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                null, null, null, null, noteOrderBy);

        //associate cursor with RecyclerView Adapter.
        mNoteRecyclerAdapter.changeCursor(noteCursor);
    }

    private void updateNavHeader() {
        //get a NavigationView reference.
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);

        //get textViews references from HeaderView.
        TextView txtUserName = (TextView) headerView.findViewById(R.id.text_user_name);
        TextView txtEmailAddress = (TextView) headerView.findViewById(R.id.text_email_address);

        //Reading values from SharedPreference we need a reference to SharedPreference
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        //Retrieve saved values from Preferences. pref_key :- key that was used to save the preference.
        // default_value :- default value in-case the user has not set value.
        String userName = pref.getString("user_display_name", "Default-UserName");
        String emailAddress = pref.getString("user_email_address", "Default-Email");

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
    protected void onDestroy() {
        //we close DatabaseOpenHelper when activity is being destroyed.
        mDbOpenHelper.close();
        super.onDestroy();
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
                        PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social", ""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(int message_id) {
        //a snackbar needs a view from the current activity.
        View view = findViewById(R.id.recycler_list_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        //----start   ContentProvider implementation.
        CursorLoader loader = null;

        if (id == LOADER_NOTES) {
            final String[] noteColumns = {
                    //Table-qualify column-names defined/appears in both tables to specify from which table we get the column.
                    Notes._ID,  //ContentProvider will take care of Table_qualifying column name appearing in both tables.
                    Notes.COLUMN_NOTE_TITLE,
                    Notes.COLUMN_COURSE_TITLE
            };

            //specify columns to order results returned from query. Sorting by course_title followed by Note_title.
            final String noteOrderBy = Notes.COLUMN_COURSE_TITLE +
                    "," + Notes.COLUMN_NOTE_TITLE;

            loader = new CursorLoader(this, Notes.CONTENT_EXPANDED_URI, noteColumns, null, null, noteOrderBy);

        }

        //-------End ContentProvider implementation.

        //This method was used before we implemented ContentProvider.
        //Note: It's simpler to issue a query using a ContentProvider.
        //return useCreateLoader(id,args);

        return loader;
    }

    //method used before we implemented the contentProvider.
    //This method was replaced by the ContentProvider implementation.
    public CursorLoader useCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;

        if (id == LOADER_NOTES) {
            loader = new CursorLoader(this) {
                @Override
                public Cursor loadInBackground() {
                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase(); //get db connection.
                    final String[] noteColumns = {
                            //table-qualify column-names used in both tables. To specify from which table we get the column.
                            NoteInfoEntry.getQName(NoteInfoEntry._ID),
                            NoteInfoEntry.COLUMN_NOTE_TITLE,
                            //NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID),
                            CourseInfoEntry.COLUMN_COURSE_TITLE
                    };

                    //specify columns to order results returned from query. Sorting by course_title followed by Note_title.
                    final String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE +
                            "," + NoteInfoEntry.COLUMN_NOTE_TITLE;

                    //note_info JOIN course_info ON note_info.course_id = course_info.course_id
                    String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN "
                            + CourseInfoEntry.TABLE_NAME + " ON " +
                            NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                            CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

                    return db.query(tablesWithJoin, noteColumns,
                            null, null, null, null, noteOrderBy);
                }
            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //called when query has finished querying database.
        if (loader.getId() == LOADER_NOTES) {
            mNoteRecyclerAdapter.changeCursor(data);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES) {
            mNoteRecyclerAdapter.changeCursor(null); //close the cursor.
        }

    }
}
