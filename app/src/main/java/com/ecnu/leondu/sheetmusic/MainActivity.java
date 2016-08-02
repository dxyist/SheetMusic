package com.ecnu.leondu.sheetmusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //TODO 这部分必须也要处理蓝牙通信

    private ImageButton readingImageButton;
    private ImageButton listeningImageButton;
    private TextView displayTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        readingImageButton = (ImageButton) findViewById(R.id.imgButton_reading);
        readingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReadingActivity.class);
                startActivity(intent);

            }
        });
        listeningImageButton = (ImageButton) findViewById(R.id.imgButton_listening);
        listeningImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 听力部分要处理
                Intent intent = new Intent(MainActivity.this, ReadingActivity.class);
                startActivity(intent);
            }
        });

        displayTextView = (TextView) findViewById(R.id.txt_display);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

}
