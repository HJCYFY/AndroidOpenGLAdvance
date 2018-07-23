package com.example.huajun.opengladvance.level5;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by huajun on 18-7-12.
 */

public class GLView extends GLSurfaceView {
    GLRender glRender;
    GLView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        glRender = new GLRender(this);
        setRenderer(glRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    GLView(Context context, AttributeSet attrs) {
        super(context,attrs);

        setEGLContextClientVersion(2);
        glRender = new GLRender(this);
        setRenderer(glRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public void setFilterType(int type) {
        glRender.setFilterType(type);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        glRender.destroy();
    }
}
