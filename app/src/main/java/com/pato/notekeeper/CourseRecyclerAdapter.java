package com.pato.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final List<CourseInfo> mCourses;
    private final LayoutInflater mLayoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        //create LayoutInflater from context.
        mLayoutInflater = LayoutInflater.from(context);
        mCourses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //This method will inflate or create a view instance and store information about the created view in a ViewHolder.
        //We inflate a view-Item, create a new ViewHolder instance and associate the view-item with the ViewHolder.
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list, viewGroup, false);  //create a viewItem
        return new ViewHolder(itemView);  //create the ViewHolder.
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //// bind data to view-items. This method is called by RecyclerView to request for data to display at the specified position.
        CourseInfo course = mCourses.get(position);  //get course at position.
        viewHolder.mTextCourse.setText(course.getTitle()); //set courseTitle.
        viewHolder.mCurrentPosition = position; //setting position of data-item in the list.

    }

    @Override
    public int getItemCount() {
        //return total number of items to be displayed.
        return mCourses.size();
    }

    //a nested ViewHolder class to hold references for the viewItems.
    public class ViewHolder extends RecyclerView.ViewHolder {

        //make the fields public to be accessed by outer-class.
        public final TextView mTextCourse;
        public int mCurrentPosition;  //set each time a view-item is associated with a viewHolder.

        //constructor matching super.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);

            //set an itemClick Listener.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent myIntent = new Intent(mContext, NoteActivity.class);
//                    myIntent.putExtra(NoteActivity.NOTE_POSITION, mCurrentPosition);  //current position of selected data-item.
//                    mContext.startActivity(myIntent);

                    Snackbar.make(v, mCourses.get(mCurrentPosition).getTitle(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
