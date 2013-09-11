package com.pokware.engine;

public interface PhysicsComponent {

	void process(MovementIntent movementIntent, long tick);

}
