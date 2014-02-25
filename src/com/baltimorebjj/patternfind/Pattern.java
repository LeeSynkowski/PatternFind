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
	
	private int[] possiblePatterns = new int[]
			{Color.RED,Color.GREEN,Color.BLUE,
			 Color.RED,Color.BLUE,Color.GREEN,
			 
			 Color.GREEN,Color.RED,Color.BLUE,
			 Color.GREEN,Color.BLUE,Color.RED,
			 
			 Color.BLUE,Color.RED,Color.GREEN,
			 Color.BLUE,Color.GREEN,Color.RED};
	
	
	public Pattern(float screenWidth,float tileSize){

		setPattern();
				
		firstElement.setPaintColor(color1);
		firstElement.setLocationTop(tileSize/3);
		firstElement.setLocationLeft(screenWidth/2);
		firstElement.setTileSize((int) tileSize);
		
		secondElement.setPaintColor(color2);
		secondElement.setLocationTop(tileSize/3);
		secondElement.setLocationLeft((screenWidth/2)+tileSize);
		secondElement.setTileSize((int)tileSize);
		
		thirdElement.setPaintColor(color3);
		thirdElement.setLocationTop(tileSize/3);
		thirdElement.setLocationLeft((screenWidth/2)+(2*tileSize));
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
		
		color1 = possiblePatterns[(3*startPoint)];
		
		color2 = possiblePatterns[(3*startPoint)+1];
			
		color3 = possiblePatterns[(3*startPoint)+2];		
	}
	

}
