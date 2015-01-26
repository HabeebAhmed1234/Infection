package com.cromiumapps.gravwar.activity;

import org.andengine.engine.Engine;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.cromiumapps.gravwar.gameobjects.GameCamera;
import com.cromiumapps.gravwar.gameobjects.GameManager;
import com.cromiumapps.gravwar.gameobjects.GameResourceManager;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.common.Utilities;
import com.cromiumapps.gravwar.common.Constants.GAME_OUTCOME;
import com.cromiumapps.gravwar.gameobjects.GameManager.GameOutcomeListener;

public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, GameOutcomeListener {
	private GameManager gameManager;
	private GameCamera gameCamera;
	
	@Override
	public void onCreate(Bundle pSavedInstanceState)
	{
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(pSavedInstanceState);
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		gameCamera = new GameCamera(this);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(GameCamera.desiredWidth/GameCamera.desiredHeight), gameCamera);
	}
	
	@Override
	public Engine onCreateEngine(final EngineOptions pEngineOptions) {
		return new org.andengine.engine.LimitedFPSEngine(pEngineOptions, gameCamera.FPS);
	}

	@Override
	public void onCreateResources() {
	}
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
				
		//all the things required for the gameManager to run
		GameOutcomeListener gameOutcomeListener = this;
		VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager(); 
		GameScene gameScene = new GameScene(this);
		GameCamera camera = gameCamera; 
		GameResourceManager.loadAllResources(this);
		
		gameManager = new GameManager(gameOutcomeListener,
									  mEngine,
									  vertexBufferObjectManager, 
									  gameScene, 
									  camera);
		
		return gameManager.getGameScene();
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Log.d("GravWar", "MainActivty: touch event occured");
		gameManager.onTouchAnywhere(pSceneTouchEvent);
		return true;
	}

	@Override
	public void onGameComplete(GAME_OUTCOME gameOutCome, float timeElapsed) {
		  Utilities.startGameFinishedActivity(this, gameOutCome, timeElapsed);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	Utilities.startMainMenuActivity(this);
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
}