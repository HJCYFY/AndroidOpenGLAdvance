package com.example.huajun.opengladvance.level9;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by huajun on 18-7-20.
 */

public class Pikachu {
    static final String vertexCode =
            "attribute vec3 vPosition;" +
            "attribute vec3 vNormal;" +             //法向量
            "attribute vec2 vCoord;" +
            "uniform mat4 vMatrix;" +
            "uniform vec3 vKa;" +
            "uniform vec3 vKd;" +
            "uniform vec3 vKs;" +
            "varying vec2 textureCoordinate;" +
            "varying vec4 vDiffuse;" +              //用于传递给片元着色器的散射光最终强度
            "varying vec4 vAmbient;" +              //用于传递给片元着色器的环境光最终强度
            "varying vec4 vSpecular;" +             //用于传递给片元着色器的镜面光最终强度
            "void main(){" +
            "    gl_Position = vMatrix*vec4(vPosition,1);" +
            "    textureCoordinate = vCoord;" +
            "    vec3 lightLocation=vec3(0.0,-200.0,-500.0);" +         //光照位置
            "    vec3 camera=vec3(0,200.0,0);" +
            "    float shininess=10.0;" +                               //粗糙度，越小越光滑
            "     vec3 newNormal=normalize((vMatrix*vec4(vNormal+vPosition,1)).xyz-(vMatrix*vec4(vPosition,1)).xyz);" +
            "     vec3 vp=normalize(lightLocation-(vMatrix*vec4(vPosition,1)).xyz);" +
            "     vDiffuse=vec4(vKd,1.0)*max(0.0,dot(newNormal,vp));" +                 //计算散射光的最终强度
            "     vec3 eye= normalize(camera-(vMatrix*vec4(vPosition,1)).xyz);" +
            "     vec3 halfVector=normalize(vp+eye);" +                                 //求视线与光线的半向量
            "     float nDotViewHalfVector=dot(newNormal,halfVector);" +                //法线与半向量的点积
            "     float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess));" +      //镜面反射光强度因子
            "     vSpecular=vec4(vKs,1.0)*powerFactor;" +                               //计算镜面光的最终强度
            "     vAmbient=vec4(vKa,1.0);" +
            "}";

    static final String fregmentCode =
            "precision mediump float;" +
            "varying vec2 textureCoordinate;" +
            "uniform sampler2D vTexture;" +
            "varying vec4 vDiffuse;" +              //接收从顶点着色器过来的散射光分量
            "varying vec4 vAmbient;" +              //接收传递给片元着色器的环境光分量
            "varying vec4 vSpecular;" +             //接收传递给片元着色器的镜面光分量
            "void main() {" +
            "    vec4 finalColor=texture2D(vTexture,textureCoordinate);" +
            "    gl_FragColor=finalColor*vAmbient+finalColor*vSpecular+finalColor*vDiffuse;" +
            "}";

    int Program;
    int PositionHandle;
    int NormalHandle;
    int CoordHandle;
    int MatrixHandle;
    int KaHandle;
    int KdHandle;
    int KsHandle;
    int TextureHandle;

    Obj3D obj;

    int texture;

    Pikachu(){
        Program = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fregmentCode);
        GLES20.glAttachShader(Program,vertexShader);
        GLES20.glAttachShader(Program,fragmentShader);
        GLES20.glLinkProgram(Program);

        PositionHandle = GLES20.glGetAttribLocation(Program,"vPosition");
        NormalHandle = GLES20.glGetAttribLocation(Program,"vNormal");
        CoordHandle = GLES20.glGetAttribLocation(Program,"vCoord");
        MatrixHandle = GLES20.glGetUniformLocation(Program,"vMatrix");
        Log.d("HJ","MatrixHandle "+MatrixHandle);
        KaHandle = GLES20.glGetUniformLocation(Program,"vKa");
        Log.d("HJ","KaHandle "+KaHandle);
        KdHandle = GLES20.glGetUniformLocation(Program,"vKd");
        Log.d("HJ","KdHandle "+KdHandle);
        KsHandle = GLES20.glGetUniformLocation(Program,"vKs");
        Log.d("HJ","KsHandle "+KsHandle);
        TextureHandle = GLES20.glGetUniformLocation(Program,"vTexture");

        checkGLError("te 1");
    }

    public void draw(float[] matrix) {
        GLES20.glUseProgram(Program);
        GLES20.glEnableVertexAttribArray(PositionHandle);
        GLES20.glVertexAttribPointer(PositionHandle,3,GLES20.GL_FLOAT,false,0,obj.vert);
        GLES20.glEnableVertexAttribArray(NormalHandle);
        GLES20.glVertexAttribPointer(NormalHandle,3,GLES20.GL_FLOAT,false,0,obj.vertNorl);
        GLES20.glEnableVertexAttribArray(CoordHandle);
        GLES20.glVertexAttribPointer(CoordHandle,2,GLES20.GL_FLOAT,false,0,obj.vertTexture);

        GLES20.glUniformMatrix4fv(MatrixHandle,1,false,matrix,0);

        GLES20.glUniform3fv(KaHandle,1,obj.mtl.Ka,0);
        GLES20.glUniform3fv(KdHandle,1,obj.mtl.Kd,0);
        GLES20.glUniform3fv(KsHandle,1,obj.mtl.Ks,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+texture);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
        GLES20.glUniform1i(TextureHandle,texture);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,obj.vertCount);

        GLES20.glDisableVertexAttribArray(PositionHandle);
        GLES20.glDisableVertexAttribArray(NormalHandle);
        GLES20.glDisableVertexAttribArray(CoordHandle);
    }


    public int loadShader(int shaderType,String shaderCode) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void setObj(Obj3D obj) {
        this.obj = obj;
    }

    public void setTexture(int texture){
        this.texture = texture;
    }


    public void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError())!= GLES20.GL_NO_ERROR) {
            Log.e("HJ",glOperation + ": glError "+error);
            throw new RuntimeException(glOperation+"glError "+error);
        }
    }
}
