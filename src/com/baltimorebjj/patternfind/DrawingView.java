package com.baltimorebjj.patternfind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;


//using the techniques of the dietel book to follow to attempt this

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback{
	
	private DrawingThread drawingThread;
	
	private Paint backgroundPaint;
	private Paint squarePaint;
	private Paint playAreaPaint;
	
	private int screenWidth;
	private int screenHeight;
	private int tileSize;
	
	private boolean gameOver = true;
	
	private float pitch_angle = 0;
	private float roll_angle = 0;

	private float playAreaTop;
	private float playAreaLeft;
	private float playAreaBottom;
	private float playAreaRight;
	
	private TurnEvent turnEvent;
	private TurnEvent previousTurnEvent;
	
	private GameBoard theGameBoard = null;
	
	private ArrayList<GameTile> activeTiles;
	private ArrayList<GameTile> patternTiles;
	private ArrayList<GameTile> movingTiles;
	
	private int score = 0;
	private int pointsPossible = 0;
	
	private int numberOfNextBlocks = 5;
	
	private Random rng = new Random();
	
	//Measure frames per second.
    private long now;
    private int framesCount=0;
    private int framesCountAvg=0;
    private long framesTimer=0;

    //Frame speed
    private long timeNow;
    private long timePrevFrame = 0;
    private long timeDelta;	
    private final int FrameRateConstant = 80; //smaller is faster
    
    private int level;
    
    private SurfaceHolder mSurfaceHolder = null;
    
    
    //bitmap images
    private Bitmap upArrow = BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow);
    private Bitmap downArrow = BitmapFactory.decodeResource(getResources(), R.drawable.down_arrow);
    private Bitmap leftArrow = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow);
    private Bitmap rightArrow = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow);
    private Bitmap circle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
    private Bitmap stationaryTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_stationary);
    private Bitmap bombTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_bomb);
    private Bitmap redTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_yellow);
    private Bitmap greenTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_green);
    private Bitmap blueTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_blue);
    private Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    
    private Paint touchedPaint = new Paint();
    private Rect screenRect;
    
    //Bomb Animation
    private boolean bombAnimation = false;
    private float bombX;
    private float bombY;
    private Bitmap blastFrame1 = BitmapFactory.decodeResource(getResources(), R.drawable.blast1);
    private Bitmap blastFrame2 = BitmapFactory.decodeResource(getResources(), R.drawable.blast2);
    private Bitmap blastFrame3 = BitmapFactory.decodeResource(getResources(), R.drawable.blast3);
    private ArrayList<Bitmap> bombBlastFrames = new ArrayList<Bitmap>();
    CanvasAnimator bombBlast;
    
    //Rocket Animation
    private boolean rocketAnimation = false;
    private int rocketX;
    private int rocketY;
    private Bitmap upRocketTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile_rocket);
    private Bitmap downRocketTile = RotateBitmap(upRocketTile, 180);
    private Bitmap leftRocketTile = RotateBitmap(upRocketTile, 270);
    private Bitmap rightRocketTile = RotateBitmap(upRocketTile, 90);
    private Orientation rocketTileOrientation = null;
    private ArrayList<GameTile> rocketTileAnimationPieces = null;
    private float ROCKET_MOVEMENT_INCREMENT;
    
    private long timerAmount = 10000;
    
    private GameTimer timer;
    
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
    
    private ImageView bgImage;
    private AnimationDrawable bombAnimationGraphics;

    

    
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		
		//this.level = lvl;
		
		backgroundPaint = new Paint();
		squarePaint = new Paint();
		playAreaPaint = new Paint();
		
		turnEvent = TurnEvent.INACTIVE;
		previousTurnEvent = TurnEvent.INACTIVE;
		
		
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
		
		//initialize animation views
		bgImage = (ImageView)findViewById(R.drawable.blast3);		

		//set up bomb blast animation
		bombBlastFrames.add(blastFrame1);
		bombBlastFrames.add(blastFrame2);
		bombBlastFrames.add(blastFrame3);
		bombBlast = new CanvasAnimator(bombBlastFrames,0.25);
		
		//initialize touched paint
		touchedPaint.setAlpha(190);
	} //end constructor
	
	@Override
	protected void onSizeChanged(int w,int h,int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		
		//store for use
		screenWidth = w;
		screenHeight = h;
		tileSize = w/10;
		
		//initialize background dimensions
		screenRect = new Rect(0,0,screenWidth,screenHeight);
		//initialize paints
		squarePaint.setColor(Color.BLUE);
		backgroundPaint.setColor(Color.WHITE);
		playAreaPaint.setColor(Color.BLACK);
		playAreaPaint.setStyle(Paint.Style.STROKE);
		playAreaPaint.setStrokeWidth(2);
		
		//initialize play area
		playAreaTop = h/10;
		playAreaBottom = (h/10)+(11 * tileSize);
		playAreaLeft = w/20; //1/2 of 1/10 the width
		playAreaRight = w - (w/20);
		
		//initialize the gameboard
		//here is where we need to know the level
		
		//theGameBoard = new GameBoard(9,11,screenWidth,screenHeight);
		//theGameBoard.populateBoard(4);
		//
		//initialize various components
		ROCKET_MOVEMENT_INCREMENT = tileSize;
		
		//initialize the pattern
		//thePattern = new Pattern(screenWidth,blockSize);
		
		
		
		//start a new game
		newGame();
	}

	public void newGame(){
		
		//try to get everything we need to start the game here
		
		//this is where we deal with the level
		theGameBoard = new GameBoard(9,11,screenWidth,screenHeight);
		theGameBoard.populateBoard(40);
		
		
		timer = new GameTimer(timerAmount);
		timer.start();
		
		drawingThread = new DrawingThread(getHolder());
		drawingThread.setRunning(true);
		drawingThread.start();
		
		if (gameOver){
			//timer.start();
			gameOver = false;
			//drawingThread = new DrawingThread(getHolder());
			//drawingThread.start();
		}
		
	}
	
	//use to move pieces around and make logical checks
	private void updatePositions(){
		//
		//additional protection checking status of last turn event so we don't try to process twice
		
		/*
		if (((turnEvent == TurnEvent.INACTIVE)&&(theGameBoard.isAnimationComplete()))||
				(theGameBoard.isTilesInFinalState()&&(theGameBoard.isAnimationComplete()))){
		*/
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
				//if the blocks are in final state and the previous turn event
				//was low pitch, do nothing
				//I think we can get rid of this
				/*
				if (theGameBoard.isTilesInFinalState()&&(previousTurnEvent==TurnEvent.LOW_PITCH)){
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.LOW_PITCH;
				} else {
					if (previousTurnEvent==turnEvent){
						//Low pitch subtracts one from height
						theGameBoard.incrementTileMoveCounter();
						if (theGameBoard.getTileMoveCounter() >= theGameBoard.getNumberOfSquaresInHeight()){
							theGameBoard.setTilesInFinalState(true);
						}					
					} else{
						theGameBoard.resetTileMoveCounter();
						theGameBoard.setTilesInFinalState(false);
					}
					theGameBoard.handleLowPitch(pitch_angle);
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.LOW_PITCH;
				}
				*/
				theGameBoard.handleLowPitch(pitch_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.LOW_PITCH;
				break;
			
			case HIGH_PITCH:
				/*
				if (theGameBoard.isTilesInFinalState()&&(previousTurnEvent==TurnEvent.HIGH_PITCH)){
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.HIGH_PITCH;
				} else {
					if (previousTurnEvent==turnEvent){
						//Low pitch subtracts one from height
						theGameBoard.incrementTileMoveCounter();
						if (theGameBoard.getTileMoveCounter() >= theGameBoard.getNumberOfSquaresInHeight()){
							theGameBoard.setTilesInFinalState(true);
						}					
					} else{
						theGameBoard.resetTileMoveCounter();
						theGameBoard.setTilesInFinalState(false);
					}
					theGameBoard.handleHighPitch(pitch_angle);
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.HIGH_PITCH;
				}
				*/
				theGameBoard.handleHighPitch(pitch_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.HIGH_PITCH;
				break;

			case LOW_ROLL:
				/*
				if (theGameBoard.isTilesInFinalState()&&(previousTurnEvent==TurnEvent.LOW_ROLL)){
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.LOW_ROLL;
				} else {
					if (previousTurnEvent==turnEvent){
						//Low pitch subtracts one from height
						theGameBoard.incrementTileMoveCounter();
						if (theGameBoard.getTileMoveCounter() >= theGameBoard.getNumberOfSquaresInWidth()){
							theGameBoard.setTilesInFinalState(true);
						}					
					} else{
						theGameBoard.resetTileMoveCounter();
						theGameBoard.setTilesInFinalState(false);
					}
					theGameBoard.handleLowRoll(roll_angle);
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.LOW_ROLL;
				}
				*/
				theGameBoard.handleLowRoll(roll_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.LOW_ROLL;
				break;
				
			case HIGH_ROLL:
				/*
				if (theGameBoard.isTilesInFinalState()&&(previousTurnEvent==TurnEvent.HIGH_ROLL)){
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.HIGH_ROLL;
				} else {
					if (previousTurnEvent==turnEvent){
						//Low pitch subtracts one from height
						theGameBoard.incrementTileMoveCounter();
						if (theGameBoard.getTileMoveCounter() >= theGameBoard.getNumberOfSquaresInWidth()){
							theGameBoard.setTilesInFinalState(true);
						}					
					} else{
						theGameBoard.resetTileMoveCounter();
						theGameBoard.setTilesInFinalState(false);
					}
					theGameBoard.handleHighRoll(roll_angle);
					turnEvent = TurnEvent.INACTIVE;
					previousTurnEvent = TurnEvent.HIGH_ROLL;
				}
				break;
				*/
				theGameBoard.handleHighRoll(roll_angle);
				turnEvent = TurnEvent.INACTIVE;
				previousTurnEvent = TurnEvent.HIGH_ROLL;
			case INACTIVE:
				break;
		}
		theGameBoard.setLastTurnEvent(previousTurnEvent);
	}
	
	//use to draw the game to the given canvas
	public synchronized void drawGameElements(Canvas canvas){
			
		//clear the background
		canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),backgroundPaint);
		canvas.drawBitmap(background, null, screenRect, null);
		
		
		//draw the background rectangle
		canvas.drawRect(playAreaLeft, playAreaTop, playAreaRight, playAreaBottom, playAreaPaint);
		
		//here is where the gameboard returns the active pieces arraylist
		activeTiles = theGameBoard.getActiveTiles();
		patternTiles = theGameBoard.thePattern.getTilePattern();
		movingTiles = theGameBoard.getMovingTiles();
		
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
		for (int tileIndex=0;tileIndex<patternTiles.size();tileIndex++){
			drawTile(patternTiles.get(tileIndex),canvas,outlinePaint,highlightPaint);
		}
		
		
		//iterate through the activeTile arrayList and draw all those pieces
		for (int tileIndex=0;tileIndex<activeTiles.size();tileIndex++){
			drawTile(activeTiles.get(tileIndex),canvas,outlinePaint,highlightPaint);
		}
		
		//draw the moving tiles
		if ((!theGameBoard.isAnimationComplete())){
			
			for (int tileIndex=0;tileIndex<movingTiles.size();tileIndex++){
				
				GameTile tile = movingTiles.get(tileIndex);
				
				if (previousTurnEvent == TurnEvent.HIGH_PITCH){
					tile.setLocationTop(tile.getLocationTop()-theGameBoard.getMovementIncriment());	
				}
				
				if (previousTurnEvent == TurnEvent.LOW_PITCH){					
					tile.setLocationTop(tile.getLocationTop()+theGameBoard.getMovementIncriment());
				}
				
				if (previousTurnEvent == TurnEvent.HIGH_ROLL){
					tile.setLocationLeft(tile.getLocationLeft()-theGameBoard.getMovementIncriment());
				}
				
				if (previousTurnEvent == TurnEvent.LOW_ROLL){
					tile.setLocationLeft(tile.getLocationLeft()+theGameBoard.getMovementIncriment());
				}
				//draw the tile
				drawTile(tile,canvas,outlinePaint,highlightPaint);
			}
			if (theGameBoard.isTileSequenceContinuous()){
				theGameBoard.adjustPointArray();
			}else{
				theGameBoard.clearTileSequence();
			}
			theGameBoard.animationStepOccured();
		} 
		if (bombAnimation){
					if (!bombBlast.isRunning()){
						bombBlast.start();
					}
					Rect destRect = new Rect((int)bombX-tileSize,(int)bombY-tileSize,(int)bombX+(2*tileSize),(int)bombY+(2*tileSize));
					Bitmap currentFrame = bombBlast.getCurrentFrame();
					if (currentFrame!=null){
						canvas.drawBitmap(currentFrame,null,destRect, null);
					}else{
						bombAnimation = false;
						//theGameBoard.setAnimationComplete(true);
					}
		} 
		if (rocketAnimation){
					//get the rocket tile animation pieces if this is the first time through
					if (rocketTileAnimationPieces == null){
						rocketTileAnimationPieces = theGameBoard.getRocketTiles();
					}
					
					//check if the rocket is complete
					switch (rocketTileAnimationPieces.get(0).getOrientation()){ 
						case DOWN:
						case RIGHT:
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex(rocketTileAnimationPieces.get(0).getLocationLeft()),theGameBoard.turnYToHeightIndex(rocketTileAnimationPieces.get(0).getLocationTop()))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
								//theGameBoard.setAnimationComplete(true);
							}
							break;
						case UP:
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex(rocketTileAnimationPieces.get(0).getLocationLeft()),theGameBoard.turnYToHeightIndex((float) (rocketTileAnimationPieces.get(0).getLocationTop()+(0.9)*theGameBoard.getTileSize())))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
								//theGameBoard.setAnimationComplete(true);
							}							
							break;
						case LEFT:
							if (theGameBoard.isRocketDone(theGameBoard.turnXToWidthIndex((float) (rocketTileAnimationPieces.get(0).getLocationLeft()+(0.9)*theGameBoard.getTileSize())),theGameBoard.turnYToHeightIndex(rocketTileAnimationPieces.get(0).getLocationTop()))){
								rocketTileAnimationPieces = null;
								rocketAnimation = false;
								//theGameBoard.setAnimationComplete(true);
							}							
							break;
					}
					
					//see animation is still necessary
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
						//check if the rocket has crossed with the next tile in its path
					
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

						//draw the pieces
						for (int tileIndex=0;tileIndex!=rocketTileAnimationPieces.size();tileIndex++){
							drawTile(rocketTileAnimationPieces.get(tileIndex),canvas,outlinePaint,highlightPaint);
						}
			

					
					}//end of second if check
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
			
			canvas.drawLines(pointArray, linePaint);
			tempTile = touchedTiles.get(touchedTiles.size()-1);
			canvas.drawCircle(tempTile.getLocationLeft()+(theGameBoard.getTileSize()/2), 
						      tempTile.getLocationTop()+(theGameBoard.getTileSize()/2), 
						      (theGameBoard.getTileSize()/6), 
						      endPaint);
		}
		
		
		//the remove button, or any other UI type stuff is here
		//CanvasButton removeButton = theGameBoard.getRemoveButton();
		
		/*
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
		*/
		//draw the tilt arrow
		/*
		if (Math.abs(pitch_angle)<20  && Math.abs(roll_angle)<20){
			canvas.drawBitmap(circle, screenWidth/10, theGameBoard.getRemoveButton().getButtonTop(), null);
		} else if ((pitch_angle < -20)){
			canvas.drawBitmap(downArrow, screenWidth/10, theGameBoard.getRemoveButton().getButtonTop(), null);
			
		}else if ((pitch_angle > 20)){
			canvas.drawBitmap(upArrow, screenWidth/10, theGameBoard.getRemoveButton().getButtonTop(), null);
			
		}else if ((roll_angle > 20)){
			canvas.drawBitmap(leftArrow, screenWidth/10, theGameBoard.getRemoveButton().getButtonTop(), null);
			
		}else if ((roll_angle < -20)){
			canvas.drawBitmap(rightArrow, screenWidth/10, theGameBoard.getRemoveButton().getButtonTop(), null);
		}
		*/
		//draw the score
		Paint scorePaint = new Paint();
		scorePaint.setColor(Color.BLACK);
		scorePaint.setTextSize(20);
		
		/*
		pointsPossible = theGameBoard.determinePossiblePoints(touchedTiles.size());
		canvas.drawText("Pts Poss: " + pointsPossible, 7*screenWidth/10, removeButton.getButtonTop(), scorePaint);
		canvas.drawText("Score: "+ score, 7*screenWidth/10, removeButton.getButtonTop()+40, scorePaint);
		*/
		
		//game over message
		if (gameOver){
			Paint blockPaint = new Paint();
			blockPaint.setTextSize(50);
			blockPaint.setColor(Color.BLACK);
			canvas.drawText("GAME OVER", 100, screenHeight/2, blockPaint);
			stopGame();
		}
		
		
		//measure frame rate
		now=System.currentTimeMillis();
		outlinePaint.setStrokeWidth(1);
		outlinePaint.setTextSize(20);
		
		//checking timer
        //canvas.drawText(framesCountAvg+" fps"+" p " + pitch_angle + " r " + roll_angle + " sec " + timer.getSecondsRemaining(), 40, 70, outlinePaint);
		canvas.drawText("Secs til Blocks " + (float)(timer.getMilliSecondsRemaining()/1000), 40, 70, outlinePaint);
        framesCount++;
        if(now-framesTimer>1000) {
                framesTimer=now;
                framesCountAvg=framesCount;
                framesCount=0;
        }
	}
	
	public void gameLogic(Canvas canvas){
		
		/*
		if (!timer.isRunning()){		
			timer.setTimerLength(timerAmount);
			timer.start();
			if (theGameBoard.numberOfSpotsRemaining() < numberOfNextBlocks){
				//add animation to fill last remaining spots
				gameOver = true;
			} else {
				if (hasSound){
					soundPool.play(soundMap.get(NEW_BLOCKS_ID), 1, 1, 1, 0, 1f);
				}
				theGameBoard.addNewPieces(numberOfNextBlocks);
				theGameBoard.setTilesInFinalState(false);
				theGameBoard.resetTileMoveCounter();
				
			}
			
		}
		*/
		if (theGameBoard != null){
			if (theGameBoard.getActiveTiles().size()==0){
			//game over state
				gameOver = true;
			}
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
		//can we just get a reference to the holder and start the drawing thread later
		mSurfaceHolder=holder;
		/*
		drawingThread = new DrawingThread(holder);
		drawingThread.setRunning(true);
		drawingThread.start();
		*/
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
                if ( timeDelta < FrameRateConstant) {
                    try {
                        Thread.sleep(160 - FrameRateConstant);
                    }
                    catch(InterruptedException e) {

                    }
                }
                timePrevFrame = System.currentTimeMillis();
				
				
				try{
					canvas = surfaceHolder.lockCanvas(null);
					
							gameLogic(canvas);
					
							updatePositions();
							//lock the surface for drawing
							synchronized(surfaceHolder){
								drawGameElements(canvas);	
					}
					
				}catch(NullPointerException e){
					//gotta put this in here to catch the initial null conditions
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
		/*
		if (((event.getAction()==MotionEvent.ACTION_DOWN)&&(theGameBoard.isAnimationComplete()))||
			(((event.getAction()==MotionEvent.ACTION_MOVE)&&(theGameBoard.isAnimationComplete())))){
		*/
		if (((event.getAction()==MotionEvent.ACTION_DOWN)||(event.getAction()==MotionEvent.ACTION_MOVE))&&(!bombAnimation)&&(!rocketAnimation)){
			int numberOfTiles = theGameBoard.getTileSequence().size();
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
				score = score + theGameBoard.determinePossiblePoints(numberOfTiles);
				theGameBoard.setTilesInFinalState(false);
				theGameBoard.resetTileMoveCounter();
				if (hasSound){
					soundPool.play(soundMap.get(REMOVE_BLOCKS_ID), 1, 1, 1, 0, 1f);
				}
				break;
			case REMOVE_BUTTON_INCORRECT:
				if (!theGameBoard.isMoving()){
					if (hasSound){
						soundPool.play(soundMap.get(PATTERN_ERROR_ID), 1, 1, 1, 0, 1f);
					}
				}
				break;
			case BOMB_TILE:
				//play bomb sound
				if (hasSound){
					soundPool.play(soundMap.get(BOMB_ID), 1, 1, 1, 0, 1f);
				}
				//initiate conditions for bomb animation which are handled in the drawloop
				bombAnimation= true;
				bombX = theGameBoard.getLeftPosition()+ (theGameBoard.getTileSize() * theGameBoard.turnXToWidthIndex(event.getX()));
				bombY = theGameBoard.getTopPosition() + (theGameBoard.getTileSize() * theGameBoard.turnYToHeightIndex(event.getY()));
				//theGameBoard.setAnimationComplete(false);
				break;
			case ROCKET_TILE:
				//play blast off sound
				if (hasSound){
					soundPool.play(soundMap.get(ROCKET_SOUND_ID), 1, 1, 1, 0, 1f);
				}
				//initiate conditions for rocket animation
				rocketAnimation= true;
				//theGameBoard.setAnimationComplete(false);
				break;			
			default:
				break;		
			}
		}
	    invalidate();
	    return true;
	  } 
	
	 private void drawTile(GameTile tile,Canvas canvas,Paint outlinePaint,Paint highlightPaint){
			if (tile.getTileType() == TileType.GAME_TILE){
				/*
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
				*/
				Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)theGameBoard.getTileSize(),(int)tile.getLocationTop()+(int)theGameBoard.getTileSize());
				
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
			if (tile.getTileType() == TileType.STATIONARY_TILE){
				Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)theGameBoard.getTileSize(),(int)tile.getLocationTop()+(int)theGameBoard.getTileSize());
				canvas.drawBitmap(stationaryTile,null,destSize, null);
			}
			if (tile.getTileType() == TileType.BOMB_TILE){
				Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)theGameBoard.getTileSize(),(int)tile.getLocationTop()+(int)theGameBoard.getTileSize());
				canvas.drawBitmap(bombTile,null,destSize, null);
			}
			if (tile.getTileType() == TileType.ROCKET_TILE){
				Rect destSize = new Rect((int)tile.getLocationLeft(),(int)tile.getLocationTop(),(int)tile.getLocationLeft()+(int)theGameBoard.getTileSize(),(int)tile.getLocationTop()+(int)theGameBoard.getTileSize());
				if (tile.getOrientation()==Orientation.UP){
					canvas.drawBitmap(upRocketTile,null,destSize, null);
				} else if (tile.getOrientation() == Orientation.DOWN){
					canvas.drawBitmap(downRocketTile,null,destSize, null);
				} else if (tile.getOrientation() == Orientation.LEFT){
					canvas.drawBitmap(leftRocketTile,null,destSize, null);
				} else if (tile.getOrientation() == Orientation.RIGHT){
					canvas.drawBitmap(rightRocketTile,null,destSize, null);
				}
			}
	 }
	 
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

}
