package com.ecnu.leondu.sheetmusic;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class ReadingActivity extends AppCompatActivity {

    private MyView myView;
    // 复音数目
    public static int noteNumber = 0;
    public static int clefNumber = 0;


    private ImageView noteOneImageView;
    private ImageView noteTwoImageView;
    private ImageView noteThreeImageView;

    private ImageView clefBothImageView;
    private ImageView clefTrebleImageView;
    private ImageView clefBassImageView;

    private Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 提供沉浸式状态栏
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //获取样式中的属性值
                TypedValue typedValue = new TypedValue();
                this.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
                int[] attribute = new int[]{android.R.attr.colorPrimary};
                TypedArray array = this.obtainStyledAttributes(typedValue.resourceId, attribute);
                int color = array.getColor(0, Color.TRANSPARENT);
                array.recycle();
                window.setStatusBarColor(color);
            }
        }
        setSupportActionBar(toolbar);

        startButton = (Button) findViewById(R.id.btn_beginning);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 听力部分要处理
                Intent intent = new Intent(ReadingActivity.this, WorkActivity.class);
                startActivity(intent);
            }
        });

        myView = (MyView) findViewById(R.id.anim_great);
        noteOneImageView = (ImageView) findViewById(R.id.img_notes_one);
        if (noteNumber == 1) {
            noteOneImageView.setImageResource(R.mipmap.key_one_touched);
        } else {
            noteOneImageView.setImageResource(R.mipmap.key_one_untouched);
        }

        noteOneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteNumber = 1;
                noteOneImageView.setImageResource(R.mipmap.key_one_touched);
                noteTwoImageView.setImageResource(R.mipmap.key_two_untouched);
                noteThreeImageView.setImageResource(R.mipmap.key_three_untouched);
            }
        });

        noteTwoImageView = (ImageView) findViewById(R.id.img_notes_two);
        if (noteNumber == 2) {
            noteTwoImageView.setImageResource(R.mipmap.key_two_touched);
        } else {
            noteTwoImageView.setImageResource(R.mipmap.key_two_untouched);

        }
        noteTwoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteNumber = 2;
                noteOneImageView.setImageResource(R.mipmap.key_one_untouched);
                noteTwoImageView.setImageResource(R.mipmap.key_two_touched);
                noteThreeImageView.setImageResource(R.mipmap.key_three_untouched);
            }
        });

        noteThreeImageView = (ImageView) findViewById(R.id.img_notes_three);
        if (noteNumber == 3) {
            noteThreeImageView.setImageResource(R.mipmap.key_three_touched);
        } else {
            noteThreeImageView.setImageResource(R.mipmap.key_three_untouched);

        }
        noteThreeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteNumber = 3;
                noteOneImageView.setImageResource(R.mipmap.key_one_untouched);
                noteTwoImageView.setImageResource(R.mipmap.key_two_untouched);
                noteThreeImageView.setImageResource(R.mipmap.key_three_touched);
            }
        });


        // 谱号选择

        clefBothImageView = (ImageView) findViewById(R.id.img_both_clef);
        if (clefNumber == 1) {
            clefBothImageView.setImageResource(R.mipmap.sheet_both_touched);
        } else {
            clefBothImageView.setImageResource(R.mipmap.sheet_both_untouched);
        }

        clefBothImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clefNumber = 1;
                clefBothImageView.setImageResource(R.mipmap.sheet_both_touched);
                clefTrebleImageView.setImageResource(R.mipmap.sheet_high_untouched);
                clefBassImageView.setImageResource(R.mipmap.sheet_low_untouched);
            }
        });

        clefTrebleImageView = (ImageView) findViewById(R.id.img_treble_clef);
        if (clefNumber == 2) {
            clefTrebleImageView.setImageResource(R.mipmap.sheet_high_touched);
        } else {
            clefTrebleImageView.setImageResource(R.mipmap.sheet_high_untouched);

        }
        clefTrebleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clefNumber = 2;
                clefBothImageView.setImageResource(R.mipmap.sheet_both_untouched);
                clefTrebleImageView.setImageResource(R.mipmap.sheet_high_touched);
                clefBassImageView.setImageResource(R.mipmap.sheet_low_untouched);
            }
        });

        clefBassImageView = (ImageView) findViewById(R.id.img_bass_clef);
        if (clefNumber == 3) {
            clefBassImageView.setImageResource(R.mipmap.sheet_low_touched);
        } else {
            clefBassImageView.setImageResource(R.mipmap.sheet_low_untouched);

        }
        clefBassImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clefNumber = 3;
                clefBothImageView.setImageResource(R.mipmap.sheet_both_untouched);
                clefTrebleImageView.setImageResource(R.mipmap.sheet_high_untouched);
                clefBassImageView.setImageResource(R.mipmap.sheet_low_touched);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView.start();
            }
        });
    }

}
