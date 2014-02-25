package com.baltimorebjj.patternfind;

import android.os.SystemClock;

public class GameTimer {

	private long timerLength;
	private long startTime;
	private boolean running = false;
	
	
	public GameTimer(long numberOfMilliSeconds){
		timerLength = numberOfMilliSeconds;
	}
	
	public float getTimerAmount() {
		return timerLength;
	}

	public void setTimerLength(long timerLength) {
		if (!running){
			this.timerLength = timerLength;
		}
	}
	
	public void start(){
		if (running == false){
			startTime = SystemClock.elapsedRealtime();
			running = true;
		}
	}
	
	
	public long getMilliSecondsRemaining(){
		long timeNow = SystemClock.elapsedRealtime() - startTime;
		if (!running){
			return 0;
		}
		if (timeNow >= timerLength){
			running = false;
			return 0;
		} else 
			return (timerLength -(SystemClock.elapsedRealtime() - startTime));
		
	}
	
	public boolean isRunning(){
		long timeNow = SystemClock.elapsedRealtime() - startTime;
		if (timeNow >= timerLength){
			running = false;
		}
		return running;
	}
	

}
