package com.pato.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
    //constants
    public static final String DATABASE_NAME = "NoteKeeper.db";
    public static final int DATABASE_VERSION = 1;

    //constructor.
    public NoteKeeperOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(NoteKeeperDBContract.CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteKeeperDBContract.NoteInfoEntry.SQL_CREATE_TABLE);

        //Insert data to CourseInfo and NoteInfo tables.
        DatabaseDataWorker dbWorker = new DatabaseDataWorker(db);
        dbWorker.insertCourses();
        dbWorker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}

