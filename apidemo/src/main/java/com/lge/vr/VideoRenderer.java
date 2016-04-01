package com.lge.vr;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.lge.friendsCamera.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

public class VideoRenderer implements CardboardView.StereoRenderer {
    private Context mContext;
    private Uri mUri;

    private static final String TAG = "VideoRenderer";
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 300.0f;

    private float[] modelSphere;
    private float[] camera;
    private float[] view;
    private float[] modelViewProjection;
    private float[] modelView;
    private float[] mSTMatrix;

    private FloatBuffer sphereVertices;
    private FloatBuffer mTexCoordsBuffer;
    private ShortBuffer drawListBuffer;

    private int sphereProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mMVPMatrixHandle;
    private int mSTMatrixHandler;
    private int mUTextureHandler;
    private int mCount;

    private SurfaceTexture mSurfaceTexture;
    private MediaPlayer mMediaPlayer;
    protected static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    int mTextureId;

    private float[] headView = new float[16];

    public VideoRenderer(Context con, Uri uri) {
        this.mContext = con;
        this.mUri = uri;

        modelSphere = new float[16];
        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];
        mSTMatrix = new float[16];

        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(modelSphere, 0);
        Matrix.scaleM(modelSphere, 0, 200.0f, 200.0f, 200.0f);

        generateGLTexture();
        setVideo(this.mContext, this.mUri);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
        mSurfaceTexture.updateTexImage();
        Matrix.setIdentityM(mSTMatrix, 0);
        mSurfaceTexture.getTransformMatrix(mSTMatrix);

        headTransform.getHeadView(headView, 0);
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        VrUtils.checkGLError("colorParam");
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        Matrix.multiplyMM(modelView, 0, view, 0, modelSphere, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);

        drawSphere();
    }


    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {
        Log.i(TAG, "onSurfaceChanged");
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.
        SkySphere sp = new SkySphere();

        ByteBuffer bbVertices = ByteBuffer.allocateDirect(sp.vertices.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        sphereVertices = bbVertices.asFloatBuffer();
        sphereVertices.put(sp.vertices);
        sphereVertices.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                sp.indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(sp.indices);
        drawListBuffer.position(0);

        mCount = sp.indices.length;

        ByteBuffer textureBB = ByteBuffer.allocateDirect(sp.textcoords.length * 4);
        textureBB.order(ByteOrder.nativeOrder());
        mTexCoordsBuffer = textureBB.asFloatBuffer();
        mTexCoordsBuffer.put(sp.textcoords);
        mTexCoordsBuffer.position(0);

        int vertexShader = VrUtils.loadGLShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.videovertex);
        int fragmentShager = VrUtils.loadGLShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.videofragment);

        sphereProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(sphereProgram, vertexShader);
        GLES20.glAttachShader(sphereProgram, fragmentShager);
        GLES20.glLinkProgram(sphereProgram);
        GLES20.glUseProgram(sphereProgram);

        VrUtils.checkGLError("SkySphere program");

        mPositionHandle = GLES20.glGetAttribLocation(sphereProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(sphereProgram, "aTexture");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(sphereProgram, "uMatrix");
        mSTMatrixHandler = GLES20.glGetUniformLocation(sphereProgram, "uSTMatrix");
        mUTextureHandler = GLES20.glGetUniformLocation(sphereProgram, "uTexture");

        VrUtils.checkGLError("onSurfaceCreated");

        mMediaPlayer.start();
    }

    @Override
    public void onRendererShutdown() {

    }

    private void generateGLTexture() {
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);
        mTextureId = texturenames[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureId);

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private void drawSphere() {
        GLES20.glUseProgram(sphereProgram);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        // Set the position of the cube
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT,
                false, 0, sphereVertices);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mTexCoordsBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureId);
        GLES20.glUniform1i(mUTextureHandler, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniformMatrix4fv(mSTMatrixHandler, 1, false, mSTMatrix, 0);  //mSTMatrix

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mCount, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
        VrUtils.checkGLError("SkySphere program params");
    }

    private void setVideo(Context con, Uri uri) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(con.getApplicationContext(), uri);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSurfaceTexture = new SurfaceTexture(mTextureId);

        Surface surface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(surface);
        surface.release();

    }

    public void pauseMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void stopMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public void startMediaPlayer() {
        if (mMediaPlayer.isPlaying())
            return;
        try {
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forwardMediaPlayer() {
        int current = mMediaPlayer.getCurrentPosition();
        int total = mMediaPlayer.getDuration();
        if (current + (10 * 1000) > total) {
            mMediaPlayer.seekTo(total);
        } else {
            mMediaPlayer.seekTo(current + (10 * 1000));
        }
    }

    private void backwardMediaPlayer() {
        int current = mMediaPlayer.getCurrentPosition();
        if (current - (10 * 1000) < 0) {
            mMediaPlayer.seekTo(0);
        } else {
            mMediaPlayer.seekTo(current - (10 * 1000));
        }
    }
}
