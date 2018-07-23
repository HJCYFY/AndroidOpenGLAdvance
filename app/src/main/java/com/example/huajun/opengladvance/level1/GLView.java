package com.example.huajun.opengladvance.level1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;

/**
 * Created by huajun on 18-7-6.
 */

public class GLView extends GLSurfaceView {

    GLRender glRender;

    GLView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("cat.png"));
        }catch (IOException e) {
            e.printStackTrace();
            return;
        }
        glRender = new GLRender(bitmap);
        setRenderer(glRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    GLView(Context context, AttributeSet attrs){
        super(context,attrs);
        setEGLContextClientVersion(2);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("cat.png"));
        }catch (IOException e) {
            e.printStackTrace();
            return;
        }
        glRender = new GLRender(bitmap);
        setRenderer(glRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public static void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ",glOperation + ": glError "+error);
            throw new RuntimeException(glOperation+"glError "+error);
        }
    }

    public void setFilter(int type) {
        glRender.setFilter(type);
        requestRender();
    }
}
