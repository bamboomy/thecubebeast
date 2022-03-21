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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by a162299 on 23-4-2016.
 */
public class ChoiceImage {

    private FloatBuffer textureBuffer; // buffer holding the texture coordinates

    private float texture[] = {
            // Mapping coordinates for the vertices
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    private final float[] complyingWithShader = {

            1.0f, 1.0f, 1.0f, 1.0f, // zhite
            1.0f, 1.0f, 1.0f, 1.0f, // zhite
            1.0f, 1.0f, 1.0f, 1.0f, // zhite
            1.0f, 1.0f, 1.0f, 1.0f, // zhite
            1.0f, 1.0f, 1.0f, 1.0f, // zhite
            1.0f, 1.0f, 1.0f, 1.0f, // zhite
    };


    /**
     * The texture pointer
     */
    private int[] textures = new int[1];

    private FloatBuffer vertexBuffer, colorBuffer; // buffer holding the vertices

    private static final float x1 = -2.25f, x2 = -1.75f, y1 = 0.75f, y2 = 1.25f, z = -1.2f;

    private static final float[] vertices = {
            // X, Y, Z, W
            x1, y1, z, 1.0f,
            x2, y1, z, 1.0f,
            x2, y2, z, 1.0f,
            x1, y1, z, 1.0f,
            x2, y2, z, 1.0f,
            x1, y2, z, 1.0f,};

    private static final int TEXTURE_COORD_DATA_SIZE = 2;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 4 * FLOAT_SIZE_BYTES;
    private boolean hidden = true;

    ChoiceImage() {

        // a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        // allocates the memory from the byte buffer
        vertexBuffer = byteBuffer.asFloatBuffer();

        // fill the vertexBuffer with the vertices
        vertexBuffer.put(vertices);

        // set the cursor position to the beginning of the buffer
        vertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);

        colorBuffer = ByteBuffer.allocateDirect(complyingWithShader.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(complyingWithShader).position(0);
    }

    /**
     * Load the texture for the square
     *
     * @param context
     */
    public void loadGLTexture(Context context, int prentje) {

        // loading texture
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                prentje);

        GLES20.glDeleteTextures(1, textures, 0);
        // generate one texture pointer
        GLES20.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        // create nearest filtered texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        // Use Android GLUtils to specify a two-dimensional texture image from
        // our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();
    }

    /**
     * The draw method for the square with the GL context
     *
     * @param maPositionHandle2
     * @param muMVPMatrixHandle2
     * @param mMVPMatrix
     * @param maColorHandle
     */
    public void draw(int mTextureCoordinateHandle, int maPositionHandle2,
                     int muMVPMatrixHandle2, float[] mMVPMatrix, int maColorHandle) {

        if (hidden) {
            return;
        }

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(maPositionHandle2, 4, GLES20.GL_FLOAT,
                false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
        checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(maPositionHandle2);
        checkGlError("glEnableVertexAttribArray");

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle2, 1, false,
                mMVPMatrix, 0);
        checkGlError("glUniformMatrix4fv");

        // Set the position for Color
        colorBuffer.position(0);
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
                0, colorBuffer);
        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maColorHandle);
        checkGlError("glEnableAttribPointer maTextureHandle");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        checkGlError("glBindTexture");

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
                TEXTURE_COORD_DATA_SIZE, GLES20.GL_FLOAT, false, 0,
                textureBuffer);
        checkGlError("glVertexAttribPointer");

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        checkGlError("glEnableVertexAttribArray");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 4);
        checkGlError("glDrawArrays");
    }

    private void checkGlError(String op) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("ii", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    boolean checkTriangleHit(int vertexDataOffset, float[] depth,
                             int mHeight, int mWidth, int x, int y,
                             float[] mMVPMatrix) {

        if (hidden) {
            return false;
        }

        float[] vertexData = vertices;

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
        if (Math.abs(det) < Cube.SMALL_NUMBER) {
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

    void show() {

        hidden = false;
    }

    void hide() {

        hidden = true;
    }
}
