package com.pato.notekeeper;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jim.
 */

public final class CourseInfo implements Parcelable {
    private String mCourseId;
    private String mTitle;
    private List<ModuleInfo> mModules;

    //constructor.
    public CourseInfo(String courseId, String title, List<ModuleInfo> modules) {
        mCourseId = courseId;
        mTitle = title;
        mModules = modules;
    }

    private CourseInfo(Parcel parcelSource) {
        //this constructor should be private to prevent access outside this class.
        //read the data from parcel in the order they were written.
        //mModules = parcelSource.readParcelable(ModuleInfo.class.getClassLoader()); //CLASS Loader provides information to create instance of a type.
        parcelSource.readList(mModules, ModuleInfo.class.getClassLoader());  //read List : OPTION 1
        //parcelSource.readTypedList(mModules, ModuleInfo.CREATOR);  //read List :OPTION 2.
        //parcelSource.readList(mModules, List.class.getClassLoader()); //Read List : OPTION 3.
        mTitle = parcelSource.readString(); //read title from parcel.
        mCourseId = parcelSource.readString(); //read text from parcel.
    }

    public String getCourseId() {
        return mCourseId;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<ModuleInfo> getModules() {
        return mModules;
    }

    public boolean[] getModulesCompletionStatus() {
        boolean[] status = new boolean[mModules.size()];

        for (int i = 0; i < mModules.size(); i++)
            status[i] = mModules.get(i).isComplete();

        return status;
    }

    public void setModulesCompletionStatus(boolean[] status) {
        for (int i = 0; i < mModules.size(); i++)
            mModules.get(i).setComplete(status[i]);
    }

    public ModuleInfo getModule(String moduleId) {
        for (ModuleInfo moduleInfo : mModules) {
            if (moduleId.equals(moduleInfo.getModuleId()))
                return moduleInfo;
        }
        return null;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseInfo that = (CourseInfo) o;

        return mCourseId.equals(that.mCourseId);

    }

    @Override
    public int hashCode() {
        return mCourseId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcelDest, int flags) {
        //handles the details of writing the content/data to parcel.
        //parcelDest.writeParcelable(mModules, 0); //write a parcelable reference.
        parcelDest.writeList(mModules);         //write a List to parcel :OPTION 1.
        //parcelDest.writeTypedList(mModules);  //write a List to parcel :OPTION 2.
        //write primitive types e.g string
        parcelDest.writeString(mTitle); //write note_title.
        parcelDest.writeString(mCourseId);  //write note_text

    }

    //code to recreate this class_instance from the parcel.
    //Note: Read parcel values in the same order they were written.
    public static final Parcelable.Creator<CourseInfo> CREATOR = new Parcelable.Creator<CourseInfo>() {

        @Override
        public CourseInfo createFromParcel(Parcel parcelSource) {
            //common practice is to use a private constructor re-create the instance and set member variables.
            return new CourseInfo(parcelSource);
        }

        @Override
        public CourseInfo[] newArray(int size) {
            //newArray(int size) creates an array of type <NoteInfo> of the specified size.
            return new CourseInfo[size];
        }
    };
}
