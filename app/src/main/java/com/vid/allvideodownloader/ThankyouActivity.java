package com.vid.allvideodownloader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ThankyouActivity extends AppCompatActivity {
    public static final String ACTION_CLOSE = "ACTION_CLOSE";
    ImageView okbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
        okbtn = (ImageView) findViewById(R.id.okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ACTION_CLOSE);
                sendBroadcast(myIntent);
                ThankyouActivity.this.finishAffinity();
            }
        });
    }
}