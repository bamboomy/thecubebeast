package com.bamboomy.thecubebeast.game;

import static java.lang.Thread.sleep;

import android.app.Application;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MotionListener extends GLSurfaceView implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private GestureDetectorCompat mDetector;

    private static Context myContext;

    private float mPreviousX = 0, mPreviousY = 0;

    private BeastRenderer renderer;

    private boolean listen = true;

    private int updateSpeed = 500;

    private boolean running = true;

    private Thread refreshThread = new Thread(new Runnable() {

        @Override
        public void run() {

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (running) {

                try {
                    sleep(updateSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                requestRender();
            }
        }
    });

    public MotionListener(Context context) {
        super(context);

        myContext = context;

        setEGLContextClientVersion(2);

        mDetector = new GestureDetectorCompat(context, this);

        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
    }

    public MotionListener(Application application, int width, int height, FragmentManager supportFragmentManager,
                          GameActivity gameActivity, Pictures pictures) {

        this(application);

        renderer = new BeastRenderer(pictures, this, gameActivity);

        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mDetector = new GestureDetectorCompat(myContext, this);

        requestRender();

        setOnTouchListener(this);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if (!listen) {
            return false;
        }

        float x = e.getX();
        float y = e.getY();

        tap(x, y);

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.mDetector.onTouchEvent(event);

        if (!listen) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                renderer.move(x, y, mPreviousX, mPreviousY);
                requestRender();
                break;
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;

    }

    private void tap(float x, float y) {

        renderer.collision((int) x, (int) y);
        requestRender();
    }

    void listen() {

        listen = true;

        //this shouldn't be done here but this is just a convenient place
        // long live the zerg approach of Memcidcez...

        refreshThread.start();
    }

    void stop() {

        running = false;
    }
}
