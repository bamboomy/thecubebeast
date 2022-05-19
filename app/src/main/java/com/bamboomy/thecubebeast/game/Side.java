package com.bamboomy.thecubebeast.game;

import static com.bamboomy.thecubebeast.MainActivity.TAG;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.bamboomy.thecubebeast.R;

import javax.microedition.khronos.opengles.GL10;

import lombok.Getter;
import lombok.Setter;

class Side {

    private int[] textures;
    private boolean dirty = false;
    private static Bitmap COVER;
    static Bitmap GOOD;

    @Setter
    private Tupple tupple;

    private boolean shown = false;

    private Bitmap bitmap = null;

    private boolean hidden = false, hiding = false;

    @Getter
    private boolean destroyed = false;

    Side(GameActivity gameActivity) {

        textures = new int[1];

        if (COVER == null) {
            COVER = Pictures.decodeSampledBitmapFromResource(
                    R.drawable.questionmark, 200, 200, gameActivity);
        }

        if (GOOD == null) {
            GOOD = Pictures.decodeSampledBitmapFromResource(
                    R.drawable.good, 200, 200, gameActivity);
        }

        /*
        tupple = GameMaster.getInstance().getTupple(this, chooseUnchosen);

        if (tupple.isNew()) {

            tupple.setPicture(Pictures.getUnchoosenPicture(gameActivity));
        }
         */
    }

    void initTextures() {

        GLES20.glGenTextures(1, textures, 0);

        loadTextures(false);
    }

    void loadTextures(boolean refresh) {

        loadGLTextureSide(refresh);
    }

    int getTexture() {

        return textures[0];
    }

    private void loadGLTextureSide(boolean refresh) {

        if (bitmap == null) {
            bitmap = COVER;
        }

        Log.d("beast", GOOD.toString() + "->" + bitmap.toString());

        // ...and bind it to our array
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // create nearest filtered textures
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        if (refresh) {
            GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, bitmap);

            Log.d(TAG, "refresh");
        } else {
            // Use Android GLUtils to specify a two-dimensional textures image
            // from
            // our bitmap
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        dirty = false;
    }

    boolean tap() {

        if (tupple.isSolved()) {
            return false;
        }

        if (shown) {

            if (GameMaster.getInstance().hide(this)) {

                bitmap = null;

                shown = false;

                loadGLTextureSide(true);

                return true;
            }

        } else if (GameMaster.getInstance().canShow(this)) {

            bitmap = tupple.getBitmap();

            shown = true;
        }

        loadGLTextureSide(true);

        return false;
    }

    synchronized void reset() {

        bitmap = null;

        shown = false;

        dirty = true;
    }

    void clean() {

        if (!dirty) {

            return;
        }

        loadGLTextureSide(true);
    }

    Tupple getTupple() {
        return tupple;
    }

    void hide() {

        hidden = true;

        dirty = true;
    }

    void setPicture(Bitmap picture) {

        bitmap = picture;

        dirty = true;
    }

    public void destroy() {

        destroyed = true;
    }
}
