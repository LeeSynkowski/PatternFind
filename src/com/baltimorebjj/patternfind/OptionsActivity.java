/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: OptionsActivity.java
*     Creation Date: 2/20/2014
*            Author: Lee Synkowski
*  
*	  The android activity houses the Options select screen allowing
*	  the player to set music options, or reset game play progress.
*  
*	  Code Review: Code reviewed 3/25/2014 by Lee Synkowski
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends Activity {

	private boolean musicOn = true;
	private boolean soundEffectsOn = true;
	private RadioGroup soundEffectsSelected;
	private RadioGroup musicEffectsSelected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/electrictoaster.ttf");
		Button textButton = (Button) findViewById(R.id.optionsTextView1);
		textButton.setTypeface(tf);
		textButton = (Button) findViewById(R.id.optionsTextView2);
		textButton.setTypeface(tf);
		RadioButton radioButton = (RadioButton) findViewById(R.id.music_on);
		radioButton.setTypeface(tf);
		radioButton = (RadioButton) findViewById(R.id.music_off);
		radioButton.setTypeface(tf);
		radioButton = (RadioButton) findViewById(R.id.sound_effects_on);
		radioButton.setTypeface(tf);
		radioButton = (RadioButton) findViewById(R.id.sound_effects_off);
		radioButton.setTypeface(tf);
		Button mButton = (Button) findViewById(R.id.optionsBackButton);
		mButton.setTypeface(tf);
		mButton = (Button) findViewById(R.id.optionsResetStats);
		mButton.setTypeface(tf);
		musicEffectsSelected = (RadioGroup) findViewById(R.id.radioGroup1);
		soundEffectsSelected = (RadioGroup) findViewById(R.id.radioGroup2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	//Method called to save selected game options
	public void backButtonPressed(View view){
		int musicSelectedId = musicEffectsSelected.getCheckedRadioButtonId();
		
		//create shared prefs for this
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		
		
		if (musicSelectedId == R.id.music_on){
			musicOn = true;
			editor.putBoolean("music", true);
		}else{
			musicOn = false;
			editor.putBoolean("music", false);
		}
		
		int soundEffectsSelectedId = soundEffectsSelected.getCheckedRadioButtonId();
		
		if (soundEffectsSelectedId == R.id.sound_effects_on){
			soundEffectsOn = true;
			editor.putBoolean("soundEffects", true);
		}else{
			soundEffectsOn = false;
			editor.putBoolean("soundEffects", false);
		}
		
		editor.apply();
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("musicOn",musicOn);
		resultIntent.putExtra("soundEffectsOn", soundEffectsOn);
		
		setResult(Activity.RESULT_OK,resultIntent);
		
		finish();
	}
	
	//Method to reset shared preferences file which holds the user's level completion data. 
	public void resetStats(View view){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		SharedPreferences.Editor editor = prefs.edit();
	    editor.clear();
	    editor.apply();

	    Toast toast = Toast.makeText(getApplicationContext(), "Progress Reset",Toast.LENGTH_SHORT);
	    toast.show();
		
	}
}
