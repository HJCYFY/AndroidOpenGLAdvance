package com.example.huajun.opengladvance.level2;

import android.content.res.Resources;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huajun on 18-7-9.
 */

public class PMKDrawer {

    private static final String vertexSharderCode =
                    "uniform mat4 VPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 vCoord;" +
                    "varying vec2 aCoord;" +
                    "void main() {" +
                    "   gl_Position = VPMatrix * vPosition;" +
                    "   aCoord = vCoord;" +
                    "}";

    private static final String fragmentSharderCode =
                    "precision mediump float;" +
                    "varying vec2 aCoord;" +
                    "uniform sampler2D vTexture;" +
                    "uniform sampler2D vTextureAlpha;" +
                    "void main() {" +
                    "   vec4 color = texture2D(vTexture,aCoord);" +
                    "   color.a = texture2D(vTextureAlpha,aCoord).r;" +
                    "   gl_FragColor = color;" +
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

    int viewWidth,viewHeight;

    ZipPKMReader zipPKMReader;
    FloatBuffer vertexBuffer;
    FloatBuffer coordBuffer;

    int Program;
    int[] texture;

    int posHandle;
    int coordHandle;
    int VPMHandler;
    int textureHandler;
    int textAlphaHandler;


    public PMKDrawer(Resources resources) {
        zipPKMReader = new ZipPKMReader(resources.getAssets());
        zipPKMReader.open();

        vertexBuffer = ByteBuffer.allocateDirect(pos.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        coordBuffer = ByteBuffer.allocateDirect(coord.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertexBuffer.put(pos).position(0);
        coordBuffer.put(coord).position(0);


        int vertexSharder = loadShader(GLES20.GL_VERTEX_SHADER,vertexSharderCode);
        int fragmentSharder = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSharderCode);

        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program,vertexSharder);
        GLES20.glAttachShader(Program,fragmentSharder);

        GLES20.glLinkProgram(Program);

        posHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        coordHandle = GLES20.glGetAttribLocation(Program,"vCoord");
        VPMHandler = GLES20.glGetUniformLocation(Program,"VPMatrix");
        textureHandler = GLES20.glGetUniformLocation(Program,"vTexture");
        textAlphaHandler = GLES20.glGetUniformLocation(Program,"vTextureAlpha");

        texture = new int[2];
        GLES20.glGenTextures(2,texture,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[1]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
    }

    public int loadShader(int shaderType,String shaderCode) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public boolean draw() {
        GLES20.glUseProgram(Program);

        GLES20.glEnableVertexAttribArray(posHandle);
        GLES20.glVertexAttribPointer(posHandle,2,GLES20.GL_FLOAT,false,0,vertexBuffer);

        GLES20.glEnableVertexAttribArray(coordHandle);
        GLES20.glVertexAttribPointer(coordHandle,2,GLES20.GL_FLOAT,false,0,coordBuffer);
        boolean c = bindTexture();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(posHandle);
        GLES20.glDisableVertexAttribArray(coordHandle);

        return c;
    }
    ETC1Util.ETC1Texture t_old;
    ETC1Util.ETC1Texture tAlpha_old;

    private boolean bindTexture() {
        ETC1Util.ETC1Texture t = zipPKMReader.getTexture();
        ETC1Util.ETC1Texture tAlpha = zipPKMReader.getTexture();

        if(t != null && tAlpha != null) {
            float[] matrix = new float[16];
            getMatrix(matrix,t.getWidth(),t.getHeight(),viewWidth,viewHeight);

            GLES20.glUniformMatrix4fv(VPMHandler,1,false,matrix,0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0,GLES20.GL_RGB,GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,t);
            GLES20.glUniform1i(textureHandler,0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[1]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0,GLES20.GL_RGB,GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,tAlpha);
            GLES20.glUniform1i(textAlphaHandler,1);
            t_old = t;
            tAlpha_old =tAlpha;
            return true;
        } else {
            float[] matrix = new float[16];
            Matrix.setIdentityM(matrix,0);
            GLES20.glUniformMatrix4fv(VPMHandler,1,false,matrix,0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0,GLES20.GL_RGB,GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,t_old);
            GLES20.glUniform1i(textureHandler,0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[1]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0,GLES20.GL_RGB,GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,tAlpha_old);
            GLES20.glUniform1i(textAlphaHandler,1);

            zipPKMReader.close();
        }
        return false;
    }


    public void setViewSize(int width,int height) {
        viewWidth = width;
        viewHeight = height;
    }

    public void getMatrix(float[] matrix, int imgWidth,int imgHeight,int viewWidth,int viewHeight) {

        if(imgWidth>0 && imgHeight>0 && viewHeight>0 && viewWidth>0) {

            float radioView=(float)viewWidth/viewHeight;
            float radioImg=(float)imgWidth/imgHeight;
            float[] projectMatrix = new float[16];
            float[] viewMatrix = new float[16];

            if(radioImg > radioView ) {
                Matrix.orthoM(projectMatrix,0,-1,1,-radioImg/radioView,radioImg/radioView,1,3);
            } else {
                Matrix.orthoM(projectMatrix,0,-radioView/radioImg,radioView/radioImg,-1,1,1,3);
            }

            Matrix.setLookAtM(viewMatrix,0,0,0,1,0,0,0,0,1,0);
            Matrix.multiplyMM(matrix,0,projectMatrix,0,viewMatrix,0);
        }
    }
}
