package com.blackware.britannia;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;

public class Region 
{
	public String name;
	private int xcoor,ycoor;
	private String[] adjacent;
	public boolean isSea,isMountain;
	public boolean hasFort=false;
	public boolean hadFort=false;

	public ArrayList<Piece> pieces;
	public Leader piecesLeader=null;
	public ArrayList<Piece> piecesInWaiting;
	public Leader piecesLeaderInWaiting=null;
	public int showDragX=-1,showDragY=-1;
	public int animateBattle=-1;

	private Britannia britannia;

	public Region(Britannia britannia, String name, int xcoor, int ycoor, String[] adjacent, int type)
	{
		this.britannia=britannia;

		this.name=name; this.xcoor=xcoor; this.ycoor=ycoor;
		this.adjacent=adjacent;
		if (type==1) this.isMountain=true; else this.isMountain=false;
		if (type==2) this.isSea=true; else this.isSea=false;
		pieces=new ArrayList<Piece>();
		piecesInWaiting=new ArrayList<Piece>();
	}

	public void addPiece(Nation n, int number)
	{
		for (int i=0; i<number; i++)
			pieces.add(new Piece(n));
//		britannia.board.setInformation(""+number+" "+n.name+" arrive in the "+name);
	}
	
	public void addPieceInitial(Nation n, int number)
	{
		for (int i=0; i<number; i++)
			pieces.add(new Piece(n));		
	}
	

	public void movePiece(Region destination)
	{
		if (destination==this) return;
		Piece p=pieces.remove(pieces.size()-1);
		destination.piecesInWaiting.add(p);
		p.lastRegion=this;
		if (piecesLeader!=null)
		{
			destination.piecesLeaderInWaiting=piecesLeader;
			piecesLeader=null;
		}
//		britannia.board.setInformation(p.nation.name+" move from "+name+" to "+destination.name);
	}

	public void acceptPieces()
	{
		if (piecesInWaiting.size()==0) return;

		if (isOpen() || isOccupiedBy(((Piece)piecesInWaiting.get(0)).nation))
		{
			int n=piecesInWaiting.size();
			for (int i=0; i<n; i++)
				pieces.add((Piece)piecesInWaiting.remove(0));

			if (piecesLeaderInWaiting!=null)
			{
				piecesLeader=piecesLeaderInWaiting;
				piecesLeaderInWaiting=null;
			}
		}
	}

	private void displayBattle(int[] attack, int[] defend)
	{
		String text="Battle for "+name+": Attack ";
		for (int i=0; i<attack.length; i++)
			text+=attack[i]+" ";
		text+="Defend ";
		for (int i=0; i<defend.length; i++)
			text+=defend[i]+" ";
		System.out.println(text);

		britannia.board.pause();
		britannia.board.aDice=attack;
		britannia.board.dDice=defend;
//		britannia.board.informationComponent.repaint();

		for (animateBattle=0; animateBattle<20; animateBattle++)
		{
			britannia.board.postInvalidate(xcoor-35, ycoor+35, xcoor-35+xcoor, ycoor+35+ycoor);
			Timer timer=new Timer();
			timer.schedule(new TimerTask(){
				public void run() {
					Looper.prepare();
					britannia.board.repaintBoard();
					britannia.game.iLock.lockResume();					
				}}, britannia.board.ANIMATION_DELAY/20);
			britannia.game.iLock.lockWait();
		}
		animateBattle=-1;
//		britannia.board.pause();
	}

