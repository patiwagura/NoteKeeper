package com.pato.notekeeper;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final List<NoteInfo> mNotes;
    private final LayoutInflater mLayoutInflater;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        mContext = context;
        //create LayoutInflater from context.
        mLayoutInflater = LayoutInflater.from(context);
        mNotes = notes;
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
        //// bind data to view-items. This method is called by RecyclerView to request for data to display at the specified position.
        NoteInfo note = mNotes.get(position);  //get note at position.
        viewHolder.mTextCourse.setText(note.getCourse().getTitle()); //set courseTitle.
        viewHolder.mTextTitle.setText(note.getTitle());
        viewHolder.mPkId = note.getId();  //get unique-ID associated with a database record.

    }

    @Override
    public int getItemCount() {
        //return total number of items to be displayed.
        return mNotes.size();
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
