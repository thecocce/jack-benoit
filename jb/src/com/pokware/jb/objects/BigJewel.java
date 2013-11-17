package com.pokware.jb.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pokware.jb.Art;
import com.pokware.jb.Level;

public class BigJewel extends GameObject implements Collectable {

	private JewelType type;
	
	public BigJewel(Level level, float x, float y, JewelType type) {
		super(level, x, y, 32, 32, CollisionCategory.COLLECTABLE, false);
		this.type = type;
	}
	
	@Override
	public TextureRegion getTextureRegion(float tick) {			
		body.applyLinearImpulse(0, -1f, body.getPosition().x, body.getPosition().y);
		
		switch(type) {
			case BLUE: return Art.bigBlueJewelAnimation.getKeyFrame(tick, true);			
		}
		return null;
	}

	@Override
	public int getScoreValue() {
		return type.getScoreValue() * 10;
	}
	
}
