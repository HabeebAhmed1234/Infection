package com.cromiumapps.gravwar.missiles;

import java.util.ArrayList;

public class MissileIdRegistry {
	public static ArrayList <Integer> REGISTERED_MISSILE_IDS = new ArrayList <Integer>();
	
	public static void registerId(int id)
	{
		for(int i = 0 ; i < REGISTERED_MISSILE_IDS.size() ; i++)
		{
			if(REGISTERED_MISSILE_IDS.get(i)==id)
			{
				return;
			}
		}
		
		REGISTERED_MISSILE_IDS.add(id);
	}
	
	public static void unRegisterId(float id)
	{
		for(int i = 0 ; i < REGISTERED_MISSILE_IDS.size() ; i++)
		{
			if(REGISTERED_MISSILE_IDS.get(i)==id)
			{
				REGISTERED_MISSILE_IDS.remove(i);
				return;
			}
		}
	}
	
	public static int getUniqueMissileId()
	{
		int max = 0;
		for(int i = 0 ; i < REGISTERED_MISSILE_IDS.size();i++)
		{
			if(REGISTERED_MISSILE_IDS.get(i)>max)
			{
				max = REGISTERED_MISSILE_IDS.get(i);
			}
		}
		max +=1;
		registerId(max);
		return max;
		
	}
}
