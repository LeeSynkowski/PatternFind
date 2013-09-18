package com.baltimorebjj.patternfind;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;
import android.graphics.Paint;

public class GameBoard {

	private GameTile[][] theBoard;
	
	private int numberOfSquaresInWidth;
	private int numberOfSquaresInHeight;
	
	private ArrayList<GameTile> activeTiles;
	
	private ArrayList<GameTile> tileSequence;
	
	private float tileSize;
	
	private float screenWidth;
	private float screenHeight;
	
	private float topPosition;
	private float leftPosition;
	
	private int rotationSteps = 0;
	
	private GameTile dummyTile;
	
	private CanvasButton removeButton;
	
	public Pattern thePattern;
	
	private float[] pointArray;
	
	private TurnEvent lastTurnEvent;
	
	private static final int Animation_Wait_Time = 500;

		
	public GameBoard(int width,int height,float screenW, float screenH){
		if ((width<100) && (height <100)){
			theBoard = new GameTile[width][height];
			numberOfSquaresInWidth = width;
			numberOfSquaresInHeight = height;
		}
		activeTiles = new ArrayList<GameTile>();
		
		tileSequence = new ArrayList<GameTile>();
		
		dummyTile = new GameTile();
		dummyTile.setOccupied(false);
		
		screenWidth = screenW;
		screenHeight = screenH;
		
		topPosition = screenHeight/10;
		leftPosition = screenWidth/(2*(numberOfSquaresInWidth+1)); //1/2 of 1/10 the width
		tileSize = screenWidth/(numberOfSquaresInWidth + 1);
		
		removeButton = new CanvasButton();
		
		removeButton.setButtonLeft(screenWidth/3);
		removeButton.setButtonTop(screenHeight - (screenHeight/10));
		removeButton.setButtonHeight(screenHeight/11);
		removeButton.setButtonWidth(screenWidth - (2*screenWidth/3));
		
		removeButton.buttonPaint.setColor(Color.LTGRAY);
		
		removeButton.setTextString("REMOVE");
		removeButton.textPaint.setColor(Color.BLACK);
		removeButton.textPaint.setTextSize(removeButton.getButtonHeight()/2);
		removeButton.setTextLeft(removeButton.getButtonLeft()+(removeButton.getButtonWidth()/11));
		removeButton.setTextTop(removeButton.getButtonTop()+(2*removeButton.getButtonHeight()/3));
		
		thePattern = new Pattern(screenWidth,tileSize);
		
		pointArray = new float[0];
		
	}
	
	
	//these methods are called when the screen rotates, and also after pieces
	//are cleared to drop pieces down to the correct side
	public void handleLowPitch(){
		
		clearTileSequence();
		moveBlocksOneRowLowPitch();
		//this moves tiles down the array, from the bigger numbered indexes to the smaller ones
		
		/*
		if (rotationSteps < numberOfSquaresInHeight){
			moveBlocksOneRowLowPitch();
			rotationSteps++;
			return true;
		}else{
			rotationSteps = 0;
			return false;
		}
		*/
	}



	public void handleHighPitch(){
		
		clearTileSequence();
		moveBlocksOneRowHighPitch();
		//this moves tiles "up" the array, from the bigger numbered indexes to the smaller ones
		/*
		if (rotationSteps < numberOfSquaresInHeight){
			moveBlocksOneRowHighPitch();
			rotationSteps++;
			return true;
		}else{
			rotationSteps = 0;
			return false;
		}
		*/
	}
	
	public void handleLowRoll() {
		clearTileSequence();
		moveBlocksOneRowRightLowRoll();
		//this moves tiles "right" in the array
		/*
		if (rotationSteps < numberOfSquaresInWidth){
			moveBlocksOneRowHighPitch();
			rotationSteps++;
			return true;
		}else{
			rotationSteps = 0;
			return false;
		}
		*/
			
	}
	
	public void handleHighRoll(){
		
		clearTileSequence();
		moveBlocksOneRowLeftHighRoll();
		//this moves tiles "left" in the array
		
		/*
		if (rotationSteps < numberOfSquaresInWidth){
			moveBlocksOneRowLeftHighRoll();
			rotationSteps++;
			return true;
		}else{
			rotationSteps = 0;
			return false;
		}
		*/
	}
	
	
	private void clearTileSequence() {
		for (GameTile tile:tileSequence){
			tile.switchTouched();
		}
		tileSequence.clear();
	}


