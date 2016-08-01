package com.ecnu.leondu.sheetmusic;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Random;

public class WorkActivity extends AppCompatActivity {

    // 按键总数,似乎是 25/7的音阶数目（不包括黑键）
    private final static int NUM_CLEF_NOTES = 25;
    // 是否显示正确的按键位置
    private boolean enable_show_correct;
    // 错误是否振动
    private boolean enable_vibration_on_wrong;

    // 谱号
    private ImageView clef_imageview;
    // 音符高度
    private ImageView note_imageview;
    // 音符高度
    private ImageView note1_imageview;
    // 音符高度
    private ImageView note2_imageview;
    // 音符高度
    private ImageView note3_imageview;

    private Button buttons[];
    // 似乎是按键的颜色表示,正确与否的信息
    private int default_color;

    // 难度等级,似乎是范围等级
    private final int[][] levels = {{8, 16}, {7, 17}, {6, 18}, {5, 19}, {4, 20}, {3, 21}, {2, 22}, {1, 23}, {0, 24}};

    // 谱号
    private final int[] clefs = {
            R.raw.treble_clef, R.raw.bass_clef, R.raw.alto_clef, R.raw.tenor_clef};
    // 音符位置
    private final int[] notes = {
            R.raw.note01, R.raw.note02, R.raw.note03, R.raw.note04, R.raw.note05,
            R.raw.note06, R.raw.note07, R.raw.note08, R.raw.note09, R.raw.note10,
            R.raw.note11, R.raw.note12, R.raw.note13, R.raw.note14, R.raw.note15,
            R.raw.note16, R.raw.note17, R.raw.note18, R.raw.note19, R.raw.note20,
            R.raw.note21, R.raw.note22, R.raw.note23, R.raw.note24, R.raw.note25};

    // 不同谱号中,第一个音出现的音高位置.
    private final int[] first_note = {3, 5, 4, 2};
    // 不同谱号包含的练习级
    /*
        private final int[] treble_notes = { 3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6 };
        private final int[] bass_notes   = { 5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1 };
        private final int[] alto_notes   = { 4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0 };
        private final int[] tenor_notes  = { 2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5 };
     */
    // 整数链表(测试表?)
    private LinkedList<Integer> quiz;
    // 正确的练习数目
    private int num_right;
    // 错误的练习数目
    private int num_wrong;
    // 开始的时间戳?
    private long time_start;
    // 练习总数?
    private int limit;
    // 当前的练习位置?
    private int current_quiz;
    // 当前的音高
    private int current_note;
    // 当前的音高
    private String  current_AbsolutePitch = "";
    // 当前的按键?
    private Button current_button;
    // 用于计算时间的线程
    private Thread time_update_thread = null;
    // 显示当前时间的文本
    private TextView time_elapsed;
    // 当前练习的进度
    private TextView progress;
    // 当前绝对音高
    private TextView absolutePitchTextview;

    private boolean time_elapsed_terminate;


