package com.bamboomy.thecubebeast.game;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bamboomy.thecubebeast.R;

public class RenderActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener {

    protected MediaPlayer M_PLAYER_RAW, M_PLAYER_LOOP;

    private int[] raws = {
            R.raw.raw1, R.raw.raw2, R.raw.raw3, R.raw.raw4
    };

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

        if (M_PLAYER_LOOP != null) {
            try {
                M_PLAYER_LOOP.stop();
                M_PLAYER_LOOP.release();
            } finally {
                M_PLAYER_LOOP = null;
            }
        }

        return false;
    }
}
