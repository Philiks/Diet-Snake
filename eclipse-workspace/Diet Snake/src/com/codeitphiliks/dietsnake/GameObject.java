package com.codeitphiliks.dietsnake;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

public abstract class GameObject {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	public GameObject(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public abstract void update();
	public abstract void render(Graphics g);

	public boolean collides(GameObject object) {
		Rectangle tr = new Rectangle(this.x, this.y, this.width, this.height);
		Rectangle or = new Rectangle(object.x, object.y, object.width, object.height);
		return tr.intersects(or);
	}
	
	public int getX()		{	return x;		}
	public int getY()		{	return y;		}
	public int getWidth()	{	return width;	}
	public int getHeight()	{	return height;	}
}

class Block extends GameObject {
	private final int ANIMATION_MAX = 3000 / GameLoop.TICK; // 3 seconds
	private int animationCtr;
	private Color blockColor;
	private boolean toDark;
	
	public Block(int x, int y, int width, int height) {
		super(x, y, width, height);
		initBlock();
	}
	
	public Block() {
		super(0, 0, Screen.PIXEL_DIM, Screen.PIXEL_DIM);
		findLocation();
		initBlock();
	}
	
	@Override
	public void update() {
		if(toDark) {
			if(--animationCtr < 0) toDark = false;
		} else	
			if(++animationCtr > ANIMATION_MAX) toDark = true;
				
		// Light Gray to Dark Gray
		// Proportion of Color (64 : 192) and animation (0 : ANIMATION_MAX)
		int minCol = 64;   // Light Gray
		int maxCol = 192;  // Dark  Gray
		int colVal = (animationCtr * (maxCol - minCol) / ANIMATION_MAX) + minCol;
		blockColor = new Color(colVal, colVal, colVal);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(blockColor);
		g.fillRect(x, y, width, height);
	}
	
	private void initBlock() {
		toDark = true;
		animationCtr = ANIMATION_MAX;
	}
	
	private void findLocation() {
		int sw = Screen.WIDTH;
		int sh = Screen.HEIGHT;
		int dim = Screen.PIXEL_DIM;
		x = new Random().nextInt((sw - dim*2) / dim) * dim + dim;
		y = new Random().nextInt((sh - dim*2) / dim) * dim + dim;
		
		// randLength ranges from 1 - 5.
		int randLength = (new Random().nextInt(5) + 1) * Screen.PIXEL_DIM;
		// 0 = Horizontal | 1 = Vertical
		int orientation = new Random().nextInt(2) % 2;
		if(orientation == 0) {
			width = randLength;
			height = Screen.PIXEL_DIM;
		} else {
			height = randLength;
			width = Screen.PIXEL_DIM;
		}
	}
}

class Food extends GameObject {
	private final int SPOILAGE_MUL = 2; // The 2 makes the spoilage longer
	private final int SPOILAGE_MAX = 255 * SPOILAGE_MUL; 
	private int spoilageCtr;
	private boolean toSpoil;
	
	public Food(int width, int height) {
		super(0, 0, width, height);
		
		findNewLocation();
		spoilageCtr = SPOILAGE_MAX;
		toSpoil = true;
	}

	@Override
	public void update() {
		if(toSpoil) {
			if(--spoilageCtr == 0) toSpoil = false;
		} else
			if(++spoilageCtr == SPOILAGE_MAX) toSpoil = true;
			
	}

	@Override
	public void render(Graphics g) {
		g.setColor(new Color(255 - spoilageCtr / SPOILAGE_MUL, spoilageCtr / SPOILAGE_MUL, 0));
		g.fillRect(x, y, width, height);
	}	
	
	public void findNewLocation() {
		int sw = Screen.WIDTH;
		int sh = Screen.HEIGHT;
		int dim = Screen.PIXEL_DIM;

		x = new Random().nextInt((sw - dim*2) / dim) * dim + dim;
		y = new Random().nextInt((sh - dim*2) / dim) * dim + dim;
	}
	
	public boolean isSpoiled() {
		return spoilageCtr < SPOILAGE_MAX / 2;
	}
	
	public boolean isFood(Point p) {
		return p.equals(new Point(x, y));
	}
}

class Snake extends GameObject {
	private int xDir, yDir;
	private int moveCtr;
	private List<Point> tails;
	private App app;
	
	public Snake(App app, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.app = app;
		
		tails = new ArrayList<>();
		
		// Adds two tails instantly on the left side of the Head.
		tails.add(new Point(x - width, y));
		tails.add(new Point(x - width*2, y));
		
		// Snake starts moving on the right
		xDir = Screen.PIXEL_DIM;
		
		moveCtr = Screen.PIXEL_DIM;
	}

	@Override
	public void update() {
		if(--moveCtr > 0)	return;
		
		// Move the tails coordinate from last to index 1.
		for(int i = tails.size() - 1; i > 0; i--) {
			tails.get(i).x = tails.get(i - 1).x;
			tails.get(i).y = tails.get(i - 1).y;
		}

		// Index 0 gets coordinate of head.
		tails.get(0).x = x;
		tails.get(0).y = y;
		
		// Update the coordinate of head.
		x += xDir;
		y += yDir;
		
		checkTailCollision();
		
		moveCtr = width;
	}

	@Override
	public void render(Graphics g) {
		Color col = Color.BLUE;
		// Head
		g.setColor(col);
		g.fillRect(x, y, width, height);
		
		// Body
		for(Point tail : tails) {
			col = darkerCol(col);
			g.setColor(col);
			g.fillRect(tail.x, tail.y, width, height);
		}
	}
	
	public void changeDir(int xDir, int yDir) {
		int dim = Screen.PIXEL_DIM;
		if(this.xDir == xDir && this.yDir == yDir)  return; // same button pressed
		else if(xDir == dim && this.xDir == -dim) 	return; // change to right but current is Left
		else if(xDir == -dim && this.xDir == dim) 	return; // change to left but current is right
		else if(yDir == dim && this.yDir == -dim) 	return; // change to down but current is up
		else if(yDir == -dim && this.yDir == dim) 	return; // change to up but current is down
		
		this.xDir = xDir;
		this.yDir = yDir;
		moveCtr = 0; // immediately move
	}
	
	public void checkTailCollision() {
		for(Point tail : tails)
			if(tail.x == x && tail.y == y) {
				app.pause();
				JOptionPane.showMessageDialog(
						app.getFrame(), 
						"Don't eat yourself, that's bad for your tummy.",
						"Are you the hungry?", 
						JOptionPane.ERROR_MESSAGE);
				app.gameOver();
			}
	}
	
	public void grow() {
		tails.add(new Point(-Screen.PIXEL_DIM, -Screen.PIXEL_DIM));
	}
	
	public void shrink() {
		int lastIndex = tails.size() - 1;
		tails.remove(lastIndex);
		
		if(tails.isEmpty()) {
			JOptionPane.showMessageDialog(
							app.getFrame(), 
							"You're all tail with no head haha!",
							"Dedzzz", 
							JOptionPane.ERROR_MESSAGE);
			app.gameOver();
		}
	}
	
	public boolean isTail(Point p) {
		for(Point tail : tails)
			if(p.equals(tail))
				return true;
			
		return false;
	}
	
	// Color.brighter() factor is 0.7
	// this method has a factor of 0.9 for better transition
	private Color darkerCol(Color col) {
		double factor = 0.9;
		return new Color(Math.max((int)(col.getRed()   * factor), 0),
						 Math.max((int)(col.getGreen() * factor), 0),
						 Math.max((int)(col.getBlue()  * factor), 0));
	}
	
	public int getSize() {
		return tails.size() + 1; // 1 for head
	}
}