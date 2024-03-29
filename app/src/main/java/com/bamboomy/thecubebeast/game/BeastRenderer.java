package com.bamboomy.thecubebeast.game;

import static com.bamboomy.thecubebeast.game.TutorialManager.COLOR_TUTORIAL_FINISHED;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.preference.PreferenceManager;

import com.bamboomy.thecubebeast.R;
import com.bamboomy.thecubebeast.util.TimeToString;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ri.blog.opengl008.TextManager;
import ri.blog.opengl008.TextObject;
import ri.blog.opengl008.riGraphicTools;

public class BeastRenderer implements GLSurfaceView.Renderer {

    static final int MATRIX_SIZE = 16;
    static final int FLOAT_SIZE_BYTES = 4;

    //open gl programs
    private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n"
                    + "attribute vec4 aPosition;\n"
                    + "attribute vec2 aTextureCoord;\n"
                    + "attribute vec4 a_Color;"
                    + "varying vec2 vTextureCoord;\n"
                    + "varying vec4 v_Color;"
                    + "void main() {\n"
                    + "  gl_Position = uMVPMatrix * aPosition;\n"
                    + "  vTextureCoord = aTextureCoord;\n"
                    + "  v_Color = a_Color;"
                    + "}\n";

    private final String mFragmentShader =
            "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "varying vec4 v_Color;"
                    + "uniform sampler2D sTexture;\n"
                    + "void main() {\n"
                    + "  gl_FragColor = texture2D(sTexture, vTextureCoord) * v_Color;\n"
                    + "}\n";

    //open gl variables
    private int mProgram, maPositionHandle, muMVPMatrixHandle, mTextureCoordinateHandle, maColorHandle;

    private int mWidth, mHeight;

    private float[] mVMatrix = new float[MATRIX_SIZE], mProjMatrix = new float[MATRIX_SIZE];

    private Beast beast;

    private int mCollision = 0, mRotate = 1;

    private float mXAngle = 0, mYAngle = 0;

    private float[] mRotMatrix = new float[MATRIX_SIZE];

    private int x, y;

    private static final String ALL = "all";
    private static final String ONE = "one";
    private static final String COLOR = "COLOR";
    private static final String COLOR_COBE_CHOOSEN = "COLOR_COBE_CHOOSEN";

    private String mode = ALL;

    private GameActivity activity;

    private TutorialManager tutorialManager;

    private float[] tutorialMatrix = null;
    private boolean sameSide = false;

    private ColorImage colorImage = new ColorImage();

    private float[] mMVPMatrix;

    // Holds the final rotation of the cube
    private float[] mCoordSystemData = {
            // x, y, z axis, fourth column is useless, but is there
            // to be able to use the multiplyMM function
            1, 0.0f, 0.0f, 0.0f,
            0.0f, 1, 0.0f, 0.0f,
            0.0f, 0.0f, 1, 0.0f,
            0.0f, 0.0f, 0.0f, 1};

    private boolean shouldShowColor = false;

    // text

    private TextManager mTextManagerStatus;
    private TextObject mTextTapTitle;
    private TextObject mTextTaps;
    private TextObject mTextTimeTitle;
    private TextObject mTextTime;

    private int mTextProgram;

    public static final String mTextVertexShader = "uniform mat4 uMVPMatrix;"
            + "attribute vec4 vPosition;" + "attribute vec4 a_Color;"
            + "attribute vec2 a_texCoord;" + "varying vec4 v_Color;"
            + "varying vec2 v_texCoord;" + "void main() {"
            + "  gl_Position = uMVPMatrix * vPosition;"
            + "  v_texCoord = a_texCoord;" + "  v_Color = a_Color;" + "}";

    public static final String mTextFragmentShader = "precision mediump float;"
            + "varying vec4 v_Color;" + "varying vec2 v_texCoord;"
            + "uniform sampler2D s_texture;" + "void main() {"
            + "  gl_FragColor = texture2D( s_texture, v_texCoord ) * v_Color;"
            + "  gl_FragColor.rgb *= v_Color.a;" + "}";

    private int mTextureID;

    private float mTextProjectionMatrix[] = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

    private int taps = 0;

    private long begin = System.currentTimeMillis();
    ;
    private String timeText = "00:00.00";

    private MotionListener motionListener;


