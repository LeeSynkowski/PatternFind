/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: InstructionsActivity.java
*     Creation Date: 3/04/2014
*            Author: Lee Synkowski
*  
*       This class is the Android activity that holds the instructions
*       displayed to the player.
*  
* 
*	Code Review:	Code reviewed 3/24/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.widget.TextView;

public class InstructionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/electrictoaster.ttf");
		TextView mTextView = (TextView)findViewById(R.id.goalTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.patternTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.selectingTilesTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.removingTilesTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.tiltingTilesTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.resetTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.specialTilesTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.blackHoleTilesTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.rocketTilesTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.supernovaTilesTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.hintsTitleTextView);
		mTextView.setTypeface(tf);
		mTextView = (TextView)findViewById(R.id.videoLinkTitleTextView);
		mTextView.setTypeface(tf);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.instructions, menu);
		return true;
	}

}
