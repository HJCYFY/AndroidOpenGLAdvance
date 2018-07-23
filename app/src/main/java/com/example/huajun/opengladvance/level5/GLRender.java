package com.example.huajun.opengladvance.level5;

import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huajun on 18-7-12.
 */

public class GLRender implements GLSurfaceView.Renderer {
    Camera1 camera1;
    int[] texture;
    SurfaceTexture surfaceTexture;
    ImageFilter imageFilter;

    GLSurfaceView glSurfaceView;

    GLRender(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
        camera1 = new Camera1();

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("HJ","onSurfaceCreated");
        GLES20.glClearColor(0.4f,0.4f,0.4f,1.0f);
        texture = new int[1];
        GLES20.glGenTextures(1,texture,0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

        // 将 SurfaceTexture 和 纹理 绑定
        surfaceTexture = new SurfaceTexture(texture[0]);

        camera1.open(0);

        Log.d("HJ","imageFilter1");
        if(imageFilter == null) {
            imageFilter = new ImageFilter();
            Log.d("HJ","imageFilter2");
        }
        surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d("HJ","onSurfaceChanged");
        surfaceTexture.setDefaultBufferSize(1920,1080);
        camera1.setPreviewSize(1440,1080);
        camera1.setPreviewTexture(surfaceTexture);
        camera1.startPreview();
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surfaceTexture.updateTexImage();
        imageFilter.draw(texture[0]);
    }


    SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            glSurfaceView.requestRender();
        }
    };

    public void setFilterType(int type) {
        if(imageFilter != null)
            imageFilter.setFilterType(type);
    }

    public void destroy() {
        Log.d("HJ","destroy");
        camera1.stopPreview();
        camera1.close();
    }
}
