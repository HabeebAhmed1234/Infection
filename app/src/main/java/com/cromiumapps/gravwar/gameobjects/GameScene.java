package com.cromiumapps.gravwar.gameobjects;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.adt.color.Color;

public class GameScene extends Scene {
	public static Color BACKGROUND_COLOR = Color.BLACK;
	
	public GameScene(IOnSceneTouchListener touchListener)
	{
		super();
		this.setBackground(new Background(BACKGROUND_COLOR));
		this.setOnSceneTouchListener(touchListener);
	}
}
