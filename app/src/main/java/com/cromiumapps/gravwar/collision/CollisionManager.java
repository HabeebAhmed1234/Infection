package com.cromiumapps.gravwar.collision;

import java.util.ArrayList;

import com.cromiumapps.gravwar.gameobjects.GameManager;
import com.cromiumapps.gravwar.missiles.Missile;
import com.cromiumapps.gravwar.planets.Planet;
import com.cromiumapps.gravwar.planets.Planet.PlanetType;

import android.util.Log;

public class CollisionManager {
	
	public static final String TAG = "CollisionManager";
	private GameManager gameManager;
	
	public CollisionManager(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}
	
	public void update()
	{
		checkMissileToPlanetCollision();
		//checkMissileToMissileCollision();
	}
	
	private void checkMissileToPlanetCollision()
	{
		ArrayList<Missile> missiles = gameManager.missileSwarmManager.getMissiles();
		ArrayList<Planet> planets = gameManager.planetManager.getAllPlanets();
		for(int p = 0 ; p < planets.size() ; p++)
		{
			for(int m = 0 ; m < missiles.size() ; m++)
			{
				if(isColliding(missiles.get(m),planets.get(p)))
				{
					collided(missiles.get(m), planets.get(p));
				}
			}
		}
	}
	
	private boolean isColliding(Missile missile, Planet planet)
	{
		if(missile.getSourcePlanetId() == planet.getId()) return false;
		return missile.getSprite().collidesWith(planet.getSprite());
	}
	
	private void collided(Missile missile, Planet planet)
	{
		//Log.d(TAG,"collision missileSource = "+missile.getSourcePlanetType()
		//		+" to planet = "+planet.getPlanetType().toString());
		
		if((missile.getSourcePlanetType() == PlanetType.PLANET_TYPE_PLAYER && planet.isEnemy()) 
		|| (missile.getSourcePlanetType() == PlanetType.PLANET_TYPE_ENEMY && planet.isPlayerPlanet()))
		{
			gameManager.missileSwarmManager.collided(missile.getId());
			gameManager.planetManager.hit(planet.getId());
		}
		
		if((missile.getSourcePlanetType() == PlanetType.PLANET_TYPE_ENEMY && planet.isEnemy()) 
		|| (missile.getSourcePlanetType() == PlanetType.PLANET_TYPE_PLAYER  && planet.isPlayerPlanet()))
		{
			//Log.d(TAG+"1","collision missileSource = "+missile.getSourcePlanetType()
			//		+" to planet = "+planet.getPlanetType().toString());
			gameManager.missileSwarmManager.docked(missile.getId());
			gameManager.planetManager.dockedMissile(planet.getId());
		}
		
		if(planet.isNeutral())
		{
			gameManager.missileSwarmManager.docked(missile.getId());
			if(missile.getSourcePlanetType() == PlanetType.PLANET_TYPE_ENEMY)
			{
				gameManager.planetManager.convertedPlanet(planet.getId(),PlanetType.PLANET_TYPE_ENEMY);	
			}
			if(missile.getSourcePlanetType() == PlanetType.PLANET_TYPE_PLAYER)
			{
				gameManager.planetManager.convertedPlanet(planet.getId(),PlanetType.PLANET_TYPE_PLAYER);	
			}
		}
	}
}
