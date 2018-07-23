package com.example.huajun.opengladvance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.huajun.opengladvance.level0.BaseShape;
import com.example.huajun.opengladvance.level0.GLView;

/**
 * Created by huajun on 18-7-5.
 */

public class Level0Activity extends AppCompatActivity implements View.OnClickListener {

    Button mBtnCube;
    Button mBtnBall;
    GLView mGLView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level0);
        mBtnCube = findViewById(R.id.btnCube);
        mBtnBall = findViewById(R.id.btnBall);
        mGLView = findViewById(R.id.glView);
    }

    @Override
    public void onClick(View v) {
        if(mGLView == null) {
            Log.d("HJ","mGLView is null");
            return;
        }
        switch (v.getId()) {
            case R.id.btnCube:
                mGLView.setShape(BaseShape.CubeType);
                break;
            case R.id.btnBall:
                mGLView.setShape(BaseShape.BallType);
                break;
            default:
                break;
        }
    }
}
