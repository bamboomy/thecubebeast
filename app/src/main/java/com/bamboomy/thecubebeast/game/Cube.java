package com.bamboomy.thecubebeast.game;

import static com.bamboomy.thecubebeast.game.BeastRenderer.FLOAT_SIZE_BYTES;
import static com.bamboomy.thecubebeast.game.BeastRenderer.MATRIX_SIZE;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class Cube {

    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 4 * FLOAT_SIZE_BYTES;
    private static final int TEXTURE_COORD_DATA_SIZE = 2;
    static final int NUMBER_OF_SIDES = 6;
    static final float SMALL_NUMBER = 1.0e-5f;

    private Side[] sides = new Side[6];
    private float[] translateMatrix, collapsedTranslateMatrix, expandedTranslateMatrix;
    private float[] mMVPMatrix = new float[MATRIX_SIZE];
    private float[] myRotationMatrux = new float[MATRIX_SIZE];

    private float[] mTriangleVerticesData;

    private final FloatBuffer mCubeTextureCoordinates;

    private static final float[] FRONT_COLLAPSED = {
            // X, Y, Z, W
            // Front face
            0.5f, -0.5f, -0.5f, 1,
            -0.5f, -0.5f, -0.5f, 1,
            -0.5f, 0.5f, -0.5f, 1,
            0.5f, -0.5f, -0.5f, 1,
            -0.5f, 0.5f, -0.5f, 1,
            0.5f, 0.5f, -0.5f, 1,};

    private static final float[] BACK_COLLAPSED = {
            // X, Y, Z, W
            // Back face
            -0.5f, -0.5f, 0.5f, 1,
            0.5f, -0.5f, 0.5f, 1,
            0.5f, 0.5f, 0.5f, 1,
            -0.5f, -0.5f, 0.5f, 1,
            0.5f, 0.5f, 0.5f, 1,
            -0.5f, 0.5f, 0.5f, 1,};

    private static final float[] LEFT_COLLAPSED = {
            // X, Y, Z, W
            // Left face
            0.5f, -0.5f, -0.5f, 1,
            0.5f, 0.5f, -0.5f, 1,
            0.5f, 0.5f, 0.5f, 1,
            0.5f, -0.5f, -0.5f, 1,
            0.5f, 0.5f, 0.5f, 1,
            0.5f, -0.5f, 0.5f, 1,};

    private static final float[] RIGHT_COLLAPSED = {
            // X, Y, Z, W
            // Right face
            -0.5f, 0.5f, -0.5f, 1,
            -0.5f, -0.5f, -0.5f, 1,
            -0.5f, -0.5f, 0.5f, 1,
            -0.5f, 0.5f, -0.5f, 1,
            -0.5f, -0.5f, 0.5f, 1,
            -0.5f, 0.5f, 0.5f, 1,};

    private static final float[] TOP_COLLAPSED = {
            // X, Y, Z, W
            // Top face
            -0.5f, 0.5f, -0.5f, 1,
            -0.5f, 0.5f, 0.5f, 1,
            0.5f, 0.5f, 0.5f, 1,
            -0.5f, 0.5f, -0.5f, 1,
            0.5f, 0.5f, 0.5f, 1,
            0.5f, 0.5f, -0.5f, 1,};

    private static final float[] BOTTOM_COLLAPSED = {
            // X, Y, Z, W
            // Bottom face
            -0.5f, -0.5f, 0.5f, 1,
            -0.5f, -0.5f, -0.5f, 1,
            0.5f, -0.5f, -0.5f, 1,
            -0.5f, -0.5f, 0.5f, 1,
            0.5f, -0.5f, -0.5f, 1,
            0.5f, -0.5f, 0.5f, 1};

    // S, T (or X, Y)
    // Texture coordinate data.
    // Because images have a Y axis pointing downward (values increase as you
    // move down the image) while
    // OpenGL has a Y axis pointing upward, we adjust for that here by flipping
    // the Y axis.
    // What's more is that the texture coordinates are the same for every face.
    private static final float[] CUBE_TEXTURE_COORD_DATA = {
            // Front face
            0.0f, 1,
            1, 1,
            1, 0.0f,
            0.0f, 1,
            1, 0.0f,
            0.0f, 0.0f,

            // Right face
            0.0f, 1,
            1, 1,
            1, 0.0f,
            0.0f, 1,
            1, 0.0f,
            0.0f, 0.0f,

            // Back face
            0.0f, 1,
            1, 1,
            1, 0.0f,
            0.0f, 1,
            1, 0.0f,
            0.0f, 0.0f,

            // Left face
            0.0f, 1,
            1, 1,
            1, 0.0f,
            0.0f, 1,
            1, 0.0f,
            0.0f, 0.0f,

            // Top face
            0.0f, 1,
            1, 1,
            1, 0.0f,
            0.0f, 1,
            1, 0.0f,
            0.0f, 0.0f,

            // Bottom face
            0.0f, 1,
            1, 1,
            1, 0.0f,
            0.0f, 1,
            1, 0.0f,
            0.0f, 0.0f,};


    private float[] front, back, left, right, top, bottom;

    private FloatBuffer mTriangleVertices, colorBuf;

    private MotionListener motionListener;

    private float[] oneCubeFullOfColor;

    {
        float one = 1f;

        oneCubeFullOfColor = new float[36 * 4];

        int counter = 0;

        for (int i = 0; i < 36; i++) {

            oneCubeFullOfColor[counter++] = one;
            oneCubeFullOfColor[counter++] = one;
            oneCubeFullOfColor[counter++] = one;
            oneCubeFullOfColor[counter++] = 0.25f;
        }
    }

    Cube(MotionListener motionListener, GameActivity gameActivity) {

        for (int i = 0; i < sides.length; i++) {

            sides[i] = new Side(gameActivity);
        }

        init();

        mCubeTextureCoordinates = ByteBuffer
                .allocateDirect(CUBE_TEXTURE_COORD_DATA.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(CUBE_TEXTURE_COORD_DATA).position(0);

        Matrix.setIdentityM(myRotationMatrux, 0);

        this.motionListener = motionListener;

        colorBuf = ByteBuffer.allocateDirect(oneCubeFullOfColor.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuf.put(oneCubeFullOfColor).position(0);
    }

    private void init() {

        front = FRONT_COLLAPSED;
        back = BACK_COLLAPSED;
        left = LEFT_COLLAPSED;
        right = RIGHT_COLLAPSED;
        top = TOP_COLLAPSED;
        bottom = BOTTOM_COLLAPSED;

        refresh();
    }

    private void refresh() {

        mTriangleVerticesData = new float[back.length * NUMBER_OF_SIDES];

        for (int i = 0; i < front.length; i++) {
            mTriangleVerticesData[i] = front[i];
        }

        int shift = front.length;

        for (int i = 0; i < back.length; i++) {
            mTriangleVerticesData[i + shift] = back[i];
        }

        shift += back.length;

        for (int i = 0; i < left.length; i++) {
            mTriangleVerticesData[i + shift] = left[i];
        }

        shift += left.length;

        for (int i = 0; i < right.length; i++) {
            mTriangleVerticesData[i + shift] = right[i];
        }

        shift += right.length;

        for (int i = 0; i < top.length; i++) {
            mTriangleVerticesData[i + shift] = top[i];
        }

        shift += top.length;

        for (int i = 0; i < bottom.length; i++) {
            mTriangleVerticesData[i + shift] = bottom[i];
        }

        mTriangleVertices = ByteBuffer
                .allocateDirect(
                        mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    float[] updatePosition(float[] mCoordSystemData, float[] mRotMatrix, float[] depthMatrix, float[] mVMatrix, float[] mProjMatrix) {

        float[] tempMatrix = new float[MATRIX_SIZE];
        float[] temp2Matrix = new float[MATRIX_SIZE];

        Matrix.multiplyMM(tempMatrix, 0, myRotationMatrux, 0, mCoordSystemData, 0);

        Matrix.invertM(temp2Matrix, 0, mRotMatrix, 0);
        Matrix.multiplyMM(tempMatrix, 0, temp2Matrix, 0, tempMatrix, 0);

        Matrix.multiplyMM(tempMatrix, 0, translateMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(tempMatrix, 0, mRotMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(tempMatrix, 0, depthMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        return mVMatrix;
    }

    void setCollapsedTranslateMatrix(float[] translateMatrix) {
        this.translateMatrix = translateMatrix;
        collapsedTranslateMatrix = translateMatrix;
    }

    void setExpandedTranslateMatrix(float[] translateMatrix) {
        expandedTranslateMatrix = translateMatrix;
    }

    void draw(int maPositionHandle, int muMVPMatrixHandle, int mTextureCoordinateHandle, int maColorHandle) {

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 4, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        int i = 0;

        // draw the sides of the cube separately
        for (Side side : sides) {

            if (side.isDestroyed()) {

                i++;

                continue;
            }

            // Set the position for Color
            colorBuf.position(0);
            GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
                    0, colorBuf);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maColorHandle);
            checkGlError("glEnableAttribPointer maTextureHandle");

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, side.getTexture());
            mCubeTextureCoordinates.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXTURE_COORD_DATA_SIZE, GLES20.GL_FLOAT,
                    false, 0, mCubeTextureCoordinates);
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, i * NUMBER_OF_SIDES, NUMBER_OF_SIDES);

            i++;
        }
    }

    void initTextures() {

        for (Side side : sides) {

            side.initTextures();
        }
    }

    boolean checkTriangleHit(int vertexDataOffset, float[] depth, int mHeight, int mWidth, int x, int y) {

        float[] vertexData = mTriangleVerticesData;

        // This function checks whether the passed x and y coordinates hit the
        // triangle
        // passed as argument. If so, it returns true and updates the depth of
        // the hit.
        // Else, it returns false
        float[] fMVPVector = new float[12];

        for (int v = 0; v < 3; v++) {
            // Project the 3 vertices of the triangle using the Model, View and
            // Projection matrix
            Matrix.multiplyMV(fMVPVector, v * 4, mMVPMatrix, 0,
                    vertexData, vertexDataOffset + v * 4);

            // Perspective division
            fMVPVector[4 * v + 0] /= fMVPVector[4 * v + 3];
            fMVPVector[4 * v + 1] /= fMVPVector[4 * v + 3];
            fMVPVector[4 * v + 2] /= fMVPVector[4 * v + 3];
            fMVPVector[4 * v + 3] /= fMVPVector[4 * v + 3];

            // Convert x, y to screen coordinates
            fMVPVector[4 * v + 0] = (fMVPVector[4 * v + 0] + 1) * mWidth / 2;
            fMVPVector[4 * v + 1] = (1 - fMVPVector[4 * v + 1]) * mHeight / 2;

        }

        // Now consider only x and y coordinates and figure out whether the
        // click is in the projected view
        // Transformation matrix for an affine coordinate system
        float[] fTMatrix = new float[4];
        fTMatrix[0] = fMVPVector[4] - fMVPVector[0]; // V1x - v0x
        fTMatrix[2] = fMVPVector[5] - fMVPVector[1]; // V1y - v0y
        fTMatrix[1] = fMVPVector[8] - fMVPVector[0]; // V2x - v0x
        fTMatrix[3] = fMVPVector[9] - fMVPVector[1]; // V2y - v0y

        float det = fTMatrix[0] * fTMatrix[3] - fTMatrix[1] * fTMatrix[2];
        if (Math.abs(det) < SMALL_NUMBER) {
            return false;
        }

        // Invert now the matrix
        float[] fTInvMatrix = new float[4];
        fTInvMatrix[0] = fTMatrix[3] / det;
        fTInvMatrix[1] = -fTMatrix[1] / det;
        fTInvMatrix[2] = -fTMatrix[2] / det;
        fTInvMatrix[3] = fTMatrix[0] / det;

        // Move the touch event fit in the new coordinate sytem
        float[] touchVector = new float[2];
        touchVector[0] = x - fMVPVector[0];
        touchVector[1] = y - fMVPVector[1];

        // Calculate the affine coordinates of the point
        float[] touchAffVector = new float[2];
        touchAffVector[0] = fTInvMatrix[0] * touchVector[0] + fTInvMatrix[1]
                * touchVector[1];
        touchAffVector[1] = fTInvMatrix[2] * touchVector[0] + fTInvMatrix[3]
                * touchVector[1];

        // The new x and y coordinates must be positive and less than 1.
        if ((touchAffVector[0] < 0.0f) || (touchAffVector[0] > 1)) {
            return false;
        }

        if ((touchAffVector[1] < 0.0f) || (touchAffVector[1] > 1)) {
            return false;
        }

        // The vector v2-v1 is the diagonal of the spanning parallelogram
        // Check that the point lies beneath the diagonal of the parallelogram
        if (touchAffVector[0] + touchAffVector[1] - 1 > 0.0f) {
            return false;
        }

        // Calculate now the depth of the touch event in the current triangle
        depth[0] = touchAffVector[0] * (fMVPVector[6] - fMVPVector[2])
                + touchAffVector[1] * (fMVPVector[10] - fMVPVector[2])
                + fMVPVector[2];
        return true;
    }

    void toggleCube() {

        if (translateMatrix.equals(collapsedTranslateMatrix)) {

            translateMatrix = expandedTranslateMatrix;

        } else if (translateMatrix.equals(expandedTranslateMatrix)) {

            translateMatrix = collapsedTranslateMatrix;

        } else {
            throw new RuntimeException("something's wrong here...");
        }
    }

    void rotate(float mYAngle, float mXAngle, float[] mCoordSystemData) {

        // First calculate the MVP matrix (Model, View, Projection)
        float[] rotMatrix = new float[MATRIX_SIZE];
        float[] tempRotMatrix = new float[MATRIX_SIZE];

        // Rotate the coordinate system of the cube around y axis
        Matrix.setRotateM(rotMatrix, 0, mYAngle, 0, 1, 0);
        Matrix.multiplyMM(tempRotMatrix, 0, rotMatrix, 0, myRotationMatrux, 0);

        // Rotate the coordinate system of the cube around x axis
        Matrix.setRotateM(rotMatrix, 0, mXAngle, 1, 0, 0);
        Matrix.multiplyMM(myRotationMatrux, 0, rotMatrix, 0, tempRotMatrix, 0);
    }

    boolean tap(int side) {

        return sides[side].tap();
    }

    void clean() {

        for (final Side side : sides) {

            side.clean();
        }
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("beast", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    void switchColor() {

        float red = (float) Math.random();
        float green = (float) Math.random();
        float blue = (float) Math.random();

        //oneCubeFullOfColor = new float[36 * 4];

        int counter = 0;

        for (int i = 0; i < 36; i++) {

            oneCubeFullOfColor[counter++] = red;
            oneCubeFullOfColor[counter++] = green;
            oneCubeFullOfColor[counter++] = blue;
            oneCubeFullOfColor[counter++] = 0.25f;
        }

        colorBuf = ByteBuffer.allocateDirect(oneCubeFullOfColor.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuf.put(oneCubeFullOfColor).position(0);
    }

    Side getSide(int index) {
        return sides[index];
    }

    public void switchColor(Side side) {

        float red = (float) Math.random();
        float green = (float) Math.random();
        float blue = (float) Math.random();

        int counter = 0;

        for (Side sideI : sides) {

            if (sideI == side) {

                for (int i = 0; i < 6; i++) {

                    oneCubeFullOfColor[counter++] = red;
                    oneCubeFullOfColor[counter++] = green;
                    oneCubeFullOfColor[counter++] = blue;
                    oneCubeFullOfColor[counter++] = 0.25f;
                }

                break;
            }

            counter += 24;
        }

        colorBuf = ByteBuffer.allocateDirect(oneCubeFullOfColor.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuf.put(oneCubeFullOfColor).position(0);
    }

    public void setDoubleTupple() {

        int i = (int) (Math.random() * sides.length);
        int j = (int) (Math.random() * sides.length);
        while (i == j) {
            j = (int) (Math.random() * sides.length);
        }

        Tupple tupple = GameMaster.getInstance().getDoubleTupple(sides[i], sides[j]);

        sides[i].setTupple(tupple);
        sides[j].setTupple(tupple);
    }

    public void addFirstSideToTupple() {
    }

    public void addSecondSideToTupple() {
    }

    public boolean isFull() {
        boolean full = true;
        for (int i = 0; i < sides.length; i++) {
            sides[i]
        }
    }
}
