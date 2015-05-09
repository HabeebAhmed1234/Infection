package com.cromiumapps.gravwar.planets;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.cromiumapps.gravwar.gameobjects.GameManager;
import com.cromiumapps.gravwar.gameobjects.GameResourceManager;
import com.cromiumapps.gravwar.gameobjects.GameScene;
import com.cromiumapps.gravwar.gameobjects.GameSprite;
import com.cromiumapps.gravwar.Position;
import com.cromiumapps.gravwar.common.Constants;

public class Planet {

	public static enum PlanetType {PLANET_TYPE_ENEMY,PLANET_TYPE_PLAYER,PLANET_TYPE_NEUTRAL};
	
	//graphics stuff
	private VertexBufferObjectManager vertexBufferObjectManager;
	private GameScene gameScene;
	private GameManager gameManager;
	private Text healthText;
	
	//game stuff
	private boolean isAddedToScreen = false;
	private float m_diameter;
	private float m_healthInMissiles;
	private float m_id;
	private PlanetType m_planetType = PlanetType.PLANET_TYPE_NEUTRAL;
	private ArrayList<Integer> m_linkedPlanets;
	private GameSprite m_planetSprite;
	private GameSprite m_planetSelectorSprite;
	private boolean m_isSelected;
	
	public Planet(float id
            , float x
            , float y
            , float diameter
            , PlanetType planetType
            , Engine engine
            , VertexBufferObjectManager vertexBufferObjectManager
            , final GameManager gameManager
            , GameScene gameScene)
	{
		this.vertexBufferObjectManager = vertexBufferObjectManager;
		this.gameScene = gameScene;
		this.gameManager = gameManager;
		m_healthInMissiles = diameter/ Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO;
		m_diameter = diameter;
		m_id = id;
		m_planetType = planetType;
		m_isSelected = false;
		
		ITiledTextureRegion planetTexture = GameResourceManager.tiledPlanetTexture;
		
		m_planetSprite = new GameSprite(x,y,planetTexture, vertexBufferObjectManager,true,engine){
		    @Override
		    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		    	gameManager.onTouchPlanet(pSceneTouchEvent,(Float) (this.getUserData()));
				return true;
		    }
		};

		m_planetSprite.setCurrentTileIndex(GameResourceManager.NEUTRAL_PLANET_TEXTURE_INDEX);
		if(m_planetType == PlanetType.PLANET_TYPE_ENEMY)m_planetSprite.setCurrentTileIndex(GameResourceManager.ENEMY_PLANET_TEXTURE_INDEX);
		if(m_planetType == PlanetType.PLANET_TYPE_PLAYER)m_planetSprite.setCurrentTileIndex(GameResourceManager.PLAYER_PLANET_TEXTURE_INDEX);
		
		m_planetSelectorSprite = new GameSprite(x, y,planetTexture, vertexBufferObjectManager,true,engine);
		m_planetSelectorSprite.setCurrentTileIndex(GameResourceManager.PLANET_SELECTOR_TEXTURE_INDEX);
		
		m_planetSprite.setUserData(m_id);
		
		m_planetSprite.setScale(m_diameter/m_planetSprite.getWidth());
		m_planetSelectorSprite.setScale(m_planetSprite.getScaleX()+0.1f);
		
