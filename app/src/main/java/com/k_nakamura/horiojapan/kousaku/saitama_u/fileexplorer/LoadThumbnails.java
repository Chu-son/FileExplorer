package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by user on 2016/01/14.
 */
public class LoadThumbnails extends AsyncTask<Void , Void , Bitmap>
{
    ImageView iv;
    String fPath;
    BitmapFactory.Options options ;
    Context context;

    static ThumbnailCache cache;

    public LoadThumbnails(Context context ,ImageView iv,String fPath)
    {
        super();
        this.iv = iv;
        this.fPath = fPath;
        this.context = context;

        if(cache == null) cache = new ThumbnailCache();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap iconImage = cache.getBitmap(fPath);
        if(iconImage == null) {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fPath, options);
            int scaleW = options.outWidth / 200 + 1;
            int scaleH = options.outHeight / 200 + 1;
            int scale = Math.max(scaleW, scaleH);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            //return BitmapFactory.decodeFile(fName,options);

            iconImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_file);
            iconImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fPath, options), iconImage.getWidth(), iconImage.getHeight());
            cache.putBitmap(fPath, iconImage);
        }
        return iconImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }
}
