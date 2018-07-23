package com.example.huajun.opengladvance.level3;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.huajun.opengladvance.Level3Activity;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by huajun on 18-7-9.
 */

public class FBORender implements GLSurfaceView.Renderer {


//    float[] mProjectMatrix = new float[16];
//    float[] mViewMatrix = new float[16];
//    float[] mVPMatrix = new float[16];

    ImageFilter imageFilter;
    Bitmap bitmap;
    private ByteBuffer mBuffer;

    int[] textureID;

    public FBORender(Bitmap map) {
        bitmap = map;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("HJ","onSurfaceCreated");
        imageFilter = new ImageFilter();

        textureID = new int[2];
        GLES20.glGenTextures(2,textureID,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID[1]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

        setPicture(bitmap);
        GLES20.glViewport(0,0,bitmap.getWidth(),bitmap.getHeight());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        GLES20.glViewport(0,0,width,height);
//        float ratio=(float)width/height;
//        //设置透视投影    这里应该设置了相机的视场角和焦距之类的
//        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
//        //设置相机位置    如果利用相机位置实现转动效果 upX 等实现相机自身旋转 设置 eyeZ 的值,可以改变物体的显示大小
//        Matrix.setLookAtM(mViewMatrix, 0, 0.f, 0.f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        //计算变换矩阵
//        Matrix.multiplyMM(mVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(bitmap == null || bitmap.isRecycled())
            return;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        createEnv();
        // 选择当前要用的 FrameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        // 选择 textureID[1] 挂载到 FrameBuffer 存储 2D 纹理
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID[1], 0);
        // 选择 renderBuffer[0] 挂载到 FrameBuffer 存储 深度
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,GLES20.GL_RENDERBUFFER, renderBuffer[0]);

        GLES20.glViewport(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // 矩阵 将图片上下翻转
        float matrix[] = new float[16];
        Matrix.setIdentityM(matrix,0);
        Matrix.scaleM(matrix,0,1,-1,1);

        imageFilter.draw(matrix,textureID[0]);
        GLES20.glReadPixels(0, 0, bitmap.getWidth(), bitmap.getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBuffer);

        Bitmap tmp=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        tmp.copyPixelsFromBuffer(mBuffer);

        try{
            FileOutputStream fout = new FileOutputStream("/storage/emulated/0/DCIM/gray.jpg");
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            tmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            fout.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        deleteEnv();
        bitmap.recycle();
    }

    public void setPicture(Bitmap bitmap) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HHHHHJJJJJJ11" + ": glError "+error);
            throw new RuntimeException("HHHHHJJJJJJ11"+"glError "+error);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID[1]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,bitmap.getWidth(),bitmap.getHeight(),0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);
    }


    private int[] frameBuffer = new int[1];
    private int[] renderBuffer = new int[1];

    public void createEnv() {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ6" + ": glError "+error);
            throw new RuntimeException("HJ6"+"glError "+error);
        }
        // 创建 FrameBuffer
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        // 创建 RenderBuffer
        GLES20.glGenRenderbuffers(1, renderBuffer, 0);
        // 选择 RenderBuffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffer[0]);
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ5" + ": glError "+error);
            throw new RuntimeException("HJ5"+"glError "+error);
        }
        // 初始化 RenderBuffer 内存
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, bitmap.getWidth(), bitmap.getHeight());

        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ4.5" + ": glError "+error);
            throw new RuntimeException("HJ4.5"+"glError "+error);
        }

        // 复位 RenderBuffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ4.7" + ": glError "+error);
            throw new RuntimeException("HJ4.7"+"glError "+error);
        }
        // 创建 buffer
        mBuffer = ByteBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ4" + ": glError "+error);
            throw new RuntimeException("HJ4"+"glError "+error);
        }
    }

    private void deleteEnv() {
        GLES20.glDeleteTextures(2, textureID, 0);
        GLES20.glDeleteRenderbuffers(1, renderBuffer, 0);
        GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
    }
}
