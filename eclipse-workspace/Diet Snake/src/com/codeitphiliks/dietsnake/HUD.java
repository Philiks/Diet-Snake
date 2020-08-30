package com.codeitphiliks.dietsnake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HUD {
	private App app;
	private Color timeColor;
	
	public HUD(App app) {
		this.app = app;
	}
	
	public void update() {
		// Green to Red.
		// Proportion of Color (0 : 255) and Timer (0 : GAME_TIMER 
		int left = app.getTimeLeft() * 255 / App.GAME_TIMER;
		timeColor = new Color(255 - left, left, 0, 100);
	}
	
	public void render(Graphics g) {
		int xOffset = 100;
		int yOffset = Screen.PIXEL_DIM * 2;
		int padding = 250;
		
		g.setFont(new Font("Arial", Font.BOLD, 15));
		g.setColor(new Color(255, 255, 255, 150));
		
		g.drawString("Current Snake Size : " + app.getSnakeSize() , xOffset + padding * 0, yOffset);
		g.drawString("Target Snake Size : "  + app.getTargetSize(), xOffset + padding * 1, yOffset);
		g.drawString("Score (so far) : "	 + app.getScore()     , xOffset + padding * 2, yOffset);
		
		g.setColor(timeColor);
		int width  = app.getTimeLeft() * 500 / App.GAME_TIMER;;
		g.fillRect((Screen.WIDTH - width) / 2, Screen.PIXEL_DIM * 2, width, Screen.PIXEL_DIM);
	}
}
