package com.blackware.britannia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;

public class Objectives 
{
	int selected=0;
	int selectedNation=0;
	public Objectives(final Britannia britannia)
	{
		selected=britannia.game.round;
	   	AlertDialog.Builder adb=new AlertDialog.Builder(britannia);
    	adb.setTitle("See the objectives for which nation?");
    	CharSequence[] n=new CharSequence[britannia.nation.length];
    	for (int i=0; i<n.length; i++)
    	{
    		n[i]=britannia.nation[i].name;
    		if (britannia.game.currentNation==britannia.nation[i])
    			selectedNation=i;
    	}
    	adb.setSingleChoiceItems(n, selectedNation, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				selectedNation=which;
			}});
    	adb.setNegativeButton("Cancel", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
			}});
    	adb.setPositiveButton("Choose Nation", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
		    	AlertDialog.Builder adb=new AlertDialog.Builder(britannia);
		    	adb.setTitle("See the objectives for which round?");
		    	CharSequence[] r=new CharSequence[16-britannia.game.round+1];
		    	for (int i=britannia.game.round; i<=16; i++)
		    		r[i-britannia.game.round]=""+i;
		    	adb.setSingleChoiceItems(r, 0, new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						selected=which+britannia.game.round;
					}});		
		    	adb.setPositiveButton("Choose Round", new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						String title=getRoundTitleInfo(britannia,selected);
						String[] info=getRoundInfo(britannia,selected);
		
					   	AlertDialog.Builder adb2=new AlertDialog.Builder(britannia);
					   	adb2.setTitle(title);
					   	adb2.setItems(info, new OnClickListener(){
							public void onClick(DialogInterface dialog, int which) {
							}});
					   	adb2.setPositiveButton("Okay", new OnClickListener(){
							public void onClick(DialogInterface dialog, int which) {
							}});
					   	adb2.show();
					}});
		    	adb.setNegativeButton("Cancel", new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
					}});
		    	adb.show();
			}});
    	adb.show();
	}

   	private String getRoundTitleInfo(Britannia britannia, int round)
	{
		String roundlabel="Round "+round+": ";
		
		if (britannia.nation[selectedNation].majorInvasion(round)) roundlabel+="  major invasion";
		if (britannia.nation[selectedNation].hasBoats(round)) roundlabel+="  boats";
		if (britannia.nation[selectedNation].isRaider(round)) roundlabel+="  raider";
		return roundlabel;
	}

	private String[] getRoundInfo(Britannia britannia, int round)
	{
		String[] roundlabel=new String[50];
		int rsize=0;
		roundlabel[rsize++]="Occupy:";
		for (int region=0; region<britannia.region.length; region++)
			if (britannia.nation[selectedNation].victoryPointDatabase.getOccupyVictoryPoints(round,britannia.region[region].name)>0)
			{
				roundlabel[rsize++]=britannia.region[region].name+" - "+britannia.nation[selectedNation].victoryPointDatabase.getOccupyVictoryPoints(round,britannia.region[region].name);
			}
		
		roundlabel[rsize++]="Hold:";
		for (int region=0; region<britannia.region.length; region++)
			if (britannia.nation[selectedNation].victoryPointDatabase.getHoldVictoryPoints(round,britannia.region[region].name)>0)
			{
				roundlabel[rsize++]=britannia.region[region].name+" - "+britannia.nation[selectedNation].victoryPointDatabase.getHoldVictoryPoints(round,britannia.region[region].name);
			}
		
		roundlabel[rsize++]="Kill:";
		for (int nation=0; nation<britannia.nation.length; nation++)
			if (britannia.nation[selectedNation].victoryPointDatabase.getKillVictoryPoints(round,britannia.nation[nation])>0)
			{
				roundlabel[rsize++]=britannia.nation[nation].name+" - "+britannia.nation[selectedNation].victoryPointDatabase.getKillVictoryPoints(round,britannia.nation[nation]);
			}
		if (britannia.nation[selectedNation].victoryPointDatabase.getBurnFortVictoryPoints(round)>0)
			roundlabel[rsize++]="Roman Forts - "+britannia.nation[selectedNation].victoryPointDatabase.getBurnFortVictoryPoints(round);
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Boudicca")>0)
			roundlabel[rsize++]="Boudicca - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Boudicca");
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Aelle")>0)
			roundlabel[rsize++]="Aelle - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Aelle");
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Arthur")>0)
			roundlabel[rsize++]="Arthur - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Arthur");
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Ivar and Halfdan")>0)
			roundlabel[rsize++]="Ivar and Halfdan - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Ivar and Halfdan");
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Harald Hardrada")>0)
			roundlabel[rsize++]="Harald Hardrada - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Harald Hardrada");
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Svein Estrithson")>0)
			roundlabel[rsize++]="Svein Estrithson - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Svein Estrithson");
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Harold")>0)
			roundlabel[rsize++]="Harold - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"Harold");
		if (britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"William")>0)
			roundlabel[rsize++]="William - "+britannia.nation[selectedNation].victoryPointDatabase.getKillLeaderVictoryPoints(round,"William");


		String[] roundlabel2=new String[rsize];
		for (int i=0; i<rsize; i++)
			roundlabel2[i]=roundlabel[i];
		return roundlabel2;		
	}
}
