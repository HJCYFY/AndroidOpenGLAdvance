package com.example.huajun.opengladvance.level2;

import android.content.res.AssetManager;
import android.opengl.ETC1;
import android.opengl.ETC1Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by huajun on 18-7-9.
 * 读取 PKM 压缩文件,PKM 属于 ETC1 压缩纹理,所有 Android 设备均支持这种压缩纹理
 */

public class ZipPKMReader {
    private static final String path = "assets/cc.zip";
    private ZipInputStream zipInputStream;
    private AssetManager assetManager;
    private ZipEntry zipEntry;
    private ByteBuffer headerBuffer;

    public ZipPKMReader(AssetManager manager) {
        assetManager = manager;
    }

    public boolean open() {
        if(path == null)
            return false;
        try {
            if(path.startsWith("assets/")){
                InputStream s=assetManager.open(path.substring(7));
                zipInputStream=new ZipInputStream(s);
            }else{
                File f=new File(path);
                if(!f.exists())
                    return false;
                zipInputStream=new ZipInputStream(new FileInputStream(path));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean open(String filePath) {
        if(filePath == null)
            return false;
        try {
            if(filePath.startsWith("assets/")){
                InputStream s=assetManager.open(filePath.substring(7));
                zipInputStream=new ZipInputStream(s);
            }else{
                File f=new File(filePath);
                if(!f.exists())
                    return false;
                zipInputStream=new ZipInputStream(new FileInputStream(filePath));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        if(zipInputStream != null) {
            try {
                zipInputStream.closeEntry();
                zipInputStream.close();
                zipInputStream = null;
            }catch (IOException e){
                e.printStackTrace();
            }
            if(headerBuffer != null) {
                headerBuffer.clear();
                headerBuffer = null;
            }
        }
    }

    /***********************
     * 判断文件是否结束
     * @return 文件结束/出错/不存在 返回 true,否则返回 false
     */
    private boolean isEOF(){
        if(zipInputStream != null) {
            try {
                zipEntry = zipInputStream.getNextEntry();
            }catch (IOException e) {
                e.printStackTrace();
                return true;
            }
            if(zipEntry != null) {
                return false;
            }
        }
        return true;
    }

    private ETC1Util.ETC1Texture createTexture(InputStream inputStream) throws IOException {
        int width = 0;
        int height = 0;

        byte[] ioBuffer = new byte[4096];

        if(inputStream.read(ioBuffer,0, ETC1.ETC_PKM_HEADER_SIZE)!= ETC1.ETC_PKM_HEADER_SIZE) {
            throw new IOException("Unable to read PKM file header.");
        }
        if(headerBuffer == null) {
            headerBuffer = ByteBuffer.allocateDirect(ETC1.ETC_PKM_HEADER_SIZE).order(ByteOrder.nativeOrder());
        }

        headerBuffer.put(ioBuffer,0,ETC1.ETC_PKM_HEADER_SIZE).position(0);
        if(!ETC1.isValid(headerBuffer)) {
            throw new IOException("Not a PKM file.");
        }
        width = ETC1.getWidth(headerBuffer);
        height = ETC1.getHeight(headerBuffer);

        int encodedSize = ETC1.getEncodedDataSize(width, height);
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(ByteOrder.nativeOrder());
        int len;
        while ((len =inputStream.read(ioBuffer))!=-1){
            dataBuffer.put(ioBuffer,0,len);
        }
        dataBuffer.position(0);
        return new ETC1Util.ETC1Texture(width, height, dataBuffer);
    }

    public ETC1Util.ETC1Texture getTexture() {
        if(!isEOF()){
            try {
                ETC1Util.ETC1Texture e= createTexture(zipInputStream);
                return e;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
