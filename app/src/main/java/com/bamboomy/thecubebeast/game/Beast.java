package com.bamboomy.thecubebeast.game;

import static com.bamboomy.thecubebeast.MainActivity.TAG;
import static com.bamboomy.thecubebeast.game.BeastRenderer.MATRIX_SIZE;
import static com.bamboomy.thecubebeast.game.Cube.NUMBER_OF_SIDES;

import android.opengl.Matrix;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

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

    private static final float[] ONE_OF_TWO_COLLAPSED = {
            -0.75f, 0f, 0f
    };

    private static final float[] ONE_OF_TWO_EXPANDED = {
            -1f, 0f, 0f
    };

    private static final float[] TWO_OF_TWO_COLLAPSED = {
            0.75f, 0f, 0f
    };

    private static final float[] TWO_OF_TWO_EXPANDED = {
            1f, 0f, 0f
    };

    private static final float[] ONE_OF_FOUR_COLLAPSED = {
            -0.75f, -0.75f, 0f
    };

    private static final float[] ONE_OF_FOUR_EXPANDED = {
            -1f, -1f, 0f
    };

    private static final float[] TWO_OF_FOUR_COLLAPSED = {
            -0.75f, 0.75f, 0f
    };

    private static final float[] TWO_OF_FOUR_EXPANDED = {
            -1f, 1f, 0f
    };

    private static final float[] THREE_OF_FOUR_COLLAPSED = {
            0.75f, -0.75f, 0f
    };

    private static final float[] THREE_OF_FOUR_EXPANDED = {
            1f, -1f, 0f
    };

    private static final float[] FOUR_OF_FOUR_COLLAPSED = {
            0.75f, 0.75f, 0f
    };

    private static final float[] FOUR_OF_FOUR_EXPANDED = {
            1f, 1f, 0f
    };

    private float[] depthMatrix = new float[MATRIX_SIZE];

    private Cube[] cubes = new Cube[8];

    private Cube current = null;

    private float[] mMVPMatrix;

    Beast(MotionListener motionListener, RenderActivity gameActivity, int nbOfCubes) {

        cubes = new Cube[nbOfCubes];

        for (int i = 0; i < cubes.length; i++) {
            cubes[i] = new Cube(motionListener, gameActivity);
        }

        Set<Integer> doubles = new HashSet<>();
        while (doubles.size() < GameActivity.GAME_MODE.getDoubles()) {
            doubles.add((int) (Math.random() * cubes.length));
        }

        for (Integer i : doubles) {
            Log.d(TAG, "double: " + i);
            cubes[i].setDoubleTupple(gameActivity);
        }
        Log.d(TAG, "past doubles...");

        boolean full = false;
        while (!full) {
            full = true;
            for (int i = 0; i < cubes.length; i++) {
                Log.d(TAG, "single(1st): " + i);
                Tupple tupple = cubes[i].addFirstSideToTupple(gameActivity);
                int counter = 0;
                if (tupple != null) {
                    while (cubes[(i + 1 + counter) % cubes.length].isFull()) {
                        counter++;
                    }
                    Log.d(TAG, "single(2nd): " + (i + 1 + counter) % cubes.length);
                    cubes[(i + 1 + counter) % cubes.length].addSecondSideToTupple(tupple);
                }
                full &= cubes[i].isFull() && cubes[(i + 1 + counter) % cubes.length].isFull();
            }
        }

        Matrix.setIdentityM(depthMatrix, 0);
        Matrix.translateM(depthMatrix, 0, 0, 0, 1);

        initCubes(nbOfCubes);
    }

    private void initCubes(int nbOfCubes) {

        switch (nbOfCubes){

            case 1:

                break;

            case 2:

                initCube(0, ONE_OF_TWO_COLLAPSED, ONE_OF_TWO_EXPANDED);
                initCube(1, TWO_OF_TWO_COLLAPSED, TWO_OF_TWO_EXPANDED);

                break;

            case 4:

                initCube(0, ONE_OF_FOUR_COLLAPSED, ONE_OF_FOUR_EXPANDED);
                initCube(1, TWO_OF_FOUR_COLLAPSED, TWO_OF_FOUR_EXPANDED);
                initCube(2, THREE_OF_FOUR_COLLAPSED, THREE_OF_FOUR_EXPANDED);
                initCube(3, FOUR_OF_FOUR_COLLAPSED, FOUR_OF_FOUR_EXPANDED);

                break;

            case 8:

                initCube(0, ONE_COLLAPSED, ONE_EXPANDED);
                initCube(1, TWO_COLLAPSED, TWO_EXPANDED);
                initCube(2, THREE_COLLAPSED, THREE_EXPANDED);
                initCube(3, FOUR_COLLAPSED, FOUR_EXPANDED);
                initCube(4, FIVE_COLLAPSED, FIVE_EXPANDED);
                initCube(5, SIX_COLLAPSED, SIX_EXPANDED);
                initCube(6, SEVEN_COLLAPSED, SEVEN_EXPANDED);
                initCube(7, EIGHT_COLLAPSED, EIGHT_EXPANDED);

                break;

            default:

                throw new RuntimeException("can't be");
        }
    }

    private void initCube(int i, float[] collapsed, float[] expanded) {

        float[] translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, collapsed[0], collapsed[1], collapsed[2]);
        cubes[i].setCollapsedTranslateMatrix(translateMatrix);
        translateMatrix = new float[BeastRenderer.MATRIX_SIZE];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, expanded[0], expanded[1], expanded[2]);
        cubes[i].setExpandedTranslateMatrix(translateMatrix);
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

    public HitInformation checkHit(int x, int y, int mWidth, int mHeight, boolean shouldAct) {

        HitInformation hitInformation = new HitInformation();

        Cube found = null;

        float[] depth = new float[1];
        float curDepth = 0;
        int curFace = -1, side = -1;

        for (Cube cube : cubes) {

            for (int f = 0; f < NUMBER_OF_SIDES; f++) { // Loop over all the faces
                for (int t = 0; t < 2; t++) { // Loop over the triangles per side

                    if (cube.checkTriangleHit(NUMBER_OF_SIDES * 4 * f + NUMBER_OF_SIDES * 2 * t,
                            depth, mHeight, mWidth, x, y)
                            && ((curFace == -1) || (depth[0] < curDepth))) {

                        if (!cube.getSide(f).isDestroyed()) {

                            curFace = f;
                            curDepth = depth[0];

                            found = cube;

                            side = f;

                            hitInformation = new HitInformation(true, found, found.getSide(side));

                            break;
                        }
                    }
                }
            }
        }

        if (shouldAct) {

            if (found != null && found.equals(current)) {

                found.tap(side);

            } else {

                toggleCube();
            }

            /*
            TODO: check whether current is needed...

        } else {

            hitInformation.setFound(found != null && found.equals(current));

             */
        }

        return hitInformation;
    }

    public void toggleCube() {

        if (current != null) {

            current.toggleCube();

            current = null;
        }
    }

    public HitInformation selectCube(int x, int y, int mWidth, int mHeight, boolean shouldAct) {

        Cube found = null;

        HitInformation result = new HitInformation();

        float[] depth = new float[1];
        float curDepth = 0;
        int curFace = -1, side = -1;

        for (Cube cube : cubes) {

            for (int f = 0; f < NUMBER_OF_SIDES; f++) { // Loop over all the faces
                for (int t = 0; t < 2; t++) { // Loop over the triangles per

                    if (cube.checkTriangleHit(NUMBER_OF_SIDES * 4 * f + NUMBER_OF_SIDES * 2 * t,
                            depth, mHeight, mWidth, x, y)
                            && ((curFace == -1) || (depth[0] < curDepth))) {

                        if (!cube.getSide(f).isDestroyed()) {

                            curFace = f;
                            curDepth = depth[0];

                            found = cube;

                            result = new HitInformation(true, found, found.getSide(curFace));

                            break;
                        }
                    }
                }
            }
        }

        if (found != null && shouldAct) {

            found.toggleCube();

            current = found;

        } else if (found != null) {

            current = found;
        }

        return result;
    }

    boolean rotateCurrentCube(float mYAngle, float mXAngle) {

        if (current != null) {
            current.rotate(mYAngle, mXAngle, mCoordSystemData);
        } else {
            return false;
        }

        return true;
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

    public void switchColor(Cube cube) {

        cube.switchColor();
    }

    public void switchColor(Cube cube, Side side) {

        cube.switchColor(side);
    }
}
