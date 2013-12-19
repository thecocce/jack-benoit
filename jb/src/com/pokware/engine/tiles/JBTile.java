package com.pokware.engine.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

public enum JBTile {

	WORLD1_PLATFORM_LEFT(5),
	WORLD1_PLATFORM(6),
	WORLD1_PLATFORM_RIGHT(7),
	BACK1_LIGHT1(15),
	BACK1_DARK1(16),
	WORLD1_LADDER_TOP(17),
	WORLD1_LADDER(18),
	FLOWER(19),
	ROAD_SIGN_RIGHT(20),
	WORLD1_GROUND(21),
	WORLD1_DIRT1(22),
	WORLD1_DIRT_HALF(23),
	WORLD1_BACK_PLAIN(24),
	BACK1_LIGHT2(31),
	BACK1_DARK2(32),
	SPIDER(33),
	ZOMBIE(34),
	JACK(35),
	ROAD_SIGN_LEFT(36),
	WORLD1_DIRT2(37),
	WORLD1_DIRT_SKELETON(38),
	EXIT(39),
	X_MARK(40),
	WORLD1_BACK_OUTER_TOP_RIGHT(42),
	WORLD1_BACK_OUTER_TOP(43),
	BACK1_NORTH_WEST(47),
	BACK1_NORTH_EAST(48),
	BACK1_SOUTH_WEST(63),
	BACK1_SOUTH_EAST(64),
	BOULDER(113),
	SPIKE1(130),
	SPIKE2(131),
	BIG_BLUE_JEWEL(225),
	BLUE_JEWEL(241);
	
	public int id;
	
	JBTile(int id) {
		this.id = id;
	}
	
	public TiledMapTile fromTileSet(TiledMapTileSet set) {
		return set.getTile(id);
	}
	
	public Cell toCell(TiledMapTileSet tileSet) {
		Cell cell = new Cell();
		cell.setTile(fromTileSet(tileSet));
		return cell;
	}

	public static JBTile fromCell(Cell cell) {
		if (cell == null) 
			return null;
		int cellTileId = cell.getTile().getId();
		JBTile[] values = values();
		for (JBTile jbTile : values) {
			if (jbTile.id == cellTileId) {
				return jbTile;
			}
		}
		return null;
	}
	
	public boolean isPlatformTile() {
		return name().contains("PLATFORM");
	}
	
	
	
}
