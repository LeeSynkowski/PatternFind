/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: OnGameCompleteListener.java
*     Creation Date: 3/07/2014
*            Author: Lee Synkowski
*  
*     This interface allows for notification to the GameActivity that
*     the game in the DrawingView is complete.
*  
*	  Code Review: Code reviewed 3/25/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.baltimorebjj.patternfind;

import android.content.Intent;

public interface OnGameCompleteListener {
	public void onGameComplete(Intent startedIntent,int numberOfMoves, int twoStarMoves, int threeStarMoves);

}