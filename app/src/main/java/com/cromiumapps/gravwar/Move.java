package com.cromiumapps.gravwar;

import com.cromiumapps.gravwar.planets.Planet;
import com.cromiumapps.gravwar.planets.Planet.PlanetType;
import com.cromiumapps.gravwar.exceptions.InvalidMoveException;

public class Move{	
	public float missilesToFireAmmount;
	public boolean isAiMove;
	public PlanetType fromPlanetType;
	public PlanetType toPlanetType;
	public float fromPlanetId;
	public float toPlanetId;
	
	public Move(float missilesToFireAmmount, Planet from, Planet to, boolean isAiMove) throws InvalidMoveException
	{
		if(from.getId() == to.getId()) throw new InvalidMoveException("fromid and toid are identical");
		if(missilesToFireAmmount <= 0) throw new InvalidMoveException("missiles to fire are <= 0");
		fromPlanetType = from.getPlanetType();
		toPlanetType = to.getPlanetType();
		this.missilesToFireAmmount = missilesToFireAmmount;
		this.isAiMove = isAiMove;
		this.fromPlanetId = from.getId();
		this.toPlanetId = to.getId();
	}
	
}

