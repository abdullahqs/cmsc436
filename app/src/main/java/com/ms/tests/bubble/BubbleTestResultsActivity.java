package com.ms.tests.bubble;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ms.tests.R;
import com.ms.tests.TestSelectionActivity;
import com.ms.tests.tap.TapTestResults;

public class BubbleTestResultsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble_test_results);

        TextView bubblePops = (TextView) findViewById(R.id.bubble_results_pops);

        Intent i = getIntent();
        /*BubbleTestResults results = (BubbleTestResults) (i.getParcelableExtra(BubbleTestResults.RESULTS_KEY));

        bubblePops.setText("You popped " + results.numPops + " balloons!");
        final String shareString = "Popped " + results.numPops + " balloons!";*/

        int numPops = i.getIntExtra(BubbleTestResults.RESULTS_KEY, 0);
        bubblePops.setText("You popped " + numPops + " balloons!");
        final String shareString = "Popped " + numPops + " balloons!";

        final Button shareButton = (Button) findViewById(R.id.bubble_results_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareString != null) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_TEXT, shareString);
                    share.setType("text/plain");
                    startActivity(Intent.createChooser(share, "Share Results"));

                }
            }
        });

        final Button button = (Button) findViewById(R.id.bubble_results_restart_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), TestSelectionActivity.class);
                startActivity(i);
            }
        });
    }
}
