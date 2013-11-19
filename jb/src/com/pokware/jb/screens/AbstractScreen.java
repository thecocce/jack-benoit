package com.pokware.jb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class AbstractScreen {

	public static ShapeRenderer shapeRenderer = new ShapeRenderer();
	public static ScreenListener listener;
		
	public AbstractScreen() {		
		Gdx.gl.glEnable(GL10.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);        
		shapeRenderer.begin(ShapeType.FilledRectangle);
        shapeRenderer.setColor(0f, 0f, 0f, 0.9f);
        shapeRenderer.filledRect(0, 0, 300, 20);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);

	}
		
	public void transitionTo(AbstractScreen newScreen) {		
		listener.notifyScreenChange(newScreen);	
	}

	public abstract void render();
		
}
