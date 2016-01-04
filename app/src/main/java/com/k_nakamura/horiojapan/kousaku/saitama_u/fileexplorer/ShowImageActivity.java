package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ShowImageActivity extends AppCompatActivity {
    ImageView iv;
    String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        iv = (ImageView)findViewById(R.id.imageView);

        Intent intent = getIntent();
        filepath = intent.getStringExtra("path");
        Toast.makeText(this, filepath, Toast.LENGTH_SHORT).show();

        Bitmap bmp = BitmapFactory.decodeFile(filepath);
        iv.setImageBitmap(bmp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_throwIntent) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            File f = new File(filepath);

            //拡張子
            String extention = MimeTypeMap.getFileExtensionFromUrl(filepath);
            //mime type
            String mimetype =MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);

            intent.setDataAndType(Uri.fromFile(f),mimetype);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
