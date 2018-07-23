package com.example.huajun.opengladvance.level0;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by huajun on 18-7-5.
 */

public class Cube extends BaseShape{
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;" +
            "varying  vec4 vColor;" +
            "attribute vec4 aColor;" +
            "void main(){" +
            "   gl_Position = vMatrix * vPosition;" +
            "   vColor = aColor;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying  vec4 vColor;" +
            "void main(){" +
            "   gl_FragColor = vColor;" +
            "}";

    final float cubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };

    final short index[]={
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2,    //下面
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
    };

    float color[] = {
            1.f,    1.f,    1f,     1f,
            0.f,    0.1f,   0.3f,   1f,
            0.1f,   0.3f,   0.5f,   1f,
            0.3f,   0.5f,   0.7f,   1f,
            0.5f,   0.7f,   0.9f,   1f,
            0.9f,   0.7f,   0.5f,   1f,
            0.7f,   0.5f,   0.3f,   1f,
            0.5f,   0.3f,   0.1f,   1f,
    };


    private int mPositionHandle;
    private int mColorHandle;
    private int mMatrixHandler;

    Cube() {
        VertexBuffer = ByteBuffer.allocateDirect(cubePositions.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        VertexBuffer.put(cubePositions).position(0);

        ColorBuffer = ByteBuffer.allocateDirect(color.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        ColorBuffer.put(color).position(0);

        IndexBuffer = ByteBuffer.allocateDirect(index.length*4).order(ByteOrder.nativeOrder()).asShortBuffer();
        IndexBuffer.put(index).position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program,vertexShader);
        GLES20.glAttachShader(Program,fragmentShader);
        GLES20.glLinkProgram(Program);

    }

    @Override
    public void draw(float[] matrix) {
        Log.d("HJ","Cube draw");
        GLES20.glUseProgram(Program);
        GLES20.glDisable(GLES20.GL_BLEND_COLOR);

        mPositionHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        mColorHandle = GLES20.glGetAttribLocation(Program,"aColor");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,VertexBuffer);
        GLES20.glVertexAttribPointer(mColorHandle,4,GLES20.GL_FLOAT,false,0,ColorBuffer);

        mMatrixHandler = GLES20.glGetUniformLocation(Program,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,matrix,0);


        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length,GLES20.GL_UNSIGNED_SHORT,IndexBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }
}
