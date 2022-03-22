/*
 * Copyright (c) 2016 Sander Theetaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.bamboomy.thecubebeast.game;

import static com.bamboomy.thecubebeast.game.Mode.COLOR_CUBE_CHOSEN;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bamboomy.thecubebeast.R;

/**
 * Created by a162299 on 24-4-2016.
 */
public class TutorialManager {

    private TutorialImage tutorialImage = new TutorialImage();

    private Context mContext;

    private boolean dirty = true;

    private boolean rotated = false;

    private boolean shouldRotate = true;

    private boolean showRotateBeast = true, showTapCube = false;
    private boolean showRotateCube = false, showRevealSide = false;
    private boolean showHideSide = false, showTapTwo = false;
    private boolean shouldShowTapAway = false, showTapAway = false;
    private boolean showOtherCube = false, showEnd = false;

    public static boolean ACTIVE = false;
    public static boolean COLOR_TUTORIAL = false;

    public static final String TUTORIAL_FINISHED = "tutorialFinished";
    public static final String COLOR_TUTORIAL_FINISHED = "colorTutorialFinished";

    private boolean cubeChoosen = false;

    private int colorTaps = 0;
    private boolean tutorialFinished = false;

    TutorialManager(Context mContext) {

        this.mContext = mContext;

        COLOR_TUTORIAL = false;
    }

    void draw(int mTextureCoordinateHandle, int maPositionHandle, int muMVPMatrixHandle, float[] mMVPMatrix, int maColorHandle) {

        if (!ACTIVE && !COLOR_TUTORIAL) {
            return;
        }

        tutorialImage.draw(mTextureCoordinateHandle, maPositionHandle, muMVPMatrixHandle, mMVPMatrix, maColorHandle);
    }

    private void loadGLTexture(int imageId) {

        tutorialImage.loadGLTexture(mContext, imageId);

        dirty = false;
    }

    void updateGLTexture(boolean sameSide, boolean allCubes) {

        if (!dirty || !ACTIVE) {
            return;
        }

        if (showRotateBeast && rotated) {

            rotated = false;
            //shouldTap = true;

            loadGLTexture(R.drawable.select_cube);

            showTapCube = true;
            showRotateBeast = false;

            return;
        }

        /*
        if (showTapCube && tapped) {

            tapped = false;
            shouldRotate = true;

            loadGLTexture(R.drawable.rotate_cube);

            showRotateCube = true;
            showTapCube = false;

            return;
        }

        if (showRotateCube && rotated) {

            rotated = false;
            shouldTap = true;

            loadGLTexture(R.drawable.reveal_side);

            showRevealSide = true;
            showRotateCube = false;

            return;
        }

        if (showRevealSide && tapped) {

            tapped = false;
            shouldTap = true;

            loadGLTexture(R.drawable.hide_side);

            showHideSide = true;
            showRevealSide = false;

            return;
        }

        if (showHideSide && tapped && sameSide) {

            tapped = false;
            shouldTap = true;

            loadGLTexture(R.drawable.two_different);

            showTapTwo = true;
            showHideSide = false;

            return;
        }

        if ((showHideSide && tapped && !sameSide)
                || (showTapTwo && tapped && !sameSide)) {

            shouldRotate = false;

            loadGLTexture(R.drawable.dissapear_hidden);

            showHideSide = false;
            showTapTwo = false;

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    dirty = true;
                    shouldShowTapAway = true;
                }
            }).start();
        }

        if (shouldShowTapAway) {

            loadGLTexture(R.drawable.cube_back);

            shouldShowTapAway = false;
            showTapAway = true;
            shouldTap = true;
            tapped = false;

            return;
        }

        if (showTapAway && tapped && allCubes) {

            loadGLTexture(R.drawable.other_cube);

            showTapAway = false;
            showOtherCube = true;
            shouldTap = true;
            tapped = false;

            return;
        }

        if (showOtherCube && tapped && !allCubes) {

            loadGLTexture(R.drawable.good_luck);

            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(mContext);

            SharedPreferences.Editor editor = sharedPrefs.edit();

            editor.putBoolean(TUTORIAL_FINISHED, true);

            editor.commit();

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ACTIVE = false;
                }
            }).start();

            return;
        }

        if (showRotateBeast) {
            loadGLTexture(R.drawable.rotate_beast);
        }

         */
    }

    synchronized void rotated() {

        if (!shouldRotate || !ACTIVE) {
            return;
        }

        shouldRotate = false;

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                rotated = true;

                dirty = true;
            }
        }).start();
    }

    /*
    synchronized void tapped() {

        Log.d("beast", "t: " + tapped + " st: " + shouldTap);

        if (!shouldTap && !(ACTIVE || COLOR_TUTORIAL)) {
            return;
        }

        shouldTap = false;

        tapped = true;

        dirty = true;
    }

     */

    void updateColorGLTexture(Mode mode, int cubesColored, boolean colorTutorialFinished) {

        if (!COLOR_TUTORIAL) {
            return;
        }

        if (mode == Mode.CUBE) {

            loadGLTexture(R.drawable.aid_color_select_cube);

            return;
        }

        if (colorTutorialFinished) {

            finishTutorial();

            return;
        }

        if (mode == COLOR_CUBE_CHOSEN) {

            if (cubesColored == 0) {

                loadGLTexture(R.drawable.aid_color_random_color);

                return;

            } else {

                loadGLTexture(R.drawable.aid_color_tap_away);

                return;
            }
        }

        /*

        if (colorMode && !cubeChoosen) {

            loadGLTexture(R.drawable.aid_color_select_cube);

            tapped = false;

            return;
        }

        if (one && cubeChoosen) {

            if (++colorTaps > 2) {

                finishTutorial();
            }

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    finishTutorial();
                }
            }).start();

            return;
        }

        if (oneChosen && tapped) {

            loadGLTexture(R.drawable.aid_color_tap_away);

            return;
        }

        if (oneChosen) {

            loadGLTexture(R.drawable.aid_color_random_color);

            shouldTap = true;
            tapped = false;

            cubeChoosen = true;

            return;
        }

         */

        loadGLTexture(R.drawable.aid_color);
    }

    private void finishTutorial() {

        if (tutorialFinished) {
            return;
        }

        loadGLTexture(R.drawable.aid_color_easier);

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putBoolean(COLOR_TUTORIAL_FINISHED, true);

        editor.commit();

        COLOR_TUTORIAL = false;

        tutorialFinished = true;
    }
}
