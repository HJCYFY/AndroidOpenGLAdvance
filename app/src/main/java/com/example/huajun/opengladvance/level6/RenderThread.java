package com.example.huajun.opengladvance.level6;

/**
 * Created by huajun on 18-7-17.
 */

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;

/**
 * This class handles all OpenGL rendering.
 * <p>
 * Start the render thread after the Surface has been created.
 */
public class RenderThread extends Thread {
    // Object must be created on render thread to get correct Looper, but is used from
    // UI thread, so we need to declare it volatile to ensure the UI thread sees a fully
    // constructed object.
    private volatile RenderHandler mHandler;
    private volatile SurfaceHolder mSurfaceHolder;  // may be updated by UI thread

    // Used to wait for the thread to start.
    private Object mStartLock = new Object();
    private boolean mReady = false;

    private EglCore mEglCore;

    SurfaceTexture mSurfaceTexture;
    Camera2 mCamera2;
    CameraFilter mCameraFilter;
    GrayImage mGrayImage;
    // Used for off-screen rendering.
    private int[] mTexture;
    private int[] mFramebuffer;
    private int[] mDepthBuffer;

    public RenderThread(SurfaceHolder holder, Context context) {
        mSurfaceHolder = holder;
        mCamera2 = new Camera2(context);
    }

    /**
     * Thread entry point.
     * <p>
     * The thread should not be started until the Surface associated with the SurfaceHolder
     * has been created.  That way we don't have to wait for a separate "surface created"
     * message to arrive.
     */
    @Override
    public void run() {
        Log.d("HJ","run");
        Looper.prepare();
        mHandler = new RenderHandler(this);
        mEglCore = new EglCore(null,EglCore.FLAG_TRY_GLES3);

        synchronized (mStartLock) {
            mReady = true;
            mStartLock.notify();
        }
        Looper.loop();


        Log.d("HJ","loop quit");
        releaseGl();
        mEglCore.release();

        synchronized (mStartLock) {
            mReady = false;
            Log.d("HJ","mReady = false");
        }
    }

    /**
     * Waits until the render thread is ready to receive messages.
     * <p>
     * Call from the UI thread.
     */
    public void waitUntilReady() {
        synchronized (mStartLock) {
            while (!mReady) {
                try {
                    mStartLock.wait();
                } catch (InterruptedException ie) { /* not expected */ }
            }
        }
    }

    /**
     * Shuts everything down.
     */
    private void shutdown() {
        mCamera2.stopPreview();
        mCamera2.close();
        Looper.myLooper().quit();
    }

    /**
     * Returns the render thread's Handler.  This may be called from any thread.
     */
    public RenderHandler getHandler() {
        return mHandler;
    }

    /**
     * Prepares the surface.
     */
    private void surfaceCreated() {
        Surface surface = mSurfaceHolder.getSurface();
        prepareGl(surface);
    }

    /**
     * Prepares window surface and GL state.
     */
    private void prepareGl(Surface surface) {

        mEglCore.createWindowSurface(surface);
        mEglCore.makeCurrent();

        mGrayImage = new GrayImage();
        mCameraFilter = new CameraFilter();

        // Set the background color.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Disable depth testing -- we're 2D only.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // Don't need backface culling.  (If you're feeling pedantic, you can turn it on to
        // make sure we're defining our shapes correctly.)
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    /**
     * Releases most of the GL resources we currently hold.
     * <p>
     * Does not release EglCore.
     */
    private void releaseGl() {
        EglCore.checkEglError("releaseGl start");
        mEglCore.releaseSurface();

        if(mTexture != null) {
            GLES20.glDeleteTextures(2,mTexture,0);
            mTexture = null;
        }
        if(mFramebuffer != null) {
            GLES20.glDeleteFramebuffers(1, mFramebuffer, 0);
            mFramebuffer = null;
        }
        if(mDepthBuffer != null) {
            GLES20.glDeleteRenderbuffers(1, mDepthBuffer, 0);
            mDepthBuffer = null;
        }
    }

    /**
     * Handles changes to the size of the underlying surface.  Adjusts viewport as needed.
     * Must be called before we start drawing.
     * (Called from RenderHandler.)
     */
    private void surfaceChanged(int width, int height) {

        prepareFramebuffer(width, height);

        // Use full window.
        GLES20.glViewport(0, 0, width, height);

        mSurfaceTexture = new SurfaceTexture(mTexture[0]);
        mSurfaceTexture.setDefaultBufferSize(1440,1080);
        mSurfaceTexture.setOnFrameAvailableListener(listener);
        mCamera2.setSurfaceTexture(mSurfaceTexture);
        mCamera2.open(0);
        mCamera2.startPreview();
    }

    /**
     * Prepares the off-screen framebuffer.
     */
    private void prepareFramebuffer(int width, int height) {
        EglCore.checkEglError("prepareFramebuffer start");

        mTexture = new int[2];
        mFramebuffer = new int[1];
        mDepthBuffer = new int[1];

        // Create a texture object and bind it.  This will be the color buffer.
        GLES20.glGenTextures(2, mTexture, 0);
        EglCore.checkEglError("glGenTextures");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTexture[0]);
        EglCore.checkEglError("glBindTexture " + mTexture[0]);

        // Set parameters.  We're probably using non-power-of-two dimensions, so
        // some values may not be available for use.
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        EglCore.checkEglError("glTexParameter");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[1]);
        EglCore.checkEglError("glBindTexture " + mTexture[1]);

