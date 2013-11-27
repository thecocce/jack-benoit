package com.pokware.engine.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.pokware.jb.objects.GameObject;

public class PlayerControlActionComponent implements ActionComponent {

	private MovementIntent movementIntent;
	private float force;

	public void process() {
		force = 0f;
		movementIntent = null;
		
	}
	
	
	@Override
	public MovementIntent getMovementIntent() {	
		return movementIntent;
	}

	@Override
	public float getMovementForce() {	
		return force;
	}

	
	public float goUp() {
		if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
			return 10f;
		}
		else {
			float accelerometerX = Gdx.input.getAccelerometerX();
			return accelerometerX < 0f ? 10f : 0f; 
		}
	}

	public float goLeft() {
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			return 12f;
		}
		else {
			float accelerometerY = Gdx.input.getAccelerometerY();
			return accelerometerY < -0.1f ? Math.min(-accelerometerY*8, 12f) : 0f;  			
		}		
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

}
