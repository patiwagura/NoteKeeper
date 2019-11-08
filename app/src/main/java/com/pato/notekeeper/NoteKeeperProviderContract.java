package com.pato.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteKeeperProviderContract {

    //CONSTANTS.
    public static final String AUTHORITY = "com.pato.notekeeper.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    //private constructor, don't initialize this class.
    private NoteKeeperProviderContract() {
        //we can access constants using   classname.ConstantName e.g Notes.COLUMN_COURSE_ID

    }

    protected interface NotesColumns {
        //groups columns for the Notes _table.
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        //public static final String COLUMN_COURSE_ID = "course_id";  //duplicate column-CONSTANT  moved to interface_CoursesIdColumns
    }

    protected interface CoursesColumns {
        //groups columns for the course table.
        //public static final String COLUMN_COURSE_ID = "course_id";  //duplicate column moved to interface_CoursesIdColumns
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface CoursesIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    //final inner class for courses
    public static final class Courses implements BaseColumns, CoursesColumns, CoursesIdColumns {
        public static final String PATH = "courses";

        //content://com.pato.notekeeper.provider/courses  => Table URI for courses table.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

    }

    //NOTES table.
    public static final class Notes implements BaseColumns, NotesColumns, CoursesIdColumns, CoursesColumns {

        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

        //constants for the uri that will join Notes_info and Course_info tables.
        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }

}
