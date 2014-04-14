/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: GameBoard.java
*     Creation Date: 8/27/2013
*            Author: Lee Synkowski
*  
*       This class maintains the state of the visible game board
*       and has the necessary methods to update the game board.
*       The game board is maintained as a both a 2D array of GameTiles and 
*       an ArrayList that holds references to all of the tiles.
*  
* 
*	Code Review:	Code reviewed 3/23/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;

public class GameBoard {

	//GameBoard variables
	private GameTile[][] theBoard;
	private int numberOfSquaresInWidth;
	private int numberOfSquaresInHeight;
	public Pattern thePattern;
	private TurnEvent lastTurnEvent;
	private GameTile lastTile = new GameTile();	
	private GameTile dummyTile;
	
	
	//GameBoard collections
	private ArrayList<GameTile> activeTiles;
	private ArrayList<GameTile> movingTiles;
	private ArrayList<GameTile> tileSequence;
	private ArrayList<GameTile> rocketTiles;
	private float[] pointArray;
	
	
	//Screen display variables
	//private float screenWidth;
	private float topPosition;
	private float leftPosition;
	private float bottomPosition;
	private float rightPosition;
	private float tileSize;
	
	
	//Animation Variables
	private boolean animationComplete = true;
	private float movementIncriment;
	private int tileMoveCounter = 0;
	private int AnimationSteps = 10;
	private int animationFrame = 0;
	int finalRocketX = 0;
	int finalRocketY = 0;
	
	
	//Touch state variables
	private boolean moving = false;
	private boolean fingerDown = false;
	private boolean firstAction = true;
	
	
	//Level score variables
	private int twoStarMoves;
	private int threeStarMoves;
	
	//Constants
	private static final int   ANIMATION_STEPS_SLOW = 10;
	private static final int ANIMATION_STEPS_MEDIUM = 5;
	private static final int   ANIMATION_STEPS_FAST = 1;
	
	private static final int    MINIMUM_LOW_ANGLE = 20;
	private static final int MINIMUM_MEDIUM_ANGLE = 30;
	private static final int   MINIMUM_HIGH_ANGLE = 60;
	
	//GameBoard should have a constructor just based off the level
	public GameBoard(int level,float screenW, float screenH,Context context) throws XmlPullParserException, IOException{

		//Initialize collections
		activeTiles = new ArrayList<GameTile>();
		tileSequence = new ArrayList<GameTile>();
		movingTiles = new ArrayList<GameTile>();
		rocketTiles = new ArrayList<GameTile>();
		pointArray = new float[0];	
		
		dummyTile = new GameTile();
		dummyTile.setOccupied(false);
		lastTile = null;
		
		//Define playable area on the screen
		topPosition =  screenW/20;
		leftPosition =  screenW/20;
		bottomPosition = topPosition + ((9* screenW)/10);
		rightPosition = leftPosition + ((9* screenW)/10);
		
		//Initialize a given level
		Level currentLevel = new Level(level,topPosition,leftPosition,rightPosition,context);
		
		numberOfSquaresInWidth = currentLevel.getNumberOfSquaresInWidth();
		numberOfSquaresInHeight = currentLevel.getNumberOfSquaresInHeight();
		
		theBoard = currentLevel.getGameBoard();
		tileSize = currentLevel.getTileSize();
		activeTiles = currentLevel.getActiveTiles();
		twoStarMoves = currentLevel.getTwoStarMoves();
		threeStarMoves = currentLevel.getThreeStarMoves();
		
		//Initialize the pattern displayed at the bottom of the screen
		thePattern = new Pattern(screenW,screenH,bottomPosition,tileSize);
		
	}
	
	//These methods are called when the screen rotates, and also after pieces
	//are cleared to drop pieces down to the correct side
	public void handleLowPitch(float pitchAngle){
		
		adjustArrayOneRowLowPitch();
		animationComplete = false;
		setAnimationSpeed(pitchAngle);
		movementIncriment = tileSize/AnimationSteps;
		lastTurnEvent = TurnEvent.LOW_PITCH;
	}

	public void handleHighPitch(float pitchAngle){
		
		adjustArrayOneRowHighPitch();
		animationComplete = false;
		setAnimationSpeed(pitchAngle);
		movementIncriment = tileSize/AnimationSteps;
		lastTurnEvent = TurnEvent.HIGH_PITCH;
	}
	
