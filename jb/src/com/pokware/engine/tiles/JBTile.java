package com.pokware.engine.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

public enum JBTile {

	WORLD1_PLATFORM_LEFT(5),
	WORLD1_PLATFORM(6),
	WORLD1_PLATFORM_RIGHT(7),
	WORLD1_LADDER_TOP(17),
	WORLD1_LADDER(18),
	FLOWER(19),
	WORLD1_GROUND(21),
	WORLD1_DIRT1(22),
	WORLD1_DIRT_HALF(23),	
	WORLD1_DIRT2(37),
	WORLD1_DIRT_SKELETON(38),
	MESSAGE_BOX_NW(86),
	MESSAGE_BOX_N(87),
	MESSAGE_BOX_NE(88),
	MESSAGE_BOX_W(102),
	MESSAGE_BOX_MIDDLE(103),
	MESSAGE_BOX_E(104),
	MESSAGE_BOX_SW(118),
	MESSAGE_BOX_S(119),
	MESSAGE_BOX_SE(120);

	public int id;
	
	JBTile(int id) {
		this.id = id;
	}
	
	public TiledMapTile fromTileSet(TiledMapTileSet set) {
		return set.getTile(id);
	}
	
	public Cell toCell(TiledMapTileSet set) {
		Cell cell = new Cell();
		cell.setTile(fromTileSet(set));
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
