package com.example.huajun.opengladvance;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.huajun.opengladvance.level7.GLRender;

/**
 * Created by huajun on 18-7-18.
 */

/**
 * 只有在RGBA模式下，才可以使用混合功能，颜色索引模式下是无法使用混合功能的。
 * 颜色混合就是两种颜色混合,一般介绍为 源颜色 和 目标颜色
 * 这里为了理解方便,源颜色 定义为 新颜色 , 目标颜色 定义为 旧颜色
 */

public class Level7Activity extends AppCompatActivity{

    /**
     * R,G,B,A 表示 新颜色/旧颜色 的RGBA值
     * Rs,Gs,Bs,As 表示 新颜色 的RGBA值
     * Rd,Gd,Bd,Ad 表示 旧颜色 的RGBA值
     */
    public static String[] factor = new String[]{
        "GL_Zero",                          //  R * 0.0, G * 0.0, B * 0.0,　A * 0.0
        "GL_One",                           //  R * 1.0, G * 1.0, B * 1.0,　A * 1.0
        "GL_SRC_COLOR",                     //  R * Rs, G * Gs, B * Bs,　A * As
        "GL_ONE_MINUS_SRC_COLOR",           //  R * (1-Rs), G * (1-Gs), B * (1-Bs),　A * (1-As)
        "GL_SRC_ALPHA",                     //  R * As, G * As, B * As,　A * As
        "GL_ONE_MINUS_SRC_ALPHA",           //  R * (1-As), G * (1-As), B * (1-As),　A * (1-As)
        "GL_DST_ALPHA",                     //  R * Ad, G * Ad, B * Ad,　A * Ad
        "GL_ONE_MINUS_DST_ALPHA",           //  R * (1-Ad), G * (1-Ad), B * (1-Ad),　A * (1-Ad)
        "GL_DST_COLOR",                     //  R * Ad, G * Ad, B * Ad,　A * Ad
        "GL_ONE_MINUS_DST_COLOR",           //  R * (1-Rd), G * (1-Gd), B * (1-Bd),　A * (1-Ad)
        "GL_SRC_ALPHA_SATURATE",            //  R * f, G * f, B * f,　A * 1.0      (f = min(As,1-Ad))
        "GL_CONSTANT_COLOR",                //  Rc, Gc, Bc, Ac
        "GL_ONE_MINUS_CONSTANT_COLOR",      //  1-Rc, 1-Gc, 1-Bc, 1-Ac
        "GL_CONSTANT_ALPHA",                //  Ac, Ac, Ac, Ac
        "GL_ONE_MINUS_CONSTANT_ALPHA"       //  1-Ac, 1-Ac, 1-Ac, 1-Bc
    };

    private int[] factorInt=new int[]{
            GLES20.GL_ZERO,
            GLES20.GL_ONE,
            GLES20.GL_SRC_COLOR,
            GLES20.GL_ONE_MINUS_SRC_COLOR,
            GLES20.GL_SRC_ALPHA,
            GLES20.GL_ONE_MINUS_SRC_ALPHA,
            GLES20.GL_DST_ALPHA,
            GLES20.GL_ONE_MINUS_DST_ALPHA,
            GLES20.GL_DST_COLOR,
            GLES20.GL_ONE_MINUS_DST_COLOR,
            GLES20.GL_SRC_ALPHA_SATURATE,
            GLES20.GL_CONSTANT_COLOR,
            GLES20.GL_ONE_MINUS_CONSTANT_COLOR,
            GLES20.GL_CONSTANT_ALPHA,
            GLES20.GL_ONE_MINUS_CONSTANT_ALPHA
    };

    /**
     * Cs 表示 新颜色    Cd 表示 旧颜色   Fs 表示 新颜色的因子    Fd 表示 旧颜色的因子
     */
    public static String[] func = new String[]{
            "GL_FUNC_ADD",                      //  Cs * Fs + Cd * Fd
            "GL_FUNC_SUBTRACT",                 //  Cs * Fs - Cd * Fd
            "GL_FUNC_REVERSE_SUBTRACT",         //  Cd * Fd - Cs * Fs
    };


    private int[] funcInt=new int[]{
            GLES20.GL_FUNC_ADD,
            GLES20.GL_FUNC_SUBTRACT,
            GLES20.GL_FUNC_REVERSE_SUBTRACT
    };

    Spinner srcFactorSpinner;
    Spinner dstFactorSpinner;
    Spinner funcSpinner;
    GLSurfaceView glSurfaceView;

    GLRender glRender;

    int srcFactor = GLES20.GL_ZERO; // 新图像
    int dstFactor = GLES20.GL_ONE;
    int function = GLES20.GL_FUNC_ADD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level7);
        srcFactorSpinner = findViewById(R.id.srcFactor);
        dstFactorSpinner = findViewById(R.id.dstFactor);
        funcSpinner = findViewById(R.id.func);
        glSurfaceView = findViewById(R.id.glView);

        srcFactorSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,factor));
        dstFactorSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,factor));
        funcSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,func));

        srcFactorSpinner.setOnItemSelectedListener(srcFactorListener);
        dstFactorSpinner.setOnItemSelectedListener(dstFactorListener);
        funcSpinner.setOnItemSelectedListener(funcListener);

        srcFactorSpinner.setSelection(0);
        dstFactorSpinner.setSelection(1);
        srcFactorSpinner.setSelection(0);

        glSurfaceView.setEGLContextClientVersion(2);
        glRender = new GLRender(this);
        glSurfaceView.setRenderer(glRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    AdapterView.OnItemSelectedListener srcFactorListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("HJ","a ");
            srcFactor = factorInt[position];
            glRender.setFactorAndFunc(srcFactor,dstFactor,function);
            glSurfaceView.requestRender();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener dstFactorListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("HJ","b ");
            dstFactor = factorInt[position];
            glRender.setFactorAndFunc(srcFactor,dstFactor,function);
            glSurfaceView.requestRender();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener funcListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("HJ","c ");
            function = funcInt[position];
            glRender.setFactorAndFunc(srcFactor,dstFactor,function);
            glSurfaceView.requestRender();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
