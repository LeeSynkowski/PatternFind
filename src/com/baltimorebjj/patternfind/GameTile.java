/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: GameTile.java
*     Creation Date: 8/26/2013
*            Author: Lee Synkowski
*  
*       This is class is the GameTile data type that represents the
*       playable pieces on the GameBoard.  It includes information
*       about the type of the game tile, its status, and location.
*  
* 
*	Code Review:	Code reviewed 3/24/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import android.graphics.Paint;

public class GameTile {

	//Key graphical drawing for drawing objects
	private float locationTop;
	private float locationLeft;
	
	//Holds the tiles color and alpha state
	private Paint tilePaint = new Paint();
	
	//If the tile is in play on the gameboard
	private boolean occupied;
	
	private int tileSize;
	
	//Used to track tiles that are touched and part of the tile sequence
	private boolean touched = false;
	
	//Tile type i.e. Game, Rocket, Bomb, or Stationary
	private TileType tileType = TileType.GAME_TILE;
	
	//Orientation of a Rocket tile
	private Orientation orientation = null;
	
	public GameTile(){};
	
	//Copy constructor to facilitate object duplication
	//needed in multi-threaded code
	public GameTile(GameTile anotherTile){
		this.locationTop = anotherTile.locationTop;
		this.locationLeft = anotherTile.locationLeft;
		this.tilePaint = anotherTile.tilePaint;
		this.occupied = anotherTile.occupied;
		this.tileSize = anotherTile.tileSize;
		this.touched = anotherTile.touched;
		this.tileType = anotherTile.tileType;
		this.orientation = anotherTile.orientation;
	}
	
	//Used to make a tile into a Stationary Tile
	public void makeStationary(){
		this.tileType = TileType.STATIONARY_TILE;
		this.setOccupied(true);
	}

	//Used to make a tile into a Bomb Tile
	public void makeBomb(){
		this.tileType = TileType.BOMB_TILE;
		this.setOccupied(true);
	}
	
	//Used to make a tile into a Rocket Tile
	public void makeRocket(Orientation orientation){
		this.tileType = TileType.ROCKET_TILE;
		this.setOccupied(true);
		this.orientation = orientation;
		
	}
	
	//Quickly switch touched state
	public void switchTouched(){
		touched = !touched;
	}
	
	public TileType getTileType() {
		return tileType;
	}

	public void setTileType(TileType tileType) {
		this.tileType = tileType;
	}

	public void setOccupied(boolean occupied){
		this.occupied = occupied;
	}
	
	public boolean isOccupied(){
		return occupied;
	}
	
	public float getLocationTop() {
		return locationTop;
	}
	public void setLocationTop(float locationTop) {
		this.locationTop = locationTop;
	}
	public float getLocationLeft() {
		return locationLeft;
	}
	public void setLocationLeft(float locationLeft) {
		this.locationLeft = locationLeft;
	}
	
	public void setPaintColor(int color){
		tilePaint.setColor(color);
	}
	
	public Paint getPaint(){
		return tilePaint;
	}

	public int getPaintColor(){
		return tilePaint.getColor();
	}
	
	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public boolean isTouched() {
		return touched;
	}
	
	public void setOrientation(Orientation orientation){
		this.orientation = orientation;
	}
	
	public Orientation getOrientation(){
		return orientation;
	}
}
