/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: LevelCompleteActivity.java
*     Creation Date: 3/07/2014
*            Author: Lee Synkowski
*  
*       This class is used after a level is completed to display the score 
*       for the level and present the next options to the player.
*  
* 
*	Code Review:	Code reviewed 3/24/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.baltimorebjj.patternfind;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class LevelCompleteActivity extends Activity {

	private int levelCompleted;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_level_complete);
		
		Intent startedIntent = getIntent();
		
		levelCompleted = startedIntent.getIntExtra("level", 1);
		
		//Use SharedPreferences to retrieve data stored about levels completed
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String levelKey = String.valueOf(levelCompleted);
		int numberOfMoves = startedIntent.getIntExtra("numberOfMoves", 0);
		int numberOfStars = prefs.getInt(levelKey+"stars",0);
		
		//set level complete
		TextView mTextView = (TextView)findViewById(R.id.textViewYouHaveCompleted);
		mTextView.setText(mTextView.getText()+levelKey);
		
		//set number of moves
		mTextView = (TextView)findViewById(R.id.textViewNumberOfMoves);
		mTextView.setText(mTextView.getText()+ "   " + String.valueOf(numberOfMoves));
		
		//set number of stars
		RatingBar stars = (RatingBar)findViewById(R.id.starsPerLevel);
		stars.setRating(numberOfStars);
		
		//set high score
		mTextView = (TextView)findViewById(R.id.textViewLevelHighScore);
		mTextView.setText(mTextView.getText()+ "   " + String.valueOf(prefs.getInt(levelKey,-1)));
		
		//set typefaces
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/electrictoaster.ttf");
		mTextView = (TextView)findViewById(R.id.textViewCongratulations);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.textViewYouHaveCompleted);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.textViewNumberOfMoves);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.textViewYouHave);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.textViewXStars);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.textViewLevelHighScore);
		mTextView.setTypeface(tf);
		
		Button mButton = (Button) findViewById(R.id.nextLevelButton);
		mButton.setTypeface(tf);
		mButton = (Button) findViewById(R.id.repeatLevelButton);
		mButton.setTypeface(tf);
		mButton = (Button) findViewById(R.id.levelSelectButton);
		mButton.setTypeface(tf);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.level_complete, menu);
		return true;
	}
	
	public void nextLevelButtonPressed(View view){

        //Get the intent ready to keep the data
		Intent startGameIntent = new Intent(getBaseContext(),GameActivity.class);
		//startGameIntent.putExtra("musicOn", musicOn);
		//startGameIntent.putExtra("soundEffectsOn",soundEffectsOn);
		startGameIntent.putExtra("level", levelCompleted + 1);
		
		//Start the new Game
		startActivity(startGameIntent);
		finish();
	}
	
	public void repeatLevelButtonPressed(View view){
        //Get the intent ready to keep the data
		Intent startGameIntent = new Intent(getBaseContext(),GameActivity.class);
		//startGameIntent.putExtra("musicOn", musicOn);
		//startGameIntent.putExtra("soundEffectsOn",soundEffectsOn);
		startGameIntent.putExtra("level", levelCompleted);
		
		//Start the new Game
		startActivity(startGameIntent);
		finish();
	}

	public void levelSelectButtonPressed(View view){
		finish();
	}	
	
}
