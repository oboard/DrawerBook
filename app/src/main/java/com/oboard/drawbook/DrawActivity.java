package com.oboard.drawbook;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Window;
import android.view.Menu;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.content.DialogInterface;
import android.view.View.*;
import android.view.*;
import android.support.design.widget.*;
import android.content.*;

public class DrawActivity extends AppCompatActivity implements OnClickListener {

    String title, image;
    int pos = 0;
	
	//Main imageview
	ImageView imageView;
	ImageView item_croprotate;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(null);
        getSupportActionBar().setElevation(0);

        image = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("text");
        pos = getIntent().getIntExtra("pos", pos);
        setContentView(R.layout.activity_draw);
        setTitle(title);

        //此activity进入
        getWindow().setEnterTransition(new Fade().setDuration(225));

        imageView = (ImageView)findViewById(R.id.draw_image);
        imageView.setImageBitmap(S.getStorePic(image));
		
		//Tool Items
		item_croprotate = (ImageView)findViewById(R.id.draw_item_croprotate);
		item_croprotate.setOnClickListener(this);
    }
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.draw_item_croprotate:
				//裁剪＆旋转 图片
				startActivity(new Intent(this, CropActivity.class).putExtras(getIntent()));
				break;
		}
	}
	
    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 0, 0, "Edit").setIcon(R.drawable.ic_edit_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getTitle().equals("Edit")) {
            final AppCompatEditText t = new AppCompatEditText(this);
            t.setText(getTitle());
            new AlertDialog.Builder(this).setTitle(R.string.dialog_3).setView(t).setPositiveButton(R.string.dialog_1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        title = t.getText().toString();
                        S.put("t" + pos, title)
                            .ok();
                        setTitle(title);
                    }
                }).setNegativeButton(R.string.dialog_2, null)
                .show();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
        MainActivity.loadData();
        MainActivity.sa.notifyDataSetChanged();
    }
    
}
