package com.example.huajun.opengladvance.level9;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huajun on 18-7-20.
 */

public class GLRender implements GLSurfaceView.Renderer {

    Context  mContext;
    private List<Pikachu> parts;
    float[] matrix = new float[16];

    public GLRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.3f,0.3f,0.3f,1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        List<Obj3D> model=ObjReader.readMultiObj(mContext,"pikachu.obj");
        Log.d("HJ","model "+model.size());
        parts=new ArrayList<>();
        for (int i=0;i<model.size();i++){
            Pikachu f=new Pikachu();
            f.setObj(model.get(i));
            Bitmap bmp = null;
            try{
                bmp = BitmapFactory.decodeStream(mContext.getResources().getAssets().open(model.get(i).mtl.map_Kd));
            }catch (IOException e) {
                e.printStackTrace();
                return;
            }
            int[] texture=new int[1];
            texture[0] = 0;
            if(bmp!=null&&!bmp.isRecycled()){
                //生成纹理
                GLES20.glGenTextures(1,texture,0);
                //生成纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
                //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
                //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
                //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
                //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
                //根据以上指定的参数，生成一个2D纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
            }
            f.setTexture(texture[0]);
            parts.add(f);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Matrix.setIdentityM(matrix,0);
        Matrix.translateM(matrix,0,0,-0.3f,0);
        Matrix.scaleM(matrix,0,0.008f,0.008f*width/height,0.008f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        for (Pikachu f:parts){
            Matrix.rotateM(matrix,0,0.3f,0,1,0);
            f.draw(matrix);
        }
    }
}
