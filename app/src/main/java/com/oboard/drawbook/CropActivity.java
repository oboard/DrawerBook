package com.oboard.drawbook;
import android.support.v7.app.*;
import android.os.*;
import android.view.*;

public class CropActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop);
	}

	public void onClose(View view) {
		finish();
	}

	public void onCheck(View view) {
		finish();
	}

}