	public void doBattle()
	{
		int attackDice=piecesInWaiting.size();
		int defendDice=pieces.size()+(hasFort? 1:0);

		int[] attackRolls=new int[attackDice];
		int[] defendRolls=new int[defendDice];

		for (int i=0; i<attackDice; i++)
			attackRolls[i]=britannia.random.nextInt(6)+1+(piecesLeaderInWaiting!=null? 1:0);
		for (int i=0; i<defendDice; i++)
			defendRolls[i]=britannia.random.nextInt(6)+1+(piecesLeader!=null? 1:0);

		displayBattle(attackRolls,defendRolls);

		//attacker kills on 5/6 normal terrain, 4/5/6 if Roman, 6 on difficult terrain
		//TODO: forts are destroyed on 5 or 6, only kill on 5/6

		int attackKills=0;
		for (int i=0; i<attackDice; i++)
		{
			if (attackRolls[i]>=6)
				attackKills++;
			else
			{
				if (!isMountain && !isOccupiedBy(britannia.getNation("Romans")))
				{
					if (attackRolls[i]==5)
						attackKills++;
					else if (attackRolls[i]==4 && ((Piece)piecesInWaiting.get(0)).nation.name.equals("Romans"))
						attackKills++;
				}
			}
		}
		int defendKills=0;
		for (int i=0; i<defendDice; i++)
		{
			if (defendRolls[i]>=6)
				defendKills++;
			else
			{
				if (!((Piece)piecesInWaiting.get(0)).nation.name.equals("Romans"))
				{
					if (defendRolls[i]==5)
						defendKills++;
					else if (defendRolls[i]==4 && isOccupiedBy(britannia.getNation("Romans")))
						defendKills++;
				}
			}
		}
		Nation attackNation=((Piece)piecesInWaiting.get(0)).nation;
		Nation defendNation=null;
		if (pieces.size()>0)
			defendNation=((Piece)pieces.get(0)).nation;
		if (defendNation==null && hasFort) defendNation=britannia.getNation("Romans");

		int i;
		String information="";
		if (attackKills>0 && pieces.size()>0)
			information+=""+(int)Math.min(attackKills,pieces.size())+" "+occupier().name+" are killed.  ";
		if (defendKills>0 && piecesInWaiting.size()>0)
			information+=""+(int)Math.min(defendKills,piecesInWaiting.size())+" "+((Piece)piecesInWaiting.get(0)).nation.name+" are killed.  ";

		for (i=0; i<attackKills && pieces.size()>0; i++)
		{
			pieces.remove(0);
			attackNation.victoryPointDatabase.assignKillVictoryPoints(britannia.game.round,defendNation);
		}
		if (hasFort && i<attackKills)
		{
			hasFort=false;
			hadFort=true;
			attackNation.victoryPointDatabase.assignBurnFortVictoryPoints(britannia.game.round);
			information+=""+"The Roman fort is destroyed.";
		}
		for (i=0; i<defendKills && piecesInWaiting.size()>0; i++)
		{
			piecesInWaiting.remove(0);
			defendNation.victoryPointDatabase.assignKillVictoryPoints(britannia.game.round,attackNation);
		}
		if (pieces.size()==0 && piecesLeader!=null)
		{
			attackNation.victoryPointDatabase.assignKillLeaderVictoryPoints(britannia.game.round,piecesLeader);
			information+=""+piecesLeader.name+" is dead.";
			piecesLeader=null;
		}
		if (piecesInWaiting.size()==0 && piecesLeaderInWaiting!=null)
		{
			defendNation.victoryPointDatabase.assignKillLeaderVictoryPoints(britannia.game.round,piecesLeaderInWaiting);
			information+=""+piecesLeaderInWaiting.name+" is dead.";
			piecesLeaderInWaiting=null;
		}
//		britannia.board.setInformation(information);
	}

	public void drawPieces(Canvas g)
	{
		Paint paint=new Paint();
		if (hasFort)
			g.drawBitmap(britannia.board.fortImage,xcoor-40,ycoor+10,paint);
		else if (hadFort)
			g.drawBitmap(britannia.board.ruinedFortImage,xcoor-40,ycoor+10,paint);
		for (int i=0; i<pieces.size(); i++)
		{
			Piece p=(Piece)pieces.get(i);
			if (i<pieces.size()-1 || showDragX<0)
				p.drawPiece(g,xcoor,ycoor,i);
			else
				p.drawPiece(g,showDragX,showDragY,0);
		}
		if (piecesLeader!=null)
		{
			paint.setTextSize(13);
			paint.setColor(Color.WHITE);
			g.drawRect(xcoor-25,ycoor+7,xcoor-25+60,ycoor+7+15, paint);
			paint.setColor(Color.BLACK);
			g.drawText(piecesLeader.name,xcoor-20,ycoor+20,paint);
		}
	}

