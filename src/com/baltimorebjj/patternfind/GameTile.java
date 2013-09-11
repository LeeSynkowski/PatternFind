package com.baltimorebjj.patternfind;

import android.graphics.Paint;

public class GameTile {

	private float locationTop;
	private float locationLeft;
	
	private Paint tilePaint = new Paint();
	
	private boolean occupied;
	
	private int tileSize;
	
	private boolean touched = false;
	
	
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

	
	public void switchTouched(){
		touched = !touched;
		if (touched){
			tilePaint.setAlpha(100);
		} else {
			tilePaint.setAlpha(255);
		}
	}
}
