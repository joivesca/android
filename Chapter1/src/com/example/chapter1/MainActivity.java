package com.example.chapter1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onCLickButton (View view) {
		TextView textView = (TextView)findViewById(R.id.textView1);
		Log.d("Visibilidad", "- " + textView.getVisibility());
		if(textView.getVisibility() == View.VISIBLE) {
			textView.setVisibility(View.INVISIBLE);
		} else {
			textView.setVisibility(View.VISIBLE);
		}
	}
}
