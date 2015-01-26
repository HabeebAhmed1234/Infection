package com.cromiumapps.gravwar.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cromiumapps.gravwar.R;
import com.cromiumapps.gravwar.common.Utilities;
import com.cromiumapps.gravwar.common.Constants;
import com.cromiumapps.gravwar.common.Constants.GAME_OUTCOME;

public class GameFinishedActivity extends Activity implements OnClickListener {
	private GAME_OUTCOME gameOutCome;
	private float gameTime = 0;
	
	private TextView outComeText;
	private TextView timeText;
	private LinearLayout restartButton;
	private LinearLayout mainMenuButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_finished);
		
		Intent intent = getIntent();
		if(intent!=null)
		{
			Bundle extras = intent.getExtras();
			if(extras!=null)
			{
				gameOutCome = GAME_OUTCOME.values()[(Integer)extras.get(Constants.GAME_OUTCOME_EXTRA_KEY)];
				gameTime = (Float) extras.get(Constants.GAME_TIME_ELAPSED_EXTRA_KEY);
			}
		}
		
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game_finished, menu);
		return true;
	}
	
	private void initViews()
	{
		outComeText = (TextView)findViewById(R.id.gameoutcometext);
		
		if(gameOutCome == GAME_OUTCOME.WIN) outComeText.setText(getResources().getString(R.string.gamewonmessage));
		if(gameOutCome == GAME_OUTCOME.LOSE) outComeText.setText(getResources().getString(R.string.gamelostmessage));
		
		timeText = (TextView)findViewById(R.id.timetext);
		timeText.setText("time: "+gameTime);
		
		restartButton = (LinearLayout)findViewById(R.id.restartbutton);
		restartButton.setOnClickListener(this);
		
		mainMenuButton = (LinearLayout)findViewById(R.id.mainmenubutton);
		mainMenuButton.setOnClickListener(this);
		
		((TextView)findViewById(R.id.restartbuttontext)).setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
		((TextView)findViewById(R.id.mainmenubuttontext)).setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
		timeText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
		outComeText.setTypeface(Typeface.createFromAsset(getAssets(),"fnt/heavy_data.ttf"));
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.restartbutton)
		{
			Utilities.startMainActivity(this);
		}
		
		if(v.getId() == R.id.mainmenubutton)
		{
			Utilities.startMainMenuActivity(this);
		}
	}
}
