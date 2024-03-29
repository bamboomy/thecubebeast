package com.bamboomy.thecubebeast.game;

import static com.bamboomy.thecubebeast.game.BeastRenderer.MATRIX_SIZE;
import static com.bamboomy.thecubebeast.game.Cube.NUMBER_OF_SIDES;

import android.opengl.Matrix;

public class Beast {

    // Holds the final rotation of the cube
    private float[] mCoordSystemData = {
            // x, y, z axis, fourth column is useless, but is there
            // to be able to use the multiplyMM function
            1, 0.0f, 0.0f, 0.0f,
            0.0f, 1, 0.0f, 0.0f,
            0.0f, 0.0f, 1, 0.0f,
            0.0f, 0.0f, 0.0f, 1};

    private static final float[] ONE_COLLAPSED = {
            -0.75f, -0.75f, -0.75f
    };

    private static final float[] ONE_EXPANDED = {
            -1f, -1f, -1f
    };

    private static final float[] TWO_COLLAPSED = {
            -0.75f, 0.75f, -0.75f
    };

    private static final float[] TWO_EXPANDED = {
            -1f, 1f, -1f
    };

    private static final float[] THREE_COLLAPSED = {
            -0.75f, -0.75f, 0.75f
    };

    private static final float[] THREE_EXPANDED = {
            -1f, -1f, 1f
    };

    private static final float[] FOUR_COLLAPSED = {
            -0.75f, 0.75f, 0.75f
    };

    private static final float[] FOUR_EXPANDED = {
            -1f, 1f, 1f
    };

    private static final float[] FIVE_COLLAPSED = {
            0.75f, -0.75f, -0.75f
    };

    private static final float[] FIVE_EXPANDED = {
            1f, -1f, -1f
    };

    private static final float[] SIX_COLLAPSED = {
            0.75f, 0.75f, -0.75f
    };

    private static final float[] SIX_EXPANDED = {
            1f, 1f, -1f
    };

    private static final float[] SEVEN_COLLAPSED = {
            0.75f, -0.75f, 0.75f
    };

    private static final float[] SEVEN_EXPANDED = {
            1f, -1f, 1f
    };

    private static final float[] EIGHT_COLLAPSED = {
            0.75f, 0.75f, 0.75f
    };

    private static final float[] EIGHT_EXPANDED = {
            1f, 1f, 1f
    };

    private float[] depthMatrix = new float[MATRIX_SIZE];

    private Cube[] cubes = new Cube[8];

    private Cube current = null;

    private float[] mMVPMatrix;

    private int[] sides = new int[2];

    Beast(Pictures pictures, MotionListener motionListener, GameActivity gameActivity) {

        for (int i = 0; i < cubes.length / 2; i++) {
            cubes[i] = new Cube(pictures, motionListener, gameActivity, true);
        }

        for (int i = cubes.length / 2; i < cubes.length; i++) {
            cubes[i] = new Cube(pictures, motionListener, gameActivity, false);
        }

        Matrix.setIdentityM(depthMatrix, 0);
        Matrix.translateM(depthMatrix, 0, 0, 0, 1);

        initCubes();

        sides[0] = -1;
        sides[1] = -1;
    }

