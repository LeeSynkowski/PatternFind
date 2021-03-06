/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: DrawingView.java
*     Creation Date: 8/24/2013
*            Author: Lee Synkowski
*  
*       This class is the graphic and display logic necessary to 
*       play pattern find.  It is a subclass of the Surface view
*       which is used in drawing complicated Android Animations
*  
* 
*	Code Review:	Code reviewed 3/21/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.util.Log;

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback{
	
	//Variables for game display
	private DrawingThread drawingThread;  //Game display thread	
	//private SurfaceHolder mSurfaceHolder = null;	
	private Paint backgroundPaint;	
	private Paint squarePaint;	
	private Paint playAreaPaint;
	private int screenWidth;	
	private int screenHeight;	
	private float tileSize;
	private float playAreaTop;	
	private float playAreaLeft;	
	private float playAreaBottom;	
	private float playAreaRight;
	private Rect resetButtonRect;
	private Rect patternBackgroundRect;
	private Rect borderRect;
	private int patternNumber;

	
	//Variables for game play
	private boolean gameOver;	
	private int level;	
	private float pitch_angle;	
	private float roll_angle;	
	private TurnEvent turnEvent;	
	private TurnEvent previousTurnEvent;	
	private GameBoard theGameBoard;	
	ArrayList<GameTile> touchedTiles;
	private OnGameCompleteListener onGameCompleteListener;	
	private Intent startedIntent;
	private int stationaryTileCount;
	private int emptyTileCount;
	
	
	//Variables for tracking frame rate 
    private long timeNow;  //for tracking frame rate    
    private long timePrevFrame; //for tracking frame rate    
    private long timeDelta;	//for tracking frame rate    
    private int frameRateFactor; //smaller is faster    
    private int numberOfMoves;    
    private int twoStarMoves;    
    private int threeStarMoves;   
    
    
    //Tile bitmap images
    private static final Bitmap stationaryTile = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.blackhole);    
    private static final Bitmap bombTile = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.tile_bomb);    
    private static final Bitmap redTile = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.redtile);    
    private static final Bitmap greenTile = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.greentile);    
    private static final Bitmap blueTile = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.bluetile);    
    private static final Bitmap background = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.foxfurnebulabw);
    private static final Bitmap startBall = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.startball);   
    private static final Bitmap finishBall = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.finishball);   
    private static final Bitmap patternBackground = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.patternbackbround);
    private static final Bitmap borderImage = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.border);
    private Paint touchedPaint;    
    private Rect screenRect;
    
    
    //Bomb Animation variables and bitmaps
    private boolean bombAnimation;    
    private float bombX;    
    private float bombY;    
    private static final Bitmap blastFrame1 = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.newblast1);    
    private static final Bitmap blastFrame2 = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.newblast2);    
    private static final Bitmap blastFrame3 = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.newblast3);    
    private ArrayList<Bitmap> bombBlastFrames;    
    private CanvasAnimator bombBlast;
    
    
    //Rocket Animation variables and bitmaps
    private boolean rocketAnimation;   
    private static final Bitmap upRocketTile = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.tile_rocket_new);    
    private static final Bitmap downRocketTile = RotateBitmap(upRocketTile, 180);    
    private static final Bitmap leftRocketTile = RotateBitmap(upRocketTile, 270);    
    private static final Bitmap rightRocketTile = RotateBitmap(upRocketTile, 90);    
    private ArrayList<GameTile> rocketTileAnimationPieces;    
    private float ROCKET_MOVEMENT_INCREMENT;        
    private ImageView bgImage;
    
    
    //Reset Button
    private static final Bitmap resetButton = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.resetbutton);
    
    //Tracking Line Variables
	private Paint startPaint;	
	private Paint endPaint;	
	private Paint linePaint;
    
	
    //Sound Variables
    private static SoundPool soundPool;    
    private boolean hasSound;    
    private Map<Integer,Integer> soundMap; 
    
    private static final int COUNT_DOWN_ID = 0;    
    private static final int NEW_BLOCKS_ID = 1;   
    private static final int PATTERN_ERROR_ID = 2;   
    private static final int REMOVE_BLOCKS_ID = 3;   
    private static final int TILE_CLICK_ID = 4;  
    private static final int BOMB_ID = 5;   
    private static final int TILE_SHATTER_ID = 6;  
    private static final int ROCKET_SOUND_ID = 7;
    
    //Constants
    private static final int SEQUENCE_INDICATOR_SCALE_FACTOR = 6;
    private static final int FRAME_RATE_CONSTANT = 160;
    private static final double BOMB_BLAST_TIME = 0.25;
     

	@SuppressLint("UseSparseArrays")
	public DrawingView(Context context, AttributeSet attrs) {		
		super(context, attrs);
		
		getHolder().addCallback(this);
		
		
		
		turnEvent = TurnEvent.INACTIVE;		
		previousTurnEvent = TurnEvent.INACTIVE;
		
		//Initialize soundmap for retrieving sound effects
		soundPool = new SoundPool(2,AudioManager.STREAM_MUSIC,1);

		soundMap = new HashMap<Integer,Integer>();
		soundMap.put(COUNT_DOWN_ID,soundPool.load(context,R.raw.count_down,1));		
		soundMap.put(NEW_BLOCKS_ID,soundPool.load(context,R.raw.new_blocks,1));		
		soundMap.put(PATTERN_ERROR_ID,soundPool.load(context,R.raw.pattern_error,1));		
		soundMap.put(REMOVE_BLOCKS_ID,soundPool.load(context,R.raw.remove_blocks,1));		
		soundMap.put(TILE_CLICK_ID,soundPool.load(context,R.raw.tile_click,1));		
		soundMap.put(BOMB_ID,soundPool.load(context,R.raw.bomb,1));		
		soundMap.put(TILE_SHATTER_ID,soundPool.load(context,R.raw.tile_shatter,1));		
		soundMap.put(ROCKET_SOUND_ID,soundPool.load(context,R.raw.rocket_blast,1));	

		//Set up bomb blast animation
		bombBlastFrames = new ArrayList<Bitmap>();
		bombBlastFrames.add(blastFrame1);
		bombBlastFrames.add(blastFrame2);
		bombBlastFrames.add(blastFrame3);
		bombBlast = new CanvasAnimator(bombBlastFrames,BOMB_BLAST_TIME);
		
		//Initialize Paints
		backgroundPaint = new Paint();		
		squarePaint = new Paint();		
		playAreaPaint = new Paint();
		touchedPaint = new Paint();
		startPaint = new Paint();	
		endPaint = new Paint();	
		linePaint = new Paint();
		
		touchedPaint.setAlpha(190);
		squarePaint.setColor(Color.BLUE);
		backgroundPaint.setColor(Color.BLUE);
		playAreaPaint.setColor(Color.WHITE);
		playAreaPaint.setStyle(Paint.Style.STROKE);
		playAreaPaint.setStrokeCap(Paint.Cap.ROUND);
		
		startPaint.setColor(Color.GREEN);
		endPaint.setColor(Color.RED);
		linePaint.setColor(Color.WHITE);
		linePaint.setAlpha(200);

		//initialize variables
		gameOver = true;
		pitch_angle = 0;	
		roll_angle = 0;
		theGameBoard = null;
		timePrevFrame = 0;
		numberOfMoves=0; 
		
		bombAnimation = false; 
		rocketAnimation = false;
		rocketTileAnimationPieces = null;

		timePrevFrame = 0;
		hasSound = true; 
		frameRateFactor = 80; 
		
		Random rng = new Random();
		patternNumber = rng.nextInt(6);
		
	} //End constructor
	
	@Override
	protected void onSizeChanged(int w,int h,int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		
		//Store for use
		screenWidth = w;
		screenHeight = (int)(h * (0.92));
		screenRect = new Rect(0,0,screenWidth,screenHeight);

		
		//New Game must be started from this callback method
		try {
			newGame();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void newGame() throws XmlPullParserException, IOException{
		
		//Initialize the game board
		theGameBoard = new GameBoard(level,screenWidth,screenHeight,getContext());
		
		tileSize = (int) theGameBoard.getTileSize();
		
		linePaint.setStrokeWidth(tileSize/8);
		linePaint.setStrokeCap(Paint.Cap.ROUND);
		
		playAreaPaint.setStrokeWidth(3);
		
		playAreaTop = theGameBoard.getTopPosition();
		playAreaBottom = theGameBoard.getBottomPosition();
		playAreaLeft = theGameBoard.getLeftPosition();
		playAreaRight = theGameBoard.getRightPosition();	
		
		twoStarMoves = theGameBoard.getTwoStarMoves();
		threeStarMoves = theGameBoard.getThreeStarMoves();
		
		resetButtonRect = new Rect((int)theGameBoard.resetButton.getButtonLeft(),
										(int)theGameBoard.resetButton.getButtonTop(),
				                        (int)(theGameBoard.resetButton.getButtonLeft()+theGameBoard.resetButton.getButtonWidth()),
				                        (int)(theGameBoard.resetButton.getButtonTop()+theGameBoard.resetButton.getButtonHeight()));
		
		float heightUnit = (screenHeight - theGameBoard.getBottomPosition())/10;
		float widthUnit = (screenWidth)/6;
		
		
		patternBackgroundRect = new Rect((int)  widthUnit,
										 (int) (theGameBoard.getBottomPosition()+(4.5*heightUnit)),
										 (int)  widthUnit*5,
										 (int) (theGameBoard.getBottomPosition()+(8.5*heightUnit)));
		int rectPadding = (int)((playAreaRight-playAreaLeft)/90);
		borderRect = new Rect((int) playAreaLeft-rectPadding,
				 (int) playAreaTop-rectPadding,
				 (int)  playAreaRight+rectPadding,
				 (int) playAreaBottom+rectPadding);
		/*
		patternBackgroundRect = new Rect((int) (theGameBoard.getBottomPosition()),
				 0,
				 screenHeight,
				 screenWidth);
		*/
		
		for (GameTile t:theGameBoard.getActiveTiles()){
			if (t.getTileType()==TileType.STATIONARY_TILE){
				stationaryTileCount++;
			}
			if (!t.isOccupied()){
				emptyTileCount++;
			}
		}
		
		ROCKET_MOVEMENT_INCREMENT = tileSize;
		
		if (gameOver){
			gameOver = false;
		}
		
		
		//Initialize and start drawing thread
		drawingThread = new DrawingThread(getHolder());
		drawingThread.setRunning(true);
		drawingThread.start();
		
	}
	
	//Logic to move and update pieces
	private void updatePositions(){

		//Only checks for turn events when animation is complete 
		if (((turnEvent == TurnEvent.INACTIVE)&&(theGameBoard.isAnimationComplete()))&&(!rocketAnimation)&&(!bombAnimation)){		
			if ((pitch_angle < -20)){
				turnEvent = TurnEvent.LOW_PITCH;
				
			}else if ((pitch_angle > 20)){
				turnEvent = TurnEvent.HIGH_PITCH;
				
			}else if ((roll_angle > 20)){
				turnEvent = TurnEvent.HIGH_ROLL;
				
			}else if ((roll_angle < -20)){
				turnEvent = TurnEvent.LOW_ROLL;
			}	
		}
		
		switch (turnEvent){
			case LOW_PITCH:
				theGameBoard.handleLowPitch(pitch_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.LOW_PITCH;
				break;
			
			case HIGH_PITCH:
				theGameBoard.handleHighPitch(pitch_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.HIGH_PITCH;
				break;

			case LOW_ROLL:
				theGameBoard.handleLowRoll(roll_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.LOW_ROLL;
				break;
				
			case HIGH_ROLL:
				theGameBoard.handleHighRoll(roll_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.HIGH_ROLL;
			case INACTIVE:
				break;
		}
		theGameBoard.setLastTurnEvent(previousTurnEvent);
	}
	
	//Draw the game to the given canvas
	public synchronized void drawGameElements(Canvas canvas){	
		
		//Clear the background
		canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),backgroundPaint);
		canvas.drawBitmap(background, null, screenRect, null);
		
		
		//Draw the background rectangle
		//canvas.drawRect(playAreaLeft, playAreaTop, playAreaRight, playAreaBottom, playAreaPaint);			
		canvas.drawBitmap(borderImage, null, borderRect, null);
		
		//Draw the pattern at the bottom of the screen
		
		canvas.drawBitmap(patternBackground, null, patternBackgroundRect, null);
		
		ArrayList<GameTile> localPatternTiles = theGameBoard.thePattern.getTilePattern();
		for (GameTile tile:localPatternTiles){
			drawPatternTile(tile,canvas);
		}
		
		//Draw the reset button
		canvas.drawBitmap(resetButton, null, resetButtonRect, null);
		
		//Draw the active tiles
		ArrayList<GameTile> localActiveTiles = theGameBoard.getCopyOfActiveTiles();
		for (GameTile tile:localActiveTiles){
			drawTile(tile,canvas);
		}
		
		//Draw the moving tiles
		if ((!theGameBoard.isAnimationComplete())){
			ArrayList<GameTile> localMovingTiles = theGameBoard.getCopyOfMovingTiles();
			for (GameTile tile:localMovingTiles){
				drawTile(tile,canvas);
			}

			if (theGameBoard.isTileSequenceContinuous()){
				theGameBoard.adjustPointArray();
			}else{
				theGameBoard.clearTileSequence();
			}
			drawTileSequenceLine(canvas);
			theGameBoard.animationStepOccurred();
		} 
		
		//Check for bomb animation and execute
		if (bombAnimation){
					if (!bombBlast.isRunning()){
						bombBlast.start();
					}

					Rect destRect = new Rect((int)(bombX-tileSize),(int)(bombY-tileSize),(int)(bombX+(2*tileSize)),(int)(bombY+(2*tileSize))); 
					Bitmap currentFrame = bombBlast.getCurrentFrame();
					if (currentFrame!=null){
						Log.v("LEE","Displayed a Bomb Frame");
						canvas.drawBitmap(currentFrame,null,destRect, null);
					}else{
						bombAnimation = false;
					}
		}
		//Check for rocket animation and execute
		if (rocketAnimation){
					//Get the rocket tile animation pieces if this is the first time through
					if (rocketTileAnimationPieces == null){
						rocketTileAnimationPieces = theGameBoard.getCopyOfRocketTiles();
					}
					
					//Check if the rocket is complete
					switch (rocketTileAnimationPieces.get(0).getOrientation()){ 
						case DOWN:
						case RIGHT:
							if (theGameBoard.isRocketDoneIncreasing(theGameBoard.turnXToWidthIndex(rocketTileAnimationPieces.get(0).getLocationLeft()),theGameBoard.turnYToHeightIndex(rocketTileAnimationPieces.get(0).getLocationTop()))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
							}
							break;
						case UP:
						case LEFT:
							if (theGameBoard.isRocketDoneDecreasing(theGameBoard.turnXToWidthIndex(rocketTileAnimationPieces.get(0).getLocationLeft()),theGameBoard.turnYToHeightIndex(rocketTileAnimationPieces.get(0).getLocationTop()))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
							}
							break;	
							/*
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex(rocketTileAnimationPieces.get(0).getLocationLeft()),theGameBoard.turnYToHeightIndex((float) (rocketTileAnimationPieces.get(0).getLocationTop()+(0.9)*theGameBoard.getTileSize())))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
							}							
							break;
						
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex((float) (rocketTileAnimationPieces.get(0).getLocationLeft()+(0.9)*theGameBoard.getTileSize())),theGameBoard.turnYToHeightIndex(rocketTileAnimationPieces.get(0).getLocationTop()))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
							}							
							break;
							*/
					}
					
					//Check if animation is still necessary
					if (rocketAnimation){

						float topLocation = rocketTileAnimationPieces.get(0).getLocationTop();
						float leftLocation = rocketTileAnimationPieces.get(0).getLocationLeft();
						switch (rocketTileAnimationPieces.get(0).getOrientation()){
							case UP:							
								rocketTileAnimationPieces.get(0).setLocationTop(topLocation - ROCKET_MOVEMENT_INCREMENT);
								break;
							case DOWN:
								rocketTileAnimationPieces.get(0).setLocationTop(topLocation + ROCKET_MOVEMENT_INCREMENT);
								break;
							case LEFT:
								rocketTileAnimationPieces.get(0).setLocationLeft(leftLocation - ROCKET_MOVEMENT_INCREMENT);
								break;
							case RIGHT:
								rocketTileAnimationPieces.get(0).setLocationLeft(leftLocation + ROCKET_MOVEMENT_INCREMENT);
								break;						
						}
						
						//Check if the rocket has crossed with the next tile in its path
						if (rocketTileAnimationPieces.size()>=2){
							switch (rocketTileAnimationPieces.get(0).getOrientation()){
								case UP:							
									if (rocketTileAnimationPieces.get(0).getLocationTop()<(rocketTileAnimationPieces.get(1).getLocationTop()+theGameBoard.getTileSize())){
										rocketTileAnimationPieces.remove(1);
										if (hasSound){
											soundPool.play(soundMap.get(TILE_SHATTER_ID), 1, 1, 1, 0, 1f);
										}
									}
									break;
								case DOWN:
									if ((rocketTileAnimationPieces.get(0).getLocationTop()+theGameBoard.getTileSize())>(rocketTileAnimationPieces.get(1).getLocationTop())){
										rocketTileAnimationPieces.remove(1);
										if (hasSound){
											soundPool.play(soundMap.get(TILE_SHATTER_ID), 1, 1, 1, 0, 1f);
										}
									}
									break;
								case LEFT:
									if ((rocketTileAnimationPieces.get(0).getLocationLeft())<(rocketTileAnimationPieces.get(1).getLocationLeft()+theGameBoard.getTileSize())){
										rocketTileAnimationPieces.remove(1);
										if (hasSound){
											soundPool.play(soundMap.get(TILE_SHATTER_ID), 1, 1, 1, 0, 1f);
										}
									}
									break;
								case RIGHT:
									if ((rocketTileAnimationPieces.get(0).getLocationLeft()+theGameBoard.getTileSize())>(rocketTileAnimationPieces.get(1).getLocationLeft())){
										rocketTileAnimationPieces.remove(1);
										if (hasSound){
											soundPool.play(soundMap.get(TILE_SHATTER_ID), 1, 1, 1, 0, 1f);
										}
									}
									break;						
							}
						}

						//Draw the pieces
						for (int tileIndex=0;tileIndex!=rocketTileAnimationPieces.size();tileIndex++){
							drawTile(rocketTileAnimationPieces.get(tileIndex),canvas);
						}
					}
		}
		
		if (theGameBoard.isAnimationComplete()){
			drawTileSequenceLine(canvas);
		}
	}
	
	
	//Checks to see if the level has been completed, and is so notifies the
	//game complete listener
	public void gameLogic(Canvas canvas){
		if (theGameBoard != null){
			if (((theGameBoard.getActiveTiles().size()-stationaryTileCount-emptyTileCount)==0)&&(theGameBoard.isAnimationComplete())&&(!bombAnimation)&&(!rocketAnimation)){
				
				if (drawingThread != null){
					drawingThread.setRunning(false);
				}
				onGameCompleteListener.onGameComplete(startedIntent,numberOfMoves,twoStarMoves,threeStarMoves,true);				
			}
		}
	}
	
	
	//Stops the game, including the Drawing thread
	public void stopGame(){
		if (drawingThread != null)
			drawingThread.setRunning(false);
		if (gameOver){
			onGameCompleteListener.onGameComplete(startedIntent,numberOfMoves,twoStarMoves,threeStarMoves,true);
		}
	}
	
	
	public void releaseResources(){	
		soundPool.release();
		soundPool = null;
	}
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	
	//This is called when the surface is first created, so it is where we initiate the drawing thread
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//mSurfaceHolder=holder;
	}

	
	//Called when the surface is destroyed, so we make sure the thread terminates properly
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//Ensure that thread terminates properly
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

	//The drawing thread that continually updates the canvas
	private class DrawingThread extends Thread{
		private SurfaceHolder surfaceHolder; // For manipulating canvas
		private volatile boolean threadIsRunning = true;
		
		
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
                if ( timeDelta < frameRateFactor) {
                    try {
                        Thread.sleep(FRAME_RATE_CONSTANT - frameRateFactor);
                    }
                    catch(InterruptedException e) {

                    }
                }
                timePrevFrame = System.currentTimeMillis();

				try{
							canvas = surfaceHolder.lockCanvas(null);
					
							gameLogic(canvas);
					
							updatePositions();
							
							//Lock the surface for drawing
							synchronized(surfaceHolder){
								drawGameElements(canvas);	
					}
					
				}catch(NullPointerException e){
					
				}finally{
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	//Called by the parent activity to set angles from the sensor
	public void setAngles(float pitch,float roll){
		pitch_angle = pitch;
		roll_angle = roll;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Logic section to determine if the touch is moving to allow for 
		//dragging to select tiles
		if (event.getAction()!=MotionEvent.ACTION_MOVE){
			theGameBoard.setMoving(false);
		}
		if (event.getAction()==MotionEvent.ACTION_MOVE){
			theGameBoard.setMoving(true);
		}
		if (event.getAction()==MotionEvent.ACTION_UP){
			theGameBoard.setFingerDown(false);
		} else {
			theGameBoard.setFingerDown(true);
		}

		if (((event.getAction()==MotionEvent.ACTION_DOWN)||(event.getAction()==MotionEvent.ACTION_MOVE))&&(!bombAnimation)&&(!rocketAnimation)){
			switch (theGameBoard.handleTouch(event.getX(), event.getY()))
			{
			case ADJUST_TILE_SEQUENCE:
				if (hasSound){
					soundPool.play(soundMap.get(TILE_CLICK_ID), 1, 1, 1, 0, 1f);
				}
				break;
			case NOTHING:
				break;
			case REMOVE_BUTTON_CORRECT:
				theGameBoard.resetTileMoveCounter();
				if (hasSound){
					soundPool.play(soundMap.get(REMOVE_BLOCKS_ID), 1, 1, 1, 0, 1f);
				}
				numberOfMoves++;
				break;
			case REMOVE_BUTTON_INCORRECT:
				if (!theGameBoard.isMoving()){
					if (hasSound){
						soundPool.play(soundMap.get(PATTERN_ERROR_ID), 1, 1, 1, 0, 1f);
					}
				}
				break;
			case BOMB_TILE:
				//Play bomb sound
				if (hasSound){
					soundPool.play(soundMap.get(BOMB_ID), 1, 1, 1, 0, 1f);
				}
				//Begin bomb animation state
				bombAnimation= true;
				bombX = theGameBoard.getLeftPosition()+ (theGameBoard.getTileSize() * theGameBoard.turnXToWidthIndex(event.getX()));
				bombY = theGameBoard.getTopPosition() + (theGameBoard.getTileSize() * theGameBoard.turnYToHeightIndex(event.getY()));
				numberOfMoves++;
				break;
			case ROCKET_TILE:
				//Play blast off sound
				if (hasSound){
					soundPool.play(soundMap.get(ROCKET_SOUND_ID), 1, 1, 1, 0, 1f);
				}
				//Begin rocket animation state
				rocketAnimation= true;
				numberOfMoves++;
				break;
			case RESET:
				onGameCompleteListener.onGameComplete(startedIntent,numberOfMoves,twoStarMoves,threeStarMoves,false);
				break;
			default:
				break;		
			}
		}
	    invalidate();
	    return true;
	  } 
	
	 //For drawing game tiles in play
	 private void drawTile2(GameTile tile,Canvas canvas){
		 
		 Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)theGameBoard.getTileSize()+1,(int)tile.getLocationTop()+(int)theGameBoard.getTileSize()+1);
			if (tile.getTileType() == TileType.GAME_TILE){
				switch (tile.getPaintColor()){
					case Color.RED:
						if (!tile.isTouched()){
							canvas.drawBitmap(redTile,null,destSize, null);
						}else{
							canvas.drawBitmap(redTile,null,destSize, touchedPaint);
						}
						break;
					case Color.BLUE:
						if (!tile.isTouched()){
							canvas.drawBitmap(blueTile,null,destSize, null);
						}else{
							canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
						}
						break;
					case Color.GREEN:
						if (!tile.isTouched()){
							canvas.drawBitmap(greenTile,null,destSize, null);
						}else{
							canvas.drawBitmap(greenTile,null,destSize, touchedPaint);
						}
						break;				
				}
			} else if (tile.getTileType() == TileType.STATIONARY_TILE){	
				canvas.drawBitmap(stationaryTile,null,destSize, null);
			} else if (tile.getTileType() == TileType.BOMB_TILE){
				canvas.drawBitmap(bombTile,null,destSize, null);
			} else if (tile.getTileType() == TileType.ROCKET_TILE){
				switch (tile.getOrientation()){
					case UP:
						canvas.drawBitmap(upRocketTile,null,destSize, null);
						break;
					case DOWN:
						canvas.drawBitmap(downRocketTile,null,destSize, null);
						break;
					case LEFT:
						canvas.drawBitmap(leftRocketTile,null,destSize, null);
						break;
					case RIGHT:
						canvas.drawBitmap(rightRocketTile,null,destSize, null);
						break;
					
				}
			}
	 }
	 
	 //For drawing game tiles in play
	 private void drawTile(GameTile tile,Canvas canvas){
		 
		 Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)theGameBoard.getTileSize()+1,(int)tile.getLocationTop()+(int)theGameBoard.getTileSize()+1);
			if (tile.getTileType() == TileType.GAME_TILE){
				switch (tile.getPaintColor()){
					case Color.RED:
						if (patternNumber==0||patternNumber==1){
							if (!tile.isTouched()){
								canvas.drawBitmap(redTile,null,destSize, null);
							}else{
								canvas.drawBitmap(redTile,null,destSize, touchedPaint);
							}
						}else if (patternNumber==2||patternNumber==3){
							if (!tile.isTouched()){
								canvas.drawBitmap(greenTile,null,destSize, null);
							}else{
								canvas.drawBitmap(greenTile,null,destSize, touchedPaint);
							}
						}else if (patternNumber==4||patternNumber==5){
							if (!tile.isTouched()){
								canvas.drawBitmap(blueTile,null,destSize, null);
							}else{
								canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
							}
						}
						break;
					case Color.BLUE:
						if (patternNumber==3||patternNumber==5){
							if (!tile.isTouched()){
								canvas.drawBitmap(redTile,null,destSize, null);
							}else{
								canvas.drawBitmap(redTile,null,destSize, touchedPaint);
							}
						}else if (patternNumber==1||patternNumber==4){
							if (!tile.isTouched()){
								canvas.drawBitmap(greenTile,null,destSize, null);
							}else{
								canvas.drawBitmap(greenTile,null,destSize, touchedPaint);
							}
						}else if (patternNumber==0||patternNumber==2){
							if (!tile.isTouched()){
								canvas.drawBitmap(blueTile,null,destSize, null);
							}else{
								canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
							}
						}
						break;
					case Color.GREEN:
						if (patternNumber==2||patternNumber==4){
							if (!tile.isTouched()){
								canvas.drawBitmap(redTile,null,destSize, null);
							}else{
								canvas.drawBitmap(redTile,null,destSize, touchedPaint);
							}
						}else if (patternNumber==0||patternNumber==5){
							if (!tile.isTouched()){
								canvas.drawBitmap(greenTile,null,destSize, null);
							}else{
								canvas.drawBitmap(greenTile,null,destSize, touchedPaint);
							}
						}else if (patternNumber==1||patternNumber==3){
							if (!tile.isTouched()){
								canvas.drawBitmap(blueTile,null,destSize, null);
							}else{
								canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
							}
						}
						break;				
				}
			} else if (tile.getTileType() == TileType.STATIONARY_TILE){	
				canvas.drawBitmap(stationaryTile,null,destSize, null);
			} else if (tile.getTileType() == TileType.BOMB_TILE){
				canvas.drawBitmap(bombTile,null,destSize, null);
			} else if (tile.getTileType() == TileType.ROCKET_TILE){
				switch (tile.getOrientation()){
					case UP:
						canvas.drawBitmap(upRocketTile,null,destSize, null);
						break;
					case DOWN:
						canvas.drawBitmap(downRocketTile,null,destSize, null);
						break;
					case LEFT:
						canvas.drawBitmap(leftRocketTile,null,destSize, null);
						break;
					case RIGHT:
						canvas.drawBitmap(rightRocketTile,null,destSize, null);
						break;
					
				}
			}
	 }
	 
	 //For drawing the large pattern tiles at the bottom of the screen
	 private void drawPatternTile(GameTile tile,Canvas canvas){
			if (tile.getTileType() == TileType.GAME_TILE){

				Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)tile.getTileSize(),(int)tile.getLocationTop()+(int)tile.getTileSize());
				
				switch (tile.getPaintColor()){
				case Color.RED:
					if (patternNumber==0||patternNumber==1){
						if (!tile.isTouched()){
							canvas.drawBitmap(redTile,null,destSize, null);
						}else{
							canvas.drawBitmap(redTile,null,destSize, touchedPaint);
						}
					}else if (patternNumber==2||patternNumber==3){
						if (!tile.isTouched()){
							canvas.drawBitmap(greenTile,null,destSize, null);
						}else{
							canvas.drawBitmap(greenTile,null,destSize, touchedPaint);
						}
					}else if (patternNumber==4||patternNumber==5){
						if (!tile.isTouched()){
							canvas.drawBitmap(blueTile,null,destSize, null);
						}else{
							canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
						}
					}
					break;
				case Color.BLUE:
					if (patternNumber==3||patternNumber==5){
						if (!tile.isTouched()){
							canvas.drawBitmap(redTile,null,destSize, null);
						}else{
							canvas.drawBitmap(redTile,null,destSize, touchedPaint);
						}
					}else if (patternNumber==1||patternNumber==4){
						if (!tile.isTouched()){
							canvas.drawBitmap(greenTile,null,destSize, null);
						}else{
							canvas.drawBitmap(greenTile,null,destSize, touchedPaint);
						}
					}else if (patternNumber==0||patternNumber==2){
						if (!tile.isTouched()){
							canvas.drawBitmap(blueTile,null,destSize, null);
						}else{
							canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
						}
					}
					break;
				case Color.GREEN:
					if (patternNumber==2||patternNumber==4){
						if (!tile.isTouched()){
							canvas.drawBitmap(redTile,null,destSize, null);
						}else{
							canvas.drawBitmap(redTile,null,destSize, touchedPaint);
						}
					}else if (patternNumber==0||patternNumber==5){
						if (!tile.isTouched()){
							canvas.drawBitmap(greenTile,null,destSize, null);
						}else{
							canvas.drawBitmap(greenTile,null,destSize, touchedPaint);
						}
					}else if (patternNumber==1||patternNumber==3){
						if (!tile.isTouched()){
							canvas.drawBitmap(blueTile,null,destSize, null);
						}else{
							canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
						}
					}
					break;			
				}
			}
	}
	 
	//For drawing the line that indicates selected tiles 
	private void drawTileSequenceLine(Canvas canvas){
		ArrayList<GameTile> localTouchedTiles = theGameBoard.getCopyOfTileSequqnce();
		float[] localPointArray = theGameBoard.getCopyOfPointArray();
		float offset = theGameBoard.getTileSize()/4;
		if (localTouchedTiles.size()==1){
			GameTile tempTile = localTouchedTiles.get(0);
			Rect startRectDest = new Rect((int)(tempTile.getLocationLeft()+offset),(int)(tempTile.getLocationTop()+offset),(int)tempTile.getLocationLeft()+(int)(theGameBoard.getTileSize()-offset),(int)tempTile.getLocationTop()+(int)(theGameBoard.getTileSize()-offset));
			canvas.drawBitmap(startBall, null, startRectDest, null);
			/*
			canvas.drawCircle(tempTile.getLocationLeft()+(theGameBoard.getTileSize()/2), 
						      tempTile.getLocationTop()+(theGameBoard.getTileSize()/2), 
						      (theGameBoard.getTileSize()/SEQUENCE_INDICATOR_SCALE_FACTOR), 
						      startPaint);
			*/
		} else if (localTouchedTiles.size()>1){
			GameTile tempTile = localTouchedTiles.get(0);
			canvas.drawLines(localPointArray, linePaint);
			Rect startRectDest = new Rect((int)(tempTile.getLocationLeft()+offset),(int)(tempTile.getLocationTop()+offset),(int)tempTile.getLocationLeft()+(int)(theGameBoard.getTileSize()-offset),(int)tempTile.getLocationTop()+(int)(theGameBoard.getTileSize()-offset));
			canvas.drawBitmap(startBall, null, startRectDest, null);
			/*
			canvas.drawCircle(tempTile.getLocationLeft()+(theGameBoard.getTileSize()/2), 
						      tempTile.getLocationTop()+(theGameBoard.getTileSize()/2), 
						      (theGameBoard.getTileSize()/SEQUENCE_INDICATOR_SCALE_FACTOR), 
						      startPaint);
			*/
			if (!localTouchedTiles.isEmpty()){
				tempTile = localTouchedTiles.get(localTouchedTiles.size()-1);
			}
			Rect finishRectDest = new Rect((int)(tempTile.getLocationLeft()+offset),(int)(tempTile.getLocationTop()+offset),(int)tempTile.getLocationLeft()+(int)(theGameBoard.getTileSize()-offset),(int)tempTile.getLocationTop()+(int)(theGameBoard.getTileSize()-offset));
			canvas.drawBitmap(finishBall, null, finishRectDest, null);
			/*
			canvas.drawCircle(tempTile.getLocationLeft()+(theGameBoard.getTileSize()/2), 
						      tempTile.getLocationTop()+(theGameBoard.getTileSize()/2), 
						      (theGameBoard.getTileSize()/SEQUENCE_INDICATOR_SCALE_FACTOR), 
						      endPaint);		
			*/

		}	
	}
	
	 
	//Used to Rotate the Rocket images 
	private static Bitmap RotateBitmap(Bitmap source, float angle)
		{
		      Matrix matrix = new Matrix();
		      matrix.postRotate(angle);
		      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
		}
	
	
	public void setHasSound(boolean sound){
		 this.hasSound = sound;
	}
	
	
	public void setLevel(int intExtra) {
		this.level = intExtra;	
	}
		
	
	public void setOnGameCompleteListener(OnGameCompleteListener listener){
		onGameCompleteListener = listener;
	}

	public void setStartedIntent(Intent startedIntent) {
		this.startedIntent = startedIntent;
	}
	
	

}
