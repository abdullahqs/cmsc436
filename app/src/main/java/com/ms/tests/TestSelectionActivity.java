package com.ms.tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selection);

        final Button button = (Button) findViewById(R.id.tapButton);
       // button.setOnClickListener(onClick);
    }

    //@Override
    public void onClick(View v) {

        Intent i=new Intent(TestSelectionActivity.this, TapTestActivity.class);
        startActivity(i);

    }

}
