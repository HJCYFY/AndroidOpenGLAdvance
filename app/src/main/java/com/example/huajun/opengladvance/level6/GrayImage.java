package com.example.huajun.opengladvance.level6;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huajun on 18-7-17.
 */

public class GrayImage {
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 aTextureCoord;" +
                    "varying vec2 vTextureCoord;" +
                    "void main(){" +
                    "   gl_Position = vPosition;" +
                    "   vTextureCoord = aTextureCoord;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 vTextureCoord;" +
                    "void main(){" +
                    "   float luminance = (texture2D(texture,vTextureCoord).r+texture2D(texture,vTextureCoord).g+texture2D(texture,vTextureCoord).b) / 3.0;" +
                    "   gl_FragColor= vec4(luminance,luminance,luminance,1.0);" +
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
            0.0f,0.0f,
            1.0f,0.0f,
            0.0f,1.0f,
            1.0f,1.0f,
    };

    protected int mPositionHandle;
    protected int mtextureHandle;
    protected int mtextCoordHandle;

    GrayImage(){
        VertexBuffer = ByteBuffer.allocateDirect(vertexPositions.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        VertexBuffer.put(vertexPositions).position(0);

        TextureBuffer = ByteBuffer.allocateDirect(textCoord.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        TextureBuffer.put(textCoord).position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program,vertexShader);
        GLES20.glAttachShader(Program,fragmentShader);
        GLES20.glLinkProgram(Program);


        mtextureHandle = GLES20.glGetUniformLocation(Program,"texture");
    }


    public void draw(int texture) {

        GLES20.glUseProgram(Program);
        mPositionHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        mtextCoordHandle = GLES20.glGetAttribLocation(Program,"aTextureCoord");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mtextCoordHandle);

        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,VertexBuffer);
        GLES20.glVertexAttribPointer(mtextCoordHandle,2,GLES20.GL_FLOAT,false,0,TextureBuffer);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
        GLES20.glUniform1i(mtextureHandle,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mtextCoordHandle);
    }
}
