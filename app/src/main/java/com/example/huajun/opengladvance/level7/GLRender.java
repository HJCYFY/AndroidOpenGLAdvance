package com.example.huajun.opengladvance.level7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.huajun.opengladvance.level5.GLView;
import com.example.huajun.opengladvance.level5.ImageFilter;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huajun on 18-7-18.
 */

public class GLRender implements GLSurfaceView.Renderer {
    Context mContext;
    public GLRender(Context context) {
        mContext = context;
    }

    int[] texture;
    BlendFilter blendFilter = null;
    int srcFactor = GLES20.GL_ZERO; // 新图像
    int dstFactor = GLES20.GL_ONE;
    int function = GLES20.GL_FUNC_ADD;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("HJ","onSurfaceCreated");
        GLES20.glClearColor(0.3f,0.3f,0.3f,1.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        // 设置混合颜色 用于 常数混合
        GLES20.glBlendColor(0,0.5f,0,0.3f);
        texture = new int[2];
        GLES20.glGenTextures(2,texture,0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

        try {
            // 这里好像丢失了 图像的 Alpha 值(Alpha 值全为1)
            Bitmap src = BitmapFactory.decodeStream(mContext.getResources().getAssets().open("dstImg.png"));
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,src,0);
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[1]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

        try {
            Bitmap src = BitmapFactory.decodeStream(mContext.getResources().getAssets().open("srcImg.jpg"));    //
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,src,0);
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        if(blendFilter == null) {
            blendFilter = new BlendFilter();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d("HJ","onSurfaceChanged");
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("HJ", "onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        if(blendFilter == null) {
            Log.d("HJ", "blendFilter == null");
            return;
        }
        // 第一张图 先关闭 BLEND
        GLES20.glDisable(GLES20.GL_BLEND);
        blendFilter.draw(texture[0]);
        // 需要混合时开启 BLEND
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(srcFactor,dstFactor);
        GLES20.glBlendEquation(function);
        blendFilter.draw(texture[1]);
    }

    public void setFactorAndFunc(int srcF,int dstF,int func) {
        Log.d("HJ", "setFactorAndFunc");
        srcFactor = srcF;
        dstFactor = dstF;
        function = func;
    }
}
