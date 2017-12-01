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

public class DrawActivity extends AppCompatActivity {

    String title, image;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(null);
        
        title = getIntent().getStringExtra("text");
        image = getIntent().getStringExtra("image");
        pos = getIntent().getIntExtra("pos", pos);
        setTitle(title);
        setContentView(R.layout.activity_draw);//决定在两个Activity之间切换时，指定两个Activity中对应的Vi

        //此activity进入
        getWindow().setEnterTransition(new Fade().setDuration(225));

        ImageView imageView = (ImageView)findViewById(R.id.draw_image);
        imageView.setImageBitmap(S.getStorePic(image));
    }

    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 0, 0, "Edit").setIcon(R.drawable.create).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getTitle().equals("Edit")) {
            final AppCompatEditText t = new AppCompatEditText(this);
            t.setText(getTitle());
            new AlertDialog.Builder(this).setTitle("Name").setView(t).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        title = t.getText().toString();
                        S.put("t" + pos, title)
                            .ok();
                        setTitle(title);
                    }
                }).setNegativeButton("No", null)
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
