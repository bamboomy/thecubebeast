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
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by a162299 on 23-4-2016.
 */
public class TutorialImage {

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

    private static final float[] vertices = {
            // X, Y, Z, W
            -2.5f, 0.0f, -1.0f, 1.0f,
            -1.0f, 0.0f, -1.0f, 1.0f,
            -1.0f, 0.75f, -1.0f, 1.0f,
            -2.5f, 0.0f, -1.0f, 1.0f,
            -1.0f, 0.75f, -1.0f, 1.0f,
            -2.5f, 0.75f, -1.0f, 1.0f, };

    private static final int TEXTURE_COORD_DATA_SIZE = 2;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 4 * FLOAT_SIZE_BYTES;
    private boolean hidden = false;

    TutorialImage(){

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
    void loadGLTexture(Context context, int prentje) {

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
     *  @param maPositionHandle2
     * @param muMVPMatrixHandle2
     * @param mMVPMatrix
     * @param maColorHandle
     */
    public void draw(int mTextureCoordinateHandle, int maPositionHandle2,
                     int muMVPMatrixHandle2, float[] mMVPMatrix, int maColorHandle) {

        if(hidden){
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
}
