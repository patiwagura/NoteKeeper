package com.pato.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    //Qualify the constant with the package name to make it unique, to differentiate other constants used somewhere in code.
    public static final String NOTE_POSITION = "com.pato.notekeeper.NOTE_POSITION"; //constant pointing to the position of the selected Note.
    public static final int POSITION_NOT_SET = -1;  //position was not set.
    private NoteInfo mSelectedNote;
    private boolean mIsNewNote; //checks if we are creating a new Note.
    private EditText mTxtNoteTitle;
    private EditText mTxtNoteText;
    private Spinner mSpinnerCourses;
    private int mNotePosition;
    private boolean mIsCancelling;  //if true user want to exit without saving changes.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get reference to EditText
        mTxtNoteTitle = findViewById(R.id.text_note_title);
        mTxtNoteText = findViewById(R.id.text_note_text);

        //spinner reference.
        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> lstCourses = DataManager.getInstance().getCourses(); //get courses from data_manager
        ArrayAdapter<CourseInfo> arrAdapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lstCourses);
        arrAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //link spinner with adapter.
        mSpinnerCourses.setAdapter(arrAdapterCourses);

        //method to retrieve object_instance from intent extras and display.
        readDisplayStateValues();

        //method to display a note. Only displayNote when there is a note.
        if (!mIsNewNote)
            displayNote(mSpinnerCourses, mTxtNoteTitle, mTxtNoteText);

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
        //mSelectedNote = myIntent.getParcelableExtra(NOTE_POSITION);  //recreate the note that was passed as an intent Extra.
        int notePosition = myIntent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET); //value-type Extras require a second_param as default value.

        //Logic: if we don't pass an intent Extra mSelectedNote is null hence isNewNote = true, else isNewNote = false.
        //mIsNewNote = mSelectedNote == null;
        mIsNewNote = notePosition == POSITION_NOT_SET;
        //check if we are not creating new note, retrieve the note.
        if (mIsNewNote) {
            createNewNote(); //handle create new note.
        } else {
            mSelectedNote = DataManager.getInstance().getNotes().get(notePosition); //get note at position specified.
        }

    }

    //create a new note.
    private void createNewNote() {
        DataManager dm = DataManager.getInstance(); //get instance.
        //create a new note and return its position.
        mNotePosition = dm.createNewNote();
        mSelectedNote = dm.getNotes().get(mNotePosition); //retrive not from List using index_notePosition.

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate our menu
        MenuInflater mnuInflater = getMenuInflater();
        mnuInflater.inflate(R.menu.menu_note, menu); //inflate menu.
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // called when we select a menuItem.
        int id = item.getItemId(); //get id of selected menuItem.

        if (id == R.id.action_send_mail) {
            //send email action.
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            //user wants to cancel without saving the changes.
            mIsCancelling = true;
            finish(); //signal activity to exit.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when user exits Activity by hitting Back_button, we save changes made by user.
        if (mIsCancelling) {
            //user want to exit without saving changes.
            if(mIsNewNote)
                DataManager.getInstance().removeNote(mNotePosition);//remove newNote_position if user cancels in the process of creating a new note.
        } else {
            saveNote(); //save any changes.
        }

    }

    private void saveNote() {
        //save details of current selected note from our app.
        mSelectedNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());  //currently selected course on our spinner.
        mSelectedNote.setTitle(mTxtNoteTitle.getText().toString());
        mSelectedNote.setText(mTxtNoteText.getText().toString());
    }

    private void sendEmail() {
        //the email should have a title/subject & body /message.
        CourseInfo eCourse = (CourseInfo) mSpinnerCourses.getSelectedItem(); //get selected course.
        String emSubject = mTxtNoteTitle.getText().toString();  //get Title of the course.
        String emBody = "Checkout what I learned in the pluralsight course \"" + eCourse.getTitle() + "\" \n" + mTxtNoteText.getText().toString();

        Intent emailIntent = new Intent(Intent.ACTION_SEND); //create Intent, we provide action in Intent constructor.
        emailIntent.setType("message/rfc2822"); //a standard internet mime-type for sending email.
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emSubject); //subject of email. as intent Extra
        emailIntent.putExtra(Intent.EXTRA_TEXT, emBody); //body of the email. as intent Extra
        startActivity(emailIntent);   //startActivity with this implicit intent.
    }
}
