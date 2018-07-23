package com.example.huajun.opengladvance.level7;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huajun on 18-7-18.
 */

public class BlendFilter {
    static final String vertexCode =
            "attribute vec4 vPosition;" +
            "attribute vec2 vCoord;" +
            "varying vec2 aCoord;" +
            "void main(){" +
            "    gl_Position = vPosition;" +
            "    aCoord = vCoord;" +
            "}";

    static final String fragmentCode =
            "precision mediump float;" +
            "varying vec2 aCoord;" +
            "uniform sampler2D vTexture;" +
            "void main() {" +
            "    gl_FragColor = texture2D( vTexture, aCoord );" +
            "}";


    protected FloatBuffer VertexBuffer;
    protected FloatBuffer TextureBuffer;
    protected int Program;

    public int loadShader(int shaderType,String shaderCode) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    float vertexPositions[] = {
            -1.0f,-1.0f,0.0f,       //左下
            1.0f,-1.0f,0.0f,        //右下
            -1.0f,1.0f,0.0f,        //左上
            1.0f,1.0f,0.0f,         //右上
    };

    float textCoord[] = {
            0.0f,1.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,0.0f,
    };


    protected int mPositionHandle;
    protected int mtextureHandle;
    protected int mtextCoordHandle;

    BlendFilter(){
        VertexBuffer = ByteBuffer.allocateDirect(vertexPositions.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        VertexBuffer.put(vertexPositions).position(0);

        TextureBuffer = ByteBuffer.allocateDirect(textCoord.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        TextureBuffer.put(textCoord).position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentCode);

        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program,vertexShader);
        GLES20.glAttachShader(Program,fragmentShader);
        GLES20.glLinkProgram(Program);

        mPositionHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        mtextCoordHandle = GLES20.glGetAttribLocation(Program,"vCoord");
        mtextureHandle = GLES20.glGetUniformLocation(Program,"vTexture");

        checkGLError("10");
    }

    public void draw(int texture) {

        GLES20.glUseProgram(Program);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mtextCoordHandle);

        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,VertexBuffer);
        GLES20.glVertexAttribPointer(mtextCoordHandle,2,GLES20.GL_FLOAT,false,0,TextureBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+texture);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);

        GLES20.glUniform1i(mtextureHandle,0+texture);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mtextCoordHandle);
    }

    public void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ",glOperation + ": glError "+error);
            throw new RuntimeException(glOperation+"glError "+error);
        }
    }
}
