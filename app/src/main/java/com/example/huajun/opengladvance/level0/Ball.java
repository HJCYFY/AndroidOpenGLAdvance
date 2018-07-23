package com.example.huajun.opengladvance.level0;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by huajun on 18-7-6.
 */

public class Ball extends BaseShape{
    private final String vertexShaderCode =
            "uniform mat4 vMatrix; " +
            "varying vec4 vColor;" +
            "attribute vec4 vPosition;" +
            "void main(){" +
            "    gl_Position=vMatrix*vPosition;" +
            "    float color;" +
        "        color=(vPosition.z+1.0)/2.0;" +
            "    vColor=vec4(color,color,color,1.0);" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying  vec4 vColor;" +
                    "void main(){" +
                    "   gl_FragColor = vColor;" +
                    "}";

    private int mPositionHandle;
    private int mMatrixHandler;

    private int numOfTriangle;

    private float[] creatBallPos(){
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        float step = 1.0f;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0);
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0);
            h1 = (float)Math.sin(i * Math.PI / 180.0);
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0);
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                data.add(r2 * cos);
                data.add(h2);
                data.add(r2 * sin);
                data.add(r1 * cos);
                data.add(h1);
                data.add(r1 * sin);
            }
        }
        float[] ret = new float[data.size()];
        for(int i=0;i<ret.length;++i){
            ret[i] = data.get(i);
        }
        return ret;
    }

    Ball() {
        float[] ballPositions = creatBallPos();
        numOfTriangle = ballPositions.length / 3;
        VertexBuffer = ByteBuffer.allocateDirect(ballPositions.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        VertexBuffer.put(ballPositions).position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program,vertexShader);
        GLES20.glAttachShader(Program,fragmentShader);
        GLES20.glLinkProgram(Program);
    }

    @Override
    public void draw(float[] matrix) {
        Log.d("HJ","Ball draw");
        GLES20.glUseProgram(Program);
        GLES20.glDisable(GLES20.GL_BLEND_COLOR);

        mPositionHandle = GLES20.glGetAttribLocation(Program,"vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,VertexBuffer);

        mMatrixHandler = GLES20.glGetUniformLocation(Program,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,matrix,0);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,numOfTriangle);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
