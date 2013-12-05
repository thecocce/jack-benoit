package com.pokware.engine.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

public enum JBTile {

	WORLD1_PLATFORM_LEFT(5),
	WORLD1_PLATFORM(6),
	WORLD1_PLATFORM_RIGHT(7),
	WORLD1_BACK_OUTER_BOTTOM_RIGHT(10),
	WORLD1_BACK_OUTER_BOTTOM(11),
	WORLD1_BACK_OUTER_BOTTOM_LEFT(12),
	WORLD1_BACK_INNER_BOTTOM_RIGHT(13),
	WORLD1_BACK_INNER_BOTTOM_LEFT(14),
	WORLD1_LADDER_TOP(17),
	WORLD1_LADDER(18),
	FLOWER(19),
	WORLD1_GROUND(21),
	WORLD1_DIRT1(22),
	WORLD1_DIRT_HALF(23),
	WORLD1_BACK_PLAIN(24),
	WORLD1_BACK_OUTER_RIGHT(26),
	WORLD1_BACK_EMPTY(27),
	WORLD1_BACK_OUTER_LEFT(28),
	WORLD1_BACK_INNER_TOP_RIGHT(29),
	WORLD1_BACK_INNER_TOP_LEFT(30),
	SPIDER(33),
	ZOMBIE(34),
	JACK(35),
	WORLD1_DIRT2(37),
	WORLD1_DIRT_SKELETON(38),
	EXIT(39),
	WORLD1_BACK_OUTER_TOP_RIGHT(42),
	WORLD1_BACK_OUTER_TOP(43),
	WORLD1_BACK_OUTER_TOP_LEFT(44),
	BIG_BLUE_JEWEL(54),
	BLUE_JEWEL(70);


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
