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
import android.graphics.drawable.*;

public class DrawActivity extends AppCompatActivity implements OnClickListener {

    String title, image;
    int pos = 0;

	//Main imageview
	ImageView imageView;
	ImageView item_croprotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
	
    public void onEdit(View view) {
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

    public void onBackPressed(View view) {
		onBackPressed();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
        MainActivity.loadData();
        MainActivity.sa.notifyDataSetChanged();
    }

}
