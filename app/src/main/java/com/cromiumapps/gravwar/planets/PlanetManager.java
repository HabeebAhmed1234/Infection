package com.cromiumapps.gravwar.planets;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.cromiumapps.gravwar.gameobjects.GameManager;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.planets.Planet.PlanetType;
import com.cromiumapps.gravwar.common.Constants;

import android.util.Log;

public class PlanetManager {
	private static final String TAG = "PlanetManager";
	private ArrayList <Planet> m_planets = new ArrayList<Planet>();
	private VertexBufferObjectManager vertexBufferObjectManager;
	private GameScene gameScene;
	private GameManager gameManager;
	private Engine mEngine;
	
	public PlanetManager(ArrayList<Planet> planets, Engine engine, VertexBufferObjectManager vertexBufferObjectManager, GameScene gameScene, GameManager gameManager)
	{
		this.vertexBufferObjectManager = vertexBufferObjectManager;
		this.gameScene = gameScene;
		this.gameManager = gameManager;
		this.mEngine = engine;
		
		Log.d("GravWar", "PlanetManager - Constructor: number of planets to add is" + planets.size());
		
		for(int i = 0 ; i < planets.size() ; i++)
		{
			Planet tempPlanet = planets.get(i);
			Log.d("PositionSet","adding planet +++++++++ "+tempPlanet.getId()+" at position "+tempPlanet.getPosition().getX()+","+tempPlanet.getPosition().getY());
			addPlanet(tempPlanet.getId(),tempPlanet.getPosition().getX(), tempPlanet.getPosition().getY(), tempPlanet.getDiameter(), tempPlanet.getPlanetType());
			planets.get(i).setIsAddedToScreen(true);
		}
	}
	
	public void addPlanet (int id, float x, float y, float diameter, PlanetType planetType)
	{
		Log.d("GravWar", "PlanetManager: adding new planet");
		Planet newPlanet = new Planet (id,x,y,diameter,planetType, mEngine, vertexBufferObjectManager, this.gameManager, this.gameScene);
		m_planets.add(newPlanet);
		newPlanet.addToScene();
		gameScene.registerTouchArea(newPlanet.getSprite());
	}
	
	public void removePlanet(int id)
	{
		for(int i = 0; i < m_planets.size() ; i++)
		{
			if(m_planets.get(i).getId() == id)
			{
				m_planets.remove(i);
				return;
			}
		}
	}
	
	public Planet getPlanetByID(int id)
	{
		for(int i = 0 ;i<this.m_planets.size();i++)
		{
			if(m_planets.get(i).getId()==id)
			{
				return m_planets.get(i);
			}
		}
		return null;
	}
	
	public void selectPlanetByID(int id)
	{
		for(int i = 0 ;i<this.m_planets.size();i++)
		{
			if(m_planets.get(i).getId()==id)
			{
				m_planets.get(i).select();
			}else
			{
				m_planets.get(i).deSelect();
			}
		}
	}
	
	public void deSelectPlanetByID(int id)
	{
		for(int i = 0 ;i<this.m_planets.size();i++)
		{
			if(m_planets.get(i).getId()==id)
			{
				m_planets.get(i).deSelect();
			}
		}
	}
	
	public void deSelectAllPlanets()
	{
		for(int i = 0 ;i<this.m_planets.size();i++)
		{
			m_planets.get(i).deSelect();
		}
	}
	
	public Planet getSelectedPlanet()
	{
		for(int i = 0 ;i<this.m_planets.size();i++)
		{
			if(m_planets.get(i).isSelected())
			{
				Log.d("Planets","Planed id "+m_planets.get(i).getId()+" is selected");
				return m_planets.get(i);
			}
		}
		return null;
	}
	
	public void generatePlanetHealths()
	{
		for(int i = 0 ;i<this.m_planets.size();i++)
		{
			m_planets.get(i).increaseHealth(Constants.PLANET_MISSILE_REGENERATION_AMMOUNT);
		}
	}
	
	public void update()
	{
		for(int i = 0 ; i < this.m_planets.size() ; i++)
		{
			m_planets.get(i).update();
		}
	}
	
	public ArrayList<Planet> getAllPlanets()
	{
		return this.m_planets;
	}

	public boolean isPlayerLosing(){
		ArrayList<Planet> allPlanets = getAllPlanets();
		int injuredEnemyPlanets = 0;
		int numEnemyPlanets = 0;
		int injuredPlayerPlanets = 0;
		int numPlayerPlanets = 0;

		for (Planet planet : allPlanets) {
			if (planet.isPlayerPlanet()) {
				numPlayerPlanets ++;
				if (planet.isAboutToDie()) injuredPlayerPlanets ++;
			}
			if (planet.isEnemy()) {
				numEnemyPlanets ++;
				if (planet.isAboutToDie()) injuredEnemyPlanets++;
			}
		}
		if (numPlayerPlanets < numEnemyPlanets && injuredPlayerPlanets > injuredEnemyPlanets) return true;
		return false;
	}

	public boolean isAllEnemies()
	{
		boolean allAreEnemies = true;
		for(int i = 0 ; i < this.m_planets.size() ; i++)
		{
			if(m_planets.get(i).isPlayerPlanet() || m_planets.get(i).isNeutral()) allAreEnemies = false;
		}
		
		return allAreEnemies;
	}

	public boolean isAllPlayers()
	{
		boolean allArePlayers = true;
		for(int i = 0 ; i < this.m_planets.size() ; i++)
		{
			if(m_planets.get(i).isEnemy() || m_planets.get(i).isNeutral()) allArePlayers = false;
		}
		
		return allArePlayers;
	}
	
	public void hit(int id)
	{
		if(!getPlanetByID(id).damageHealth(1)){
			Log.e(TAG, "Failed to damage health of planet id = "+id);
		}
	}
	
	public void dockedMissile(int id)
	{
		getPlanetByID(id).increaseHealth(1);
	}
	
	public void convertedPlanet(int id, PlanetType typeToConvertTo)
	{
		getPlanetByID(id).convertTo(typeToConvertTo);
	}
}