    BeastRenderer(Pictures pictures, MotionListener motionListener, GameActivity gameActivity) {

        beast = new Beast(pictures, motionListener, gameActivity);

        Matrix.setIdentityM(mRotMatrix, 0);

        this.activity = gameActivity;

        tutorialManager = new TutorialManager(activity);

        this.motionListener = motionListener;

        textSetupManagers(true, true);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mProgram = createProgram(mVertexShader, mFragmentShader, true);

        if (mProgram == 0) {
            return;
        }

        beast.initTextures();

        colorImage.loadGLTexture(activity, R.drawable.color);

        //TODO: rethink this
        Matrix.setLookAtM(mVMatrix, 0, 0, 0,
                -5,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);

        mMVPMatrix = new float[16];

        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mCoordSystemData, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        mTextProgram = createProgram(mTextVertexShader, mTextFragmentShader, false);
        // This is ugly, but the TextManager does not allow to pass the
        // programID to be used for rendering.
        riGraphicTools.sp_Text = mTextProgram;

        int[] textures = new int[2];

        GLES20.glDeleteTextures(2, textures, 0);

        GLES20.glGenTextures(2, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        textSetupFontTexture(textures[1]);
    }

    private void textSetupFontTexture(int hTexture) {
        int id = activity.getResources().getIdentifier("drawable/font", null,
                activity.getPackageName());
        // Log.i(TAG, "Load font bitmap. Id: " + id);
        Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), id);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, hTexture);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        mWidth = width;
        mHeight = height;
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        setGLParameters();

        processInterface();

        drawBeast();

        if (Tupple.popIsOneSolved()) {

            activity.raw();
        }

        tutorialManager.updateGLTexture(sameSide, mode.equalsIgnoreCase(ALL));

        tutorialManager.updateColorGLTexture(mode.equalsIgnoreCase(COLOR),
                mode.equalsIgnoreCase(COLOR_COBE_CHOOSEN), mode.equalsIgnoreCase(ALL));

        tutorialManager.draw(mTextureCoordinateHandle, maPositionHandle, muMVPMatrixHandle, tutorialMatrix, maColorHandle);

        colorImage.draw(mTextureCoordinateHandle, maPositionHandle, muMVPMatrixHandle, tutorialMatrix, maColorHandle);

        updateTimeText();

        renderText();
    }

    private void setGLParameters() {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Use Z-buffering for occlusion
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glUseProgram(mProgram);
    }

    private void processInterface() {

        if (mRotate == 1) {
            updateRotMatrix();
            mRotate = 0;
        }

        if (mCollision == 1) {

            if (mode.equalsIgnoreCase(ALL)) {

                if (colorImage.checkTriangleHit(0, new float[1],
                        mHeight, mWidth, x, y, mMVPMatrix)) {

                    mode = COLOR;
                }

                if (beast.selectCube(x, y, mWidth, mHeight, true)) {

                    mode = ONE;

                    colorImage.hide();
                }

            } else if (mode.equalsIgnoreCase(ONE)) {

                boolean[] flags = beast.checkHit(x, y, mWidth, mHeight, true);

                boolean cubeHit = flags[0];
                sameSide = flags[1];

                if (!cubeHit) {

                    mode = ALL;

                    if (shouldShowColor) {
                        colorImage.show();
                    }

                } else {

                    taps++;
                }

            } else if (mode.equalsIgnoreCase(COLOR)) {

                if (beast.selectCube(x, y, mWidth, mHeight, false)) {

                    beast.switchColorCurrentCube();

                    mode = COLOR_COBE_CHOOSEN;
                }

            } else if (mode.equalsIgnoreCase(COLOR_COBE_CHOOSEN)) {

                boolean[] flags = beast.checkHit(x, y, mWidth, mHeight, false);

                if (flags[0]) {

                    beast.switchColorCurrentCube();

                } else {

                    mode = ALL;
                }
            }

            mCollision = 0;

            if (GameMaster.getInstance().showColor() && !shouldShowColor) {

                shouldShowColor = true;

                colorImage.show();

                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(activity);

                if (!sharedPrefs.getBoolean(COLOR_TUTORIAL_FINISHED, false)) {

                    TutorialManager.COLOR = true;
                }
            }

            refresh();
        }
    }

    private void drawBeast() {

        beast.clean();

        beast.draw(maPositionHandle, muMVPMatrixHandle, mTextureCoordinateHandle, maColorHandle);
    }

    private void updateRotMatrix() {

        if (mode.equalsIgnoreCase(ALL)) {

            // First calculate the MVP matrix (Model, View, Projection)
            float[] rotMatrix = new float[MATRIX_SIZE];
            float[] tempMatrix = new float[MATRIX_SIZE];

            // Rotate the coordinate system of the cube around y axis
            Matrix.setRotateM(rotMatrix, 0, mYAngle, 0, 1, 0);
            Matrix.multiplyMM(tempMatrix, 0, rotMatrix, 0, mRotMatrix, 0);

            // Rotate the coordinate system of the cube around x axis
            Matrix.setRotateM(rotMatrix, 0, mXAngle, 1, 0, 0);
            Matrix.multiplyMM(mRotMatrix, 0, rotMatrix, 0, tempMatrix, 0);

            beast.rotateAllCubes(mYAngle, mXAngle);

            refresh();

        } else if (mode.equalsIgnoreCase(ONE)) {

            beast.rotateCurrentCube(mYAngle, mXAngle);

            refresh();
        }
    }

    private void refresh() {

        float[] temp;

        temp = beast.updatePosition(mRotMatrix, mVMatrix, mProjMatrix);

        if (tutorialMatrix == null) {

            tutorialMatrix = new float[temp.length];

            for (int i = 0; i < temp.length; i++) {
                tutorialMatrix[i] = temp[i];
            }
        }
    }

    private int createProgram(String vertexSource, String fragmentSource, boolean beast) {

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, pixelShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }

        if (beast) {

            maColorHandle = GLES20.glGetAttribLocation(program, "a_Color");
            if (maColorHandle == -1) {
                throw new RuntimeException("Could not get attrib location for a_Color");
            }

            maPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
            if (maPositionHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aPosition");
            }

            mTextureCoordinateHandle = GLES20.glGetAttribLocation(program,
                    "aTextureCoord");
            if (mTextureCoordinateHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aTextureCoord");
            }

            muMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
            if (muMVPMatrixHandle == -1) {
                throw new RuntimeException("Could not get attrib location for uMVPMatrix");
            }
        }

        return program;
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }

        return shader;
    }

    public void move(float x, float y, float mPreviousX, float mPreviousY) {

        float dx = x - mPreviousX;
        float dy = y - mPreviousY;

        rotate(-dy * 180.0f / 320, dx * 180.0f / 320);
    }

    private void rotate(float xAngle, float yAngle) {

        if (Math.round(xAngle) != 0) {

            mXAngle = xAngle;
        }

        if (Math.round(yAngle) != 0) {

            mYAngle = yAngle;
        }

        mRotate = 1;

        tutorialManager.rotated();
    }

    public void collision(int x2, int y2) {
        x = x2;
        y = y2;
        mCollision = 1;

        tutorialManager.tapped();
    }

    // text

    private void renderText() {

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        //textSetupFontTexture(mTextureID);

        // Use alpha channel for blending
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        if (mTextTapTitle != null) {
            mTextTapTitle.text = "Taps:";
            mTextTaps.text = taps + "";
        }

        if (mTextTimeTitle != null) {
            mTextTimeTitle.text = "time";
            mTextTime.text = timeText;
        }

        if (mTextManagerStatus != null) {
            mTextManagerStatus.PrepareDraw();
            mTextManagerStatus.Draw(mTextProjectionMatrix);
        }
    }

    private void updateTimeText() {
        long now = System.currentTimeMillis();
        timeText = TimeToString.convert(now - begin);
        //motionListener.requestRender();
    }

    private void textSetupManagers(boolean tapsEnabled, boolean time) {

        if (tapsEnabled || time) {
            // Text in front of the screen
            mTextManagerStatus = new TextManager();
            mTextManagerStatus.setTextureID(1);
            mTextManagerStatus.setUniformscale(0.005f);

            if (tapsEnabled) {

                mTextTapTitle = new TextObject("", -0.95f, 0.8f);
                mTextTapTitle.color = new float[]{0.23f, 0.63f, 0.19f, 1.0f}; // Green-ish

                mTextManagerStatus.addText(mTextTapTitle);

                mTextTaps = new TextObject("", -0.95f, 0.6f);
                mTextTaps.color = new float[]{0.23f, 0.63f, 0.19f, 1.0f}; // Green-ish

                mTextManagerStatus.addText(mTextTaps);
            }

            if (time) {

                mTextTimeTitle = new TextObject("", -0.95f, -0.8f);
                mTextTimeTitle.color = new float[]{0.23f, 0.63f, 0.19f, 1.0f}; // Green-ish

                mTextManagerStatus.addText(mTextTimeTitle);

                mTextTime = new TextObject("", -0.95f, -1.0f);
                mTextTime.color = new float[]{0.23f, 0.63f, 0.19f, 1.0f}; // Green-ish

                mTextManagerStatus.addText(mTextTime);
            }
        }
    }
}
