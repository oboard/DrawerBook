package com.oboard.drawbook;
import android.support.v7.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.graphics.*;

public class CropActivity extends AppCompatActivity {

	FrameLayout canvas;
	ImageView canvas_face;
	View canvas_rect;

	Bitmap bitmap;
    String title, image;
    int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//获得资源
        image = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("text");
        pos = getIntent().getIntExtra("pos", pos);

		setContentView(R.layout.activity_crop);

		canvas = (FrameLayout)findViewById(R.id.crop_canvas);
		canvas_face = (ImageView)findViewById(R.id.crop_canvas_face);
		canvas_rect = findViewById(R.id.crop_canvas_rect);

		bitmap = S.getStorePic(image);
		freshCanvas();

		//加入图片
		canvas_face.setImageBitmap(bitmap);
	}

	public void freshCanvas() {/*
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)canvas.getLayoutParams();
		Display dv = getWindowManager().getDefaultDisplay();
		int bw = bitmap.getWidth(), bh = bitmap.getHeight(), dw = dv.getWidth(), dh = dv.getHeight();
		dh = dh - (int) (48 * getResources().getDisplayMetrics().density + 0.5f);
		//按比例适应
		if (bw > bh) {
			lp.width = dv.getWidth();
			lp.height = dv.getWidth() / bitmap.getWidth() * bitmap.getHeight();
		} else if (bw < bh) {
			lp.height = dh;
			lp.width = dh / bh * bw;
		} else {
			if (dw > dh) {
				lp.height = dh;
				lp.width = dh;
			} else if (dw <= dh) {
				lp.height = dw;
				lp.width = dw;
			}
		}
		canvas.setLayoutParams(lp);
		canvas.requestLayout();*/
	}

	@Override
	protected void onDestroy() {
		bitmap.recycle();
		super.onDestroy();
	}

	public void onClose(View view) {
		finish();
	}

	public void onCheck(View view) {
		finish();
	}

}
