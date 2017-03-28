package com.ms.tests;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.Date;

/**
 * Created by AndrewLee on 2/17/17.
 */

public class TestResults implements Parcelable {
    int value;
    String date;

    public TestResults(int value,String date){
        this.value = value;
        this.date = date;
    }

    public TestResults(Parcel data){
        date = data.readString();
        value = data.readInt();
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(date);
        dest.writeInt(value);
    }

    public static final Parcelable.Creator<TestResults> CREATOR = new Parcelable.Creator<TestResults>() {
        public TestResults createFromParcel(Parcel data) {
            return new TestResults(data);
        }

        public TestResults[] newArray(int size) {
            return new TestResults[size];
        }
    };

}
