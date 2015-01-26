package com.cromiumapps.gravwar.missiles;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.cromiumapps.gravwar.Angle;
import com.cromiumapps.gravwar.exceptions.InvalidMissileException;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.Position;
import com.cromiumapps.gravwar.common.Utilities;
import com.cromiumapps.gravwar.common.Constants;
import com.cromiumapps.gravwar.planets.Planet;

public class MissileSwarm {
	public static final String TAG = "MissileSwarm";
	//the degree by which launched missiles will spread
	private ArrayList <Missile> m_missiles;
	private float m_id;
	
	MissileSwarm (float id,Planet fromPlanet, float numMissilesReadyToFire, float originPlanetRadius, Position origin, Position destination, GameScene gameScene, Engine engine, VertexBufferObjectManager vertexBufferObjectManager) throws InvalidMissileException
	{
		m_id = id;
		m_missiles = new ArrayList<Missile>();
		Angle alpha = Utilities.getVectorAngleFromPoints(origin, destination);
		Log.d(TAG,"missile swarm angle = "+alpha.get() + " origin = ("+origin.getX()+","+origin.getY()+") destination = ("+destination.getX()+","+destination.getY()+")");
		Angle theta = new Angle(alpha.add(numMissilesReadyToFire/2* Constants.MISSILE_SPREAD_INDEX));
		
		for(int i = 0 ; i < numMissilesReadyToFire ; i ++)
		{
			Missile newMissile = new Missile(Utilities.getMissileVectorFromAngle(theta),MissileIdRegistry.getUniqueMissileId(),fromPlanet,getValidOrigin(origin,destination,originPlanetRadius,theta),destination,engine,vertexBufferObjectManager, gameScene);
			m_missiles.add(newMissile);
			gameScene.attachChild(newMissile.getSprite());
			Log.d(TAG,"New Missile added id = "+newMissile.getId());
			theta.subtract(Constants.MISSILE_SPREAD_INDEX);
		}
	}
	
	private Position getValidOrigin(Position origin, Position destination, float originPlanetRadius, Angle theta)
	{		
		return new Position((float) (origin.getX()+(originPlanetRadius+Constants.MISSILE_PLANET_TO_MISSILE_GAP)*Math.cos(theta.get())),
							(float) (origin.getY()+(originPlanetRadius+Constants.MISSILE_PLANET_TO_MISSILE_GAP)*Math.sin(theta.get())));
	}
	
	public float getId()
	{
		return this.m_id;
	}
	
	public Missile getMissileById(float id)
	{
		for(int i =0 ;i<m_missiles.size();i++)
		{
			if(m_missiles.get(i).getId() == id)
			{
				return m_missiles.get(i);
			}
		}
		return null;
	}
	
	private void removeMissileById(float id, boolean isExploding)
	{
		for(int i =0 ;i<m_missiles.size();i++)
		{
			if(m_missiles.get(i).getId() == id)
			{
				if(isExploding)
				{
					m_missiles.get(i).explode();
				}else{
					m_missiles.get(i).dock();
				}
				m_missiles.remove(i);
			}
		}
	}
	
	public void explodeMissileById(float id)
	{
		removeMissileById(id,true);
	}
	
	public void dockMissileById(float id)
	{
		removeMissileById(id,false);
	}
	
	public ArrayList<Missile> getAllMissiles()
	{
		return this.m_missiles;
	}
	
	public void update()
	{
		for(int i =0 ; i < m_missiles.size() ; i++)
		{
			m_missiles.get(i).update();
		}
	}
	
}
