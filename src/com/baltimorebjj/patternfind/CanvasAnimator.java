package com.baltimorebjj.patternfind;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.os.SystemClock;

public class CanvasAnimator {

	private long startTime;
	private boolean running = false;
	private ArrayList<Bitmap> frames;
	private long timeInterval;
	
	public CanvasAnimator(ArrayList<Bitmap> frames,double length){
		this.frames = frames;
		timeInterval = (long) ((length)/(frames.size()) * 1000);
	}
	
	public void start(){
		startTime = SystemClock.elapsedRealtime();
		running = true;
	}
	
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
