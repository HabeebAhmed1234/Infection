package com.cromiumapps.gravwar.gameobjects;

import java.util.ArrayList;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import android.util.Log;

public class GameResourceManager {
	public static final String TAG = "GameTextureManager"; 
	
	public static final int ENEMY_PLANET_TEXTURE_INDEX = 0;
	public static final int NEUTRAL_PLANET_TEXTURE_INDEX = 1;
	public static final int PLAYER_PLANET_TEXTURE_INDEX = 2;
	public static final int PLANET_SELECTOR_TEXTURE_INDEX = 3;

	public static final String FONT_FILE_PATH = "fnt/heavy_data.ttf";
	public static final String PLANET_IMAGE_FILE_PATH = "Planet.png";
	public static final String MISSILE_PLAYER_IMAGE_FILE_PATH = "Missile_Player.png";
	public static final String MISSILE_ENEMY_IMAGE_FILE_PATH = "Missile_Enemy.png";
	
	public static final String EXPLOSION_1_IMAGE_FILE_PATH = "explosion1.png";
	public static final String EXPLOSION_2_IMAGE_FILE_PATH = "explosion2.png";
	
	public static ITiledTextureRegion tiledPlanetTexture; 
	private static ITiledTextureRegion missileTexturePlayer;
	private static ITiledTextureRegion missileTextureEnemy;

	private static ArrayList<ITiledTextureRegion> explosionTextures = new ArrayList<ITiledTextureRegion>();
	
	public static Font font; 
	
	public static void loadAllResources(SimpleBaseGameActivity context){
		GameResourceManager.loadTextures(context);
		GameResourceManager.loadFonts(context);
	}
	
	public static ITiledTextureRegion getMissileTexture(boolean isPlayerMissile){
		if(isPlayerMissile) return missileTexturePlayer;
		return missileTextureEnemy;
	}
	
	public static ITiledTextureRegion getExplosionTextureRegion() {
		int index = (int) (Math.random() * 2);
		return explosionTextures.get(index);
	}
	
	private static void loadTextures(SimpleBaseGameActivity context)
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		BitmapTextureAtlas BitmapTextureAtlas1 = new BitmapTextureAtlas(context.getTextureManager(), 1024, 1024);
		BitmapTextureAtlas BitmapTextureAtlas2 = new BitmapTextureAtlas(context.getTextureManager(), 1024, 1024);
		
		tiledPlanetTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(BitmapTextureAtlas1, context, PLANET_IMAGE_FILE_PATH, 0, 0, 2, 2);
		missileTexturePlayer = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(BitmapTextureAtlas1, context, MISSILE_PLAYER_IMAGE_FILE_PATH, 0, 920,1,1);
		missileTextureEnemy = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(BitmapTextureAtlas1, context, MISSILE_ENEMY_IMAGE_FILE_PATH, 33, 920,1,1);

		explosionTextures.add(BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(BitmapTextureAtlas2, context, EXPLOSION_1_IMAGE_FILE_PATH, 0, 0,8,1));
		explosionTextures.add(BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(BitmapTextureAtlas2, context, EXPLOSION_2_IMAGE_FILE_PATH, 0, 128,5,1));
		
		Log.d(TAG, "BitmapTextureAtlas.load"); 
		BitmapTextureAtlas1.load(); 
		BitmapTextureAtlas2.load();
	}
	
	private static void loadFonts(SimpleBaseGameActivity context){
		font = FontFactory.createFromAsset(context.getFontManager(), context.getTextureManager(), 256, 256, context.getAssets(),
			    FONT_FILE_PATH, 18, true, android.graphics.Color.WHITE);
	    font.load();
	}
}
