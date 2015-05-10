package com.cromiumapps.gravwar.level;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.cromiumapps.gravwar.gameobjects.GameManager;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.Path;
import com.cromiumapps.gravwar.Position;
import com.cromiumapps.gravwar.common.Utilities;
import com.cromiumapps.gravwar.planets.Planet;
import com.cromiumapps.gravwar.planets.Planet.PlanetType;
import com.cromiumapps.gravwar.common.Constants;

import android.util.Log;

public class LevelGenerator {
	public static final String LOG_TAG = "levelGenerator";
	public static final int TOTAL_STARTING_ENEMY_MISSILES = 30;
	public static final int TOTAL_STARTING_PLAYER_MISSILES = 30;
	public static final float TOTAL_NUMBER_OF_PLANETS_PER_TEAM = 5;
	
	private VertexBufferObjectManager vertexBufferObjectManager;
	private GameScene gameScene;
	private Engine mEngine;
	private GameManager gameManager;
	
	private float m_screenHeight;
	private float m_screenWidth;
	ArrayList<Planet> m_levelPlanets;
	ArrayList<Path> m_levelPaths;
	private float m_midwayLine;
	
	
	
	public LevelGenerator(float width, float height, Engine engine, VertexBufferObjectManager vertexBufferObjectManager, GameScene gameScene, GameManager gameManager)
	{
		this.vertexBufferObjectManager = vertexBufferObjectManager;
		this.gameScene = gameScene;
		this.mEngine = engine;
		this.gameManager = gameManager;
		
		m_screenHeight = height;
		m_screenWidth = width;
		//midway line divides the enemy and player
		m_midwayLine = height/2;
		m_levelPlanets = new ArrayList <Planet>();
		m_levelPaths = new ArrayList <Path>();
		Log.d(LOG_TAG,"Level Generator initialized with screen size "+width+","+height);
	}
	
	public Level generateLevel ()
	{
		Log.d(LOG_TAG,"Generating level");
		
		//generate level
		generateEnemyPlanets();
		generatePlayerPlanets();
		//every planet is linked with another, find all of the possible paths between all
		//planets and fill in that info into the planets as ids
		populatePlanetPaths();
		
		Log.d(LOG_TAG,"Level generated");
		
		
		
		return new Level(m_levelPlanets,m_levelPaths);
	}
	
	private void generateEnemyPlanets()
	{
		float totalMissiles = TOTAL_STARTING_ENEMY_MISSILES;
		float missilesPerPlanet = (float)(totalMissiles/TOTAL_NUMBER_OF_PLANETS_PER_TEAM);
		while(totalMissiles>0)
		{
			totalMissiles-=missilesPerPlanet;
			//create new planet with null id since id is set by the planet manager
			Planet newEnemyPlanet = new Planet(getUniquePlanetId(), 0, 0, missilesPerPlanet * Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO, PlanetType.PLANET_TYPE_ENEMY, mEngine, this.vertexBufferObjectManager, gameManager, gameScene);
			Position pos = getLegalPosition(true);
			newEnemyPlanet.setPosition((int)pos.getX(),(int)pos.getY());
			m_levelPlanets.add(newEnemyPlanet);
		}
	}
	
	private void generatePlayerPlanets()
	{
		float totalMissiles = TOTAL_STARTING_PLAYER_MISSILES;
		float missilesPerPlanet = (float)(totalMissiles/TOTAL_NUMBER_OF_PLANETS_PER_TEAM);
		while(totalMissiles>0)
		{
			totalMissiles-=missilesPerPlanet;
			Planet newEnemyPlanet = new Planet(getUniquePlanetId(), 0, 0, missilesPerPlanet * Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO, PlanetType.PLANET_TYPE_PLAYER, mEngine, vertexBufferObjectManager, gameManager,gameScene);
			Position pos = getLegalPosition(false);
			newEnemyPlanet.setPosition((int)pos.getX(),(int)pos.getY());
			m_levelPlanets.add(newEnemyPlanet);
		}	
	}
	
	private int getUniquePlanetId()
	{
		int max = 0;
		for(int i = 0 ; i < m_levelPlanets.size();i++)
		{
			if(m_levelPlanets.get(i).getId()>max)
			{
				max = m_levelPlanets.get(i).getId();
			}
		}
		max +=1;
		return max;
	}
	
	private ArrayList <Path> populatePlanetPaths()
	{
		ArrayList <Path> paths = new ArrayList <Path>();
		ArrayList <Planet> unmatchedPlanets = (ArrayList<Planet>) m_levelPlanets.clone();
		
		Log.d("GravWar","LevelGenerator: Adding level paths");
		
		for (int i = 0; i < unmatchedPlanets.size(); i++) {
			  for (int j = i+1; j < unmatchedPlanets.size(); j++) {
			    // compare list.get(i) and list.get(j)
				  if(pathIsUnBlocked(unmatchedPlanets.get(i),unmatchedPlanets.get(j)))
				  {
					  Path newPath = new Path(unmatchedPlanets.get(i),unmatchedPlanets.get(j), vertexBufferObjectManager);
					  m_levelPaths.add(newPath);
					  Log.d("GravWar","LevelGenerator: new path created");
				  }
			}
		}
		return paths;
	}
	
