package com.example.huajun.opengladvance.level0;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by huajun on 18-7-5.
 */

public class GLView extends GLSurfaceView {

    private GLRender renderer;

    GLView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new GLRender();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    // 如果在 XML 中定义,必须实现该方法
    GLView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setEGLContextClientVersion(2);
        renderer = new GLRender();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setShape(int type) {
        renderer.setShape(type);
        requestRender();
    }

    float x,y;
    boolean update = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int fingerNum = event.getPointerCount();
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(fingerNum == 1){
                    if(update == true) {
                        x = event.getX();
                        y = event.getY();
                        update = false;
                    } else {
                        renderer.mTheta += (event.getY()-y)/180.f;
                        renderer.mAlpha += (event.getX()-x)/180.f;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                update = true;
                break;
        }
        requestRender();
        return true;
    }

    public static void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ",glOperation + ": glError "+error);
            throw new RuntimeException(glOperation+"glError "+error);
        }
    }
}
