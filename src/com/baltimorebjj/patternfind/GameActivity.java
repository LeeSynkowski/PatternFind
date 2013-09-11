package com.baltimorebjj.patternfind;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity implements SensorEventListener{
	
	private SensorManager mSensorManager;
	private Sensor mOrientation;
	//private TextView azimuthTextViewDisplay;
	//private TextView pitchTextViewDisplay;
	//private TextView rollTextViewDisplay;
	//private DrawingPanel drawingPanel;
	
	//private TextView infoText;
	
	private DrawingView drawingView;
	
	//private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		//set background image
		RelativeLayout relativeLayout = new RelativeLayout(this);;

		
		//get sensor references
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		//get the DrawingView
		drawingView = (DrawingView) findViewById(R.id.drawingView);
		
		//gestureDetector = new GestureDetector(this,gestureListener);
		
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	public void backButton(View view){
		finish();
	}

	@Override
	protected void onResume(){
		super.onResume();
		mSensorManager.registerListener(this,mOrientation,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not worried about this now
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float azimuth_angle = event.values[0];
		float pitch_angle = event.values[1];
		float roll_angle = event.values[2];
		//azimuthTextViewDisplay.setText(Float.toString(azimuth_angle));
		//pitchTextViewDisplay.setText(Float.toString(pitch_angle));
		//rollTextViewDisplay.setText(Float.toString(roll_angle));
		drawingView.setAngles(azimuth_angle, pitch_angle, roll_angle);
		//infoText.setText(drawingPanel.showDelay()+Float.toString(azimuth_angle));
	}
	
	/* trying a simple listener on the view
	SimpleOnGestureListener gestureListener = new SimpleOnGestureListener(){
		
		@Override
		public boolean onDown(MotionEvent e){
			drawingView.handleTap(e);
			return true;
		}
	};
	*/
}
