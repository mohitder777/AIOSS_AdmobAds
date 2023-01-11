package com.vid.allvideodownloader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vid.allvideodownloader.R;

public class MainActivity2 extends AppCompatActivity {



    TextView tvplay;
    RelativeLayout rlhome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        rlhome=findViewById(R.id.rlhome);
        rlhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://play.google.com/store/apps/details?id=qureka.live.game.show&hl=en_IN&gl=US");
                finish();
            }
        });




    }

    private void goToUrl(String s) {
        Uri uri=Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
        finish();

    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(MainActivity2.this,MainActivity2.class);
        startActivity(i);
        finish();

        super.onBackPressed();
    }
}