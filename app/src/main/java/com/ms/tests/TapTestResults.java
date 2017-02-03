package com.ms.tests;

import android.os.Parcel;
import android.os.Parcelable;

import junit.framework.Test;

/**
 * Created by Ramse on 2/2/2017.
 */

public class TapTestResults implements Parcelable {
    public Boolean IsLeftHand;
    public int NumTests;
    public int[] TestResults;

    public  TapTestResults(Boolean isLeftHand, int numTests) {
        IsLeftHand = isLeftHand;
        NumTests = numTests;
        TestResults = new int[NumTests];
    }

    public  TapTestResults(Parcel data) {
        IsLeftHand = data.readInt() == 0;
        NumTests = data.readInt();
        TestResults = data.createIntArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(IsLeftHand ? 0 : 1);
        dest.writeInt(NumTests);
        dest.writeIntArray(TestResults);
    }

    public static final Parcelable.Creator<TapTestResults> CREATOR = new Parcelable.Creator<TapTestResults>() {
        public TapTestResults createFromParcel(Parcel data) {
            return new TapTestResults(data);
        }

        public TapTestResults[] newArray(int size) {
            return new TapTestResults[size];
        }
    };
}
