package com.ms.tests.tap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.ms.tests.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TapConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_config);

        final Button button = (Button) findViewById(R.id.teststart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.numberoftests);

                int numTests = 1;
                if(!editText.getText().toString().isEmpty())
                    numTests = Integer.parseInt(editText.getText().toString());

                RadioButton leftButton, rightButton;

                leftButton = (RadioButton) findViewById(R.id.lefthand);
                rightButton = (RadioButton) findViewById(R.id.righthand);

                boolean isleft = true;
                if(rightButton.isChecked()){
                    isleft = false;
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String date = df.format(c.getTime());

                TapTestResults result = new TapTestResults(isleft,numTests,0,date);

                Intent i=new Intent(TapConfigActivity.this, TapTestActivity.class);
                i.putExtra(TapTestResults.RESULTS_KEY, result);
                startActivity(i);

            }
        });
    }


}
