package com.ms.tests.tap;

import android.os.Parcel;
import android.os.Parcelable;

import com.ms.tests.TestResults;
import java.util.Date;

/**
 * Created by Ramse on 2/2/2017.
 */

public class TapTestResults extends TestResults {
    public static final String RESULTS_KEY = "TEST_RESULTS";

    public Boolean isLeftHand;
    public int numTests;
    public int[] testResults;

    public TapTestResults(Boolean isLeftHand, int numTests, int value, String date) {
        super(value,date);
        this.isLeftHand = isLeftHand;
        this.numTests = numTests;
        testResults = new int[this.numTests];
    }

    public TapTestResults(Parcel data) {
        super(data);
        isLeftHand = data.readInt() == 0;
        numTests = data.readInt();
        testResults = data.createIntArray();
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeInt(isLeftHand ? 0 : 1);
        dest.writeInt(numTests);
        dest.writeIntArray(testResults);
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
