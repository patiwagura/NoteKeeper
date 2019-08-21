package com.pato.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    //Qualify the constant with the package name to make it unique, to differentiate other constants used somewhere in code.
    public static final String NOTE_INFO ="com.pato.notekeeper.NOTE_INFO";
    private NoteInfo mSelectedNote;
    private boolean mIsNewNote; //checks if we are creating a new Note.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get reference to EditText
        EditText txtNoteTitle = findViewById(R.id.text_note_title);
        EditText txtNoteText = findViewById(R.id.text_note_text);

        //spinner reference.
        Spinner spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> lstCourses = DataManager.getInstance().getCourses(); //get courses from data_manager
        ArrayAdapter<CourseInfo> arrAdapterCourses = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,lstCourses);
        arrAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //link spinner with adapter.
        spinnerCourses.setAdapter(arrAdapterCourses);

        //method to retrieve object_instance from intent extras and display.
        readDisplayStateValues();

        //method to display a note. Only displayNote when there is a note.
        if(!mIsNewNote)
        displayNote(spinnerCourses,txtNoteTitle, txtNoteText);

    }

    private void displayNote(Spinner spinnerCourses, EditText txtNoteTitle, EditText txtNoteText) {
        List<CourseInfo> lstCourses = DataManager.getInstance().getCourses();  //get list of courses.
        int courseIndex = lstCourses.indexOf(mSelectedNote.getCourse());  //get index of the course from our selected note.

        //select the item using given index
        spinnerCourses.setSelection(courseIndex);

        txtNoteTitle.setText(mSelectedNote.getTitle());
        txtNoteText.setText(mSelectedNote.getText());
    }

    private void readDisplayStateValues() {
        Intent myIntent = getIntent(); //get intent that started this activity.
        mSelectedNote = myIntent.getParcelableExtra(NOTE_INFO);

        //if we don't pass an intent Extra mSelectedNote is null hence isNewNote = true, else isNewNote = false.
        mIsNewNote = mSelectedNote == null;
    }

}
