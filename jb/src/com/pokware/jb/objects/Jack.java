package com.pokware.jb.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pokware.jb.Art;
import com.pokware.jb.Level;

public class Jack extends GameObject implements Climber {
	
	public static enum JackStateEnum {
		IDLE(Art.walkingLeftAnimation), 
		WALK_LEFT(Art.walkingLeftAnimation),
		WALK_RIGHT(Art.walkingRightAnimation),
		CLIMBING_UP(Art.climbingAnimation),
		CLIMBING_DOWN(Art.climbingAnimation), 
		CLIMBING_IDLE(Art.climbingAnimation),
		FALLING(Art.walkingLeftAnimation);
		private Animation animation;

		private JackStateEnum(Animation animation) {
			this.animation = animation;
		}
		
		public Animation getAnimation() {
			return animation;
		}
	}

	private JackStateEnum state = JackStateEnum.IDLE;
		
	private Vector2 antiGravityVector;
			
	public Jack(Level level, float x, float y) {
		super(level, x, y, CollisionCategory.JACK);
		body.setBullet(true);			
		antiGravityVector = level.gravityVector.cpy().mul(-body.getMass()).mul(0.8f);				
	}	

	final Vector2 forceVector = new Vector2();
	boolean wasClimbing = false;
	
	
	int lastLadderStatus = 0;
	@Override
	public TextureRegion getTextureRegion(float tick) {
		state = JackStateEnum.IDLE;
		body.setGravityScale(0.8f);
//		body.setLinearDamping(0f);
		
		int ladderStatus = getLadderStatus();	
		
		
		
		float goRight = goRight();
		float goLeft = goLeft();
		/*float goUp = goUp();
		float goDown = goDown();*/
		
		if (lastLadderStatus == GameObject.NO_LADDER && 
				(ladderStatus == GameObject.LADDER || ladderStatus == GameObject.LADDER + GameObject.LADDER_BELOW)) {
			if (body.getLinearVelocity().y != 0.0f) {
				body.setLinearVelocity(0f, 0f);
			}
		}
		else {
			if (goRight > 0) {
				if (ladderStatus != GameObject.LADDER && ladderStatus != GameObject.LADDER + GameObject.LADDER_BELOW) {
					body.applyLinearImpulse(forceVector.set(6.4f*goRight, 0.0f), FORCE_APPLICATION_POINT);
				}
				else {
					body.applyLinearImpulse(forceVector.set(3.2f*goRight, 0.0f), FORCE_APPLICATION_POINT);
				}
			}		
			else if (goLeft > 0) {
				if (ladderStatus != GameObject.LADDER && ladderStatus != GameObject.LADDER + GameObject.LADDER_BELOW) {
					body.applyLinearImpulse(forceVector.set(-6.4f*goLeft, 0.0f), FORCE_APPLICATION_POINT);
				}
				else {
					body.applyLinearImpulse(forceVector.set(-3.2f*goLeft, 0.0f), FORCE_APPLICATION_POINT);
				}
			}
		}
		
		/*if (goUp > 0) {					
			if (ladderStatus == GameObject.LADDER || ladderStatus == GameObject.LADDER + GameObject.LADDER_BELOW) {
				state = JackStateEnum.CLIMBING_UP;
				body.applyForce(antiGravityVector, FORCE_APPLICATION_POINT);
				
				body.applyLinearImpulse(forceVector.set(0.0f, 6.4f*goUp), FORCE_APPLICATION_POINT);
			}
			else if (ladderStatus == GameObject.LADDER_BELOW && wasClimbing) {
				// Last steps of the ladder: re-enable gravity so jack "jump" onto the platform
				state = JackStateEnum.IDLE;
				body.setLinearVelocity(0f, 64f);
			}
		}		
		else if (goDown > 0) {					
			if (ladderStatus == GameObject.LADDER_BELOW || ladderStatus == GameObject.LADDER + GameObject.LADDER_BELOW) {
				state = JackStateEnum.CLIMBING_DOWN;
				body.applyForce(antiGravityVector, FORCE_APPLICATION_POINT);
				
				body.applyLinearImpulse(forceVector.set(0.0f, -6.4f*goDown), FORCE_APPLICATION_POINT);
			}
		}*/
		//else {
			if (ladderStatus == GameObject.LADDER || ladderStatus == GameObject.LADDER + GameObject.LADDER_BELOW) {
				state = JackStateEnum.CLIMBING_IDLE;
				body.applyForce(antiGravityVector, FORCE_APPLICATION_POINT);
			}			
		//}
		
		wasClimbing = isClimbing();
		if (body.getLinearVelocity().x > 0 && !wasClimbing) {
			state =  JackStateEnum.WALK_RIGHT;
		}
		else if (body.getLinearVelocity().x < 0 && !wasClimbing) {
			state =  JackStateEnum.WALK_LEFT;
		}
		
		boolean looping = Math.abs(body.getLinearVelocity().x) > 0.1f || Math.abs(body.getLinearVelocity().y) > 0.1f;
		
		lastLadderStatus = ladderStatus;
		return state.getAnimation().getKeyFrame(tick, looping);
		
	}

	private float goUp() {
		if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
			return 10f;
		}
		else {
			float accelerometerX = Gdx.input.getAccelerometerX();
			return accelerometerX < 0f ? 10f : 0f; 
		}
	}

	private float goLeft() {
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			return 12f;
		}
		else {
			float accelerometerY = Gdx.input.getAccelerometerY();
			return accelerometerY < -0.1f ? Math.min(-accelerometerY*8, 12f) : 0f;  			
		}		
	}

	@Override
	public boolean isClimbing() {
		return state == JackStateEnum.CLIMBING_DOWN || state == JackStateEnum.CLIMBING_IDLE || state == JackStateEnum.CLIMBING_UP;
	}
	
	public float goDown() {
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			return 10f;
		}
		else {
			float accelerometerX = Gdx.input.getAccelerometerX();
			return accelerometerX > 0f ? 10f : 0f;  			
		}			
	}

	public float goRight() {
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			return 12f;
		}
		else {
			float accelerometerY = Gdx.input.getAccelerometerY();
			return accelerometerY > 0.1f ? Math.min(accelerometerY*8, 12f) : 0f;  			
		}
	}

	public JackStateEnum getState() {
		return state;
	}
	
	@Override
	public String toString() {
		return "UP:"+goUp() + " DOWN:"+goDown() + " LEFT:"+goLeft() + " RIGHT:"+goRight();
	}


	final Vector2 jumpVector = new Vector2();
	
	public void jump() {				
		int ladderStatus = getLadderStatus();
		boolean ladder = ladderStatus == GameObject.LADDER || ladderStatus == GameObject.LADDER + GameObject.LADDER_BELOW;
		if (ladder) {
			body.applyLinearImpulse(jumpVector.set(0f, 1200f), FORCE_APPLICATION_POINT);
		} else {
			Vector2 linearVelocity = body.getLinearVelocity();			
			if (linearVelocity.y == 0.0f) {				
				body.applyLinearImpulse(jumpVector.set(0f, 3000f+Math.abs(linearVelocity.x)*50), FORCE_APPLICATION_POINT);				
			}
		}		
	}
		
}
