package com.codeitphiliks.dietsnake;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class KeyInput {
	private App app;
	
	public KeyInput(App app) {
		this.app = app;
		initKeyBindings();
	}
	
	private void initKeyBindings() {
		createKeyBindings("snake.move.left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), 	new SnakeAction(app, -1, 0));
		createKeyBindings("snake.move.right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),	new SnakeAction(app, 1, 0));
		createKeyBindings("snake.move.up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), 		new SnakeAction(app, 0, -1));
		createKeyBindings("snake.move.down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), 	new SnakeAction(app, 0, 1));
		createKeyBindings("game.state.pause", KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), 	new ScreenAction(app, "pause"));
		createKeyBindings("game.state.start", KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), 	new ScreenAction(app, "start"));
		createKeyBindings("game.state.resume", KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), 	new ScreenAction(app, "resume"));
		createKeyBindings("screen.grid.show", KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), 	new ScreenAction(app, "show"));
		createKeyBindings("screen.grid.hide", KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), 	new ScreenAction(app, "hide"));
	}
	
	private void createKeyBindings(String name, KeyStroke keyStroke, AbstractAction action) {
		Screen screen = app.getScreen();
		InputMap inputMap = screen.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = screen.getActionMap();
		
		inputMap.put(keyStroke, name);
		actionMap.put(name, action);
	}

	class ScreenAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final App app;
		private final String action;
		
		public ScreenAction(App app, String action) {
			this.app = app;
			this.action = action;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(action.equals("pause")) app.pause();
			else if(action.equals("start")) app.start();
			else if(action.equals("resume")) app.resume();
			else if(action.equals("show")) app.showGrid();
			else if(action.equals("hide")) app.hideGrid();
		}		
	}
	
	class SnakeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private final int x;
		private final int y;
		private final App app;
		
		public SnakeAction(App app, int x, int y) {
			this.x = x * Screen.PIXEL_DIM;
			this.y = y * Screen.PIXEL_DIM;
			this.app = app;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			app.changeSnakeDirection(x, y);
		}
		
	}
}
