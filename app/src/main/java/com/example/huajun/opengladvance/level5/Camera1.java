package com.example.huajun.opengladvance.level5;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

/**
 * Created by huajun on 18-7-12.
 */

public class Camera1 {
    Camera camera;

    Camera1(){

    }

    public void open(int id) {
        int camNum = Camera.getNumberOfCameras();
        if(id >= camNum || id <0) {
            Log.e("HJ","invalid parameter");
            return;
        }
        camera = Camera.open(id);
    }

    public void close() {
        if(camera == null)
            return;
        camera.release();
        camera = null;
    }

    public void setPreviewSize(int width,int height) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(width,height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
    }

    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        try {
            camera.setPreviewTexture(surfaceTexture);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPreview() {
        camera.startPreview();
    }

    public void stopPreview() {
        camera.stopPreview();
    }
}
