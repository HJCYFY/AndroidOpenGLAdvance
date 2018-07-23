package com.example.huajun.opengladvance.level6;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Arrays;


/**
 * Created by huajun on 18-7-13.
 */

public class Camera2 {

    HandlerThread mCameraThread;
    Handler mCameraHandler;

    CameraManager mCameraManager;
    CameraDevice mCameraDevice;
    CameraCaptureSession mCameraCaptureSession;

    String[] mCameraIDList;

    Size mPreviewSize;
    Size mCaptureSize;

    SurfaceTexture mSurfaceTexture;           // 用于相机预览
    ImageReader mImageReader;   // 用于相机Capture 暂时不用

    Surface mSurface;

    public Camera2(Context context) {
        mCameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraIDList = mCameraManager.getCameraIdList();
        }catch (CameraAccessException e) {
            e.printStackTrace();
            return;
        }

        mCameraThread = new HandlerThread("CameraThread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    public void open(int id) {
        if(id >= mCameraIDList.length) {
            throw new RuntimeException("NO Such Camera");
        }
        if(mSurfaceTexture == null)
            throw new RuntimeException("SurfaceTexture is null");

        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraIDList[id]);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] previewSizes = map.getOutputSizes(SurfaceHolder.class);
            mPreviewSize = new Size(1440,1080);
            Size[] captureSizes = map.getOutputSizes(ImageFormat.JPEG);
            mCaptureSize = captureSizes[0];
        }catch (CameraAccessException e) {
            Log.e("HJ","failed to get characteristics");
            return;
        }

        try {
            mCameraManager.openCamera(mCameraIDList[id],mCamDevCallback,mCameraHandler);
        }catch (SecurityException e) {
            e.printStackTrace();
            return;
        }catch (CameraAccessException e) {
            e.printStackTrace();
            return;
        }
    }

    public void startPreview() {
        if (mCameraCaptureSession == null) {
            try {
                Thread.sleep(500);
            }catch (InterruptedException e) {
                throw new RuntimeException("startPreview timeout!",e);
            }
            if(mCameraCaptureSession == null) {
                Log.e("HJ", "startPreview failed, CameraCaptureSession not exist");
                return;
            }
        }
        try {
            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(mSurface);
            builder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            CaptureRequest captureRequest = builder.build();
            mCameraCaptureSession.setRepeatingRequest(captureRequest,mCamCapSesCaptureCallback,mCameraHandler);
        }catch (CameraAccessException e ){
            e.printStackTrace();
            return;
        }

    }

    public void stopPreview() {
        if(mCameraCaptureSession == null) {
            Log.e("HJ","startPreview failed, CameraCaptureSession not exist");
            return;
        }
        try {
            mCameraCaptureSession.abortCaptures();
            mCameraCaptureSession.stopRepeating();
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        mCameraCaptureSession.close();
        mCameraCaptureSession = null;
        mCameraDevice.close();
        mCameraDevice = null;
    }


    CameraDevice.StateCallback mCamDevCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            try {
                if(mSurfaceTexture == null) {
                    Log.e("HJ","set SurfaceTexture before open");
                    return;
                }
                mSurface =  new Surface(mSurfaceTexture);
                mImageReader = ImageReader.newInstance(mCaptureSize.getWidth(),mCaptureSize.getHeight(),ImageFormat.JPEG,2);
                mCameraDevice.createCaptureSession(Arrays.asList(mSurface,mImageReader.getSurface()),mCamCapSesStateCallback,mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                return;
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            // 当相机设备不再可以使用时调用
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            // 相机设备遭遇严重错误时调用
        }
    };

    CameraCaptureSession.StateCallback mCamCapSesStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mCameraCaptureSession = session;
            Log.d("HJ","create CameraCaptureSession");
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e("hj","onConfigureFailed");
        }
    };

    CameraCaptureSession.CaptureCallback mCamCapSesCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };

}
