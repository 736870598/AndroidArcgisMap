package com.bs.graduation.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.esri.android.map.MapView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 保存绘制好的图层
 */
public class SaveUtils {

    /**
     * 保存图层
     */
    public static boolean saveMapViewScreen(Context context, MapView v) {
        v.clearFocus();
        v.setPressed(false);

        //能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while(cacheBitmap == null){
            cacheBitmap = v.getDrawingMapCache(0, 0, v.getWidth(), v.getHeight());
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return saveBitmap(context.getApplicationContext(), bitmap);

    }


    /**
     * 保存bitmap图片
     * @return
     */
    public static boolean saveBitmap(Context context, Bitmap bmp) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String name = System.currentTimeMillis() + ".png";
        File f = new File( path, name);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(context, "保存成功，保存在：内存卡/" + name, Toast.LENGTH_SHORT).show();

            //发送广播让系统扫描改文件，保证在电脑上能显示出来
            Intent intent = new Intent();
            Uri uri = Uri.fromFile(f);
            intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            context.sendBroadcast(intent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
