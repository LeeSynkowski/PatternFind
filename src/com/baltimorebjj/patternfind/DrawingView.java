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
	private SurfaceHolder mSurfaceHolder = null;	
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
	
	
	//Variables for game play
	private boolean gameOver = true;	
	private int level;	
	private float pitch_angle = 0;	
	private float roll_angle = 0;	
	private TurnEvent turnEvent;	
	private TurnEvent previousTurnEvent;	
	private GameBoard theGameBoard = null;	
	ArrayList<GameTile> touchedTiles;
	private OnGameCompleteListener onGameCompleteListener;	
	private Intent startedIntent;
	private int stationaryTileCount;
	private int emptyTileCount;
	
	
	//Variables for tracking frame rate 
    private long timeNow;  //for tracking frame rate    
    private long timePrevFrame = 0; //for tracking frame rate    
    private long timeDelta;	//for tracking frame rate    
    private final int frameRateFactor = 80; //smaller is faster    
    private int numberOfMoves=0;    
    private int twoStarMoves;    
    private int threeStarMoves;   
    
    
    //Tile bitmap images
    private Bitmap stationaryTile = BitmapFactory.decodeResource(getResources(), R.drawable.blackhole);    
    private Bitmap bombTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_bomb);    
    private Bitmap redTile = BitmapFactory.decodeResource(getResources(), R.drawable.redtile);    
    private Bitmap greenTile = BitmapFactory.decodeResource(getResources(), R.drawable.greentile);    
    private Bitmap blueTile = BitmapFactory.decodeResource(getResources(), R.drawable.bluetile);    
    private Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.foxfurnebulabw);
    private Bitmap startBall = BitmapFactory.decodeResource(getResources(), R.drawable.startball);   
    private Bitmap finishBall = BitmapFactory.decodeResource(getResources(), R.drawable.finishball);   
    private Paint touchedPaint = new Paint();    
    private Rect screenRect;
    
    
    //Bomb Animation variables and bitmaps
    private boolean bombAnimation = false;    
    private float bombX;    
    private float bombY;    
    private Bitmap blastFrame1 = BitmapFactory.decodeResource(getResources(), R.drawable.newblast1);    
    private Bitmap blastFrame2 = BitmapFactory.decodeResource(getResources(), R.drawable.newblast2);    
    private Bitmap blastFrame3 = BitmapFactory.decodeResource(getResources(), R.drawable.newblast3);    
    private ArrayList<Bitmap> bombBlastFrames = new ArrayList<Bitmap>();    
    private CanvasAnimator bombBlast;
    
    
    //Rocket Animation variables and bitmaps
    private boolean rocketAnimation = false;   
    private Bitmap upRocketTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_rocket_new);    
    private Bitmap downRocketTile = RotateBitmap(upRocketTile, 180);    
    private Bitmap leftRocketTile = RotateBitmap(upRocketTile, 270);    
    private Bitmap rightRocketTile = RotateBitmap(upRocketTile, 90);    
    private ArrayList<GameTile> rocketTileAnimationPieces = null;    
    private float ROCKET_MOVEMENT_INCREMENT;        
    private ImageView bgImage;
    
    
    //Tracking Line Variables
	private Paint startPaint = new Paint();	
	private Paint endPaint = new Paint();	
	private Paint linePaint = new Paint();
    
	
    //Sound Variables
    private SoundPool soundPool;    
    private boolean hasSound = true;    
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
		
		backgroundPaint = new Paint();		
		squarePaint = new Paint();		
		playAreaPaint = new Paint();		
		
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
		bombBlastFrames.add(blastFrame1);
		bombBlastFrames.add(blastFrame2);
		bombBlastFrames.add(blastFrame3);
		bombBlast = new CanvasAnimator(bombBlastFrames,BOMB_BLAST_TIME);
		
		//Initialize Paints
		touchedPaint.setAlpha(190);
		squarePaint.setColor(Color.BLUE);
		backgroundPaint.setColor(Color.WHITE);
		playAreaPaint.setColor(Color.WHITE);
		playAreaPaint.setStyle(Paint.Style.STROKE);
		playAreaPaint.setStrokeCap(Paint.Cap.ROUND);
		
		startPaint.setColor(Color.GREEN);
		endPaint.setColor(Color.RED);
		linePaint.setColor(Color.WHITE);
		linePaint.setAlpha(200);
		
		
		
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
		canvas.drawRect(playAreaLeft, playAreaTop, playAreaRight, playAreaBottom, playAreaPaint);			

		//Draw the pattern at the bottom of the screen
		ArrayList<GameTile> localPatternTiles = theGameBoard.thePattern.getTilePattern();
		for (GameTile tile:localPatternTiles){
			drawPatternTile(tile,canvas);
		}
		
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
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex(rocketTileAnimationPieces.get(0).getLocationLeft()),theGameBoard.turnYToHeightIndex(rocketTileAnimationPieces.get(0).getLocationTop()))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
							}
							break;
						case UP:
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex(rocketTileAnimationPieces.get(0).getLocationLeft()),theGameBoard.turnYToHeightIndex((float) (rocketTileAnimationPieces.get(0).getLocationTop()+(0.9)*theGameBoard.getTileSize())))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
							}							
							break;
						case LEFT:
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex((float) (rocketTileAnimationPieces.get(0).getLocationLeft()+(0.9)*theGameBoard.getTileSize())),theGameBoard.turnYToHeightIndex(rocketTileAnimationPieces.get(0).getLocationTop()))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
							}							
							break;
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
				onGameCompleteListener.onGameComplete(startedIntent,numberOfMoves,twoStarMoves,threeStarMoves);				
			}
		}
	}
	
	
	//Stops the game, including the Drawing thread
	public void stopGame(){
		if (drawingThread != null)
			drawingThread.setRunning(false);
		if (gameOver){
			onGameCompleteListener.onGameComplete(startedIntent,numberOfMoves,twoStarMoves,threeStarMoves);
		}
	}
	
	
	public void releaseResources(){		
	}
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	
	//This is called when the surface is first created, so it is where we initiate the drawing thread
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder=holder;
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
			default:
				break;		
			}
		}
	    invalidate();
	    return true;
	  } 
	
	 //For drawing game tiles in play
	 private void drawTile(GameTile tile,Canvas canvas){
		 
		 Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)theGameBoard.getTileSize()+1,(int)tile.getLocationTop()+(int)theGameBoard.getTileSize()+1);
			if (tile.getTileType() == TileType.GAME_TILE){
				switch (tile.getPaintColor()){
					case Color.BLUE:
						if (!tile.isTouched()){
							canvas.drawBitmap(blueTile,null,destSize, null);
						}else{
							canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
						}
						break;
					case Color.RED:
						if (!tile.isTouched()){
							canvas.drawBitmap(redTile,null,destSize, null);
						}else{
							canvas.drawBitmap(redTile,null,destSize, touchedPaint);
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
	 
	 //For drawing the large pattern tiles at the bottom of the screen
	 private void drawPatternTile(GameTile tile,Canvas canvas){
			if (tile.getTileType() == TileType.GAME_TILE){

				Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)tile.getTileSize(),(int)tile.getLocationTop()+(int)tile.getTileSize());
				
				switch (tile.getPaintColor()){
					case Color.BLUE:
						if (!tile.isTouched()){
							canvas.drawBitmap(blueTile,null,destSize, null);
						}else{
							canvas.drawBitmap(blueTile,null,destSize, touchedPaint);
						}
						break;
					case Color.RED:
						if (!tile.isTouched()){
							canvas.drawBitmap(redTile,null,destSize, null);
						}else{
							canvas.drawBitmap(redTile,null,destSize, touchedPaint);
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
	private Bitmap RotateBitmap(Bitmap source, float angle)
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
