package com.blackware.britannia;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class Britannia extends Activity 
{
	public Board board;
	public Region[] region;
	public Nation[] nation;
	public Game game;
	public Random random;
	public boolean started=false;
	private int selected=0;
	private Britannia britannia=this;
	private SelectView sv;

	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
		random=new Random();
		setupRegions();
		setupNations();

		board=new Board(this);
		board.makeBoard(this);

		game=new Game(this);

	   	AlertDialog.Builder adb=new AlertDialog.Builder(this);
    	adb.setTitle("Choose your color");
    	adb.setSingleChoiceItems(new CharSequence[]{"Blue","Green","Yellow","Red"}, 0, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				selected=which;
			}});

    	adb.setPositiveButton("Choose", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				for (int n=0; n<nation.length; n++)
					if (nation[n].pieceColor!=new int[]{Color.BLUE,Color.GREEN,Color.YELLOW,Color.RED}[selected])
						nation[n].addAI();
				
 					Runnable runGame=new Runnable(){ public void run() { startGame(); } };
					new Thread(runGame, "Timer").start();
 			}});
    	adb.setNegativeButton("Select nations manually", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
			   	AlertDialog.Builder adb2=new AlertDialog.Builder(britannia);
			   	sv=new SelectView(adb2);
			   	adb2.setPositiveButton("Choose", new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						int humancolor=new int[]{Color.BLUE,Color.GREEN,Color.RED,Color.YELLOW,Color.MAGENTA}[sv.human.getSelectedItemPosition()];
						for (int n=0; n<nation.length; n++)
						{
							nation[n].pieceColor=new int[]{Color.BLUE,Color.GREEN,Color.RED,Color.YELLOW,Color.MAGENTA}[sv.nationcolor[n].getSelectedItemPosition()];
							if (nation[n].pieceColor!=humancolor)
								nation[n].addAI();
						}
   						Runnable runGame=new Runnable(){ public void run() { startGame(); } };
						new Thread(runGame, "Timer").start();
 						
					}
			   	});
			   	adb2.show();
				
			}});
    	adb.show();
    }

	public void startGame()
	{
		Looper.prepare();

		board.setControlButton("Start Game");
		game.phaseLock.lockWait();
		started=true;
		board.setControlButton("");
		game.doGame();
	}
	
	private class SelectView
	{
		Spinner human;
		Spinner[] nationcolor;
		
		public SelectView(AlertDialog.Builder adb2) 
		{
			ScrollView s1=new ScrollView(britannia);
			LinearLayout l1=new LinearLayout(britannia);
    		l1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
    		l1.setOrientation(LinearLayout.VERTICAL);
			
			LinearLayout l2=new LinearLayout(britannia);
    		l2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
    		l2.setOrientation(LinearLayout.HORIZONTAL);
			
    		TextView t=new TextView(britannia);
    		t.setText("Human plays ");
    		l2.addView(t);
    		
    		human=new Spinner(britannia);
			human.setAdapter(new ArrayAdapter<String>(britannia,android.R.layout.simple_spinner_item,new String[]{"Blue","Green","Red","Yellow","Purple"}));
			human.setSelection(0);
			l2.addView(human);
			l1.addView(l2);
    		
    		nationcolor=new Spinner[nation.length];
			for (int i=0; i<nation.length; i++)
			{
				l2=new LinearLayout(britannia);
	    		l2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	    		l2.setOrientation(LinearLayout.HORIZONTAL);
				
	    		t=new TextView(britannia);
	    		t.setText(nation[i].name);
	    		l2.addView(t);
	    		
	    		nationcolor[i]=new Spinner(britannia);
				nationcolor[i].setAdapter(new ArrayAdapter<String>(britannia,android.R.layout.simple_spinner_item,new String[]{"Blue","Green","Red","Yellow","Purple"}));
				int position=0;
				if (nation[i].pieceColor==Color.BLUE)
					position=0;
				else if (nation[i].pieceColor==Color.GREEN)
					position=1;
				else if (nation[i].pieceColor==Color.RED)
					position=2;
				else if (nation[i].pieceColor==Color.YELLOW)
					position=3;
				nationcolor[i].setSelection(position);
				
				l2.addView(nationcolor[i]);
				l1.addView(l2);
			}
			
			s1.addView(l1);
			adb2.setView(s1);
		}
	}

	public void setupRegions()
	{
		region=new Region[43];
		region[0]=new Region(this,"Cornwall",615,1599,new String[]{"Devon","Dyfed","English Channel","Atlantic Ocean"},1);
		region[1]=new Region(this,"Devon",733,1544,new String[]{"Cornwall","Avalon","Wessex","Gwent","English Channel","Atlantic Ocean"},1);
		region[2]=new Region(this,"Avalon",831,1481,new String[]{"Hwicce","Downlands","Wessex","Devon","Atlantic Ocean"},0);
		region[3]=new Region(this,"Wessex",958,1500,new String[]{"Devon","Avalon","Downlands","Sussex","English Channel"},0);
		region[4]=new Region(this,"Sussex",1139,1488,new String[]{"Wessex","Downlands","Essex","Kent","English Channel"},0);
		region[5]=new Region(this,"Essex",1153,1393,new String[]{"Downlands","Sussex","South Mercia","Suffolk","Kent","Frisian Sea"},0);
		region[6]=new Region(this,"Downlands",986,1434,new String[]{"Wessex","Avalon","Hwicce","South Mercia","Essex","Sussex"},1);
		region[7]=new Region(this,"Hwicce",902,1338,new String[]{"Avalon","Gwent","Powys","March","North Mercia","South Mercia","Downlands","Atlantic Ocean"},0);
		region[8]=new Region(this,"Kent",1230,1442,new String[]{"Sussex","Essex","English Channel","Frisian Sea"},0);
		region[9]=new Region(this,"Suffolk",1179,1292,new String[]{"Essex","South Mercia","North Mercia","Lindsey","Norfolk","Frisian Sea"},0);
		region[10]=new Region(this,"Norfolk",1239,1183,new String[]{"Suffolk","Lindsey","Frisian Sea"},0);
		region[11]=new Region(this,"South Mercia",1035,1360,new String[]{"Downlands","Hwicce","North Mercia","Suffolk","Essex"},0);
		region[12]=new Region(this,"North Mercia",1000,1232,new String[]{"South Mercia","Hwicce","March","York","Lindsey","Suffolk"},0);
		region[13]=new Region(this,"Lindsey",1105,1151,new String[]{"Norfolk","Suffolk","North Mercia","York","Frisian Sea"},1);
		region[14]=new Region(this,"March",894,1169,new String[]{"Hwicce","Gwent","Powys","Clwyd","Cheshire","York","North Mercia"},0);
		region[15]=new Region(this,"York",1056,989,new String[]{"March","Lindsey","North Mercia","Cheshire","Pennines","Bernicia","Frisian Sea","North Sea"},0);
		region[16]=new Region(this,"Cheshire",879,1073,new String[]{"Cumbria","Pennines","York","March","Clwyd","Atlantic Ocean","Irish Sea"},0);
		region[17]=new Region(this,"Pennines",933,958,new String[]{"York","Cheshire","Cumbria","Bernicia","Lothian"},1);
		region[18]=new Region(this,"Cumbria",827,887,new String[]{"Galloway","Lothian","Pennines","Cheshire","Irish Sea"},0);
		region[19]=new Region(this,"Bernicia",1009,887,new String[]{"York","Pennines","Lothian","North Sea"},0);
		region[20]=new Region(this,"Galloway",700,788,new String[]{"Strathclyde","Lothian","Cumbria","Irish Sea"},1);
		region[21]=new Region(this,"Lothian",873,719,new String[]{"Bernicia","Pennines","Cumbria","Galloway","Strathclyde","Dunedin","North Sea"},0);
		region[22]=new Region(this,"Dyfed",663,1323,new String[]{"Gwent","Powys","Cornwall","Atlantic Ocean"},0);
		region[23]=new Region(this,"Gwent",787,1355,new String[]{"Hwicce","Dyfed","Powys","Devon","Atlantic Ocean"},1);
		region[24]=new Region(this,"Powys",767,1223,new String[]{"Gwent","Dyfed","Gwynedd","Clwyd","March","Hwicce","Atlantic Ocean"},1);
		region[25]=new Region(this,"Gwynedd",699,1140,new String[]{"Clwyd","Powys","Atlantic Ocean"},0);
		region[26]=new Region(this,"Clwyd",768,1107,new String[]{"Cheshire","March","Powys","Gwynedd","Atlantic Ocean"},1);
		region[27]=new Region(this,"Strathclyde",680,686,new String[]{"Galloway","Dalriada","Dunedin","Lothian","Irish Sea"},0);
		region[28]=new Region(this,"Dunedin",746,591,new String[]{"Lothian","Strathclyde","Dalriada","Mar","Alban","North Sea"},0);
		region[29]=new Region(this,"Dalriada",615,536,new String[]{"Skye","Alban","Dunedin","Strathclyde","Irish Sea","Icelandic Sea"},1);
		region[30]=new Region(this,"Alban",706,466,new String[]{"Dunedin","Dalriada","Skye","Moray","Mar"},1);
		region[31]=new Region(this,"Skye",572,383,new String[]{"Dalriada","Alban","Moray","Caithness","Hebrides","Icelandic Sea"},1);
		region[32]=new Region(this,"Moray",671,371,new String[]{"Skye","Alban","Mar","Caithness","Icelandic Sea"},1);
		region[33]=new Region(this,"Mar",819,388,new String[]{"Dunedin","Alban","Moray","Icelandic Sea","North Sea"},1);
		region[34]=new Region(this,"Caithness",668,201,new String[]{"Skye","Moray","Orkneys","Icelandic Sea"},1);
		region[35]=new Region(this,"Hebrides",424,230,new String[]{"Skye","Icelandic Sea"},1);
		region[36]=new Region(this,"Orkneys",793,55,new String[]{"Caithness","Icelandic Sea"},1);
		region[37]=new Region(this,"Icelandic Sea",473,45,new String[]{"Skye","Hebrides","Caithness","Orkneys","Moray","Mar"},2);
		region[38]=new Region(this,"North Sea",1099,602,new String[]{"Mar","Dunedin","Lothian","Bernicia","York"},2);
		region[39]=new Region(this,"Frisian Sea",1204,1040,new String[]{"York","Lindsey","Norfolk","Suffolk","Essex","Kent"},2);
		region[40]=new Region(this,"English Channel",1003,1625,new String[]{"Kent","Sussex","Wessex","Devon","Cornwall"},2);
		region[41]=new Region(this,"Atlantic Ocean",430,1481,new String[]{"Cornwall","Devon","Avalon","Hwicce","Gwent","Dyfed","Powys","Gwynedd","Clwyd","Cheshire"},2);
		region[42]=new Region(this,"Irish Sea",357,678,new String[]{"Cheshire","Cumbria","Galloway","Strathclyde","Dalriada","Skye"},2);
	}

	public void setupNations()
	{
		nation=new Nation[17];
		nation[0]=new Nation(this,"Romans");
		nation[1]=new Nation(this,"Romano-British");
		nation[2]=new Nation(this,"Belgae");
		nation[3]=new Nation(this,"Welsh");
		nation[4]=new Nation(this,"Brigantes");
		nation[5]=new Nation(this,"Caledonians");
		nation[6]=new Nation(this,"Picts");
		nation[7]=new Nation(this,"Irish");
		nation[8]=new Nation(this,"Scots");
		nation[9]=new Nation(this,"Norsemen");
		nation[10]=new Nation(this,"Dubliners");
		nation[11]=new Nation(this,"Danes");
		nation[12]=new Nation(this,"Norwegians");
		nation[13]=new Nation(this,"Jutes");
		nation[14]=new Nation(this,"Saxons");
		nation[15]=new Nation(this,"Angles");
		nation[16]=new Nation(this,"Normans");
	}

	public Region getRegion(String name)
	{
		for (int r=0; r<region.length; r++)
			if (region[r].name.equals(name)) return region[r];
		return null;
	}

	public Nation getNation(String name)
	{
		for (int n=0; n<nation.length; n++)
			if (nation[n].name.equals(name)) return nation[n];
		return null;
	}
	
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater=getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.Exit:
     		System.exit(0);
    		return true;
    	case R.id.About:
    		doAboutBox();
    		return true;
    	case R.id.Victory:
    		new Victory(this,false);
    		return true;
    	case R.id.Help:
    		doHelpBox();
    		return true;
     	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    public void doAboutBox()
    {
		britannia.board.handler.post(new Runnable(){
			public void run()
			{
    	AlertDialog.Builder adb=new AlertDialog.Builder(britannia);
    	adb.setIcon(R.drawable.britainicon5);
    	adb.setTitle("About Britannia");
    	adb.setMessage("This game was written by Michael Black in July, 2011.\n\nIt is based on the board game Britannia written by Lewis Pulsipher and publised by Gibsons Games, 1986.");
    	adb.setNeutralButton("Okay", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
			}});
    	adb.show();
			}
		});    	
    }

    public void doHelpBox()
    {
		britannia.board.handler.post(new Runnable(){
			public void run()
			{
    	AlertDialog.Builder adb=new AlertDialog.Builder(britannia);
    	adb.setIcon(R.drawable.britainicon5);
    	adb.setTitle("Help");
    	
    	adb.setMessage("Britannia is a turn-based war game that reproduces the Dark Age invasions of Britain.  It is played through sixteen rounds, starting at Roman times and ending at the Norman conquest.  Seventeen nations, ranging from the Jutes to the Norwegians, each arrive at the coast of Britain, and are tasked with occupying and settling certain regions.  When these nations occupy their objective regions during particular game rounds, they earn victory points, and the player controlling the nations with the most victory points wins.\n\nGame Setup:\nWhen you begin the game you choose a color - Blue, Green, Red, or Yellow - which determines a set of nations you control.  All other nations are controlled by the computer.  Not all of the nations will be present at the beginning, and many will not survive until the end of the game.  Therefore at any given round there may be six or seven nations on the board, some of whom you control.  Each nation will have one or more army units, which can be moved around and made to invade neighbors.\n\nBoard:\nThe board represents a map of Great Britain, divided into historic regions, each of which may be occupied by one or more armies belonging to one particular nation.  The board is surrounded by seas, where the invading army units of new nations will appear.  In general, armies can be moved from regions to neighboring regions.  Some mountainous areas are colored darker on the map.  These regions slow troop movement and earn fewer victory points, but are easier to defend.\n\nGame Play:\nOn each round of the game, every nation that has army units on the board will get a turn.  During a nation's turn, several tasks will happen in sequence:\n1) New armies will appear.  This may happen automatically as new nations arrive in the seas.  Or, if a nation occupies several regions, it may breed, and you will be invited to place the new army in one of the nation's regions.\n2) Armies can move.  Each turn, every army can be moved or left alone.  Armies can be moved into neighboring regions, and even can be moved two spaces if it is passing through a nonmountainous empty region.\n3) Battles are fought.  If you move an army into another nation's region, you will have to fight for it.  The odds of winning are better if you have several armies invading, your opponent has fewer armies defending, the region is flat, or you have a leader in the battle.  Romans tend to have better luck than other nations.  If the battle is inconclusive and both nations still have armies on the battlefield, both the attacker and the defender will be given a chance to retreat.  Otherwise, the battle continues until one nation occupies the region.\n4) Armies die off.  If you have too many armies and occupy too few regions, you may have to select some of your armies to starve to death.\n5) Victory points are assigned.\n\nObjectives:\nThere are four actions that can earn a nation victory points.  It can occupy a region by controlling it sometime during the round.  It can hold a region by controlling it at the end of the round.  It can kill enemy armies, and it can kill enemy leaders.  Each round of the game, different nations have different objectives.  For example, the Welsh earn 12 points if they occupy York in Round 8, but only 1 for holding it in Round 16, and they never earn points for occuping the Orkneys.  At the beginning of each turn, you will have an opportunity to see your nation's objectives, and you should plan and strategize for several rounds ahead.\n\nOther Game Components:\n- Leaders:  From time to time, a leader will appear for your nation.  This shows up as a name in a white box.  With a leader, you are more likely to win battles, but your opponents may earn victory points for killing your leader.  Leaders die at the end of the round.\n- Major invasions:  On some rounds, a nation will get two turns back-to-back.  This represents the nation's dramatic entrance to the scene.\n- Roman Forts: When the Romans occupy a region for the first time, they place a fort.  This acts as an immovable extra Roman army.  If a region contains a fort, it does not slow down Roman armies passing through.\n- Submission to the Romans:  While the Romans are exceedingly powerful during the first few rounds, they disappear at Round 5.  A nation facing extermination by the Romans may be given a chance to submit to them.  The submitting nation is frozen until Round 5 and the Romans earn plenty of victory points, but it lives to fight another day.\n- Bretwalda and King:  During the later rounds of the game, a nation occupying a majority of English regions will nominate a Bretwalda.  This earns the nation several additional victory points.\n\nThank you for playing.  I hope you enjoy the game.\n-Michael Black");
     	adb.setNeutralButton("Okay", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
			}});
    	adb.show();
			}
		});    	
    }
    
    public void onConfigurationChanged(Configuration newConfig) 
    {
    	  super.onConfigurationChanged(newConfig);
    }
}