package com.blackware.britannia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;

public class Victory 
{
	public Victory(final Britannia britannia, final boolean gameEnd)
	{
	   	AlertDialog.Builder adb=new AlertDialog.Builder(britannia);
	   	if (gameEnd)
	   		adb.setTitle("Final Tally");
	   	else
	   		adb.setTitle("Tally");
	   	
	   	CharSequence[] n=new CharSequence[britannia.nation.length];
    	for (int i=0; i<n.length; i++)
    	{
    		n[i]=britannia.nation[i].name+": "+britannia.nation[i].victoryPoints;
    	}
    	adb.setItems(n, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
			}});
    	adb.setPositiveButton("View by color", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				
			   	AlertDialog.Builder adb2=new AlertDialog.Builder(britannia);
			   	if (gameEnd)
			   		adb2.setTitle("Final Tally");
			   	else
			   		adb2.setTitle("Tally");
			   	
			   	CharSequence[] n=new CharSequence[5];
				int j=0;
				for (int i=0; i<britannia.nation.length; i++)
					if (britannia.nation[i].pieceColor==Color.BLUE) j+=britannia.nation[i].victoryPoints;
				n[0]="Blue: "+j;
				j=0;
				for (int i=0; i<britannia.nation.length; i++)
					if (britannia.nation[i].pieceColor==Color.GREEN) j+=britannia.nation[i].victoryPoints;
				n[1]="Green: "+j;
				j=0;
				for (int i=0; i<britannia.nation.length; i++)
					if (britannia.nation[i].pieceColor==Color.RED) j+=britannia.nation[i].victoryPoints;
				n[2]="Red: "+j;
				j=0;
				for (int i=0; i<britannia.nation.length; i++)
					if (britannia.nation[i].pieceColor==Color.YELLOW) j+=britannia.nation[i].victoryPoints;
				n[3]="Yellow: "+j;
				j=0;
				for (int i=0; i<britannia.nation.length; i++)
					if (britannia.nation[i].pieceColor==Color.MAGENTA) j+=britannia.nation[i].victoryPoints;
				n[4]="Purple: "+j;
		    	adb2.setItems(n, new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
					}});
		    	adb2.setPositiveButton("Okay", new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						
					}});
				adb2.show();
			}});
    	adb.show();
	}
}