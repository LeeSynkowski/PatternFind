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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//set background image
		RelativeLayout relativeLayout = new RelativeLayout(this);;
		relativeLayout.setBackgroundResource(R.drawable.snowbackground);

		/* This is using anon inner classes for the buttons, can we simplify?
		//get references to buttons
		Button startButton = (Button)findViewById(R.id.button1);
		Button quitButton = (Button)findViewById(R.id.button2);
		
		//create anonymous inner class listeners for button clicks
        startButton.setOnClickListener(new View.OnClickListener() {
            
        	//startButton click
        	public void onClick(View v) {
            
        		//Bring Up new screen with the Game activity
        		GameActivity gameActivity = new GameActivity();
        		gameActivity.onCreate(savedInstanceState);
            }
        });
        
        quitButton.setOnClickListener(new View.OnClickListener() {
            
        	//quitButton click
        	public void onClick(View v) {
            
        		// Perform action on click
            }
        });
		*/
		
	}
	
	public void startGame(View view){
		Intent intent = new Intent(this,GameActivity.class);
		startActivity(intent);
	}
	
	public void quitGame(View view){
		System.exit(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
