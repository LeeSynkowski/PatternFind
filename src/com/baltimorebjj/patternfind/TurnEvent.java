/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: TurnEvent.java
*     Creation Date: 9/11/2013
*            Author: Lee Synkowski
*  
*	  Enum type for the different types of turn events events:
*	  LOW_PITCH,HIGH_PITCH,LOW_ROLL,HIGH_ROLL, and INACTIVE
*  
*	  Code Review: Code reviewed 3/25/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.baltimorebjj.patternfind;

public enum TurnEvent {
	LOW_PITCH,
	HIGH_PITCH,
	LOW_ROLL,
	HIGH_ROLL,
	INACTIVE
}