    /*
        private final int[][] clef_notes = { treble_notes, bass_notes, alto_notes, tenor_notes };
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 返回 app 的设置参数
        SharedPreferences options = getSharedPreferences("sire", 0);
        // 返回存储的系统变量
        Bundle extras = getIntent().getExtras();
        // 目前练习的谱号
        boolean enable_treble_clef = options.getBoolean("enable_treble_clef", true);
        boolean enable_bass_clef = options.getBoolean("enable_bass_clef", true);
        boolean enable_alto_clef = options.getBoolean("enable_alto_clef", false);
        boolean enable_tenor_clef = options.getBoolean("enable_tenor_clef", false);

        int note_naming_style = options.getInt("note_naming_style", 0);
        // 难度层级
        int difficulty_level = options.getInt("difficulty_level", 0);


//        boolean practice = extras.getBoolean("practice", false);
        // 设置练习模式和比赛模式
//        if (practice) {
        // 练习模式,会显示正确的按键,错误的时候会振动
        limit = options.getInt("practice_limit", 200);
        enable_show_correct = options.getBoolean("enable_show_correct", true);
        enable_vibration_on_wrong = options.getBoolean("enable_vibration_on_wrong", true);
//        } else {
//            limit = options.getInt("quiz_limit", 10);
//            enable_show_correct = false;
//            enable_vibration_on_wrong = false;
//        }
        //Log.i ("info", String.format("Limit: %d", limit));

        // widgets , 应该指的是五线谱部分
        clef_imageview = (ImageView) findViewById(R.id.clef);
        note_imageview = (ImageView) findViewById(R.id.note);
        note1_imageview = (ImageView) findViewById(R.id.note1);
        note2_imageview = (ImageView) findViewById(R.id.note2);
        note3_imageview = (ImageView) findViewById(R.id.note3);

        //  五线谱框格部分(似乎默认显示)
        ImageView staff = (ImageView) findViewById(R.id.staff);

        // 不知道是不是影响绘制位置
        try {
            // 似乎是反射方法
            Method setLayerTypeMethod;
            // 查询一下这些方法的使用方法
            setLayerTypeMethod = clef_imageview.getClass().getMethod("setLayerType", new Class[]{int.class, Paint.class});
            setLayerTypeMethod.invoke(clef_imageview, new Object[]{View.LAYER_TYPE_SOFTWARE, null});

            setLayerTypeMethod = note_imageview.getClass().getMethod("setLayerType", new Class[]{int.class, Paint.class});
            setLayerTypeMethod.invoke(note_imageview, new Object[]{View.LAYER_TYPE_SOFTWARE, null});

            setLayerTypeMethod = note1_imageview.getClass().getMethod("setLayerType", new Class[]{int.class, Paint.class});
            setLayerTypeMethod.invoke(note1_imageview, new Object[]{View.LAYER_TYPE_SOFTWARE, null});

            setLayerTypeMethod = note2_imageview.getClass().getMethod("setLayerType", new Class[]{int.class, Paint.class});
            setLayerTypeMethod.invoke(note2_imageview, new Object[]{View.LAYER_TYPE_SOFTWARE, null});

            setLayerTypeMethod = staff.getClass().getMethod("setLayerType", new Class[]{int.class, Paint.class});
            setLayerTypeMethod.invoke(staff, new Object[]{View.LAYER_TYPE_SOFTWARE, null});
        } catch (NoSuchMethodException e) {
            // Older OS, no HW acceleration anyway
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        // 五线谱框格
        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.staff);
        staff.setImageDrawable(svg.createPictureDrawable());

        // 时间动态变化值
        time_elapsed = (TextView) findViewById(R.id.time_elapsed);
        progress = (TextView) findViewById(R.id.progress);
        absolutePitchTextview = (TextView) findViewById(R.id.txt_absolute_pitch);
        absolutePitchTextview.setText("绝对音高");
        time_elapsed.setText("0s");
        progress.setText("0 / 0");


        // 按键
        buttons = new Button[7];
        buttons[0] = (Button) findViewById(R.id.keyA);
        buttons[1] = (Button) findViewById(R.id.keyB);
        buttons[2] = (Button) findViewById(R.id.keyC);
        buttons[3] = (Button) findViewById(R.id.keyD);
        buttons[4] = (Button) findViewById(R.id.keyE);
        buttons[5] = (Button) findViewById(R.id.keyF);
        buttons[6] = (Button) findViewById(R.id.keyG);


        // note naming style（表示用何种方式来命名按键名称）
        String[] note_names;
        Resources res = getResources();

        // 意大利还是,英文
        switch (note_naming_style) {
            case 1:
                note_names = res.getStringArray(R.array.note_names_italian);
                break;
            case 0:
            default:
                note_names = res.getStringArray(R.array.note_names_english);
        }

        // set button captions from string resource（命名,意大利还是英文）
        for (int i = 0; i < 7; ++i)
            buttons[i].setText(note_names[i]);

        default_color = buttons[0].getTextColors().getDefaultColor();

        // 链表？(表示了练琴的范围)
        quiz = new LinkedList<Integer>();

        //Log.i ("info", String.format("Difficulty: %d", difficulty_level));
        // 通过练习难度,随机填充这个链表
        // 如果事先没有状态,用于回复当前练习状态
        if (savedInstanceState == null) { // previous state not saved

            // get bounds for selected difficulty level
            // 返回根据难度等级确定的上限和下限
            int a = levels[difficulty_level][0];
            int b = levels[difficulty_level][1];

            // 谱号
            int rand_clefs[] = new int[4];
            int rand_clefs_count = 0;
            // 是否启用对应谱号
            if (enable_treble_clef) rand_clefs[rand_clefs_count++] = 0;
            if (enable_bass_clef) rand_clefs[rand_clefs_count++] = 1;
            // 下面两个默认关闭
            if (enable_alto_clef) rand_clefs[rand_clefs_count++] = 2;
            if (enable_tenor_clef) rand_clefs[rand_clefs_count++] = 3;
            // 产生随机练习
            Random rand = new Random();

            int c, n, q, lq = -1;
            // 填充数据
            while (quiz.size() < limit) {
                // 随机谱号
                c = rand_clefs[rand.nextInt(rand_clefs_count)]; // random clef
                // 随机调号
                n = rand.nextInt(b - a + 1) + a; // random note
                // 每种号之间差25?
                q = NUM_CLEF_NOTES * c + n;

                //  不加入两个连续一样的
                if (lq != q) {
                    quiz.add(Integer.valueOf(q));
                    lq = q;
                }
            }

            time_start = System.currentTimeMillis();
            // 随机挑一个开始绘制

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_note();
                // 开始练习的线程？
                //  这个部分只是为了跳秒
                start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 更新时间流逝
    void update_time_elapsed() {
        long time_end = System.currentTimeMillis();

        long time = (long) (time_end - time_start);
        int minutes = (int) (time / (1000 * 60));
        time = time % (1000 * 60);

        int seconds = (int) (time / 1000);

        if (minutes > 0)
            time_elapsed.setText(String.format("%d:%02ds", minutes, seconds));
        else
            time_elapsed.setText(String.format("%ds", seconds));
    }

    void start() {
        update_time_elapsed();

        time_update_thread = new Thread(new Runnable() {
            public void run() {

                do {
                    time_elapsed.post(new Runnable() {

                        public void run() {
                            update_time_elapsed();
                        }
                    });

                    try {
                        // 这个部分其实是为了让时间一秒钟跳一次
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }

                } while (!time_elapsed_terminate);
            }
        });

        time_elapsed_terminate = false;
        // 线程开始运行
        time_update_thread.start();
    }

    // 关闭线程
    void stop() {
        if (time_update_thread != null) {
            time_elapsed_terminate = true;
            time_update_thread.interrupt();
            try {
                time_update_thread.join();
            } catch (InterruptedException e) {
            }
            time_update_thread = null;
        }
    }

    /*
        @Override
        public void onBackPressed () {
            stop ();
            super.onBackPressed ();
        }
    */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            stop();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onKeyA(View view) {
        clicked(0);
    }

