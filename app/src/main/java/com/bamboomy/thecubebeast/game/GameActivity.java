package com.bamboomy.thecubebeast.game;

import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bamboomy.thecubebeast.MainActivity;
import com.bamboomy.thecubebeast.R;

public class GameActivity extends RenderActivity {

    private MotionListener motionListener;

    private Pictures pictures;

    private OnlineDialog dialog;

    private String md5Hex;

    static GameActivity INSTANCE;

    private int width, height;

    public static GameMode GAME_MODE = null;

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
                getSupportFragmentManager(), this, 8);

        setContentView(motionListener);

        INSTANCE = this;

        GameMaster.dirtyReset();
        Pictures.reset();

        start();

        Log.d("beast", "game: start");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (M_PLAYER_LOOP != null) {
            M_PLAYER_LOOP.release();
        }

        M_PLAYER_LOOP = MediaPlayer.create(this, R.raw.loop);
        M_PLAYER_LOOP.setOnErrorListener(this);
        M_PLAYER_LOOP.setLooping(true);
        M_PLAYER_LOOP.setVolume(100, 100);

        play = true;

        M_PLAYER_LOOP.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (M_PLAYER_LOOP != null && M_PLAYER_LOOP.isPlaying()) {
            M_PLAYER_LOOP.stop();
        }

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

        if (M_PLAYER_LOOP != null) {
            M_PLAYER_LOOP.release();
        }

        M_PLAYER_RAW = null;
        M_PLAYER_LOOP = null;

        Log.d("beast", "game : destroy");
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
}
