package com.pokware.engine;

public class Entity {
	
	public ActionComponent actionComponent;
	public LogicComponent logicComponent;
	public RenderComponent renderComponent;
	public PhysicsComponent physicsComponent;

	public Entity() {	
	}

	public void process(long tick) {
		if (actionComponent != null) {
			actionComponent.process();
		}
		if (logicComponent != null) {
			logicComponent.process(actionComponent);
		}
		if (physicsComponent != null) {
			physicsComponent.process(actionComponent.getMovementIntent(), tick);
		}
		if (renderComponent != null) {
			renderComponent.process(tick);
		}		
	}
	
}
