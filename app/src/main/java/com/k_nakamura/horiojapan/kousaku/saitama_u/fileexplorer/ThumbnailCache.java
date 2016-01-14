package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by user on 2016/01/14.
 */
public class ThumbnailCache
{
    private LruCache<String,Bitmap> mMemoryCache;

    ThumbnailCache()
    {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //KB
                return value.getRowBytes() * value.getHeight()/1024;
            }
        };
    }

    public Bitmap getBitmap(String path)
    {
        return mMemoryCache.get(path);
    }

    /**
     *
     * @param path
     * @param bitmap
     * @return キャッシュが存在していればtrue
     */
    public boolean putBitmap(String path,Bitmap bitmap)
    {
        Bitmap old = mMemoryCache.put(path,bitmap);
        if(old != null)
        {
            if(!old.isRecycled()) old.recycle();
            old = null;

            return true;
        }
        return false;
    }

}
