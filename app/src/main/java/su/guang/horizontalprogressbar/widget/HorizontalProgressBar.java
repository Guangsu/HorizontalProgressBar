package su.guang.horizontalprogressbar.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * <title>水平进度条</title>
 * <p>{@link HorizontalProgressBar#start()}被调用后，进度条在第一阶段的速度为慢，第二阶段速度为中，
 * 第三阶段为快。</p>
 * <p>当第二阶段结束时，如果{@link HorizontalProgressBar#finish(long tag)}方法还未被调用，则进度条停止，
 * 进入等待状态，直到{@link HorizontalProgressBar#finish(long tag)}被调用，进入第三阶段至结束。</p>
 * <p/>
 * Created by Jaren on 2016/11/24.
 */
public class HorizontalProgressBar extends ProgressBar {

    private final static String TAG = "HorizontalProgressBar";

    // 进度条在每个阶段发送给Handler的消息
    private final static int MSG_FIRST_START_PROGRESS = 1;
    private final static int MSG_SECOND_START_PROGRESS = 2;
    private final static int MSG_THIRD_START_PROGRESS = 3;
    private final static int MSG_CUSTOM_PROGRESS = 4;

    // 进度条在每个阶段的更新时间间隔，ms
    private final static int FIRST_START_PROGRESS_INTERVAL = 10;
    private final static int SECOND_START_PROGRESS_INTERVAL = 100;
    private final static int THIRD_START_PROGRESS_INTERVAL = 1;

    // 最大进度
    private final int MAX_PROGRESS = 100;

    // 进度条每个阶段的起始值
    private final int FIRST_START_PROGRESS = 40;
    private final int SECOND_START_PROGRESS = 60;
    private final int THIRD_START_PROGRESS = 80;

    // 进度条每次更新的进度值
    private final int UPDATE_PROGRESS_INTERVAL = 1;

    private long mBarTag;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Message n = obtainMessage();
            n.obj = msg.obj;
            switch (msg.what) {
                case MSG_FIRST_START_PROGRESS:
                    if (getProgress() < FIRST_START_PROGRESS) {
                        setProgress(getProgress() + UPDATE_PROGRESS_INTERVAL);
                        sendEmptyMessageDelayed(MSG_FIRST_START_PROGRESS, FIRST_START_PROGRESS_INTERVAL);
                    } else {
                        sendEmptyMessage(MSG_SECOND_START_PROGRESS);
                    }
                    break;
                case MSG_SECOND_START_PROGRESS:
                    if (getProgress() >= FIRST_START_PROGRESS && getProgress() < SECOND_START_PROGRESS) {
                        setProgress(getProgress() + UPDATE_PROGRESS_INTERVAL);

                        sendEmptyMessageDelayed(MSG_SECOND_START_PROGRESS, SECOND_START_PROGRESS_INTERVAL);
                    }
                    break;
                case MSG_THIRD_START_PROGRESS:
                    removeMessages(MSG_FIRST_START_PROGRESS);
                    removeMessages(MSG_SECOND_START_PROGRESS);

                    // 防止多次调用start()时，finish()也被多次调用，导致进度条闪多次的情况
                    if (getProgress() == 0) {
                        break;
                    }
                    if (getProgress() < MAX_PROGRESS) {
                            setProgress(getProgress() + (UPDATE_PROGRESS_INTERVAL * 5));

                            n.what = MSG_THIRD_START_PROGRESS;
                            sendMessageDelayed(n,THIRD_START_PROGRESS_INTERVAL);
                    }
                    if (getProgress() >= MAX_PROGRESS) {
                        removeMessages(MSG_THIRD_START_PROGRESS);
                        setProgress(0);
                        if(0 == (long)msg.obj){
                            Log.i(TAG, "finish all" );
                        }else if(mBarTag == (long)msg.obj){
                            Log.i(TAG, "finish-" + mBarTag);
                        } else {
                            Log.d(TAG,"Current tag is " + mBarTag + ",but target is " + msg.obj + ".Progress is " + getProgress() + "." );
                        }
                    }
                    break;
                case MSG_CUSTOM_PROGRESS:
                    removeMessages(MSG_FIRST_START_PROGRESS);
                    removeMessages(MSG_SECOND_START_PROGRESS);
                    removeMessages(MSG_THIRD_START_PROGRESS);

                    setProgress(msg.arg1);
                    break;
            }
        }
    };

    public HorizontalProgressBar(Context context) {
        super(context);
        setMax(MAX_PROGRESS);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开始进度条时调用
     */
    public long start() {
        //Log.i(TAG, "start");
        setProgress(0);
        mHandler.removeMessages(MSG_FIRST_START_PROGRESS);
        mHandler.removeMessages(MSG_SECOND_START_PROGRESS);
        mHandler.removeMessages(MSG_THIRD_START_PROGRESS);

        mBarTag = System.currentTimeMillis();

        Message msg = new Message();
        msg.what = MSG_FIRST_START_PROGRESS;
        msg.obj = mBarTag;
        mHandler.sendMessage(msg);
        //mHandler.sendEmptyMessage(MSG_FIRST_START_PROGRESS);
        Log.i(TAG, "start-" + mBarTag);
        return mBarTag;
    }

    public long start(long tag) {
        setProgress(0);
        mHandler.removeMessages(MSG_FIRST_START_PROGRESS);
        mHandler.removeMessages(MSG_SECOND_START_PROGRESS);
        mHandler.removeMessages(MSG_THIRD_START_PROGRESS);

        mBarTag = tag;

        Message msg = new Message();
        msg.what = MSG_FIRST_START_PROGRESS;
        msg.obj = mBarTag;
        mHandler.sendMessage(msg);
        Log.i(TAG, "start-" + mBarTag);
        return mBarTag;
    }

    /**
     * <p>调用{@link HorizontalProgressBar#loading(int progress)}时，
     * 不需要重复调用{@link HorizontalProgressBar#start()}和
     * {@link HorizontalProgressBar#finish(long tag)} 方法</p>
     *
     * <p>在{@link HorizontalProgressBar#loading(int progress)}中，
     * 根据progress的值进行分发处理</p>
     *
     * @param progress 进度值
     */
    public void loading(int progress) {
        Log.i(TAG, "loading,progress = " + progress);

        // 分发
        if (progress == 0) {
            start();
            return;
        }
        if (progress == MAX_PROGRESS) {
            finish();
            return;
        }

        Message msg = new Message();
        msg.what = MSG_CUSTOM_PROGRESS;
        msg.arg1 = progress;
        mHandler.sendMessage(msg);
    }

    /**
     * 结束进度条时调用
     */
    public void finish(long tag) {
        //Log.i(TAG, "finish-" + tag);
        if(tag == 0 || tag == mBarTag){
            Message msg = new Message();
            msg.what = MSG_THIRD_START_PROGRESS;
            msg.obj = tag;
            mHandler.sendMessage(msg);
        }
    }

    public void finish() {
        Log.i(TAG, "finish all");

        finish(0);
    }

    public long getBarTag() {
        return mBarTag;
    }

    public void setBarTag(long tag) {
        this.mBarTag = tag;
    }
}
