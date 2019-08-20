package com.pato.notekeeper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //spinner reference.
        Spinner spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> lstCourses = DataManager.getInstance().getCourses(); //get courses from data_manager
        ArrayAdapter<CourseInfo> arrAdapterCourses = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,lstCourses);
        arrAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //link spinner with adapter.
        spinnerCourses.setAdapter(arrAdapterCourses);
    }

}
