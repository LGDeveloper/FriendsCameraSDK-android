package com.lge.vr;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.provider.MediaStore;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import com.lge.friendsCamera.R;

public class ImageRenderer implements CardboardView.StereoRenderer {
    private static final String TAG = "ImageRenderer";

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 1000f;
    private static final float CAMERA_Z = 0.01f;

    private int mProgram;

    private float[] mTexView = new float[16];
    private float[] mTexModelView = new float[16];
    private float[] mTexMVP = new float[16];
    private float[] camera = new float[16];
    private float[] mModel = new float[16];

    private FloatBuffer mVertexBuffer;
    private ShortBuffer mDrawListBuffer;
    private FloatBuffer mTexCoordsBuffer;

    private int mPositionHandle;
    private int mTextureHandle;
    private int mMVPMatrixHandle;

    private int mCount;
    private int mTextureId;

    private Context mContext;
    private Uri mUri;
    private Bitmap mBitmap;

    public ImageRenderer(Context con, Uri uri) {
        this.mContext = con;
        this.mUri = uri;
        int screenWidth = VrUtils.getMaximumTextureSize();
        int screenHeight = screenWidth / 2;
        try {
            String path = "file://" + uri;//VrUtils.getPath(con, uri);
            Bitmap srcBmp = MediaStore.Images.Media.getBitmap(con.getContentResolver(), Uri.parse(path));
            this.mBitmap = VrUtils.scaleBitmap(srcBmp, screenWidth, screenHeight);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        VrUtils.checkGLError("onReadyToDraw");
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        Matrix.multiplyMM(mTexView, 0, eye.getEyeView(), 0, camera, 0);
        Matrix.multiplyMM(mTexModelView, 0, mTexView, 0, mModel, 0);
        Matrix.multiplyMM(mTexMVP, 0, perspective, 0, mTexModelView, 0);

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
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        SkySphere data = new SkySphere();

        ByteBuffer verticesBB = ByteBuffer.allocateDirect(data.vertices.length * 4);
        verticesBB.order(ByteOrder.nativeOrder());
        mVertexBuffer = verticesBB.asFloatBuffer();
        mVertexBuffer.put(data.vertices);
        mVertexBuffer.position(0);

        ByteBuffer indexBB = ByteBuffer.allocateDirect(data.indices.length * 2);
        indexBB.order(ByteOrder.nativeOrder());
        mDrawListBuffer = indexBB.asShortBuffer();
        mDrawListBuffer.put(data.indices);
        mDrawListBuffer.position(0);

        mCount = data.indices.length;

        ByteBuffer textureBB = ByteBuffer.allocateDirect(data.textcoords.length * 4);
        textureBB.order(ByteOrder.nativeOrder());
        mTexCoordsBuffer = textureBB.asFloatBuffer();
        mTexCoordsBuffer.put(data.textcoords);
        mTexCoordsBuffer.position(0);

        int vertexShader = VrUtils.loadGLShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.imagevertex);
        int fragShader = VrUtils.loadGLShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.imagefragment);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragShader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);
        VrUtils.checkGLError("glCreateProgram");

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMatrix");

        Matrix.setIdentityM(mModel, 0);
        mTextureId = VrUtils.setTexture(this.mBitmap);
        Matrix.scaleM(mModel, 0, 540f, 540f, 540f);
    }

    @Override
    public void onRendererShutdown() {

    }

    public void drawSphere() {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mTexCoordsBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mTexMVP, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mCount, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
        VrUtils.checkGLError("glDrawElements");
    }
}