		Log.d("PositionSet","final planet position +++++++++ "+this.getId()+" at position "+this.getPosition().getX()+","+this.getPosition().getX());
	}
	
	public void select()
	{	
		if(this.isEnemy())return;
		if(this.isNeutral())return;
		
		if(!m_isSelected)
		{
			gameScene.attachChild(m_planetSelectorSprite);
			m_planetSelectorSprite.setScale(m_planetSprite.getScaleX()+0.1f);
			m_isSelected = true;
			return;
		}else
		{
			if(this.m_healthInMissiles>Constants.PLANET_MISSILES_PER_SELECTION)
			{
				if(damageHealth(Constants.PLANET_MISSILES_PER_SELECTION))
					gameManager.numMissilesReadyToFire += Constants.PLANET_MISSILES_PER_SELECTION;
			}
		}
	}
	
	public void deSelect()
	{
		gameScene.detachChild(m_planetSelectorSprite);
		
		if(m_isSelected)
		{
			increaseHealth(gameManager.numMissilesReadyToFire);
			gameManager.numMissilesReadyToFire = 0;
		}
		
		m_isSelected = false;
	}
	
	public boolean isSelected()
	{
		return m_isSelected;
	}
	
	public GameSprite getSprite()
	{
		return this.m_planetSprite;
	}
	
	public Position getPosition()
	{
		return new Position (m_planetSprite.getX(),m_planetSprite.getY());
	}
	
	public void setPosition(int x, int y)
	{
		//m_planetSprite.getX()-m_planetSprite.getWidth()/2, m_planetSprite.getY()-m_planetSprite.getHeight()/2
		this.m_planetSprite.setX(x);
		this.m_planetSprite.setY(y);
	}
	
	public float getDiameter()
	{
		return this.m_diameter;
	}
	
	public float getRadius()
	{
		return this.m_diameter/2;
	}
	
	public float getHealthInMissiles()
	{
		return this.m_healthInMissiles;
	}
	
	//returns true is sucessfully damaged else false
	public boolean damageHealth(float damage)
	{
		if(m_planetType == PlanetType.PLANET_TYPE_NEUTRAL || (m_healthInMissiles-damage)<0) return false;
		this.m_healthInMissiles -= damage;
		if(this.m_healthInMissiles <=0)
		{
			convertTo(PlanetType.PLANET_TYPE_NEUTRAL);
			this.m_healthInMissiles = 0;
		}
		updateSprite();
		updateHealthText();
		return true;
	}
	
	public void increaseHealth(float increase)
	{
		if(m_planetType == PlanetType.PLANET_TYPE_NEUTRAL) return;
		if((this.m_healthInMissiles + increase)>Constants.PLANET_MAX_HEALTH){
			m_healthInMissiles = Constants.PLANET_MAX_HEALTH;
		}else{
			this.m_healthInMissiles += increase;
		}
		updateSprite();
		updateHealthText();
	}
	
	public void convertTo(PlanetType planetTypeToConvertTo)
	{
		m_planetType = planetTypeToConvertTo;
		m_isSelected = false;
		updateSprite();
	}
	
	public float getId()
	{
		return this.m_id;
	}
	
	public PlanetType getPlanetType()
	{
		return this.m_planetType;
	}

	public boolean isAboutToDie(){
		return this.m_healthInMissiles <= Constants.PLANET_MAX_HEALTH/4;
	}

	public boolean isEnemy()
	{
		return m_planetType == PlanetType.PLANET_TYPE_ENEMY;
	}
	
	public boolean isNeutral()
	{
		return m_planetType == PlanetType.PLANET_TYPE_NEUTRAL;
	}
	
	public boolean isPlayerPlanet()
	{
		return m_planetType == PlanetType.PLANET_TYPE_PLAYER;
	}
	
	public void setIsEnemy(boolean isEnemy)
	{
		this.m_planetType = PlanetType.PLANET_TYPE_ENEMY;
	}
	
	public void addLinkedPlanet(int id)
	{
		m_linkedPlanets.add(id);
	}
	
	public void removeLinkedPlanet(int id)
	{
		for (int i = 0 ; i < m_linkedPlanets.size() ; i++)
		{
			if(m_linkedPlanets.get(i) == id)
			{
				m_linkedPlanets.remove(i);
				return;
			}
		}
	}
	
	public void update()
	{
		updatePlanetSmoothMotion();
		updatePlanetSelectorRotation();
	}
	
	private void updatePlanetSmoothMotion(){
		
	}
	
	private void updatePlanetSelectorRotation()
	{
		this.m_planetSelectorSprite.setRotation(m_planetSelectorSprite.getRotation() + Constants.PLANET_SELECTOR_ROTATION_SPEED);
	}
	
	public void setIsAddedToScreen(boolean isAdded)
	{
		isAddedToScreen = isAdded;
	}
	
	public void addToScene(){
		gameScene.attachChild(this.m_planetSprite);
		initHealthText();
	}
	
	private void initHealthText(){
		healthText = new Text(0, 0, GameResourceManager.font, Integer.toString((int)Math.round(m_healthInMissiles)),100, this.vertexBufferObjectManager);
	    this.gameScene.attachChild(healthText);
	    healthText.setPosition(this.getPosition().getX(), this.getPosition().getY());
	}
	
	private void updateHealthText(){
		healthText.setText(Integer.toString((int)m_healthInMissiles));
	}
	
	private void updateSprite(){
		float fakeHealth = m_healthInMissiles+Constants.MINIMUM_PLANET_DIAMETER_IN_MISSILES; //this is just used for the purposes of rendering the sprite
		this.m_diameter = (fakeHealth) * Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO;
		this.m_planetSprite.setScaleSmooth(((fakeHealth)*Constants.PLANET_HEALTH_IN_MISSILES_TO_DIAMETER_RATIO)/m_planetSprite.getWidth());

		m_planetSprite.setCurrentTileIndex(GameResourceManager.NEUTRAL_PLANET_TEXTURE_INDEX);
		if(m_planetType == PlanetType.PLANET_TYPE_ENEMY)m_planetSprite.setCurrentTileIndex(GameResourceManager.ENEMY_PLANET_TEXTURE_INDEX);
		if(m_planetType == PlanetType.PLANET_TYPE_PLAYER)m_planetSprite.setCurrentTileIndex(GameResourceManager.PLAYER_PLANET_TEXTURE_INDEX);
	}
}
