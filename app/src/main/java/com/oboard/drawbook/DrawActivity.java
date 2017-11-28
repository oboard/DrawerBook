package com.oboard.drawbook;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.KeyEvent;

public class DrawActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.activity_draw);
        ImageView shareImage = (ImageView) findViewById(R.id.draw_image);
        shareImage.setImageBitmap(S.getStorePic(getIntent().getStringExtra("image")));
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
