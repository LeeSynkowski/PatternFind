/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: LevelDataAdapter.java
*     Creation Date: 3/19/2014
*            Author: Lee Synkowski
*  
*     This is an Android array adapter sub class used to return the needed view
*     for the ListView that displays the level select items. 
*  
* 
*	Code Review:	Code reviewed 3/25/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.baltimorebjj.patternfind;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class LevelDataAdapter extends ArrayAdapter<LevelData>{

	public LevelDataAdapter(Context context, List<LevelData> objects) {
		super(context,R.layout.level_item,objects);
	}

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LevelData levelData = getItem(position);  
        
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.level_item, null);
        }
        // Lookup view for data population
        TextView levelName = (TextView) convertView.findViewById(R.id.levelName);
        //TextView level = (TextView) convertView.findViewById(R.id.listViewLevel);
       // Typeface tf = Typeface.createFromAsset(parent.getContext().getAssets(), "electrictoaster.ttf");
       // levelName.setTypeface(tf);
       // level.setTypeface(tf);
        RatingBar stars = (RatingBar) convertView.findViewById(R.id.starsPerLevel);
        // Populate the data into the template view using the data object
        levelName.setText("" + levelData.levelNumber);
        stars.setRating(levelData.numberOfStars);

        
        //Check if the given level is playable
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        
		String levelPlayableKey = String.valueOf(position+1) + "playable";
		if (prefs.getBoolean(levelPlayableKey, false)){
        	convertView.setBackgroundResource(R.drawable.level_item_background);
        } else {
        	convertView.setBackgroundResource(R.drawable.level_item_background_greyed);
        }

        
        // Return the completed view to render on screen
        return convertView;
    }
    
    
    @Override
    public boolean isEnabled(int position) {
    	LevelData levelData = getItem(position);  
    	return levelData.playable;
    }
    
	
}
