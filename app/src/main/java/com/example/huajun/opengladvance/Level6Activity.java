package com.example.huajun.opengladvance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.huajun.opengladvance.level6.RenderThread;

/**
 * Created by huajun on 18-7-13.
 */

public class Level6Activity extends AppCompatActivity implements SurfaceHolder.Callback{

    SurfaceView surfaceView;
    private RenderThread mRenderThread;
    RenderThread.RenderHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level6);

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        surfaceView = findViewById(R.id.surfaceView);

        surfaceView.getHolder().addCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mRenderThread = new RenderThread(surfaceView.getHolder(),this);
        mRenderThread.setName("Render Thread");
        mRenderThread.start();
        mRenderThread.waitUntilReady();

        mHandler = mRenderThread.getHandler();

        if(mHandler != null) {
            mHandler.sendSurfaceCreated();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mHandler != null)
            mHandler.sendSurfaceChanged(format,width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mHandler != null) {
            Log.d("HJ","sendShutdown");
            mHandler.sendShutdown();
            try {
                mRenderThread.join();
            }catch (InterruptedException e) {
                throw new RuntimeException("join was interrupted",e);
            }
        }
        mRenderThread = null;
    }


}
