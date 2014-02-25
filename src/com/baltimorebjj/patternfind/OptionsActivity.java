package com.baltimorebjj.patternfind;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;

public class OptionsActivity extends Activity {

	private boolean musicOn = true;
	private boolean soundEffectsOn = true;
	private RadioGroup soundEffectsSelected;
	private RadioGroup musicEffectsSelected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		musicEffectsSelected = (RadioGroup) findViewById(R.id.radioGroup1);
		soundEffectsSelected = (RadioGroup) findViewById(R.id.radioGroup2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	public void backButtonPressed(View view){
		int musicSelectedId = musicEffectsSelected.getCheckedRadioButtonId();
		
		if (musicSelectedId == R.id.music_on){
			musicOn = true;
		}else{
			musicOn = false;
		}
		
		int soundEffectsSelectedId = soundEffectsSelected.getCheckedRadioButtonId();
		
		if (soundEffectsSelectedId == R.id.sound_effects_on){
			soundEffectsOn = true;
		}else{
			soundEffectsOn = false;
		}
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("music",musicOn);
		resultIntent.putExtra("soundEffects", soundEffectsOn);
		
		setResult(Activity.RESULT_OK,resultIntent);
		
		finish();
	}
}
