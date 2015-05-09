package com.cromiumapps.gravwar.gameobjects;

import org.andengine.engine.camera.Camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;



public class GameCamera extends Camera {
	//shake variables
	boolean mShaking;
	float mDuration;
	float mIntensity;
	float mCurrentDuration;

	float mX;
	float mY;

	//camera variables
	public static final int FPS = 30; 
	public static float GAME_SCALING_FACTOR = 1;
	
	public static float desiredWidth = 560;
	public static float desiredHeight = 1000;
	
	private Activity context;
	
	public GameCamera(Activity context) {
		super(0, 0, desiredWidth, desiredHeight);

		mShaking = false;

		mX = this.getCenterX();
		mY = this.getCenterY();

		this.context = context;
		//setScreenSize();
	}
	
	public float getScreenHeight(){
		return this.getHeight();
	}

	@SuppressLint("NewApi")
	private void setScreenSize()
	{
		int width = 0;
		int height = 0;
		
		Display display = context.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
		    //Do something for API 17 only (4.2)
			display.getRealSize(size);
			width = size.x;
			height = size.y;
		}
		else if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
		    // Do something for API 13 and above , but below API 17 (API 17 will trigger the above block
		    //getSize()
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else{
		    // do something for phones running an SDK before Android 3.2 (API 13)
		    //getWidth(), getHeight()
			width=display.getWidth();
			height = display.getHeight();
		}
		GAME_SCALING_FACTOR = (desiredWidth + desiredHeight) / (width + height);
		desiredWidth= width;
		desiredHeight = height;
	}



	public void shake(float duration, float intensity){
		mShaking = true;
		mDuration = duration;
		mIntensity = intensity;
		mCurrentDuration = 0;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		super.onUpdate(pSecondsElapsed);

		if(mShaking){
			mCurrentDuration+=pSecondsElapsed;
			if(mCurrentDuration>mDuration)
			{
				mShaking = false;
				mCurrentDuration = 0;
				this.setCenter( mX, mY);
			}
			else{
				int sentitX =   1;
				int sentitY =   1;
				if(Math.random() < 0.5) sentitX = -1;
				if(Math.random() < 0.5) sentitY = -1;
				this.setCenter( (float)(mX + Math.random()*mIntensity*sentitX),
						(float)(mY + Math.random()*mIntensity*sentitY));
			}
		}
	}
}
