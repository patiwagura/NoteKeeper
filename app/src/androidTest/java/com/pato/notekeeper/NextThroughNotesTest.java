package com.pato.notekeeper;

import org.junit.Rule;
import org.junit.Test;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import static org.junit.Assert.*;


public class NextThroughNotesTest {
    //This test opens NavigationDrawer, selects Notes option to display notes on recyclerView, then clicks a Note item which is displayed on NoteActivity.
    //we move next through the notes.
    //First thing is to create ActivityTestRule. This Activity will be started before our test is started. manages Activity lifeCycle.
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule(MainActivity.class);

    //Testing method.
    @Test
    public void NextThroughNotes() {
        //Testing method to verify we can select a Note from mainActivity, and then move Next through the notes.
        //We have to open Drawer ,select option to display notes, then from mainActivity select a note.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());  //open NavigationDrawer.
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));  //Select notes options on the NavigationView.
        //select and click the first item from RecyclerView. This will open NoteActivity.
        onView(withId(R.id.recycler_list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();  //get allNotes from DataManager.
        //Loop through all the notes.
        for (int index = 0; index < notes.size(); index++) {
            NoteInfo note = notes.get(index);  //retrieve noteInfo at index;

            //check spinner_courses, Note_title and note_text to make sure they contain values for the selected note.
            onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(note.getCourse().getTitle())));
            onView(withId(R.id.text_note_title)).check(matches(withText(note.getTitle())));
            onView(withId(R.id.text_note_text)).check(matches(withText(note.getText())));

            //We only click next when button is not disabled. At end of list Next is disabled.
            if (index < notes.size() - 1)
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());
        }
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));  //check to ensure next was disabled when we got to end of list.
        pressBack(); //go back to startingActivity.
    }


}