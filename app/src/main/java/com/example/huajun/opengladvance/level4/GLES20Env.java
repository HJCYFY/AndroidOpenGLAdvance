package com.example.huajun.opengladvance.level4;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created by huajun on 18-7-11.
 */

public class GLES20Env {

    int mWidth,mHeight;
    EGLHelper mEglHelper;
    ImageFilter mImageFilter;

    public GLES20Env(int width, int height) {
        mWidth = width;
        mHeight = height;

        mEglHelper = new EGLHelper(width,height);

        mImageFilter = new ImageFilter();
    }

    int[] texture;

    public void setTexture(Bitmap bitmap) {
        if(texture == null) {
            texture = new int[1];
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
    }

    public void onDrawFrame() {
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix,0);
        Matrix.scaleM(matrix,0,1,-1,1);

        GLES20.glViewport(0,0,mWidth,mHeight);

        mImageFilter.draw(matrix,texture[0]);
        ByteBuffer buffer = ByteBuffer.allocate(mWidth*mHeight*4);
        mEglHelper.mGl10.glReadPixels(0,0,mWidth,mHeight, GLES10.GL_RGBA,GLES10.GL_UNSIGNED_BYTE, buffer);

        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/storage/emulated/0/DCIM/gray2.jpg");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            fileOutputStream.close();
            Log.d("HJ","Write file");
        }catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
    }

    public void destory() {
        mEglHelper.destroy();
    }
}
