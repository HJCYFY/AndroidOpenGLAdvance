package com.example.huajun.opengladvance.level1;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Looper;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huajun on 18-7-6.
 */

public class GLRender implements GLSurfaceView.Renderer{

    float[] mProjectMatrix = new float[16];
    float[] mViewMatrix = new float[16];
    float[] mVPMatrix = new float[16];

    RawImage rawImage;
    Bitmap picture;

    int imageType = 0;
    int newType = 0;

    GLRender(Bitmap bitmap) {
        picture = bitmap;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.3f,0.3f,0.3f,0.f);
//        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        rawImage = new RawImage();
        rawImage.setPicture(picture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        GLES20.glViewport(0,0,width,height);
        float ratio=(float)width/height;
        //设置透视投影    这里应该设置了相机的视场角和焦距之类的
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置    如果利用相机位置实现转动效果 upX 等实现相机自身旋转 设置 eyeZ 的值,可以改变物体的显示大小
        Matrix.setLookAtM(mViewMatrix, 0, 0.f, 0.f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("HJ","newType "+newType + " imageType "+imageType);
        if(newType != imageType) {
            switch (newType) {
                case RawImage.RAWIMAGE:
                    rawImage = new RawImage();
                    rawImage.setPicture(picture);
                    break;
                case RawImage.GRAYIMAGE:
                    rawImage = new GrayImage();
                    rawImage.setPicture(picture);
                    break;
                case RawImage.BLURIMAGE:
                    rawImage = new BlurImage();
                    rawImage.setPicture(picture);
                    break;
                case RawImage.MAGIMAGE:
                    rawImage = new MagImage();
                    rawImage.setPicture(picture);
                default:
                    break;
            }
            imageType = newType;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        rawImage.draw(mVPMatrix);
    }

    public void setFilter(int type) {
        newType = type;
    }
}
