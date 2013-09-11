package com.baltimorebjj.patternfind;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;

public class Pattern {
	GameTile firstElement = new GameTile();
	GameTile secondElement = new GameTile();
	GameTile thirdElement = new GameTile();
	
	private ArrayList<GameTile> tilePattern;
	
	
	public Pattern(float screenWidth,float tileSize){

		int color1,color2,color3;
		
		/*  //'come back and add a way to randomize this so there is always one of each
		color1 = getColor();
		color2 = getColor();
		color3 = getColor();

		color1 = Color.RED;
		color2 = Color.GREEN;
		color3 = Color.BLUE;

		*/
				
		firstElement.setPaintColor(Color.BLUE);
		firstElement.setLocationTop(tileSize/3);
		firstElement.setLocationLeft(screenWidth/2);
		firstElement.setTileSize((int) tileSize);
		
		secondElement.setPaintColor(Color.RED);
		secondElement.setLocationTop(tileSize/3);
		secondElement.setLocationLeft((screenWidth/2)+tileSize);
		secondElement.setTileSize((int)tileSize);
		
		thirdElement.setPaintColor(Color.GREEN);
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
	/*
	private int getColor(){
		Random rng = new Random();
		switch(rng.nextInt(3)){
			case (0):
				return Color.RED;
			case (1):
				return Color.GREEN;
			case (2):
				return Color.BLUE;
		}
		return Color.BLACK;
	}
	*/

}