	public void handleLowRoll(float rollAngle) {
		
		adjustArrayOneRowRightLowRoll();
		animationComplete = false;
		setAnimationSpeed(rollAngle);
		movementIncriment = tileSize/AnimationSteps;
		lastTurnEvent = TurnEvent.LOW_ROLL;
	}
	
	public void handleHighRoll(float rollAngle){

		adjustArrayOneRowLeftHighRoll();
		animationComplete = false;
		setAnimationSpeed(rollAngle);
		movementIncriment = tileSize/AnimationSteps;
		lastTurnEvent = TurnEvent.HIGH_ROLL;
	}
	
	//These methods update the 2D GameBoard array as needed
	
	//Moving tiles "down" the game board.  Start with the row 2nd from the "bottom".	
	public void adjustArrayOneRowLowPitch(){

		//Check if there is a tile directly "below" it, and if not move that tile "down".
		for (int heightIndex=(numberOfSquaresInHeight-2);heightIndex!=-1;heightIndex--){
			for (int widthIndex=0;widthIndex<numberOfSquaresInWidth;widthIndex++){
				
				//Check if there is a tile in the space & not tile above it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex][heightIndex+1].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   
					
					//Get gameTile from the current spot on the board 
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					
					//Remove the tile from the list of activeTiles
					activeTiles.remove(currentTile);
					
					//Set the board location of the above space to that tile
					theBoard[widthIndex][heightIndex+1] = currentTile;
					
					//
					//If the tile is in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex][heightIndex+1]);
						adjustPointArray();
					}
						
					//Add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
					//Set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}		
	}

	//Moving tiles "up" the game board.  Start with the row 2nd from the "top".
	public void adjustArrayOneRowHighPitch(){		

		//Check if there is a tile directly "above" it, and if not move that tile "up".
		for (int heightIndex=1;heightIndex<numberOfSquaresInHeight;heightIndex++){
			for (int widthIndex=0;widthIndex<numberOfSquaresInWidth;widthIndex++){
				
				//Check if there is a tile in the space & not tile below it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex][heightIndex-1].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   //coordinates of tile above it
					
					//Get gameTile from the current spot on the board
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					
					//Remove the tile from the list of active tiles
					activeTiles.remove(currentTile);
					
					//Set the board location of the "above" space to that tile
					theBoard[widthIndex][heightIndex-1] = currentTile;

					//If its in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex][heightIndex-1]);
						adjustPointArray();
					}
					
					//Add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
					//Set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}

	//Moving tiles "right" the game board.  Start with the row 2nd from the right.
	public void adjustArrayOneRowRightLowRoll(){		

		//Check if there is a tile directly "right" of it, and if not move that tile "right".
		for (int widthIndex=(numberOfSquaresInWidth-2);widthIndex!=-1;widthIndex--){
			for (int heightIndex=0;heightIndex!=numberOfSquaresInHeight;heightIndex++){
				
				//If there is a tile & a tile to the right of it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex+1][heightIndex].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   //coordinates of tile right of it
					
					//Get gameTile from the current spot on the board
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					
					//Remove the tile from the list of active tiles
					activeTiles.remove(currentTile);
					
					//Set the board location to the right of the above space to that tile
					theBoard[widthIndex+1][heightIndex] = currentTile;
					
					//If its in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex+1][heightIndex]);
						adjustPointArray();
					}
					
					//Add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
					//Set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}

	//Moving tiles "left" the game board.  Start with the row 2nd from the "left".
	public void adjustArrayOneRowLeftHighRoll(){		

		//Check if there is a tile directly "left" of it, and if not move that tile "left".
		for (int widthIndex=1;widthIndex!=numberOfSquaresInWidth;widthIndex++){
			for (int heightIndex=0;heightIndex!=numberOfSquaresInHeight;heightIndex++){
				
				//Check if there is a tile & a tile to the left of it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex-1][heightIndex].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   //coordinates of tile left of it

					//Get gameTile from the current spot on the board
					GameTile currentTile = theBoard[widthIndex][heightIndex];
										
					//Remove the tile from the list of active tiles
					activeTiles.remove(currentTile);
					
					//Set the board location to the right of the above space to that tile
					theBoard[widthIndex-1][heightIndex] = currentTile;

					//If its in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex-1][heightIndex]);
						adjustPointArray();
					}
					
					//Add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
					//Set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}


	//This method responds to touch event that occurs within the game play area
	public synchronized TouchEvent handleTouch(float x, float y) {		
		
		//Check if touch is outside game board, if so return and do nothing
		if ( (x<leftPosition) || (x>rightPosition) || (y<topPosition) || (y>bottomPosition) ){
			return TouchEvent.NOTHING;
			
		//Check if the touched tile is occupied on the game board, 
		//and not a member of the moving tiles
		}else if ((theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].isOccupied())&&(!movingTiles.contains(theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]))){
				
				//Check if the touched tile is a game tile
				if (theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].getTileType()==TileType.GAME_TILE){
					
					//If there is a tile sequence, find the location 
					//of the last tile in the sequence
					if (tileSequence.size()>0){
						lastTile = tileSequence.get(tileSequence.size()-1);		
					} else{
						lastTile = null;
					}
					
					//If the touch in in the moving state and in the same tile as the last touched,
					//tile we do nothing (ie do NOT throw it to update valid move)
					if ((moving)&&(lastTile == theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)])){
						return TouchEvent.NOTHING;
					}
					
					//If there are three or more tiles in the chain and user touches the first tile
					if ((tileSequence.size()>=3)){
						
						if (theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]==tileSequence.get(0)){
							
							//Check if the tile sequence is valid in handleRemoveButtonPress()
							if (handleRemoveButtonPress()){
								//this checks if correct and automatically removes them
								clearTileSequence();
								return TouchEvent.REMOVE_BUTTON_CORRECT;
							} else {
								clearTileSequence();
								return TouchEvent.REMOVE_BUTTON_INCORRECT;
							}
						}
					}
					
					
					if (updateValidMove(x,y)){
						return TouchEvent.ADJUST_TILE_SEQUENCE;
					}
				}
				if ((theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].getTileType()==TileType.BOMB_TILE)){
					//remove surrounding tiles in the event of a bomb block selection
					clearTileSequence();
					bombTileSelected(x,y);
					return TouchEvent.BOMB_TILE;
				}
				if ((theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].getTileType()==TileType.ROCKET_TILE)){
					clearTileSequence();
					rocketTileSelected(x,y,theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].getOrientation());
					return TouchEvent.ROCKET_TILE;
				}
		}
		return TouchEvent.NOTHING;
		
	}

	//This methods responds to a touch event that occurs on a bomb tile
	private void bombTileSelected(float x, float y) {
		// Remove the tiles surrounding the bomb tile from the active tile list 
		// and set references to them on the board with the dummy tile
		int bombX = turnXToWidthIndex(x);
		int bombY = turnYToHeightIndex(y);
		
		//Remove the bomb
		activeTiles.remove(theBoard[bombX][bombY]); 
		theBoard[bombX][bombY] = dummyTile;
		
		//To avoid array index errors, you need to check for all possible locations
		//of the bomb, and delete surrounding tiles as appropriate.
		
		//Bomb in top left corner
		if ((bombX == 0) && (bombY == 0)){ 
			activeTiles.remove(theBoard[bombX+1][bombY]); //remove the one to the right
			theBoard[bombX+1][bombY] = dummyTile;
			activeTiles.remove(theBoard[bombX][bombY+1]); //remove the one below
			theBoard[bombX][bombY+1] = dummyTile;
			activeTiles.remove(theBoard[bombX+1][bombY+1]); //remove the one right below
			theBoard[bombX+1][bombY+1] = dummyTile;
			
		//Bomb in bottom right corner
		}	else if ((bombX == numberOfSquaresInWidth-1)&&(bombY == numberOfSquaresInHeight-1)){ 
						activeTiles.remove(theBoard[bombX][bombY-1]); //remove the one above
						theBoard[bombX][bombY-1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY]); //remove the one to the left
						theBoard[bombX-1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY-1]); //remove the one to the left above
						theBoard[bombX-1][bombY-1] = dummyTile;
		
		//Bomb in bottom left corner				
		}	else if ((bombX == 0)&&(bombY == numberOfSquaresInHeight-1)){ 
						activeTiles.remove(theBoard[bombX+1][bombY]); //remove the one to the right
						theBoard[bombX+1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX][bombY-1]); //remove the one above
						theBoard[bombX][bombY-1] = dummyTile;
						activeTiles.remove(theBoard[bombX+1][bombY-1]); //remove the one to the right above
						theBoard[bombX+1][bombY-1] = dummyTile;
		
		//Bomb in top right
		}   else if ((bombX == numberOfSquaresInWidth-1) && (bombY == 0)){ 
						activeTiles.remove(theBoard[bombX-1][bombY]); //remove the one to the left
						theBoard[bombX-1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX][bombY+1]); //remove the one below
						theBoard[bombX][bombY+1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY+1]); //remove the one left below
						theBoard[bombX-1][bombY+1] = dummyTile;
			
		//Bomb along left side
		}   else if (bombX == 0){ 
					activeTiles.remove(theBoard[bombX+1][bombY]); //remove the one to the right
					theBoard[bombX+1][bombY] = dummyTile;
					activeTiles.remove(theBoard[bombX][bombY+1]); //remove the one below
					theBoard[bombX][bombY+1] = dummyTile;
					activeTiles.remove(theBoard[bombX+1][bombY+1]); //remove the one right below
					theBoard[bombX+1][bombY+1] = dummyTile;
					activeTiles.remove(theBoard[bombX][bombY-1]); //remove the one above
					theBoard[bombX][bombY-1] = dummyTile;
					activeTiles.remove(theBoard[bombX+1][bombY-1]); //remove the one to the right above
					theBoard[bombX+1][bombY-1] = dummyTile;
		
		//Bomb along right side
		}	else if (bombX == numberOfSquaresInWidth-1){ 
					activeTiles.remove(theBoard[bombX-1][bombY]); //remove the one to the left
					theBoard[bombX-1][bombY] = dummyTile;
					activeTiles.remove(theBoard[bombX][bombY+1]); //remove the one below
					theBoard[bombX][bombY+1] = dummyTile;
					activeTiles.remove(theBoard[bombX-1][bombY+1]); //remove the one left below
					theBoard[bombX-1][bombY+1] = dummyTile;
					activeTiles.remove(theBoard[bombX-1][bombY-1]); //remove the one to the left above
					theBoard[bombX-1][bombY-1] = dummyTile;
					activeTiles.remove(theBoard[bombX][bombY-1]); //remove the one above
					theBoard[bombX][bombY-1] = dummyTile;
					
		//Bomb along top side
		}	else if (bombY == 0){  
						activeTiles.remove(theBoard[bombX+1][bombY]); //remove the one to the right
						theBoard[bombX+1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX][bombY+1]); //remove the one below
						theBoard[bombX][bombY+1] = dummyTile;
						activeTiles.remove(theBoard[bombX+1][bombY+1]); //remove the one right below
						theBoard[bombX+1][bombY+1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY+1]); //remove the one left below
						theBoard[bombX-1][bombY+1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY]); //remove the one to the left
						theBoard[bombX-1][bombY] = dummyTile;						
						
		//Bomb along bottom side
		}	else if (bombY == numberOfSquaresInHeight-1){ 
						activeTiles.remove(theBoard[bombX+1][bombY]); //remove the one to the right
						theBoard[bombX+1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX][bombY-1]); //remove the one above
						theBoard[bombX][bombY-1] = dummyTile;
						activeTiles.remove(theBoard[bombX+1][bombY-1]); //remove the one to the right above
						theBoard[bombX+1][bombY-1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY-1]); //remove the one to the left above
						theBoard[bombX-1][bombY-1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY]); //remove the one to the left
						theBoard[bombX-1][bombY] = dummyTile;
						
		//Bomb located anywhere else on the board				
		}	else { 
			for (int indexX = bombX-1;indexX<bombX+2;indexX++){
				for (int indexY = bombY-1;indexY<bombY+2;indexY++){
					if (theBoard[indexX][indexY].getTileType() != TileType.STATIONARY_TILE){
						activeTiles.remove(theBoard[indexX][indexY]);
						theBoard[indexX][indexY] = dummyTile;
					}
				}
			}
		}
		
	}

	//This methods responds to a touch event that occurs on a rocket tile
	private void rocketTileSelected(float x, float y,Orientation orientation){
		//Clear out any previous Tiles from the list
		rocketTiles.clear();
		
		//Find rockets initial co-ordinates
		int rocketX = turnXToWidthIndex(x);
		int rocketY = turnYToHeightIndex(y);
		
		//The rocket tile is the 0 element in the array list, and it is removed from the active tile array list
		rocketTiles.add(theBoard[rocketX][rocketY]);
		activeTiles.remove(theBoard[rocketX][rocketY]);
		theBoard[rocketX][rocketY] = dummyTile;
		
		//Add to the array list all of the tiles removed by the rocket, 
		//then remove these tiles from the list of active tiles and the game board
		
		//Must check for each type of rocket orientation
		if (orientation == Orientation.UP){
			
			int yIndex = rocketY-1;
			
			while ((yIndex>=0)&&(theBoard[rocketX][yIndex].getTileType()!=TileType.STATIONARY_TILE)){
				
				//If there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
				if (theBoard[rocketX][yIndex].isOccupied()){
					rocketTiles.add(theBoard[rocketX][yIndex]);
					activeTiles.remove(theBoard[rocketX][yIndex]);
					theBoard[rocketX][yIndex] = dummyTile;
				}
				yIndex = yIndex - 1;
			}
			
			finalRocketX = rocketX;
			finalRocketY = yIndex +1;
		
		} else if (orientation == Orientation.DOWN){
			
			int yIndex = rocketY+1;
			
			while ((yIndex<=numberOfSquaresInHeight-1)&&(theBoard[rocketX][yIndex].getTileType()!=TileType.STATIONARY_TILE)){
			
				//If there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
				if (theBoard[rocketX][yIndex].isOccupied()){
					rocketTiles.add(theBoard[rocketX][yIndex]);
					activeTiles.remove(theBoard[rocketX][yIndex]);
					theBoard[rocketX][yIndex] = dummyTile;
				}
				yIndex = yIndex + 1;
			}
			
			finalRocketX = rocketX;
			finalRocketY = yIndex - 1;
		
		} else if (orientation == Orientation.LEFT){
			
			int xIndex = rocketX-1;
			
			while ((xIndex>=0)&&(theBoard[xIndex][rocketY].getTileType()!=TileType.STATIONARY_TILE)){
			
				//If there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
				if (theBoard[xIndex][rocketY].isOccupied()){
					rocketTiles.add(theBoard[xIndex][rocketY]);
					activeTiles.remove(theBoard[xIndex][rocketY]);
					theBoard[xIndex][rocketY] = dummyTile;
				}
				xIndex = xIndex - 1;
			}
			
			finalRocketX = xIndex+1;
			finalRocketY = rocketY;
		
		} else if (orientation == Orientation.RIGHT){
			
			int xIndex = rocketX+1;
			
			while ((xIndex<=numberOfSquaresInWidth-1)&&(theBoard[xIndex][rocketY].getTileType()!=TileType.STATIONARY_TILE)){
			
				//If there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
				if (theBoard[xIndex][rocketY].isOccupied()){
					rocketTiles.add(theBoard[xIndex][rocketY]);
					activeTiles.remove(theBoard[xIndex][rocketY]);
					theBoard[xIndex][rocketY] = dummyTile;
				}
				xIndex = xIndex+1;
			}
			
			finalRocketX = xIndex - 1;
			finalRocketY = rocketY;
		}	
	}
	
	//Method to determine if the Rocket has reached its final location
	public boolean isRocketDone(int i, int j) {
		if ((i == finalRocketX)&&(j == finalRocketY))
			return true;
		return false;
	}
	
	
	//Called when attempting to remove tiles,
	//returns true if the pattern matched the desired pattern
	//and removes the needed tiles
	private synchronized boolean handleRemoveButtonPress() {
		
		if (checkPattern()){
			for (GameTile tile:tileSequence){
				activeTiles.remove(tile);
				theBoard[turnXToWidthIndex(tile.getLocationLeft()+2)][turnYToHeightIndex(tile.getLocationTop()+2)] = dummyTile;
			}
			clearTileSequence();
			firstAction = false;
			return true;
			
		} else {
			clearTileSequence();
			firstAction = false;
		}
		return false;
	}

	//Checks the selected pattern against the given pattern to see if 
	//it matches
	private boolean checkPattern() {
		
		ArrayList<GameTile> thePatternArray = thePattern.getTilePattern();
		int checkIndex = 0;
		boolean longEnough = false;
		for (GameTile sequenceTile:tileSequence){
			
			//Keep re-circling through the check index
			if (checkIndex==thePatternArray.size()){
				checkIndex = 0;
			}
			
			// If the tile colors don't match
			
			Paint tempPaint = sequenceTile.getPaint();
			tempPaint.setAlpha(255);
			if (tempPaint.getColor()!=thePatternArray.get(checkIndex).getPaintColor()){
				return false;
			}
			checkIndex++;
			
			//Make sure the pattern is at least 3 long
			if (checkIndex==thePatternArray.size()&&(longEnough==false)){
				longEnough = true;
			}
		}
		if (longEnough)
			return true;
		else
			return false;		
	}

	//Called to update the tile sequence
	private boolean updateValidMove(float x, float y) {

		//If it is the first touched tile
		if ((tileSequence.isEmpty())&&(firstAction)){
			tileSequence.add(theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]);
			theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
			firstAction = false;
			return true;
		}
		
		//If you are clearing the first tile
		if((tileSequence.size()==1)&&(firstAction)){
				//if the touched tile matches the tile in the sequence
				if (tileSequence.get(0)==theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]){
					tileSequence.remove(tileSequence.get(0));
					theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
					firstAction = false;
					return true;
				}
		}		

		//If you are clearing the second or more tile via tap	
		if ((tileSequence.size()>1)&&(firstAction)) {
			//If the last tile in the tile sequence is the same one that is touched
			if (tileSequence.get(tileSequence.size()-1)== theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]){
				tileSequence.remove(tileSequence.get(tileSequence.size()-1));
				theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();		
				if (tileSequence.size()>1){
					adjustPointArray();
				}
				firstAction = false;
				return true;
			}
		} 
		
		//If you are dragging back into the last correct square
		if (tileSequence.size()>=2){
				if (tileSequence.get(tileSequence.size()-2)==theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]){
					GameTile lastTileDraggedOff = tileSequence.get(tileSequence.size()-1);
					tileSequence.remove(tileSequence.get(tileSequence.size()-1));
					theBoard[turnXToWidthIndex(lastTileDraggedOff.getLocationLeft())][turnYToHeightIndex(lastTileDraggedOff.getLocationTop())].switchTouched();
					if (tileSequence.size()>1){
						adjustPointArray();
					}
					return true;
				}
		}
		
		
		//Check if the touched tile is not in the tileSequence
		if ((!tileSequence.contains(theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]))&&(tileSequence.size()>0)&&(moving==true||firstAction==true)){
			//then check all four sides of the most recent tile and if it is one of them, then add it
			GameTile mostRecentTile = tileSequence.get(tileSequence.size()-1);
			GameTile touchedTile = theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)];
			
			//touchedTile is to the right of mostRecentTile
			if ((turnXToWidthIndex(mostRecentTile.getLocationLeft()+2))>0){
				if (touchedTile == theBoard[turnXToWidthIndex(mostRecentTile.getLocationLeft()+2)-1][turnYToHeightIndex(mostRecentTile.getLocationTop()+2)]){			
					tileSequence.add(touchedTile);
					theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
					adjustPointArray();
					firstAction = false;
					return true;
				}
			}
			
			//touchedTile is to the left of mostRecentTile
			if ((turnXToWidthIndex(mostRecentTile.getLocationLeft()+2))<numberOfSquaresInWidth - 1){
				if (touchedTile == theBoard[turnXToWidthIndex(mostRecentTile.getLocationLeft()+2)+1][turnYToHeightIndex(mostRecentTile.getLocationTop()+2)]){	
					tileSequence.add(touchedTile);
					theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
					adjustPointArray();
					firstAction = false;
					return true;
				}
			}
			
			//touchedTile is below mostRecentTile
			if ((turnYToHeightIndex(mostRecentTile.getLocationTop()+2))<numberOfSquaresInHeight - 1){
				if (touchedTile == theBoard[turnXToWidthIndex(mostRecentTile.getLocationLeft()+2)][turnYToHeightIndex(mostRecentTile.getLocationTop()+2)+1]){	

					tileSequence.add(touchedTile);
					theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
					adjustPointArray();
					firstAction = false;
					return true;
				}
			}
			
			//touchedTile is above mostRecentTile
			if ((turnYToHeightIndex(mostRecentTile.getLocationTop()+2))>0){
				if (touchedTile == theBoard[turnXToWidthIndex(mostRecentTile.getLocationLeft()+2)][turnYToHeightIndex(mostRecentTile.getLocationTop()+2)-1]){
					tileSequence.add(touchedTile);
					theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
					adjustPointArray();
					firstAction = false;
					return true;
				}
			}
		}	
		return false;
	}

	//Used to turn the pixel Y location into a height index for the given GameBoard
	public int turnYToHeightIndex(float y) {
		
		int ret = (int)((y - topPosition)/tileSize);
		
		if (ret >= numberOfSquaresInHeight){
			return numberOfSquaresInHeight - 1;
		} else if (ret <= 0){
			return 0;
		} else {
			return ret;
		}
			
	}
	
	//Used to turn the pixel X location into a width index for the given GameBoard
	public int turnXToWidthIndex(float x) {

		int ret = (int)((x - leftPosition)/tileSize);
	
		if (ret >= numberOfSquaresInWidth){
			return numberOfSquaresInWidth - 1;
		} else if (ret <= 0){
			return 0;
		} else {
			return ret;
		}
	}
	
	//Check if the Tile Sequence is still continuous and valid
	public synchronized boolean isTileSequenceContinuous(){
		int tileSequenceSize = tileSequence.size();
		if (tileSequenceSize>=2){
			for (int i=0;i<tileSequenceSize-1;i++){
				if (tileSequence.get(i).getLocationLeft() != tileSequence.get(i+1).getLocationLeft()){
					if (tileSequence.get(i).getLocationTop() != tileSequence.get(i+1).getLocationTop()){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//Adjust the array containing the information needed to draw the line
	//indicated the selected tile sequence
	public synchronized void adjustPointArray(){
		if (tileSequence.size()>0){
			pointArray = new float[4*tileSequence.size()];
		
			for (int tileNumber=0;tileNumber!=tileSequence.size()-1;tileNumber++){
				pointArray[(4*tileNumber)]   = (tileSequence.get(tileNumber).getLocationLeft()) + (tileSize/2); 
				pointArray[(4*tileNumber)+1] = (tileSequence.get(tileNumber).getLocationTop()) + (tileSize/2);
				pointArray[(4*tileNumber)+2] = (tileSequence.get(tileNumber+1).getLocationLeft()) + (tileSize/2); 
				pointArray[(4*tileNumber)+3] = (tileSequence.get(tileNumber+1).getLocationTop()) + (tileSize/2);
			}
		}
	}
	
	//Update the locations of the moving tiles if an animation step occurred
	public synchronized void animationStepOccurred() {

		//The animation is over
		if (animationFrame >= AnimationSteps){
			animationFrame = 0;
			animationComplete = true;
			
			//Merge moving tiles with active tiles
			for (GameTile tile:movingTiles){
				activeTiles.add(tile);
			}
			
			//Erase moving tiles
			movingTiles.clear();
			
		} else {
			
			//Deal with updating the moving tiles
			for (GameTile tile:movingTiles){

				//Update the direction of the tile movement based on the previous turn
				if (lastTurnEvent == TurnEvent.HIGH_PITCH){
					tile.setLocationTop(tile.getLocationTop()-movementIncriment);	
				}
				
				if (lastTurnEvent == TurnEvent.LOW_PITCH){					
					tile.setLocationTop(tile.getLocationTop()+movementIncriment);
				}
				
				if (lastTurnEvent == TurnEvent.HIGH_ROLL){
					tile.setLocationLeft(tile.getLocationLeft()-movementIncriment);
				}
				
				if (lastTurnEvent == TurnEvent.LOW_ROLL){
					tile.setLocationLeft(tile.getLocationLeft()+movementIncriment);
				}
			}
			animationFrame++;
		}
		
	}
	
	//Sets the number of steps in block animation based on the angle read from the sensor
	private void setAnimationSpeed(float Angle) {
		if (Math.abs(Angle)>=MINIMUM_LOW_ANGLE && Math.abs(Angle) < MINIMUM_MEDIUM_ANGLE){
			AnimationSteps = ANIMATION_STEPS_SLOW ;
		}	
		if (Math.abs(Angle)>=MINIMUM_MEDIUM_ANGLE && Math.abs(Angle) < MINIMUM_HIGH_ANGLE){
			AnimationSteps = ANIMATION_STEPS_MEDIUM;
		}
		if (Math.abs(Angle)>=MINIMUM_HIGH_ANGLE){
			AnimationSteps = ANIMATION_STEPS_FAST;
		}
	}
	
	//Method used to clear the Tile Sequence
	public synchronized void clearTileSequence() {
		for (GameTile tile:tileSequence){
			tile.switchTouched();
		}
		tileSequence.clear();
	}
	
	//Method to determine if the finger is dragging and
	//if it is a first action
	public void setFingerDown(boolean fingerDown) {

		this.fingerDown = fingerDown;
		
		//If you lift the finger you will be in a first action state
		if (this.fingerDown == false){
			firstAction = true;
		}
	}
	
	//Method to return a copy of the active tiles so they can
	//displayed by the drawing thread without concurrency errors
	public synchronized ArrayList<GameTile> getCopyOfActiveTiles(){
		ArrayList<GameTile> ret = new ArrayList<GameTile>();
		for (GameTile tile:activeTiles){
			GameTile newTile = new GameTile(tile);
			ret.add(newTile);
		}
		return ret;
	}
	
	//Method to return a copy of the moving tiles so they can
	//displayed by the drawing thread without concurrency errors	
	public synchronized ArrayList<GameTile> getCopyOfMovingTiles(){
		ArrayList<GameTile> ret = new ArrayList<GameTile>();
		for (GameTile tile:movingTiles){
			GameTile newTile = new GameTile(tile);
			ret.add(newTile);
		}		
		return ret;
	}
	
	//Method to return a copy of the tile sequence so they can
	//displayed by the drawing thread without concurrency errors	
	public synchronized ArrayList<GameTile> getCopyOfTileSequqnce(){
		ArrayList<GameTile> ret = new ArrayList<GameTile>();
		for (GameTile tile:tileSequence){
			GameTile newTile = new GameTile(tile);
			ret.add(newTile);
		}		
		return ret;
	}
	
	//Method to return a copy of the rocket tiles so they can
	//displayed by the drawing thread without concurrency errors	
	public synchronized ArrayList<GameTile> getCopyOfRocketTiles(){
		ArrayList<GameTile> ret = new ArrayList<GameTile>();
		for (GameTile tile:rocketTiles){
			GameTile newTile = new GameTile(tile);
			ret.add(newTile);
		}		
		return ret;
	}
	
	//Method to return a copy of the point array for use by the
	//the drawing thread without concurrency errors
	public synchronized float[] getCopyOfPointArray(){
		float[] ret = new float[pointArray.length];
		System.arraycopy(pointArray, 0, ret, 0, pointArray.length);
		return ret;
	}
	
	
	//Public getter and Setter methods
	public ArrayList<GameTile> getActiveTiles(){
		return activeTiles;
	}
	
	public float getTileSize() {
		return tileSize;
	}
	
	public float[] getPointArray() {
		return pointArray;
	}

	public void setLastTurnEvent(TurnEvent lastTurnEvent) {
		this.lastTurnEvent = lastTurnEvent;
	}
	
	public float getMovementIncriment() {
		return movementIncriment;
	}
	
	public ArrayList<GameTile> getMovingTiles() {
		return movingTiles;
	}
	
	public int getAnimationsteps() {
		return AnimationSteps;
	}

	public float getTopPosition(){
		return topPosition;
	}
	
	public float getLeftPosition(){
		return leftPosition;
	}

	public ArrayList<GameTile> getRocketTiles() {
		return rocketTiles;
	}

	public void setAnimationComplete(boolean ac){
		this.animationComplete = ac;
	}
	
	public boolean isMoving(){
		return moving;
	}
	
	public void setMoving(boolean mov){
		moving = mov;
	}
	
	public boolean isFingerDown() {
		return fingerDown;
	}

	public int getNumberOfSquaresInWidth() {
		return numberOfSquaresInWidth;
	}

	public int getNumberOfSquaresInHeight() {
		return numberOfSquaresInHeight;
	}
	
	public int getTileMoveCounter() {
		return tileMoveCounter;
	}

	public void setTileMoveCounter(int tileMoveCounter) {
		this.tileMoveCounter = tileMoveCounter;
	}

	public void incrementTileMoveCounter() {
		this.tileMoveCounter++;	
	}

	public void resetTileMoveCounter() {
		this.tileMoveCounter=0;
	}
	
	public boolean isAnimationComplete() {
		return animationComplete;
	}
	
	public float getBottomPosition() {
		return bottomPosition;
	}

	public float getRightPosition() {
		return rightPosition;
	}

	public int getTwoStarMoves() {
		return twoStarMoves;
	}

	public int getThreeStarMoves() {
		return threeStarMoves;
	}

}
