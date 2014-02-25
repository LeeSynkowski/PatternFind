package com.baltimorebjj.patternfind;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	static final int OPTIONS_MENU_SELECTED = 0;
	private boolean musicOn = true;
	private boolean soundEffectsOn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//set background image
		RelativeLayout relativeLayout = new RelativeLayout(this);;
		relativeLayout.setBackgroundResource(R.drawable.snowbackground);
		
	}
	
	public void startGame(View view){
		Intent startGameIntent = new Intent(this,GameActivity.class);
		//add intent extras which will toggle off music and sound
		startGameIntent.putExtra("music", musicOn);
		startGameIntent.putExtra("soundEffects",soundEffectsOn);
		startGameIntent.putExtra("level", 1);
		startActivity(startGameIntent);
	}
	
	public void optionsMenu(View view){
		Intent optionsIntent = new Intent(this,OptionsActivity.class);
		startActivityForResult(optionsIntent,OPTIONS_MENU_SELECTED);
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
		if (request == OPTIONS_MENU_SELECTED){
			if (result==RESULT_OK){
				musicOn = data.getBooleanExtra("music", true);
				soundEffectsOn = data.getBooleanExtra("soundEffects", true);
			}
		}

	}

}
