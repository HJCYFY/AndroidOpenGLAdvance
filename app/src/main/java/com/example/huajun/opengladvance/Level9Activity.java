package com.example.huajun.opengladvance;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.huajun.opengladvance.level9.GLRender;

/**
 * Created by huajun on 18-7-20.
 */

public class Level9Activity extends AppCompatActivity {

    GLSurfaceView glSurfaceView;
    GLRender renderer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level9);
        glSurfaceView = findViewById(R.id.glView);
        glSurfaceView.setEGLContextClientVersion(2);
        renderer = new GLRender(this);
        if(glSurfaceView == null) {
            Log.d("HJ","glSurfaceView is null");
        }
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
