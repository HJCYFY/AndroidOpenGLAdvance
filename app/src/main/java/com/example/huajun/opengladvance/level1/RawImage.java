package com.example.huajun.opengladvance.level1;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huajun on 18-7-6.
 */

public class RawImage {
    public final static int RAWIMAGE = 0;
    public final static int GRAYIMAGE = 1;
    public final static int BLURIMAGE = 2;
    public final static int MAGIMAGE = 3;

    protected FloatBuffer VertexBuffer;
    protected FloatBuffer TextureBuffer;
    protected int Program;

    public int loadShader(int shaderType,String shaderCode) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private final String vertexShaderCode =
                    "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "attribute vec2 aTextureCoord;" +
                    "varying vec2 vTextureCoord;" +
                    "void main(){" +
                    "   gl_Position = vMatrix * vPosition;" +
                    "   vTextureCoord = aTextureCoord;" +
                    "}";

    private final String fragmentShaderCode =
                    "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 vTextureCoord;" +
                    "void main(){" +
                    "   gl_FragColor = texture2D(texture,vTextureCoord);" +
                    "}";


    float vertexPositions[] = {
            -1.0f,-1.0f,0.0f,       //左下
            1.0f,-1.0f,0.0f,        //右下
            -1.0f,1.0f,0.0f,        //左上
            1.0f,1.0f,0.0f,         //右上
    };

    float textCoord[] = {
            0.0f,1.0f,          //左下
            1.0f,1.0f,          //右下
            0.0f,0.0f,          //左上
            1.0f,0.0f,          //右上
    };


    protected int mPositionHandle;
    protected int mtextureHandle;
    protected int mtextCoordHandle;
    protected int mMatrixHandler;

    protected Bitmap picture;
    protected int[] textureID;


    RawImage() {
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

        GLES20.glUseProgram(Program);

        mMatrixHandler = GLES20.glGetUniformLocation(Program,"vMatrix");

        mtextureHandle = GLES20.glGetUniformLocation(Program,"texture");

        textureID = new int[1];
        GLES20.glGenTextures(1,textureID,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

    }

    public void setPicture(Bitmap bitmap) {
        picture = bitmap;
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,picture,0);
    }

    public void draw(float[] matrix) {

        mPositionHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        mtextCoordHandle = GLES20.glGetAttribLocation(Program,"aTextureCoord");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mtextCoordHandle);

        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,VertexBuffer);
        GLES20.glVertexAttribPointer(mtextCoordHandle,2,GLES20.GL_FLOAT,false,0,TextureBuffer);

        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,matrix,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID[0]);
        GLES20.glUniform1i(mtextureHandle,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mtextCoordHandle);
    }
}
