package com.example.huajun.opengladvance.level8;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by huajun on 18-7-19.
 */

public class CubeWithLight {

    //在片元着色器中计算光照会获得更好更真实的光照效果，但是会比较耗性能
    static final String vertexCode =
            "attribute vec4 aPosition;" +
            "attribute vec3 aNormal;" +
            "uniform mat4 uMatrix;" +
            "uniform vec4 uBaseColor;" +
            "uniform vec3 uLightPosition;" +
            "varying vec3 vPosition;" +
            "varying vec4 vColor;" +
            "varying vec3 vLightPosition;" +
            "varying vec3 vLightDirection;" +
            "varying vec3 vNormalDirection;" +
            "void main(){" +
            "    vPosition = (uMatrix*aPosition).xyz;" +
            "    vColor = uBaseColor;" +
            "    vLightPosition = uLightPosition;" +
            "    vLightDirection = normalize(uLightPosition-vPosition);" +      // 光照方向
            "    vNormalDirection = normalize(mat3(uMatrix)*aNormal);" +        // 模型变换后的法线向量
            "    gl_Position = uMatrix*aPosition;" +
            "}";


    static final String fragmentCode =
            "precision mediump float;" +
            "varying vec3 vPosition;" +
            "varying vec4 vColor;" +
            "varying vec3 vLightPosition;" +
            "varying vec3 vLightDirection;" +
            "varying vec3 vNormalDirection;" +
            "uniform vec3 uLightColor;" +
            "uniform float uAmbientStrength;" +
            "uniform float uDiffuseStrength;" +
            "uniform float uSpecularStrength;" +
            //环境光的计算
            "vec4 ambientColor(){" +
            "    vec3 ambient = uAmbientStrength * uLightColor;" +
            "    return vec4(ambient,1.0);" +
            "}" +

            //漫反射的计算
            "vec4 diffuseColor(){" +
            "    float diff = max(dot(vNormalDirection,vLightDirection), 0.0);" +           // max(cos(入射角)，0)
            "    vec3 diffuse=uDiffuseStrength * diff * uLightColor;" +     // 材质的漫反射系数*max(cos(入射角)，0)*光照颜色
            "    return vec4(diffuse,1.0);" +
            "}" +

            //镜面光计算，镜面光计算有两种方式，一种是冯氏模型，一种是Blinn改进的冯氏模型
            //这里使用的是改进的冯氏模型，基于Half-Vector的计算方式
            "vec4 specularColor(){" +
            "    vec3 viewDirection=normalize(vec3(0,0,vLightPosition.z)-vPosition);" +       //观察方向，这里将观察点固定在（0，0，uLightPosition.z）处
            "    vec3 hafVector=normalize(vLightDirection+viewDirection);" +                 //观察向量与光照向量的半向量
            "    float diff=pow(max(dot(vNormalDirection,hafVector),0.0),4.0);" +                     //max(0,cos(半向量与法向量的夹角)^粗糙度
            "    vec3 specular=uSpecularStrength*diff*uLightColor;" +                       //材质的镜面反射系数*max(0,cos(半向量与法向量的夹角)^粗糙度*光照颜色
            "    return vec4(specular,1.0);" +
            "}" +
            "void main(){" +
            "    gl_FragColor=  min((ambientColor() + diffuseColor() + specularColor()),1.0) *vColor;" +
            "}";

    // 顶点坐标 以及 顶点法向量(由它所在的面决定)
    private final float[] vertexAndNormal=new float[]{
            // 前面
            0.5f,  -0.5f,  -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,   0.5f,  -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  -0.5f,   0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  -0.5f,   0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,   0.5f,  -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,   0.5f,   0.5f,  1.0f,  0.0f,  0.0f,
            // 后面
            -0.5f,   0.5f,  -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  -0.5f,  -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,   0.5f,   0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,   0.5f,   0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  -0.5f,  -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  -0.5f,   0.5f, -1.0f,  0.0f,  0.0f,
            // 左面
            -0.5f,  -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
             0.5f,  -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f,  -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f,  -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
             0.5f,  -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
             0.5f,  -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            // 右面
             0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  0.0f,
             0.5f,  0.5f,  0.5f,  0.0f, 1.0f,  0.0f,
             0.5f,  0.5f,  0.5f,  0.0f, 1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,  0.0f,
            // 上面
             0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            // 下面
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
             0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
             0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
    };

    int Program;
    int PositionHandle;
    int NormalHandle;
    int MatrixHandle;
    int BaseColorHandle;
    int LightColorHandle;
    int AmbientStrengthHandle;
    int DiffuseStrengthHandle;
    int SpecularStrengthHandle;
    int LightPositionHandle;

