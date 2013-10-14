package com.blackware.britannia;

import java.util.ArrayList;

import android.graphics.Color;

public class AI 
{
	private Britannia britannia;
	private Nation nation;

	public AI(Britannia britannia, Nation nation)
	{
		this.britannia=britannia;
		this.nation=nation;
	}

	public Region chooseDefenderRetreat(Region reg)
	{
		int i=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r].isAdjacent(reg) && britannia.region[r].isOccupiedBy(nation))
				i++;
		}
		int j=britannia.random.nextInt(i);
		i=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r].isAdjacent(reg) && britannia.region[r].isOccupiedBy(nation))
			{
				if (i==j)
				{
					return britannia.region[r];
				}
				i++;
			}
		}
		return null;
	}

	public boolean doDefenderRetreat(Region reg)
	{
		int i;
		for (i=0; i<britannia.region.length; i++)
			if (britannia.region[i].isAdjacent(reg) && britannia.region[i].isOccupiedBy(nation))
				break;
		if (i==britannia.region.length) return false;

		i=4-nation.PESSIMISM+reg.piecesInWaiting.size()-reg.pieces.size();
		return britannia.random.nextInt(10)>i;
	}

	public boolean doAttackerRetreat(Region r)
	{
		int i=4-nation.PESSIMISM+r.piecesInWaiting.size()-r.pieces.size();
		return britannia.random.nextInt(10)>i;
	}

	public boolean doSurrender()
	{
		if (nation.canSurrender(britannia.game.round))
		{
			int i=4-nation.PESSIMISM+nation.quantity();
			return britannia.random.nextInt(15)>i;
		}
		return false;
	}

	public Region removePopulation()
	{
		// just remove a random piece for now
		int i=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r].isOccupiedBy(nation) && britannia.region[r].pieces.size()>1)
				i++;
		}
		if (i==0)
		{
			for (int r=0; r<britannia.region.length; r++)
			{
				if (britannia.region[r].isOccupiedBy(nation) && britannia.region[r].pieces.size()>0)
					i++;
			}
			int j=britannia.random.nextInt(i);
			i=0;
			for (int r=0; r<britannia.region.length; r++)
			{
				if (britannia.region[r].isOccupiedBy(nation) && britannia.region[r].pieces.size()>0)
				{
					if (i==j)
					{
						System.out.println(nation.name+" remove population from "+britannia.region[r].name);
						return britannia.region[r];
					}
					i++;
				}
			}
		}
		else
		{
			int j=britannia.random.nextInt(i);
			i=0;
			for (int r=0; r<britannia.region.length; r++)
			{
				if (britannia.region[r].isOccupiedBy(nation) && britannia.region[r].pieces.size()>1)
				{
					if (i==j)
					{
						System.out.println(nation.name+" remove population from "+britannia.region[r].name);
						return britannia.region[r];
					}
					i++;
				}
			}
		}
		return null;
	}

	public Region populateRegion()
	{
		// just choose a random occupied square for now
		int i=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r].isOccupiedBy(nation))
				i++;
		}
		int j=britannia.random.nextInt(i);
		i=0;
		for (int r=0; r<britannia.region.length; r++)
		{
			if (britannia.region[r].isOccupiedBy(nation))
			{
				if (i==j)
				{
					System.out.println(nation.name+" add population to "+britannia.region[r].name);
					return britannia.region[r];
				}
				i++;
			}
		}
		return null;
	}

	public void doMovements()
	{
		ArrayList<PieceToMove> possibleMovements=findAllPossibleMovements();
		if (nation.pieceColor!=Color.MAGENTA)
			makeGreedyMoves(possibleMovements);
		else
			makeRandomMoves(possibleMovements);
		britannia.board.repaintBoard();
	}

	private ArrayList<PieceToMove> findAllPossibleMovements()
	{
		ArrayList<PieceToMove> pList=new ArrayList<PieceToMove>();
		for (int s=0; s<britannia.region.length; s++)
		{
			if (!britannia.region[s].isOccupiedBy(nation) || britannia.region[s].pieces.size()==0) continue;
			for (int i=0; i<britannia.region[s].pieces.size(); i++)
			{
				PieceToMove p=new PieceToMove(britannia.region[s]);
				pList.add(p);
			}
		}
		return pList;
	}

	//move every piece randomly
	private void makeRandomMoves(ArrayList<PieceToMove> pList)
	{
		for (int p=0; p<pList.size(); p++)
		{
			PieceToMove piece=(PieceToMove)pList.get(p);
			int d=britannia.random.nextInt(piece.destinations.size());
			piece.source.movePiece((Region)piece.destinations.get(d));
		}
	}

	private void makeGreedyMoves(ArrayList<PieceToMove> pList)
	{
		int OCCUPYING_FACTOR=10;
		int HOLDING_FACTOR_1TURN=10;
		int HOLDING_FACTOR_2TURN=8;
		int HOLDING_FACTOR_3TURN=6;
		int HOLDING_FACTOR_4TURN=5;
		int HOLDING_FACTOR_5TURN=4;
		int HOLDING_FACTOR_6TURN=3;
		int HOLDING_FACTOR_7TURN=3;
		int HOLDING_FACTOR_8TURN=3;
		int HOLDING_FACTOR_9TURN=3;
		int HOLDING_FACTOR_10TURN=3;
		int HOLDING_FACTOR_11TURN=3;
		int HOLDING_FACTOR_12TURN=3;
		int HOLDING_FACTOR_13TURN=3;
		int HOLDING_FACTOR_14TURN=3;
		int HOLDING_FACTOR_15TURN=3;
		int HOLDING_FACTOR_16TURN=3;
		int ARMY_KILLING_FACTOR=5;
		int FORT_BURNING_FACTOR=5;
		double OCCUPATION_PESSIMISM_FACTOR=1.5;
		double BATTLE_PESSIMISM_FACTOR=2.0+(double)nation.PESSIMISM/2;
		int RANDOM_FACTOR=4;

		//evaluate the desirability of each destination
		int[] destRegionValue=new int[britannia.region.length];
		//guess number of pieces to hold destination
		int[] destRegionNeed=new int[britannia.region.length];
		for (int r=0; r<britannia.region.length; r++)
		{
			//consider:
			//points obtained immediately by occupying region
			destRegionValue[r]+=nation.victoryPointDatabase.getOccupyVictoryPoints(britannia.game.round,britannia.region[r].name)*OCCUPYING_FACTOR;
			//points obtained this turn by holding region
			destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round,britannia.region[r].name)*HOLDING_FACTOR_1TURN;
			//next turn
			if (britannia.game.round<=15)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_2TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_2TURN:0);
			//two turns from now
			if (britannia.game.round<=14)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+2,britannia.region[r].name)*HOLDING_FACTOR_3TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_3TURN:0);
			//three turns from now
			if (britannia.game.round<=13)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+3,britannia.region[r].name)*HOLDING_FACTOR_4TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_4TURN:0);
			//four turns from now
			if (britannia.game.round<=12)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+4,britannia.region[r].name)*HOLDING_FACTOR_5TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_5TURN:0);
			if (britannia.game.round<=11)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+5,britannia.region[r].name)*HOLDING_FACTOR_6TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_6TURN:0);
			if (britannia.game.round<=10)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+6,britannia.region[r].name)*HOLDING_FACTOR_7TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_7TURN:0);
			if (britannia.game.round<=9)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+7,britannia.region[r].name)*HOLDING_FACTOR_8TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_8TURN:0);
			if (britannia.game.round<=8)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+8,britannia.region[r].name)*HOLDING_FACTOR_9TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_9TURN:0);
			if (britannia.game.round<=7)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+9,britannia.region[r].name)*HOLDING_FACTOR_10TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_10TURN:0);
			if (britannia.game.round<=6)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+10,britannia.region[r].name)*HOLDING_FACTOR_11TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_11TURN:0);
			if (britannia.game.round<=5)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+11,britannia.region[r].name)*HOLDING_FACTOR_12TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_12TURN:0);
			if (britannia.game.round<=4)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+12,britannia.region[r].name)*HOLDING_FACTOR_13TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_13TURN:0);
			if (britannia.game.round<=3)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+13,britannia.region[r].name)*HOLDING_FACTOR_14TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_14TURN:0);
			if (britannia.game.round<=2)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+14,britannia.region[r].name)*HOLDING_FACTOR_15TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_15TURN:0);
			if (britannia.game.round<=1)
				destRegionValue[r]+=nation.victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+15,britannia.region[r].name)*HOLDING_FACTOR_16TURN+(nation.name.equals("Romans")? britannia.getNation("Romano-British").victoryPointDatabase.getHoldVictoryPoints(britannia.game.round+1,britannia.region[r].name)*HOLDING_FACTOR_16TURN:0);
			//points obtained from killing an enemy army
			if (britannia.region[r].pieces.size()>0 && !britannia.region[r].isOccupiedBy(nation))
				destRegionValue[r]+=nation.victoryPointDatabase.getKillVictoryPoints(britannia.game.round,britannia.region[r].occupier())*ARMY_KILLING_FACTOR;
			//points obtained from burning a fort
			if (britannia.region[r].hasFort && !nation.name.equals("Romans"))
				destRegionValue[r]+=nation.victoryPointDatabase.getBurnFortVictoryPoints(britannia.game.round)*FORT_BURNING_FACTOR;

			//add on a random factor to make things interesting
			destRegionValue[r]+=britannia.random.nextInt(RANDOM_FACTOR*2)-RANDOM_FACTOR;

			//cost of region?
			//unoccupied or friendly occupied
			if (britannia.region[r].isOpen() || britannia.region[r].isOccupiedBy(nation))
				destRegionNeed[r]=(int)OCCUPATION_PESSIMISM_FACTOR;
			//enemy occupied
			else
				destRegionNeed[r]=(int)((britannia.region[r].pieces.size()+(britannia.region[r].hasFort? 1:0))*BATTLE_PESSIMISM_FACTOR);
			//if friendly occupied and we're the romans, don't need as many troops
			if (britannia.region[r].isOccupiedBy(nation)&&britannia.region[r].hasFort)
				destRegionNeed[r]-=1;
		}

		//sort destinations by value
		int[] destPriority=new int[britannia.region.length];
		boolean[] destHandled=new boolean[britannia.region.length];
		int highest;
		for (int i=0; i<destPriority.length; i++)
		{
			highest=-1;
			for (int j=0; j<destPriority.length; j++)
			{
				if (destHandled[j]) continue;
				if (highest==-1) highest=j;
				if (destRegionValue[j]>destRegionValue[highest]) highest=j;
			}
			destPriority[i]=highest;
			destHandled[highest]=true;
		}

		//move needed armies to destinations in priority order
		for (int i=0; i<destPriority.length; i++)
		{
			//get list of all who can serve this destination
			int[] dlist=new int[britannia.region.length];
			int dlistsize=0;
			for (int j=0; j<pList.size(); j++)
			{
				PieceToMove p=(PieceToMove)pList.get(j);
				if (p.marked) continue;
				int k;
				for (k=0; k<p.destinations.size(); k++)
				{
					if ((Region)p.destinations.get(k)==britannia.region[destPriority[i]])
						break;
				}
				if (k==p.destinations.size()) continue;
				dlist[dlistsize++]=j;
			}

			//randomize list ordering
			int[] dlist2=new int[dlistsize];
			for (int j=0; j<dlistsize; j++)
				dlist2[j]=dlist[j];
			for (int j=0; j<dlistsize; j++)
			{
				int k=britannia.random.nextInt(dlistsize);
				int l=dlist2[j];
				dlist2[j]=dlist2[k];
				dlist2[k]=l;
			}

			//set the destinations for all the units, if not already determined, and mark the needed units
			int k=0;
			for (int j=0; j<dlistsize; j++)
			{
				PieceToMove p=(PieceToMove)pList.get(dlist2[j]);
				if (p.marked) continue;
				if (k<destRegionNeed[destPriority[i]])
				{
					p.marked=true;
					p.destinationChoice=britannia.region[destPriority[i]];
					k++;
				}
				else if (p.destinationChoice==null)
					p.destinationChoice=britannia.region[destPriority[i]];
			}
		}
		//now move everybody to their destinations
		for (int p=0; p<pList.size(); p++)
		{
			PieceToMove piece=(PieceToMove)pList.get(p);
			piece.destinationChoice.centerOnRegion();
			piece.source.movePiece(piece.destinationChoice);
			britannia.board.repaintBoard();
			britannia.board.pause();
		}
	}

	//represents the movement possibilities of one piece
	private class PieceToMove
	{
		public Region source;
		public ArrayList<Region> destinations;
		public boolean marked;
		public Region destinationChoice=null;
		public PieceToMove(Region r)
		{
			source=r;
			destinations=new ArrayList<Region>();
			for (int d=0; d<britannia.region.length; d++)
			{
				if (britannia.game.canMovePiece(source,britannia.region[d]))
					destinations.add(britannia.region[d]);
			}
		}
	}
}
