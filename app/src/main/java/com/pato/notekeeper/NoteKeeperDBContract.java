package com.pato.notekeeper;

import android.provider.BaseColumns;

//Db Contract class
public final class NoteKeeperDBContract {
    //Note: In SQLite we don't have to specify explicit Column Data-Types e.g CREATE TABLE person (AGE INT, phone);

    //Make class non-creatable. This class should not be instantiated, Use a private constructor.
    private NoteKeeperDBContract() {
    }

    //Nested class to create course_info table.
    public static final class CourseInfoEntry implements BaseColumns {
        //constants
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        //CREATE INDEX table_index_name1 ON table_name (column_1)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_COURSE_TITLE + ")";

        public static final String getQName(String columnName) {
            //return a table-qualified columnName e.g course_info.course_id.
            return TABLE_NAME + "." + columnName;
        }

        //CREATE TABLE course_info (course_id, course_title);
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL, " +
                COLUMN_COURSE_TITLE + " TEXT NOT NULL)";

    }

    //Nested class to create table NoteInfo.
    public static final class NoteInfoEntry implements BaseColumns {
        //constants
        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        //CREATE INDEX table_index_name1 ON table_name (column_1)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_NOTE_TITLE + ")";

        public static final String getQName(String columnName) {
            //return a table-qualified columnName e.g table-name.course_id
            return TABLE_NAME + "." + columnName;
        }

        //TABLE Create constant.
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                COLUMN_NOTE_TEXT + " TEXT, " +
                COLUMN_COURSE_ID + " TEXT NOT NULL)";

    }


}