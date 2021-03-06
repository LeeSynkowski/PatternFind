/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: GameActivity.java
*     Creation Date: 8/21/2013
*            Author: Lee Synkowski
*  
*       This class holds the GameActivity that launches the drawing view
*       that controls the game. It is responsible for receiving and
*       delivering the sensor updates to the drawing view.  Also launching
*       the next intent on game completion
*  
* 
*	Code Review:	Code reviewed 3/21/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.*;


public class GameActivity extends Activity implements SensorEventListener{
	
	private SensorManager mSensorManager;
	private Sensor mOrientation;	
	private DrawingView drawingView;
	
	private MediaPlayer mPlayer=null;
	
	private int level;

	private AdView myAdView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
			
		//Get sensor references
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		//Get the DrawingView
		drawingView = (DrawingView) findViewById(R.id.drawingView);
		
		//Add the ads
		myAdView = (AdView)this.findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    myAdView.loadAd(adRequest);

		//Get the intent to set the sound parameters
		Intent startedIntent = getIntent();
		
		//Set to play sound effects
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		//drawingView.setHasSound(startedIntent.getBooleanExtra("soundEffectsOn", true));
		drawingView.setHasSound(prefs.getBoolean("soundEffects", true));
		
		//Only start the music if the proper option is selected
		//if (startedIntent.getBooleanExtra("musicOn", true)){
		if (prefs.getBoolean("music", true)){
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mPlayer = MediaPlayer.create(GameActivity.this, R.raw.backgroundmusic);
			mPlayer.setLooping(true);
			mPlayer.start();
		}
		
		//Give the DrawingView the correct level
		level = startedIntent.getIntExtra("level",1);
		drawingView.setLevel(level);
		drawingView.setStartedIntent(startedIntent);
		drawingView.setOnGameCompleteListener(new OnGameCompleteListener(){

			@Override
			public void onGameComplete(Intent startedIntent,int numberOfMoves, int twoStarMoves, int threeStarMoves, boolean finished) {

			if (finished){	
				Intent levelCompleteIntent = new Intent(getApplicationContext(),LevelCompleteActivity.class);
				
				//Update the shared preferences file
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String levelKey = String.valueOf(level);
				
				if (numberOfMoves<prefs.getInt(levelKey,100000)){
					SharedPreferences.Editor editor = prefs.edit();
					editor.putInt(levelKey, numberOfMoves);
					if (numberOfMoves <= threeStarMoves){
						editor.putInt(levelKey+"stars", 3);
					} else if (numberOfMoves <= twoStarMoves){
						editor.putInt(levelKey+"stars", 2);
					} else {
						editor.putInt(levelKey+"stars", 1);
					}
					editor.putBoolean(levelKey+"done" ,true);
					if (level<Level.NUMBER_OF_LEVELS){
						String nextPlayableLevel = String.valueOf(level + 1) + "playable";
						editor.putBoolean(nextPlayableLevel,true);
					}
					editor.apply();
				}
				
				//Add intent extras which will toggle off music and sound
				levelCompleteIntent.putExtra("musicOn", startedIntent.getBooleanExtra("musicOn", true));
				levelCompleteIntent.putExtra("soundEffectsOn",startedIntent.getBooleanExtra("soundEffectsOn", true));
				levelCompleteIntent.putExtra("numberOfMoves",numberOfMoves);
				levelCompleteIntent.putExtra("level",level);
				startActivity(levelCompleteIntent);
				finish();
			}
			//reset condition
			else{			
				Intent restartIntent = new Intent(getApplicationContext(),GameActivity.class);
				level = startedIntent.getIntExtra("level",1);
				restartIntent.putExtra("level",level);
				finish();
				//overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				System.gc();
				startActivity(restartIntent);
				//overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				
			}
			}
		});	
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mSensorManager.registerListener(this,mOrientation,SensorManager.SENSOR_DELAY_NORMAL);
		if (mPlayer != null){
			if (!mPlayer.isPlaying()){
				mPlayer.start();
			}
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
		if (mPlayer != null){
			if (mPlayer.isPlaying()){
				mPlayer.pause();
			}
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if (mPlayer != null){
			if (mPlayer.isPlaying()){
				mPlayer.pause();
			}
		}
	}
	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
		//Must implement this method, but it doesn't have an impace on gameplay 
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		//Update angle values and pass them to the game in progress
		//in the drawing view
		float pitch_angle = event.values[1];
		float roll_angle = event.values[2];
		drawingView.setAngles(pitch_angle, roll_angle);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if (mPlayer!=null){
			mPlayer.release();
			mPlayer = null;
		}
		finish();
	}
	
	@Override
	public void onBackPressed(){
		drawingView.stopGame();
		finish();
	}


}
