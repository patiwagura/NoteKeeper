package com.pato.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

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
       final ListView listNotes = findViewById(R.id.list_notes); //listView reference.
        List<NoteInfo> notes = DataManager.getInstance().getNotes();  //get notes.
        //create an adapter.
        ArrayAdapter<NoteInfo> adapterNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listNotes.setAdapter(adapterNotes);

        //add onitemclick listener to allow users to make a selection.
        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //show the item selected from the ListView.
                Intent mIntent = new Intent(NoteListActivity.this, NoteActivity.class);
                NoteInfo note = (NoteInfo) listNotes.getItemAtPosition(position); //get the item selected from ListView.
                mIntent.putExtra(NoteActivity.NOTE_INFO, note);  //passing NoteInfo reference as an intent extra
                startActivity(mIntent);

            }
        });

    }

}
