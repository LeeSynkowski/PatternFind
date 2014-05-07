/*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*                    PATTERN FIND
*  	                      
*                                                                       
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   
*              Name: LevelData.java
*     Creation Date: 3/19/2014
*            Author: Lee Synkowski
*  
*       This class is used to unpack level data from the associated
*       XML data file so it can be returned to the GameBoard for display.
*  
* 
*	Code Review:	Code reviewed 3/24/2014 by Lee Synkowski
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.baltimorebjj.patternfind;

public class LevelData {

	public int levelNumber;
	public int numberOfStars;
	public boolean playable;
	
	public LevelData(int levelNumber,int numberOfStars,boolean playable){
		this.levelNumber = levelNumber;
		this.numberOfStars = numberOfStars;
		this.playable = playable;
	}
}
