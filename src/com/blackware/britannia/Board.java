package com.blackware.britannia;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Board extends View implements View.OnTouchListener
{
	public static final int XSIZE=1100,YSIZE=700;
	public static final int XISIZE=1352,YISIZE=1738;
	public static final int XHSIZE=XSIZE,YHSIZE=30;
	public static final int XINFSIZE=XSIZE, YINFSIZE=60;
	public static final int XBSIZE=XSIZE,YBSIZE=YSIZE-YHSIZE-YINFSIZE;
	public int INSTRUCTION_DELAY=300;
	public int ANIMATION_DELAY=500;
	public int[] aDice={},dDice={};
	public Handler handler;
	private boolean zoommode=false;

	public InformationComponent informationComponent;
	public HeaderComponent headerComponent;
	public Bitmap britainImage=null, fortImage=null, ruinedFortImage=null;
	public Bitmap[] diceImage=null;
	private Britannia britannia;
	public String buttonPressed="";
	private Button button,button2;
	private Region selectRegion=null;
	private float downX,downX2;
	private float downY,downY2;
	private float SCALE=(float)0.5;
	private boolean moveAPiece=false;
	private boolean selectAPiece=false;
	private int pressedX,pressedY;

	public Board(Context context)
	{
		super(context);
		handler=new Handler();
	}
	public void makeBoard(final Britannia britannia)
	{
		this.britannia=britannia;
		
        britainImage = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.britain1));
        fortImage = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fort));
        ruinedFortImage = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fortruins));
        diceImage = new Bitmap[]{null,Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dice1)),Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dice2)),Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dice3)),Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dice4)),Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dice5)),Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dice6))};

