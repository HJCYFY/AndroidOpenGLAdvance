package com.example.huajun.opengladvance.level5;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huajun on 18-7-12.
 */

public class ImageFilter {
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
            "uniform int vFilterType;" +
            "varying vec2 aCoord;" +
            "void main() {" +
            "   if(vFilterType == 0) {" +
            "       float gray = (texture2D( vTexture, aCoord).r + texture2D( vTexture, aCoord).g + texture2D( vTexture, aCoord).b)/3.0;" +
            "       gl_FragColor =vec4( gray, gray,gray,1.0);" +
            "   } else {" +
            "       vec4 color = texture2D( vTexture, aCoord);" +
            "       float r = color.r<0.5 ? color.r:1.0-color.r;" +
            "       r = pow(r,3.0)*4.0;" +
            "       r = color.r<0.5 ?  r:1.0-r;" +
            "       float g = color.g<0.5 ? color.g:1.0-color.g;" +
            "       g = pow(g,2.0)*2.0;" +
            "       g = color.g<0.5 ? g:1.0-g;" +
            "       float b = color.b/2.0 + 0.145;" +
            "       gl_FragColor =vec4( r, g, b,1.0);" +
            "   }" +
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
    int filterHandle;
    public int textureHandle;


    FloatBuffer vertexBuffer;
    FloatBuffer coordBuffer;

    int filterType = 0;

    private int loadShaeder(int type,String code) {
        int sharder = GLES20.glCreateShader(type);
        GLES20.glShaderSource(sharder,code);
        GLES20.glCompileShader(sharder);
        return sharder;
    }

    ImageFilter() {
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
        filterHandle = GLES20.glGetUniformLocation(Program,"vFilterType");
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

        GLES20.glUniform1i(filterHandle,filterType); // 这句话必须放在 glUseProgram 之后
        GLES20.glUniform1i(textureHandle,texture); // 这句话必须放在 glUseProgram 之后

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(coordHandle);
        checkGLError("error");
    }

    public void setFilterType(int type) {
        if(type >1)
            return;
        filterType = type;
    }

    // 经常使用 checkGLError 来排查 GLSL 的 Bug
    public  void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ",glOperation + ": glError "+error);
            throw new RuntimeException(glOperation+"glError "+error);
        }
    }
}
