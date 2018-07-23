package com.example.huajun.opengladvance.level8;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by huajun on 18-7-20.
 */

public class GLView extends GLSurfaceView {

    GLRender glRender;

    GLView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        glRender = new GLRender();
        setRenderer(glRender);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    GLView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setEGLContextClientVersion(2);
        glRender = new GLRender();
        setRenderer(glRender);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    boolean update = true;
    float alpha;
    float y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(update) {
                    y = event.getY();
                    update = false;
                }else {
                    alpha = alpha + (event.getY() - y) / 100.f;
                    while (alpha > 360) {
                        alpha -= 360;
                    }
                    while (alpha < 0) {
                        alpha += 360;
                    }
                    float radians = alpha /180.f * 3.14159f;
                    glRender.setLookAt(radians);
                }
                Log.d("HJ","MOVE");
                break;
            case MotionEvent.ACTION_UP:
                update = true;
                Log.d("HJ","ACTION_UP");
                break;
            default:
                break;
        }
        Log.d("HJ", "alpha "+alpha);

        return true;
    }

    public void setStrength(float ambient,float diffuse,float specular)  {
        glRender.setStrength( ambient, diffuse, specular);
    }
}