	public void drawPiecesInWaiting(Canvas g)
	{
		Paint paint=new Paint();
		for (int i=0; i<piecesInWaiting.size(); i++)
		{
			Piece p=(Piece)piecesInWaiting.get(i);
			p.drawPiece(g,xcoor-30,ycoor+30,i);

			drawArrow(g,p.lastRegion.xcoor,p.lastRegion.ycoor,xcoor-30,ycoor+30);
		}
		if (piecesLeaderInWaiting!=null)
		{
			paint.setColor(Color.WHITE);
			g.drawRect(xcoor-25-30,ycoor+7+30,60,15,paint);
//			g.setColor(Color.BLACK);
//			g.drawRect(xcoor-25-30,ycoor+7+30,60,15);
			paint.setTextSize(13);
			paint.setColor(Color.BLACK);
			g.drawText(piecesLeaderInWaiting.name,xcoor-20-30,ycoor+20+30,paint);
		}

		if (animateBattle>=0)
		{
			paint.setColor(Color.RED);
//			g.setColor(Color.RED);
			for (int i=0; i<piecesInWaiting.size(); i++)
			{
				g.drawLine(xcoor-30+animateBattle,ycoor+30-animateBattle-3*i,xcoor-30+animateBattle+10,ycoor+30-animateBattle-10-3*i,paint);
				g.drawLine(xcoor-30+animateBattle+10-1,ycoor+30-animateBattle-10-3*i,xcoor-30+animateBattle+10,ycoor+30-animateBattle-10+1-3*i,paint);
				g.drawLine(xcoor-30+animateBattle+10-2,ycoor+30-animateBattle-10-3*i,xcoor-30+animateBattle+10,ycoor+30-animateBattle-10+2-3*i,paint);
				g.drawLine(xcoor-30+animateBattle-1,ycoor+30-animateBattle-3*i,xcoor-30+animateBattle,ycoor+30-animateBattle+1-3*i,paint);
				g.drawLine(xcoor-30+animateBattle-2,ycoor+30-animateBattle-3*i,xcoor-30+animateBattle,ycoor+30-animateBattle+2-3*i,paint);
			}
		}
	}

	private void drawArrow(Canvas g, int x1, int y1, int x2, int y2)
	{
		Paint paint=new Paint();
		paint.setColor(Color.GREEN);
//		g.setColor(Color.GREEN);
		g.drawLine(x1,y1,x2,y2,paint);
	}

	public void centerOnRegion()
	{
		britannia.board.positionAt(xcoor, ycoor);
//		if (!britannia.board.boardpane.getViewport().getViewRect().contains(xcoor,ycoor))
//			britannia.board.boardpane.getViewport().setViewPosition(new Point(xcoor-Board.XBSIZE/2,ycoor-Board.YBSIZE/2));
	}

	public boolean isMouse(int x, int y)
	{
		double RADIUS=50.0;

		if (Math.abs(x-xcoor)<RADIUS && Math.abs(y-ycoor)<RADIUS) return true;
		return false;
	}

	public boolean isAdjacent(Region r)
	{
		for (int i=0; i<adjacent.length; i++)
			if (adjacent[i].equals(r.name)) return true;
		return false;
	}

	public boolean isOpen()
	{
		if (pieces.size()==0 && !hasFort) return true;
		return false;
	}

	public boolean isOccupiedBy(Nation n)
	{
		if (pieces.size()>0 && ((Piece)pieces.get(0)).nation==n)
			return true;
		if (pieces.size()==0 && hasFort && n.name.equals("Romans"))
			return true;
		return false;
	}

	public Nation occupier()
	{
		if (hasFort)
			return britannia.getNation("Romans");
		else if (pieces.size()>0)
			return ((Piece)pieces.get(0)).nation;
		else return null;
	}

	public class Piece
	{
		public Nation nation;
		public Region lastRegion;
		public Piece(Nation nation)
		{
			this.nation=nation;
		}
		public void drawPiece(Canvas c, int xcoor, int ycoor, int offset)
		{
			nation.drawPiece(c,xcoor,ycoor,offset);
		}
	}

}
