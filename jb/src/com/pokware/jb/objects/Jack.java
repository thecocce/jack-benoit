package com.pokware.jb.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.pokware.jb.Art;
import com.pokware.jb.Constants;
import com.pokware.jb.Level;

public class Jack extends GameObject implements Climber, InputProcessor {
	
	public static float WALK_POWER = 3;
	public static float JUMP_POWER = 1700;
	public static float CLIMB_POWER = 200;

	public JackStateEnum state = JackStateEnum.IDLE;
	public JackStateEnum lastState = null;
	public int lastLadderStatus = 0;
	
	public static int life = 3;
	public int mojo = 3;
	public int score = 0;
	public boolean dead = false;
	
	// invicibility management
	public int grantInvisibilityOnNextRender = 0;
	public float invicibilityExpirationTime = -1;
	public boolean invicible;
	
	public Vector2 antiGravityVector;
	public boolean wasDraggingDown = false;
	public Vector2 forceVector = new Vector2();
	public boolean wasClimbing = false;	
		
			
	public Jack(int id, Level level, float x, float y) {
		super(id, level, x, y, CollisionCategory.JACK, false);
		body.setBullet(true);			
		antiGravityVector = level.gravityVector.cpy().scl(-body.getMass());
		
		Gdx.input.setInputProcessor(this);		
	}
	
	
	protected void createFixtures(Body body, float widthRatio, float heightRatio) {
		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(width*0.2f, height*0.4f);
		fixture = body.createFixture(polyShape, 5);
		fixture.setFriction(0f);
		polyShape.dispose();		
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.40f);
		circle.setPosition(new Vector2(0f, -0.5f));
		fixture = body.createFixture(circle, 0);
		fixture.setFriction(0f);
		circle.dispose();		
	}

	@Override
	public TextureRegion getTextureRegion(float tick) {
		manageInvicibilityStatus(tick);						
		checkCollisions();
		
		state = lastState != null ? lastState : JackStateEnum.IDLE;
		
		int ladderStatus = getLadderStatus();
		if (ladderStatus == GameObject.NO_LADDER && lastLadderStatus != NO_LADDER) {
			state = JackStateEnum.IDLE;
		}			
		float goRight = goRight();
		float goLeft = goLeft();
						
		if (lastLadderStatus == GameObject.NO_LADDER && 
				(ladderStatus == GameObject.LADDER || ladderStatus == GameObject.LADDER + GameObject.LADDER_BELOW)) {
			if (body.getLinearVelocity().y != 0.0f) {
				body.setLinearVelocity(0f, 0f);
			}
		}
		else {			
			if (goRight > 0) {
				switch(ladderStatus) {
				case GameObject.LADDER:
					body.applyLinearImpulse(forceVector.set(4.4f*goRight, 0.0f), FORCE_APPLICATION_POINT, true);break;
				case GameObject.LADDER + GameObject.LADDER_BELOW:
					body.applyLinearImpulse(forceVector.set(1.4f*goRight, 0.0f), FORCE_APPLICATION_POINT, true);break;
				default:
					body.applyLinearImpulse(forceVector.set(6.4f*goRight, 0.0f), FORCE_APPLICATION_POINT, true);break;		
				}
			}		
			else if (goLeft > 0) {
				switch(ladderStatus) {				
				case GameObject.LADDER:
					body.applyLinearImpulse(forceVector.set(-4.4f*goLeft, 0.0f), FORCE_APPLICATION_POINT, true);break;
				case GameObject.LADDER + GameObject.LADDER_BELOW:
					body.applyLinearImpulse(forceVector.set(-1.4f*goLeft, 0.0f), FORCE_APPLICATION_POINT, true);break;				
				default:
					body.applyLinearImpulse(forceVector.set(-6.4f*goLeft, 0.0f), FORCE_APPLICATION_POINT, true);break;
				}								
			}
		}
		

		if (ladderStatus == GameObject.LADDER || ladderStatus == GameObject.LADDER + GameObject.LADDER_BELOW) {
			state = JackStateEnum.CLIMBING_IDLE;
			if (!wasDraggingDown) {
				body.applyForce(antiGravityVector, FORCE_APPLICATION_POINT, true);
			}
		}			
		
		wasClimbing = isClimbing();
		if (body.getLinearVelocity().x > 0 && !wasClimbing) {
			state =  JackStateEnum.WALK_RIGHT;
		}
		else if (body.getLinearVelocity().x < 0 && !wasClimbing) {
			state =  JackStateEnum.WALK_LEFT;
		}
		
		boolean looping = Math.abs(body.getLinearVelocity().x) > 0.1f || Math.abs(body.getLinearVelocity().y) > 0.1f;
		
		lastLadderStatus = ladderStatus;
		lastState = state;
		
		
		TextureRegion keyFrame = state.getAnimation().getKeyFrame(looping ? tick : 0, true);
		if (invicible) {		
			if (Math.random() > 0.5) {	
				return null;			
			}
		}
		
		return keyFrame;
		
	}


	private void checkCollisions() {
		Vector2 position = body.getPosition();		
		int tileX = (int) (position.x/Constants.METERS_PER_TILE);				
		int tileY = (int) (position.y/Constants.METERS_PER_TILE);		
		TiledMapTileLayer ladderLayer = (TiledMapTileLayer)level.tiledMap.getLayers().get(Constants.LADDER_LAYER);			 					
		Cell cell = ladderLayer.getCell(tileX, tileY);
		if (cell!=null && Constants.HAZARD_ZONE.equals(cell.getTile().getProperties().get("col"))) {	
			GameObjectData gameObjectData = (GameObjectData)body.getUserData();
			if (gameObjectData.flying) {
				onHit();
			}
		}
	}


	private void manageInvicibilityStatus(float tick) {
		if (grantInvisibilityOnNextRender > 0) {			
			invicibilityExpirationTime = tick + grantInvisibilityOnNextRender;
			grantInvisibilityOnNextRender = 0;
			invicible = true;
		}
		if (invicibilityExpirationTime < tick) {
			invicible = false;
			invicibilityExpirationTime = -1;
		}
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
			return WALK_POWER;
		}
		else {
			float accelerometerY = Gdx.input.getAccelerometerY();
			return accelerometerY < -0.2f ? Math.min(-accelerometerY*WALK_POWER*2, WALK_POWER) : 0f;  			
		}		
	}

	public float goRight() {
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			return WALK_POWER;
		}
		else {
			float accelerometerY = Gdx.input.getAccelerometerY();
			return accelerometerY > 0.2f ? Math.min(accelerometerY*WALK_POWER*2, WALK_POWER) : 0f;  			
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


	public JackStateEnum getState() {
		return state;
	}
	
	@Override
	public String toString() {
		return "UP:"+goUp() + " DOWN:"+goDown() + " LEFT:"+goLeft() + " RIGHT:"+goRight();
	}


	
	final Vector2 jumpVector = new Vector2();
	public void jump() {		
		if (wasClimbing) {					
				if (isTopOfTheLadder()) {
					body.applyLinearImpulse(jumpVector.set(0f, CLIMB_POWER*2), FORCE_APPLICATION_POINT, true);	
				}
				else {
					body.applyLinearImpulse(jumpVector.set(0f, CLIMB_POWER), FORCE_APPLICATION_POINT, true);
				}									
		} else {
			Vector2 linearVelocity = body.getLinearVelocity();			
			if (Math.abs(linearVelocity.y) < 0.0001f) {							
				body.applyLinearImpulse(jumpVector.set(0f, JUMP_POWER), FORCE_APPLICATION_POINT, true);
				Art.jumpSound.play();
			}			
		}		
	}
		
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {	
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {	
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {	
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {	
		return false;
	}
		 	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (screenX < Gdx.graphics.getWidth() / 2) {			
			wasDraggingDown = true;
		}
		else {
			jump();
		}
		return true;
	}
	

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return true;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		wasDraggingDown = false;
		return true;
	}

	public int getLife() {
		return life;
	}

	public int getMojo() {
		return mojo;
	}


	public int getScore() {
		return score;
	}
	
	public void incScore(int delta) {
		score+=delta;
	}


	public void onHit() {
		if (!invicible) {
			Art.hurtSound.play();
			
			if (mojo > 0) {				
				mojo--;				
			}
			
			if (mojo > 0) {
				grantInvisibilityOnNextRender = 2;
				invicible = true;
			}
			else {
				dead = true;
				invicible = true;
				grantInvisibilityOnNextRender = 10;
				life--;
				level.reset();
			}
		}
	}

	public void onItemCollected(Collectable collectable) {
		incScore(collectable.getScoreValue());
	}

	
	public static enum JackStateEnum {
		IDLE(Art.walkingRightAnimation), 
		WALK_LEFT(Art.walkingLeftAnimation),
		WALK_RIGHT(Art.walkingRightAnimation),
		CLIMBING_UP(Art.climbingAnimation),
		CLIMBING_DOWN(Art.climbingAnimation), 
		CLIMBING_IDLE(Art.climbingAnimation);
		private Animation animation;

		private JackStateEnum(Animation animation) {
			this.animation = animation;
		}
		
		public Animation getAnimation() {
			return animation;
		}
	}


	public boolean isInvicible() {
		return invicible;
	}
}
