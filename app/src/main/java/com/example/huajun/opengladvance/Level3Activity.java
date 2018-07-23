package com.example.huajun.opengladvance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.huajun.opengladvance.level3.GLView;


/**
 * Created by huajun on 18-7-9.
 */

public class Level3Activity extends AppCompatActivity {

    GLView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level3);
        glSurfaceView = findViewById(R.id.glView);
    }
}
