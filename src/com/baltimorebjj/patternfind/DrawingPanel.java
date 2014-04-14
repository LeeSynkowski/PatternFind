/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: DrawingPanel.java
*     Creation Date: 8/22/2013
*            Author: Lee Synkowski
*  
*       This class is the graphic and display logic necessary to 
*       play pattern find.  It is a subclass of the Surface view
*       which is used in drawing complicated Android Animations
*  
* 
*	Code Review:	Code reviewed 3/20/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */




package com.baltimorebjj.patternfind;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback{

	private PanelThread _thread;
	
	private Paint rectanglePaint;
	
	private Paint backgroundPaint;
	
	private float squareTopX = 100;
	
	private float squareTopY = 100;
	
	private float squareBottomX = squareTopX + 50;
	
	private float squareBottomY = squareBottomX + 50;
	
	private long delay = -1000;
	
	//private SensorManager mSensorManager;
	
	//private Sensor mOrientation;
	
	//private float azimuth_angle = 0;
	//private float pitch_angle = 0;
	//private float roll_angle = 0;
	

	
	
	
	public DrawingPanel(Context context) {
		
		super(context);
		
		getHolder().addCallback(this);

		rectanglePaint = new Paint();

		rectanglePaint.setColor(Color.BLUE);
		
		rectanglePaint.setStrokeWidth(15);

		backgroundPaint = new Paint();

		backgroundPaint.setColor(Color.WHITE);
		
	}
	
	
	public DrawingPanel(Context context, AttributeSet attrs){
		
		super(context,attrs);
		
	}
	
	
	public DrawingPanel(Context context, AttributeSet attrs, int defStyle){

		super(context,attrs,defStyle);

	}

	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false); //Allows us to use invalidate to call onDraw()
		_thread = new PanelThread(getHolder(),this);  //start the thread that will make
		_thread.setRunning(true);                     //calls to onDraw()
		_thread.start();
		
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try{
			_thread.setRunning(false); //Tells thread to stop
			_thread.join();			//Removes thread from memory
		}catch(InterruptedException e){
			
		}
		
	}
	
	/*
	@Override
	public void onDraw(Canvas canvas){
			canvas.drawRect(squareTopX,squareTopY,squareBottomX,squareBottomY, rectanglePaint);
	}
	*/
	
	public void drawGameElements(Canvas canvas){
		canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),backgroundPaint);
		canvas.drawRect(squareTopX,squareTopY,squareBottomX,squareBottomY, rectanglePaint);
	}
	
	private class PanelThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		private DrawingPanel _panel;
		private boolean _run = false;
		
		public PanelThread(SurfaceHolder surfaceHolder, DrawingPanel panel){
			_surfaceHolder = surfaceHolder;
			_panel = panel;
		}
		
		public void setRunning(boolean run){
			_run = run;
		}
		
		@Override
		public void run(){
			Canvas c=null;
			while(_run){
				c = null; //when setRunning(false) occurs, _run is set to false, loop ends stopping thread
				
				try{
					c = _surfaceHolder.lockCanvas(null);
					synchronized(_surfaceHolder){
						//insert methods to modify positions of items in onDraw()
						delay++;
						drawGameElements(c);
						//postInvalidate(); //supposedly this draws?
					}
				}
				finally{
					if (c!=null){
						_surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
	
	/* Delete after review
	public void setAngles(float azimuth,float pitch,float roll){
		azimuth_angle = azimuth;
		pitch_angle = pitch;
		roll_angle = roll;
	}
	*/
	
	
	public String showDelay(){
		return Long.toString(delay);
	}
}
