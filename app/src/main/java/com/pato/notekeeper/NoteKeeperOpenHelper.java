package com.pato.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pato.notekeeper.NoteKeeperDBContract.CourseInfoEntry;
import com.pato.notekeeper.NoteKeeperDBContract.NoteInfoEntry;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
    //constants
    public static final String DATABASE_NAME = "NoteKeeper.db";
    public static final int DATABASE_VERSION = 2;  //DataBase Version_1 contained only tables, We created indexes in Version_2.

    //constructor.
    public NoteKeeperOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create tables in the database.
        db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteInfoEntry.SQL_CREATE_TABLE);

        //create indexes to the tables. Database uses a Data-Structure for the indexes which helps to easily and effectively search records.
        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);

        //Insert data to CourseInfo and NoteInfo tables.
        DatabaseDataWorker dbWorker = new DatabaseDataWorker(db);
        dbWorker.insertCourses();
        dbWorker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This method is called to upgrade the database from oldVersion to newVersion.
        // OldVersion we had tables created, we are adding indexes to newVersion (2).
        if (oldVersion < 2) {
            db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);
        }

    }
}

