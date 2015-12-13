package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private List<String> nowDirList = new ArrayList<String>();
    private int nowDirNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nowDirTxtView = (TextView)findViewById(R.id.nowDir);
        fileListView = (ListView) findViewById(R.id.fileList);
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                if (files[position].isDirectory()) {
                    nowDirList.add(nowDirNum, item);
                    nowDirNum++;
                    nowDirTxtView.setText(getDirName());
                    setFiles2ListView();
                } else if(files[position].getName().endsWith(".jpg")||files[position].getName().endsWith(".png")) {
                    startShowImageIntent(item);
                }
                else{
                    showItem(item);
                }
            }
        });

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

        setFiles2ListView();
    }

    private void setFiles2ListView()
    {
        List<String> fileList = new ArrayList<String>();
        files = new File(getDirName()).listFiles();
        if(files == null) return;
        if(files.length > 0){
            int f = 0;
            int d = 0;
            for(int i = 0; i < files.length; i++){
                //if(files[i].isFile() && files[i].getName().endsWith(".mp3")){
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

    private boolean backDir()
    {
        if(nowDirNum == 1) return false;
        nowDirNum--;
        nowDirTxtView.setText(getDirName());
        setFiles2ListView();
        return true;
    }

    public void showItem(String str){
        Toast.makeText(this, getDirName() + str, Toast.LENGTH_SHORT).show();
    }

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

    private void startShowImageIntent(String item)
    {
        Intent intent = new Intent(getApplicationContext(),ShowImageActivity.class);
        intent.putExtra("path",getDirName()+item);
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }else{
            if(!backDir()) return super.onKeyDown(keyCode, event);
            return false;
        }
    }

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
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
