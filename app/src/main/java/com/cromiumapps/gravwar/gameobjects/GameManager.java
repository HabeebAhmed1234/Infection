package com.cromiumapps.gravwar.gameobjects;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.cromiumapps.gravwar.HUD;
import com.cromiumapps.gravwar.Move;
import com.cromiumapps.gravwar.ai.GameAi;
import com.cromiumapps.gravwar.common.Constants;
import com.cromiumapps.gravwar.common.Constants.GAME_OUTCOME;
import com.cromiumapps.gravwar.collision.CollisionManager;
import com.cromiumapps.gravwar.exceptions.InvalidMissileException;
import com.cromiumapps.gravwar.exceptions.InvalidMoveException;
import com.cromiumapps.gravwar.level.Level;
import com.cromiumapps.gravwar.level.LevelGenerator;
import com.cromiumapps.gravwar.missiles.MissileSwarmManager;
import com.cromiumapps.gravwar.planets.Planet;
import com.cromiumapps.gravwar.planets.PlanetManager;

import android.util.Log;

public class GameManager {
	public interface GameOutcomeListener
	{
		public void onGameComplete(GAME_OUTCOME gameOutCom, float timeElapsed);
	}
	
	public static String TAG = "GameManager";
	
	private VertexBufferObjectManager vertexBufferObjectManager;
	
	//andengine objects
	private GameCamera gameCamera;
	public GameCamera getGameCamera(){return gameCamera;}
	private GameAi gameAi;
	private CollisionManager collisionManager;
	private GameOutcomeListener gameOutcomeListener;
	private HUD hud;
	public HUD getHud(){return hud;}
	
	private float gameClock = 0;
	//managers
	public PlanetManager planetManager;
	public MissileSwarmManager missileSwarmManager;
	public GameScene gameScene;
	public Engine mEngine;
	
	public float numMissilesReadyToFire = 0;
	
	public GameManager(GameOutcomeListener gameOutcomeListener, Engine engine, VertexBufferObjectManager vertexBufferObjectManager, GameScene gameScene, GameCamera gameCamera)
	{
		this.gameOutcomeListener = gameOutcomeListener;
		this.mEngine = engine;
		this.vertexBufferObjectManager =  vertexBufferObjectManager;
		this.gameScene = gameScene;
		this.gameCamera = gameCamera;
		this.missileSwarmManager = new MissileSwarmManager(gameScene, mEngine, vertexBufferObjectManager);
		this.collisionManager = new CollisionManager(this);
		
		generateLevel();
		
		//buildWalls();
		
		createGameLoop();
		this.gameAi = new GameAi(this);
	}
	
	public GameScene getGameScene()
	{
		return this.gameScene;
	}
	
	private void createGameLoop()
	{
		gameScene.registerUpdateHandler(new IUpdateHandler() {                    
            public void reset() {        
            }             
            public void onUpdate(float pSecondsElapsed) {
                //HERE IS THE GAME LOOP
            	gameClock+=1;
            	updateGame(pSecondsElapsed);
            }
        });
	}
	
	public void updateGame(float SecondsElapsed)
	{
		if(gameClock % Constants.PLANET_REGENERATION_INTERVAL == 0) 	planetManager.generatePlanetHealths();
		if(gameClock % Constants.COLLISION_CHECK_INTERVAL == 0) 		collisionManager.update();
		if(gameClock % Constants.MISSILE_SWARM_UPDATE_INTERVAL == 0) 	missileSwarmManager.update();
		if(gameClock % Constants.PLANET_UPDATE_INTERVAL == 0) planetManager.update();
		if(gameClock % Constants.GAME_AI_UPDATE_INTERVAL == 0) gameAi.update(SecondsElapsed);
		if(gameClock % Constants.GAME_OUTCOME_CHECK_INTERVAL == 0) checkGameOutCome();
	}
	
	private void buildWalls()
	{
		final Rectangle ground = new Rectangle(0, gameCamera.getHeight() - 2, gameCamera.getWidth(), 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, gameCamera.getWidth(), 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, gameCamera.getHeight(), vertexBufferObjectManager);
		final Rectangle right = new Rectangle(gameCamera.getWidth() - 2, 0, 2, gameCamera.getHeight(), vertexBufferObjectManager);
		this.gameScene.attachChild(ground);
		this.gameScene.attachChild(roof);
		this.gameScene.attachChild(left);
		this.gameScene.attachChild(right);
	}
	
	public void generateLevel()
	{
		LevelGenerator levelGenerator = new LevelGenerator(gameCamera.getWidth(),gameCamera.getHeight(), mEngine, this.vertexBufferObjectManager, this.gameScene, this);
		Level level = levelGenerator.generateLevel();
		this.hud = new HUD(level.getPaths(),gameScene,vertexBufferObjectManager);
		planetManager = new PlanetManager(level.getPlanets(), mEngine, vertexBufferObjectManager, this.gameScene, this);
	}
	
	public void resetGame()
	{
		
	}
	
	public float getGameClock(){
		return this.gameClock;
	}
	
	public void checkGameOutCome()
	{
		if(planetManager.isAllEnemies())
		{
			gameOutcomeListener.onGameComplete(GAME_OUTCOME.LOSE, gameClock);
		}
		
		if(planetManager.isAllPlayers())
		{
			gameOutcomeListener.onGameComplete(GAME_OUTCOME.WIN, gameClock);
		} 
	}
	
	public void onTouchAnywhere(TouchEvent event)
	{
		//Log.d("GravWar", "GameManager: touch event occured");
		//planetManager.deSelectAllPlanets(); 
	}
	
	public void onTouchPlanet(TouchEvent event, float touchedPlanetID)
	{
		if(!event.isActionUp()) return;
		//Log.d("GravWar", "GameManager: touch event on a planet of id +" + touchedPlanetID);
		Planet prevSelectedPlanet = planetManager.getSelectedPlanet();
		Planet touchedPlanet = planetManager.getPlanetByID(touchedPlanetID);
		try {
			if(prevSelectedPlanet!=null)makeAMove(new Move(numMissilesReadyToFire,prevSelectedPlanet,touchedPlanet,false));
			numMissilesReadyToFire = 0;
		} catch (InvalidMoveException e) {
			e.printWhat();
		}
		planetManager.selectPlanetByID(touchedPlanetID);
		this.hud.updateMissilesSelectedText(numMissilesReadyToFire);
	}
	
	public void makeAMove(Move move)
	{
		if(move.missilesToFireAmmount>0)
		{
			
			if(this.hud.isMovePermissible(move,planetManager))
			{
				//Log.d("GravWar", "GameManager: firing missile swarm from planet id " + move.fromPlanetId+" to planet id"+move.toPlanetId);
				try {
					this.missileSwarmManager.addMissileSwarm( planetManager.getPlanetByID(move.fromPlanetId)
															, planetManager.getPlanetByID(move.toPlanetId)
															, move.missilesToFireAmmount);
					if(move.isAiMove) planetManager.getPlanetByID(move.fromPlanetId).damageHealth(move.missilesToFireAmmount);
				} catch (InvalidMissileException e) {
					e.printWhat();
				}
			}
		}
	}
}