    FloatBuffer PositionAndNormalBuffer;

    float[] BaseColor = new float[]{0f,1.0f,0f,1.0f};
    float[] LightColor = new float[]{1.0f,1.0f,1.0f};
    float[] LightPosition = new float[]{1.0f,1.0f,0.5f};

    float AmbientStrength = 0.3f;
    float DiffuseStrength = 0.6f;
    float SpecularStrength= 0.8f;

    CubeWithLight() {
        Log.d("HJ","p1");
        PositionAndNormalBuffer = ByteBuffer.allocateDirect(vertexAndNormal.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        PositionAndNormalBuffer.put(vertexAndNormal).position(0);

        Log.d("HJ","p2");
        Program = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentCode);

        Log.d("HJ","p3");
        GLES20.glAttachShader(Program,vertexShader);
        GLES20.glAttachShader(Program,fragmentShader);
        GLES20.glLinkProgram(Program);

        NormalHandle = GLES20.glGetAttribLocation(Program,"aNormal");
        Log.d("HJ","NormalHandle "+NormalHandle);
        PositionHandle = GLES20.glGetAttribLocation(Program,"aPosition");
        Log.d("HJ","PositionHandle "+PositionHandle);
        MatrixHandle = GLES20.glGetUniformLocation(Program,"uMatrix");
        Log.d("HJ","MatrixHandle "+MatrixHandle);
        BaseColorHandle = GLES20.glGetUniformLocation(Program,"uBaseColor");
        Log.d("HJ","BaseColorHandle "+BaseColorHandle);
        LightColorHandle = GLES20.glGetUniformLocation(Program,"uLightColor");
        Log.d("HJ","LightColorHandle "+LightColorHandle);
        AmbientStrengthHandle = GLES20.glGetUniformLocation(Program,"uAmbientStrength");
        Log.d("HJ","AmbientStrengthHandle "+AmbientStrengthHandle);
        DiffuseStrengthHandle = GLES20.glGetUniformLocation(Program,"uDiffuseStrength");
        Log.d("HJ","DiffuseStrengthHandle "+DiffuseStrengthHandle);
        SpecularStrengthHandle = GLES20.glGetUniformLocation(Program,"uSpecularStrength");
        Log.d("HJ","SpecularStrengthHandle "+SpecularStrengthHandle);
        LightPositionHandle = GLES20.glGetUniformLocation(Program,"uLightPosition");
        Log.d("HJ","LightPositionHandle "+LightPositionHandle);
        checkGLError("33333333333");
    }

    public void draw(float[] matrix) {
        checkGLError("draw 0");
        GLES20.glUseProgram(Program);
        checkGLError("draw 0.11");
        GLES20.glEnableVertexAttribArray(PositionHandle);
        checkGLError("draw 0.44");
        GLES20.glEnableVertexAttribArray(NormalHandle);
        checkGLError("draw 0.22");
        PositionAndNormalBuffer.position(0);
        GLES20.glVertexAttribPointer(PositionHandle,3,GLES20.GL_FLOAT,false,4*6,PositionAndNormalBuffer);
        checkGLError("draw 0.33");
        PositionAndNormalBuffer.position(3);
        GLES20.glVertexAttribPointer(NormalHandle,3,GLES20.GL_FLOAT,false,4*6,PositionAndNormalBuffer);

        checkGLError("draw 1");
        GLES20.glUniformMatrix4fv(MatrixHandle,1,false,matrix,0);
        GLES20.glUniform4fv(BaseColorHandle,1,BaseColor,0);
        GLES20.glUniform3fv(LightColorHandle,1,LightColor,0);
        GLES20.glUniform1f(AmbientStrengthHandle,AmbientStrength);
        GLES20.glUniform1f(DiffuseStrengthHandle,DiffuseStrength);
        GLES20.glUniform1f(SpecularStrengthHandle,SpecularStrength);
        GLES20.glUniform3fv(LightPositionHandle,1,LightPosition,0);
        checkGLError("draw 2");
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,vertexAndNormal.length/6);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        checkGLError("draw 3");
        GLES20.glDisableVertexAttribArray(PositionHandle);
        GLES20.glDisableVertexAttribArray(NormalHandle);
        checkGLError("draw finish");
    }

    public void setStrength(float ambient,float diffuse,float specular) {
        Log.d("HJ","ambient "+ambient+" diffuse "+diffuse+" specular "+specular);
        AmbientStrength =ambient;
        DiffuseStrength = diffuse;
        SpecularStrength = specular;
    }

    public int loadShader(int shaderType,String shaderCode) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ",glOperation + ": glError "+error);
            throw new RuntimeException(glOperation+"glError "+error);
        }
    }
}
