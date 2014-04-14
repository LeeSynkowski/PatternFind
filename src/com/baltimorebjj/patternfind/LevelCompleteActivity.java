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
import android.view.Menu;
import android.widget.TextView;

public class LevelCompleteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_level_complete);
		
		Intent startedIntent = getIntent();
		
		int levelCompleted = startedIntent.getIntExtra("level", 1);
		
		//Use SharedPreferences to retrieve data stored about levels completed
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String levelKey = String.valueOf(levelCompleted);
		
		int numberOfMoves = startedIntent.getIntExtra("numberOfMoves", 0);
		String displayString = "Congrats you have completed level " + levelKey + 
								" in " + numberOfMoves + " moves. ";
		displayString += " Your high score is " + prefs.getInt(levelKey,-1);
		displayString += " You have " + prefs.getInt(levelKey+"stars",0) + " stars for the level. ";
		
		TextView mEditText = (TextView)findViewById(R.id.levelCompleteInfo);
		mEditText.setText(displayString);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.level_complete, menu);
		return true;
	}

}
