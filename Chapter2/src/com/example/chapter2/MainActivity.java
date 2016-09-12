package com.example.chapter2;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IotdHandler iotdHandler = new IotdHandler();
		iotdHandler.processFeed();
		resetDisplay(iotdHandler.getTitle(), iotdHandler.getDate(), iotdHandler.getImage(), iotdHandler.getDescription());
		//resetDisplay(iotdHandler.getTitle(), iotdHandler.getDate(), 
		//		iotdHandler.getBitmap("http://www.nasa.gov/images/content/729774main_hubble_spin_cropped_516-387.jpg"), iotdHandler.getDescription());
	}
	
	private void resetDisplay(String title, String date, Bitmap image, StringBuffer description){
		TextView titleView = (TextView)findViewById(R.id.imageTitle);
		titleView.setText(title);
		
		TextView dateView = (TextView)findViewById(R.id.imageDate);
		dateView.setText(date);
		
		ImageView imageView = (ImageView)findViewById(R.id.imageDisplay);
		imageView.setImageBitmap(image);
		
		TextView descriptionView = (TextView)findViewById(R.id.imageDescription);
		descriptionView.setText(description);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
