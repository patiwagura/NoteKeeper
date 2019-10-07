package com.pato.notekeeper;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pato.notekeeper.NoteKeeperDBContract.CourseInfoEntry;
import com.pato.notekeeper.NoteKeeperDBContract.NoteInfoEntry;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    //private final List<NoteInfo> mNotes;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mCoursePos;
    private int mIdPos;
    private int mNoteTitlePos;


    //public NoteRecyclerAdapter(Context context, List<NoteInfo> notes)  //Old-Constructor
    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        //mNotes = notes;
        mCursor = cursor;
        //create LayoutInflater from context.
        mLayoutInflater = LayoutInflater.from(context);

        //get Index positions of the columns to retrieve from database.
        populateColumnPositions();

    }

    private void populateColumnPositions() {
        if (mCursor == null)
            return;

        //Get column indexes from mCursor.
        mCoursePos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor) {
        //close existing cursor
        if (mCursor != null)
            mCursor.close();

        //re-assign new cursor
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //This method will inflate or create a view instance and store information about the created view in a ViewHolder.
        //We inflate a view-Item, create a new ViewHolder instance and associate the view-item with the ViewHolder.
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, viewGroup, false);  //create a viewItem
        return new ViewHolder(itemView);  //create the ViewHolder.
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //Bind data to view-items. This method is called by RecyclerView to request for data at the specified position.

        if (!mCursor.isClosed()) {
            //move cursor to required position.
            mCursor.moveToPosition(position);

            //get actual values to display.
            String course = mCursor.getString(mCoursePos);
            String noteTitle = mCursor.getString(mNoteTitlePos);
            int id = mCursor.getInt(mIdPos);


            //display data to the user.
            viewHolder.mTextCourse.setText(course);
            viewHolder.mTextTitle.setText(noteTitle);
            viewHolder.mPkId = id;
        }


        //
        //NoteInfo note = mNotes.get(position);  //get note at position.
        //viewHolder.mTextCourse.setText(note.getCourse().getTitle()); //set courseTitle.
        //viewHolder.mTextTitle.setText(note.getTitle());
        //viewHolder.mPkId = note.getId();  //get unique-ID associated with a database record.

    }

    @Override
    public int getItemCount() {
        //return total number of items to be displayed.
        //return mNotes.size();

        //if cursor is null return zero else return number of items.
        return mCursor == null ? 0 : mCursor.getCount();
    }

    //a nested ViewHolder class to hold references for the viewItems.
    public class ViewHolder extends RecyclerView.ViewHolder {

        //make the fields public to be accessed by outer-class.
        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mPkId;  //unique id e.g position / index set each time a view-item is associated with a viewHolder.

        //constructor matching super.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            //set an itemClick Listener.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(mContext, NoteActivity.class);
                    myIntent.putExtra(NoteActivity.NOTE_ID, mPkId);  //current position of selected data-item.
                    mContext.startActivity(myIntent);
                }
            });
        }
    }
}
