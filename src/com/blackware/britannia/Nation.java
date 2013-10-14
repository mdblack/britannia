package com.blackware.britannia;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Nation 
{
	public String name;
	public int populationMarker;
	public int pieceColor;
	private Britannia britannia;
	public int victoryPoints;
	public VictoryPointDatabase victoryPointDatabase;
	public boolean isHuman=true;
	public boolean isSurrendered=false;
	public AI ai=null;
	public int PESSIMISM=-1;

	public Nation(Britannia britannia, String name)
	{
		this.britannia=britannia;
		this.name=name;
		populationMarker=0;
		victoryPoints=0;
		victoryPointDatabase=new VictoryPointDatabase();

		if (name.equals("Romans")) pieceColor=Color.YELLOW;
		if (name.equals("Romano-British")) pieceColor=Color.YELLOW;
		if (name.equals("Belgae")) pieceColor=Color.BLUE;
		if (name.equals("Welsh")) pieceColor=Color.GREEN;
		if (name.equals("Brigantes")) pieceColor=Color.RED;
		if (name.equals("Caledonians")) pieceColor=Color.GREEN;
		if (name.equals("Picts")) pieceColor=Color.BLUE;
		if (name.equals("Irish")) pieceColor=Color.RED;
		if (name.equals("Scots")) pieceColor=Color.YELLOW;
		if (name.equals("Norsemen")) pieceColor=Color.YELLOW;
		if (name.equals("Dubliners")) pieceColor=Color.RED;
		if (name.equals("Danes")) pieceColor=Color.YELLOW;
		if (name.equals("Norwegians")) pieceColor=Color.GREEN;
		if (name.equals("Jutes")) pieceColor=Color.GREEN;
		if (name.equals("Saxons")) pieceColor=Color.RED;
		if (name.equals("Angles")) pieceColor=Color.BLUE;
		if (name.equals("Normans")) pieceColor=Color.BLUE;
	}

	public void addAI()
	{
		isHuman=false;
		ai=new AI(britannia,this);
	}

	public void drawPiece(Canvas c, int xcoor, int ycoor, int offset)
	{
		Paint p=new Paint();
		p.setColor(Color.BLACK);
		c.drawRect(xcoor-10, ycoor-10-offset*2, xcoor-10+20, ycoor-10-offset*2+20, p);
		p.setColor(pieceColor);
		if (isSurrendered)
			p.setColor(Color.WHITE);
		c.drawRect(xcoor-10+1, ycoor-10-offset*2+1, xcoor-10+20-1, ycoor-10-offset*2+20-1, p);
		
		p.setColor(Color.BLACK);
		if (name.equals("Romano-British"))
		{
			p.setTextSize(12);
			c.drawText("RB", xcoor-9, ycoor+8-offset*2, p);
		}
		else
		{
			p.setTextSize(17);
			c.drawText(name.substring(0,1),xcoor-8,ycoor+8-offset*2,p);
		}
	}

	public int quantity()
	{
		int q=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r].isSea) continue;

			if (!britannia.region[r].isOccupiedBy(this)) continue;

			q+=britannia.region[r].pieces.size();
		}
		return q;
	}

	public int totalQuantity()
	{
		int q=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (!britannia.region[r].isOccupiedBy(this)) continue;
			q+=britannia.region[r].pieces.size();
		}
		return q;
	}

	public int advancePopulation()
	{
		int increase=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r].isSea) continue;
			if (!britannia.region[r].isOccupiedBy(this)) continue;
			if (britannia.region[r].isMountain)
				populationMarker+=1;
			else
				populationMarker+=2;
			if (populationMarker>=6)
			{
				populationMarker-=6;
				increase++;
			}	
		}
		return increase;
	}

	public boolean majorInvasion(int round)
	{
		if (round==1 && name.equals("Romans")) return true;
		if (round==6 && name.equals("Saxons")) return true;
		if (round==7 && name.equals("Scots")) return true;
		if (round==7 && name.equals("Angles")) return true;
		if (round==11 && name.equals("Norsemen")) return true;
		if (round==12 && name.equals("Danes")) return true;
		if (round==13 && name.equals("Dubliners")) return true;
		if (round==14 && name.equals("Danes")) return true;
		if (round==15 && name.equals("Norwegians")) return true;
		if (round==15 && name.equals("Normans")) return true;
		if (round==16 && name.equals("Danes")) return true;
		return false;
	}

	public boolean isRaider(int round)
	{
		if (round==3 && name.equals("Irish")) return true;
		if (round==4 && name.equals("Irish")) return true;
		if (round==4 && name.equals("Picts")) return true;
		if (round==4 && name.equals("Scots")) return true;
		if (round==4 && name.equals("Jutes")) return true;
		if (round==4 && name.equals("Saxons")) return true;
		if (round==4 && name.equals("Angles")) return true;
		if (round==5 && name.equals("Picts")) return true;
		if (round==5 && name.equals("Irish")) return true;
		if (round==5 && name.equals("Scots")) return true;
		if (round==5 && name.equals("Jutes")) return true;
		if (round==5 && name.equals("Saxons")) return true;
		if (round==5 && name.equals("Angles")) return true;
		if (round==6 && name.equals("Irish")) return true;
		if (round==6 && name.equals("Scots")) return true;
		if (round==7 && name.equals("Scots")) return true;
		if (round==11 && name.equals("Norsemen")) return true;
		if (round==11 && name.equals("Danes")) return true;
		if (round==12 && name.equals("Norsemen")) return true;
		if (round==12 && name.equals("Danes")) return true;
		return false;
	}

	public boolean hasBoats(int round)
	{
		if (round==3 && name.equals("Caledonians")) return true;
		if (round==3 && name.equals("Picts")) return true;
		if (round==3 && name.equals("Irish")) return true;
		if (round==4 && name.equals("Caledonians")) return true;
		if (round==4 && name.equals("Picts")) return true;
		if (round==4 && name.equals("Irish")) return true;
		if (round==4 && name.equals("Scots")) return true;
		if (round==4 && name.equals("Jutes")) return true;
		if (round==4 && name.equals("Saxons")) return true;
		if (round==4 && name.equals("Angles")) return true;
		if (round==5 && name.equals("Caledonians")) return true;
		if (round==5 && name.equals("Picts")) return true;
		if (round==5 && name.equals("Irish")) return true;
		if (round==5 && name.equals("Scots")) return true;
		if (round==5 && name.equals("Jutes")) return true;
		if (round==5 && name.equals("Saxons")) return true;
		if (round==5 && name.equals("Angles")) return true;
		if (round==6 && name.equals("Caledonians")) return true;
		if (round==6 && name.equals("Picts")) return true;
		if (round==6 && name.equals("Irish")) return true;
		if (round==6 && name.equals("Scots")) return true;
		if (round==6 && name.equals("Jutes")) return true;
		if (round==6 && name.equals("Saxons")) return true;
		if (round==6 && name.equals("Angles")) return true;
		if (round==7 && name.equals("Irish")) return true;
		if (round==7 && name.equals("Scots")) return true;
		if (round==7 && name.equals("Jutes")) return true;
		if (round==7 && name.equals("Saxons")) return true;
		if (round==7 && name.equals("Angles")) return true;
		if (round==8 && name.equals("Jutes")) return true;
		if (round==8 && name.equals("Saxons")) return true;
		if (round==8 && name.equals("Angles")) return true;
		if (round==9 && name.equals("Saxons")) return true;
		if (round==9 && name.equals("Angles")) return true;
		if (round==10 && name.equals("Saxons")) return true;
		if (round==10 && name.equals("Angles")) return true;
		if (round==11 && name.equals("Norsemen")) return true;
		if (round==12 && name.equals("Norsemen")) return true;
		if (round==12 && name.equals("Danes")) return true;
		if (round==13 && name.equals("Norsemen")) return true;
		if (round==13 && name.equals("Danes")) return true;
		if (round==13 && name.equals("Dubliners")) return true;
		if (round==14 && name.equals("Norsemen")) return true;
		if (round==14 && name.equals("Danes")) return true;
		if (round==14 && name.equals("Dubliners")) return true;
		if (round==15 && name.equals("Danes")) return true;
		if (round==15 && name.equals("Norwegians")) return true;
		if (round==16 && name.equals("Danes")) return true;
		if (round==16 && name.equals("Norwegians")) return true;

		return false;
	}

	public boolean canSurrender(int round)
	{
		if (isSurrendered) return false;
			
		if (name.equals("Welsh") && (round<=5) && (quantity()<=5)) return true;
		if (name.equals("Brigantes") && (round<=5) && (quantity()<=3)) return true;
		if (name.equals("Picts") && (round<=5) && (quantity()<=3)) return true;
		return false;
	}

	public class VictoryPointDatabase
	{
		public boolean[] occupyCredit;

		public VictoryPointDatabase()
		{
			occupyCredit=new boolean[britannia.region.length];
		}

		public int getHoldVictoryPoints(int round, String regionsHeld)
		{
			if (name.equals("Romans"))
			{
				switch(round)
				{
					case 5: return matches(regionsHeld,new String[]{"Cheshire","York","Essex"})*6+matches(regionsHeld,new String[]{"Norfolk","Suffolk","North Mercia","Hwicce","South Mercia","Kent","Sussex","Wessex"})*4+matches(regionsHeld,new String[]{"Lothian","Bernicia","Pennines","Cumbria","March","Avalon","Devon","Cornwall"})*2+matches(regionsHeld,new String[]{"Gwynedd","Clwyd","Powys","Dyfed","Gwent","Downlands"})*1;
				}
			}
			if (name.equals("Romano-British"))
			{
				switch(round)
				{
					case 7: case 10: case 13: case 16: return matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Essex","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","March","York","Cheshire","Cumbria","Bernicia","Lothian"})*2;
				}
			}
			if (name.equals("Belgae"))
			{
				switch(round)
				{
					case 5: case 7: case 10: case 13: case 16: return matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Essex","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","March","Downlands","Lindsey"})*2;
				}
			}
			if (name.equals("Welsh"))
			{
				switch(round)
				{
					case 5: case 7: case 10: case 13:
						return	matches(regionsHeld,new String[]{"Powys","Gwynedd","Dyfed"})*4+
							matches(regionsHeld,new String[]{"Cornwall","Devon","Clwyd","Gwent"})*2+
							matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*1;
					case 16:
						return	matches(regionsHeld,new String[]{"Powys","Gwynedd","Dyfed"})*4+
							matches(regionsHeld,new String[]{"Cornwall","Devon","Clwyd","Gwent"})*2+
							matches(regionsHeld,new String[]{"Cheshire","March","Hwicce","Avalon"})*2+
							matches(regionsHeld,new String[]{"Wessex","Sussex","Essex","Downlands","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","York","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*1;
				}
			}
			if (name.equals("Brigantes"))
			{
				switch(round)
				{
					case 5:
						return	matches(regionsHeld,new String[]{"Strathclyde"})*6+
							matches(regionsHeld,new String[]{"Galloway"})*4+
							matches(regionsHeld,new String[]{"Dalriada","Dunedin","Lothian","Bernicia","Pennines","Cumbria","Cheshire","York","March","North Mercia","Lindsey"})*2;
					case 7:
						return	matches(regionsHeld,new String[]{"Strathclyde"})*6+
							matches(regionsHeld,new String[]{"Dalriada","Dunedin","Galloway","Cumbria","Bernicia","York"})*4+
							matches(regionsHeld,new String[]{"Lothian","Pennines","Cheshire","March","North Mercia","Lindsey"})*2;
					case 10:
						return	matches(regionsHeld,new String[]{"Strathclyde"})*8+
							matches(regionsHeld,new String[]{"Cumbria"})*6+
							matches(regionsHeld,new String[]{"Dunedin","Galloway","Bernicia","York"})*4+
							matches(regionsHeld,new String[]{"Dalriada","Lothian","Pennines","Cheshire","March","North Mercia","Lindsey"})*2;
					case 13: case 16:
						return	matches(regionsHeld,new String[]{"Strathclyde"})*10+
							matches(regionsHeld,new String[]{"Dalriada","Dunedin","Lothian","Galloway","Cumbria","Pennines","Bernicia","York","Cheshire","March","North Mercia","Lindsey"})*2;
				}
			}
			if (name.equals("Picts"))
			{
				switch(round)
				{
					case 5: case 7: case 10: case 13: case 16:
						return	matches(regionsHeld,new String[]{"Alban","Mar","Moray"})*4+
							matches(regionsHeld,new String[]{"Strathclyde","Dunedin","Dalriada","Skye","Caithness","Hebrides","Orkneys"})*2+
							matches(regionsHeld,new String[]{"Galloway","Lothian","Bernicia"})*2;
				}
			}
			if (name.equals("Caledonians"))
			{
				switch(round)
				{
					case 5: case 7:
						return	matches(regionsHeld,new String[]{"Orkneys","Caithness","Hebrides"})*4+
							matches(regionsHeld,new String[]{"Moray","Skye","Dalriada"})*2;
					case 10: case 13: case 16:
						return	matches(regionsHeld,new String[]{"Orkneys","Hebrides"})*4+
							matches(regionsHeld,new String[]{"Caithness","Moray","Skye","Dalriada"})*2;
				}
			}
			if (name.equals("Irish"))
			{
				switch(round)
				{
					case 5: case 7:
						return	matches(regionsHeld,new String[]{"Cumbria","Cheshire","Gwynedd","Dyfed","Avalon","Devon","Cornwall"})*4;
					case 10: case 13:
						return	matches(regionsHeld,new String[]{"Cumbria","Cheshire","Gwynedd","Dyfed","Avalon","Devon","Cornwall"})*2;
					case 16:
						return	matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*2+
							matches(regionsHeld,new String[]{"Powys","Gwynedd","Dyfed","Cornwall","Devon","Clwyd","Gwent"})*2+
							matches(regionsHeld,new String[]{"Alban","Mar","Moray","Strathclyde","Dunedin","Dalriada","Skye","Caithness","Hebrides","Orkneys"})*2;
				}
			}
			if (name.equals("Scots"))
			{
				switch(round)
				{
					case 5:
						return	matches(regionsHeld,new String[]{"Alban","Mar","Moray","Strathclyde","Dunedin","Dalriada","Skye","Caithness","Hebrides","Orkneys","Galloway","Lothian"})*2;
					case 7: case 10: case 13: case 16:
						return	matches(regionsHeld,new String[]{"Skye","Dalriada","Dunedin"})*4+
							matches(regionsHeld,new String[]{"Alban","Mar","Moray","Strathclyde","Caithness","Hebrides","Orkneys","Galloway","Lothian"})*2;
				}
			}
			if (name.equals("Jutes"))
			{
				switch(round)
				{
					case 5:
						return	matches(regionsHeld,new String[]{"Kent"})*8+
							matches(regionsHeld,new String[]{"Essex","Sussex","Wessex"})*4;
					case 7: case 10: case 13: case 16:
						return	matches(regionsHeld,new String[]{"Kent"})*4+
							matches(regionsHeld,new String[]{"Essex","Sussex","Wessex"})*2;
				}
			}
			if (name.equals("Angles"))
			{
				switch(round)
				{
					case 5:
						return	matches(regionsHeld,new String[]{"Essex","South Mercia","North Mercia","Hwicce","March","Suffolk","Norfolk","Lindsey","York"})*2;
					case 7:
						return	matches(regionsHeld,new String[]{"Norfolk"})*6+
							matches(regionsHeld,new String[]{"Dunedin","Lothian","Bernicia","York","North Mercia"})*4+
							matches(regionsHeld,new String[]{"Pennines","Cumbria","Cheshire","March","Hwicce","South Mercia","Essex","Suffolk","Lindsey"})*2;
					case 10: case 13: case 16:
						return	matches(regionsHeld,new String[]{"Lothian","Bernicia","Pennines","Cheshire","York","North Mercia","Hwicce","Norfolk"})*4+
							matches(regionsHeld,new String[]{"Galloway","Cumbria","March","Lindsey","Suffolk","South Mercia"})*2;
				}
			}
			if (name.equals("Saxons"))
			{
				switch(round)
				{
					case 5:
						return	matches(regionsHeld,new String[]{"Essex","South Mercia","North Mercia","Hwicce","March","Suffolk","Avalon","Wessex","Downlands","Sussex","Kent"})*2;
					case 7:
						return	matches(regionsHeld,new String[]{"Essex"})*6+
							matches(regionsHeld,new String[]{"Sussex","Wessex","Avalon"})*4+
							matches(regionsHeld,new String[]{"Suffolk","Kent"})*3+
							matches(regionsHeld,new String[]{"Norfolk","York","Cheshire","March","Hwicce","South Mercia","North Mercia","Downlands","Devon","Cornwall"})*2;
					case 10: case 13: case 16:
						return	matches(regionsHeld,new String[]{"Essex","Sussex","Wessex","Avalon","Downlands","Hwicce"})*4+
							matches(regionsHeld,new String[]{"Kent"})*3+
							matches(regionsHeld,new String[]{"Norfolk","March","Suffolk","South Mercia","Devon","Cornwall"})*2;
				}
			}
			if (name.equals("Dubliners"))
			{
				switch(round)
				{
					case 13:
						return	matches(regionsHeld,new String[]{"York"})*8+matches(regionsHeld,new String[]{"Cumbria"})*5+
							matches(regionsHeld,new String[]{"Dalriada","Bernicia","Cheshire","Dyfed","Gwent","Avalon"})*2;
					case 16:
						return	matches(regionsHeld,new String[]{"Cumbria"})*4+
							matches(regionsHeld,new String[]{"Dalriada","Bernicia","Cheshire","York","Dyfed","Gwent","Avalon"})*2+
							matches(regionsHeld,new String[]{"Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","Pennines","Galloway","Lothian"})*1+
							matches(regionsHeld,new String[]{"Powys","Gwynedd","Cornwall","Devon","Clwyd"})*1+
							matches(regionsHeld,new String[]{"Alban","Mar","Moray","Strathclyde","Dunedin","Skye","Caithness","Hebrides","Orkneys"})*1;
				}
			}
			if (name.equals("Norsemen"))
			{
				switch(round)
				{
					case 13:
						return	matches(regionsHeld,new String[]{"Orkneys","Hebrides","Caithness"})*6+
							matches(regionsHeld,new String[]{"Cumbria","Skye"})*4+
							matches(regionsHeld,new String[]{"Mar","Moray","Dalriada","Strathclyde","Galloway","Pennines","Cheshire","Clwyd","Gwynedd","Dyfed","Gwent","Avalon"})*2;
					case 16:
						return	matches(regionsHeld,new String[]{"Cumbria","Caithness","Orkneys","Hebrides"})*4+
							matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","York","Cheshire","Pennines","Bernicia","Galloway","Lothian"})*2+
							matches(regionsHeld,new String[]{"Powys","Gwynedd","Dyfed","Cornwall","Devon","Clwyd","Gwent"})*2+
							matches(regionsHeld,new String[]{"Alban","Mar","Moray","Strathclyde","Dunedin","Dalriada","Skye"})*2;
				}
			}
			if (name.equals("Norwegians"))
			{
				switch(round)
				{
					case 16:
						return	matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*2;
				}
			}
			if (name.equals("Danes"))
			{
				switch(round)
				{
					case 13:
						return	matches(regionsHeld,new String[]{"York"})*8+
							matches(regionsHeld,new String[]{"Lindsey","North Mercia","Essex","Suffolk","Norfolk"})*4+
							matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Downlands","Hwicce","Kent","South Mercia","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*2+
							matches(regionsHeld,new String[]{"Dunedin","Strathclyde","Devon","Cornwall"})*2;
					case 16:
						return	matches(regionsHeld,new String[]{"York"})*8+
							matches(regionsHeld,new String[]{"Lindsey","North Mercia","Essex","Suffolk","Norfolk"})*4+
							matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Downlands","Hwicce","Kent","South Mercia","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*2+
							matches(regionsHeld,new String[]{"Devon","Cornwall"})*2;
				}
			}
			if (name.equals("Normans"))
			{
				switch(round)
				{
					case 16:
						return	matches(regionsHeld,new String[]{"Avalon","Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*2;
				}
			}
			return 0;
		}

		public void assignHoldVictoryPoints(int round, String regionsHeld)
		{
			int newVictoryPoints=getHoldVictoryPoints(round,regionsHeld);
			victoryPoints+=newVictoryPoints;
			if (newVictoryPoints>0)
			{
				britannia.board.setInformation("From holding "+regionsHeld+", "+name+" gain "+newVictoryPoints+" victory points for a total of "+victoryPoints);
				britannia.board.pause();
			}
		}

		public int getOccupyVictoryPoints(int round, String regionOccupied)
		{
			int r=0;
			for (r=0; r<britannia.region.length; r++)
				if (britannia.region[r].name.equals(regionOccupied)) break;
			if (occupyCredit[r]) return 0;

			if (name.equals("Romans"))
			{
				switch(round)
				{
					case 1: case 2: case 3: return matches(regionOccupied,new String[]{"Alban","Dalriada","Dunedin","Mar"})*4+matches(regionOccupied,new String[]{"Strathclyde","Gwynedd","Dyfed","Devon"})*2+matches(regionOccupied,new String[]{"Avalon","Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*2+matches(regionOccupied,new String[]{"Cornwall","Gwent","Powys","Clwyd"})*1;
				}
			}
			if (name.equals("Welsh"))
			{
				switch(round)
				{
					case 8: case 9: return matches(regionOccupied,new String[]{"York"})*12;
				}
			}
			if (name.equals("Norsemen"))
			{
				switch(round)
				{
					case 11: case 12: case 13: case 14: case 15: case 16: 
						return	matches(regionOccupied,new String[]{"Orkneys","Hebrides","Cumbria","Caithness"})*3+
							matches(regionOccupied,new String[]{"Mar","Moray","Skye","Dalriada","Strathclyde","Galloway","Pennines","Cheshire","Clwyd","Gwynedd","Dyfed","Gwent","Avalon"})*1;
				}
			}
			if (name.equals("Norwegians"))
			{
				switch(round)
				{
					case 15: 
						return	matches(regionOccupied,new String[]{"York"})*10+matches(regionOccupied,new String[]{"North Mercia"})*6+matches(regionOccupied,new String[]{"Bernicia","Cheshire","March"})*4;
				}
			}
			if (name.equals("Danes"))
			{
				switch(round)
				{
					case 11: 
						return	matches(regionOccupied,new String[]{"Avalon","Wessex","Sussex","Essex","Downlands","Hwicce","Kent","Suffolk","Norfolk","South Mercia","North Merica","Lindsey","March","York","Cheshire","Pennines","Cumbria","Bernicia","Galloway","Lothian"})*2+
							matches(regionOccupied,new String[]{"Powys","Gwynedd","Dyfed","Cornwall","Devon","Clwyd","Gwent"})*2+
							matches(regionOccupied,new String[]{"Alban","Mar","Moray","Strathclyde","Dunedin","Dalriada","Skye","Caithness","Hebrides","Orkneys"})*2;
					case 12: 
						return	matches(regionOccupied,new String[]{"York"})*8+
							matches(regionOccupied,new String[]{"Essex","Suffolk","South Mercia","North Merica","Lindsey","Cheshire","Pennines","Cumbria","Bernicia","Lothian","Strathclyde"})*4+
							matches(regionOccupied,new String[]{"March","Kent"})*2+
							matches(regionOccupied,new String[]{"Hwicce","Downlands","Sussex","Wessex","Avalon","Devon","Cornwall"})*1;
				}
			}
			if (name.equals("Normans"))
			{
				switch(round)
				{
					case 15: 
						return	matches(regionOccupied,new String[]{"Essex"})*8+matches(regionOccupied,new String[]{"Wessex"})*6+matches(regionOccupied,new String[]{"South Mercia","Suffolk","Sussex","Kent"})*4+matches(regionOccupied,new String[]{"Norfolk","Hwicce","Avalon"})*2;
				}
			}
			return 0;
		}

		public void assignOccupyVictoryPoints(int round, String regionOccupied)
		{
			int r=0;
			for (r=0; r<britannia.region.length; r++)
				if (britannia.region[r].name.equals(regionOccupied)) break;
			int newVictoryPoints=getOccupyVictoryPoints(round,regionOccupied);
			victoryPoints+=newVictoryPoints;
			if(newVictoryPoints>0)
			{
				britannia.board.setInformation("After occupying "+regionOccupied+", "+name+" gain "+newVictoryPoints+" victory points for a total of "+victoryPoints);
				System.out.println("After occupying "+regionOccupied+", "+name+" gain "+newVictoryPoints+" victory points for a total of "+victoryPoints);
				britannia.board.pause();
			}
			occupyCredit[r]=true;
		}

		public int getKillLeaderVictoryPoints(int round, String leaderKilled)
		{
			if (name.equals("Romans") && (round==1||round==2))
				return matches(leaderKilled,new String[]{"Boudicca"})*6;
			if (name.equals("Romano-British") && (round==6||round==7))
				return matches(leaderKilled,new String[]{"Aelle"})*6;
			if (name.equals("Welsh") && (round==6||round==7))
				return matches(leaderKilled,new String[]{"Aelle"})*2;
			if (name.equals("Jutes") && round==7)
				return matches(leaderKilled,new String[]{"Arthur"})*6;
			if (name.equals("Angles"))
				return matches(leaderKilled,new String[]{"Arthur","Ivar and Halfdan","Harald Hardrada","William","Svein Estrithson"})*6;
			if (name.equals("Saxons"))
				return matches(leaderKilled,new String[]{"Arthur","Ivar and Halfdan","Harald Hardrada","William","Svein Estrithson"})*6;
			if (name.equals("Norwegians") && (round==15||round==16))
				return matches(leaderKilled,new String[]{"Harold","William","Svein Estrithson"})*6;
			if (name.equals("Danes") && (round==15||round==16))
				return matches(leaderKilled,new String[]{"Harold","William","Harald Hardrada"})*6;
			if (name.equals("Normans") && (round==15||round==16))
				return matches(leaderKilled,new String[]{"Harold","Svein Estrithson","Harald Hardrada"})*6;
			return 0;
		}

		public int getKillVictoryPoints(int round, Nation nationKilled)
		{
			if (name.equals("Romano-British") && (round==6||round==7))
				return matches(nationKilled.name,new String[]{"Jutes","Saxons","Angles"})*2;
			if (name.equals("Belgae") && (round==1))
				return matches(nationKilled.name,new String[]{"Romans"})*6;
			if (name.equals("Belgae") && (round>=2))
				return matches(nationKilled.name,new String[]{"Romans"})*2;
			if (name.equals("Welsh"))
				return matches(nationKilled.name,new String[]{"Romans"})*2;
			if (name.equals("Brigantes"))
				return matches(nationKilled.name,new String[]{"Romans"})*6;
			if (name.equals("Picts"))
				return matches(nationKilled.name,new String[]{"Romans"})*2;
			if (name.equals("Irish"))
				return matches(nationKilled.name,new String[]{"Romans","Romano-British","Belgae","Welsh","Picts","Caledonians","Brigantes","Scots","Norsemen","Dubliners","Danes","Jutes","Saxons","Angles","Norwegians","Normans"})*2;
			if (name.equals("Scots") && (round==4||round==5||round==6))
				return matches(nationKilled.name,new String[]{"Romans","Romano-British","Belgae","Welsh","Picts","Caledonians","Brigantes","Scots","Norsemen","Dubliners","Danes","Jutes","Saxons","Angles","Norwegians","Normans"})*2;
			if (name.equals("Jutes"))
				return matches(nationKilled.name,new String[]{"Romans"})*2;
			if (name.equals("Angles"))
				return matches(nationKilled.name,new String[]{"Romans"})*2;
			if (name.equals("Saxons"))
				return matches(nationKilled.name,new String[]{"Romans"})*2;
			return 0;
		}

		public void assignKillVictoryPoints(int round, Nation nationKilled)
		{
			int newVictoryPoints=getKillVictoryPoints(round,nationKilled);
			victoryPoints+=newVictoryPoints;
			if(newVictoryPoints>0)
			{
				britannia.board.setInformation("After killing the "+nationKilled.name+" army, "+name+" gain "+newVictoryPoints+" victory points for a total of "+victoryPoints);
				britannia.board.pause();
			}
		}

		public void assignKillLeaderVictoryPoints(int round, Leader leaderKilled)
		{
			int newVictoryPoints=getKillLeaderVictoryPoints(round,leaderKilled.name);
			victoryPoints+=newVictoryPoints;
			if(newVictoryPoints>0)
			{
				britannia.board.setInformation("After killing "+leaderKilled.name+", "+name+" gain "+newVictoryPoints+" victory points for a total of "+victoryPoints);
				britannia.board.pause();
			}
		}

		public int getBurnFortVictoryPoints(int round)
		{
			if (name.equals("Belgae") && (round==1))
				return 6;
			if (name.equals("Belgae") && (round>=2))
				return 4;
			if (name.equals("Welsh"))
				return 6;
			if (name.equals("Brigantes"))
				return 2;
			if (name.equals("Picts"))
				return 6;
			if (name.equals("Irish"))
				return 2;
			if (name.equals("Scots"))
				return 2;
			if (name.equals("Jutes"))
				return 6;
			if (name.equals("Angles"))
				return 6;
			if (name.equals("Saxons"))
				return 6;
			return 0;
		}

		public void assignBurnFortVictoryPoints(int round)
		{
			int newVictoryPoints=getBurnFortVictoryPoints(round);
			victoryPoints+=newVictoryPoints;
			if(newVictoryPoints>0)
			{
				britannia.board.setInformation("After burning the Roman fort, "+name+" gain "+newVictoryPoints+" victory points for a total of "+victoryPoints);
				britannia.board.pause();
			}
		}

		public int matches(String[] a, String[] b)
		{
			int found=0;
			for (int i=0; i<a.length; i++)
			{
				for (int j=0; j<b.length; j++)
				{
					if (a[i].equals(b[j]))
					{
						found++; break;
					}
				}
			}
			return found;
		}
		public int matches(String a, String[] b)
		{
			for (int i=0; i<b.length; i++)
				if (b[i].equals(a)) return 1;
			return 0;
		}
	}
}
