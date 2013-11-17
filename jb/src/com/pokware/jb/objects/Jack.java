package com.pokware.jb.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.pokware.jb.Art;
import com.pokware.jb.Level;

public class Jack extends GameObject implements Climber, InputProcessor {
	
	public static float WALK_POWER = 3;
	public static float JUMP_POWER = 700;
	public static float CLIMB_POWER = 200;
	
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
	private JackStateEnum lastState = null;
	private int life = 3;
	private int score;
	private Vector2 antiGravityVector;
			
	public Jack(Level level, float x, float y) {
		super(level, x, y, CollisionCategory.JACK, false);
		body.setBullet(true);			
		antiGravityVector = level.gravityVector.cpy().mul(-body.getMass());
		
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

	final Vector2 forceVector = new Vector2();
	boolean wasClimbing = false;
	
	
	int lastLadderStatus = 0;
	
	@Override
	public TextureRegion getTextureRegion(float tick) {		
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
					body.applyLinearImpulse(forceVector.set(4.4f*goRight, 0.0f), FORCE_APPLICATION_POINT);break;
				case GameObject.LADDER + GameObject.LADDER_BELOW:
					body.applyLinearImpulse(forceVector.set(1.4f*goRight, 0.0f), FORCE_APPLICATION_POINT);break;
				default:
					body.applyLinearImpulse(forceVector.set(6.4f*goRight, 0.0f), FORCE_APPLICATION_POINT);break;		
				}
			}		
			else if (goLeft > 0) {
				switch(ladderStatus) {				
				case GameObject.LADDER:
					body.applyLinearImpulse(forceVector.set(-4.4f*goLeft, 0.0f), FORCE_APPLICATION_POINT);break;
				case GameObject.LADDER + GameObject.LADDER_BELOW:
					body.applyLinearImpulse(forceVector.set(-1.4f*goLeft, 0.0f), FORCE_APPLICATION_POINT);break;				
				default:
					body.applyLinearImpulse(forceVector.set(-6.4f*goLeft, 0.0f), FORCE_APPLICATION_POINT);break;
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
		lastState = state;
		return state.getAnimation().getKeyFrame(looping ? tick : 0, true);
		
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
				body.applyLinearImpulse(jumpVector.set(0f, CLIMB_POWER*2), FORCE_APPLICATION_POINT);	
			}
			else {
				body.applyLinearImpulse(jumpVector.set(0f, CLIMB_POWER), FORCE_APPLICATION_POINT);
			}						
		} else {
			Vector2 linearVelocity = body.getLinearVelocity();			
			if (Math.abs(linearVelocity.y) < 0.0001f) {							
				body.applyLinearImpulse(jumpVector.set(0f, JUMP_POWER), FORCE_APPLICATION_POINT);
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
		jump();
		return true;
	}

	private Vector3 curr = new Vector3();
	private Vector3 last = new Vector3();
	private Vector3 delta = new Vector3();
	public boolean wasDraggingUp = false;
	public boolean wasDraggingDown = false;
	
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
				
		level.camera.front.unproject(curr.set(screenX, screenY, 0));
		if (!(last.x == -1 && last.y == -1 )) {
			level.camera.front.unproject(delta.set(last.x, last.y, 0));
			delta.sub(curr);
			
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
	
			if (lastLadderStatus == GameObject.LADDER || lastLadderStatus == GameObject.LADDER + GameObject.LADDER_BELOW || lastLadderStatus == GameObject.LADDER_BELOW) {					
				if (delta.y < 0) {
					// drag up				
					wasDraggingUp = true;
					body.applyLinearImpulse(forceVector.set(0.0f, -delta.y*20), FORCE_APPLICATION_POINT);
				}
				else if (delta.y > 0) {
					// drag down								
					wasDraggingDown = true;
					body.applyLinearImpulse(forceVector.set(0.0f, -delta.y*20), FORCE_APPLICATION_POINT);
				}					
			}
		}
		last.set(screenX, screenY, 0);
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {				
		wasDraggingDown = false;
		wasDraggingUp = false;
		last.set(-1, -1, -1);
		return true;
	}


	public int getLife() {
		return life;
	}


	public int getScore() {
		return score;
	}
	
	public void incScore(int delta) {
		score+=delta;
	}


	public void decrementLife() {
		if (life > 1) {
			life--;
		}
	}

	public void onItemCollected(Collectable collectable) {
		incScore(collectable.getScoreValue());
	}


}
