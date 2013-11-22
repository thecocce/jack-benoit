package com.pokware.jb.objects;

import static com.pokware.jb.Constants.METERS_PER_TILE;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.pokware.jb.Constants;
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
		TiledMapTileLayer mapLayer = (TiledMapTileLayer)level.tiledMap.getLayers().get(Constants.BACKGROUND_LAYERS[0]);
		TiledMapTileLayer spriteLayer = (TiledMapTileLayer)level.tiledMap.getLayers().get(Constants.SPRITE_LAYERS[0]);
		int mapHeightInMeters = mapLayer.getHeight()*METERS_PER_TILE;
		
		add(new Jack(level, 20f, 20f));		
		// Spawn from tiles
			
		for (int y = spriteLayer.getHeight() - 1; y > 0; y--) {							
			for (int x = 0; x < spriteLayer.getWidth(); x++) {
				Cell cell = spriteLayer.getCell(x, y);				
				String tileProperty = "";
				if (cell != null) {
					Object object = cell.getTile().getProperties().get("spawn");
					if (object != null) {
						tileProperty = object.toString();						
					}
				}

				if ("Spider".equals(tileProperty)) {	
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE-0.2f;
					add(new Spider(level, xPosition, yPosition));
				}
				else if ("Zombie".equals(tileProperty)) {	
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE+1;
					add(new Zombie(level, xPosition, yPosition));
				}
				else if("blue_jewel".equals(tileProperty)) {
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE+1;
					add(new Jewel(level, xPosition, yPosition, JewelType.BLUE));
				}
				else if("big_blue_jewel".equals(tileProperty)) {
					float xPosition = x*METERS_PER_TILE+1;
					float yPosition = y*METERS_PER_TILE+1;
					add(new BigJewel(level, xPosition, yPosition, JewelType.BLUE));
				}
			}
		}	
	}
	
}
