package com.baltimorebjj.patternfind;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


//using the techniques of the dietel book to follow to attempt this

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback{
	
	private DrawingThread drawingThread;
	//private Activity activity;
	
	private Paint backgroundPaint;
	private Paint squarePaint;
	private Paint playAreaPaint;
	
	private int screenWidth;
	private int screenHeight;
	private int blockSize;
	
	private boolean gameOver = false;
	
	private float pitch_angle = 0;
	private float roll_angle = 0;

	private float playAreaTop;
	private float playAreaLeft;
	private float playAreaBottom;
	private float playAreaRight;
	
	private TurnEvent turnEvent;
	private TurnEvent previousTurnEvent;
	
	private GameBoard theGameBoard;
	
	private ArrayList<GameTile> activeTiles;
	private ArrayList<GameTile> patternTiles;
	
	private Random rng = new Random();
	
	//Measure frames per second.
    private long now;
    private int framesCount=0;
    private int framesCountAvg=0;
    private long framesTimer=0;
    //Paint fpsPaint=new Paint();

    //Frame speed
    private long timeNow;
    private long timePrev = 0;
    private long timePrevFrame = 0;
    private long timeDelta;
	
	//private Pattern thePattern;
	
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//activity = (Activity) context;
		
		//register SurfaceHolder.Callback listener
		getHolder().addCallback(this);
		
		backgroundPaint = new Paint();
		squarePaint = new Paint();
		playAreaPaint = new Paint();
		
		turnEvent = TurnEvent.INACTIVE;
		previousTurnEvent = TurnEvent.INACTIVE;
		
	} //end constructor
	
	@Override
	protected void onSizeChanged(int w,int h,int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		
		//store for use
		screenWidth = w;
		screenHeight = h;
		blockSize = w/10;
		
		
		//initialize paints
		squarePaint.setColor(Color.BLUE);
		backgroundPaint.setColor(Color.WHITE);
		playAreaPaint.setColor(Color.BLACK);
		playAreaPaint.setStyle(Paint.Style.STROKE);
		playAreaPaint.setStrokeWidth(2);
		
		//initialize play area
		playAreaTop = h/10;
		playAreaBottom = (h/10)+(11 * blockSize);
		playAreaLeft = w/20; //1/2 of 1/10 the width
		playAreaRight = w - (w/20);
		
		//initialize the gameboard
		theGameBoard = new GameBoard(9,11,screenWidth,screenHeight);
		theGameBoard.populateBoard(25+rng.nextInt(40));
		
		//initialize the pattern
		//thePattern = new Pattern(screenWidth,blockSize);
		
		//start a new game
		newGame();
	}

	public void newGame(){
		
		
		if (gameOver){
			gameOver = false;
			drawingThread = new DrawingThread(getHolder());
			drawingThread.start();
		}
		
	}
	
	//use to move pieces around and make logical checks
	private void updatePositions(){
		//can only read turn events if inactive
		//additional protection checking status of last turn event so we don't try to process twice
		if (turnEvent == TurnEvent.INACTIVE){
			/*
			if ((pitch_angle < -40)&&(previousTurnEvent!=TurnEvent.LOW_PITCH)){
				turnEvent = TurnEvent.LOW_PITCH;
				
			}else if ((pitch_angle > 40)&&(previousTurnEvent!=TurnEvent.HIGH_PITCH)){
				turnEvent = TurnEvent.HIGH_PITCH;
				
			}else if ((roll_angle > 40)&&(previousTurnEvent!=TurnEvent.HIGH_ROLL)){
				turnEvent = TurnEvent.HIGH_ROLL;
				
			}else if ((roll_angle < -40)&&(previousTurnEvent!=TurnEvent.LOW_ROLL)){
				turnEvent = TurnEvent.LOW_ROLL;
			}
			*/
			if ((pitch_angle < -40)){
				turnEvent = TurnEvent.LOW_PITCH;
				
			}else if ((pitch_angle > 40)){
				turnEvent = TurnEvent.HIGH_PITCH;
				
			}else if ((roll_angle > 40)){
				turnEvent = TurnEvent.HIGH_ROLL;
				
			}else if ((roll_angle < -40)){
				turnEvent = TurnEvent.LOW_ROLL;
			}	
		}
		
		switch (turnEvent){
			case LOW_PITCH:
				theGameBoard.handleLowPitch();
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.LOW_PITCH;
				/*
				if (theGameBoard.handleLowPitch()){

				} else{
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.LOW_PITCH;
				}
				*/
				break;
			
			case HIGH_PITCH:
				theGameBoard.handleHighPitch();
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.HIGH_PITCH;
				/*
				if (theGameBoard.handleHighPitch()){

				} else{
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.HIGH_PITCH;
				}
				*/
				break;

			case LOW_ROLL:
				theGameBoard.handleLowRoll();
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.LOW_ROLL;
				/*
				if (theGameBoard.handleLowRoll()){

				} else{
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.LOW_ROLL;
				}
				*/
				break;
				
			case HIGH_ROLL:
				theGameBoard.handleHighRoll();
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.HIGH_ROLL;
				/*
				if (theGameBoard.handleHighRoll()){

				} else{
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.HIGH_ROLL;
				}
				*/
				break;
			case INACTIVE:
				//do nothing
				break;
		}
		theGameBoard.setLastTurnEvent(previousTurnEvent);
	}
	
	//use to draw the game to the given canvas
	public synchronized void drawGameElements(Canvas canvas){
			
		//clear the background
		canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),backgroundPaint);
		

		//draw the background rectangle
		canvas.drawRect(playAreaLeft, playAreaTop, playAreaRight, playAreaBottom, playAreaPaint);
		
		//here is where the gameboard could return the active pieces arraylist
		activeTiles = theGameBoard.getActiveTiles();
		patternTiles = theGameBoard.thePattern.getTilePattern();
		
		Paint outlinePaint = new Paint();
		outlinePaint.setColor(Color.BLACK);
		outlinePaint.setStrokeWidth(5);
		outlinePaint.setStyle(Paint.Style.STROKE);
		
		Paint highlightPaint = new Paint();
		highlightPaint.setColor(Color.WHITE);
		highlightPaint.setAlpha(200);
		
		Paint textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		canvas.drawText("Your Pattern: ", screenWidth/3, theGameBoard.getTileSize()/2, textPaint);
		
		//draw the pattern at the top of the screen
		for (int tileIndex=0;tileIndex!=patternTiles.size();tileIndex++){
			
			GameTile tile = patternTiles.get(tileIndex);
			canvas.drawRect(tile.getLocationLeft(), tile.getLocationTop(),
					tile.getLocationLeft()+theGameBoard.getTileSize(), 
					tile.getLocationTop()+theGameBoard.getTileSize(), tile.getPaint());
			canvas.drawRect(tile.getLocationLeft(), tile.getLocationTop(),
					tile.getLocationLeft()+theGameBoard.getTileSize(), 
					tile.getLocationTop()+theGameBoard.getTileSize(), outlinePaint);
			canvas.drawRect(
					tile.getLocationLeft()+((7*theGameBoard.getTileSize())/10), 
					tile.getLocationTop()+((theGameBoard.getTileSize())/10),
					tile.getLocationLeft()+((9*theGameBoard.getTileSize())/10), 
					tile.getLocationTop()+((3*theGameBoard.getTileSize())/10), highlightPaint);
		}
		
		
		//iterate through the activeTile arrayList and draw all those pieces

		for (int tileIndex=0;tileIndex!=activeTiles.size();tileIndex++){
			GameTile tile = activeTiles.get(tileIndex);
			canvas.drawRect(tile.getLocationLeft(), tile.getLocationTop(),
					tile.getLocationLeft()+theGameBoard.getTileSize(), 
					tile.getLocationTop()+theGameBoard.getTileSize(), tile.getPaint());
			canvas.drawRect(tile.getLocationLeft(), tile.getLocationTop(),
					tile.getLocationLeft()+theGameBoard.getTileSize(), 
					tile.getLocationTop()+theGameBoard.getTileSize(), outlinePaint);
			canvas.drawRect(
					tile.getLocationLeft()+((7*theGameBoard.getTileSize())/10), 
					tile.getLocationTop()+((theGameBoard.getTileSize())/10),
					tile.getLocationLeft()+((9*theGameBoard.getTileSize())/10), 
					tile.getLocationTop()+((3*theGameBoard.getTileSize())/10), highlightPaint);
		}
		
		
		
		Paint startPaint = new Paint();
		startPaint.setColor(Color.WHITE);
		Paint endPaint = new Paint();
		endPaint.setColor(Color.BLACK);
		Paint linePaint = new Paint();
		linePaint.setColor(Color.GRAY);
		
		ArrayList<GameTile> touchedTiles = theGameBoard.getTileSequence();
		float[] pointArray = theGameBoard.getPointArray();
		
		if (touchedTiles.size()==1){
			GameTile tempTile = touchedTiles.get(0);
			canvas.drawCircle(tempTile.getLocationLeft()+(theGameBoard.getTileSize()/2), 
						      tempTile.getLocationTop()+(theGameBoard.getTileSize()/2), 
						      (theGameBoard.getTileSize()/6), 
						      startPaint);
		} else if (touchedTiles.size()>1){
			GameTile tempTile = touchedTiles.get(0);
			canvas.drawCircle(tempTile.getLocationLeft()+(theGameBoard.getTileSize()/2), 
						      tempTile.getLocationTop()+(theGameBoard.getTileSize()/2), 
						      (theGameBoard.getTileSize()/6), 
						      startPaint);
			/*
			float[] pointArray = new float[4*touchedTiles.size()];
			
			for (int tileNumber=0;tileNumber!=touchedTiles.size()-1;tileNumber++){
				pointArray[(4*tileNumber)]   = (touchedTiles.get(tileNumber).getLocationLeft()) + (theGameBoard.getTileSize()/2); 
				pointArray[(4*tileNumber)+1] = (touchedTiles.get(tileNumber).getLocationTop()) + (theGameBoard.getTileSize()/2);
				pointArray[(4*tileNumber)+2] = (touchedTiles.get(tileNumber+1).getLocationLeft()) + (theGameBoard.getTileSize()/2); 
				pointArray[(4*tileNumber)+3] = (touchedTiles.get(tileNumber+1).getLocationTop()) + (theGameBoard.getTileSize()/2);
			}
			canvas.drawLines(pointArray, linePaint);
			*/
			
			canvas.drawLines(pointArray, linePaint);
			tempTile = touchedTiles.get(touchedTiles.size()-1);
			canvas.drawCircle(tempTile.getLocationLeft()+(theGameBoard.getTileSize()/2), 
						      tempTile.getLocationTop()+(theGameBoard.getTileSize()/2), 
						      (theGameBoard.getTileSize()/6), 
						      endPaint);
		}
		
		//the remove button, or any other UI type stuff is here
		CanvasButton removeButton = theGameBoard.getRemoveButton();
		

		canvas.drawRect(removeButton.getButtonLeft(),
				        removeButton.getButtonTop(), 
				        removeButton.getButtonLeft()+removeButton.getButtonWidth(), 
				        removeButton.getButtonTop()+removeButton.getButtonHeight(),
				        removeButton.getButtonPaint());
		
		canvas.drawRect(removeButton.getButtonLeft(),
		        removeButton.getButtonTop(), 
		        removeButton.getButtonLeft()+removeButton.getButtonWidth(), 
		        removeButton.getButtonTop()+removeButton.getButtonHeight(),
		        outlinePaint);
		
		canvas.drawText(removeButton.getTextString(), removeButton.getTextLeft(),removeButton.getTextTop(),removeButton.getTextPaint());
		
		//measure frame rate
		now=System.currentTimeMillis();
		outlinePaint.setStrokeWidth(1);
		outlinePaint.setTextSize(20);
        canvas.drawText(framesCountAvg+" fps", 40, 70, outlinePaint);
        framesCount++;
        if(now-framesTimer>1000) {
                framesTimer=now;
                framesCountAvg=framesCount;
                framesCount=0;
        }
	}
	
	public void stopGame(){
		if (drawingThread != null)
			drawingThread.setRunning(false);
	}
	
	public void releaseResources(){
		
	}
	
	//called when surface changes, usually to deal with changes in screen orientation.  
	//we don't have orientation changes, so this is not important
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//Auto-generated method stub
		
	}

	
	//this is called when the surface is first created, so it is where we initiate the drawing thread
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawingThread = new DrawingThread(holder);
		drawingThread.setRunning(true);
		drawingThread.start();
		
	}

	
	//called when the surface is destroyed, so we make sure the thread terminates properly
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//ensure that thread terminates properly
		boolean retry = true;
		drawingThread.setRunning(false);
		
		while(retry){
			try{
				drawingThread.join();
				retry = false;
			} catch (InterruptedException e){
				
			}
		}
		
	}

	private class DrawingThread extends Thread{
		private SurfaceHolder surfaceHolder; // for manipulating canvas
		private boolean threadIsRunning = true;
		
		
		//constructor
		public DrawingThread(SurfaceHolder holder){
			surfaceHolder = holder;
			setName("DrawingThread");
		}
		
		//changes running state
		public void setRunning(boolean running){
			threadIsRunning = running;
		}
		
		public void run(){
			Canvas canvas = null;
			
			while (threadIsRunning){
				
				//limit frame rate
                timeNow = System.currentTimeMillis();
                timeDelta = timeNow - timePrevFrame;
                if ( timeDelta < 160) {
                    try {
                        Thread.sleep(160 - timeDelta);
                    }
                    catch(InterruptedException e) {

                    }
                }
                timePrevFrame = System.currentTimeMillis();
				
				
				try{
					canvas = surfaceHolder.lockCanvas(null);
					
					
					updatePositions();
					//lock the surface for drawing
					synchronized(surfaceHolder){
						drawGameElements(canvas);
						
					}
					
				}finally{
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	public void setAngles(float pitch,float roll){
		pitch_angle = pitch;
		roll_angle = roll;
	}
	
	
	@Override
	  public boolean onTouchEvent(MotionEvent event) {
		
		//you have to tap the squares
		if (event.getAction()==MotionEvent.ACTION_DOWN){
			theGameBoard.handleTouch(event.getX(), event.getY());
		}
	    invalidate();
	    return true;
	  } 
}
