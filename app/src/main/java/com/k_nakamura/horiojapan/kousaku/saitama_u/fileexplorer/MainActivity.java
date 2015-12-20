package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView fileListView;
    private GridView gridview;
    private ListView listview;
    private TextView nowDirTxtView;
    private File[] files;

    private String imageExtension[] = {".jpg",".png",".jpeg",".gif",".bmp"};
    private String musicExtension[] = {".mp3",".wav",};
    private String movieExtension[] = {".mp4",".avi"};

    private List<String> nowDirList = new ArrayList<String>();
    private int nowDirNum = 0;

    private MyListFragment fragment1;
    private MyGridFragment fragment2;
    private List<String> fileListForFragment;
    private ArrayAdapter<String> adapterFragment;
    private BitmapAdapter adapterBitmapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_fragment_main);

        nowDirTxtView = (TextView)findViewById(R.id.nowDir);
        fileListView = (ListView) findViewById(R.id.fileList);

        gridview = (GridView)findViewById(R.id.gridView);

        /*// レイアウトインフレーター使用
        LayoutInflater factory = LayoutInflater.from(this);
        // 他のレイアウトファイルを指定
        View layInfView = factory.inflate(R.layout.fragment_listview, null);
        listview = (ListView)layInfView.findViewById(R.id.listView);*/

        //registerForContextMenu(fileListView);

        /*fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                if (files[position].isDirectory()) {
                    nowDirList.add(nowDirNum, item);
                    nowDirNum++;
                    nowDirTxtView.setText(getDirName());
                    setFiles2ListView();
                } else if(isImage(files[position])) {
                    startShowImageIntent(item);
                }
                else if(isMusic(files[position]))
                {
                    sendMusicIntent(files[position]);
                }
                else if(isMovie(files[position]))
                {
                    sendMovieIntent(files[position]);
                }
                else{
                    showItem(item);
                }
            }
        });*/

        // パスを分解してStringの配列に格納
        String path = Environment.getExternalStorageDirectory().getPath();
        String searchWord = "/";
        int begin = 1;
        int end;
        while((end = path.indexOf(searchWord, begin)) > -1)
        {
            nowDirList.add(path.substring(begin,end));
            begin = end + 1;
            nowDirNum++;
        }
        if((begin = path.lastIndexOf(searchWord)) + 1 != path.length()) {
            nowDirList.add(path.substring(begin+1));
            nowDirNum++;
        }

        Toast.makeText(this, getDirName(), Toast.LENGTH_SHORT).show();
        nowDirTxtView.setText(getDirName());

        //setFiles2ListView();
        //setFiles2ListView_fragment();
        setFiles2GridView_fragment();
    }

    /*
     *  現在のディレクトリ内のファイルとディレクトリを一覧にしてListViewにセット
     */
    private void setFiles2ListView()
    {
        List<String> fileList = new ArrayList<String>();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    f++;
                }else d++;
                fileList.add(files[i].getName());
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, fileList);
            fileListView.setAdapter(adapter);
            fileListView.setEnabled(true);
        }
        else
        {
            fileList.add("なんもないやで");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, fileList);
            fileListView.setAdapter(adapter);
            fileListView.setEnabled(false);
        }
    }

    private void setFiles2ListView_fragment()
    {
        if (this.fragment1 == null) {
            this.fragment1 = new MyListFragment();
        }

        ArrayList<String> fileListForFragment = new ArrayList<String>();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    f++;
                }else d++;
                fileListForFragment.add(files[i].getName());
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
            adapterFragment = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, fileListForFragment);
        }
        else
        {
            fileListForFragment.add("なんもないやで");
            adapterFragment = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, fileListForFragment);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, this.fragment1);
        fragmentTransaction.commit();
    }
    /*
     *  現在のディレクトリ内のファイルとディレクトリを一覧にしてGridViewにセット
     */
    private void setFiles2GridView_fragment()
    {
        if (this.fragment2 == null) {
            this.fragment2 = new MyGridFragment();
        }

        List<Bitmap> fileListForFragment = new ArrayList<Bitmap>();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    f++;
                }else d++;
                fileListForFragment.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
            adapterBitmapFragment = new BitmapAdapter(getApplicationContext(), R.layout.fragment_imagelist, fileListForFragment);
        }
        else
        {
            fileListForFragment.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            adapterBitmapFragment = new BitmapAdapter(getApplicationContext(), R.layout.fragment_imagelist, fileListForFragment);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, this.fragment2);
        fragmentTransaction.commit();
    }

    /*
     *  １つ前のディレクトリに戻る
     */
    private boolean backDir()
    {
        if(nowDirNum == 1) return false;
        nowDirNum--;
        nowDirTxtView.setText(getDirName());
        setFiles2ListView();
        return true;
    }

    /*
     *  指定したアイテム名を絶対パスでトースト表示
     */
    public void showItem(String str){
        Toast.makeText(this, getDirName() + str, Toast.LENGTH_SHORT).show();
    }

    /*
     *  現在のディレクトリの絶対パスを返す
     */
    private String getDirName()
    {
        String retStr = "/";
        for(int i = 0 ; i < nowDirNum ; i++)
        {
            retStr += nowDirList.get(i);
            retStr += "/";
        }
        return retStr;
    }

    /*
     *  画像表示のアクティビティを起動
     */
    private void startShowImageIntent(String item)
    {
        Intent intent = new Intent(getApplicationContext(),ShowImageActivity.class);
        intent.putExtra("path",getDirName()+item);
        startActivity(intent);
    }

    /*
     *  指定したファイルが指定した拡張子のどれかに該当するか判別
     */
    private boolean isExtension(File f ,String extension[])
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
    private boolean isImage(File f)
    {
        return isExtension(f,imageExtension);
    }
    /*
     *  指定したファイルが音楽なのかどうかを判別
     */
    private boolean isMusic(File f)
    {
        return isExtension(f,musicExtension);
    }
    /*
     *  指定したファイルが動画なのかどうかを判別
     */
    private boolean isMovie(File f)
    {
        return isExtension(f,movieExtension);
    }

    /*
     *  音楽を再生するためのインテントを発行
     */
    private void sendMusicIntent(File f)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f),"audio/*");
        startActivity(intent);
    }
    /*
     *  動画を再生するためのインテントを発行
     */
    private void sendMovieIntent(File f)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f),"video/*");
        startActivity(intent);
    }

    /*
     *  ボタンイベントを取得
     *  　・バックボタンならひとつ前のディレクトリに戻る
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }else{
            if(!backDir()) return super.onKeyDown(keyCode, event);
            return false;
        }
    }

    /*
     *  コンテキストメニュー作成
     */
    static final int CONTEXT_MENU_FILELISTVIEW_ITEM__ID_1 = 0x01;
    static final int CONTEXT_MENU_FILELISTVIEW_ITEM__ID_2 = 0x02;
    static final int CONTEXT_MENU_FILELISTVIEW_ITEM__RENAME = 0x03;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        int viewId = v.getId();

        if(viewId == R.id.fileList)
        {
            menu.setHeaderTitle("めにゅー");
            menu.add(0, CONTEXT_MENU_FILELISTVIEW_ITEM__ID_1, 0, "めにゅー１");
            menu.add(0,CONTEXT_MENU_FILELISTVIEW_ITEM__ID_2,0,"めにゅー２");
            menu.add(0,CONTEXT_MENU_FILELISTVIEW_ITEM__RENAME,0,"りねーむ");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case CONTEXT_MENU_FILELISTVIEW_ITEM__ID_1:
                Toast.makeText(this, "めにゅー１", Toast.LENGTH_SHORT).show();
                return true;
            case CONTEXT_MENU_FILELISTVIEW_ITEM__ID_2:
                Toast.makeText(this, "めにゅー２", Toast.LENGTH_SHORT).show();
                return true;
            case CONTEXT_MENU_FILELISTVIEW_ITEM__RENAME:
                renameDialog(files[0]);
                Toast.makeText(this, "りねーむ", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void renameDialog(File file)
    {
        //テキスト入力を受け付けるビューを作成します。
        final EditText editView = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("テキスト入力ダイアログ")
                        //setViewにてビューを設定します。
                .setView(editView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //入力した文字をトースト出力する
                        Toast.makeText(MainActivity.this,
                                editView.getText().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    /*
     *  オプションメニュー作成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_changeview) {
            Toast.makeText(this, "ちぇんじびゅー", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyListFragment extends ListFragment {
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }*/

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(adapterFragment);
        }
    }
    class MyGridFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View parent = inflater.inflate(R.layout.fragment_gridview, container, false);
            GridView gv = (GridView) parent.findViewById(R.id.gridView);
            gv.setAdapter(adapterBitmapFragment);
            return parent;
        }
    }
    public class BitmapAdapter extends ArrayAdapter<Bitmap> {

        private int resourceId;

        public BitmapAdapter(Context context, int resource, List<Bitmap> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resourceId, null);
            }

            ImageView view = (ImageView) convertView;
            view.setImageBitmap(getItem(position));

            return view;
        }

    }
}


