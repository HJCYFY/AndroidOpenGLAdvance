package com.example.huajun.opengladvance.level2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huajun on 18-7-9.
 */

public class ZipAniView extends GLSurfaceView {

    PMKRender pmkRender;

    public ZipAniView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setZOrderOnTop(true); //设置 View 在所有控件上方(不会被其他控件遮挡)
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // GLSurfaceView默认EGL配置的像素格式为RGB_656，16位的深度缓存(depth buffer)，默认不开启遮罩缓存(stencil buffer)。
        setEGLConfigChooser(8,8,8,8,16,0);
        pmkRender = new PMKRender(getResources());
        setRenderer(pmkRender);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public ZipAniView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setEGLContextClientVersion(2);
        setZOrderOnTop(true); //设置 View 在所有控件上方(不会被其他控件遮挡)
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // GLSurfaceView默认EGL配置的像素格式为RGB_656，16位的深度缓存(depth buffer)，默认不开启遮罩缓存(stencil buffer)。
        setEGLConfigChooser(8,8,8,8,16,0);
        pmkRender = new PMKRender(getResources());
        setRenderer(pmkRender);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }


    public static class PMKRender implements Renderer{
        private PMKDrawer pmkDrawer;

        Resources resources;

        PMKRender(Resources res) {
            resources = res;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.6f,0.6f,0.6f,0.0f);
//            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
            pmkDrawer = new PMKDrawer(resources);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0,0,width,height);
            pmkDrawer.setViewSize(width,height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
            Log.d("HJ","onDrawFrame");
            pmkDrawer.draw();
        }
    }
}
