/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: TouchEvent.java
*     Creation Date: 10/07/2013
*            Author: Lee Synkowski
*  
*	  Enum type for the different types of screen touch events:
*	  REMOVE_BUTTON_CORRECT,REMOVE_BUTTON_INCORRECT,NOTHING,
*	  ADJUST_TILE_SEQUENCE,BOMB_TILE,ROCKET_TILE
*  
*	  Code Review: Code reviewed 3/25/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.baltimorebjj.patternfind;

public enum TouchEvent {
	REMOVE_BUTTON_CORRECT,
	REMOVE_BUTTON_INCORRECT,
	NOTHING,
	ADJUST_TILE_SEQUENCE,
	BOMB_TILE,
	ROCKET_TILE,
	RESET
}
