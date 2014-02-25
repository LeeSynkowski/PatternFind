package com.baltimorebjj.patternfind;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;

public class Level {
	

	private int numberOfSquaresInWidth;
	private int numberOfSquaresInHeight;
	private float topPosition;
	private float leftPosition;
	
	private int levelNumber;
	
	private GameTile[][] theLevel;
	
	private Context mContext;
	
	public Level(int ln,Context context) throws XmlPullParserException, IOException{
		levelNumber = ln;
		inflateLevel();
		mContext = context;
	}
	
	public int getNumberOfSquaresInWidth() {
		return numberOfSquaresInWidth;
	}

	public int getNumberOfSquaresInHeight() {
		return numberOfSquaresInHeight;
	}
	
	private void inflateLevel() throws XmlPullParserException, IOException{
		XmlResourceParser xrp = mContext.getResources().getXml(R.xml.level_data);		

		boolean processingXML = true;
		//read through everything until we get to the level we want
		do{
			xrp.next();
			if ((xrp.getEventType()==XmlPullParser.START_TAG)&&(xrp.getName()=="level_number")){
				xrp.next();
				if (levelNumber == Integer.valueOf(xrp.getText())){
					xrp.next();//closing level_number tag
					xrp.next();//opening height tag
					xrp.next();//height data
					numberOfSquaresInHeight = Integer.valueOf(xrp.getText());
					xrp.next();//closing height tag
					xrp.next();//opening width tag
					xrp.next();//width data
					numberOfSquaresInWidth = Integer.valueOf(xrp.getText());
					xrp.next();//closing height tag
					processingXML = false;
				}
			}
		} while (processingXML);
		
		theLevel = new GameTile[numberOfSquaresInWidth][numberOfSquaresInHeight];
		
		String rowData;
		for (int y=0;y<numberOfSquaresInHeight;y++){
			xrp.next();//opening row tag
			xrp.next();//data
			rowData = xrp.getText();
			
			for (int x=0;x<rowData.length();x++){
				theLevel[x][y] = getTileFromChar(rowData.charAt(x)); 
			}
			xrp.next();//closing row tag
		}
	}

	private GameTile getTileFromChar(char charAt) {
		GameTile currentTile = new GameTile();
		currentTile.setOccupied(true);
		/*
		currentTile.setLocationTop(topPosition + (testHeight*tileSize));
		currentTile.setLocationLeft(leftPosition + (testWidth*tileSize));
		
		int paintColor = randGen.nextInt(3);
		
		if (paintColor == 0){
			currentTile.setPaintColor(Color.BLUE);
		}
		if (paintColor == 1){
			currentTile.setPaintColor(Color.RED);
		}
		if (paintColor == 2){
			currentTile.setPaintColor(Color.GREEN);
		}
		return null;
		
		*/
		return null;
	}

	public GameTile[][] getGameBoard() {
		// TODO Auto-generated method stub
		return null;
	}

}
