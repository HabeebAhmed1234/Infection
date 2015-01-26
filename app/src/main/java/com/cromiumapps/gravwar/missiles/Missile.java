package com.cromiumapps.gravwar.missiles;

import org.andengine.engine.Engine;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.cromiumapps.gravwar.Angle;
import com.cromiumapps.gravwar.exceptions.InvalidMissileException;
import com.cromiumapps.gravwar.gameobjects.GameResourceManager;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.gameobjects.GameSprite;
import com.cromiumapps.gravwar.Position;
import com.cromiumapps.gravwar.common.Utilities;
import com.cromiumapps.gravwar.planets.Planet;
import com.cromiumapps.gravwar.planets.Planet.PlanetType;
import com.cromiumapps.gravwar.common.Constants;

import android.util.Log;

public class Missile {
	public static final String TAG = "Missile";
	public static final float MISSILE_SCALE = 0.3f;
	public static final float EXPLOSION_SCALE = 0.2f;
	
	private GameScene mGameScene;
	private Engine mEngine;
	
	private Angle m_currentAngle = new Angle(0);
	private Position m_position = new Position(0,0);
	private Position m_originPosition = new Position(0,0);
	private Position m_destinationPosition = new Position(0,0);
	private GameSprite missileSprite;
	private GameSprite mExplosionSprite;
	private GameSprite mDockSprite;
	private final PlanetType fromPlanetType;
	private final float fromPlanetId;
	private float m_id = 0;
	private float v_x = 3;
	private float v_y = 3;
	
	Missile(float [] vxvy, float id, Planet fromPlanet, Position origin, Position destination, Engine engine, VertexBufferObjectManager vertexBufferObjectManager, GameScene gameScene) throws InvalidMissileException
	{ 
		m_id = id;
		mGameScene = gameScene;
		mEngine = engine;
		this.fromPlanetType = fromPlanet.getPlanetType();
		this.fromPlanetId = fromPlanet.getId();
		if(fromPlanetType == PlanetType.PLANET_TYPE_NEUTRAL) {
            throw new InvalidMissileException("origin planet is neutral");
        }
		this.v_x = vxvy[0];    
		this.v_y = vxvy[1];
		m_position = new Position(origin.getX(),origin.getY());
		m_originPosition = new Position(origin.getX(),origin.getY());
		m_destinationPosition = new Position(destination.getX(),destination.getY());

        mExplosionSprite = new GameSprite(0, 0, GameResourceManager.getExplosionTextureRegion(), vertexBufferObjectManager, true,mEngine);
        mExplosionSprite.setScale(EXPLOSION_SCALE);
        mDockSprite = new GameSprite(0, 0, GameResourceManager.getDockTextureRegion(fromPlanet.isEnemy()), vertexBufferObjectManager, true,mEngine);
        mDockSprite.setScale(MISSILE_SCALE);
        missileSprite = new GameSprite(m_position.getX(),m_position.getY(),GameResourceManager.getMissileTexture(fromPlanet.isPlayerPlanet()),vertexBufferObjectManager,true,mEngine);
		missileSprite.setUserData(m_id);
		m_currentAngle = Utilities.getVectorAngleFromComponents(v_x, v_y);
		missileSprite.setAngle(m_currentAngle);
		missileSprite.setScale(MISSILE_SCALE); 
		//Log.d("MissileSystem","added missile sprite of id = "+m_id);
	}
	
	public float getSourcePlanetId(){
		return this.fromPlanetId;
	}
	
	public PlanetType getSourcePlanetType()
	{
		return fromPlanetType;
	}
	 
	public float getId() 
	{ 
		return this.m_id;
	}
	
	public GameSprite getSprite()
	{
		return missileSprite;
	}
	
	public Position getPosition()
	{
		return m_position;
	} 
	
	public float getRadius()
	{
		return missileSprite.getHeight()/2;
	}
	
	public void update()
	{
		updateVelocity();
		updatePosition();
	}
	
	private void updateVelocity()
	{
		this.m_currentAngle = missileSprite.getAngle();
		float rotationDifference = Utilities.getVectorAngleFromPoints(m_position,m_destinationPosition).subtract(m_currentAngle.get());
		 
		if(rotationDifference > 0.05) 
		{
			if(Math.abs(rotationDifference)<Math.PI)
			{
				m_currentAngle.add(Constants.MISSILE_ROTATION_SPEED);
			}
			else if(Math.abs(rotationDifference) >Math.PI && Math.abs(rotationDifference)<(2*Math.PI)){
				m_currentAngle.subtract(Constants.MISSILE_ROTATION_SPEED);
			}
		}	
		
		if(rotationDifference < -0.05) 
		{
			if(Math.abs(rotationDifference)<Math.PI)
			{
				m_currentAngle.subtract(Constants.MISSILE_ROTATION_SPEED);
			}
			else if(Math.abs(rotationDifference) >Math.PI && Math.abs(rotationDifference)<(2*Math.PI)){
				m_currentAngle.add(Constants.MISSILE_ROTATION_SPEED);
			}
		}	
		
		missileSprite.setAngle(m_currentAngle);
		
		float [] vxvy = Utilities.getMissileVectorFromAngle(m_currentAngle);
		v_x = vxvy[0];
		v_y = vxvy[1];
		
		//Log.d(TAG,"id = "+this.m_id+" m_currentAngle = " + m_currentAngle.get());
	}
	
	private void updatePosition()  
	{
		m_position.changeX(v_x);
		m_position.changeY(v_y);
		missileSprite.setPosition(m_position.getX(), m_position.getY());
	}
	
	public void explode()
	{
		mExplosionSprite.animate(100,true);
		mExplosionSprite.setPosition(missileSprite.getX(), missileSprite.getY());
		mExplosionSprite.setRotation((float) (missileSprite.getRotation()+Math.PI));
		mGameScene.attachChild(mExplosionSprite);
		missileSprite.detachSelf();
	}
	
	public void dock()
	{
		mDockSprite.animate(100, true);
		mDockSprite.setPosition(missileSprite.getX(), missileSprite.getY());
		mDockSprite.setRotation(missileSprite.getRotation());
		mGameScene.attachChild(mDockSprite);
		missileSprite.detachSelf();
	}
}
