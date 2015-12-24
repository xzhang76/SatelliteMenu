package com.zhangxt4.satellitemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.zhangxt4.satellitemenu.view.ArcMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private ArcMenu mArcMenuLeftBottom;
    private ArcMenu mArcMenuRightBottom;
    private List<String> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatas();
        initView();
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDatas));
        initEvent();
    }

    //滑动ListView时，菜单应该闭合
    private void initEvent() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mArcMenuLeftBottom.isOpen()){
                    mArcMenuLeftBottom.toggleMenu(600);
                }
                if (mArcMenuRightBottom.isOpen()){
                    mArcMenuRightBottom.toggleMenu(300);
                }
            }
        });
        mArcMenuRightBottom.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Toast.makeText(MainActivity.this, ""+view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.id_listview);
        mArcMenuLeftBottom = (ArcMenu) findViewById(R.id.id_menu_left_bottom);
        mArcMenuRightBottom = (ArcMenu) findViewById(R.id.id_menu_right_bottom);
    }

    private void initDatas() {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i<'Z'; i++)
            mDatas.add((char)i+"");
    }
}
