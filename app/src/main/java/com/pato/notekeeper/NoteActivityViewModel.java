package com.pato.notekeeper;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;

public class NoteActivityViewModel extends ViewModel {
    //CONSTANTS qualified by package_name to make them unique. used as names into our stored bundle.
    public static final String ORIGINAL_NOTE_COURSEID = "com.pato.notekeeper.ORIGINAL_NOTE_COURSEID";
    public static final String ORIGINAL_NOTE_TITLE = "com.pato.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.pato.notekeeper.ORIGINAL_NOTE_TEXT";

    //instance state variables used to restore activity state once its destroyed.
    public String mOriginalNoteCourseId;
    public String mOriginalNoteTitle;
    public String mOriginalNoteText;
    public boolean mIsNewlyCreated = true;  //indicates that instance is newly created. its set to true when instance is created.

    public void saveState(Bundle outState) {
        //method to save the activity state. this method is called by onSaveInstanceState() method.
        outState.putString(ORIGINAL_NOTE_COURSEID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    public void restoreState(Bundle inState){
        //when restoring our activity state, a bundle is passed to onCreate() method.
        mOriginalNoteCourseId = inState.getString(ORIGINAL_NOTE_COURSEID);
        mOriginalNoteTitle = inState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = inState.getString(ORIGINAL_NOTE_TEXT);
    }
}
