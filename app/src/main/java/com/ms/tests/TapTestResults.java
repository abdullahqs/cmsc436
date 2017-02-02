package com.ms.tests;

/**
 * Created by Ramse on 2/2/2017.
 */

public class TapTestResults {
    public Boolean IsLeftHand;
    public int NumTests;
    public int[] TestResults;

    public  TapTestResults(Boolean isLeftHand, int numTests) {
        IsLeftHand = isLeftHand;
        NumTests = numTests;
        TestResults = new int[NumTests];
    }
}
