package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.app.Fragment;
import android.app.FragmentBreadCrumbs;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView fileListView;
    private TextView nowDirTxtView;
    private File[] files;

    private String imageExtension[] = {".jpg",".png",".jpeg",".gif",".bmp"};
    private String musicExtension[] = {".mp3",".wav",};
    private String movieExtension[] = {".mp4",".avi"};

    private List<String> nowDirList = new ArrayList<String>();
    private List<Integer> listPos = new ArrayList<Integer>();
    private int nowDirNum = 0;

    private MyListFragment fragment1;
    private MyGridFragment fragment2;
    private MyAbsListFragment myFragment;
    private ArrayList<String>  fileListForFragmentString;

    private MyListAdapter adapterFragment;
    private MyGridAdapter gridFragmentAdapter;
    private MyAbsListViewAdapter myAdapter;

    private boolean isListView = true;
    private boolean displayHiddenFile = true;
    private boolean displayThumbnails = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        nowDirTxtView = (TextView)findViewById(R.id.nowDir);
        fileListView = (ListView) findViewById(R.id.fileList);

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
        int begin = 1; //はじめの/はとばす
        int end;
        while((end = path.indexOf(searchWord, begin)) > -1)
        {
            nowDirList.add(path.substring(begin,end));
            listPos.add(0);
            begin = end + 1;
            nowDirNum++;
        }
        if((begin = path.lastIndexOf(searchWord)) + 1 != path.length()) {
            nowDirList.add(path.substring(begin+1));
            listPos.add(0);
            nowDirNum++;
        }

        Toast.makeText(this, getDirName(), Toast.LENGTH_SHORT).show();
        nowDirTxtView.setText(getDirName());

        setFiles2ListView_fragment();
        //setFiles2GridView_fragment();
        //setFiles2AbsListView_fragment();
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

        fileListForFragmentString = new ArrayList<String>();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    f++;
                }else d++;
                fileListForFragmentString.add(files[i].getName());
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
            //adapterFragment = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, fileListForFragmentString);
            //adapterFragment = new MyListAdapter(this, android.R.layout.simple_expandable_list_item_1, files);
            myAdapter = new MyAbsListViewAdapter(this, R.layout.fragment_imagelist, files);
        }
        else
        {
            fileListForFragmentString.add("なんもないやで");
            //adapterFragment = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, fileListForFragmentString);
            //adapterFragment = new MyListAdapter(this, android.R.layout.simple_expandable_list_item_1, files);
            myAdapter = new MyAbsListViewAdapter(this, R.layout.fragment_imagelist, files);
        }
        if(!displayHiddenFile) files = removeHiddenFiles(files);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, this.fragment1);
        fragmentTransaction.commit();
        isListView = true;
    }
    private void refreshList(int pos)
    {
        if (this.fragment1 == null) {
            this.fragment1 = new MyListFragment();
        }

        fileListForFragmentString.clear();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    f++;
                }else d++;
                fileListForFragmentString.add(files[i].getName());
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
        }
        else
        {
            fileListForFragmentString.add("なんもないやで");
        }
        if(!displayHiddenFile) files = removeHiddenFiles(files);

        //adapterFragment.notifyDataSetChanged();
        myAdapter.resetFiles(files);
        //fragment1.setSelection(pos);
    }
    private void setFiles2AbsListView_fragment()
    {
        if (this.myFragment == null) {
            this.myFragment = new MyAbsListFragment(this);
        }

        fileListForFragmentString = new ArrayList<String>();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    f++;
                }else d++;
                fileListForFragmentString.add(files[i].getName());
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
            myAdapter = new MyAbsListViewAdapter(this, R.layout.fragment_imagelist, files);
        }
        else
        {
            fileListForFragmentString.add("なんもないやで");
            myAdapter = new MyAbsListViewAdapter(this, R.layout.fragment_imagelist, files);
        }
        if(!displayHiddenFile) files = removeHiddenFiles(files);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, this.myFragment);
        fragmentTransaction.commit();
        isListView = true;
    }
    private void refreshAbsList(int pos)
    {
        if(isListView)  refreshList(pos);
        else refreshGrid(0);
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
                    fileListForFragment.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file));
                }else
                {
                    d++;
                    fileListForFragment.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_folder));
                }
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
            //gridFragmentAdapter = new MyGridAdapter(getApplicationContext(), R.layout.fragment_imagelist, files);
            myAdapter = new MyAbsListViewAdapter(this, R.layout.fragment_imagelist, files);
        }
        else
        {
            fileListForFragment.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            //gridFragmentAdapter = new MyGridAdapter(getApplicationContext(), R.layout.fragment_imagelist, files);
            myAdapter = new MyAbsListViewAdapter(this, R.layout.fragment_imagelist, files);
        }
        if(!displayHiddenFile) files = removeHiddenFiles(files);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, this.fragment2);
        fragmentTransaction.commit();
        isListView = false;
    }

    private void refreshGrid(int pos)
    {
        if (this.fragment2 == null) {
            this.fragment2 = new MyGridFragment();
        }

        fileListForFragmentString.clear();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    f++;
                }else d++;
                fileListForFragmentString.add(files[i].getName());
            }

            Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();
        }
        else
        {
            fileListForFragmentString.add("なんもないやで");
        }
        if(!displayHiddenFile) files = removeHiddenFiles(files);

        //adapterFragment.notifyDataSetChanged();
        //gridFragmentAdapter.resetFiles(files);
        //fragment2.setSelection(pos);
        myAdapter.resetFiles(files);
    }

    /*
     *  １つ前のディレクトリに戻る
     */
    private boolean backDir()
    {
        if(nowDirNum == 1) return false;
        nowDirNum--;
        nowDirTxtView.setText(getDirName());
        refreshAbsList(listPos.get(nowDirNum-1));

        return true;
    }

    private File[] removeHiddenFiles(File[] f)
    {
        ArrayList list = new ArrayList();
        for(File file:f)
            if(!file.isHidden()) list.add(file);
        return (File[])list.toArray(new File[0]);
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
    private void startShowThumbnailIntent()
    {
        Intent intent = new Intent(getApplicationContext(),ThumbnailTestActivity.class);
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

    private void changeDisplayHiddenFiles(MenuItem item)
    {
        if(displayHiddenFile)
        {
            item.setTitle("隠しファイルを表示");
        }
        else item.setTitle("隠しファイルを非表示");
        displayHiddenFile = !displayHiddenFile;
        refreshAbsList(0);
    }
    private void changeDisplayThumbnails(MenuItem item)
    {
        if(displayThumbnails)
        {
            item.setTitle("サムネイルを表示");
        }
        else item.setTitle("サムネイルを非表示");
        displayThumbnails = !displayThumbnails;
        refreshAbsList(0);
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
            changeView();
            return true;
        }
        if(id == R.id.action_hiddenfile)
        {
            Toast.makeText(this, "隠しファイル表示/非表示", Toast.LENGTH_SHORT).show();
            changeDisplayHiddenFiles(item);
            return true;
        }
        if(id == R.id.action_thumbnail_test)
        {
            Toast.makeText(this, "画像一覧テスト", Toast.LENGTH_SHORT).show();
            startShowThumbnailIntent();
            return true;
        }
        if(id == R.id.action_thumbnail)
        {
            Toast.makeText(this, "サムネイル表示/非表示", Toast.LENGTH_SHORT).show();
            changeDisplayThumbnails(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeView()
    {
        if (isListView)
            setFiles2GridView_fragment();
        else setFiles2ListView_fragment();
        /*myFragment.changeView();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this.myFragment);
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        fragmentTransaction.commit();*/
        Log.d("test","1");
    }

    public class MyListFragment extends ListFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            //setListAdapter(adapterFragment);
            setListAdapter(myAdapter.listMode());
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            ListView listView = l;
            //String item = (String) listView.getItemAtPosition(position);
            String item = myAdapter.getFileName(position);
            if (files[position].isDirectory()) {
                nowDirList.add(nowDirNum, item);
                listPos.add(nowDirNum,l.getFirstVisiblePosition());
                nowDirNum++;
                nowDirTxtView.setText(getDirName());
                refreshList(0);
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
    }
    public class MyGridFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View parent = inflater.inflate(R.layout.fragment_gridview, container, false);
            GridView gv = (GridView) parent.findViewById(R.id.gridView);
            gv.setHorizontalSpacing(5);
            //gv.setAdapter(gridFragmentAdapter);
            gv.setAdapter(myAdapter.gridMode());

            ((AbsListView)gv).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = myAdapter.getFileName(position);
                    if (files[position].isDirectory()) {
                        nowDirList.add(nowDirNum, item);
                        nowDirNum++;
                        nowDirTxtView.setText(getDirName());
                        refreshGrid(0);
                    } else if (isImage(files[position])) {
                        startShowImageIntent(item);
                    } else if (isMusic(files[position])) {
                        sendMusicIntent(files[position]);
                    } else if (isMovie(files[position])) {
                        sendMovieIntent(files[position]);
                    } else {
                        showItem(item);
                    }

                }
            });

            return parent;
        }

    }

    public class MyAbsListFragment extends Fragment
    {
        private boolean isListView;
        private Context context;
        private int listPosition;

        public MyAbsListFragment(Context context,boolean isListView)
        {
            this.isListView = isListView;
            this.context = context;
            this.listPosition = 0;
        }

        public MyAbsListFragment(Context context)
        {
            this(context,true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            //View parent = inflater.inflate(R.layout.fragment_myabslistbase, container, false);
            LinearLayout parent = new LinearLayout(context);
            parent.setBackgroundColor(Color.WHITE);

            AbsListView absList;

            if(isListView) {
                ListView lv = new ListView(context);
                lv.setAdapter(myAdapter.listMode());
                lv.setSelection(listPosition);

                parent.addView(lv);
                absList = lv;
                Log.d("test","3");
            }
            else
            {
                GridView gv = new GridView(context);
                gv.setNumColumns(4);
                gv.setHorizontalSpacing(5);
                gv.setAdapter(myAdapter.gridMode());

                parent.addView(gv);
                absList = gv;
                Log.d("test","4");
            }
            Log.d("test","5");

            absList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = myAdapter.getFileName(position);
                    if (files[position].isDirectory()) {
                        nowDirList.add(nowDirNum, item);
                        nowDirNum++;
                        nowDirTxtView.setText(getDirName());
                        refreshGrid(0);
                    } else if (isImage(files[position])) {
                        startShowImageIntent(item);
                    } else if (isMusic(files[position])) {
                        sendMusicIntent(files[position]);
                    } else if (isMovie(files[position])) {
                        sendMovieIntent(files[position]);
                    } else {
                        showItem(item);
                    }

                }
            });

            return parent;
        }

        public void changeView()
        {
            isListView = !isListView;
            myAdapter.notifyDataSetChanged();
            Log.d("test","2");
        }

    }

    public class MyGridAdapter extends ArrayAdapter<File> {

        private File[] filesArray;
        private int resourceId;
        private LayoutInflater inflater;

        public MyGridAdapter(Context context, int resource, File[] objects) {
            super(context, resource, objects);
            resourceId = resource;
            filesArray = objects;
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(resourceId, null);

            LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.linear);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);

            String fName = filesArray[position].getName();
            Bitmap iconImage = null;
            if(filesArray[position].isFile()){
                iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file);
            }else
            {
                iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_folder);
            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.iconImageView);
            icon.setImageBitmap(iconImage);

            TextView text = (TextView) convertView.findViewById(R.id.fileNameTextView);
            text.setTextColor(Color.BLACK);
            float textSize = 12f;
            text.setTextSize(textSize);
            text.setGravity(Gravity.CENTER_HORIZONTAL);
            text.setWidth((int) (iconImage.getWidth() * 1.5));
            /*Paint p = new Paint();
            p.setTextSize(textSize);
            Paint.FontMetrics fm = p.getFontMetrics();
            text.setHeight((int)(Math.abs(fm.top)+Math.abs(fm.descent)+0.5f));*/
            text.setLines(2);
            text.setText(fName);

            if(displayHiddenFile&&filesArray[position].isHidden())
            {
                icon.setColorFilter(0xbbffffff);
                text.setTextColor(Color.GRAY);
            }

            return convertView;
        }
        public String getFileName(int position )
        {
            return filesArray[position].getName();
        }

        @Override
        public int getCount() {
            return filesArray.length;
        }

        public void resetFiles(File[] f)
        {
            filesArray = f;
            this.notifyDataSetChanged();
        }
    }

    public class MyListAdapter extends ArrayAdapter<File>
    {
        File[] filesArray;
        int resId;
        LayoutInflater lInflater = null;

        public MyListAdapter(Context context, int resource, File[] objects) {
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
            String fName = filesArray[position].getName();
            convertView = lInflater.inflate(R.layout.fragment_imagelist,parent,false);

            LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.linear);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_VERTICAL);
            ll.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );

            ImageView icon = (ImageView) convertView.findViewById(R.id.iconImageView);
            Bitmap iconImage;
            if(filesArray[position].isFile()){
                if(displayThumbnails) {
                    if(isImage(filesArray[position]))
                    {
                        iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file);
                        LoadThumbnails lt = new LoadThumbnails(getContext(),icon,getDirName() + fName);
                        lt.execute();
                    }
                    else
                    {
                        iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file);
                    }
                }
                else
                {
                    iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file);
                }
            }else
            {
                iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_folder);
            }

            icon.setImageBitmap(iconImage);

            TextView text = (TextView) convertView.findViewById(R.id.fileNameTextView);
            text.setTextColor(Color.BLACK);
            text.setTextSize(18);
            text.setText(fName);

            if(displayHiddenFile&&filesArray[position].isHidden())
            {
                icon.setColorFilter(0xbbffffff);
                text.setTextColor(Color.GRAY);
            }

            return convertView;
        }

        public String getFileName(int position )
        {
            return filesArray[position].getName();
        }

        public void resetFiles(File[] f) {
            filesArray = f;
            this.notifyDataSetChanged();
        }

    }

    public class MyAbsListViewAdapter extends BaseAdapter
    {
        private File[] filesArray;
        private int resourceId;
        private LayoutInflater inflater;
        private Context context;
        private boolean isListView;

        public MyAbsListViewAdapter(Context context, int resourceId, File[] objects) {
            super();
            filesArray = objects;
            this.resourceId = resourceId;
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return filesArray.length;
        }

        @Override
        public Object getItem(int position) {
            return filesArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(resourceId,null);
            String fName = filesArray[position].getName();

            ImageView icon = (ImageView) convertView.findViewById(R.id.iconImageView);
            Bitmap iconImage;
            if(filesArray[position].isFile()){
                if(displayThumbnails) {
                    if(isImage(filesArray[position]))
                    {
                        iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file);
                        LoadThumbnails lt = new LoadThumbnails(context,icon,getDirName() + fName);
                        lt.execute();
                    }
                    else
                    {
                        iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file);
                    }
                }
                else
                {
                    iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_file);
                }
            }else
            {
                iconImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_folder);
            }
            icon.setImageBitmap(iconImage);

            LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.linear);
            TextView text = (TextView) convertView.findViewById(R.id.fileNameTextView);
            text.setTextColor(Color.BLACK);
            text.setText(fName);
            if(isListView) {
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setGravity(Gravity.CENTER_VERTICAL);
                ll.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                        )
                );

                text.setTextSize(18);

                Log.d("test","List");
            }
            else
            {
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setGravity(Gravity.CENTER_HORIZONTAL);
                ll.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );

                float textSize = 12f;
                text.setTextSize(textSize);
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                text.setWidth((int) (iconImage.getWidth() * 1.5));
                text.setLines(2);

                Log.d("test","Grid");
            }

            if(displayHiddenFile&&filesArray[position].isHidden())
            {
                icon.setColorFilter(0xbbffffff);
                text.setTextColor(Color.GRAY);
            }

            return convertView;
        }

        public String getFileName(int position )
        {
            return filesArray[position].getName();
        }

        public void resetFiles(File[] f) {
            filesArray = f;
            this.notifyDataSetChanged();
        }

        public MyAbsListViewAdapter gridMode()
        {
            isListView = false;
            return this;
        }
        public MyAbsListViewAdapter listMode()
        {
            isListView = true;
            return this;
        }
    }

}


