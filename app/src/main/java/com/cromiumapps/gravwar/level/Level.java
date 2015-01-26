package com.cromiumapps.gravwar.level;

import com.cromiumapps.gravwar.Path;
import com.cromiumapps.gravwar.planets.Planet;

import java.util.ArrayList;

public class Level {
	ArrayList<Planet> planets;
	ArrayList<Path> paths;
	
	Level (ArrayList<Planet> planets,ArrayList<Path> paths)
	{
		this.planets = planets;
		this.paths = paths;
	}
	
	public ArrayList<Planet> getPlanets()
	{
		return planets;
	}
	
	public ArrayList<Path> getPaths()
	{
		return paths;
	}
	
}
