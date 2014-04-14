/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: Level.java
*     Creation Date: 2/11/2014
*            Author: Lee Synkowski
*  
*       This class is used to unpack level data from the associated
*       XML data file so it can be returned to the GameBoard for display.
*  
* 
*	Code Review:	Code reviewed 3/24/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;

public class Level {
	

	private int numberOfSquaresInWidth;
	private int numberOfSquaresInHeight;
	private float topPosition;
	private float leftPosition;
	private float rightPosition;
	private float tileSize;
	private int twoStarMoves;
	private int threeStarMoves;
	
	private int levelNumber;
	
	private GameTile[][] theLevel;
	
	private Context mContext;
	
	public final static int NUMBER_OF_LEVELS = 100;
	
	private ArrayList<GameTile> activeTiles = new ArrayList<GameTile>();
	
	public ArrayList<GameTile> getActiveTiles() {
		return activeTiles;
	}
	
	private GameTile dummyTile = new GameTile();

	public Level(int ln,float topPos, float leftPos, float rightPos, Context context) throws XmlPullParserException, IOException{
		mContext = context;
		levelNumber = ln;
		topPosition = topPos;
		leftPosition = leftPos;
		rightPosition = rightPos;
		dummyTile.setOccupied(false);
		inflateLevel(levelNumber);
		
	}

	//Returns the correct GameTile based on the string stored for a given row 
	private GameTile getTileFromChar(char charAt) {
		GameTile currentTile = new GameTile();
		currentTile.setOccupied(true);
		
		switch(charAt){
		
		//Blue Tile
		case 'B':
			currentTile.setPaintColor(Color.BLUE);
			currentTile.setTileType(TileType.GAME_TILE);
			break;
			
		//Red Tile
		case 'R':
			currentTile.setPaintColor(Color.RED);
			currentTile.setTileType(TileType.GAME_TILE);
			break;
			
		//Green Tile	
		case 'G':
			currentTile.setPaintColor(Color.GREEN);
			currentTile.setTileType(TileType.GAME_TILE);
			break;
			
		//Left Rocket	
		case 'l':
			currentTile.makeRocket(Orientation.LEFT);
			break;
			
		//Right Rocket
		case 'r':
			currentTile.makeRocket(Orientation.RIGHT);
			break;
			
		//Up Rocket
		case 'u':
			currentTile.makeRocket(Orientation.UP);
			break;
			
		//Down Rocket	
		case 'd':
			currentTile.makeRocket(Orientation.DOWN);
			break;
			
		//Bomb
		case 'b':
			currentTile.makeBomb();
			break;
			
		//Stationary Tile	
		case 's':
			currentTile.makeStationary();
			break;
			
		//Empty Tile	
		case 'x':
			currentTile = dummyTile;
			break;			
		}
		return currentTile;
	}
		
	//Populate a level given the level number from an associated XML resource
	private void inflateLevel(int levelNumber) throws XmlPullParserException, IOException{
		
		//Open an XML parsing tool for evaluating the XML data file		
		XmlResourceParser xrp = mContext.getResources().getXml(R.xml.level_data);		

		//Main loop to step through XML document
		while (xrp.getEventType()!= XmlResourceParser.END_DOCUMENT){
			
			//Evaluating Level tags
			if ((xrp.getEventType()==XmlResourceParser.START_TAG)
					&&(xrp.getName().contentEquals("level_number"))){
				xrp.next();
				
				//Matching the correct level number
				if ((xrp.getEventType() == XmlResourceParser.TEXT)&&(levelNumber == Integer.valueOf(xrp.getText()))){
					
					while ( !((xrp.getEventType()==XmlResourceParser.START_TAG)
							&&(xrp.getName().contentEquals("size")))){
						xrp.next();
					}
					xrp.next();
					
					//At the level size field
					int levelSize = Integer.valueOf(xrp.getText());
					numberOfSquaresInWidth = levelSize;
					numberOfSquaresInHeight = levelSize;
					tileSize = (rightPosition - leftPosition)/(numberOfSquaresInWidth);
					theLevel = new GameTile[numberOfSquaresInWidth][numberOfSquaresInHeight];
					
					while ( !((xrp.getEventType()==XmlResourceParser.START_TAG)
							&&(xrp.getName().contentEquals("twoStarMoves")))){
						xrp.next();
					}
					xrp.next();
					
					//At the twoStarMoves tag
					twoStarMoves = Integer.valueOf(xrp.getText());
					
					while ( !((xrp.getEventType()==XmlResourceParser.START_TAG)
							&&(xrp.getName().contentEquals("threeStarMoves")))){
						xrp.next();
					}
					xrp.next();
					
					//At the threeStarMoves tag
					threeStarMoves = Integer.valueOf(xrp.getText());
					
					
					while ( !((xrp.getEventType()==XmlResourceParser.START_TAG)
							&&(xrp.getName().contentEquals("row")))){
						xrp.next();
					}
					//At the start of the row tab
					for (int y=0;y<levelSize;y++){
						xrp.next();
						
						//Here is where you get the individual string stored on the row
						String currentRow = xrp.getText();
						
						for (int x=0;x<levelSize;x++){
							theLevel[x][y] = getTileFromChar(currentRow.charAt(x));
							theLevel[x][y].setLocationTop(topPosition + (y*tileSize));
							theLevel[x][y].setLocationLeft(leftPosition + (x*tileSize));
							activeTiles.add(theLevel[x][y]);
						}
						
						xrp.next();
						xrp.next();
					}
				}
			}
			xrp.next();
		}
		
		return;
	}

	//Public getter methods	
	public int getTwoStarMoves() {
		return twoStarMoves;
	}

	public int getThreeStarMoves() {
		return threeStarMoves;
	}
	
	public int getNumberOfSquaresInWidth() {
		return numberOfSquaresInWidth;
	}

	public int getNumberOfSquaresInHeight() {
		return numberOfSquaresInHeight;
	}
	
	public GameTile[][] getGameBoard() {
		return theLevel;
	}

	public float getTileSize() {
		return tileSize;
	}

}
