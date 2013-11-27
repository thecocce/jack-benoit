package com.pokware.engine.components;

public interface ActionComponent {

	void process();
	
	MovementIntent getMovementIntent();

	float getMovementForce();

}