    public void onKeyB(View view) {
        clicked(1);
    }

    public void onKeyC(View view) {
        clicked(2);
    }

    public void onKeyD(View view) {
        clicked(3);
    }

    public void onKeyE(View view) {
        clicked(4);
    }

    public void onKeyF(View view) {
        clicked(5);
    }

    public void onKeyG(View view) {
        clicked(6);
    }

    // 主要的逻辑结构
    void clicked(int note_id) {
        // 如果触发按键后,检测到不是当前状态"正确按键"的位置,则认为错误,进行错误的处理
        if (note_id != current_note) {
            ++num_wrong;

            if (enable_show_correct) {
                current_button.setTextColor(Color.BLUE);
                current_button.setTypeface(Typeface.DEFAULT_BOLD);
            }

            if (enable_vibration_on_wrong) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(250L);
            }
        } else {
            ++num_right;

            if (enable_show_correct) {
                current_button.setTextColor(default_color);
                current_button.setTypeface(Typeface.DEFAULT);
            }

            // 这里，直到next_note()返回了程序结束
            if (!next_note()) {
                // 程序执行完毕
                long time_end = System.currentTimeMillis();

                Toast.makeText(getBaseContext(), "测试完成", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent (this, ResultsActivity.class);
//                intent.putExtra ("num_right", num_right);
//                intent.putExtra ("num_wrong", num_wrong);
//                intent.putExtra ("total_time", (long)(time_end - time_start));
//                startActivity (intent);
                stop();
                finish();
            }
        }
    }

