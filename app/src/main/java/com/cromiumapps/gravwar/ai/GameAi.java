package com.cromiumapps.gravwar.ai;

import com.cromiumapps.gravwar.gameobjects.GameManager;
import com.cromiumapps.gravwar.exceptions.InvalidMoveException;
import com.cromiumapps.gravwar.Move;
import com.cromiumapps.gravwar.planets.Planet;
import com.cromiumapps.gravwar.common.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class GameAi {
    private static final String TAG = "GameAi";
	private GameManager gameManager;

	private ArrayList<Move> mMoveList = new ArrayList<Move>();
	Map<Integer, Set<Planet>> mAdjList;
	ArrayList<Planet> mAllPlanets;

	public GameAi(GameManager gameManager) {
		this.gameManager = gameManager;

		Runnable moveGenerator = new Runnable() {
			@Override
			public void run() {
				generateMoves();
				new Handler(Looper.getMainLooper()).postDelayed(this, (long) Constants.GAME_AI_GENERATE_MOVES_INTERVAL_SECS * 1000);
			}
		};

		new Handler(Looper.getMainLooper()).postDelayed(moveGenerator, (long) Constants.GAME_AI_GENERATE_MOVES_INTERVAL_SECS * 1000);

		mAdjList = gameManager.getHud().getAdjacencyList();
		mAllPlanets = gameManager.planetManager.getAllPlanets();
	}

	public void update(){
		synchronized (mMoveList) {
			if (mMoveList.size() > 0) {
				Log.d(TAG, "making a move");
				gameManager.makeAMove(mMoveList.get(0));
				mMoveList.remove(0);
			}
		}
	}

	private void generateMoves(){
		Log.d(TAG, "generating moves");
		synchronized (mMoveList) {
			Map<Integer, Boolean> targetList = new HashMap<Integer, Boolean>(); // if a planet is targeted then it is added to this map as true

			for (Planet planet : mAllPlanets) {
				if (mAdjList.containsKey(planet.getId()) && planet.isEnemy()) {
					Set<Planet> adjacentPlanets = mAdjList.get(planet.getId());
					for (Planet adjPlanet : adjacentPlanets) {
						boolean isTargeted = false;
						if (targetList.containsKey(adjPlanet.getId())) {
							isTargeted = targetList.get(adjPlanet.getId());
						}
						if (adjPlanet.isPlayerPlanet() && !isTargeted) {
							try {
								mMoveList.add(new Move((int)planet.getHealthInMissiles() / 5, planet, adjPlanet, true));
								targetList.put(adjPlanet.getId(), true);
							} catch (InvalidMoveException e) {
								Log.e(TAG, e.getMessage());
							}
						} else if (adjPlanet.isEnemy()) {
							if (adjPlanet.getPosition().getY() > planet.getPosition().getY()) {
								try {
									mMoveList.add(new Move((int)planet.getHealthInMissiles() / 5, planet, adjPlanet, true));
								} catch (InvalidMoveException e) {
									Log.e(TAG, e.getMessage());
								}
							}
						}
					}
				}
			}

			Log.d(TAG, "currently " + mMoveList.size() + " moves are pending");
		}
	}

	/*
	private Move getMove(){
		ArrayList<Move> movesList = new ArrayList<Move>();
		ArrayList <Planet> allPlanets = gameManager.planetManager.getAllPlanets();
		
		for(int i = 0 ; i < allPlanets.size() ; i++)
		{
			for(int x = 0 ; x < allPlanets.size() ; x++)
			{
				try{
					Planet planetA = allPlanets.get(i);
					Planet planetB = allPlanets.get(x);
					if(planetA.getId() != planetB.getId() && gameManager.getHud().arePlanetsConnected(planetA, planetB))
					{
						if(planetA.isEnemy() && planetB.isPlayerPlanet())
						{
							movesList.add(new Move(1, planetA, planetB,true));
						}
						
						if(planetB.isEnemy() && planetA.isPlayerPlanet())
						{
							movesList.add(new Move(1, planetB, planetA,true));
						}
						
						if(planetA.isEnemy() && planetB.isNeutral())
						{
							movesList.add(new Move(1, planetA, planetB,true));
						}
						
						if(planetA.isEnemy() && planetB.isEnemy())
						{
							if(planetA.getPosition().getY()>planetB.getPosition().getY()){
								movesList.add(new Move(1, planetA, planetB,true));
							}else{
								movesList.add(new Move(1, planetB, planetA,true));
							}
						}
					}
				}catch (InvalidMoveException e){
					e.printWhat();
				}
			}	
		}
		Move bestMove = getBestMove(movesList);
        if (bestMove!= null) bestMove.missilesToFireAmmount = getNumberOfMissilesToFire(bestMove);
		return bestMove;
	}
	
	private Move getBestMove(ArrayList<Move> moves){
		Move [] movesArray = moves.toArray(new Move[moves.size()]);
		Arrays.sort (movesArray, new Comparator<Move>() {
            @Override
            public int compare(Move a, Move b) {
            	Planet aToPlanet = gameManager.planetManager.getPlanetByID(a.toPlanetId);
            	Planet bToPlanet = gameManager.planetManager.getPlanetByID(b.toPlanetId);
            	Planet aFromPlanet = gameManager.planetManager.getPlanetByID(a.fromPlanetId);
            	Planet bFromPlanet = gameManager.planetManager.getPlanetByID(b.fromPlanetId);
            	
            	//the bigger a factor the better the move
            	//the weights should add up to 100
            	float aPositionFactor = getPositionFactor(aToPlanet, 30);
            	float aHealthFactor = getHealthFactor(aFromPlanet, aToPlanet,70);

            	float bPositionFactor = getPositionFactor(bToPlanet, 30);
            	float bHealthFactor = getHealthFactor(bFromPlanet, bToPlanet,70);
            	
            	float aFactor = aPositionFactor + aHealthFactor;
            	float bFactor = bPositionFactor + bHealthFactor;
            	
            	if(aFactor > bFactor){
            		return 1;
            	}else if (aFactor == bFactor){
            		return 0;
            	}else if (aFactor < bFactor){
            		return -1;
            	}
            	return 0;
            }
        });
		 
		if(movesArray.length > 0){
			return movesArray[0];
		}else{
			return null;
		}
	}
	
	//minimum factor value is 0 and max factor value is weight
	private float getPositionFactor(Planet moveToPlanet, float weight){
        if (moveToPlanet.isPlayerPlanet()) {
            //if we are shooting at a player planet then we want to hit the nearest one on the bottom half of the screen
            return (gameManager.getGameCamera().getHeight() - moveToPlanet.getPosition().getY()) / gameManager.getGameCamera().getHeight() * weight;
        } else {
            //if we are docking to a enemy planet then we want to dock to the farthest freindly planet
            return moveToPlanet.getPosition().getY() / gameManager.getGameCamera().getHeight() * weight;
        }
	}
	
	private float getHealthFactor(Planet moveFromPlanet, Planet moveToPlanet, float weight){
		float diff = moveFromPlanet.getHealthInMissiles() - moveToPlanet.getHealthInMissiles();
		if(diff<0) return 0;
        if (moveToPlanet.isPlayerPlanet()) {
             return moveToPlanet.getHealthInMissiles()/Constants.PLANET_MAX_HEALTH * weight;
        } else {
            return (Constants.PLANET_MAX_HEALTH - moveToPlanet.getHealthInMissiles()) / Constants.PLANET_MAX_HEALTH * weight;
        }
	}
	
	private int getNumberOfMissilesToFire(Move move) {
        if (move == null) return 0 ;
        final Planet toPlanet = gameManager.planetManager.getPlanetByID(move.toPlanetId);
        final Planet fromPlanet = gameManager.planetManager.getPlanetByID(move.fromPlanetId);
        int numMissiles = 0;
        if (toPlanet.isPlayerPlanet()) {
            if (fromPlanet.getHealthInMissiles() < toPlanet.getHealthInMissiles()) {
                numMissiles = (int)Math.min(toPlanet.getHealthInMissiles(), fromPlanet.getHealthInMissiles());
                numMissiles = numMissiles/2;
            } else {
                numMissiles = (int)Math.min(toPlanet.getHealthInMissiles(), fromPlanet.getHealthInMissiles());
            }
        } else {
            if (fromPlanet.getHealthInMissiles() >= toPlanet.getHealthInMissiles()) {
                numMissiles = (int)Math.min(fromPlanet.getHealthInMissiles(), toPlanet.getHealthInMissiles() - fromPlanet.getHealthInMissiles());
            } else {
                numMissiles = (int)Math.min(fromPlanet.getHealthInMissiles(), toPlanet.getHealthInMissiles() - fromPlanet.getHealthInMissiles());
                numMissiles = numMissiles/2;
            }
        }
        if (numMissiles == fromPlanet.getHealthInMissiles()) numMissiles--;
        return numMissiles;
	}*/
}