        // Set parameters.  We're probably using non-power-of-two dimensions, so
        // some values may not be available for use.
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        EglCore.checkEglError("glTexParameter");

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,width,height,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);

        // Create framebuffer object and bind it.
        GLES20.glGenFramebuffers(1, mFramebuffer, 0);
        EglCore.checkEglError("glGenFramebuffers");
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer[0]);
        EglCore.checkEglError("glBindFramebuffer " + mFramebuffer[0]);

        // Create a depth buffer and bind it.
        GLES20.glGenRenderbuffers(1, mDepthBuffer, 0);
        EglCore.checkEglError("glGenRenderbuffers");
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer[0]);
        EglCore.checkEglError("glBindRenderbuffer " + mDepthBuffer[0]);

        // Allocate storage for the depth buffer.
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                width, height);
        EglCore.checkEglError("glRenderbufferStorage");

        // Attach the depth buffer and the texture (color buffer) to the framebuffer object.
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mDepthBuffer[0]);
        EglCore.checkEglError("glFramebufferRenderbuffer");
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mTexture[1], 0);
        EglCore.checkEglError("glFramebufferTexture2D");

        // See if GLES is happy with all this.
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        // Switch back to the default framebuffer.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        EglCore.checkEglError("prepareFramebuffer done");
    }


    private void doFrame() {
        draw();
        boolean swapResult = mEglCore.swapBuffers();
        if (!swapResult) {
            // This can happen if the Activity stops without waiting for us to halt.
            Log.w("HJ", "swapBuffers failed, killing renderer thread");
            shutdown();
            return;
        }
    }

    /**
     * Draws the scene.
     */
    private void draw() {
        mEglCore.checkEglError("draw start");

        // Clear to a non-black color to make the content easily differentiable from
        // the pillar-/letter-boxing.
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // render image to mFramebuffer[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramebuffer[0]);

        mSurfaceTexture.updateTexImage();
        mCameraFilter.draw(mTexture[0]);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);

        mGrayImage.draw(mTexture[1]);

        mEglCore.checkEglError("draw done");
    }

    /**
     * Handler for RenderThread.  Used for messages sent from the UI thread to the render thread.
     * <p>
     * The object is created on the render thread, and the various "send" methods are called
     * from the UI thread.
     */
    public static class RenderHandler extends Handler {
        private static final int MSG_SURFACE_CREATED = 0;
        private static final int MSG_SURFACE_CHANGED = 1;
        private static final int MSG_SHUTDOWN = 5;

        // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
        // but no real harm in it.
        private WeakReference<RenderThread> mWeakRenderThread;

        /**
         * Call from render thread.
         */
        public RenderHandler(RenderThread rt) {
            mWeakRenderThread = new WeakReference<RenderThread>(rt);
        }

        /**
         * Sends the "surface created" message.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceCreated() {
            sendMessage(obtainMessage(RenderHandler.MSG_SURFACE_CREATED));
        }

        /**
         * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceChanged(@SuppressWarnings("unused") int format,
                                       int width, int height) {
            // ignore format
            sendMessage(obtainMessage(RenderHandler.MSG_SURFACE_CHANGED, width, height));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendShutdown() {
            sendMessage(obtainMessage(RenderHandler.MSG_SHUTDOWN));
        }

        @Override  // runs on RenderThread
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);

            RenderThread renderThread = mWeakRenderThread.get();
            if (renderThread == null) {
                Log.w("HJ", "RenderHandler.handleMessage: weak ref is null");
                return;
            }

            switch (what) {
                case MSG_SURFACE_CREATED:
                    renderThread.surfaceCreated();
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThread.surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_SHUTDOWN:
                    renderThread.shutdown();
                    break;
                default:
                    throw new RuntimeException("unknown message " + what);
            }
        }
    }


    SurfaceTexture.OnFrameAvailableListener listener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            if(mReady) {
                doFrame();
            }
        }
    };
}
