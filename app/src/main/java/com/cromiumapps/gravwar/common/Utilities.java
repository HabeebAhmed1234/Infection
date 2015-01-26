package com.cromiumapps.gravwar.common;

import com.cromiumapps.gravwar.Angle;
import com.cromiumapps.gravwar.Position;
import com.cromiumapps.gravwar.activity.GameFinishedActivity;
import com.cromiumapps.gravwar.activity.MainActivity;
import com.cromiumapps.gravwar.activity.MainMenuActivity;
import com.cromiumapps.gravwar.common.Constants;
import com.cromiumapps.gravwar.common.Constants.GAME_OUTCOME;
import com.cromiumapps.gravwar.missiles.Missile;
import com.cromiumapps.gravwar.planets.Planet;

import android.app.Activity;
import android.content.Intent;

public class Utilities {
	public static final String TAG = "Utilities";
	
	public static boolean isMissileCollidingWithPlanet(Missile missile, Planet planet)
	{
		return circlesColliding(missile.getPosition().getX(),missile.getPosition().getY(),missile.getRadius(),planet.getPosition().getX(),planet.getPosition().getY(),planet.getRadius());
	}
	
	public static boolean circlesColliding(float x1,float y1,float radius1,float x2,float y2,float radius2)
	{
	    //compare the distance to combined radii
		float dx = x2 - x1;
		float dy = y2 - y1;
		float radii = radius1 + radius2;
	    if ( ( dx * dx )  + ( dy * dy ) < radii * radii ) 
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}
	
	public static Angle getVectorAngleFromPoints(Position from,Position to)
	{
		Angle angle = new Angle((float)Math.atan2(to.getY() - from.getY(),to.getX() - from.getX()));
		return angle;
	}
	
	public static Angle getVectorAngleFromComponents(float xc,float yc)
	{
		Angle angle = new Angle((float)Math.atan2(yc , xc));
		return angle;
	}
	
	public static float [] getMissileVectorFromAngle(Angle theta)
	{
		float [] vxvy = new float [2];
		vxvy [0] = (float)(Math.cos(theta.get())* Constants.MISSILE_STARTING_MISSILE_SPEED);
		vxvy [1] = (float)(Math.sin(theta.get())*Constants.MISSILE_STARTING_MISSILE_SPEED);
		return vxvy;
	}
	
	public static void startMainActivity(Activity context)
	{
		Intent intent = new Intent(context, MainActivity.class);
	    context.startActivity(intent);
	    context.finish();
	}
	
	public static void startGameFinishedActivity(Activity context, GAME_OUTCOME gameOutCome, float timeElapsed)
	{
		Intent intent = new Intent(context, GameFinishedActivity.class);
		intent.putExtra(Constants.GAME_OUTCOME_EXTRA_KEY, gameOutCome.ordinal());
		intent.putExtra(Constants.GAME_TIME_ELAPSED_EXTRA_KEY, timeElapsed);
		context.startActivity(intent);  
		context.finish();
	}
	
	public static void startMainMenuActivity(Activity context)
	{
		Intent intent = new Intent(context, MainMenuActivity.class);
	    context.startActivity(intent);
	    context.finish();
	}
}
