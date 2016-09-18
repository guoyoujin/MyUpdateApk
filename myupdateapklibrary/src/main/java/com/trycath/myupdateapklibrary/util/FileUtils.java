package com.trycath.myupdateapklibrary.util;

import android.os.Environment;
import android.util.Log;

import com.trycath.myupdateapklibrary.UpdateKey;
import com.trycath.myupdateapklibrary.model.AppInfoModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-09 15:25
 * @version: V1.0 <描述当前版本功能>
 */

public class FileUtils {
    private static final String TAG = "FileUtils";
    /**
     * InputStrem 转byte[]
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static byte[] readStreamToBytes(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int length = -1;
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        out.flush();
        byte[] result = out.toByteArray();
        in.close();
        out.close();
        return result;
    }

    /**
     * 写入文件
     *
     * @param in
     * @param file
     */
    public static void writeFile(InputStream in, File file) throws IOException {
        Log.d(TAG,"write file=====start==");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (file != null && file.exists())
            file.delete();

        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[1024 * 128];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        Log.d(TAG,"write file====success===");
        out.flush();
        out.close();
        in.close();

    }
    
    
    public static long getFileSize(File file){
        if(file.exists() && file.isFile()){
            return file.length();
        }
        return 0;
    }
    
    public static File getFile(AppInfoModel appInfoModel){
        if (appInfoModel==null)
            return null;
        String fileName= "";
        if(UpdateKey.DOWNLOAD_APK_NAME!=null && !"".equals(UpdateKey.DOWNLOAD_APK_NAME)){
            fileName = String.format("%s_%s.apk",UpdateKey.DOWNLOAD_APK_NAME,appInfoModel.getVersionShort());
        }else{
            fileName = String.format("%s_%s.apk",appInfoModel.getName(),appInfoModel.getVersionShort());
        }
       return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
    }
}