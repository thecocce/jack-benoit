package com.pokware.engine;

public interface ActionComponent {

	void process();
	
	MovementIntent getMovementIntent();

	float getMovementForce();

}