	public void moveBlocksOneRowLowPitch(){	
		//in this case every tile in theBoard[x][0] is ok		
		//this should move the squares all up one line	
		//we need to start with theBoard[1][x]
		//for each tile in that row check and see if the tile below it is occupied
		for (int heightIndex=(numberOfSquaresInHeight-2);heightIndex!=-1;heightIndex--){
			for (int widthIndex=0;widthIndex<numberOfSquaresInWidth;widthIndex++){
				
				//if there is a tile & a tile above it
				if ((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex][heightIndex+1].isOccupied())){   //coordinates of tile below it
					
					
					//if not occupied, move the tile in the space above it to the current tile
					
					//Pop the game Tile out of the ArrayList
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					activeTiles.remove(currentTile);
					
					//Change its position parameters
					currentTile.setLocationTop(currentTile.getLocationTop()+tileSize);
					
					//set the board location of the above space to that tile
					theBoard[widthIndex][heightIndex+1] = currentTile;
					
					//add it back to the array list
					activeTiles.add(currentTile);
					
					//set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
		
		
		
		
	}
	
	public void moveBlocksOneRowHighPitch(){		
		//in this case every tile in theBoard[x][0] is ok		
		//this should move the squares all up one line	
		//we need to start with theBoard[1][x]
		//for each tile in that row check and see if the tile above it is occupied
		for (int heightIndex=1;heightIndex<numberOfSquaresInHeight;heightIndex++){
			for (int widthIndex=0;widthIndex<numberOfSquaresInWidth;widthIndex++){
				
				//if there is a tile & a tile above it
				if ((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex][heightIndex-1].isOccupied())){   //coordinates of tile above it
					
					//if not occupied, move the tile in the space above it to the current tile
					
					//Pop the game Tile out of the ArrayList
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					activeTiles.remove(currentTile);
					
					//Change its position parameters
					currentTile.setLocationTop(currentTile.getLocationTop()-tileSize);
					
					//set the board location of the above space to that tile
					theBoard[widthIndex][heightIndex-1] = currentTile;
					
					//add it back to the array list
					activeTiles.add(currentTile);
					
					//set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}
	
	
	
	public void moveBlocksOneRowRightLowRoll(){		

		for (int widthIndex=(numberOfSquaresInWidth-2);widthIndex!=-1;widthIndex--){
			for (int heightIndex=0;heightIndex!=numberOfSquaresInHeight;heightIndex++){
				
				//if there is a tile & a tile to the right of it
				if ((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex+1][heightIndex].isOccupied())){   //coordinates of tile right of it
					
					//if not occupied, move the tile in the space above it to the current tile
					
					//Pop the game Tile out of the ArrayList
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					activeTiles.remove(currentTile);
					
					//Change its position parameters
					currentTile.setLocationLeft(currentTile.getLocationLeft()+tileSize);
					
					//set the board location to the right of the above space to that tile
					theBoard[widthIndex+1][heightIndex] = currentTile;
					
					//add it back to the array list
					activeTiles.add(currentTile);
					
					//set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}
		
	public void moveBlocksOneRowLeftHighRoll(){		

		for (int widthIndex=1;widthIndex!=numberOfSquaresInWidth;widthIndex++){
			for (int heightIndex=0;heightIndex!=numberOfSquaresInHeight;heightIndex++){
				
				//if there is a tile & a tile to the right of it
				if ((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex-1][heightIndex].isOccupied())){   //coordinates of tile left of it
					
					//if not occupied, move the tile in the space above it to the current tile
					
					//Pop the game Tile out of the ArrayList
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					activeTiles.remove(currentTile);
					
					//Change its position parameters
					currentTile.setLocationLeft(currentTile.getLocationLeft()-tileSize);
					
					//set the board location to the right of the above space to that tile
					theBoard[widthIndex-1][heightIndex] = currentTile;
					
					//add it back to the array list
					activeTiles.add(currentTile);
					
					//set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}
	
	public ArrayList<GameTile> getActiveTiles(){
		return activeTiles;
	}
	
	public ArrayList<GameTile> getTileSequence(){
		return tileSequence;
	}

	public void populateBoard(int numberOfTiles){
		
		initializeEmptyBoard();
		
		if (numberOfTiles <= (numberOfSquaresInWidth * numberOfSquaresInHeight)){
			
			boolean tileCollisionOccured = true;
			
			for (int populateIndex=0; populateIndex != numberOfTiles ; populateIndex++){
				
				do{
					//get a random location on the board randWidth, randHeight
					Random randGen = new Random();
					int testWidth = randGen.nextInt(numberOfSquaresInWidth);
					int testHeight = randGen.nextInt(numberOfSquaresInHeight);
				
					//see if this position is occupied
					if (!theBoard[testWidth][testHeight].isOccupied()){
					//if not, set that tile to a newly created tile with random color
						GameTile currentTile = new GameTile();
						currentTile.setOccupied(true);
					
						currentTile.setLocationTop(topPosition + (testHeight*tileSize));
						currentTile.setLocationLeft(leftPosition + (testWidth*tileSize));
						
						int paintColor = randGen.nextInt(3);
						
						if (paintColor == 0){
							currentTile.setPaintColor(Color.BLUE);
						}
						if (paintColor == 1){
							currentTile.setPaintColor(Color.RED);
						}
						if (paintColor == 2){
							currentTile.setPaintColor(Color.GREEN);
						}
						
						theBoard[testWidth][testHeight] = currentTile;
						
						activeTiles.add(currentTile);
						
						//let the loop know we didn't have a collison so we can exit
						tileCollisionOccured = false;
					}
				}while (tileCollisionOccured);
				tileCollisionOccured = true;	
			} 
				
				
				
				//if it is occupied, generate another random number
		}
	}
	
	private void initializeEmptyBoard(){
		
		//set all the tiles to a tile that always returns not occupied
		for (int widthIndex=0;widthIndex!=numberOfSquaresInWidth;widthIndex++){
			for(int heightIndex=0;heightIndex!=numberOfSquaresInHeight;heightIndex++){
				theBoard[widthIndex][heightIndex] = dummyTile;
			}
		}
	}
	
	public float getTileSize() {
		return tileSize;
	}


	public void handleTouch(float x, float y) {
		//if touch at x & y hits a tile
		
		//check if touch is outside of board
		
		if (removeButton.isPointInsideButton(x, y)){
			handleRemoveButtonPress();
		}
		if ((x<leftPosition)||(x>(leftPosition+(numberOfSquaresInWidth*tileSize))||
			(y<topPosition)||(y>(topPosition+(numberOfSquaresInHeight*tileSize))))){
			
			//do nothing if touch is outside gameboard
			
		}else if (theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)].isOccupied()){
			
			//check if the touched squared is either the first in the sequence 
			//or the adjacent to the last touched square
			//or removing the last active tile
			
			updateValidMove(x,y);
		}
		
	}




	private void handleRemoveButtonPress() {
		
		if (checkPattern()){
			int tileSequenceLength = tileSequence.size();
			for (GameTile tile:tileSequence){
				activeTiles.remove(theBoard[turnXToWidhtIndex(tile.getLocationLeft())][turnYToHeightIndex(tile.getLocationTop())]);
				theBoard[turnXToWidhtIndex(tile.getLocationLeft())][turnYToHeightIndex(tile.getLocationTop())] = dummyTile;
			}
			clearTileSequence();
			//could put something here for handling last gravity
			//taking it out for the rotate game
			/*
			switch (lastTurnEvent){
			case LOW_PITCH:
				for (int moveIndex=0;moveIndex!=tileSequenceLength;moveIndex++){
					moveBlocksOneRowLowPitch();
				}
				break;
			
			case HIGH_PITCH:
				for (int moveIndex=0;moveIndex!=tileSequenceLength;moveIndex++){
					moveBlocksOneRowHighPitch();
				}
				break;

			case LOW_ROLL:
				for (int moveIndex=0;moveIndex!=tileSequenceLength;moveIndex++){
					moveBlocksOneRowRightLowRoll();
				}
				break;
				
			case HIGH_ROLL:
				for (int moveIndex=0;moveIndex!=tileSequenceLength;moveIndex++){
					moveBlocksOneRowLeftHighRoll();
				}
				break;
			}
			*/
			
		} else {
			clearTileSequence();
		}
	}


	private boolean checkPattern() {
		
		ArrayList<GameTile> thePatternArray = thePattern.getTilePattern();
		int checkIndex = 0;
		boolean longEnough = false;
		for (GameTile sequenceTile:tileSequence){
			
			//keep re-circling through the check index
			if (checkIndex==thePatternArray.size()){
				checkIndex = 0;
			}
			
			// if the tile colors don't match
			
			Paint tempPaint = sequenceTile.getPaint();
			tempPaint.setAlpha(255);
			if (tempPaint.getColor()!=thePatternArray.get(checkIndex).getPaintColor()){
				return false;
			}
			checkIndex++;
			//make sure the pattern is at least 3 long
			if (checkIndex==thePatternArray.size()&&(longEnough==false)){
				longEnough = true;
			}
		}
		if (longEnough)
			return true;
		else
			return false;
		
	}


	private void updateValidMove(float x, float y) {
		
		//if it is the first touched tile
		if (tileSequence.isEmpty()){
			tileSequence.add(theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)]);
			theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)].switchTouched();
			return;
			
		//if you are clearing the last touched tile	
		} else if (tileSequence.get(tileSequence.size()-1)== 
				theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)]){
			tileSequence.remove(tileSequence.get(tileSequence.size()-1));
			theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)].switchTouched();
			
			if (tileSequence.size()>1){
				adjustPointArray();
			}
			
			return;
		} 
		
		//first check if the touched tile is not in the tileSequence
		if (!tileSequence.contains(theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)])){
			//then check all four sides of the most recent tile and if it is one of them, then add it
			GameTile mostRecentTile = tileSequence.get(tileSequence.size()-1);
			GameTile touchedTile = theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)];
			
			//touchedTile is to the right of mostRecentTile
			if ((mostRecentTile.getLocationLeft()+tileSize)==touchedTile.getLocationLeft()&&
					(mostRecentTile.getLocationTop()==touchedTile.getLocationTop())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				return;
			}
			
			//touchedTile is to the left of mostRecentTile
			if ((mostRecentTile.getLocationLeft()-tileSize)==touchedTile.getLocationLeft()&&
					(mostRecentTile.getLocationTop()==touchedTile.getLocationTop())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				return;
			}
			
			//touchedTile is below mostRecentTile
			if ((mostRecentTile.getLocationTop()+tileSize)==touchedTile.getLocationTop()&&
					(mostRecentTile.getLocationLeft()==touchedTile.getLocationLeft())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				return;
			}
			
			//touchedTile is above mostRecentTile
			if ((mostRecentTile.getLocationTop()-tileSize)==touchedTile.getLocationTop()&&
					(mostRecentTile.getLocationLeft()==touchedTile.getLocationLeft())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidhtIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				return;
			}			
		}
		
		
	}


	private int turnYToHeightIndex(float y) {
		return (int)((y - topPosition)/tileSize);
	}


	private int turnXToWidhtIndex(float x) {
		return (int)((x - leftPosition)/tileSize);
	}
	
	
	private void adjustPointArray(){
		pointArray = new float[4*tileSequence.size()];
		
		for (int tileNumber=0;tileNumber!=tileSequence.size()-1;tileNumber++){
			pointArray[(4*tileNumber)]   = (tileSequence.get(tileNumber).getLocationLeft()) + (tileSize/2); 
			pointArray[(4*tileNumber)+1] = (tileSequence.get(tileNumber).getLocationTop()) + (tileSize/2);
			pointArray[(4*tileNumber)+2] = (tileSequence.get(tileNumber+1).getLocationLeft()) + (tileSize/2); 
			pointArray[(4*tileNumber)+3] = (tileSequence.get(tileNumber+1).getLocationTop()) + (tileSize/2);
		}
	}
	
	
	public CanvasButton getRemoveButton(){
		return removeButton;
	}


	public float[] getPointArray() {
		return pointArray;
	}

	public void setLastTurnEvent(TurnEvent lastTurnEvent) {
		this.lastTurnEvent = lastTurnEvent;
	}
	
}
