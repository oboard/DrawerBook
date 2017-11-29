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

public class DrawActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
 
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra("text"));
        setContentView(R.layout.activity_draw);//决定在两个Activity之间切换时，指定两个Activity中对应的Vi
        
        //此activity进入
        getWindow().setEnterTransition(new Fade().setDuration(225));
        //此activity退出
        getWindow().setExitTransition(new Slide().setDuration(225));
        
        ImageView image = (ImageView)findViewById(R.id.draw_image);
        image.setImageBitmap(S.getStorePic(getIntent().getStringExtra("image")));

        //再次进入时使用(如果当前Activity已经打开过，并且再次打开该Activity时的动画 )
        //getWindow().setReenterTransition(new Explode().setDuration(2000));
        //决定在两个Activity之间切换时，指定两个Activity中对应的View的过渡效果
        // getWindow().setSharedElementEnterTransition(new Explode().setDuration(2000));ew的过渡效果
        // getWindow().setSharedElementEnterTransition(new Explode().setDuration(2000));
    }
                                                  
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            ActivityCompat.finishAfterTransition(this);
        return super.onOptionsItemSelected(item);
    } @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            ActivityCompat.finishAfterTransition(this);
        return super.onKeyDown(keyCode, event);
    } 
    
    
}
