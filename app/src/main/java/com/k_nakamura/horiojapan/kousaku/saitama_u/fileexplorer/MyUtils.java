package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by user on 2016/01/17.
 */
public class MyUtils {
    private static String imageExtension[] = {".jpg",".png",".jpeg",".gif",".bmp"};
    private static String musicExtension[] = {".mp3",".wav",};
    private static String movieExtension[] = {".mp4",".avi"};

    /*
     *  指定したファイルが指定した拡張子のどれかに該当するか判別
     */
    public static boolean isExtension(File f ,String extension[])
    {
        for(String ext:extension)
            if(f.getName().endsWith(ext))
                return true;
        for(String ext:extension)
            if(f.getName().endsWith(ext.toUpperCase()))
                return true;
        return false;
    }
    /*
     *  指定したファイルが画像なのかどうかを判別
     */
    public static boolean isImage(File f)
    {
        return isExtension(f,imageExtension);
    }
    /*
     *  指定したファイルが音楽なのかどうかを判別
     */
    public static boolean isMusic(File f)
    {
        return isExtension(f,musicExtension);
    }
    /*
     *  指定したファイルが動画なのかどうかを判別
     */
    public static boolean isMovie(File f)
    {
        return isExtension(f,movieExtension);
    }

    /*
     *  音楽を再生するためのインテントを発行
     */
    public static void sendMusicIntent(File f,Context context)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f),"audio/*");
        context.startActivity(intent);
    }
    /*
     *  動画を再生するためのインテントを発行
     */
    public static void sendMovieIntent(File f,Context context)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f),"video/*");
        context.startActivity(intent);
    }
    /*
     *  画像表示のアクティビティを起動
     */
    public static void startShowImageIntent(String itemPath,Context context)
    {
        Intent intent = new Intent(context,ShowImageActivity.class);
        intent.putExtra("path", itemPath);
        context.startActivity(intent);
    }
    public static void startShowThumbnailIntent(Context context)
    {
        Intent intent = new Intent(context,ThumbnailTestActivity.class);
        context.startActivity(intent);
    }


}
