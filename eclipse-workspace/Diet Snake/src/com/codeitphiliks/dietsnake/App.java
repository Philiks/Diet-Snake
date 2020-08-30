package com.codeitphiliks.dietsnake;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {
	private JFrame frame;
	private Screen screen;
	private GameLoop gameLoop;
	private Snake snake;
	private Food food;
	private HUD hud;
	private List<GameObject> gameObjects;
	private int targetSize;
	private int score;
	private int timeLeft;
	
	public static final int GAME_TIMER = 60_000 / GameLoop.TICK; // 60 seconds
	
	private App() {
		hud = new HUD(this);
		createLevel();
		initAndRenderGUI();
		new KeyInput(this);
		gameLoop = new GameLoop(this);
	}
	
	public void update() {
		checkSnakeCollision();
		checkSnakeAndTargetSize();
		checkTimeLeft();
		for(GameObject object : gameObjects)
			object.update();
		hud.update();
	}
	
	public void render() {
		screen.repaint();
	}
	
	private void checkSnakeCollision() {
		for(GameObject object : gameObjects) {
			if(!(object instanceof Snake) &&
					snake.collides(object)) {
				if(object instanceof Food) {
					if(food.isSpoiled()) snake.shrink();
					else snake.grow();
					food.findNewLocation();
				} 
				
				if(object instanceof Block)
					gameOver();
				
				// Breaks the loop.
				break;
			}
		}
	}
		
	private void checkSnakeAndTargetSize() {
		if(getSnakeSize() != getTargetSize()) return;
		
		score++;
		generateTargetSize();
		refreshTimeLeft();
	}
	
	private void checkTimeLeft() {
		if(--timeLeft == 0) gameOver();
	}

	private void createLevel() {
		gameObjects = new ArrayList<>();
		
		// Place walls first since they're static.
		gameObjects.add(new Block(0, 0, Screen.WIDTH, Screen.PIXEL_DIM));
		gameObjects.add(new Block(0, Screen.HEIGHT - Screen.PIXEL_DIM, Screen.WIDTH, Screen.PIXEL_DIM));
		gameObjects.add(new Block(0, Screen.PIXEL_DIM, Screen.PIXEL_DIM, Screen.HEIGHT - 2*Screen.PIXEL_DIM));
		gameObjects.add(new Block(Screen.WIDTH - Screen.PIXEL_DIM, Screen.PIXEL_DIM, Screen.PIXEL_DIM, Screen.HEIGHT - 2*Screen.PIXEL_DIM));
		
		int obstacleNum = 10;
		for(int i = 0; i < obstacleNum; i++) {
			Block block = new Block();
			gameObjects.add(block);
		}
		
		// Dynamic GameObjects.
		int centerX = Screen.WIDTH / 2;
		int centerY = Screen.HEIGHT / 2;
		snake = new Snake(this, centerX, centerY, Screen.PIXEL_DIM, Screen.PIXEL_DIM);
		food = new Food(Screen.PIXEL_DIM, Screen.PIXEL_DIM);
		
		gameObjects.add(snake);
		gameObjects.add(food);
		
		generateTargetSize();
		refreshTimeLeft();
	}
	
	private void generateTargetSize() {
		int oldTarget = getTargetSize();
		
		// Generates new target size between 3 - 10.
		targetSize = (int)(Math.random() * 7) + 3;
		
		if(oldTarget == targetSize)
			generateTargetSize();
	}
	
	private void refreshTimeLeft() {
		timeLeft = GAME_TIMER;
	}
	
	public void start()  {	gameLoop.start();	screen.resume();  }
	public void pause()  {	gameLoop.pause();	screen.pause();  }
	public void resume() {	gameLoop.resume();	screen.resume(); }
	
	public boolean stop() {
		gameLoop.pause();
		
		int ans = JOptionPane.showConfirmDialog(
						frame, 
						"Are you quitting son?", 
						"It's okay to leave :)", 
						JOptionPane.YES_NO_OPTION);
		
		if(ans == JOptionPane.NO_OPTION)
			return false;
		
		return true;
	}
	
	private void restart() {
		createLevel();
		Screen.initScreen(frame, (screen = new Screen(gameObjects, hud)));
		new KeyInput(this);
		gameLoop = new GameLoop(this);
		score = 0;
	}
	
	public void gameOver() {
		gameLoop.pause();
		
		int ans = JOptionPane.showConfirmDialog(
				frame, 
				"Score : " + score + "\nWant a comeback?", 
				"No surrender!", 
				JOptionPane.YES_NO_OPTION);

		if(ans == JOptionPane.YES_OPTION) {
			restart();
		} else if(stop()) {
			frame.setVisible(false);
			frame.dispose();
		}
	}
	
	public void showGrid() 	   {	screen.showGridLines();	}
	public void hideGrid() 	   {	screen.hideGridLines();	}
	public Screen getScreen()  {	return screen; 			}
	public JFrame getFrame()   {	return frame;			}
	public int getSnakeSize()  {	return snake.getSize(); }
	public int getTargetSize() {	return targetSize;		}
	public int getScore() 	   {	return score;			}
	public int getTimeLeft()   {	return timeLeft;		}
	
	public void changeSnakeDirection(int x, int y) {
		snake.changeDir(x, y);
	}
	
	private void initAndRenderGUI() {
		Screen.initScreen((frame = new JFrame()), 
						  (screen = new Screen(gameObjects, hud)));
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(!stop())	return;
				
				frame.setVisible(false);
				frame.dispose();
			}
		});
	}
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(() -> new App());
	}
}
