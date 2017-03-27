package com.ms.tests.rotation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ms.tests.R;
import com.ms.tests.TestSelectionActivity;

import org.w3c.dom.Text;


public class RotateResultActivity extends AppCompatActivity {

    TextView display;
    Button mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_result);

        Intent i = getIntent();
        int[] out = i.getIntArrayExtra(RotateTestResults.RESULT_KEY);
        int average = 0;
        for(int index = 0;index<out.length;index++){
            average += out[index];
        }
        average = average/out.length;
        String avg_str = "On average, it took: "+average + " seconds to complete each rep.";
        display = (TextView) findViewById(R.id.output);
        display.setText(avg_str);

        mBack = (Button)  findViewById(R.id.testselection);

        mBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(RotateResultActivity.this, TestSelectionActivity.class);
                startActivity(i);
            }
        });



    }
}
