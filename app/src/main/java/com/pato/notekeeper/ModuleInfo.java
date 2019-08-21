package com.pato.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim.
 */

public final class ModuleInfo implements Parcelable {
    private final String mModuleId;
    private final String mTitle;
    private boolean mIsComplete = false;

    public ModuleInfo(String moduleId, String title) {
        this(moduleId, title, false);
    }

    public ModuleInfo(String moduleId, String title, boolean isComplete) {
        mModuleId = moduleId;
        mTitle = title;
        mIsComplete = isComplete;
    }

    //called to recreate the instance of the object from the Parcel.
    private ModuleInfo(Parcel in) {
        mModuleId = in.readString();
        mTitle = in.readString();
        //mIsComplete = in.readByte() != 0;
        mIsComplete = (in.readInt() == 1);  //read from parcel, check if value = 1 assign true else assign false.
    }

    public String getModuleId() {
        return mModuleId;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isComplete() {
        return mIsComplete;
    }

    public void setComplete(boolean complete) {
        mIsComplete = complete;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleInfo that = (ModuleInfo) o;

        return mModuleId.equals(that.mModuleId);
    }

    @Override
    public int hashCode() {
        return mModuleId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcelDest, int flags) {
        //handles the details of writing the variables/data to parcel.

        //parcelDest.writeParcelable(mModules, 0); //write a parcelable reference.
        //write primitive types e.g string
        parcelDest.writeString(mModuleId); //write module_id
        parcelDest.writeString(mTitle); //write note_title.
        //write a boolean to parcel, convert boolean to int using logic true=1 : false =0 .
        parcelDest.writeInt(mIsComplete ? 1 : 0);  //check if mIscomplete is true . write 1 else write 0.
    }

    //code to recreate this class_instance from the parcel.
    //Note: Read parcel values in the same order they were written.
    public static final Creator<ModuleInfo> CREATOR = new Creator<ModuleInfo>() {
        @Override
        public ModuleInfo createFromParcel(Parcel in) {
            return new ModuleInfo(in);
        }

        @Override
        public ModuleInfo[] newArray(int size) {
            return new ModuleInfo[size];
        }
    };
}
