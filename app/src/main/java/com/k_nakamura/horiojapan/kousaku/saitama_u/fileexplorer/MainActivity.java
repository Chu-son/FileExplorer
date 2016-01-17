package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView nowDirTxtView;
    private File[] files;

    private List<String> nowDirList = new ArrayList<>();
    private List<Integer> listPos = new ArrayList<>();
    private int nowDirNum = 0;

    private MyFragments.MyListFragment listFragment;
    private MyFragments.MyGridFragment gridFragment;

    private MyFragments.MyAbsListViewAdapter myAdapter;

    private boolean isListView = true;
    private boolean displayHiddenFile = true;
    private boolean displayThumbnails = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        nowDirTxtView = (TextView)findViewById(R.id.nowDir);

        /*getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        //registerForContextMenu(fileListView);

        defaultDirectory();
        setFiles2AbsListView_fragment();
    }

    /*
     *  現在のディレクトリ内のファイルとディレクトリを一覧にしてListViewにセット
     */
    private void setFiles2AbsListView_fragment()
    {
        files = new File(getDirName()).listFiles();
        if(files == null) return;

        int f = 0;
        int d = 0;
        for(File file:files){
            if(file.isFile()){
                f++;
            }else d++;
        }
        Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();

        if(!displayHiddenFile) files = removeHiddenFiles(files);

        if(myAdapter == null)
            myAdapter = new MyFragments.MyAbsListViewAdapter(this, R.layout.fragment_imagelist, files,getDirName());
        if (this.listFragment == null && isListView)
            this.listFragment = new MyFragments.MyListFragment(myAdapter,this);
        if (this.gridFragment == null && !isListView)
            this.gridFragment = new MyFragments.MyGridFragment(myAdapter,this);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isListView)
            fragmentTransaction.replace(R.id.fragment_container, this.listFragment);
        else
            fragmentTransaction.replace(R.id.fragment_container, this.gridFragment);

        fragmentTransaction.commit();
    }

    private void refreshAbsList(int pos)
    {
        files = new File(getDirName()).listFiles();
        if(files == null) return;

        int f = 0;
        int d = 0;
        for(File file:files){
            if(file.isFile()){
                f++;
            }else d++;
        }
        Toast.makeText(this, Integer.toString(f) + " files\n" + Integer.toString(d) + " directories", Toast.LENGTH_SHORT).show();

        if(!displayHiddenFile) files = removeHiddenFiles(files);

        myAdapter.resetFiles(files, getDirName());
        if(isListView) listFragment.setSelection(pos);
    }

    /*
     *  １つ前のディレクトリに戻る
     */
    private boolean backDir()
    {
        if(nowDirNum == 1) return false;
        nowDirNum--;
        nowDirTxtView.setText(getDirName());
        refreshAbsList(listPos.get(nowDirNum));

        return true;
    }

    private void defaultDirectory()
    {
        nowDirNum = 0;
        nowDirList.clear();
        listPos.clear();
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
        myAdapter.setDisplayHiddenFile(displayHiddenFile);
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
        myAdapter.setDisplayThumbnails(displayThumbnails);
        refreshAbsList(0);
    }

    /*
     *  オプションメニュー作成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d("unko","unko");
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
            MyUtils.startShowThumbnailIntent(this);
            return true;
        }
        if(id == R.id.action_thumbnail)
        {
            Toast.makeText(this, "サムネイル表示/非表示", Toast.LENGTH_SHORT).show();
            changeDisplayThumbnails(item);
            return true;
        }
        if(id == android.R.id.home)
        {
            defaultDirectory();
            refreshAbsList(0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeView()
    {
        isListView = !isListView;
        setFiles2AbsListView_fragment();
    }

    public void myFragmentItemClickListener(MyFragments.MyAbsListViewAdapter myAdapter , int pos ,ViewGroup parent , int ViewID)
    {
        String item = myAdapter.getFileName(pos);
        File file = (File)myAdapter.getItem(pos);
        if (file.isDirectory()) {
            nowDirList.add(nowDirNum, item);
            if(ViewID == MyFragments.LISTVIEW)
                listPos.add(nowDirNum, ((ListView) parent).getFirstVisiblePosition());
            else listPos.add(nowDirNum, ((GridView) parent).getFirstVisiblePosition());
            nowDirNum++;
            nowDirTxtView.setText(getDirName());
            refreshAbsList(0);
        } else if (MyUtils.isImage(file)) {
            MyUtils.startShowImageIntent(getDirName()+item,this);
        } else if (MyUtils.isMusic(file)) {
            MyUtils.sendMusicIntent(file, this);
        } else if (MyUtils.isMovie(file)) {
            MyUtils.sendMovieIntent(file,this);
        } else {
            showItem(item);
        }

    }



}


