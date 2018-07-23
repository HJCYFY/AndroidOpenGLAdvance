package com.example.huajun.opengladvance.level4;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION;
import static android.opengl.EGL14.EGL_NO_CONTEXT;

/**
 * Created by huajun on 18-7-10.
 */

public class EGLHelper {
    private int red=8;
    private int green=8;
    private int blue=8;
    private int alpha=8;
    private int depth=16;
    private int renderType=4;

    private EGLContext shareContext=EGL10.EGL_NO_CONTEXT;

    private int mSurfaceType = EGL10.EGL_PBUFFER_BIT;

    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLConfig[] mEglConfigs;
    private EGLSurface mEglSurface;
    private EGLContext mEglContext;
    public GL10 mGl10;

    private Object surface_native_obj;

    public EGLHelper(int width,int height) {
        int[] attrs = new int[] {
                EGL10.EGL_RED_SIZE,red,                 // 指定RGBA 中 R 位数
                EGL10.EGL_GREEN_SIZE,green,             // 指定RGBA 中 G 位数
                EGL10.EGL_BLUE_SIZE,blue,               // 指定RGBA 中 B 位数
                EGL10.EGL_ALPHA_SIZE,alpha,             // 指定Alpha大小，以上四项实际上指定了像素格式
                EGL10.EGL_DEPTH_SIZE,depth,             // 指定深度缓存(Z Buffer)大小
                EGL10.EGL_RENDERABLE_TYPE,renderType,   // 指定渲染api类别, 如上一小节描述，这里或者是硬编码的4，或者是EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE};                        // 总是以EGL10.EGL_NONE结尾

        mEgl = (EGL10)EGLContext.getEGL();
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if(mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            Log.e("HJ","EGL NO DISPLAY");
            return;
        }

        int[] version=new int[2];    //主版本号和副版本号
        boolean success = mEgl.eglInitialize(mEglDisplay,version);
        if(!success) {
            int error = mEgl.eglGetError();
            Log.e("HJ","EGL inital failed, Error Code : "+error);
            return;
        }

        /**********************************
              EGLBoolean eglChooseConfig(EGLDisplay display,
                 const EGLint* attribs,    // 你想要的属性事先定义到这个数组里
                 EGLConfig* configs,       // 图形系统将返回若干满足条件的配置到该数组
                 EGLint maxConfigs,        // 上面数组的容量
                 EGLint* numConfigs);      // 图形系统返回的可用的配置个数
         */
        mEglConfigs = new EGLConfig[1];
        int[] configNum=new int[1];
        success = mEgl.eglChooseConfig(mEglDisplay,attrs,mEglConfigs,1,configNum);
        if(configNum[0] == 0 || !success){
            Log.d("HJ","Cant find config");
            return;
        }
        /********************
            EGL_WIDTH, EGL_HEIGHT
            EGL_LARGEST_PBUFFER:        如果参数不合适，可使用最大的pbuffer
            EGL_TEXTURE_FORMAT:         [EGL_NO_TEXTURE] 如果pbuffer要绑定到纹理映射，要指定纹理的格式
            EGL_TEXTURE_TARGET:            [EGL_NO_TEXTURE, EGL_TEXTURE_2D]
            EGL_MIPMAP_TEXTRUE:         [EGL_TRUE, EGL_FALSE]

            创建失败时返回EGL_NO_SURFACE，错误码：
            EGL_BAD_ALLOC:      缺少资源
            EGL_BAD_CONFIG:     配置错误
            EGL_BAD_PARAMETER:  EGL_WIDTH和EGL_HEIGHT为负数
            EGL_BAD_MATCH:      配置错误；如果用于纹理映射，则高宽参数错误；EGL_TEXTURE_FORMAT和EGL_TEXTURE_TARGET只有一个不是EGL_NO_TEXTURE
            EGL_BAD_ATTRIBUTE:  指定了EGL_TEXTURE_FORMAT、EGL_TEXTURE_TARGET或者EGL_MIPMAP_TEXTRUE，却不指定使用OpenGLES在配置里
        *****************************/

        int[] surAttr=new int[]{
                EGL10.EGL_WIDTH,width,
                EGL10.EGL_HEIGHT,height,
                EGL10.EGL_NONE
        };

        mEglSurface=createSurface(surAttr);

        if(mEglSurface == EGL10.EGL_NO_SURFACE) {
            Log.e("HJ","EGL NO SURFACE");
            return;
        }

        int[] contextAttr=new int[]{
                EGL_CONTEXT_CLIENT_VERSION,2,
                EGL10.EGL_NONE
        };
        mEglContext=mEgl.eglCreateContext(mEglDisplay,mEglConfigs[0],shareContext,contextAttr);
        if(mEglContext == EGL10.EGL_NO_CONTEXT) {
            Log.e("HJ","EGL NO CONTEXT");
            return;
        }

        success = mEgl.eglMakeCurrent(mEglDisplay,mEglSurface,mEglSurface,mEglContext);
        if(!success) {
            Log.e("HJ","EGL Make Current failed");
            return;
        }

        mGl10 = (GL10)mEglContext.getGL();
    }

    public void setSurfaceType(int type,Object ... obj){
        mSurfaceType=type;
        if(obj!=null){
            this.surface_native_obj=obj[0];
        }
    }

    private EGLSurface createSurface(int[] attr){
        switch (mSurfaceType){
            case EGL10.EGL_WINDOW_BIT:
                return mEgl.eglCreateWindowSurface(mEglDisplay,mEglConfigs[0],surface_native_obj,attr);
            case EGL10.EGL_PIXMAP_BIT:
                return mEgl.eglCreatePixmapSurface(mEglDisplay,mEglConfigs[0],surface_native_obj,attr);
            default:
                return mEgl.eglCreatePbufferSurface(mEglDisplay,mEglConfigs[0],attr);
        }
    }

    public void destroy() {
        mEgl.eglMakeCurrent(mEglDisplay,mEglSurface,mEglSurface,EGL10.EGL_NO_CONTEXT);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglTerminate(mEglDisplay);
    }

}
