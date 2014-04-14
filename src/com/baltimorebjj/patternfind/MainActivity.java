/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: MainActivity.java
*     Creation Date: 8/21/2013
*            Author: Lee Synkowski
*  
*     This is the initial Android activity that displays the screen
*     to choose the New Game, Options, Instructions, or Quit activities.
*  
* 
*	  Code Review: Code reviewed 3/25/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.baltimorebjj.patternfind;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	static final int OPTIONS_MENU_SELECTED = 1;
	private boolean musicOn = true;
	private boolean soundEffectsOn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Set background image
		RelativeLayout relativeLayout = new RelativeLayout(this);;
		relativeLayout.setBackgroundResource(R.drawable.snowbackground);		
	}
	
	public void startGame(View view){
		Intent levelSelectIntent = new Intent(this,LevelSelectActivity.class);
		
		//Add intent extras which will toggle off music and sound
		levelSelectIntent.putExtra("musicOn", musicOn);
		levelSelectIntent.putExtra("soundEffectsOn",soundEffectsOn);
		startActivity(levelSelectIntent);
	}
	
	public void optionsMenu(View view){
		Intent optionsIntent = new Intent(this,OptionsActivity.class);
		startActivityForResult(optionsIntent,OPTIONS_MENU_SELECTED);
	}
	
	
	public void instructions(View view){
		Intent instructionsIntent = new Intent(this,InstructionsActivity.class);
		startActivity(instructionsIntent);
	}
	
	public void quitGame(View view){
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onActivityResult(int request,int result,Intent data){
		//Update game state options
		if (request == OPTIONS_MENU_SELECTED){
			if (result==RESULT_OK){
				musicOn = data.getBooleanExtra("musicOn", true);
				soundEffectsOn = data.getBooleanExtra("soundEffectsOn", true);
			}
		}

	}

}
