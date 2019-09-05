package com.pato.notekeeper;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

//We don't need className.method if we static import class.
import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    static DataManager sDataMgr; //static reference to dataManager class.

    @BeforeClass
    public static void classSetUp() throws Exception {
        //initialize the static dataManager reference.
        sDataMgr = DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<NoteListActivity> mNoteListActivityRule = new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote() {
        //Method to test create new note.
        final CourseInfo course = sDataMgr.getCourse("java_lang");  //select a course.
        final String noteTitle = "Test note title";
        final String noteText = "This is the body of our test note";

        onView(withId(R.id.fab)).perform(click());  //click our fab-button to create new Note, NoteActivity is displayed.
        //Note we have to click on spinner, then make the selection.
        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(course))).perform(click());  //Selected course should match a Type-of:courseInfo and actual-value=selected_course.
        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(containsString(course.getTitle())))); //verify UI behavior, ensure spinner displays selected courseTitle.

        //we can perform an action and immediately check without having to repeat the onView to get the ViewInteraction.
        // Perform just like onView returns a ViewInteraction.
        onView(withId(R.id.text_note_title)).perform(typeText(noteTitle))
                .check(matches(withText(containsString(noteTitle))));
        //Note: perform method takes variable-length/more than one argument list
        onView(withId(R.id.text_note_text)).perform(typeText(noteText),
                closeSoftKeyboard());
        //checking if the tests met expectaction.
        onView(withId(R.id.text_note_text)).check(matches(withText(containsString(noteText)))); //text-property contains our string.

        pressBack();  //PressBack to Leave Activity when we complete test.

        //android saves changes when back-button is pressed.
        //We have to verify Logic to make sure the application did the right thing.
        int noteIndex = sDataMgr.getNotes().size() -1;  //The last saved note is on last-index on list.
        NoteInfo note = sDataMgr.getNotes().get(noteIndex);
        assertEquals(course, note.getCourse());
        assertEquals(noteTitle, note.getTitle());
        assertEquals(noteText, note.getText());
    }

}