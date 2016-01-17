package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ThumbnailTestActivity extends AppCompatActivity {

    private ArrayList<String> fname = new ArrayList<String>();
    private MyListFragment fragment;
    private MyListAdapter adapterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadThumbnail();
        setFileName2List();
    }
    private void setFileName2List()
    {
        setContentView(R.layout.activity_fragment_main);

        if (this.fragment == null) {
            this.fragment = new MyListFragment();
        }

        //ContentResolver取得＆イメージ取得
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor c = this.managedQuery(uri, null, null, null, null);

        String[] columns = {MediaStore.Images.Media.DATA};
        //Cursor c = cr.query("image/*", columns, null, null, null);

        c.moveToFirst();
        for (int k = 0; k < c.getCount(); k++) {
            fname.add(c.getString(1));
            c.moveToNext();
        }
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, fname);
        //((ListView) findViewById(R.id.listView)).setAdapter(adapter);

        adapterFragment = new MyListAdapter(this, android.R.layout.simple_expandable_list_item_1, (String[])fname.toArray(new String[0]));
        adapterFragment.setCursor(c,cr);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, this.fragment);
        fragmentTransaction.commit();
    }

    /**
     * サムネイル取得と配置
     */
    private void loadThumbnail() {
        LinearLayout linearLayout = new LinearLayout(this);

        //ContentResolver取得＆イメージ取得
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor c = this.managedQuery(uri, null, null, null, null);

        c.moveToFirst();
        for (int k = 0; k < c.getCount(); k++) {
            // ID取得
            long id = c.getLong(c.getColumnIndexOrThrow("_id"));
            // IDをキーにサムネイル取得
            Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            //取得したサムネイルをイメージビューにセット
            setImageView(linearLayout,bmp);

            c.moveToNext();
        }
        setContentView(linearLayout);
    }

    private void setImageView(LinearLayout linearLayout,Bitmap bmp) {
        ImageView image = new ImageView(this);
        image.setImageBitmap(bmp);
        linearLayout.addView(image, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thumbnail_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        Log.d("unko", "unko");

        return super.onOptionsItemSelected(item);
    }

    public class MyListFragment extends ListFragment {
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }*/

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(adapterFragment);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String item = adapterFragment.getFileName(position);

            Intent intent = new Intent(getApplicationContext(),ShowImageActivity.class);
            intent.putExtra("path",item);
            startActivity(intent);
        }
    }

    public class MyListAdapter extends ArrayAdapter<String>
    {
        String[] filesArray;
        int resId;
        LayoutInflater lInflater = null;
        Cursor c = null;
        long id;
        ContentResolver cr ;

        public MyListAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            filesArray = objects;
            resId = resource;
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return filesArray.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListView l = (ListView) parent;
            Bitmap iconImage = null;
            // ID取得
            c.moveToPosition(position);
            id = c.getLong(c.getColumnIndexOrThrow("_id"));

            // IDをキーにサムネイル取得
            iconImage = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);

            convertView = lInflater.inflate(R.layout.fragment_listview, parent, false);

            ImageView icon = (ImageView) convertView.findViewById(R.id.iconImageView);
            icon.setImageBitmap(iconImage);

            TextView text = (TextView) convertView.findViewById(R.id.fileNameTextView);
            text.setTextColor(Color.BLACK);
            text.setTextSize(18);
            text.setText(filesArray[position]);

            //c.moveToNext();

            return convertView;
        }

        public void setCursor(Cursor c,ContentResolver cr)
        {
            this.cr=cr;
            this.c = c;
            c.moveToFirst();
        }

        public String getFileName(int pos)
        {
            return filesArray[pos];
        }

    }
}
