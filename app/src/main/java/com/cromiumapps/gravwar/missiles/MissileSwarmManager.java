package com.cromiumapps.gravwar.missiles;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.cromiumapps.gravwar.exceptions.InvalidMissileException;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.planets.Planet;

public class MissileSwarmManager {
	private ArrayList <MissileSwarm> m_missileSwarms = new ArrayList <MissileSwarm>();;
	private VertexBufferObjectManager vertexBufferObjectManager;
	private GameScene gameScene;
	private Engine mEngine;
	
	public MissileSwarmManager(GameScene gameScene, Engine engine, VertexBufferObjectManager vertexBufferObjectManager)
	{
		this.vertexBufferObjectManager = vertexBufferObjectManager;
		mEngine = engine;
		this.gameScene = gameScene;
	}
	
	public void addMissileSwarm(Planet fromPlanet, Planet toPlanet, float numPlanetsReadyToFire) throws InvalidMissileException
	{
		MissileSwarm newMissileSwarm = new MissileSwarm(getUniqueMissileSwarmId(),fromPlanet,numPlanetsReadyToFire, fromPlanet.getDiameter()/2, fromPlanet.getPosition(), toPlanet.getPosition(), gameScene, mEngine, vertexBufferObjectManager);
		m_missileSwarms.add(newMissileSwarm);
		Log.d("MissileSystem","New Missile Swarm added id = "+newMissileSwarm.getId());
	}
	
	public void removeMissileSwarm(int id)
	{
		for(int i = 0; i < m_missileSwarms.size() ; i++)
		{
			if(m_missileSwarms.get(i).getId() == id)
			{
				m_missileSwarms.remove(i);
				return;
			}
		}
	}
	
	public void explodeMissileById(float id)
	{
		for(int i = 0; i < m_missileSwarms.size() ; i++)
		{
			m_missileSwarms.get(i).explodeMissileById(id);
		}
	}
	
	public void dockMissileById(float id)
	{
		for(int i = 0; i < m_missileSwarms.size() ; i++)
		{
			m_missileSwarms.get(i).dockMissileById(id);
		}
	}
	
	private float getUniqueMissileSwarmId()
	{
		float max = 0;
		for(int i = 0 ; i < m_missileSwarms.size();i++)
		{
			if(m_missileSwarms.get(i).getId()>max)
			{
				max = m_missileSwarms.get(i).getId();
			}
		}
		max +=1;
		return max;
	}
	
	public void update()
	{
		for(int i =0 ; i<m_missileSwarms.size();i++)
		{
			m_missileSwarms.get(i).update();
		}
	}
	
	public ArrayList<Missile> getMissiles()
	{
		ArrayList<Missile> missiles = new ArrayList<Missile>();
		if(m_missileSwarms.size() == 0) return missiles;
		for(int i = 0 ; i < m_missileSwarms.size() ; i++)
		{
			missiles.addAll(m_missileSwarms.get(i).getAllMissiles());
		}
		return missiles;
	}
	
	public void collided(float id)
	{
		explodeMissileById(id);
	}
	
	public void docked(float id)
	{
		dockMissileById(id);
	}
}
