package com.example.huajun.opengladvance.level1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by huajun on 18-7-6.
 */

public class BlurImage extends RawImage {
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
                    "   vec4 nColor = texture2D(texture,vTextureCoord);" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x-0.006,vTextureCoord.y-0.006));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x-0.006,vTextureCoord.y+0.006));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x+0.006,vTextureCoord.y-0.006));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x+0.006,vTextureCoord.y+0.006));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x-0.004,vTextureCoord.y-0.004));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x-0.004,vTextureCoord.y+0.004));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x+0.004,vTextureCoord.y-0.004));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x+0.004,vTextureCoord.y+0.004));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x-0.002,vTextureCoord.y-0.002));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x-0.002,vTextureCoord.y+0.002));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x+0.002,vTextureCoord.y-0.002));" +
                    "   nColor+=texture2D(texture,vec2(vTextureCoord.x+0.002,vTextureCoord.y+0.002));" +
                    "   nColor/=13.0;" +
                    "   gl_FragColor=nColor;" +
                    "}";

    BlurImage(){
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
}
