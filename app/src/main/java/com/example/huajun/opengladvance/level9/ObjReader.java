package com.example.huajun.opengladvance.level9;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huajun on 18-7-20.
 */

public class ObjReader {

    public static List<Obj3D> readMultiObj(Context context, String file){
        boolean isAssets;
        ArrayList<Obj3D> data=new ArrayList<>();
        ArrayList<Float> oVs=new ArrayList<Float>();//原始顶点坐标列表
        ArrayList<Float> oVNs=new ArrayList<>();    //原始顶点法线列表
        ArrayList<Float> oVTs=new ArrayList<>();    //原始贴图坐标列表
        ArrayList<Float> oFVs=new ArrayList<>();     //面顶点
        ArrayList<Float> oFVNs=new ArrayList<>();
        ArrayList<Float> oFVTs=new ArrayList<>();
        HashMap<String,MtlInfo> mTls=null;
        HashMap<String,Obj3D> mObjs=new HashMap<>();
        Obj3D nowObj=null;
        MtlInfo nowMtl=null;
        try{
            InputStream inputStream;
            isAssets=true;
            inputStream=context.getAssets().open(file);

            InputStreamReader isr=new InputStreamReader(inputStream);
            BufferedReader br=new BufferedReader(isr);
            String temps;
            while((temps=br.readLine())!=null){
                if("".equals(temps)){

                }else{
                    String[] tempsa=temps.split("[ ]+");
                    switch (tempsa[0].trim()){
                        case "mtllib":  //材质
                            InputStream stream;
                            if (isAssets){
                                stream=context.getAssets().open(tempsa[1]);
                            }else{
                                stream=new FileInputStream(tempsa[1]);
                            }
                            mTls=readMtl(stream);
                            break;
                        case "usemtl":  //采用纹理
                            if(mTls!=null){
                                nowMtl=mTls.get(tempsa[1]);
                            }
                            if(mObjs.containsKey(tempsa[1])){
                                nowObj=mObjs.get(tempsa[1]);
                            }else{
                                nowObj=new Obj3D();
                                nowObj.mtl=nowMtl;
                                mObjs.put(tempsa[1],nowObj);
                            }
                            break;
                        case "v":       //原始顶点
                            read(tempsa,oVs);
                            break;
                        case "vn":      //原始顶点法线
                            read(tempsa,oVNs);
                            break;
                        case "vt":
                            read(tempsa,oVTs);
                            break;
                        case "f":
                            for (int i=1;i<tempsa.length;i++){
                                String[] fs=tempsa[i].split("/");
                                int index;
                                if(fs.length>0){
                                    //顶点索引
                                    index=Integer.parseInt(fs[0])-1;
                                    nowObj.addVert(oVs.get(index*3));
                                    nowObj.addVert(oVs.get(index*3+1));
                                    nowObj.addVert(oVs.get(index*3+2));
                                }
                                if(fs.length>1){
                                    //贴图
                                    index=Integer.parseInt(fs[1])-1;
                                    nowObj.addVertTexture(oVTs.get(index*2));
                                    nowObj.addVertTexture(oVTs.get(index*2+1));
                                }
                                if(fs.length>2){
                                    //法线索引
                                    index=Integer.parseInt(fs[2])-1;
                                    nowObj.addVertNorl(oVNs.get(index*3));
                                    nowObj.addVertNorl(oVNs.get(index*3+1));
                                    nowObj.addVertNorl(oVNs.get(index*3+2));
                                }
                            }
                            break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        for (Map.Entry<String, Obj3D> stringObj3DEntry : mObjs.entrySet()) {
            Obj3D obj = stringObj3DEntry.getValue();
            data.add(obj);
            obj.dataLock();
        }
        return data;
    }

    private static void read(String[] value,ArrayList<Float> list){
        for (int i=1;i<value.length;i++){
            list.add(Float.parseFloat(value[i]));
        }
    }

    private static void read(String[] value,float[] fv){
        for (int i=1;i<value.length&&i<fv.length+1;i++){
            fv[i-1]=Float.parseFloat(value[i]);
        }
    }

    public static HashMap<String,MtlInfo> readMtl(InputStream stream){
        HashMap<String,MtlInfo> map=new HashMap<>();
        try{
            InputStreamReader isr=new InputStreamReader(stream);
            BufferedReader br=new BufferedReader(isr);
            String temps;
            MtlInfo mtlInfo=new MtlInfo();
            while((temps=br.readLine())!=null)
            {
                String[] tempsa=temps.split("[ ]+");
                switch (tempsa[0].trim()){
                    case "newmtl":  //材质
                        mtlInfo=new MtlInfo();
                        mtlInfo.newmtl=tempsa[1];
                        map.put(tempsa[1],mtlInfo);
                        break;
                    case "illum":     //光照模型
                        mtlInfo.illum=Integer.parseInt(tempsa[1]);
                        break;
                    case "Kd":
                        read(tempsa,mtlInfo.Kd);
                        break;
                    case "Ka":
                        read(tempsa,mtlInfo.Ka);
                        break;
                    case "Ke":
                        read(tempsa,mtlInfo.Ke);
                        break;
                    case "Ks":
                        read(tempsa,mtlInfo.Ks);
                        break;
                    case "Ns":
                        mtlInfo.Ns=Float.parseFloat(tempsa[1]);
                    case "map_Kd":
                        mtlInfo.map_Kd=tempsa[1];
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public static  class MtlInfo {
        public String newmtl;
        public float[] Ka=new float[3];     //阴影色
        public float[] Kd=new float[3];     //固有色
        public float[] Ks=new float[3];     //高光色
        public float[] Ke=new float[3];     //
        public float Ns;                    //shininess
        public String map_Kd;               //固有纹理贴图
        public String map_Ks;               //高光纹理贴图
        public String map_Ka;               //阴影纹理贴图

        //denotes the illumination model used by the material.
        // illum = 1 indicates a flat material with no specular highlights,
        // so the value of Ks is not used.
        // illum = 2 denotes the presence of specular highlights,
        // and so a specification for Ks is required.
        public int illum;

    }
}
