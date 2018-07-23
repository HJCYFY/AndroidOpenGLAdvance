package com.example.huajun.opengladvance.level4;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huajun on 18-7-11.
 */

public class ImageFilter {

    private static final String vertexSharderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 vCoord;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec2 textureCoordinate;" +
                    "void main(){" +
                    "    gl_Position = vMatrix*vPosition;" +
                    "    textureCoordinate = vCoord;" +
                    "}" ;
    private static final String fragmentSharderCode =
            "precision mediump float;" +
                    "varying vec2 textureCoordinate;" +
                    "uniform sampler2D vTexture;" +
                    "void main() {" +
                    "    vec4 color=texture2D( vTexture, textureCoordinate);" +
                    "    float rgb=color.g;" +
                    "    vec4 c=vec4(rgb,rgb,rgb,color.a);" +
                    "    gl_FragColor = c;" +
                    "}";

    //顶点坐标
    private float pos[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    //纹理坐标
    private float[] coord={
            0.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  0.0f,
            1.0f, 1.0f,
    };

    int Program;
    int positionHandle;
    int coordHandle;
    int matrixHandle;
    public int textureHandle;


    FloatBuffer vertexBuffer;
    FloatBuffer coordBuffer;

    private int loadShaeder(int type,String code) {
        int sharder = GLES20.glCreateShader(type);
        GLES20.glShaderSource(sharder,code);
        GLES20.glCompileShader(sharder);
        return sharder;
    }

    ImageFilter() {
        vertexBuffer = ByteBuffer.allocateDirect(pos.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        coordBuffer = ByteBuffer.allocateDirect(coord.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertexBuffer.put(pos).position(0);
        coordBuffer.put(coord).position(0);

        int vertexSharder = loadShaeder(GLES20.GL_VERTEX_SHADER,vertexSharderCode);
        int fragmentSharder = loadShaeder(GLES20.GL_FRAGMENT_SHADER,fragmentSharderCode);

        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program,vertexSharder);
        GLES20.glAttachShader(Program,fragmentSharder);
        GLES20.glLinkProgram(Program);

        matrixHandle = GLES20.glGetUniformLocation(Program,"vMatrix");
        textureHandle = GLES20.glGetUniformLocation(Program,"vTexture");
        positionHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        coordHandle = GLES20.glGetAttribLocation(Program,"vCoord");
    }

    public void draw (float[] matrix,int texture) {
        GLES20.glUseProgram(Program);
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ3" + ": glError "+error);
            throw new RuntimeException("HJ3"+"glError "+error);
        }
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle,2,GLES20.GL_FLOAT,false,0,vertexBuffer);
        GLES20.glEnableVertexAttribArray(coordHandle);
        GLES20.glVertexAttribPointer(coordHandle,2,GLES20.GL_FLOAT,false,0,coordBuffer);

        GLES20.glUniformMatrix4fv(matrixHandle,1,false,matrix,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
        GLES20.glUniform1i(textureHandle,0); // 这句话必须放在 glUseProgram 之后
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ2" + ": glError "+error);
            throw new RuntimeException("HJ2"+"glError "+error);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(coordHandle);

        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ","HJ1" + ": glError "+error);
            throw new RuntimeException("HJ1"+"glError "+error);
        }
    }
}
