package com.pokware.jb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.pokware.jb.objects.Jack;

public class HUD {

	ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	public void draw(Level level, SpriteBatch spriteBatch, float timer) {		

		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();
		
		Jack jack = level.objectManager.getJack();
		int life = jack.getMojo();
		for(int i = 0; i < life; i++) {					
			TextureRegion keyFrame = Art.heartAnimation.getKeyFrame(timer, true);			
			spriteBatch.draw(keyFrame, Gdx.graphics.getWidth()-128+32*i, Gdx.graphics.getHeight()-40, 32, 32);			
		}
		
		Art.bitmapFont.draw(spriteBatch, String.format("SCORE: %08d  JACK: %d   MOJO:", jack.getScore(), jack.getLife()), 20, Gdx.graphics.getHeight()-20);				
		spriteBatch.end();
	}

	
}
