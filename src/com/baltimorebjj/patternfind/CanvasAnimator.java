/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: Canvas Animator.java
*     Creation Date: 12/18/2013
*            Author: Lee Synkowski
*  
*       Description: This class is used to display a sequence of Bitmap
*       images to a canvas for use in animation
*  
* 
*	Code Review:	Code reviewed 3/20/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.os.SystemClock;

public class CanvasAnimator {

	private long startTime = 0;
	
	private boolean running = false;
	
	private ArrayList<Bitmap> frames = new ArrayList<Bitmap>();
	
	private long timeInterval = 1;
	
	//Constructor accepts the ArrayList of Animation frames and
	//the total length of time in seconds of the animation 
	public CanvasAnimator(ArrayList<Bitmap> frames,double length){
		
		//frames holds an ArrayList of each animation frame
		this.frames = frames;
		
		//length of time in mS that each frame will be returned
		if ( frames.size()!=0 ){
			
			timeInterval =(long) ((length /( frames.size() )) * 1000);
		}
	}
	
	//Starts the animation running
	public void start(){
		
		startTime = SystemClock.elapsedRealtime();
		
		running = true;
	}
	
	
	//Returns the frame needed at the correct time 
	//or null and stops the animation running if needed
	public Bitmap getCurrentFrame(){
		if (running){
			
			long timeElapsed = SystemClock.elapsedRealtime() - startTime;
			
			if (timeElapsed >= (this.frames.size() * timeInterval)){
			
				running = false;
				
				return null;
			
			} else {
				
				return this.frames.get((int)(timeElapsed/timeInterval));
			}
		}
		
		return null;
	}
	
	public boolean isRunning(){
		
		return running;
	}
	
}
