package com.pato.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    static DataManager sDataMgr;

    @BeforeClass
    public static void classSetUp() throws Exception{
        //this method runs once before all tests.
        sDataMgr = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        //method runs before each test in this class.
        sDataMgr.getNotes().clear(); //clear all items in the list.
        sDataMgr.initializeExampleNotes();  //initialize with example notes.

    }

    @Test
    public void createNewNote() throws Exception {
        //steps for creating a newNote.
        //final DataManager dm = DataManager.getInstance(); //get instance of data-manager.
        final CourseInfo course = sDataMgr.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body text of my test note";

        int noteIndex = sDataMgr.createNewNote(); //create a new note and get index of its position.
        NoteInfo newNote = sDataMgr.getNotes().get(noteIndex); //retrieve note at index specified.
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        //when we create the newNote and set the values, we expect that if we retrieve the note at that
        //index it will contain the values we set.
        NoteInfo compareNote = sDataMgr.getNotes().get(noteIndex); //get note at index.
        //check if two references point to same object. This wouldn't be of much help to test for functionality in our app.
        //assertSame(compareNote, newNote);

        // we want to test for actual values stored in our note.
        assertEquals(course, compareNote.getCourse());
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(noteText, compareNote.getText());

    }

    @Test
    public void findSimilarNotes() throws Exception {
        //we will create two notes with similar course & title but with different text-body.
        //use findNote to check if we find the correct note.
        //sDataMgr = DataManager.getInstance();
        final CourseInfo course = sDataMgr.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText1 = "This is the body text of my test note";
        final String noteText2 = "This is the body text of my second test note";

        //create First note.
        int noteIndex1 = sDataMgr.createNewNote();
        NoteInfo newNote1 = sDataMgr.getNotes().get(noteIndex1);
        newNote1.setCourse(course);
        newNote1.setTitle(noteTitle);
        newNote1.setText(noteText1);

        //create Second Note.
        int noteIndex2 = sDataMgr.createNewNote();
        NoteInfo newNote2 = sDataMgr.getNotes().get(noteIndex2);
        newNote2.setCourse(course);
        newNote2.setTitle(noteTitle);
        newNote2.setText(noteText2);

        //compare index for the created note.
        int foundIndex1 = sDataMgr.findNote(newNote1);
        assertEquals(noteIndex1, foundIndex1);

        int foundIndex2 = sDataMgr.findNote(newNote2);
        assertEquals(noteIndex2, foundIndex2);

    }

    @Test
    public void createNewNoteOneStep(){
        //create a new note as a one-step.
        final CourseInfo course = sDataMgr.getCourse("android_async");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body of my test note";

        //our specification is to create an overloaded method that creates the note and populates the data.
        int noteIndex = sDataMgr.createNewNote(course, noteTitle, noteText);

        NoteInfo compareNote = sDataMgr.getNotes().get(noteIndex);
        assertEquals(course, compareNote.getCourse());
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(noteText, compareNote.getText());
    }
}