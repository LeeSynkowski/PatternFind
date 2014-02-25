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

	private GameTile[][] theBoard;
	
	private int numberOfSquaresInWidth;
	private int numberOfSquaresInHeight;
	
	private ArrayList<GameTile> activeTiles;
	private ArrayList<GameTile> movingTiles;
	
	private ArrayList<GameTile> tileSequence;
	
	private ArrayList<GameTile> rocketTiles;
	int finalRocketX = 0;
	int finalRocketY = 0;
	
	private float tileSize;
	
	private float screenWidth;
	private float screenHeight;
	
	private float topPosition;
	private float leftPosition;
	
	private GameTile dummyTile;
	
	private CanvasButton removeButton;
	
	public Pattern thePattern;
	
	private float[] pointArray;
	
	private TurnEvent lastTurnEvent;
	
	private boolean animationComplete = true;
	
	public boolean isAnimationComplete() {
		return animationComplete;
	}

	private float movementIncriment;

	private int AnimationSteps = 10;
	
	private int animationFrame = 0;
	
	private GameTile lastTile = new GameTile();
	
	private boolean moving = false;
	
	private boolean fingerDown = false;
	private boolean firstAction = true;
	
	private boolean tilesInFinalState = false;
	private int tileMoveCounter = 0;

	public GameBoard(int width,int height,float screenW, float screenH){
		if ((width<100) && (height <100)){
			theBoard = new GameTile[width][height];
			numberOfSquaresInWidth = width;
			numberOfSquaresInHeight = height;
		}
		activeTiles = new ArrayList<GameTile>();
		
		tileSequence = new ArrayList<GameTile>();
		
		movingTiles = new ArrayList<GameTile>();
		
		rocketTiles = new ArrayList<GameTile>();
		
		dummyTile = new GameTile();
		dummyTile.setOccupied(false);
		lastTile = null;
		
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

	//GameBoard should have a constructor just based off the level
	public GameBoard(int level,float screenW, float screenH,Context context) throws XmlPullParserException, IOException{

		activeTiles = new ArrayList<GameTile>();
		
		tileSequence = new ArrayList<GameTile>();
		
		movingTiles = new ArrayList<GameTile>();
		
		rocketTiles = new ArrayList<GameTile>();
		
		dummyTile = new GameTile();
		dummyTile.setOccupied(false);
		lastTile = null;
		
		screenWidth = screenW;
		screenHeight = screenH;
		
		//need to get # of squares in width & height and use it to set the params of the
		//screen size
		Level currentLevel = new Level(level,context);
		
		numberOfSquaresInWidth = currentLevel.getNumberOfSquaresInWidth();
		numberOfSquaresInHeight = currentLevel.getNumberOfSquaresInHeight();
		
		theBoard = currentLevel.getGameBoard();
		
		topPosition = screenHeight/10;
		leftPosition = screenWidth/(2*(numberOfSquaresInWidth+1)); //1/2 of 1/10 the width
		tileSize = screenWidth/(numberOfSquaresInWidth + 1);
		
		thePattern = new Pattern(screenWidth,tileSize);
		
		pointArray = new float[0];	
	}
	
	
	
	//these methods are called when the screen rotates, and also after pieces
	//are cleared to drop pieces down to the correct side
	public void handleLowPitch(float pitchAngle){
		
		//clearTileSequence();
		adjustArrayOneRowLowPitch();
		animationComplete = false;
		setAnimationSpeed(pitchAngle);
		movementIncriment = tileSize/AnimationSteps;
	}

	public void handleHighPitch(float pitchAngle){
		
		//clearTileSequence();
		adjustArrayOneRowHighPitch();
		animationComplete = false;
		setAnimationSpeed(pitchAngle);
		movementIncriment = tileSize/AnimationSteps;
	}
	
	public void handleLowRoll(float rollAngle) {
		//clearTileSequence();
		adjustArrayOneRowRightLowRoll();
		animationComplete = false;
		setAnimationSpeed(rollAngle);
		movementIncriment = tileSize/AnimationSteps;	
	}
	
	public void handleHighRoll(float rollAngle){
		
		//clearTileSequence();
		adjustArrayOneRowLeftHighRoll();
		animationComplete = false;
		setAnimationSpeed(rollAngle);
		movementIncriment = tileSize/AnimationSteps;
	}

	//trying to make an an animated version
	
	public void adjustArrayOneRowLowPitch(){	
		//in this case every tile in theBoard[x][0] is ok		
		//this should move the squares all up one line	
		//we need to start with theBoard[1][x]
		//for each tile in that row check and see if the tile below it is occupied	
		for (int heightIndex=(numberOfSquaresInHeight-2);heightIndex!=-1;heightIndex--){
			for (int widthIndex=0;widthIndex<numberOfSquaresInWidth;widthIndex++){
				
				//if there is a tile & not tile above it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex][heightIndex+1].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   //coordinates of tile below it
									
					//if not occupied, move the tile in the space above it to the current tile
					
					//Get gameTile from the current spot on the board 
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					
					//remove the tile from the list of activeTiles
					activeTiles.remove(currentTile);
					
					//set the board location of the above space to that tile
					theBoard[widthIndex][heightIndex+1] = currentTile;
					
					//
					//if its in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex][heightIndex+1]);
						adjustPointArray();
					}
					
					
					//add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
					//set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}		
	}
	
	public void adjustArrayOneRowHighPitch(){		
		//in this case every tile in theBoard[x][0] is ok		
		//this should move the squares all up one line	
		//we need to start with theBoard[1][x]
		//for each tile in that row check and see if the tile above it is occupied
		for (int heightIndex=1;heightIndex<numberOfSquaresInHeight;heightIndex++){
			for (int widthIndex=0;widthIndex<numberOfSquaresInWidth;widthIndex++){
				
				//if there is a tile & a tile above it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex][heightIndex-1].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   //coordinates of tile above it
					
					//if not occupied, move the tile in the space above it to the current tile
					
					//Get gameTile from the current spot on the board
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					
					//remove the tile from the list of active tiles
					activeTiles.remove(currentTile);
					
					//set the board location of the above space to that tile
					theBoard[widthIndex][heightIndex-1] = currentTile;

					//
					//if its in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex][heightIndex-1]);
						adjustPointArray();
					}
					
					//add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
					//set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}
	
	public void adjustArrayOneRowRightLowRoll(){		

		for (int widthIndex=(numberOfSquaresInWidth-2);widthIndex!=-1;widthIndex--){
			for (int heightIndex=0;heightIndex!=numberOfSquaresInHeight;heightIndex++){
				
				//if there is a tile & a tile to the right of it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex+1][heightIndex].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   //coordinates of tile right of it
					
					//if not occupied, move the tile in the space above it to the current tile
					
					//Get gameTile from the current spot on the board
					GameTile currentTile = theBoard[widthIndex][heightIndex];
					
					//remove the tile from the list of active tiles
					activeTiles.remove(currentTile);
					
					//set the board location to the right of the above space to that tile
					theBoard[widthIndex+1][heightIndex] = currentTile;
					
					//
					//if its in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex+1][heightIndex]);
						adjustPointArray();
					}
					
					//add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
					//set the current tile to a dummyTile notOccupied tile
					theBoard[widthIndex][heightIndex] = dummyTile;
				}
			}
		}
	}
	
	public void adjustArrayOneRowLeftHighRoll(){		

		for (int widthIndex=1;widthIndex!=numberOfSquaresInWidth;widthIndex++){
			for (int heightIndex=0;heightIndex!=numberOfSquaresInHeight;heightIndex++){
				
				//if there is a tile & a tile to the right of it
				if (((theBoard[widthIndex][heightIndex].isOccupied())&&  //coordinates of current tile
						(!theBoard[widthIndex-1][heightIndex].isOccupied()))&&
						theBoard[widthIndex][heightIndex].getTileType()!=TileType.STATIONARY_TILE){   //coordinates of tile left of it
					
					//if not occupied, move the tile in the space above it to the current tile
					
					//Get gameTile from the current spot on the board
					GameTile currentTile = theBoard[widthIndex][heightIndex];
										
					//remove the tile from the list of active tiles
					activeTiles.remove(currentTile);
					
					//set the board location to the right of the above space to that tile
					theBoard[widthIndex-1][heightIndex] = currentTile;

					//
					//if its in the tile sequence, remove and update
					if (tileSequence.contains(currentTile)){
						int tileIndex = tileSequence.indexOf(currentTile);
						tileSequence.set(tileIndex, theBoard[widthIndex-1][heightIndex]);
						adjustPointArray();
					}
					
					//add the tile to the list of moving tiles
					movingTiles.add(currentTile);
					
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
		
		//Experimenting with putting stationary tiles in an empty board
		GameTile currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + (5*tileSize));
		currentTile.setLocationLeft(leftPosition + (5*tileSize));
		currentTile.makeStationary();
		theBoard[5][5] = currentTile;
		
		activeTiles.add(currentTile);
		
		//Experimenting with putting bomb tiles in an empty board
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + (0*tileSize));
		currentTile.setLocationLeft(leftPosition + (0*tileSize));
		currentTile.makeBomb();
		theBoard[0][0] = currentTile;
				
		activeTiles.add(currentTile);
		
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + ((numberOfSquaresInHeight-1)*tileSize));
		currentTile.setLocationLeft(leftPosition + ((numberOfSquaresInWidth-1)*tileSize));
		currentTile.makeBomb();
		theBoard[numberOfSquaresInWidth-1][(numberOfSquaresInHeight-1)] = currentTile;
				
		activeTiles.add(currentTile);
		
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + (0)*tileSize);
		currentTile.setLocationLeft(leftPosition + ((numberOfSquaresInWidth-1)*tileSize));
		currentTile.makeBomb();
		theBoard[numberOfSquaresInWidth-1][0] = currentTile;
				
		activeTiles.add(currentTile);
		
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + ((numberOfSquaresInHeight-1)*tileSize));
		currentTile.setLocationLeft(leftPosition + ((0)*tileSize));
		currentTile.makeBomb();
		theBoard[0][(numberOfSquaresInHeight-1)] = currentTile;
				
		activeTiles.add(currentTile);
		
		
		//Experimenting with adding rocket tiles
		
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + (2*tileSize));
		currentTile.setLocationLeft(leftPosition + (2*tileSize));
		currentTile.makeRocket(Orientation.UP);
		theBoard[2][2] = currentTile;
				
		activeTiles.add(currentTile);
		
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + ((numberOfSquaresInHeight-2)*tileSize));
		currentTile.setLocationLeft(leftPosition + ((numberOfSquaresInWidth-2)*tileSize));
		currentTile.makeRocket(Orientation.DOWN);
		theBoard[numberOfSquaresInWidth-2][(numberOfSquaresInHeight-2)] = currentTile;
				
		activeTiles.add(currentTile);
		
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + (2)*tileSize);
		currentTile.setLocationLeft(leftPosition + ((numberOfSquaresInWidth-2)*tileSize));
		currentTile.makeRocket(Orientation.LEFT);
		theBoard[numberOfSquaresInWidth-2][2] = currentTile;
				
		activeTiles.add(currentTile);
		
		currentTile = new GameTile();		
		currentTile.setLocationTop(topPosition + ((numberOfSquaresInHeight-2)*tileSize));
		currentTile.setLocationLeft(leftPosition + ((2)*tileSize));
		currentTile.makeRocket(Orientation.RIGHT);
		theBoard[2][(numberOfSquaresInHeight-2)] = currentTile;
				
		activeTiles.add(currentTile);
		
		if (numberOfTiles <= (numberOfSquaresInWidth * numberOfSquaresInHeight)){
			
			addNewPieces(numberOfTiles);
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


	public TouchEvent handleTouch(float x, float y) {
		//if touch at x & y hits a tile
		
		//check if touch is outside of board
		
		if (removeButton.isPointInsideButton(x, y)){
			if (handleRemoveButtonPress()){
				//this checks if correct and automatically removes them
				return TouchEvent.REMOVE_BUTTON_CORRECT;
			} else return TouchEvent.REMOVE_BUTTON_INCORRECT;
		}else if ((x<leftPosition)||(x>(leftPosition+(numberOfSquaresInWidth*tileSize))||
			(y<topPosition)||(y>(topPosition+(numberOfSquaresInHeight*tileSize))))){
			
			//do nothing if touch is outside gameboard
			return TouchEvent.NOTHING;
			
		}else if ((theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].isOccupied())&&(!movingTiles.contains(theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]))){
			
				if (theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].getTileType()==TileType.GAME_TILE){
					
					if (tileSequence.size()>0){
						lastTile = tileSequence.get(tileSequence.size()-1);
						
					} else{
						lastTile = null;
					}
					//if its moving and in the same tile as the last touched tile we DONT 
					//throw it to update valid move
					//so make a thing that checks for these conditions and returns nothing
					if ((moving)&&(lastTile == theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)])){
						return TouchEvent.NOTHING;
					}
					//if there are three or more tiles in the chain and user touches the first tile
					if ((tileSequence.size()>=3)){
						if (theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]==tileSequence.get(0)){
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

	private void bombTileSelected(float x, float y) {
		// remove the tiles surrounding the bomb tile from the active tile list 
		// and set references to them on the board with the dummy tile
		int bombX = turnXToWidthIndex(x);
		int bombY = turnYToHeightIndex(y);
		
		activeTiles.remove(theBoard[bombX][bombY]); //remove the bomb
		theBoard[bombX][bombY] = dummyTile;
		
		if ((bombX == 0) && (bombY == 0)){ //top left
			activeTiles.remove(theBoard[bombX+1][bombY]); //remove the one to the right
			theBoard[bombX+1][bombY] = dummyTile;
			activeTiles.remove(theBoard[bombX][bombY+1]); //remove the one below
			theBoard[bombX][bombY+1] = dummyTile;
			activeTiles.remove(theBoard[bombX+1][bombY+1]); //remove the one right below
			theBoard[bombX+1][bombY+1] = dummyTile;
			
		}	else if ((bombX == numberOfSquaresInWidth-1)&&(bombY == numberOfSquaresInHeight-1)){ //bottom right
						activeTiles.remove(theBoard[bombX][bombY-1]); //remove the one above
						theBoard[bombX][bombY-1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY]); //remove the one to the left
						theBoard[bombX-1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY-1]); //remove the one to the left above
						theBoard[bombX-1][bombY-1] = dummyTile;
						
		}	else if ((bombX == 0)&&(bombY == numberOfSquaresInHeight-1)){ //bottom left
						activeTiles.remove(theBoard[bombX+1][bombY]); //remove the one to the right
						theBoard[bombX+1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX][bombY-1]); //remove the one above
						theBoard[bombX][bombY-1] = dummyTile;
						activeTiles.remove(theBoard[bombX+1][bombY-1]); //remove the one to the right above
						theBoard[bombX+1][bombY-1] = dummyTile;
						
		}   else if ((bombX == numberOfSquaresInWidth-1) && (bombY == 0)){ //top right
						activeTiles.remove(theBoard[bombX-1][bombY]); //remove the one to the left
						theBoard[bombX-1][bombY] = dummyTile;
						activeTiles.remove(theBoard[bombX][bombY+1]); //remove the one below
						theBoard[bombX][bombY+1] = dummyTile;
						activeTiles.remove(theBoard[bombX-1][bombY+1]); //remove the one left below
						theBoard[bombX-1][bombY+1] = dummyTile;
			
		}   else if (bombX == 0){ //left edge
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
			
		}	else if (bombX == numberOfSquaresInWidth-1){ //right edge
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
					
			
		}	else if (bombY == 0){  //top edge
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
						
		}	else if (bombY == numberOfSquaresInHeight-1){ //bottom edge
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
						
						
		}	else { //everywhere else on the board
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

	private void rocketTileSelected(float x, float y,Orientation orientation){
		//clear out any previous Tiles from the list
		rocketTiles.clear();
		
		//find rockets final co-ordinates
		int rocketX = turnXToWidthIndex(x);
		int rocketY = turnYToHeightIndex(y);
		
		//the rocket tile is the 0 element in the array list, and it is removed from the active tile array list
		rocketTiles.add(theBoard[rocketX][rocketY]);
		activeTiles.remove(theBoard[rocketX][rocketY]);
		theBoard[rocketX][rocketY] = dummyTile;
		
		//create an array list of all tiles removed by the rocket, remove these tiles from the list of active tiles
		if (orientation == Orientation.UP){
			int yIndex = rocketY-1;
			while ((yIndex>=0)&&(theBoard[rocketX][yIndex].getTileType()!=TileType.STATIONARY_TILE)){
				//if there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
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
				//if there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
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
				//if there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
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
				//if there is a tile in the spot, remove it from the board and the active tile list, and then add it to the rocket tile list
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
	
	private boolean handleRemoveButtonPress() {
		
		if (checkPattern()){
			//int tileSequenceLength = tileSequence.size();
			for (GameTile tile:tileSequence){
				activeTiles.remove(theBoard[turnXToWidthIndex(tile.getLocationLeft())][turnYToHeightIndex(tile.getLocationTop())]);
				theBoard[turnXToWidthIndex(tile.getLocationLeft())][turnYToHeightIndex(tile.getLocationTop())] = dummyTile;
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


	private boolean updateValidMove(float x, float y) {

		//these should return when they meet the given condition, and perform the given operation
		//other wise keep checking
		
		
		//if it is the first touched tile
		if ((tileSequence.isEmpty())&&(firstAction)){
			tileSequence.add(theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]);
			theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
			firstAction = false;
			return true;
		}
		
		//if you are clearing the first tile
		if((tileSequence.size()==1)&&(firstAction)){
				//if the touched tile matches the tile in the sequence
				if (tileSequence.get(0)==theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]){
					tileSequence.remove(tileSequence.get(0));
					theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
					firstAction = false;
					return true;
				}
		}		

		// if you are clearing the second or more tile via tap	
		if ((tileSequence.size()>1)&&(firstAction)) {
			//if the last tile in the tile sequence is the same one that is touched
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
		
		// or if you are dragging back into the last correct square
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
		
		
		//first check if the touched tile is not in the tileSequence
		if ((!tileSequence.contains(theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)]))&&(tileSequence.size()>0)&&(moving==true||firstAction==true)){
			//then check all four sides of the most recent tile and if it is one of them, then add it
			GameTile mostRecentTile = tileSequence.get(tileSequence.size()-1);
			GameTile touchedTile = theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)];
			
			//touchedTile is to the right of mostRecentTile
			if ((mostRecentTile.getLocationLeft()+tileSize)==touchedTile.getLocationLeft()&&
					(mostRecentTile.getLocationTop()==touchedTile.getLocationTop())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				firstAction = false;
				return true;
			}
			
			//touchedTile is to the left of mostRecentTile
			if ((mostRecentTile.getLocationLeft()-tileSize)==touchedTile.getLocationLeft()&&
					(mostRecentTile.getLocationTop()==touchedTile.getLocationTop())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				firstAction = false;
				return true;
			}
			
			//touchedTile is below mostRecentTile
			if ((mostRecentTile.getLocationTop()+tileSize)==touchedTile.getLocationTop()&&
					(mostRecentTile.getLocationLeft()==touchedTile.getLocationLeft())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				firstAction = false;
				return true;
			}
			
			//touchedTile is above mostRecentTile
			if ((mostRecentTile.getLocationTop()-tileSize)==touchedTile.getLocationTop()&&
					(mostRecentTile.getLocationLeft()==touchedTile.getLocationLeft())){
				tileSequence.add(touchedTile);
				theBoard[turnXToWidthIndex(x)][turnYToHeightIndex(y)].switchTouched();
				
				adjustPointArray();
				firstAction = false;
				return true;
			}			
		}
		
		return false;
	}


	public int turnYToHeightIndex(float y) {
		return (int)((y - topPosition)/tileSize);
	}


	public int turnXToWidthIndex(float x) {
		return (int)((x - leftPosition)/tileSize);
	}
	
	public boolean isTileSequenceContinuous(){
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
	
	
	
	public void adjustPointArray(){
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
	
	
	public CanvasButton getRemoveButton(){
		return removeButton;
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

	public void animationStepOccured() {
		animationFrame++;
		//the animation is over
		if (animationFrame == AnimationSteps){
			animationFrame = 0;
			animationComplete = true;
			
			//merge moving tiles with active tiles
			for (GameTile tile:movingTiles){
				activeTiles.add(tile);
			}
			
			//erase moving tiles
			movingTiles.clear();
		}	
		
	}
	
	private void setAnimationSpeed(float Angle) {
		
	
		if (Math.abs(Angle)>=20 && Math.abs(Angle) < 30){
			AnimationSteps = 10;
		}	
		if (Math.abs(Angle)>=30 && Math.abs(Angle) < 60){
			AnimationSteps = 5;
		}
		if (Math.abs(Angle)>=60){
			AnimationSteps = 1;
		}
		
	}


	public int determinePossiblePoints(int size) {
		if (size<3){
			return 0;
		} else if (size==3){
			return 100;
		}
		int value = size-3;
		return (int) (100 * Math.pow(2, value));
	}





	public boolean isFull() {
		if (numberOfSquaresInHeight*numberOfSquaresInWidth == activeTiles.size()){		
					return true;
		}
		return false;
	}

	public void addNewPieces(int numberOfPieces) {
		boolean tileCollisionOccured = true;
		
		for (int populateIndex=0; populateIndex != numberOfPieces ; populateIndex++){
			
			do{
				//get a random location on the board randWidth, randHeight
				Random randGen = new Random();
				randGen.setSeed(SystemClock.elapsedRealtime());
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
		
	}

	public int numberOfSpotsRemaining() {
		return ((numberOfSquaresInWidth * numberOfSquaresInHeight) - activeTiles.size());
	}
	
	public float getTopPosition(){
		return topPosition;
	};
	
	public float getLeftPosition(){
		return leftPosition;
	}

	public ArrayList<GameTile> getRocketTiles() {
		return rocketTiles;
	}

	public boolean isRocketDone(int i, int j) {
		if ((i == finalRocketX)&&(j == finalRocketY))
			return true;
		return false;
	}
	
	public void setAnimationComplete(boolean ac){
		this.animationComplete = ac;
	}
	
	public GameTile getTile(int x,int y){
		if (x>=0 && y>=0 && x<= numberOfSquaresInWidth-1 && y<= numberOfSquaresInHeight-1){
			return theBoard[x][y];
		} else
			return null;
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

	public void setFingerDown(boolean fingerDown) {

		this.fingerDown = fingerDown;
		//if you lift the finger you will be in a first action state
		if (this.fingerDown == false){
			firstAction = true;
		}
	}
	
	public boolean isTilesInFinalState() {
		return tilesInFinalState;
	}

	public void setTilesInFinalState(boolean blocksInFinalState) {
		this.tilesInFinalState = blocksInFinalState;
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
	
	public void clearTileSequence() {
		for (GameTile tile:tileSequence){
			tile.switchTouched();
		}
		tileSequence.clear();
	}
	
}
