package com.blackware.britannia;

public class Leader 
{
	public Britannia britannia;
	public String name;
	public Nation nation;
	public int startTurn;

	public Leader(Britannia britannia, String name)
	{
		this.britannia=britannia; this.name=name; this.nation=britannia.game.currentNation; this.startTurn=britannia.game.round;
	}

	public void birth(Region r)
	{
		if (r!=null && !r.isOccupiedBy(nation)) return;
		//select a random occupied region
		else if (r==null)
		{
			int j=0;
			for (int i=0; i<britannia.region.length; i++)
				if (!britannia.region[i].isSea && britannia.region[i].isOccupiedBy(nation)) j++;
			if (j==0) return;
			int k=britannia.random.nextInt(j);
			j=0;
			for (int i=0; i<britannia.region.length; i++)
			{
				if (!britannia.region[i].isSea && britannia.region[i].isOccupiedBy(nation))
				{
					if (j==k)
					{
						r=britannia.region[i];
						break;
					}
					j++;
				}
			}
		}
		if (!r.isSea) britannia.board.setInstruction(name+" leads the "+nation.name);
		r.piecesLeader=this;
	}
}