//		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		LinearLayout layout = new LinearLayout(britannia);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		headerComponent=new HeaderComponent(britannia);
		headerComponent.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,YHSIZE));
		layout.addView(headerComponent);
		
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,britannia.getWindowManager().getDefaultDisplay().getHeight()-80-YHSIZE));
		layout.addView(this);
		
		LinearLayout blayout = new LinearLayout(britannia);
		blayout.setOrientation(LinearLayout.HORIZONTAL);
		blayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		ButtonListener bl=new ButtonListener();
		
		button=new Button(britannia);
		button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		button.setVisibility(Button.INVISIBLE);
		button.setOnClickListener(bl);
		button2=new Button(britannia);
		button2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		button2.setVisibility(Button.INVISIBLE);
		button2.setOnClickListener(bl);
		blayout.addView(button);
		blayout.addView(button2);
		
		layout.addView(blayout);
		
		britannia.setContentView(layout);
       			
		scrollTo(0,0);
		invalidate();
       
        
        setOnTouchListener(this);
        
		informationComponent=new InformationComponent();
	}

	protected void onDraw(Canvas canvas)
	{
		canvas.scale(SCALE, SCALE);
		super.onDraw(canvas);
//		canvas.translate(((float)canvas.getWidth()-XISIZE)/2,((float)canvas.getHeight()-YISIZE)/2);
		
		Paint paint=new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawBitmap(britainImage, 0, 0, paint);

		for (int r=0; r<britannia.region.length; r++)
		{
			britannia.region[r].drawPieces(canvas);
			britannia.region[r].drawPiecesInWaiting(canvas);
		}
	}	
	private void phaseLockResume()
	{
		britannia.game.phaseLock.lockResume();
	}

	private void doMovePiece(int startx, int starty, int endx, int endy)
	{
		Region startRegion=null, endRegion=null;

		if (!britannia.game.movePieceExpected) return;

		for (int i=0; i<britannia.region.length; i++)
		{
			if (britannia.region[i].isMouse(startx,starty))
				startRegion=britannia.region[i];
			if (britannia.region[i].isMouse(endx,endy))
				endRegion=britannia.region[i];
		}
		if (startRegion==null || endRegion==null || !startRegion.isOccupiedBy(britannia.game.currentNation) || startRegion.pieces.size()==0) return;

		if (!britannia.game.canMovePiece(startRegion,endRegion)) return;

		startRegion.movePiece(endRegion);
//		repaintBoard();
		postInvalidate();
	}

	public void declareVictory()
	{
		handler.post(new Runnable(){
			public void run()
			{
				new Victory(britannia,true);
			}
		});
	}
	
	public void setControlButton(final String text)
	{
		handler.post(new Runnable(){
			public void run()
			{
 		button.setText(text);
		if (text.equals(""))
			button.setVisibility(Button.INVISIBLE);
		else
		{
			button.setVisibility(Button.VISIBLE);
			button.requestFocus();
		}
			}});
	}

	public void setControl2Button(final String text)
	{
		handler.post(new Runnable(){
			public void run()
			{
 		button2.setText(text);
		if (text.equals(""))
			button2.setVisibility(Button.INVISIBLE);
		else
		{
			button2.setVisibility(Button.VISIBLE);
			button2.requestFocus();
		}
			}});
	}

    private class ButtonListener implements View.OnClickListener
    {
		public void onClick(View v) 
		{
			if (((Button)v).getText().equals("Exit"))
				System.exit(0);
			else if (((Button)v).getText().equals("Start Game"))
				phaseLockResume();
			else if (((Button)v).getText().equals("Objectives"))
				new Objectives(britannia);
			else if (((Button)v).getText().equals("Victory Points"))
				new Victory(britannia,false);
			else
			{
				buttonPressed=((Button)v).getText().toString(); 
				setControlButton(""); 
				setControl2Button(""); 
				phaseLockResume();	
			}
		}
    }
	
	public void repaintBoard()
	{
		postInvalidate();
	}

	public void setInformation(final String text)
	{
		if (text.equals("")) return;

		handler.post(new Runnable(){
			public void run()
			{
		    	Toast t=Toast.makeText(britannia, text, Toast.LENGTH_SHORT);
		    	t.setDuration(INSTRUCTION_DELAY);
		    	t.show();
			}
		});
 	}

	public void setInstruction(String text)
	{
		if (text.equals("")) return;
		
		setInformation(text);
	}

	public void pause()
	{
		Timer timer=new Timer();
		timer.schedule(new TimerTask(){
			public void run() {
				Looper.prepare();
				britannia.game.iLock.lockResume();
			}}, ANIMATION_DELAY);
		britannia.game.iLock.lockWait();
	}

	public class InformationComponent
	{
		public String information="";
		public InformationComponent()
		{
			super();
		}
/*		public void paintComponent(Graphics g)
		{
			g.setColor(Color.WHITE);
			g.fillRect(0,0,XINFSIZE,YINFSIZE);

			if (aDice.length>0)
			{
				for (int i=0; i<aDice.length; i++)
					g.drawImage(diceImage[aDice[i]],XSIZE-230*2-35-35*i-35*dDice.length-10,2,null);
				g.setColor(Color.BLACK);
				g.fillRect(XSIZE-230*2-35*dDice.length-7,0,1,YINFSIZE);
			}
			if (dDice.length>0)
			{
				for (int i=0; i<dDice.length; i++)
					g.drawImage(diceImage[dDice[i]],XSIZE-230*2-35-35*i,2,null);
			}

			g.setColor(Color.BLACK);
			g.setFont(new Font("URW Bookman L",Font.BOLD,15));
			g.drawString(information,50,17);
		}*/
	}

	public boolean onTouch(View v, MotionEvent event) 
	{
		float x=event.getX();
		float y=event.getY();
		float dx=downX-x;
		float dy=downY-y;
		int xxx=(int)((x+getScrollX())/SCALE);
		int yyy=(int)((y+getScrollY())/SCALE);
		
		if (getScrollY()+dy<0)
			dy=-getScrollY();
		if (getScrollX()+dx<0)
			dx=-getScrollX();
		if (getScrollY()+dy>YISIZE*SCALE-v.getHeight())
			dy=YISIZE*SCALE-v.getHeight()-getScrollY();
		if (getScrollX()+dx>XISIZE*SCALE-v.getWidth())
			dx=XISIZE*SCALE-v.getWidth()-getScrollX();

		//are we moving a piece or scrolling the board?
		if (event.getActionMasked()==MotionEvent.ACTION_DOWN)
		{
			moveAPiece=false;
			if (britannia.game.movePieceExpected)
			{
				Region region=null;
				moveAPiece=true;
				for (int i=0; i<britannia.region.length; i++)
				{
					if (britannia.region[i].isMouse(xxx,yyy))
						region=britannia.region[i];
				}
				if (region==null || !region.isOccupiedBy(britannia.game.currentNation) || region.pieces.size()==0)
					moveAPiece=false;
			}
			else if (britannia.game.selectRegionExpected)
			{
				selectAPiece=true;
			}

			britannia.game.selectRegion=null;
		}

		//movement: either scrolling or zooming
		if (event.getActionMasked()==MotionEvent.ACTION_MOVE && !moveAPiece && !selectAPiece)
		{
			if (zoommode)
			{
				float dist1=(downX-downX2)*(downX-downX2)+(downY-downY2)*(downY-downY2);
				float dist2=(event.getX(0)-event.getX(1))*(event.getX(0)-event.getX(1))+(event.getY(0)-event.getY(1))*(event.getY(0)-event.getY(1));
				if (dist2<dist1)
					SCALE=SCALE-(float)0.01;
				else if (dist2>dist1)
					SCALE=SCALE+(float)0.01;
				if (SCALE>1)
					SCALE=1;
				if (SCALE<0.1)
					SCALE=(float)0.1;
				postInvalidate();
				
				downX2=event.getX(1);
				downY2=event.getY(1);
				downX=event.getX(0);
				downY=event.getY(0);
			}			
			else
			{
				scrollBy((int)(dx),(int)(dy));
				downX=x;
				downY=y;
			}
		}

		//second finger down: switch to zoom
		else if (event.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN && !moveAPiece && !selectAPiece)
		{
			downX=event.getX(0);
			downY=event.getY(0);
			downX2=event.getX(1);
			downY2=event.getY(1);
			zoommode=true;
		}

		//second finger up: not zooming anymore
		else if (event.getActionMasked()==MotionEvent.ACTION_POINTER_UP)
		{
			zoommode=false;
		}
		
		//scrolling
		else if (event.getActionMasked()==MotionEvent.ACTION_DOWN && !moveAPiece)
		{
			downX=x;
			downY=y;
		}

		//piece movement: choosing a piece
		else if (event.getActionMasked()==MotionEvent.ACTION_DOWN && moveAPiece)
		{
			if (selectRegion!=null) { selectRegion.showDragX=-1; selectRegion.showDragY=-1; repaintBoard();}

			pressedX=xxx; pressedY=yyy;

			for (int i=0; i<britannia.region.length; i++)
			{
				if (britannia.region[i].isMouse(xxx,yyy))
					selectRegion=britannia.region[i];
			}

			selectRegion.showDragX=xxx; selectRegion.showDragY=yyy;
		}
		
		//piece movement: drag the piece
		else if (event.getActionMasked()==MotionEvent.ACTION_MOVE && moveAPiece)
		{
			if (selectRegion!=null && selectRegion.showDragX>=0)
			{
				selectRegion.showDragX=xxx; selectRegion.showDragY=yyy;
				repaintBoard();
			}
		}
		//piece movement: place the piece
		else if (event.getActionMasked()==MotionEvent.ACTION_UP && moveAPiece)
		{
			moveAPiece=false;
			if (selectRegion!=null) { selectRegion.showDragX=-1; selectRegion.showDragY=-1; repaintBoard();}

			doMovePiece(pressedX,pressedY,xxx,yyy);
			button2.requestFocus();
			button.requestFocus();

		
		}
		//scroll or zoom
		else if (event.getActionMasked()==MotionEvent.ACTION_UP && !moveAPiece)
		{
			if (!zoommode)
			{
				scrollBy((int)(dx),(int)(dy));
				if (dx!=0 || dy!=0)
					selectAPiece=false;
			}
			zoommode=false;
		}

		//clicking on a region
		if (event.getActionMasked()==MotionEvent.ACTION_UP && selectAPiece)
		{
			selectAPiece=false;
			for (int r=0; r<britannia.region.length; r++)
				if (britannia.region[r].isMouse(xxx,yyy))
					britannia.game.selectRegion=britannia.region[r];
			britannia.game.phaseLock.lockResume();
		}
		return true;
	}
	
	public class HeaderComponent extends View
	{
		private String action;
		public HeaderComponent(Context c)
		{
			super(c);
			action="";
		}
		public void onDraw(Canvas g)
		{
			super.onDraw(g);
			Paint paint=new Paint();
			g.drawColor((255<<24)+(0xfb<<16)+(0xde<<8)+0x93);
			paint.setTextSize(15);
			paint.setColor(Color.BLACK);
			if (britannia.game==null) return;
			String message="AD ";
			message+=britannia.game.year[britannia.game.round]+" (";
			message+="Turn "+britannia.game.round+")";
			if (britannia.game.currentNation!=null)
				message+=":  The "+britannia.game.currentNation.name;
			g.drawText(message,50,YHSIZE-10,paint);
			g.drawText(action,XHSIZE-300,YHSIZE-10,paint);
		}

		public void newTurn()
		{
			action="";
			setInformation("");
			postInvalidate();
		}
		public void setAction(String action)
		{
			this.action=action;
			postInvalidate();
		}
	}

	public void choice(final String message, final String option1, final String option2) 
	{
		handler.post(new Runnable(){
			public void run()
			{
    	AlertDialog.Builder adb=new AlertDialog.Builder(britannia);
    	adb.setTitle(message);
    	adb.setPositiveButton(option1, new Dialog.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				buttonPressed=option1; 
				phaseLockResume();	
			}});
    	adb.setNegativeButton(option2, new Dialog.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				buttonPressed=option2; 
				phaseLockResume();	
			}});
    	adb.show();
			}
		});	
	}
	
	public void positionAt(int x, int y) 
	{
		//if x,y is not within the middle 80% of the canvas, recenter
		if (x*SCALE<getScrollX()+0.5*getWidth()-0.4*getWidth())
		{
			float dx=x*SCALE-getScrollX()-(float)(0.5*getWidth());
			final float ddx=dx;
			handler.post(new Runnable(){
				public void run()
				{
					for (int i=0; i<-ddx; i++)
					{
						if (getScrollX()<=0)
							break;
						scrollBy(-1,0);
					}
					
				}
			});
		}
		else if (x*SCALE>getScrollX()+0.5*getWidth()+0.4*getWidth())
		{
			float dx=x*SCALE-getScrollX()-(float)(0.5*getWidth());
			final float ddx=dx;
			handler.post(new Runnable(){
				public void run()
				{
					for (int i=0; i<ddx; i++)
					{
						if(getScrollX()>=XISIZE*SCALE-getWidth())
							break;
						scrollBy(1,0);
					}
				}
			});
		}
		if (y*SCALE<getScrollY()+0.5*getHeight()-0.4*getHeight())
		{
			float dy=y*SCALE-getScrollY()-(float)(0.5*getHeight());
			final float ddy=dy;
			handler.post(new Runnable(){
				public void run()
				{
					for (int i=0; i<-ddy; i++)
					{
						if (getScrollY()<=0)
							break;
						scrollBy(0,-1);
					}
				}
			});
		}
		else if (y*SCALE>getScrollY()+0.5*getHeight()+0.4*getHeight())
		{
			float dy=y*SCALE-getScrollY()-(float)(0.5*getHeight());
			final float ddy=dy;
			handler.post(new Runnable(){
				public void run()
				{
					for (int i=0; i<ddy; i++)
					{
						if (getScrollY()>=YISIZE*SCALE-getHeight())
							break;
						scrollBy(0,1);
					}
				}
			});
		}
	}   

}
