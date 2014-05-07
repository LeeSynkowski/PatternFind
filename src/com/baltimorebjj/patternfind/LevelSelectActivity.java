/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: LevelSelectActivity.java
*     Creation Date: 2/27/2014
*            Author: Lee Synkowski
*  
*     This is the Android activity that houses the level select screen
*     allowing the player to choose the desired level.
*  
* 
*	  Code Review: Code reviewed 3/25/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.baltimorebjj.patternfind;

import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;



public class LevelSelectActivity extends Activity {

	private boolean musicOn = true;
	private boolean soundEffectsOn = true;
	private Context mContext;
	private ArrayList<LevelData> levelData = new ArrayList<LevelData>();
	private LevelDataAdapter adapter;

	private static final String AD_UNIT_ID = "ca-app-pub-3921690034232729/6220364092";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicOn = getIntent().getBooleanExtra("musicOn", true);
		soundEffectsOn = getIntent().getBooleanExtra("soundEffectsOn", true);
		mContext = getBaseContext();
		
		setLevelData();
		
		setContentView(R.layout.activity_level_select);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/electrictoaster.ttf");
		Button button = (Button)findViewById(R.id.levelSelectButton);
		button.setTypeface(tf);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		
		adapter = new LevelDataAdapter(this,levelData);
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListenerListViewItem());
		

	}

	//Method to update array list containing data about the state
	//of levels completed from the Shared Preferences file
	private void setLevelData() {
		levelData.clear();
		int numberOfLevels = Level.NUMBER_OF_LEVELS;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
		for (int i=0;i<numberOfLevels;i++){
			levelData.add(new LevelData(i+1,prefs.getInt(String.valueOf(i+1)+"stars",0),prefs.getBoolean(String.valueOf(i+1)+"playable", false)));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.level_select, menu);
		return true;
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		//
		setLevelData();
		adapter.notifyDataSetChanged();
		
	}
	
	
	private class OnItemClickListenerListViewItem implements OnItemClickListener {
			 
			    @Override
			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			    	
			    	//displayInterstitial();
			    	
			    	TextView levelTextView = ((TextView) view.findViewById(R.id.levelName));
			 
			        // Get the level
			        int level = Integer.valueOf(levelTextView.getText().toString());
			 
			        //Get the intent ready to keep the data
					Intent startGameIntent = new Intent(mContext,GameActivity.class);
					startGameIntent.putExtra("musicOn", musicOn);
					startGameIntent.putExtra("soundEffectsOn",soundEffectsOn);
					startGameIntent.putExtra("level", level);
					
					//Start the new Game
					startActivity(startGameIntent);
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);		 
			    }
			 
	}
	


}
