package com.blackware.britannia;

import java.util.ArrayList;

public class Game 
{
	private Britannia britannia;

	public int round=0;

	public boolean movePieceExpected=false;
	public boolean selectRegionExpected=false;
	public Nation currentNation=null;
	public boolean retreatExpected=false;
	public Region selectRegion=null;
	public int[] year=new int[]{1,43,60,100,250,340,430,485,560,635,710,785,860,935,985,1035,1066,1085};

	public Lock phaseLock,iLock;

	public Game(Britannia britannia)
	{
		this.britannia=britannia;
		phaseLock=new Lock();
		iLock=new Lock();

		setupInitialBoard();
	}

	private void setupInitialBoard()
	{
		britannia.getRegion("Caithness").addPieceInitial(britannia.getNation("Caledonians"),1);
		britannia.getRegion("Hebrides").addPieceInitial(britannia.getNation("Caledonians"),1);
		britannia.getRegion("Orkneys").addPieceInitial(britannia.getNation("Caledonians"),1);
		britannia.getRegion("Alban").addPieceInitial(britannia.getNation("Picts"),1);
		britannia.getRegion("Dalriada").addPieceInitial(britannia.getNation("Picts"),1);
		britannia.getRegion("Dunedin").addPieceInitial(britannia.getNation("Picts"),1);
		britannia.getRegion("Mar").addPieceInitial(britannia.getNation("Picts"),1);
		britannia.getRegion("Moray").addPieceInitial(britannia.getNation("Picts"),1);
		britannia.getRegion("Skye").addPieceInitial(britannia.getNation("Picts"),1);
		britannia.getRegion("Bernicia").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("Cheshire").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("Cumbria").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("Galloway").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("Lothian").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("March").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("Pennines").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("Strathclyde").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("York").addPieceInitial(britannia.getNation("Brigantes"),1);
		britannia.getRegion("Downlands").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Essex").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Kent").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Lindsey").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("North Mercia").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("South Mercia").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Norfolk").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Suffolk").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Sussex").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Wessex").addPieceInitial(britannia.getNation("Belgae"),1);
		britannia.getRegion("Avalon").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Clwyd").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Cornwall").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Devon").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Dyfed").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Gwent").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Gwynedd").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Hwicce").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("Powys").addPieceInitial(britannia.getNation("Welsh"),1);
		britannia.getRegion("English Channel").addPieceInitial(britannia.getNation("Romans"),16);
	}

	public void doGame()
	{
		for (round=1; round<=16; round++)
		{
			currentNation=null;
			britannia.board.headerComponent.newTurn();
			System.out.println("Round "+round);
			for (int currentNationNumber=0; currentNationNumber<17; currentNationNumber++)
			{
				currentNation=britannia.nation[currentNationNumber];
				System.out.println(currentNation.name+" Turn");
				britannia.board.headerComponent.newTurn();

				if (currentNation.isHuman && currentNation.totalQuantity()>0 && !currentNation.isSurrendered)
				{
					britannia.board.setControlButton("Continue");
					britannia.board.setControl2Button("Objectives");
					while(true)
					{
						phaseLock.lockWait();
						if (britannia.board.buttonPressed.equals("Continue")) break;
						else if (britannia.board.buttonPressed.equals("Objectives")) new Objectives(britannia);
					}
				}

				britannia.board.headerComponent.setAction("Population Phase");
				if (!currentNation.isHuman && currentNation.totalQuantity()>0)
				{
					britannia.board.pause();
				}

				//population phase
				populationPhase(currentNation,round);

				//major invasion?
				for (int mi=0; mi<(currentNation.majorInvasion(round)? 2:1); mi++)
				{
					britannia.board.headerComponent.setAction("Movement Phase");
					if (!currentNation.isHuman && currentNation.totalQuantity()>0 && !currentNation.isSurrendered)
						britannia.board.pause();

					//movement phase
					if (currentNation.totalQuantity()>0 && !currentNation.isSurrendered)
					{
						movementPhase(currentNation);
					}

					britannia.board.headerComponent.setAction("Battle Phase");
					if (!currentNation.isHuman && currentNation.totalQuantity()>0 && !currentNation.isSurrendered)
						britannia.board.pause();

					//battle phase
					for (int r=0; r<britannia.region.length; r++)
					{
						if (britannia.region[r].piecesInWaiting.size()>0)
							battlePhase(britannia.region[r]);
					}
					britannia.board.aDice=new int[0];
					britannia.board.dDice=new int[0];

					britannia.board.headerComponent.setAction("Scoring Phase");
					britannia.board.repaintBoard();
					//occupation victory points
					for (int r=0; r<britannia.region.length; r++)
					{
						if (!britannia.region[r].isSea && britannia.region[r].isOccupiedBy(currentNation))
						{
							if (currentNation.name.equals("Romans") && !britannia.region[r].hasFort && !britannia.region[r].hadFort)
								britannia.region[r].hasFort=true;
							currentNation.victoryPointDatabase.assignOccupyVictoryPoints(round,britannia.region[r].name);
						}
						else if (currentNation.name.equals("Romans") && britannia.region[r].occupier()!=null && britannia.region[r].occupier().isSurrendered)
							britannia.getNation("Romans").victoryPointDatabase.assignOccupyVictoryPoints(round,britannia.region[r].name);
					}
					britannia.board.repaintBoard();
					if (!currentNation.isHuman && currentNation.totalQuantity()>0)
						britannia.board.pause();

				}

				//overpopulation phase
				overpopulationPhase(currentNation);

//				if (!currentNation.isHuman && currentNation.totalQuantity()>0)
//					britannia.board.pause();
			}

			//hold victory points
			for (int currentNationNumber=0; currentNationNumber<17; currentNationNumber++)
			{
				currentNation=britannia.nation[currentNationNumber];

				//hold victory points
				for (int r=0; r<britannia.region.length; r++)
				{
					if (britannia.region[r].isOccupiedBy(currentNation))
						currentNation.victoryPointDatabase.assignHoldVictoryPoints(round,britannia.region[r].name);
					else if (currentNation.name.equals("Romans") && britannia.region[r].occupier()!=null && britannia.region[r].occupier().isSurrendered)
						britannia.getNation("Romans").victoryPointDatabase.assignHoldVictoryPoints(round,britannia.region[r].name);
				}

				//opportunity to submit to Romans
				if (currentNation.canSurrender(round))
				{
					if (!currentNation.isHuman)
					{
						currentNation.isSurrendered=currentNation.ai.doSurrender();
						britannia.board.repaintBoard();
					}
					else
					{
//						britannia.board.setInstruction("Should "+currentNation.name+" submit to the Romans?");
//						britannia.board.setControlButton("Submit");
//						britannia.board.setControl2Button("Fight On");
						britannia.board.choice("Should "+currentNation.name+" submit to the Romans?","Submit","Fight On");
						phaseLock.lockWait();
						if(britannia.board.buttonPressed.equals("Submit"))
						{
							currentNation.isSurrendered=true;
							britannia.board.repaintBoard();
						}
					}
				}
			}

			bretwaldaPhase();
			britannia.board.pause();			

			//remaining leaders die (except on turns 15/16)
			if (round<15)
			{
				for (int r=0; r<britannia.region.length; r++)
				{
					if (britannia.region[r].piecesLeader!=null)
					{
						britannia.region[r].piecesLeader=null;
					}
				}
			}
		}

		System.out.println("\nVictory Point Totals\n-----------------------------");
		for (int i=0; i<britannia.nation.length; i++)
		{
			System.out.println(britannia.nation[i].name+"\t"+britannia.nation[i].victoryPoints);
		}
		britannia.board.declareVictory();
		britannia.board.setControlButton("Exit");
//		phaseLock.lockWait();
	}

	public void populationPhase(Nation n, int round)
	{
		if (round==1)
		{
			if (n.name.equals("Romans")) britannia.board.setInstruction("16 Roman armies invade from the English Channel");
			if (n.name.equals("Belgae")) new Leader(britannia,"Boudicca").birth(null);
			return;
		}

		//Roman reinforcements
		if (n.name.equals("Romans"))
		{
			int q=n.quantity();
			if ((round==2 && (q==9||q==10||q==11))||(round==3 && (q==9||q==10))||(round==4 && (q==7||q==8||q==9))||(round==5 && (q==7||q==8)))
				britannia.getRegion("English Channel").addPiece(britannia.getNation("Romans"),1);
			if (((round==2||round==3)&&(q==7||q==8))||((round==4||round==5)&&(q==6)))
				britannia.getRegion("English Channel").addPiece(britannia.getNation("Romans"),2);
			if (((round==2||round==3)&&(q==5||q==6))||((round==4||round==5)&&(q==4||q==5)))
				britannia.getRegion("English Channel").addPiece(britannia.getNation("Romans"),3);
			if ((round==2||round==3)&&(q<=4))
				britannia.getRegion("English Channel").addPiece(britannia.getNation("Romans"),4);
			if (round==6)
			{
				//Unsubmit
				for (int nn=0; nn<britannia.nation.length; nn++)
					britannia.nation[nn].isSurrendered=false;
				//Romano-British replacement
				for (int r=0; r<britannia.region.length; r++)
				{
					britannia.region[r].hadFort=false;

					if (!britannia.region[r].isOccupiedBy(n)) continue;
					int i=britannia.region[r].pieces.size();
					while (britannia.region[r].pieces.size()>0) britannia.region[r].pieces.remove(0);
					if ((r>14 && r<22)||(r>26)) 
					{
						britannia.region[r].hasFort=false;
						continue;
					}
					if (britannia.region[r].hasFort)
						britannia.region[r].addPiece(britannia.getNation("Romano-British"),1);
					britannia.region[r].hasFort=false;
					britannia.region[r].addPiece(britannia.getNation("Romano-British"),i);
				}
				britannia.board.setInstruction("Romans depart from Britain");
			}
			britannia.board.repaintBoard();
			return;
		}

		//new arrivals
		if (round==3)
		{
			if (n.name.equals("Irish")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Irish"),1);
			if (n.name.equals("Irish")) britannia.board.setInstruction("1 Irish raider appears in the Atlantic Ocean");
		}
		if (round==4)
		{
			if (n.name.equals("Irish")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Irish"),2);
			if (n.name.equals("Irish")) britannia.board.setInstruction("2 Irish raiders appear in the Atlantic Ocean");
			if (n.name.equals("Scots")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Scots"),1);
			if (n.name.equals("Scots")) britannia.board.setInstruction("1 Scottish raider appears in the Irish Sea");
			if (n.name.equals("Jutes")) britannia.getRegion("English Channel").addPiece(britannia.getNation("Jutes"),2);
			if (n.name.equals("Jutes")) britannia.board.setInstruction("2 Jute raiders appear in the English Channel");
			if (n.name.equals("Saxons")) britannia.getRegion("English Channel").addPiece(britannia.getNation("Saxons"),3);
			if (n.name.equals("Saxons")) britannia.board.setInstruction("3 Saxon raiders appear in the English Channel");
			if (n.name.equals("Angles")) britannia.getRegion("Frisian Sea").addPiece(britannia.getNation("Angles"),3);
			if (n.name.equals("Angles")) britannia.board.setInstruction("3 Angle raiders appear in the Frisian Sea");
		}
		if (round==5)
		{
			if (n.name.equals("Irish")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Irish"),1);
			if (n.name.equals("Irish")) britannia.board.setInstruction("1 Irish raider appears in the Atlantic Ocean");
			if (n.name.equals("Scots")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Scots"),2);
			if (n.name.equals("Scots")) britannia.board.setInstruction("2 Scottish raiders appear in the Irish Sea");
			if (n.name.equals("Jutes")) britannia.getRegion("English Channel").addPiece(britannia.getNation("Jutes"),2);
			if (n.name.equals("Jutes")) britannia.board.setInstruction("2 Jute raiders appear in the English Channel");
			if (n.name.equals("Saxons")) britannia.getRegion("English Channel").addPiece(britannia.getNation("Saxons"),3);
			if (n.name.equals("Saxons")) britannia.board.setInstruction("3 Saxon raiders appear in the English Channel");
			if (n.name.equals("Angles")) britannia.getRegion("Frisian Sea").addPiece(britannia.getNation("Angles"),3);
			if (n.name.equals("Angles")) britannia.board.setInstruction("3 Angle raiders appear in the Frisian Sea");
		}
		if (round==6)
		{
			if (n.name.equals("Irish")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Irish"),1);
			if (n.name.equals("Irish")) britannia.board.setInstruction("1 Irish raider appears in the Atlantic Ocean");
			if (n.name.equals("Scots")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Scots"),2);
			if (n.name.equals("Scots")) britannia.board.setInstruction("2 Scottish raiders appear in the Irish Sea");
			if (n.name.equals("Jutes")) britannia.getRegion("English Channel").addPiece(britannia.getNation("Jutes"),1);
			if (n.name.equals("Jutes")) britannia.board.setInstruction("1 Jute army attacks from the English Channel");
			if (n.name.equals("Saxons")) britannia.getRegion("Frisian Sea").addPiece(britannia.getNation("Saxons"),8);
			if (n.name.equals("Saxons")) britannia.board.setInstruction("8 Saxon armies invade from the Frisian Sea");
			if (n.name.equals("Angles")) britannia.getRegion("North Sea").addPiece(britannia.getNation("Angles"),4);
			if (n.name.equals("Angles")) britannia.board.setInstruction("4 Angle armies attack from the North Sea");
		}
		if (round==7)
		{
			if (n.name.equals("Romano-British")) new Leader(britannia,"King Arthur").birth(null);
			if (n.name.equals("Brigantes")) new Leader(britannia,"Urien").birth(null);
			if (n.name.equals("Irish")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Irish"),1);
			if (n.name.equals("Irish")) britannia.board.setInstruction("1 Irish army attacks from the Atlantic Ocean");
			if (n.name.equals("Scots")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Scots"),3);
			if (n.name.equals("Scots")) new Leader(britannia,"Fergus").birth(britannia.getRegion("Irish Sea"));
			if (n.name.equals("Scots")) britannia.board.setInstruction("3 Scottish armies, led by Fergus, invade from the Irish Sea");
			if (n.name.equals("Saxons")) britannia.getRegion("Frisian Sea").addPiece(britannia.getNation("Saxons"),4);
			if (n.name.equals("Saxons")) britannia.board.setInstruction("4 Saxon armies attack from the Frisian Sea");
			if (n.name.equals("Angles")) britannia.getRegion("North Sea").addPiece(britannia.getNation("Angles"),7);
			if (n.name.equals("Angles")) new Leader(britannia,"Ida").birth(britannia.getRegion("Irish Sea"));
			if (n.name.equals("Angles")) britannia.board.setInstruction("7 Angle armies, led by Ida, invade from the North Sea");
		}
		if (round==8)
		{
			if (n.name.equals("Irish")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Irish"),1);
			if (n.name.equals("Irish")) britannia.board.setInstruction("1 Irish army attacks from the Atlantic Ocean");
			if (n.name.equals("Scots")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Scots"),1);
			if (n.name.equals("Scots")) britannia.board.setInstruction("1 Scottish army attacks from the Irish Sea");
			if (n.name.equals("Angles")) britannia.getRegion("North Sea").addPiece(britannia.getNation("Angles"),2);
			if (n.name.equals("Angles")) britannia.board.setInstruction("2 Angle armies attack from the North Sea");
		}
		if (round==9)
		{
			if (n.name.equals("Irish")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Irish"),1);
			if (n.name.equals("Irish")) britannia.board.setInstruction("1 Irish army attacks from the Atlantic Ocean");
			if (n.name.equals("Angles")) new Leader(britannia,"Oswiu").birth(britannia.getRegion("Bernicia"));
		}
		if (round==10)
		{
			if (n.name.equals("Angles")) new Leader(britannia,"Offa").birth(britannia.getRegion("North Mercia"));
		}
		if (round==11)
		{
			if (n.name.equals("Norsemen")) britannia.getRegion("Icelandic Sea").addPiece(britannia.getNation("Norsemen"),5);
			if (n.name.equals("Norsemen")) new Leader(britannia,"Ketil").birth(britannia.getRegion("Icelandic Sea"));
			if (n.name.equals("Norsemen")) britannia.board.setInstruction("5 Norsemen, led by Ketil, invade from the Icelandic Sea");
			if (n.name.equals("Danes")) britannia.getRegion("North Sea").addPiece(britannia.getNation("Danes"),4);
			if (n.name.equals("Danes")) britannia.getRegion("Frisian Sea").addPiece(britannia.getNation("Danes"),4);
			if (n.name.equals("Danes")) britannia.board.setInstruction("8 Danish raiders appear in the North and Frisian Seas");
			if (n.name.equals("Saxons")) new Leader(britannia,"Egbert").birth(britannia.getRegion(null));
		}
		if (round==12)
		{
			if (n.name.equals("Norsemen")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Norsemen"),2);
			if (n.name.equals("Norsemen")) britannia.getRegion("Atlantic Ocean").addPiece(britannia.getNation("Norsemen"),2);
			if (n.name.equals("Norsemen")) britannia.board.setInstruction("4 Norse raiders appear in the Irish Sea and Atlantic Ocean");
			if (n.name.equals("Dubliners")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Dubliners"),2);
			if (n.name.equals("Dubliners")) britannia.board.setInstruction("2 Dubliner raiders appear in the Irish Sea");
			if (n.name.equals("Danes")) britannia.getRegion("North Sea").addPiece(britannia.getNation("Danes"),6);
			if (n.name.equals("Danes")) new Leader(britannia,"Ivar and Halfdan").birth(britannia.getRegion("North Sea"));
			if (n.name.equals("Danes")) britannia.board.setInstruction("6 Danish armies, led by Ivar and Halfdan, invade from the North Sea");
			if (n.name.equals("Saxons")) new Leader(britannia,"Alfred").birth(britannia.getRegion(null));
		}
		if (round==13)
		{
			if (n.name.equals("Dubliners")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Dubliners"),5);
			if (n.name.equals("Dubliners")) new Leader(britannia,"Olaf").birth(britannia.getRegion("Irish Sea"));
			if (n.name.equals("Dubliners")) britannia.board.setInstruction("5 Dubliners, led by Olaf, invade from the Irish Sea");
			if (n.name.equals("Danes")) britannia.getRegion("North Sea").addPiece(britannia.getNation("Danes"),2);
			if (n.name.equals("Danes")) britannia.board.setInstruction("2 Danish armies attack from the North Sea");
			if (n.name.equals("Saxons")) new Leader(britannia,"Edgar").birth(britannia.getRegion(null));
		}
		if (round==14)
		{
			if (n.name.equals("Dubliners")) britannia.getRegion("Irish Sea").addPiece(britannia.getNation("Dubliners"),3);
			if (n.name.equals("Dubliners")) britannia.board.setInstruction("3 Dubliner armies attack from the Irish Sea");
			if (n.name.equals("Danes")) britannia.getRegion("Frisian Sea").addPiece(britannia.getNation("Danes"),6);
			if (n.name.equals("Danes")) new Leader(britannia,"Cnut").birth(britannia.getRegion("Frisian Sea"));
			if (n.name.equals("Danes")) britannia.board.setInstruction("6 Danish armies, led by Cnut, invade from the Frisian Sea");
		}
		if (round==15)
		{
			if (n.name.equals("Saxons")) new Leader(britannia,"Harold").birth(britannia.getRegion(null));
			if (n.name.equals("Norwegians")) britannia.getRegion("North Sea").addPiece(britannia.getNation("Norwegians"),10);
			if (n.name.equals("Norwegians")) new Leader(britannia,"Harald Hardrada").birth(britannia.getRegion("North Sea"));
			if (n.name.equals("Norwegians")) britannia.board.setInstruction("10 Norwegian armies, led by Harald Hardrada, invade from the North Sea");
			if (n.name.equals("Normans")) britannia.getRegion("English Channel").addPiece(britannia.getNation("Normans"),10);
			if (n.name.equals("Normans")) new Leader(britannia,"William").birth(britannia.getRegion("English Channel"));
			if (n.name.equals("Normans")) britannia.board.setInstruction("10 Norman armies, led by William the Conqueror, invade from the English Channel");
		}
		if (round==16)
		{
			if (n.name.equals("Danes")) britannia.getRegion("Frisian Sea").addPiece(britannia.getNation("Danes"),3);
			if (n.name.equals("Danes")) new Leader(britannia,"Svein Estrithson").birth(britannia.getRegion("Frisian Sea"));
			if (n.name.equals("Danes")) britannia.board.setInstruction("3 Danish armies, led by Svein Estrithson, invade from the Frisian Sea");
		}
		britannia.board.repaintBoard();

		int increase=n.advancePopulation();

		if (n.isSurrendered && increase>1)
			increase=increase/2;

		if (round==16)
		{
			if (n.name.equals("Normans"))
			{
				for (int i=0; i<britannia.region.length; i++)
				{
					if (britannia.region[i].piecesLeader!=null && britannia.region[i].piecesLeader.name.equals("William"))
					{
						increase+=(britannia.getRegion("Essex").isOccupiedBy(n)? 1:0);
						increase+=(britannia.getRegion("Wessex").isOccupiedBy(n)? 1:0);
						increase+=(britannia.getRegion("Hwicce").isOccupiedBy(n)? 1:0);
						increase+=(britannia.getRegion("South Mercia").isOccupiedBy(n)? 1:0);
						int j=0;
						for (j=0; j<britannia.region.length; j++)
							if (britannia.region[i].piecesLeader!=null && britannia.region[i].piecesLeader.name.equals("Harold"))
								break;
						if (j==britannia.region.length)
							increase+=3;
						britannia.board.setInstruction("Normans receive reinforcements");
						break;
					}
				}
			}
			if (n.name.equals("Saxons"))
			{
				int aincrease=0;
				for (int i=0; i<britannia.region.length; i++)
				{
					if (britannia.region[i].piecesLeader!=null && britannia.region[i].piecesLeader.name.equals("Harold"))
					{
						aincrease+=(britannia.getRegion("Essex").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Wessex").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Hwicce").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("South Mercia").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("North Mercia").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Suffolk").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Norfolk").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Sussex").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Kent").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Avalon").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Downlands").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Pennines").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Cumbria").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Cheshire").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("March").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("York").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Bernicia").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Lothian").isOccupiedBy(n)? 1:0);
						aincrease+=(britannia.getRegion("Galloway").isOccupiedBy(n)? 1:0);
						increase+=aincrease/2;
						britannia.board.setInstruction("Saxons receive reinforcements");
						break;
					}
				}
			}
			if (n.name.equals("Norwegians"))
			{
				for (int i=0; i<britannia.region.length; i++)
				{
					if (britannia.region[i].piecesLeader!=null && britannia.region[i].piecesLeader.name.equals("Harald Hardrada"))
					{
						for (int j=0; j<britannia.region.length; j++)
							if (britannia.region[j].isOccupiedBy(n))
								britannia.getRegion("North Sea").addPiece(britannia.getNation("Norwegians"),1);	
						britannia.board.setInstruction("Norwegians receive reinforcements");
						break;							
					}
				}
			}
		}

		for (int i=increase; i>0; i--)		
		{
			if (n.isHuman)
			{
				britannia.board.setInstruction(n.name+": place "+i+" new armies");
				selectRegionExpected=true;
				phaseLock.lockWait();
				selectRegionExpected=false;
			}
			else
			{
				selectRegion=n.ai.populateRegion();
				if (selectRegion!=null)
					selectRegion.centerOnRegion();
			}

			if (selectRegion==null || selectRegion.isSea || selectRegion.isOpen() || !selectRegion.isOccupiedBy(n))
			{
				i++; continue;
			}
			selectRegion.addPiece(n,1);
			britannia.board.repaintBoard();
			if (!n.isHuman)
				britannia.board.pause();
		}
	}

	public void overpopulationPhase(Nation n)
	{
		if (n.name.equals("Romans")) return;
		int allowed=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (!britannia.region[r].isSea && britannia.region[r].isOccupiedBy(n))
			{
				allowed++;
				if (!britannia.region[r].isMountain)
					allowed++;
			}
		}
		int nn;
		for (nn=0; nn<britannia.nation.length; nn++)
			if (britannia.nation[nn]==n)
				break; 
		allowed=(int)Math.min(allowed,new int[]{16,8,10,13,11,7,10,8,11,10,9,18,12,20,6,17,12}[nn]);
		int quantity=n.quantity();
		int remove=quantity-allowed;

		while(remove>0)
		{
			if (n.isHuman)
			{
				britannia.board.setInstruction(n.name+": remove "+remove+" armies");
				selectRegionExpected=true;
				phaseLock.lockWait();
				selectRegionExpected=false;
			}
			else
			{
				selectRegion=n.ai.removePopulation();
				if (selectRegion!=null)
					selectRegion.centerOnRegion();
			}

			if (selectRegion==null || selectRegion.isSea || selectRegion.pieces.size()<=0 || ((Region.Piece)selectRegion.pieces.get(0)).nation!=n)
			{
				continue;
			}
			selectRegion.pieces.remove(0);
			remove--;
//			britannia.board.setInformation("One army of the "+n.name+" is removed from "+selectRegion.name);
			britannia.board.repaintBoard();
			if (!n.isHuman)
				britannia.board.pause();
		}
	}

	public void bretwaldaPhase()
	{
		if (round>=8 && round<=10)
		{
			britannia.board.headerComponent.setAction("Electing Bretwalda");
			int[] n=new int[britannia.nation.length];
			int occupied=0;
			for (int r=2; r<21; r++)
			{
				for (int nn=0; nn<britannia.nation.length; nn++)
				{
					if (britannia.region[r].occupier()==britannia.nation[nn])
					{
						n[nn]++;
						occupied++;
						break;
					}
				}
			}
			for (int nn=0; nn<n.length; nn++)
			{
				if (n[nn]>occupied/2)
				{
					britannia.board.setInstruction("The Bretwalda is elected from the "+britannia.nation[nn].name);
					britannia.board.setInformation("The "+britannia.nation[nn].name+" get 4 victory points for the Bretwalda");
					britannia.nation[nn].victoryPoints+=4;
					return;
				}
			}
			britannia.board.setInformation("No Bretwalda is elected");
		}
		if ((round>=11 && round<=14)||round==16)
		{
			britannia.board.headerComponent.setAction("Annointing King");
			int[] n=new int[britannia.nation.length];
			int occupied=0;
			for (int r=2; r<21; r++)
			{
				for (int nn=0; nn<britannia.nation.length; nn++)
				{
					if (britannia.region[r].occupier()==britannia.nation[nn])
					{
						n[nn]++;
						occupied++;
						break;
					}
				}
			}
			for (int nn=0; nn<n.length; nn++)
			{
				if (n[nn]>2*occupied/3 && n[nn]>=4)
				{
					if (round<16)
					{
						britannia.board.setInstruction("The King of England is elected from the "+britannia.nation[nn].name);
						britannia.board.setInformation(britannia.nation[nn].name+" get 8 victory points for the King");
						britannia.nation[nn].victoryPoints+=8;
						return;
					}
					else
					{
						for (int r=0; r<britannia.region.length; r++)
						{
							if(britannia.region[r].piecesLeader!=null && britannia.region[r].isOccupiedBy(britannia.nation[nn]))
							{
								britannia.board.setInstruction(britannia.region[r].piecesLeader.name+" of the "+britannia.nation[nn].name+" ascends to the throne of England");
								britannia.board.setInformation(britannia.nation[nn].name+" get 8 victory points for King "+britannia.region[r].piecesLeader.name);
								britannia.nation[nn].victoryPoints+=8;
								return;
							}
						}
					}
				}
			}
			britannia.board.setInformation("No King is elected");
		}
	}

	public void movementPhase(Nation currentNation)
	{
		if (currentNation.isHuman)
		{
			for (int r=0; r<britannia.region.length; r++)
			{
				if (britannia.region[r].isOccupiedBy(currentNation))
				{
					britannia.region[r].centerOnRegion();
					break;
				}
			}
			movePieceExpected=true;
			britannia.board.setInstruction("Move "+currentNation.name);
			britannia.board.setControlButton("Done Moving");
			phaseLock.lockWait();
			movePieceExpected=false;
		}
		else
		{
			currentNation.ai.doMovements();
		}

		for (int i=0; i<britannia.region.length; i++)
		{
			britannia.region[i].acceptPieces();
		}
		britannia.board.repaintBoard();
	}

	public void battlePhase(Region r)
	{
		Nation attacker=((Region.Piece)r.piecesInWaiting.get(0)).nation;
		Nation defender=r.occupier();

		r.centerOnRegion();
		britannia.board.headerComponent.setAction("Battle of "+r.name);
		britannia.board.setInformation("Battle of "+r.name);
		britannia.board.pause();
		r.doBattle();
		r.acceptPieces();
		britannia.board.repaintBoard();

		while (r.piecesInWaiting.size()>0)
		{
			if (defender.isHuman)
			{
//				britannia.board.setControlButton("Defender Continue");
//				britannia.board.setControl2Button("Defender Retreat");
				britannia.board.choice(defender.name+": make a choice for "+r.name,"Defender Continue","Defender Retreat");
				phaseLock.lockWait();
			}
			
			if ((!defender.isHuman && defender.ai.doDefenderRetreat(r)) || (defender.isHuman && britannia.board.buttonPressed.equals("Defender Retreat")))
			{
				int itr=r.pieces.size();
				for (int i=0; i<itr; i++)
				{
					if (defender.isHuman)
					{
						britannia.board.setInstruction("Defender: choose retreat location for one piece");
						selectRegionExpected=true;
						phaseLock.lockWait();
						selectRegionExpected=false;
					}
					else
					{
						selectRegion=defender.ai.chooseDefenderRetreat(r);
					}

					//retreat rules: adjacent, occupied by retreating army or nobody, not sea, not the region attacker came from
					//TODO: stacking, no retreat to empty square adjacent to attacking army
					if (selectRegion!=null && r.isAdjacent(selectRegion) && !(selectRegion.isSea && !((Region.Piece)r.pieces.get(0)).nation.isRaider(round)) && selectRegion!=((Region.Piece)r.piecesInWaiting.get(0)).lastRegion)
					{
						if (selectRegion.isOccupiedBy(((Region.Piece)r.pieces.get(0)).nation));
						{
							selectRegion.pieces.add((Region.Piece)r.pieces.remove(0));
							r.acceptPieces();

							if (r.piecesLeader!=null)
							{
								selectRegion.piecesLeader=r.piecesLeader;
								r.piecesLeader=null;
							}

							britannia.board.repaintBoard();
						}
					}
				}
			}
			else
			{
				if (attacker.isHuman)
				{
//					britannia.board.setControlButton("Attacker Continue");
//					britannia.board.setControl2Button("Attacker Retreat");
					britannia.board.choice(attacker.name+": make a choice for "+r.name,"Attacker Continue","Attacker Retreat");
					phaseLock.lockWait();
				}

				if ((!attacker.isHuman && attacker.ai.doAttackerRetreat(r)) || (attacker.isHuman && britannia.board.buttonPressed.equals("Attacker Retreat")))
				{
					ArrayList<Region.Piece> tempList=new ArrayList<Region.Piece>();
					while(r.piecesInWaiting.size()>0)
					{
						Region.Piece p=(Region.Piece)r.piecesInWaiting.remove(0);
						if(p.lastRegion.isSea && !p.nation.isRaider(round))
							tempList.add(p);
						else
							p.lastRegion.pieces.add(p);

						if (r.piecesLeaderInWaiting!=null)
						{
							p.lastRegion.piecesLeader=r.piecesLeaderInWaiting;
							r.piecesLeaderInWaiting=null;
						}
					}
					while(tempList.size()>0)
						r.piecesInWaiting.add((Region.Piece)tempList.remove(0));
					britannia.board.repaintBoard();
				}
				else
				{
					r.doBattle();
					r.acceptPieces();
					britannia.board.repaintBoard();
				}
			}
		}
		britannia.board.pause();
	}

	public boolean canMovePiece(Region source, Region dest)
	{
		//movement rules:
		if (source==null || dest==null) return false;
		//piece exists to be moved
		if (source.pieces.size()==0) return false;
		//correct nation is moved
//		if (((Region.Piece)source.pieces.get(0)).nation!=britannia.game.currentNation) return false;
		//destination region is land unless raiders
		if (dest.isSea && !currentNation.isRaider(round)) return false;

		if (currentNation.name.equals("Romans") && dest.occupier()!=null && dest.occupier().isSurrendered) return false;

		//destination region is adjacent to source region
		if (source.isAdjacent(dest)) return true;

		//how far is destination region?
		int[] rdist=new int[britannia.region.length];
		int destnum=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r]==dest) destnum=r;
			rdist[r]=100;
			if (britannia.region[r]==source) rdist[r]=0;
			if (!britannia.region[r].isSea && currentNation.name.equals("Romans") && source.hasFort && source.isAdjacent(britannia.region[r])) rdist[r]=0;
			if (!britannia.region[r].isSea && source.isAdjacent(britannia.region[r])) rdist[r]=1;
		}
		for (int d=2; d<=britannia.region.length; d++)
		{
			if (d>=4 && !currentNation.name.equals("Romans")) break;
			for (int r=0; r<britannia.region.length; r++)
			{
				if (rdist[r]==100) continue;
				//can't move multiple spaces through mountains or sea
				if (britannia.region[r].isSea && !currentNation.hasBoats(round)) continue;
				if (britannia.region[r].isMountain && britannia.region[r].piecesLeader==null && britannia.region[r].piecesLeaderInWaiting==null) continue;

				//can't move through enemy zone unless overrun
				if (!(currentNation.name.equals("Romans") && britannia.region[r].occupier()!=null && britannia.region[r].occupier().isSurrendered))
					if (britannia.region[r].pieces.size()>0 && ((Region.Piece)britannia.region[r].pieces.get(0)).nation!=currentNation && britannia.region[r].piecesInWaiting.size()<=2*britannia.region[r].pieces.size()) continue;
				for (int r2=0; r2<britannia.region.length; r2++)
				{
					if (currentNation.name.equals("Romans") && (britannia.region[r].hasFort||(britannia.region[r].occupier()!=null && britannia.region[r].occupier().isSurrendered)) && britannia.region[r].isAdjacent(britannia.region[r2]) && rdist[r2]>rdist[r])
						rdist[r2]=rdist[r];

					else if (britannia.region[r].isAdjacent(britannia.region[r2]) && rdist[r2]>rdist[r])
						rdist[r2]=rdist[r]+1;
				}
			}
		}

		if (currentNation.name.equals("Romans") && rdist[destnum]>=0 && rdist[destnum]<=3)
			return true;

		if (rdist[destnum]>=0 && rdist[destnum]<=2)
			return true;

		return false;
	}

	public class Lock
	{
		public void lockWait()
		{
			synchronized(this)
			{
				try
				{
					this.wait();
				}
				catch(InterruptedException e)
				{
				}
			}
		}
		public void lockResume()
		{
			synchronized(this)
			{
				this.notify();
			}
		}
	}
}
