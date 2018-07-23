package com.example.huajun.opengladvance.level6;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huajun on 18-7-13.
 */

public class CameraFilter {
    private static final String vertexSharderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 vCoord;" +
                    "varying vec2 aCoord;" +
                    "void main() {" +
                    "   gl_Position = vPosition;" +
                    "   aCoord = vCoord;" +
                    "}";
    private static final String fragmentSharderCode =
            "#extension GL_OES_EGL_image_external : require\n"+ // 注意这里必须加上\n
                    "precision mediump float;" +
                    "uniform samplerExternalOES vTexture;" +
                    "varying vec2 aCoord;" +
                    "void main() {" +
                    "   gl_FragColor = texture2D( vTexture, aCoord);" +
                    "}";

    float[] vertexPos = new float[] {
            -1.f,-1.f,
            1.f,-1.f,
            -1.f,1.f,
            1.f,1.f
    };

    float[] coords = new float[] {
            1.f,1.f,
            1.f,0.f,
            0.f,1.f,
            0.f,0.f,
    };

    int Program;
    int positionHandle;
    int coordHandle;
    public int textureHandle;


    FloatBuffer vertexBuffer;
    FloatBuffer coordBuffer;

    private SurfaceTexture mSurfaceTexture;


    private int loadShaeder(int type,String code) {
        int sharder = GLES20.glCreateShader(type);
        GLES20.glShaderSource(sharder,code);
        GLES20.glCompileShader(sharder);
        return sharder;
    }

    CameraFilter() {
        vertexBuffer = ByteBuffer.allocateDirect(vertexPos.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        coordBuffer = ByteBuffer.allocateDirect(coords.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertexBuffer.put(vertexPos).position(0);
        coordBuffer.put(coords).position(0);

        int vertexSharder = loadShaeder(GLES20.GL_VERTEX_SHADER,vertexSharderCode);
        int fragmentSharder = loadShaeder(GLES20.GL_FRAGMENT_SHADER,fragmentSharderCode);

        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program,vertexSharder);
        GLES20.glAttachShader(Program,fragmentSharder);
        GLES20.glLinkProgram(Program);

        textureHandle = GLES20.glGetUniformLocation(Program,"vTexture");
        positionHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        coordHandle = GLES20.glGetAttribLocation(Program,"vCoord");

    }

    public void draw (int texture) {
        GLES20.glUseProgram(Program);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle,2,GLES20.GL_FLOAT,false,0,vertexBuffer);
        GLES20.glEnableVertexAttribArray(coordHandle);
        GLES20.glVertexAttribPointer(coordHandle,2,GLES20.GL_FLOAT,false,0,coordBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+texture);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,texture);

        GLES20.glUniform1i(textureHandle,texture); // 这句话必须放在 glUseProgram 之后

        checkGLError("error3333");
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        checkGLError("error4444");
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(coordHandle);
        checkGLError("error5555");
    }

    // 经常使用 checkGLError 来排查 GLSL 的 Bug
    public  void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ",glOperation + ": glError "+error);
            throw new RuntimeException(glOperation+"glError "+error);
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }
}
