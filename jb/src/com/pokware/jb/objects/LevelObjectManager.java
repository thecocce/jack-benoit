package com.pokware.jb.objects;

import static com.pokware.jb.Constants.*;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pokware.jb.Level;

public class LevelObjectManager {

	private List<GameObject> list = new ArrayList<GameObject>(150);
	
	private Level level;
	
	public LevelObjectManager(Level level) {	
		this.level = level;					
	}
	
	public void add(GameObject gameObject) {
		if (gameObject.id == list.size()) {
			list.add(gameObject);	
		}
		else {
			list.add(gameObject.id, gameObject);	
		}				
	}
	
	public GameObject get(int id) {
		return list.get(id);
	}
	
	public Jack getJack() {
		return (Jack) list.get(0);
	}
	
	public void draw(SpriteBatch spriteBatch, float tick) {	
		for (int i = 0; i < list.size(); i++) {
			GameObject gameObject = list.get(i);		
			gameObject.render(spriteBatch, tick);
		}		
	}

	public void drawDebugInfo() {		
		for (int i = 0; i < list.size(); i++) {
			GameObject gameObject = list.get(i);	
			gameObject.renderDebugInfo();
		}		
	}	
	
	public Level getWorld() {
		return level;
	}

	public int size() {
		return list.size();
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		for (GameObject gameObject : list) {
			buffer.append(""+i+";"+gameObject + "\n");
			i++;
		}
		return buffer.toString();
	}

	public void populateLevel() {
		
		add(new Jack(level, 20f, level.tiledMap.height*METERS_PER_TILE - 20f));
		
		// Spawn from tiles
		int[][] interactionTiles = level.tiledMap.layers.get(SPRITE_LAYERS[0]).tiles;
		for (int y = interactionTiles.length - 1; y > 0; y--) {
			int[] row = interactionTiles[y];					
			for (int x = 0; x < row.length; x++) {
				int id = row[x];
				String tileProperty = level.tiledMap.getTileProperty(id, "spawn");
				int mapHeightInMeters = level.tiledMap.height*METERS_PER_TILE;
				if ("Spider".equals(tileProperty)) {	
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE+2.2f;
					add(new Spider(level, xPosition, mapHeightInMeters-yPosition));
				}
				else if ("Zombie".equals(tileProperty)) {	
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE+1;
					add(new Zombie(level, xPosition, mapHeightInMeters-yPosition));
				}
				else if("blue_jewel".equals(tileProperty)) {
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE+1;
					add(new Jewel(level, xPosition, mapHeightInMeters-yPosition, JewelType.BLUE));
				}
				else if("big_blue_jewel".equals(tileProperty)) {
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE+1;
					add(new BigJewel(level, xPosition, mapHeightInMeters-yPosition, JewelType.BLUE));
				}
			}
		}
		/*for (TiledObjectGroup group : tiledMap.objectGroups) {
			for (TiledObject object : group.objects) {
				if (object.type != null) {
					int x = object.x/16;
					int y = tiledMap.height*2-object.y/16;
					if (object.type.equals("blue_jewel")) {			
						objectManager.add(new Jewel(this, x, y));
					}
					else if (object.type.equals("zombie")) {
						objectManager.add(new Zombie(this, x, y));
					}
					else if (object.type.equals("spider")) {
						objectManager.add(new Spider(this, x, y));
					}
				}				
			}
		}*/		
	}
	
}
