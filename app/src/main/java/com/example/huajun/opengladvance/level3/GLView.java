package com.example.huajun.opengladvance.level3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


import java.io.IOException;

/**
 * Created by huajun on 18-7-10.
 */

public class GLView extends GLSurfaceView {
    FBORender glRender;

    GLView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("color.jpg"));
        }catch (IOException e){
            Log.d("HJ","color.jpg not found");
            e.printStackTrace();
            return;
        }
        glRender = new FBORender(bitmap);
        setRenderer(glRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    GLView(Context context, AttributeSet attrs){
        super(context,attrs);
        setEGLContextClientVersion(2);
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("color.jpg"));
        }catch (IOException e){
            Log.d("HJ","color.jpg not found");
            e.printStackTrace();
            return;
        }
        glRender = new FBORender(bitmap);
        setRenderer(glRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

}
