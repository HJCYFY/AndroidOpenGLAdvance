package com.example.huajun.opengladvance.level0;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by huajun on 18-7-5.
 */

abstract public class BaseShape {
    public static final int CubeType = 0;
    public static final int BallType = 1;


    protected FloatBuffer VertexBuffer,ColorBuffer;
    protected ShortBuffer IndexBuffer;

    protected int Program;

    public int loadShader(int shaderType,String shaderCode) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    abstract public void draw(float[] matrix);
}
