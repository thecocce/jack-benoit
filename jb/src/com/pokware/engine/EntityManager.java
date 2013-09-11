package com.pokware.engine;

import java.util.ArrayList;

public class EntityManager {
	
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public EntityManager() {			
	}
	
	public void process(long tick) {
		for (Entity entity : entities) {			
			entity.process(tick);
		}
	}
}
