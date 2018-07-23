package com.example.huajun.opengladvance.level0;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huajun on 18-7-5.
 */

public class GLRender implements GLSurfaceView.Renderer {

    float[] mProjectMatrix = new float[16];
    float[] mViewMatrix = new float[16];
    float[] mVPMatrix = new float[16];
    float[] mRotateMatrix = new float[16];

    float mScale = 1.f;
    float mTheta = 90.f;
    float mAlpha = 0.f;

    BaseShape baseShape;
    private int  shapeType = BaseShape.CubeType;
    private int  newType;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置背景色
        GLES20.glClearColor(0.3f,0.3f,0.3f,0.f);
        // 开启深度
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLView.checkGLError("Cube Initial");

//        // 开启纹理  这里会造成 OpenGL 参数不合法的 异常,原因未知
//        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        baseShape = new Cube();
        Log.d("HJ","onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d("HJ","onSurfaceChanged");
        GLES20.glViewport(0,0,width,height);
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置    如果利用相机位置实现转动效果 upX 等实现相机自身旋转
        Matrix.setLookAtM(mViewMatrix, 0, 0.f, 0.f, -10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (shapeType != newType) {
            changeShape(newType);
            shapeType = newType;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setRotateM(mRotateMatrix,0,mAlpha,0,1,0);
//        Log.d("HJ","mRotateMatrix \n"+mRotateMatrix[0]+","+mRotateMatrix[1]+","+mRotateMatrix[2]+","+mRotateMatrix[3]+",\n"+
//                                                mRotateMatrix[4]+","+mRotateMatrix[5]+","+mRotateMatrix[6]+","+mRotateMatrix[7]+",\n"+
//                                                mRotateMatrix[8]+","+mRotateMatrix[9]+","+mRotateMatrix[10]+","+mRotateMatrix[11]+",\n"+
//                                                mRotateMatrix[12]+","+mRotateMatrix[13]+","+mRotateMatrix[14]+","+mRotateMatrix[15]+"\n");
//        使用 setRotateEulerM 旋转,y 方向旋转有问题,应该是Android的Bug
//        Matrix.setRotateEulerM(mRotateMatrix,0,0,0,0);

        float[] mat = new float[16];
        Matrix.multiplyMM(mat,0,mVPMatrix,0,mRotateMatrix,0);

        baseShape.draw(mat);
    }

    public void changeShape(int type) {
        switch (newType) {
            case BaseShape.CubeType:
                baseShape = new Cube();
                break;
            case BaseShape.BallType:
                baseShape = new Ball();
                break;
            default:
                break;
        }
    }

    // 因为　setShape 的线程与 GL 的线程不在一个线程,这里无法直接调用 OpenGL 函数
    // 也就是说这里直接 new Cude() 或者 new Ball() 是无效的
    // 改成在 Draw 时判断
    public void setShape(int type){
        newType = type;
    }
}
