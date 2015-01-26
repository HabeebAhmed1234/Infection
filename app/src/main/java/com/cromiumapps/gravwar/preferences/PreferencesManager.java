package com.cromiumapps.gravwar.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	
	public static final String APP_PREFERENCES = "application_preferences";
	public static enum Difficulty {EASY,NORMAL,HARD};
	
	public static final String DIFFICULTY_KEY = "difficulty_pref_key";
	public static final String EXPERIENCE_KEY = "experience_pref_key";
	
	private SharedPreferences sharedPreferences;
	private Editor editor;
	
	private Context mContext;
	private static PreferencesManager instance;
	
	public static final PreferencesManager getInstance(Context context){
		if(instance == null) return new PreferencesManager(context);
		return instance;
	}
	
	private void setInt(String key, int value){
		editor.putInt(key, value);
		editor.commit();
	}
	
	private int getInt(String key){
		return sharedPreferences.getInt(key, -1);
	}
	
	private PreferencesManager(Context context){
		mContext = context;
		sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}
	
	public void setDifficulty (Difficulty difficulty){
		setInt(DIFFICULTY_KEY, difficulty.ordinal());
	}
	
	public Difficulty getDifficulty(){
		int diff = getInt(DIFFICULTY_KEY);
		if(diff == -1) return null;
		return Difficulty.values()[getInt(DIFFICULTY_KEY)];
	}
	
	public void setExperience(int experience){
		setInt(EXPERIENCE_KEY, experience);
	}

	public int getExperience(){
		int exp = getInt(EXPERIENCE_KEY);
		if(exp == -1) return 0;
		return getInt(EXPERIENCE_KEY);
	}
}