    // 产生下一个音符(这个只是为了显示
    boolean next_note() {
        if (quiz.size() <= 0) return false;

        // 删掉上一个音符(队列出队)
        current_quiz = quiz.removeFirst();

        progress.setText(String.format("%d / %d", limit - quiz.size(), limit));
        // 算出谱号,音符位置,!!!存储的可以有重复
        int clef = current_quiz / NUM_CLEF_NOTES;
        int note = current_quiz % NUM_CLEF_NOTES;
        // 算出是哪个音,C D E F G A B
        // 当前正确音,当前正确按键
        current_note = (first_note[clef] + note) % 7;
        current_button = buttons[current_note];
        current_AbsolutePitch = RelativePitchToAbsolutePitch(clef,note);
        absolutePitchTextview.setText(current_AbsolutePitch);

        // 绘制谱号,绘制音符
        SVG svg;
        svg = SVGParser.getSVGFromResource(getResources(), clefs[clef]);
        clef_imageview.setImageDrawable(svg.createPictureDrawable());


        svg = SVGParser.getSVGFromResource(getResources(), notes[note]);
        note_imageview.setImageDrawable(svg.createPictureDrawable());

        svg = SVGParser.getSVGFromResource(getResources(), notes[note+2]);
        Drawable drawable = svg.createPictureDrawable();

//        svg = new
//         svg = new SVGBuilder()
//                .readFromResource(getResources(), notes[note+2]) // if svg in res/raw
//                        // if svg in assets
//                 .setWhiteMode(true) // draw fills in white, doesn't draw strokes
//                 .setColorSwap(0xFF008800, 0xFF33AAFF) // swap a single colour
////                 .setColorFilter(filter) // run through a colour filter
////                 .set[Stroke|Fill]ColorFilter(filter) // apply a colour filter to only the stroke or fill
//                .build();

//        drawable.setColorFilter();
//        note1_imageview.setImageDrawable(drawable);




        svg = SVGParser.getSVGFromResource(getResources(), notes[note+4]);
        note2_imageview.setImageDrawable(svg.createPictureDrawable());


        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putLong("time_start", time_start);
        savedInstanceState.putInt("num_right", num_right);
        savedInstanceState.putInt("num_wrong", num_wrong);
        savedInstanceState.putInt("limit", limit);

        int array[] = new int[quiz.size() + 1];
        array[0] = current_quiz;
        for (int i = 0; i < quiz.size(); ++i)
            array[i + 1] = quiz.get(i);
        savedInstanceState.putIntArray("quiz", array);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        time_start = savedInstanceState.getLong("time_start");
        num_right = savedInstanceState.getInt("num_right");
        num_wrong = savedInstanceState.getInt("num_wrong");
        limit = savedInstanceState.getInt("limit");

        int array[] = savedInstanceState.getIntArray("quiz");

        for (int i = 0; i < array.length; ++i)
            quiz.add(array[i]);

        next_note();
        start();
    }

    /**
     * 将相对音高转换成绝对音高值,例如 C5 D2 这种
     *
     * @param clef
     * @param note
     * @return
     */
    private String RelativePitchToAbsolutePitch(int clef, int note) {
        String AbsolutePitch = "";
        switch ((first_note[clef] + note) % 7) {
            case 0:
                AbsolutePitch += "A";
                break;
            case 1:
                AbsolutePitch += "B";
                break;
            case 2:
                AbsolutePitch += "C";
                break;
            case 3:
                AbsolutePitch += "D";
                break;
            case 4:
                AbsolutePitch += "E";
                break;
            case 5:
                AbsolutePitch += "F";
                break;
            case 6:
                AbsolutePitch += "G";
                break;

        }

        switch (clef) {
            // 高音谱表
            case 0: {
                int level = 0;
                level = 3 + (first_note[clef] + note - 2) / 7;
                AbsolutePitch += level;
            }
            break;
            case 1: {
                int level = 0;
                level = 1 + (first_note[clef] + note - 2) / 7;
                AbsolutePitch += level;
            }
            break;
            case 2:
                AbsolutePitch += "E";
                break;
            case 3:
                AbsolutePitch += "F";
                break;
        }


        return AbsolutePitch;
    }
}

