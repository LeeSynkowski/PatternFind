/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: Pattern.java
*     Creation Date: 08/30/2013
*            Author: Lee Synkowski
*  
*	  This class holds an Array List containing info
*	  used to check and display the matching pattern.
*  
*	  Code Review: Code reviewed 3/25/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;
import android.text.format.Time;

public class Pattern {
	GameTile firstElement = new GameTile();
	GameTile secondElement = new GameTile();
	GameTile thirdElement = new GameTile();
	
	private ArrayList<GameTile> tilePattern;
	private int color1,color2,color3;
	private float tileSize;
	
	//This is an array used to make it easier to select one of the
	//possible playable patterns
	private int[] possiblePatterns = new int[]
			{Color.RED,Color.GREEN,Color.BLUE,
			 Color.RED,Color.BLUE,Color.GREEN,
			 
			 Color.GREEN,Color.RED,Color.BLUE,
			 Color.GREEN,Color.BLUE,Color.RED,
			 
			 Color.BLUE,Color.RED,Color.GREEN,
			 Color.BLUE,Color.GREEN,Color.RED};
	
	
	public Pattern(float screenWidth,float screenHeight,float bottomPosition,float tileS){

		setPattern();
		
		tileSize = (3)*(screenWidth/10);
		float topLocation = ((screenHeight - bottomPosition)/2) + bottomPosition - (tileSize/2);
		
		
		
		firstElement.setPaintColor(color1);
		firstElement.setLocationTop(topLocation);
		firstElement.setLocationLeft((screenWidth/2)-((3*tileSize)/2));
		firstElement.setTileSize((int) tileSize);
		
		secondElement.setPaintColor(color2);
		secondElement.setLocationTop(topLocation);
		secondElement.setLocationLeft((screenWidth/2)-(tileSize/2));
		secondElement.setTileSize((int)tileSize);
		
		thirdElement.setPaintColor(color3);
		thirdElement.setLocationTop(topLocation);
		thirdElement.setLocationLeft((screenWidth/2)+(tileSize/2));
		thirdElement.setTileSize((int)tileSize);
		
		tilePattern = new ArrayList<GameTile>();
		tilePattern.add(firstElement);
		tilePattern.add(secondElement);
		tilePattern.add(thirdElement);
	}
	
	public ArrayList<GameTile> getTilePattern(){
		return tilePattern;
	}
	
	
	private void setPattern(){
		
		Time t = new Time();
		t.setToNow();
		Random rng = new Random(t.toMillis(false));
		int startPoint = rng.nextInt(6);
		/* switched off for non randomized testing
		color1 = possiblePatterns[(3*startPoint)];
		
		color2 = possiblePatterns[(3*startPoint)+1];
			
		color3 = possiblePatterns[(3*startPoint)+2];
		*/		
		color1 = possiblePatterns[0];
		
		color2 = possiblePatterns[1];
			
		color3 = possiblePatterns[2];
	}
	

}
