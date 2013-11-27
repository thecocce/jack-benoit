package com.pokware.jb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.pokware.jb.screens.AbstractScreen;
import com.pokware.jb.screens.LevelScreen;
import com.pokware.jb.screens.ScreenListener;

public class JackBenoitApplication extends InputAdapter implements ApplicationListener, ScreenListener {
	
	private AbstractScreen currentScreen;
	
	public JackBenoitApplication() {
	}
	
	@Override
	public void render() {				
		currentScreen.render(0f);
	}
	
	@Override
	public void create() {
		// Load assets in static refs
		loadArt();
		
		AbstractScreen.listener = this;
//		currentScreen = new MenuScreen();
		currentScreen = new LevelScreen("");
	}

	private void loadArt() {
		Art.load(new TextureAtlas(Gdx.files.internal("data/output/pack")));
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
	}

	@Override
	public void notifyScreenChange(AbstractScreen newScreen) {
		this.currentScreen = newScreen;
	}
}
