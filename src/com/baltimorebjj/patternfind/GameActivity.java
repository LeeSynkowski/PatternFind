package com.baltimorebjj.patternfind;

import java.io.IOException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
	
	private MediaPlayer mPlayer;
	private boolean playerPrepared = false;
		

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
		
		//Get the intent to set the volume levels
		Intent startedIntent = getIntent();
		//need to set the volume of the bg music here and pass a value to the sound effects to set later
		drawingView.setHasSound(startedIntent.getBooleanExtra("soundEffects", true));
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mPlayer = MediaPlayer.create(GameActivity.this, R.raw.arp_music);
		mPlayer.setLooping(true);
		
		//only start the music if the proper option is selected
		if (startedIntent.getBooleanExtra("music", true)){
			mPlayer.start();
			
		drawingView.setLevel(startedIntent.getIntExtra("level",1));
		drawingView.newGame();
		//this is where we start the game up
		
		}
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
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
		if (mPlayer.isPlaying()){
			mPlayer.pause();
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if (mPlayer.isPlaying()){
			mPlayer.pause();
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not worried about this now
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float pitch_angle = event.values[1];
		float roll_angle = event.values[2];
		drawingView.setAngles(pitch_angle, roll_angle);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mPlayer.release();
		mPlayer = null;
		finish();
	}
	
	@Override
	public void onBackPressed(){
		drawingView.stopGame();
		finish();
	}

}
