package com.ms.tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class TapConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_config);




        final Button button = (Button) findViewById(R.id.teststart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.numberoftests);
                int numberoftests = Integer.parseInt(editText.getText().toString());

                RadioButton leftButton, rightButton;

                leftButton = (RadioButton) findViewById(R.id.lefthand);
                rightButton = (RadioButton) findViewById(R.id.righthand);

                boolean isleft = true;
                if(rightButton.isChecked()){
                    isleft = false;
                }

                TapTestResults result = new TapTestResults(isleft, numberoftests);

                Intent i=new Intent(TapConfigActivity.this, TapTestActivity.class);
                i.putExtra("TEST_RESULTS", result);
                startActivity(i);

            }
        });
    }


}
