package com.fbksoft.jb.screens;

public class GameOverScreen extends AbstractScreen {

	public GameOverScreen() {
		fadeIn();
	}
	
	@Override
	public void render(float delta) {
		
	}
	
	@Override
	protected void onFadeInTermination() {
		fadeOut();
	}
	
	@Override
	protected void onFadeOutTermination() {
		transitionTo(new MenuScreen());
	}

}
