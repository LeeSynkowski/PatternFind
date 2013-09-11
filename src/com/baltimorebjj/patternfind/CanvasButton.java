package com.baltimorebjj.patternfind;

import android.graphics.Paint;


//this class is used for the easy creation of rectangular buttons on a canvas
//that have built in functionality to detect wether a touch occurs within their
//borders

public class CanvasButton {
	
	
	private float buttonLeft;
	private float buttonTop;
	private float buttonHeight;
	private float buttonWidth;
	
	//making the paints public so we don't need getter and setters for all the properties
	public Paint buttonPaint;
	public Paint textPaint;
	
	private String textString;
	private float textLeft;
	private float textTop;
	
	
	public CanvasButton(){
		buttonPaint = new Paint();
		textPaint = new Paint();
	}
	
	public float getButtonLeft() {
		return buttonLeft;
	}
	public void setButtonLeft(float buttonLeft) {
		this.buttonLeft = buttonLeft;
	}

	public float getButtonTop() {
		return buttonTop;
	}

	public void setButtonTop(float buttonTop) {
		this.buttonTop = buttonTop;
	}

	public float getButtonHeight() {
		return buttonHeight;
	}

	public void setButtonHeight(float buttonHeight) {
		this.buttonHeight = buttonHeight;
	}

	public float getButtonWidth() {
		return buttonWidth;
	}

	public void setButtonWidth(float buttonWidth) {
		this.buttonWidth = buttonWidth;
	}

	public Paint getButtonPaint() {
		return buttonPaint;
	}

	public void setButtonPaint(Paint buttonPaint) {
		this.buttonPaint = buttonPaint;
	}

	public Paint getTextPaint() {
		return textPaint;
	}

	public void setTextPaint(Paint textPaint) {
		this.textPaint = textPaint;
	}
	
	public boolean isPointInsideButton(float x,float y){
		if ( (x>=buttonLeft)&&(x<=(buttonLeft+buttonWidth)) &&
			 (y>=buttonTop)&&(y<=(buttonTop+buttonHeight))){
			return true;
		} else
			return false;
			
	}

	public String getTextString() {
		return textString;
	}

	public void setTextString(String textString) {
		this.textString = textString;
	}

	public float getTextLeft() {
		return textLeft;
	}

	public void setTextLeft(float textLeft) {
		this.textLeft = textLeft;
	}

	public float getTextTop() {
		return textTop;
	}

	public void setTextTop(float textTop) {
		this.textTop = textTop;
	}
	
}
