package com.cromiumapps.gravwar.gameobjects;

import com.cromiumapps.gravwar.Angle;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;

public class GameSprite extends AnimatedSprite  {
	//private boolean isCoordinateCenterInCenter = false;
	public static final float SMOOTH_SCALE_THRESHOLD = 0.01f;
	public static final float SMOOTH_SCALE_INCREMENT = 0.005f; 
	
	private float targetScale;
	private boolean smoothScaleOn = false;
	
	private Engine mEngine;
	public GameSprite(float x, float y, ITiledTextureRegion  texture,VertexBufferObjectManager vertexBufferObjectManager,boolean isCoordinateCenterInCenter, Engine engine) {
		super(x, y, texture,vertexBufferObjectManager);
		mEngine = engine;
	}
	
	public void setAngle(Angle a)
	{
		setRotation(a.get());
	}
	
	public Angle getAngle()
	{
		return new Angle(this.getRotation());
	}
	
	@Override
	public void setRotation(float radians)
	{
		while(radians>(Math.PI*2)){radians-=(Math.PI*2);}
		while(radians<(0)){radians+=(Math.PI*2);}
		this.mRotation = MathUtils.radToDeg((float) ((radians-Math.PI/2)*-1));
	}
	
	@Override 
	public float getRotation()
	{
		float radians = (float) ((MathUtils.degToRad((float)this.mRotation)*-1)+Math.PI/2);
		while(radians>(Math.PI*2)){radians-=(Math.PI*2);}
		while(radians<0){radians+=(Math.PI*2);}
		return radians;
	} 
	
	public void animate(long durationPerFrame, boolean destroyOnFinish){
		if(destroyOnFinish){
			super.animate(durationPerFrame,false,new IAnimationListener(){

				@Override
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
						int pInitialLoopCount) {
					
				}

				@Override
				public void onAnimationFrameChanged(
						AnimatedSprite pAnimatedSprite, int pOldFrameIndex,
						int pNewFrameIndex) {
					
				}

				@Override
				public void onAnimationLoopFinished(
						AnimatedSprite pAnimatedSprite,
						int pRemainingLoopCount, int pInitialLoopCount) {
					
				}

				@Override
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
					mEngine.runOnUpdateThread(new Runnable() {
				        @Override
				        public void run() {
							detachSelf();
				        }
				    });
				}
			
			});
		}else{
			super.animate(durationPerFrame);
		}
	}
			
	/**
	 *call this to set the sprite scale smoothly 
	 */
	public void setScaleSmooth(float scale){
		targetScale = scale;
		smoothScaleOn = true;
	}
	
	@Override
	public void onCustomUpdate(float pSecondsElapsed){
		if(smoothScaleOn){
			if(this.getScaleX()>targetScale){
				this.setScale(this.getScaleX() - SMOOTH_SCALE_INCREMENT);
			}else{
				this.setScale(this.getScaleX() + SMOOTH_SCALE_INCREMENT);
			}
			
			if(this.getScaleX() < targetScale + SMOOTH_SCALE_THRESHOLD && this.getScaleX() > targetScale - SMOOTH_SCALE_THRESHOLD){
				this.setScale(targetScale);
				smoothScaleOn = false;	
			}
		}
	}
}
