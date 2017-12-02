package com.oboard.drawbook;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recycler;
    FloatingActionButton add;
    CoordinatorLayout coordinator;
    ImageView collapsing_image;

    static List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();;
    static MyAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //4.4以上透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

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
					PopupMenu pm = new PopupMenu(MainActivity.this, view);
					pm.inflate(R.menu.add);
					pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								switch (item.getItemId()) {
									case R.id.item_add_new:
										//新建空白的图
										int i = S.get("tm", 0);
										S.addIndexX("tm", new String[] { "t", "d" }, new String[] { "Paper " + i, "paper" + System.currentTimeMillis()});
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

										break;
									case R.id.item_add_photo:
										//来自图库的图
										Intent intent = new Intent();
										intent.setType("image/*");
										intent.setAction(Intent.ACTION_GET_CONTENT);
										startActivityForResult(intent, 1);

										break;
								}
								return true;
							}
						});
					pm.show();
                }
            });

        WallpaperManager wm = WallpaperManager.getInstance(this);
        collapsing_image.setImageDrawable(wm.getDrawable());

        loadData();
        recycler.setNestedScrollingEnabled(false);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recycler.setHasFixedSize(true);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        sa = new MyAdapter(mData);
        sa.setOnItemClickListener(this, new OnItemClickListener() {
                @Override
                public void onItemClick(MyAdapter.ViewHolder viewholder, int position) {
                    if (position >= mData.size()) return;
                    Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                    intent.putExtra("image", (String) mData.get(position).get("image"));
                    intent.putExtra("text", (String) mData.get(position).get("text"));
                    intent.putExtra("pos", position);
                    ImageView iv = new ImageView(MainActivity.this);
                    ((FrameLayout)viewholder.mImageView.getParent()).addView(iv, viewholder.mImageView.getLayoutParams());
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, iv, "share").toBundle());
                }
            });
        recycler.setAdapter(sa);

        //先实例化Callback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(sa);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(recycler);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
				//接收图库的图
                try {
					int i = S.get("tm", 0);
					S.addIndexX("tm", new String[] { "t", "d" }, new String[] { "Paper " + i, "paper" + System.currentTimeMillis()});
					S.storePic(S.get("d" + i, ""), BitmapFactory.decodeStream(cr.openInputStream(uri)));
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("image", S.get("d" + i, ""));
					map.put("text", S.get("t" + i, ""));
					mData.add(map);
					sa.notifyItemInserted(i);
					Snackbar.make(coordinator, "Done", Snackbar.LENGTH_SHORT).show();

					recycler.requestLayout();
					recycler.getParent().requestLayout();
                }
				catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

	}

    public static void loadData() {
        mData.clear();
        for (int i = 0; i < S.get("tm", 0); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", S.get("d" + i, ""));
            map.put("text", S.get("t" + i, ""));
            mData.add(map);
        }
    }

    public interface ItemTouchHelperAdapter {
        //数据交换
        void onItemMove(int fromPosition, int toPosition);
        //数据删除
        void onItemDissmiss(int position);
    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private static final float ALPHA_FULL = 1.0f;
        private ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
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
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDissmiss(viewHolder.getAdapterPosition());
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements ItemTouchHelperAdapter {

        public Context context;
        public List<Map<String, Object>> datas = null;
        public MyAdapter(List<Map<String, Object>> datas) {
            this.datas = datas;
        }

        private OnItemClickListener mOnItemClickListener;
        public void setOnItemClickListener(Context context, OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
            this.context = context;
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
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            viewHolder.mTextView.setText((String) datas.get(position).get("text"));
            viewHolder.mImageView.setImageBitmap(S.getStorePic((String) datas.get(position).get("image")));

            viewHolder.mView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(viewHolder, viewHolder.getPosition());
                    }
                });
        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder {
            public FrameLayout mView;
            public TextView mTextView;
            public ImageView mImageView;
            public ViewHolder(View view) {
                super(view);
                mView = (FrameLayout) view.findViewById(R.id.item_home_view);
                mTextView = (TextView) view.findViewById(R.id.item_home_text);
                mImageView = (ImageView) view.findViewById(R.id.item_home_image);
            }
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            //交换位置
            Collections.swap(mData, fromPosition, toPosition);
            for (int i = 1; i < mData.size(); i++) {
                S.put("t" + i, (String) mData.get(i).get("text"))
                    .put("d" + i, (String) mData.get(i).get("image"))
                    .ok();
                mData.get(i).put("text", S.get("t" + i, ""));
                mData.get(i).put("image", S.get("d" + i, ""));
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

    interface OnItemClickListener {
        void onItemClick(MyAdapter.ViewHolder viewholder, int position);
    }

}
    


