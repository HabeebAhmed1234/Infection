package com.cromiumapps.gravwar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.cromiumapps.gravwar.gameobjects.GameResourceManager;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.planets.Planet;
import com.cromiumapps.gravwar.planets.Planet.PlanetType;
import com.cromiumapps.gravwar.planets.PlanetManager;

import android.util.Log;



public class HUD {
	public static final String TAG = "HUD";
	private ArrayList <Path> paths = new ArrayList<Path>();
	private GameScene gameScene;
	private VertexBufferObjectManager vertexBufferObjectManager;
	private Text missilesSelectedText;
	
	public HUD(ArrayList<Path> paths, GameScene gameScene, VertexBufferObjectManager vertexBufferObjectManager)
	{
		this.gameScene = gameScene;
		this.paths = paths;
		this.vertexBufferObjectManager = vertexBufferObjectManager;
		Log.d("GravWar","HUD: HUD initialized with "+this.paths.size() +" paths");
		renderPaths();
		initMissilesSelectedText();
	}

	public Map <Integer, Set<Planet>> getAdjacencyList(){
		Map <Integer, Set<Planet>> adjList = new HashMap<Integer, Set<Planet>>();
		for (Path path : paths) {
			if (adjList.containsKey(path.planetA)) {
				adjList.get(path.planetA).add(path.planetB);
			} else {
				Set newList = new HashSet<Planet>();
				newList.add(path.planetB);
				adjList.put(path.planetA.getId(),newList);
			}
		}
		return adjList;
	}
	
	public boolean arePlanetsConnected(Planet a, Planet b){
		for(Path path : paths){
			if(path.isIncidentTo(a, b)) return true;
		}
		return false;
	}
	
	public void renderPaths() 
	{
		//Log.d("GravWar","HUD: rendering paths");
		for(int i =0 ;i<paths.size();i++)
		{
			//Log.d("GravWar","HUD: adding new path to scene");
			gameScene.attachChild(paths.get(i).getLine());
		}
	}
	
	public boolean isMovePermissible(Move move, PlanetManager planetManager)
	{
		Planet fromPlanet = planetManager.getPlanetByID(move.fromPlanetId);
		if(fromPlanet.isNeutral())return false;
		if(!fromPlanet.isEnemy() && move.isAiMove || fromPlanet.isEnemy() && !move.isAiMove) return false;
		
		for(int i = 0 ; i< paths.size() ; i++)
		{
			Path path = paths.get(i);
			if((path.planetA.getId() == move.fromPlanetId && path.planetB.getId() == move.toPlanetId) || 
			   (path.planetB.getId() == move.fromPlanetId && path.planetA.getId() == move.toPlanetId)) {
					return true;
			}
		}
		
		return false;
	}
	
	private void initMissilesSelectedText(){
		this.missilesSelectedText = new Text(50, 50, GameResourceManager.font, "0", 5, vertexBufferObjectManager);
	    //this.gameScene.attachChild(missilesSelectedText);
	}
	
	public void updateMissilesSelectedText(float numMissiles){
		missilesSelectedText.setText(Float.toString(numMissiles));
	}
}