    private void initCubes() {

        float[] translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, ONE_COLLAPSED[0], ONE_COLLAPSED[1], ONE_COLLAPSED[2]);
        cubes[0].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, ONE_EXPANDED[0], ONE_EXPANDED[1], ONE_EXPANDED[2]);
        cubes[0].setExpandedTranslateMatrix(translateMatrix);

        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, TWO_COLLAPSED[0], TWO_COLLAPSED[1], TWO_COLLAPSED[2]);
        cubes[1].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, TWO_EXPANDED[0], TWO_EXPANDED[1], TWO_EXPANDED[2]);
        cubes[1].setExpandedTranslateMatrix(translateMatrix);

        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, THREE_COLLAPSED[0], THREE_COLLAPSED[1], THREE_COLLAPSED[2]);
        cubes[2].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, THREE_EXPANDED[0], THREE_EXPANDED[1], THREE_EXPANDED[2]);
        cubes[2].setExpandedTranslateMatrix(translateMatrix);

        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, FOUR_COLLAPSED[0], FOUR_COLLAPSED[1], FOUR_COLLAPSED[2]);
        cubes[3].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, FOUR_EXPANDED[0], FOUR_EXPANDED[1], FOUR_EXPANDED[2]);
        cubes[3].setExpandedTranslateMatrix(translateMatrix);

        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, FIVE_COLLAPSED[0], FIVE_COLLAPSED[1], FIVE_COLLAPSED[2]);
        cubes[4].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, FIVE_EXPANDED[0], FIVE_EXPANDED[1], FIVE_EXPANDED[2]);
        cubes[4].setExpandedTranslateMatrix(translateMatrix);

        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, SIX_COLLAPSED[0], SIX_COLLAPSED[1], SIX_COLLAPSED[2]);
        cubes[5].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, SIX_EXPANDED[0], SIX_EXPANDED[1], SIX_EXPANDED[2]);
        cubes[5].setExpandedTranslateMatrix(translateMatrix);

        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, SEVEN_COLLAPSED[0], SEVEN_COLLAPSED[1], SEVEN_COLLAPSED[2]);
        cubes[6].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, SEVEN_EXPANDED[0], SEVEN_EXPANDED[1], SEVEN_EXPANDED[2]);
        cubes[6].setExpandedTranslateMatrix(translateMatrix);

        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, EIGHT_COLLAPSED[0], EIGHT_COLLAPSED[1], EIGHT_COLLAPSED[2]);
        cubes[7].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, EIGHT_EXPANDED[0], EIGHT_EXPANDED[1], EIGHT_EXPANDED[2]);
        cubes[7].setExpandedTranslateMatrix(translateMatrix);
    }

    void initTextures() {

        for (Cube cube : cubes) {

            cube.initTextures();
        }
    }

    float[] updatePosition(float[] mRotMatrix, float[] mVMatrix, float[] mProjMatrix) {

        mMVPMatrix = new float[16];

        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mCoordSystemData, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        for (Cube cube : cubes) {

            cube.updatePosition(mCoordSystemData, mRotMatrix, depthMatrix, mVMatrix, mProjMatrix);
        }

        return mMVPMatrix;
    }

    void draw(int maPositionHandle, int muMVPMatrixHandle, int mTextureCoordinateHandle, int maColorHandle) {

        for (Cube cube : cubes) {

            cube.draw(maPositionHandle, muMVPMatrixHandle, mTextureCoordinateHandle, maColorHandle);
        }
    }

    public boolean[] checkHit(int x, int y, int mWidth, int mHeight, boolean shouldAct) {

        boolean[] flags = new boolean[2];

        flags[0] = flags[1] = false;

        Cube found = null;

        float[] depth = new float[1];
        float curDepth = 0;
        int curFace = -1, side = -1;

        for (Cube cube : cubes) {

            for (int f = 0; f < NUMBER_OF_SIDES; f++) { // Loop over all the faces
                for (int t = 0; t < 2; t++) { // Loop over the triangles per

                    if (cube.checkTriangleHit(NUMBER_OF_SIDES * 4 * f + NUMBER_OF_SIDES * 2 * t,
                            depth, mHeight, mWidth, x, y)
                            && ((curFace == -1) || (depth[0] < curDepth))) {

                        curFace = f;
                        curDepth = depth[0];

                        found = cube;

                        side = f;

                        pushAndHide(side);
                    }
                }
            }
        }

        if (shouldAct) {

            if (found != null && found.equals(current)) {

                flags[0] = true;
                flags[1] = found.tap(side);

                return flags;

            } else if (current != null) {

                current.toggleCube();

                current = null;
            }

        } else {

            flags[0] = found != null && found.equals(current);
        }

        return flags;
    }

    private void pushAndHide(int side) {

        if (sides[0] == -1) {

            sides[0] = side;

        } else {

            if (sides[1] != -1) {

                hideSides();

            } else if (sides[0] != side) {

                sides[1] = side;
            }
        }

    }

    private void hideSides() {
    }

    public boolean selectCube(int x, int y, int mWidth, int mHeight, boolean shouldAct) {

        Cube found = null;

        float[] depth = new float[1];
        float curDepth = 0;
        int curFace = -1, side = -1;

        for (Cube cube : cubes) {

            for (int f = 0; f < NUMBER_OF_SIDES; f++) { // Loop over all the faces
                for (int t = 0; t < 2; t++) { // Loop over the triangles per

                    if (cube.checkTriangleHit(NUMBER_OF_SIDES * 4 * f + NUMBER_OF_SIDES * 2 * t,
                            depth, mHeight, mWidth, x, y)
                            && ((curFace == -1) || (depth[0] < curDepth))) {

                        curFace = f;
                        curDepth = depth[0];

                        found = cube;
                    }
                }
            }
        }

        if (found != null && shouldAct) {

            found.toggleCube();

            current = found;

            return true;

        } else if (found != null) {

            current = found;

            return true;
        }

        return false;
    }

    void rotateCurrentCube(float mYAngle, float mXAngle) {

        current.rotate(mYAngle, mXAngle, mCoordSystemData);
    }

    void clean() {

        for (Cube cube : cubes) {

            cube.clean();
        }
    }

    public void rotateAllCubes(float mYAngle, float mXAngle) {

        for (Cube cube : cubes) {

            cube.rotate(mYAngle, mXAngle, mCoordSystemData);
        }
    }

    public void switchColorCurrentCube() {

        current.switchColor();
    }
}
