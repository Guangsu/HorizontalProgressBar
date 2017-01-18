# HorizontalProgressBar


### 介绍：
一个简单的水平进度条。

* `start()`被调用后，进度条在第一阶段的速度为慢，第二阶段速度为中，第三阶段为快。
* 当第二阶段结束时，如果`finish()`方法还未被调用，则进度条暂停，进入等待状态，直到`finish()`被调用，进入第三阶段至结束。
* 多次调用同一个HorizontalProgressBar对象的start()和finish(long tag)方法时，finish(long tag)方法只对最后一次的start()调用有效

HorizontalProgressBar可以通过指定tag结束进度条
`public void finish(long tag) {}`

通过以下方式来获取一个tag

* `long start()`返回一个tag
* `long getBarTag()`返回一个tag
* 自定义一个long类型的tag，通过`long start(long tag)`传递

### 效果图

![效果图1](https://github.com/Guangsu/HorizontalProgressBar/blob/master/1.gif)

![效果图2](https://github.com/Guangsu/HorizontalProgressBar/blob/master/2.gif)

![效果图3](https://github.com/Guangsu/HorizontalProgressBar/blob/master/3.gif)

### 在XML中使用
```xml
<package.HorizontalProgressBar
    android:id="@+id/progressbar"
    style="?android:attr/progressBarStyleHorizontal"
    android:layout_width="match_parent"
    android:layout_height="2dp"
    android:progressDrawable="@drawable/horizontal_progress_bar"/>
```

### Demo

```java
public class MainActivity extends AppCompatActivity {

    // 任务状态消息
    private static final int TASK_1_STARTED = 1;
    private static final int TASK_2_STARTED = 2;
    private static final int TASK_3_STARTED = 3;
    private static final int TASK_4_PROGRESS = 4;
    private static final int TASK_1_FINISHED = 5;
    private static final int TASK_2_FINISHED = 6;
    private static final int TASK_3_FINISHED = 7;

    // 控件
    private HorizontalProgressBar mProgressBar1;
    private Button mStartBtn1;

    private HorizontalProgressBar mProgressBar2;
    private Button mStartBtn2;

    private HorizontalProgressBar mProgressBar3;
    private Button mStartBtn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar1 = (HorizontalProgressBar) findViewById(R.id.progressbar1);
        mStartBtn1 = (Button) findViewById(R.id.btn_start1);

        mProgressBar2 = (HorizontalProgressBar) findViewById(R.id.progressbar2);
        mStartBtn2 = (Button) findViewById(R.id.btn_start2);

        mProgressBar3 = (HorizontalProgressBar) findViewById(R.id.progressbar3);
        mStartBtn3 = (Button) findViewById(R.id.btn_start3);
    }

    public void start1(View v){
        mStartBtn1.setClickable(false);
        // 开始任务1
        mHandler.sendEmptyMessage(TASK_1_STARTED);
    }

    public void start2(View v){
        mStartBtn2.setClickable(false);
        // 开始任务2
        mHandler.sendEmptyMessage(TASK_2_STARTED);
        // 模拟任务2开始2s后开始任务3
        mHandler.sendEmptyMessageDelayed(TASK_3_STARTED, 2 * 1000);
    }

    public void start3(View v){
        mStartBtn3.setClickable(false);
        // 开始任务4
        mHandler.sendEmptyMessage(TASK_4_PROGRESS);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TASK_1_STARTED: {
                    Toast.makeText(MainActivity.this,"任务1开始",Toast.LENGTH_SHORT).show();

                    long tag1 = mProgressBar1.start();

                    Message msg1 = new Message();
                    msg1.what = TASK_1_FINISHED;
                    msg1.obj = tag1;
                    // 模拟任务1耗时5s
                    mHandler.sendMessageDelayed(msg1, 5 * 1000);
                }
                    break;
                case TASK_1_FINISHED: {
                    Toast.makeText(MainActivity.this,"任务1结束",Toast.LENGTH_SHORT).show();

                    long tag1 = (long) msg.obj;
                    mProgressBar1.finish(tag1);
                    mStartBtn1.setClickable(true);
                }
                    break;
                case TASK_2_STARTED: {
                    Toast.makeText(MainActivity.this,"任务2开始",Toast.LENGTH_SHORT).show();

                    long tag2 = mProgressBar2.start();

                    Message msg2 = new Message();
                    msg2.what = TASK_2_FINISHED;
                    msg2.obj = tag2;
                    // 模拟任务2耗时5s
                    mHandler.sendMessageDelayed(msg2, 5 * 1000);
                }
                    break;
                case TASK_2_FINISHED: {
                    Toast.makeText(MainActivity.this,"任务2结束",Toast.LENGTH_SHORT).show();

                    long tag1 = (long) msg.obj;
                    mProgressBar2.finish(tag1);
                }
                    break;
                case TASK_3_STARTED:{
                    Toast.makeText(MainActivity.this,"任务3开始",Toast.LENGTH_SHORT).show();

                    long tag2 = mProgressBar2.start();

                    Message msg2 = new Message();
                    msg2.what = TASK_3_FINISHED;
                    msg2.obj = tag2;
                    // 模拟任务3耗时5s
                    mHandler.sendMessageDelayed(msg2, 5 * 1000);
                }
                    break;
                case TASK_3_FINISHED:{
                    Toast.makeText(MainActivity.this,"任务3结束",Toast.LENGTH_SHORT).show();

                    long tag2 = (long) msg.obj;
                    mProgressBar2.finish(tag2);
                    mStartBtn2.setClickable(true);
                }
                    break;
                case TASK_4_PROGRESS: {
                    int progress = msg.obj == null ? 0 : (int) msg.obj;

                    if(progress == 0)
                        Toast.makeText(MainActivity.this,"任务4开始",Toast.LENGTH_SHORT).show();

                    if (progress >= 100) {
                        mStartBtn3.setClickable(true);
                        Toast.makeText(MainActivity.this,"任务4结束",Toast.LENGTH_SHORT).show();
                        break;
                    }

                    mProgressBar3.loading(++progress);

                    Message msg3 = new Message();
                    msg3.what = TASK_4_PROGRESS;
                    msg3.obj = progress;
                    // 模拟任务4周期为0.05s
                    mHandler.sendMessageDelayed(msg3, 50);
                }
                    break;
            }
        }
    };
```
