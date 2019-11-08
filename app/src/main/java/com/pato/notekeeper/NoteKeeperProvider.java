package com.pato.notekeeper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.pato.notekeeper.NoteKeeperDBContract.CourseInfoEntry;
import com.pato.notekeeper.NoteKeeperDBContract.NoteInfoEntry;
import com.pato.notekeeper.NoteKeeperProviderContract.Courses;
import com.pato.notekeeper.NoteKeeperProviderContract.CoursesIdColumns;
import com.pato.notekeeper.NoteKeeperProviderContract.Notes;

public class NoteKeeperProvider extends ContentProvider {

    private NoteKeeperOpenHelper mDbOpenHelper;

    //return No_MATCH if we try to match a uri without (AUTHORITY or PATH)
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES = 0;  //constant to identify the uri for courses_table.
    public static final int NOTES = 1;  //Constant to identify the uri for notes_table.
    public static final int NOTES_EXPANDED = 2;   //Constant to identify the uri for the joined tables (note_info & course_info).

    //Add valid uri to a static initializer.
    //static initializer, allows to run some code when a type is initially loaded.
    static {
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Courses.PATH, COURSES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH, NOTES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
    }

    private String TAG_PROVIDER = getClass().getSimpleName();

    //constructor.
    public NoteKeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;  //indicate whether the ContentProvider was created successfully. true=success, false=failure
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //projection :- list of columns to get from database.
        //Delegate the work of a contentProvider_query to SQLite e.g SQLite has capability to filter and sort queries.
        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase(); //Inorder to query the DB we need a Database Reference.

        //Using uri, we can determine what TABLE we need to query. e.g course/Notes table.
        int uriMatch = sUriMatcher.match(uri);  //return an integer matching the specified uri 0=> courses, 1=>Notes constants define above.
        switch (uriMatch) {
            case COURSES:
                //Uri matched the courses Uri, query the courses table.
                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case NOTES:
                //Uri matched the Notes Uri, query the Notes table.
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTES_EXPANDED:
                //Using ContentProvider to abstract database storage details e.g Create a join_query to return notes with corresponding course_titles for a note.
                cursor = notesExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
                break;

        }


        Log.d(TAG_PROVIDER, "Provider-Query: -----------");

        return cursor;
    }

    /**
     * NotesExpandedQuery method perform a join on note_info and course_info tables.
     *
     * @param db            - reference to database.
     * @param projection    - list of column names to return.
     * @param selection     - table_column name used to filter the results.
     * @param selectionArgs - an array of list of column values to filter results with.
     * @param sortOrder     - how to sort the query results usually by column name in descending order.
     */
    private Cursor notesExpandedQuery(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Table-qualify columns defined/appearing in both tables(note_info & course_info).
        String[] colsQualified = new String[projection.length];  //Array of columns to return in query passed as projection = array of columns.
        for (int idx = 0; idx < projection.length; idx++) {
            //check if the column name = _ID from baseColumns or column name = course_id and table-qualify the columns else don't table-qualify.
            //Note:- we have to table-qualify column-names that are defined in more than one tables. e.g note_info and course_info have a column called course_id.
            colsQualified[idx] = projection[idx].equals(BaseColumns._ID) ||
                    projection[idx].equals(CoursesIdColumns.COLUMN_COURSE_ID) ?
                    NoteInfoEntry.getQName(projection[idx]) : projection[idx];
        }

        //Example of SQL_Join: SELECT col1, col2 FROM Table_1 JOIN Table_2 ON Table_1.id = Table_2.id
        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " +
                CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        //Log the query.
        Log.d(TAG_PROVIDER, "Join Query : " + tablesWithJoin);

        return db.query(tablesWithJoin, colsQualified, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
