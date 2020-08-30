package com.codeitphiliks.dietsnake;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class GameLoop {
	public static final int TICK = 1000 / 120; // 8 ms
	private Timer timer;
	private boolean isPaused = false;
	private int frames;
	private long time;
	
	public GameLoop(App app) {
		timer = new Timer(TICK, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.update();
				app.render();
				frames++;
				if(System.currentTimeMillis() > time + 1000) {
					time = System.currentTimeMillis();
					app.getFrame().setTitle("Diet Snake | FPS : " + frames);
					frames = 0;
				}
			}
			
		});
	}
	
	public void start() {
		time = System.currentTimeMillis();
		timer.start();		
	}
	
	public void pause() {
		timer.stop();
		isPaused = true;
	}
	
	public void resume() {
		if(!isPaused) return;
		
		isPaused = false;
		timer.start();
	}
}
