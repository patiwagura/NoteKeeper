package com.pato.notekeeper;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.pato.notekeeper.NoteKeeperDBContract.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();  //TAG CONSTANT.
    //Qualify the constant with the package name to make it unique, to differentiate other constants used somewhere in code.
    public static final String NOTE_ID = "com.pato.notekeeper.NOTE_ID"; //constant pointing to primary_key of selected Note.
    public static final int ID_NOT_SET = -1;  //Primary-Key ID was not set.
    private NoteInfo mSelectedNote = new NoteInfo(DataManager.getInstance().getCourses().get(0), "","");
    private boolean mIsNewNote; //checks if we are creating a new Note.
    private EditText mTxtNoteTitle;
    private EditText mTxtNoteText;
    private Spinner mSpinnerCourses;
    private int mNoteId;
    private boolean mIsCancelling;  //if true user want to exit without saving changes.
    /**
     * moved to NoteActivityViewModel class.
     */
//    private String mOriginalNoteCourseId;
//    private String mOriginalNoteTitle;
//    private String mOriginalNoteText;

    //instance to ViewModel.
    private NoteActivityViewModel mViewModel;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Database Open Helper must be created Activity's onCreate and closed when activity is destroyed.
        mDbOpenHelper = new NoteKeeperOpenHelper(this);

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
        mTxtNoteTitle = (EditText) findViewById(R.id.text_note_title);
        mTxtNoteText = (EditText) findViewById(R.id.text_note_text);

        //spinner reference.
        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> lstCourses = DataManager.getInstance().getCourses(); //get courses from data_manager
        ArrayAdapter<CourseInfo> arrAdapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lstCourses);
        arrAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //link spinner with adapter.
        mSpinnerCourses.setAdapter(arrAdapterCourses);

        //method to retrieve object_instance from intent extras and display.
        readDisplayStateValues();
        //saveOriginalNoteValues();   //if it doesn't work comment if statement below.

        if (savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            //restoreOriginalNoteValues(savedInstanceState);
            restorePreviousNoteValues();
        }

        //method to display a note. Only displayNote when there is a note.
        if (!mIsNewNote) {
            //displayNote();
            loadNoteData();
        }

        Log.d(TAG, "onCreate");

    }

    private void loadNoteData() {
        //This method query's the database for a particular specified note.
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        //assuming we want to select a course called "android_intents" where the Note_title starts with "dynamic"
        String courseId = "android_intents";
        String titleStart = "dynamic";

        //create the SQL selection clause. consists of column Names and the operators only.
        //String selection = NoteInfoEntry.COLUMN_COURSE_ID + " = ? AND " + NoteInfoEntry.COLUMN_NOTE_TITLE + " LIKE ?";
        String selection = NoteInfoEntry._ID + " = ?";  //Use primary-key ID to get note from database.

        //SelectionArgs /selection-values consists of values for the column-names in the selection clause.
        //String[] selectionArgs = {courseId, titleStart + "%"};
        String[] selectionArgs = {Integer.toString(mNoteId)};

        //Specify list of columns whose values should be returned in the query (list of columns we want returned).
        String[] noteColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT
        };

        //query the database passing required parameters.
        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                selection, selectionArgs, null, null, null);

        //get column-Index from cursor. Don't hard code the column index
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        mNoteCursor.moveToNext(); //move to First record in the result.
        displayNote();
    }

    @Override
    protected void onDestroy() {
        //close database open Helper.
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void saveOriginalNoteValues() {
        //method to save original note Values, to the Model.
        if (mIsNewNote) {
            return;  //if its a new note we have nothing to save. exit.
        }
        //we are saving original note values to NoteActivityViewModel which is maintained separately from activity.
        mViewModel.mOriginalNoteCourseId = mSelectedNote.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mSelectedNote.getTitle();
        mViewModel.mOriginalNoteText = mSelectedNote.getText();
    }

    private void displayNote() {
        //get actual column values from cursor using columnIndex (Note- we don't use column-name when getting values).
        String courseId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);

        //Spinner is populated with data from List_of_courses inside DataManager.
        DataManager mdb = DataManager.getInstance();  //get instance of database manager.
        List<CourseInfo> lstCourses = mdb.getCourses();  //get list of all courses.
        CourseInfo course = mdb.getCourse(courseId);   //get course which matches specified courseId.
        int courseIndex = lstCourses.indexOf(course);  //get index of course we want spinner to select.

        //display note to view-items
        mSpinnerCourses.setSelection(courseIndex);   //select specified course using course-index
        mTxtNoteTitle.setText(noteTitle);
        mTxtNoteText.setText(noteText);
    }

    private void readDisplayStateValues() {
        Intent myIntent = getIntent(); //get intent that started this activity.
        //mSelectedNote = myIntent.getParcelableExtra(NOTE_ID);  //recreate the note that was passed as an intent Extra.
        mNoteId = myIntent.getIntExtra(NOTE_ID, ID_NOT_SET); //value-type Extras require a second_param as default value.

        //Logic: if we don't pass Note_position, default = -1, hence isNewNote = true, else isNewNote = false.
        //mIsNewNote = mSelectedNote == null;
        mIsNewNote = mNoteId == ID_NOT_SET;
        //check if we are not creating new note, retrieve the note.
        if (mIsNewNote) {
            createNewNote(); //create a newNote and set new note_position index.
        }

        Log.i(TAG, "initial mNoteId : " + mNoteId);
        //mSelectedNote = DataManager.getInstance().getNotes().get(mNoteId); //get note at position specified.

    }

    //create a new note.
    private void createNewNote() {
        DataManager dm = DataManager.getInstance(); //get instance.
        //create a new note and return its position.
        mNoteId = dm.createNewNote();
        //mSelectedNote = dm.getNotes().get(mNoteId); //retrive not from List using index_notePosition.

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
        int mnuItemId = item.getItemId(); //get id of selected menuItem.

        if (mnuItemId == R.id.action_send_mail) {
            //send email action.
            sendEmail();
            return true;
        } else if (mnuItemId == R.id.action_cancel) {
            //user wants to cancel without saving the changes.
            mIsCancelling = true;
            finish(); //signal activity to exit.
            return true;
        } else if (mnuItemId == R.id.action_next) {
            //move to the next item in the list.
            moveNext();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mnuItem = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1; //lastIndex is size - 1.
        mnuItem.setEnabled(mNoteId < lastNoteIndex); //if mNoteId is less than lastNoteIdex = true enable menuItem else disable
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();  //save currently selected-note, before we moveNext().
        ++mNoteId;  //increment note Position to move to next note.
        mSelectedNote = DataManager.getInstance().getNotes().get(mNoteId); //retrieve Note at new position.
        saveOriginalNoteValues();
        displayNote();

        //schedule a call to onPrepareOptionsMenu to enable / disable Next menuItem.
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when user exits Activity by hitting Back_button, we save changes made by user.
        if (mIsCancelling) {
            Log.i(TAG, "Cancelling note at position : " + mNoteId);
            //user want to exit without saving changes.
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(mNoteId);//remove newNote_position if user cancels in the process of creating a new note.
            } else {
                //restore Previous note values ie original note before change.
                restorePreviousNoteValues();
            }
        } else {
            saveNote(); //save any changes.
        }
        Log.d(TAG, "onPause");

    }


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
        String emBody = "Checkout what I learned in the pluralsight course \"" +
                eCourse.getTitle() + "\" \n" + mTxtNoteText.getText().toString();

        Intent emailIntent = new Intent(Intent.ACTION_SEND); //create Intent, we provide action in Intent constructor.
        emailIntent.setType("message/rfc2822"); //a standard internet mime-type for sending email.
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emSubject); //subject of email. as intent Extra
        emailIntent.putExtra(Intent.EXTRA_TEXT, emBody); //body of the email. as intent Extra
        startActivity(emailIntent);   //startActivity with this implicit intent.
    }
}
