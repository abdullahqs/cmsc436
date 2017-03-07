package com.ms.tests.bubble;

import android.os.Parcel;
import android.os.Parcelable;

import com.ms.tests.TestResults;

public class BubbleTestResults extends TestResults {
    public static final String RESULTS_KEY = "BUBBLE_TEST_RESULTS";

    public int numPops;

    public BubbleTestResults(int value, String date) {
        super(value,date);
        this.numPops = value;
    }

    public BubbleTestResults(Parcel data) {
        super(data);
        numPops = data.readInt();
    }

}
