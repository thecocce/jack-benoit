package com.fbksoft.jb;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fbksoft.engine.ActionResolver;
import com.fbksoft.jb.Constants;
import com.fbksoft.jb.JackBenoitApplication;
import com.fbksoft.jb.GameHelper.GameHelperListener;


public class MainActivity extends AndroidApplication implements GameHelperListener, ActionResolver {
	
	private GameHelper gameHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        Constants.ZOOM_FACTOR = 0.5f;
        		
        initialize(new JackBenoitApplication(this), cfg);        
        gameHelper.setup(this);		        
    }

	public MainActivity(){
		gameHelper = new GameHelper(this);
		gameHelper.enableDebugLog(true, "GPGS");
	}
	

	@Override
	public void onStart(){
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameHelper.onActivityResult(request, response, data);
	}
	
	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable(){
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
		}
	}

	@Override
	public void submitScoreGPGS(int score) {
		gameHelper.getGamesClient().submitScore(getString(R.string.leaderboard_id), score);
	}
	
	@Override
	public void unlockAchievementGPGS(String achievementId) {
		gameHelper.getGamesClient().unlockAchievement(achievementId);
	}
	
	@Override
	public void getLeaderboardGPGS() {
		startActivityForResult(gameHelper.getGamesClient().getLeaderboardIntent(getString(R.string.leaderboard_id)), 100);
	}

	@Override
	public void getAchievementsGPGS() {
		startActivityForResult(gameHelper.getGamesClient().getAchievementsIntent(), 101);
	}
	
	@Override
	public void onSignInFailed() {
	}

	@Override
	public void onSignInSucceeded() {
	}
}
