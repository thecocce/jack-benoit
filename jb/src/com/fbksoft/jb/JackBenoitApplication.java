package com.fbksoft.jb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.fbksoft.engine.ActionResolver;
import com.fbksoft.jb.screens.AbstractScreen;
import com.fbksoft.jb.screens.MenuScreen;
import com.fbksoft.jb.screens.ScreenListener;

public class JackBenoitApplication extends InputAdapter implements ApplicationListener, ScreenListener {
	
	private AbstractScreen currentScreen;
	public ActionResolver actionResolver;
	
	public JackBenoitApplication(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
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
		currentScreen = new MenuScreen();
	}

	private void loadArt() {
		Art.load(new TextureAtlas(Gdx.files.internal("data/output/pack.atlas")));
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
