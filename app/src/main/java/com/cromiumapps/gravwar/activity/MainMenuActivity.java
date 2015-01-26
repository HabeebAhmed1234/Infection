package com.cromiumapps.gravwar.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cromiumapps.gravwar.preferences.PreferencesManager;
import com.cromiumapps.gravwar.preferences.PreferencesManager.Difficulty;
import com.cromiumapps.gravwar.R;
import com.cromiumapps.gravwar.common.Utilities;

public class MainMenuActivity extends Activity implements OnClickListener{
	LinearLayout difficultysettingsframe;
	
	TextView titleText;
	LinearLayout quickGameButton;
	LinearLayout settingsButton;
	
	LinearLayout easyButton;
	LinearLayout normalButton;
	LinearLayout hardButton;
	LinearLayout exitButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		initViews();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
	private void initViews()
	{  
		//get views
		titleText = (TextView)findViewById(R.id.gametitletext);
	    TextView quickGameText = (TextView)findViewById(R.id.quickgamebuttontext);
	    TextView settingsText = (TextView)findViewById(R.id.settingsbuttontext);
	    TextView easyText = (TextView)findViewById(R.id.easybuttontext);
	    TextView mediumText = (TextView)findViewById(R.id.normalbuttontext);
	    TextView hardText = (TextView)findViewById(R.id.hardbuttontext);
	    TextView exitText = (TextView)findViewById(R.id.easybuttontext);
	    
	    quickGameButton = (LinearLayout)findViewById(R.id.quickgamebutton);
	    settingsButton = (LinearLayout)findViewById(R.id.settingsbutton);
	    easyButton = (LinearLayout)findViewById(R.id.easybutton);
	    normalButton = (LinearLayout)findViewById(R.id.normalbutton);
	    hardButton = (LinearLayout)findViewById(R.id.hardbutton);
	    exitButton = (LinearLayout)findViewById(R.id.exitbutton);
	    
	    difficultysettingsframe = (LinearLayout) findViewById(R.id.difficultysettingsframe);
	    
	    //click listeners
	    quickGameButton.setOnClickListener(this);
	    settingsButton.setOnClickListener(this);
	    easyButton.setOnClickListener(this);
	    normalButton.setOnClickListener(this);
	    hardButton.setOnClickListener(this);
	    exitButton.setOnClickListener(this);
	    
	    //fonts
	    titleText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	    quickGameText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	    settingsText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	    easyText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	    mediumText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	    hardText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	    exitText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	    
	    //set difficuly selection
	    Difficulty difficulty = PreferencesManager.getInstance(this).getDifficulty();
	    setDifficultySettingsBackgroundColor(difficulty);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.quickgamebutton)
		{
			Utilities.startMainActivity(this);
		}
		
		if(v.getId() == R.id.settingsbutton){
			if(difficultysettingsframe.getVisibility() == LinearLayout.VISIBLE){
				difficultysettingsframe.setVisibility(LinearLayout.GONE);
			}else{
				difficultysettingsframe.setVisibility(LinearLayout.VISIBLE);
			}
		}
		
		if(v.getId() == R.id.exitbutton){
			finish();
		}
		
		if(v.getId() == R.id.easybutton){
			PreferencesManager.getInstance(this).setDifficulty(Difficulty.EASY);
			setDifficultySettingsBackgroundColor(Difficulty.EASY);
		}

		if(v.getId() == R.id.normalbutton){
			PreferencesManager.getInstance(this).setDifficulty(Difficulty.NORMAL);
			setDifficultySettingsBackgroundColor(Difficulty.NORMAL);
		}
		
		if(v.getId() == R.id.hardbutton){
			PreferencesManager.getInstance(this).setDifficulty(Difficulty.HARD);
			setDifficultySettingsBackgroundColor(Difficulty.HARD);			
		}
	}
	
	private void setDifficultySettingsBackgroundColor(Difficulty difficulty){
		easyButton.setBackgroundResource(R.color.Black);
		normalButton.setBackgroundResource(R.color.Black);
		hardButton.setBackgroundResource(R.color.Black);
		if(difficulty == null) return;
		
		if(difficulty == Difficulty.EASY){
			easyButton.setBackgroundResource(R.color.Red);
		}else if(difficulty == difficulty.NORMAL){
			normalButton.setBackgroundResource(R.color.Red);			
		}else if(difficulty == Difficulty.HARD){
			hardButton.setBackgroundResource(R.color.Red);			
		}
	}
}
