package com.bamboomy.thecubebeast.game;

import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bamboomy.thecubebeast.MainActivity;
import com.bamboomy.thecubebeast.R;

public class GameActivity extends FragmentActivity implements MediaPlayer.OnErrorListener {

    private MotionListener motionListener;

    private Pictures pictures;

    private OnlineDialog dialog;

    private String md5Hex;

    private MediaPlayer M_PLAYER_RAW, M_PLAYER_KEY1, M_PLAYER_KEY2;

    static GameActivity INSTANCE;

    private int width, height;

    private int[] raws = {
            R.raw.raw1, R.raw.raw2, R.raw.raw3, R.raw.raw4
    };

    private boolean play;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("beast", "game: start -- begin");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        motionListener = new MotionListener(getApplication(), width, height,
                getSupportFragmentManager(), this, pictures);

        setContentView(motionListener);

        INSTANCE = this;

        GameMaster.reset();
        Pictures.reset();

        Log.d("beast", "game: start");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (M_PLAYER_KEY1 != null) {
            M_PLAYER_KEY1.release();
        }

        M_PLAYER_KEY1 = MediaPlayer.create(this, R.raw.piano1);
        M_PLAYER_KEY1.setOnErrorListener(this);
        M_PLAYER_KEY1.setLooping(false);
        M_PLAYER_KEY1.setVolume(100, 100);

        if (M_PLAYER_KEY2 != null) {
            M_PLAYER_KEY2.release();
        }

        M_PLAYER_KEY2 = MediaPlayer.create(this, R.raw.piano2);
        M_PLAYER_KEY2.setOnErrorListener(this);
        M_PLAYER_KEY2.setLooping(false);
        M_PLAYER_KEY2.setVolume(100, 100);

        play = true;

        new Thread(new PianoMan()).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        play = false;
    }

    public void start() {

        motionListener.listen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        play = false;

        if (M_PLAYER_RAW != null) {
            M_PLAYER_RAW.release();
        }

        if (M_PLAYER_KEY1 != null) {
            M_PLAYER_KEY1.release();
        }

        if (M_PLAYER_KEY2 != null) {
            M_PLAYER_KEY2.release();
        }

        M_PLAYER_RAW = null;
        M_PLAYER_KEY1 = null;
        M_PLAYER_KEY2 = null;

        Log.d("beast", "game : destroy");
    }

    void raw() {

        if (M_PLAYER_RAW != null) {
            M_PLAYER_RAW.release();
        }

        M_PLAYER_RAW = MediaPlayer.create(this, raws[(int) (Math.random() * raws.length)]);
        M_PLAYER_RAW.setOnErrorListener(this);
        M_PLAYER_RAW.setLooping(false);
        M_PLAYER_RAW.setVolume(100, 100);

        M_PLAYER_RAW.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();

        if (M_PLAYER_RAW != null) {
            try {
                M_PLAYER_RAW.stop();
                M_PLAYER_RAW.release();
            } finally {
                M_PLAYER_RAW = null;
            }
        }

        if (M_PLAYER_KEY1 != null) {
            try {
                M_PLAYER_KEY1.stop();
                M_PLAYER_KEY1.release();
            } finally {
                M_PLAYER_KEY1 = null;
            }
        }

        if (M_PLAYER_KEY2 != null) {
            try {
                M_PLAYER_KEY2.stop();
                M_PLAYER_KEY2.release();
            } finally {
                M_PLAYER_KEY2 = null;
            }
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        motionListener.stop();

        INSTANCE = null;

        Log.d("beast", "game: stop");
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        finish();
    }


    private class PianoMan implements Runnable {

        @Override
        public void run() {

            while (play) {

                long wait = (long) ((Math.random() * 3) + 1) * 1000;

                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if ((Math.random() * 3) > 1) {

                    if (M_PLAYER_KEY1 != null) {
                        M_PLAYER_KEY1.start();
                    }

                } else {

                    if (M_PLAYER_KEY2 != null) {
                        M_PLAYER_KEY2.start();
                    }
                }
            }
        }
    }
}