	private boolean pathIsUnBlocked(Planet a, Planet b)
	{
		float x1 = a.getPosition().getX();
		float y1 = a.getPosition().getY();
		float x2 = b.getPosition().getX();
		float y2 = b.getPosition().getY();
		
		ArrayList <Planet> analysisPlanets = (ArrayList<Planet>) m_levelPlanets.clone();
		
		analysisPlanets = removePlanetFromArrayListByLocation(analysisPlanets,a);
		analysisPlanets = removePlanetFromArrayListByLocation(analysisPlanets,b);
		
		for(int i = 0; i < analysisPlanets.size() ; i++)
		{
			Planet tempPlanet =  analysisPlanets.get(i);
			
			if(circleToLineCollision(x1,y1,x2,y2,
					tempPlanet.getPosition().getX(),
					tempPlanet.getPosition().getY(),
					(Constants.PLANET_MAX_HEALTH*Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO)/2))
			{
				return false;
			}
		}
		return true;
	}
	
	private ArrayList<Planet> removePlanetFromArrayListByLocation(ArrayList<Planet> in , Planet planet)
	{
		for(int i = 0 ; i < in.size() ; i++)
		{
			if(in.get(i).getPosition().getX() == planet.getPosition().getX() &&
			   in.get(i).getPosition().getY() == planet.getPosition().getY())
			{
				in.remove(i);
				return in;
			}
		}
		return in;
	}
	
	private boolean circleToLineCollision(float ax, float ay, float bx, float by, float cx, float cy, float cr)
	{
	    float vx = bx - ax;
	    float vy = by - ay;
	    float xdiff = ax - cx;
	    float ydiff = ay - cy;
	    float a = (float) (Math.pow(vx, 2) + Math.pow(vy, 2));
	    float b = 2 * ((vx * xdiff) + (vy * ydiff));
	    float c = (float) (Math.pow(xdiff, 2) + Math.pow(ydiff, 2) - Math.pow(cr, 2));
	    float quad = (float) (Math.pow(b, 2) - (4 * a * c));
	    if (quad >= 0)
	    {
	        // An infinite collision is happening, but let's not stop here
	        float quadsqrt=(float) Math.sqrt(quad);
	        for (int i = -1; i <= 1; i += 2)
	        {
	            // Returns the two coordinates of the intersection points
	            float t = (i * -b + quadsqrt) / (2 * a);
	            float x = ax + (i * vx * t);
	            float y = ay + (i * vy * t);
	            // If one of them is in the boundaries of the segment, it collides
	            if (x >= Math.min(ax, bx) && x <= Math.max(ax, bx) && y >= Math.min(ay, by) && y <= Math.max(ay, by)) return true;
	        }
	    }
	    return false;
		
	}
	
	private Position getLegalPosition(boolean isEnemy)
	{
		float x = (float)(Math.random()*m_screenWidth);
		float y = (float)(Math.random()*m_screenHeight);
		float radius =(Constants.PLANET_MAX_HEALTH*Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO)/2;
		
		
		if(!isEnemy)
		{
			while(y-radius<0 || y+radius>m_midwayLine || x-radius<0 || x+radius>m_screenWidth || isCollidingWithAnyPlanet(x,y))
			{
				x = (float)(Math.random()*m_screenWidth);
				y = (float)(Math.random()*m_screenHeight);
			}
		}else
		{
			while(y-radius<m_midwayLine || y+radius>m_screenHeight || x-radius<0 || x+radius>m_screenWidth || isCollidingWithAnyPlanet(x,y))
			{
				x = (float)(Math.random()*m_screenWidth);
				y = (float)(Math.random()*m_screenHeight);
			}
		}
		
		Log.d("LevelGenerator", "getting position: "+x+","+y+" in screen size "+m_screenWidth+","+m_screenHeight);
		
		return new Position(x,y);
	}
	
	private boolean isCollidingWithAnyPlanet(float x, float y) {
		float r = (Constants.PLANET_MAX_HEALTH*Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO)/2;
		for(int i = 0 ; i < m_levelPlanets.size(); i++ )
		{
			Planet tempPlanet = m_levelPlanets.get(i);
			if(Utilities.circlesColliding(x, y, r, tempPlanet.getPosition().getX(), tempPlanet.getPosition().getY(), r)) return true;
		}
		return false;
	}
}
