package com.pato.notekeeper;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
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
    /**
     * moved to NoteActivityViewModel class.
     */
//    private String mOriginalNoteCourseId;
//    private String mOriginalNoteTitle;
//    private String mOriginalNoteText;

    //instance to ViewModel.
    private NoteActivityViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ViewModel is managed by ViewModelProvider.
        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        mViewModel = viewModelProvider.get(NoteActivityViewModel.class); //class extending ViewModel class.

        //method used to restore saved activity_instance state.
        if (mViewModel.mIsNewlyCreated && savedInstanceState != null) {
            //For device configuration change e.g portrait to landscape use ViewModel to restore and save state.
            //we only need to restore state from Bundle:savedInstanceState, when activity is destroyed along with the ViewModel.
            mViewModel.restoreState(savedInstanceState);
        }

        mViewModel.mIsNewlyCreated = false;

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
        saveOriginalNoteValues();

        //method to display a note. Only displayNote when there is a note.
        if (!mIsNewNote)
            displayNote(mSpinnerCourses, mTxtNoteTitle, mTxtNoteText);

    }

    private void saveOriginalNoteValues() {
        //method to save original note Values.
        if (mIsNewNote) {
            return;  //if its a new note we have nothing to save. exit.
        }
        //we are saving original note values to NoteActivityViewModel which is maintained separately from activity.
        mViewModel.mOriginalNoteCourseId = mSelectedNote.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mSelectedNote.getTitle();
        mViewModel.mOriginalNoteText = mSelectedNote.getText();
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
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);//remove newNote_position if user cancels in the process of creating a new note.
            } else {
                //restore Previous note values ie original note before change.
                restorePreviousNoteValues();
            }
        } else {
            saveNote(); //save any changes.
        }

    }

    /**
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            //save instance state if the bundle is not null.
            mViewModel.saveState(outState);
        }
    }

    private void restorePreviousNoteValues() {
        //method to restore original note values when user cancels a note before saving the note.
        CourseInfo prevCourse = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mSelectedNote.setCourse(prevCourse); //restore previous course.
        mSelectedNote.setTitle(mViewModel.mOriginalNoteTitle); //original note Title.
        mSelectedNote.setText(mViewModel.mOriginalNoteText);  //original note text.
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
