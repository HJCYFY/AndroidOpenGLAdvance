package com.example.huajun.opengladvance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.huajun.opengladvance.level4.GLES20Env;

import java.io.IOException;

/**
 * Created by huajun on 18-7-10.
 */

public class Level4Activity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level4);
        processImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGles20Env.destory();
    }

    GLES20Env mGles20Env;
    private void processImage() {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("color.jpg"));
        }catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mGles20Env = new GLES20Env(bitmap.getWidth(),bitmap.getHeight());
        mGles20Env.setTexture(bitmap);
        mGles20Env.onDrawFrame();
    }

}
