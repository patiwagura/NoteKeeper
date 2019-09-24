package com.pato.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;

    //private ArrayAdapter<NoteInfo> mAdapterNotes;  //ArrayAdapter used with ListView.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new note
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        //method to initialize notes to a listView.
        initializeDisplayContent();
    }

    private void initializeDisplayContent() {
        //Commented this code as it was used with ListView. Recycler View is different.
       /*
       final ListView listNotes = findViewById(R.id.list_notes); //listView reference.
        List<NoteInfo> notes = DataManager.getInstance().getNotes();  //get notes.
        //create an adapter.
        mAdapterNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listNotes.setAdapter(mAdapterNotes);

        //add onitemclick listener to allow users to make a selection.
        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //show the item selected from the ListView.
                Intent mIntent = new Intent(NoteListActivity.this, NoteActivity.class);
                //NoteInfo note = (NoteInfo) listNotes.getItemAtPosition(position); //get the item selected from ListView.
                //mIntent.putExtra(NoteActivity.NOTE_POSITION, note);  //passing NoteInfo reference as an intent extra

                //Logic changed. Earlier we created intent, retrieved our note at selected_position, then passed the reference of Note_object as an intent Extra.
                mIntent.putExtra(NoteActivity.NOTE_POSITION, position);  //pass position of selected item and let DataManager retrieve our Note.
                startActivity(mIntent);

            }
        });
        */

        //Components we need for RecyclerView implementation / Initialization.
        // 1. LayoutManager
        // 2. Design Item View Layout (used to style individual View-items of the recyclerView)
        // 3. Adapter.
        final RecyclerView recyclerNotes = (RecyclerView)findViewById(R.id.recycler_list_notes);
        final LinearLayoutManager notesLayoutMgr = new LinearLayoutManager(this); //create a Layout Manager.
        recyclerNotes.setLayoutManager(notesLayoutMgr);

        //adapter
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //notify arrayAdapter that data set has changed.
        //mAdapterNotes.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //notify arrayAdapter that data set has changed.
        //mAdapterNotes.notifyDataSetChanged();
        //refresh our data every time we resume activity.
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }
}
