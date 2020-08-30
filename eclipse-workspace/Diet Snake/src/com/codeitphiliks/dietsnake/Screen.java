package com.codeitphiliks.dietsnake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Screen extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	public static final int PIXEL_DIM = 20;
	private boolean showGrid;
	private List<GameObject> gameObjects;
	private HUD hud;
	private boolean paused;
	
	public Screen(List<GameObject> gameObjects, HUD hud) {
		showGrid = false;
		paused = false;
		this.gameObjects = gameObjects;
		this.hud = hud;
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(GameObject object : gameObjects)
			object.render(g);
		
		hud.render(g);
		
		if(showGrid) {
			g.setColor(Color.MAGENTA);
			for(int i = PIXEL_DIM; i < WIDTH - PIXEL_DIM; i+=PIXEL_DIM)
				for(int j = PIXEL_DIM; j < HEIGHT - PIXEL_DIM; j+=PIXEL_DIM)
					g.drawRect(i, j, PIXEL_DIM, PIXEL_DIM);
		}
		
		if(paused) {
			int fontSize = 15;
			g.setFont(new Font("Arial", Font.BOLD, fontSize));
			g.setColor(Color.ORANGE);
			g.drawString("PAUSE", (Screen.WIDTH - 50) / 2, Screen.HEIGHT / 2);
		}
	}

	public static void initScreen(JFrame frame, Screen screen) {
		frame.setResizable(false);
		frame.setContentPane(screen);
		frame.validate();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		screen.requestFocusInWindow();
	}
	
	public void showGridLines() {
		showGrid = true;
		repaint();
	}
	
	public void hideGridLines() {
		showGrid = false;
		repaint();
	}
	
	public void pause() {	
		paused = true;
		repaint();
	}
	
	public void resume() {
		paused = false;
		repaint();
	}
}
