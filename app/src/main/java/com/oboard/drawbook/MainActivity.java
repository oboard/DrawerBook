package com.oboard.drawbook;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.support.v7.widget.helper.ItemTouchHelper;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recycler;
    FloatingActionButton add;
    CoordinatorLayout coordinator;
    ImageView collapsing_image;

    List<Map<String, Object>> mData;
    MyAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        S.init(this, "main");

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        coordinator = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        collapsing_image = (ImageView) findViewById(R.id.main_collapsing_image);
        recycler = (RecyclerView) findViewById(R.id.main_recycler);
        add = (FloatingActionButton) findViewById(R.id.main_add);
        toolbar.setTitle(R.string.app_name);

        setSupportActionBar(toolbar);

        add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = S.get("tm", 0);
                    S.addIndexX("tm", new String[] { "t", "d" }, new String[] { "Draw" + i, "draw" + System.currentTimeMillis()});
                    Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    new Canvas(b).drawColor(Color.BLACK);
                    S.storePic(S.get("d" + i, ""), b);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("image", S.get("d" + i, ""));
                    map.put("text", S.get("t" + i, ""));
                    mData.add(map);
                    sa.notifyItemInserted(i);
                    Snackbar.make(coordinator, "Done", Snackbar.LENGTH_SHORT).show();
                    

                    recycler.requestLayout();
                    recycler.getParent().requestLayout();
                }
            });

        WallpaperManager wm = WallpaperManager.getInstance(this);
        collapsing_image.setImageDrawable(wm.getDrawable());

        loadData();
        //创建默认的线性LayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recycler.setHasFixedSize(true);
        sa = new MyAdapter(mData);
        recycler.setAdapter(sa);
        
        //先实例化Callback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(sa);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(recycler);
    }

    public void loadData() {
        List<Map<String, Object>> date_list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < S.get("tm", 0); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", S.get("d" + i, ""));
            map.put("text", S.get("t" + i, ""));
            date_list.add(map);
        }
        mData = date_list;
    }

    public interface ItemTouchHelperAdapter {
        //数据交换
        void onItemMove(int fromPosition, int toPosition);
        //数据删除
        void onItemDissmiss(int position);
    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback{

        private ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter){
            mAdapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.LEFT;
            return makeMovementFlags(dragFlags,swipeFlags);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDissmiss(viewHolder.getAdapterPosition());
        }
    }
    
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements ItemTouchHelperAdapter {

        public List<Map<String, Object>> datas = null;
        public MyAdapter(List<Map<String, Object>> datas) {
            this.datas = datas;
        }
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home, viewGroup, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mTextView.setText((String) datas.get(position).get("text"));
            viewHolder.mImageView.setImageBitmap(S.getStorePic((String) datas.get(position).get("image")));
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public ImageView mImageView;
            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.item_home_text);
                mImageView = (ImageView) view.findViewById(R.id.item_home_image);
            }
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            //交换位置
            Collections.swap(mData, fromPosition, toPosition);
            for (int i = 0; i < mData.size(); i++) {
                S.put("t" + i, (String) mData.get(i).get("text"))
                    .put("d" + i, (String) mData.get(i).get("image"))
                    .ok();
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDissmiss(int position) {
            //移除数据
            mData.remove(position);
            S.delIndexX("tm", new String[] { "t", "d" }, position);
            notifyItemRemoved(position);
        }

    }

}
    

