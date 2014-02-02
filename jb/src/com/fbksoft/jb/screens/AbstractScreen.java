package com.fbksoft.jb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class AbstractScreen implements Screen {

	public static ShapeRenderer shapeRenderer = new ShapeRenderer();
	public static ScreenListener listener;
	
	public float curtainTransparency = 1;
	public boolean fadeInEnabled = false;
	public boolean fadeOutEnabled = false;
	
	public AbstractScreen() {		
	}
		
	public void transitionTo(AbstractScreen newScreen) {		
		listener.notifyScreenChange(newScreen);	
	}

	public void renderCurtain() {
		if (fadeInEnabled) {
			if (curtainTransparency > 0) {
		        curtainTransparency-=0.01;
			}
			else {
				onFadeInTermination();
				fadeInEnabled = false;
			}
		}
		if (fadeOutEnabled) {
			if (curtainTransparency < 1) {
		        curtainTransparency+=0.01;
			}
			else {
				onFadeOutTermination();
				fadeOutEnabled = false;
			}
		}
		
		if (curtainTransparency > 0) {
			drawCurtain();
		}
	}
	
	protected void onFadeOutStart() {
	}

	protected void onFadeInStart() {
	}

	protected void onFadeOutTermination() {
	}

	protected void onFadeInTermination() {
	}

	private void drawCurtain() {
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);        
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0f, 0f, 0f, curtainTransparency);
		shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shapeRenderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);
	}
	
	public void fadeIn() {
		fadeInEnabled = true;
		fadeOutEnabled = false;
		curtainTransparency = 1;
		onFadeInStart();
	}
	
	public void fadeOut() {
		fadeInEnabled = false;
		fadeOutEnabled = true;
		curtainTransparency = 0;
		onFadeOutStart();
	}

	@Override
	public void resize(int width, int height) {			
	}
	
	@Override
	public void dispose() {	
	}
	
	@Override
	public void resume() {	
	}
	
	@Override
	public void hide() {	
	}
	
	@Override
	public void pause() {		
	}
	
	@Override
	public void show() {		
	}
	
}
