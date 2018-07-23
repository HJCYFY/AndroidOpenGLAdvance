package com.example.huajun.opengladvance.level8;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huajun on 18-7-19.
 */

public class GLRender implements GLSurfaceView.Renderer {

    CubeWithLight cubeWithLight;

    float[] PMatrix = new float[16];
    float[] VMatrix = new float[16];
    float[] PVMatrix = new float[16];
    float[] RMatrix = new float[16];
    float[] RYMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("HJ","onSurfaceCreated");
        GLES20.glClearColor(0.3f,0.3f,0.3f,1.0f);
        Log.d("HJ","onSurfaceCreated2");
        cubeWithLight = new CubeWithLight();
        Log.d("HJ","onSurfaceCreated3");
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","test 1 " + ": glError "+error);
            throw new RuntimeException("test 1 "+"glError "+error);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","test 2 " + ": glError "+error);
            throw new RuntimeException("test 2 "+"glError "+error);
        }
        Log.d("HJ","onSurfaceChanged");
        GLES20.glViewport(0,0,width,height);
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","test 3 " + ": glError "+error);
            throw new RuntimeException("test 3 "+"glError "+error);
        }
        float ratio = (float)width/height;
        Matrix.frustumM(PMatrix,0, -ratio, ratio, -1, 1, 3, 20);
        Matrix.setLookAtM(VMatrix, 0, 0.f, 0.f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(PVMatrix,0,PMatrix,0,VMatrix,0);
        Matrix.setRotateM(RYMatrix,0,1,0,1,0);
        Matrix.setIdentityM(RMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.multiplyMM(RMatrix,0,RMatrix,0,RYMatrix,0);
        float[] mat = new float[16];
        Matrix.multiplyMM(mat,0,PVMatrix,0,RMatrix,0);
        cubeWithLight.draw(mat);
    }

    public void setStrength(float ambient,float diffuse,float specular)  {
        cubeWithLight.setStrength( ambient, diffuse, specular);
    }

    public void setLookAt(float alpha){
            float y = (float)(10 * Math.sin(alpha));
            float z = (float)(10 * Math.cos(alpha));
            Matrix.setLookAtM(VMatrix, 0, 0.f, y, z, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(PVMatrix,0,PMatrix,0,VMatrix,0);
    }
}
