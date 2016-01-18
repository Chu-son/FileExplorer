package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by user on 2016/01/17.
 */
public class MyFragments {
    public static int LISTVIEW = 0x01;
    public static int GRIDVIEW = 0x02;

    static public class MyListFragment extends ListFragment {
        private MyAbsListViewAdapter myAdapter;
        private MainActivity main;

        public MyListFragment(MyAbsListViewAdapter myAdapter,MainActivity main)
        {
            this.myAdapter = myAdapter;
            this.main = main;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(myAdapter.getListAdapter());
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            main.myFragmentItemClickListener(myAdapter,position,l,LISTVIEW);
        }
    }

    static public class MyGridFragment extends Fragment {
        private MyAbsListViewAdapter myAdapter;
        private MainActivity main;

        public MyGridFragment(MyAbsListViewAdapter myAdapter,MainActivity main)
        {
            this.myAdapter = myAdapter;
            this.main = main;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            /*View parent = inflater.inflate(R.layout.fragment_gridview, container, false);
            GridView gv = (GridView) parent.findViewById(R.id.gridView);
            gv.setHorizontalSpacing(5);
            gv.setAdapter(myAdapter.getGridAdapter());*/

            LinearLayout parent = new LinearLayout(main);
            parent.setBackgroundColor(Color.WHITE);

            GridView gv = new GridView(main);
            gv.setNumColumns(4);
            gv.setHorizontalSpacing(5);
            gv.setAdapter(myAdapter.getGridAdapter());

            parent.addView(gv);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    main.myFragmentItemClickListener(myAdapter,position,parent,GRIDVIEW);
                }
            });

            return parent;
        }

    }

    static public class MyAbsListViewAdapter extends BaseAdapter
    {
        private File[] filesArray;
        private int resourceId;
        private LayoutInflater inflater;
        private Context context;
        private boolean isListView;
        private String dirName;
        private boolean displayThumbnails = true;
        private boolean displayHiddenFile = true;

        public MyAbsListViewAdapter(Context context, int resourceId, File[] objects , String dirName) {
            super();
            filesArray = objects;
            this.dirName = dirName;
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
            convertView = inflater.inflate(resourceId,parent,false);
            String fName = filesArray[position].getName();

            ImageView icon = (ImageView) convertView.findViewById(R.id.iconImageView);
            Bitmap iconImage;
            if(filesArray[position].isFile()){
                if(displayThumbnails) {
                    if(MyUtils.isImage(filesArray[position]))
                    {
                        iconImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_loading);
                        LoadThumbnails lt = new LoadThumbnails(context,icon,dirName + fName);
                        lt.execute();
                    }
                    else
                    {
                        iconImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_file);
                    }
                }
                else
                {
                    iconImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_file);
                }
            }else
            {
                iconImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_folder2);
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

        public void resetFiles(File[] f , String dirName) {
            filesArray = f;
            this.dirName = dirName;
            this.notifyDataSetChanged();
        }

        public void setDisplayThumbnails(boolean isDisplay)
        {
            displayThumbnails = isDisplay;
        }
        public void setDisplayHiddenFile(boolean isDisplay)
        {
            displayHiddenFile = isDisplay;
        }

        public MyAbsListViewAdapter getGridAdapter()
        {
            isListView = false;
            return this;
        }
        public MyAbsListViewAdapter getListAdapter()
        {
            isListView = true;
            return this;
        }
    }
}
