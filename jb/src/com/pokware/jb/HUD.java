package com.pokware.jb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pokware.jb.objects.Jack;

public class HUD {

	public void draw(Level level, SpriteBatch spriteBatch, float timer) {		
				
		Jack jack = level.objectManager.getJack();
		int life = jack.getLife();
		for(int i = 0; i < life; i++) {					
			TextureRegion keyFrame = Art.heartAnimation.getKeyFrame(timer, true);			
			spriteBatch.draw(keyFrame, Gdx.graphics.getWidth()-128+32*i, Gdx.graphics.getHeight()-40, 32, 32);			
		}
		
		Art.bitmapFont.draw(spriteBatch, String.format("SCORE: %08d  JACK: 3   MOJO:", jack.getScore()), 20, Gdx.graphics.getHeight()-20);				
		
	}

	
}
