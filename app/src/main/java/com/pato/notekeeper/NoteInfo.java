package com.pato.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim.
 */

//implementing Parcelable interface to make our class parcelable.

public final class NoteInfo implements Parcelable {
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;

    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    private NoteInfo(Parcel parcelSource) {
        //this constructor should be private to prevent access outside this class.
        //read the data from parcel in the order they were written.
        mCourse = parcelSource.readParcelable(CourseInfo.class.getClassLoader()); //CLASS Loader provides information to create instance of a type.
        mTitle = parcelSource.readString(); //read title from parcel.
        mText = parcelSource.readString(); //read text from parcel.
    }

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcelDest, int flags) {
        //handles the details of writing the content to parcel.

        parcelDest.writeParcelable(mCourse, 0); //write a parcelable reference.
        //write primitive types e.g string
        parcelDest.writeString(mTitle); //write note_title.
        parcelDest.writeString(mText);  //write note_text

    }

    //code to recreate this class_instance<NoteInfo> from the parcel.
    //Note: Read parcel values in the same order they were written.
    public static final Parcelable.Creator<NoteInfo> CREATOR = new Parcelable.Creator<NoteInfo>(){

        @Override
        public NoteInfo createFromParcel(Parcel parcelSource) {
            //common practice is to use a private constructor re-create the NoteInfo_instance and set member variables.
            return new NoteInfo(parcelSource);
        }

        @Override
        public NoteInfo[] newArray(int size) {
            //newArray(int size) creates an array of type <NoteInfo> of the specified size.
            return new NoteInfo[size];
        }
    };
}
