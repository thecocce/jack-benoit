package com.pokware.jb.procedural;

import static com.pokware.engine.tiles.JBTile.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.pokware.engine.tiles.JBLevelLayout;
import com.pokware.engine.tiles.JBTile;
import com.pokware.engine.tiles.Room;
import com.pokware.engine.tiles.RoomType;
import com.pokware.jb.Constants;

public class ProceduralLevelGenerator {
	private TiledMapTileLayer platformLayer;
	private TiledMapTileLayer ladderLayer;
	private TiledMapTileLayer spriteLayer;
	private TiledMapTileLayer backgroundLayer;
	private TiledMapTileSet tileSet;
	private int roomHeight;
	private int roomWidth;
	private Random rng;
	/** for each room, the relative altitude */
	private int[][] groundAltitudes;

	private List<Platform>[] generatedPlatforms;
	private List<Ladder> ladderList;

	public ProceduralLevelGenerator(TiledMap tiledMap, int roomHeight, int roomWidth) {
		super();
		this.rng = new Random();
		this.platformLayer = (TiledMapTileLayer) tiledMap.getLayers().get(Constants.PLATFORM_LAYER);
		this.ladderLayer = (TiledMapTileLayer) tiledMap.getLayers().get(Constants.LADDER_LAYER);
		this.spriteLayer = (TiledMapTileLayer) tiledMap.getLayers().get(Constants.SPRITE_LAYER);
		this.backgroundLayer = (TiledMapTileLayer) tiledMap.getLayers().get(Constants.PARRALAX_LAYER);
		this.tileSet = tiledMap.getTileSets().getTileSet(0);
		this.roomHeight = roomHeight;
		this.roomWidth = roomWidth;

		System.out.println(String.format("layer width: %d, height: %d", platformLayer.getWidth(), platformLayer.getHeight()));
		int horizontalRooms = (platformLayer.getWidth()) / roomWidth;
		int verticalRooms = (platformLayer.getHeight()) / roomHeight;
		System.out.println(String.format("Number of horizontal rooms: %d, vertical: %d", horizontalRooms, verticalRooms));

		this.groundAltitudes = new int[horizontalRooms * verticalRooms][roomWidth];
		this.generatedPlatforms = new ArrayList[horizontalRooms * verticalRooms];
		this.ladderList = new ArrayList<Ladder>();
	}

	private void fillRoom(int offsetX, int offsetY) {
		for (int y = 0; y < roomHeight; y++) {
			for (int x = 0; x < roomWidth; x++) {
				setPlatform(offsetX, offsetY, x, y, WORLD1_DIRT1);
			}
		}
	}

	public void createRandomRoom(Room room) {

		if (room.ground) {
			generateGround(room.id, room.offsetX, room.offsetY);
		}

		if (room.roomType == RoomType.PROCEDURAL || room.roomType == RoomType.END) {
			createRandomPlatforms(room.id, room.offsetX, room.offsetY, room.ground);
			createRandomLadders(room.id, room.offsetX, room.offsetY);
		}
		
		createRoomWalls(room);

		if (room.roomType == RoomType.START) {
			createStartingPosition(room);
		}
		if (room.roomType == RoomType.END) {
			createExitPosition(room);
		}
		
		createEnvironmentalHazard(room);
		createSprites(room.id);

	}

	private void createStartingPosition(Room room) {
		for (int y = 0; y < roomHeight - 1; y++) {
			for (int x = 1; x < roomWidth - 1; x++) {
				if (blockAt(room.offsetX, room.offsetY, x, y)) {
					if (!blockAt(room.offsetX, room.offsetY, x, y+1)) {
						System.out.println("Found Starting position at " + x + "," + y + " in room " + room.id);
						spriteLayer.setCell(room.offsetX+x, room.offsetY+y+1, JBTile.JACK.toCell(tileSet));
						return;
					}
				}
			}
		}
	}
	private void createExitPosition(Room room) {
		for (int y = 0; y < roomHeight - 1; y++) {
			for (int x = 1; x < roomWidth - 1; x++) {
				if (blockAt(room.offsetX, room.offsetY, x, y)) {
					if (!blockAt(room.offsetX, room.offsetY, x, y+1)) {
						System.out.println("Found Exit position at " + x + "," + y + " in room " + room.id);
						ladderLayer.setCell(room.offsetX+x, room.offsetY+y+1, JBTile.EXIT.toCell(tileSet));
						return;
					}
				}
			}
		}
	}
	private void createEnvironmentalHazard(Room room) {
		for (int y = 0; y < roomHeight - 1; y++) {
			for (int x = 1; x < roomWidth - 1; x++) {
				if (blockAt(room.offsetX, room.offsetY, x, y)) {
					if (!blockAt(room.offsetX, room.offsetY, x, y+1)) {
						if (rng.nextFloat() > 0.95) {
							ladderLayer.setCell(room.offsetX+x, room.offsetY+y+1, JBTile.SPIKE1.toCell(tileSet));
						}
					}
				}
			}
		}		
	}

	private void createSprites(int roomIndex) {
		List<Platform> platformList = generatedPlatforms[roomIndex];		
		if (platformList == null) {
			return;
		}
		int zombies = rng.nextInt(5);
		for (int i = 0; i < zombies; i++) {
			// get a random platform
			
			Platform platform = platformList.get(rng.nextInt(platformList.size()));
			int randomPlacement = rng.nextInt(platform.length);
			
			if (!blockAt(platform.x+randomPlacement, platform.y+1)) {				
				int random = rng.nextInt(3);
				if (random == 0) {
					spriteLayer.setCell(platform.x+randomPlacement, platform.y+1, FLOWER.toCell(tileSet));
				}
				else if (random == 1) {
					ladderLayer.setCell(platform.x+randomPlacement, platform.y, SPIDER.toCell(tileSet));
					spriteLayer.setCell(platform.x+randomPlacement, platform.y, SPIDER.toCell(tileSet));
				}
				else {
					spriteLayer.setCell(platform.x+randomPlacement, platform.y+1, ZOMBIE.toCell(tileSet));
				}				
			}
		}
		
		// Collectables
		for(Platform platform : platformList) {
			if (rng.nextDouble() > 0.5d) {
				for (int jewelIndex = 0; jewelIndex < platform.length; jewelIndex++) {
					if (!blockAt(platform.x+jewelIndex, platform.y+1) && rng.nextDouble() > 0.6) {
						spriteLayer.setCell(platform.x+jewelIndex, platform.y+1, BLUE_JEWEL.toCell(tileSet));
					}
				}
			}
		}
	}

	private void createRoomWalls(Room room) {

		for (int y = roomHeight - 1; y >= 0; y--) {
			if (room.leftWall) {
				setPlatform(room.offsetX, room.offsetY, 0, y, WORLD1_DIRT1);
			}
			if (room.rightWall) {
				setPlatform(room.offsetX, room.offsetY, roomWidth - 1, y, WORLD1_DIRT1);
			}
		}

		if (room.bottomWall) {
			for (int x = 0; x < roomWidth; x++) {
				platformLayer.setCell(room.offsetX + x, room.offsetY, WORLD1_DIRT1.toCell(tileSet));
			}
		}

		if (room.topWall) {
			for (int x = 0; x < roomWidth; x++) {
				platformLayer.setCell(room.offsetX + x, room.offsetY + roomHeight - 1, WORLD1_DIRT1.toCell(tileSet));
			}
		}
	}

	private void generateGround(int roomIndex, int roomOffsetX, int roomOffsetY) {
		int groundAltitude = 0;
		boolean hasHole = false;
		int currentHoleWidth = 0;
		// Generate random terrain
		for (int x = 0; x < roomWidth; x++) {
			int noise1 = -rng.nextInt(4) + 2;

			groundAltitude += noise1;
			if (groundAltitude > 3) {
				groundAltitude = 3;
			}
			if (groundAltitude < 0) {
				groundAltitude = 0;
			}

			// Max hole size == 4
			else if (currentHoleWidth >= 4 && groundAltitude == 0) {
				groundAltitude = 1;
				currentHoleWidth = 0;
			}

			groundAltitudes[roomIndex][x] = groundAltitude;

			if (groundAltitude == 0) { // current hole
				hasHole = true;
				currentHoleWidth++;
			} else if (currentHoleWidth > 0) { // end of hole
				currentHoleWidth = 0;
			}

			if (groundAltitude > 0) {
				// Fill Ground
				for (int i = 0; i < groundAltitude; i++) {
					setPlatform(roomOffsetX, roomOffsetY, x, i);
				}

				int groundX = roomOffsetX + x;
				int groundY = roomOffsetY + groundAltitudes[roomIndex][x];
				platformLayer.setCell(groundX, groundY, WORLD1_GROUND.toCell(tileSet));
			}
		}

		if (!hasHole) { // Create at least one hole
			int randomIndex = 1 + (int) Math.random() * (roomWidth - 2);
			groundAltitudes[roomIndex][randomIndex] = 0;
		}

	}

	private void createRandomPlatforms(int roomIndex, int roomOffsetX, int roomOffsetY, boolean ground) {

		generatedPlatforms[roomIndex] = new ArrayList<Platform>();

		for (int y = ground ? 3 : 0; y < roomHeight - 2; y++) {
			if (Math.random() <= 0.7) {
				for (int x = 1; x < roomWidth - 2; x++) {
					double nextGaussian = rng.nextGaussian() + 1;
					if (nextGaussian < 0) {
						nextGaussian = 0;
					}
					if (nextGaussian > 2) {
						nextGaussian = 2;
					}

					if (Math.random() < 0.2 && !blockAt(roomOffsetX, roomOffsetY, x, y - 2)) {
						int platformLength = (int) (nextGaussian * 4);
						if (x + platformLength > roomWidth) {
							platformLength = roomWidth - x;
						}
						int i = 0;
						if (platformLength > 0) {
							for (i = 0; i < platformLength; i++) {
								setPlatform(roomOffsetX, roomOffsetY, x + i, y);
							}

							generatedPlatforms[roomIndex].add(new Platform(roomOffsetX + x, roomOffsetY + y, i));
						}

						int variableSpaceLength = 1 + (int) rng.nextDouble() * 8;
						x += i + variableSpaceLength;
					}
				}
			}
		}
	}

	private void createRandomLadders(int roomIndex, int roomOffsetX, int roomOffsetY) {

		List<Platform> platformList = generatedPlatforms[roomIndex];
		for (Platform platform : platformList) {

			int ladderX = rng.nextInt(platform.length);
			if (!blockAt(platform.x + ladderX, platform.y + 1) && !blockAt(platform.x + ladderX, platform.y - 1)) {
				setLadder(platform.x + ladderX, platform.y, true);
				ladderList.add(new Ladder(platform.x + ladderX, platform.y, 1));
			}

		}
	}

	@SuppressWarnings("unused")
	private void dumpRoom(int roomIndex, int roomOffsetX, int roomOffsetY) {
		for (int y = roomHeight - 1; y >= 0; y--) {
			System.out.print(String.format("%02d", y));
			for (int x = 0; x < roomWidth; x++) {
				if (blockAt(roomOffsetX, roomOffsetY, x, y)) {
					System.out.print("@");
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
		System.out.print("  ");
		for (int x = 0; x < roomWidth; x++) {
			System.out.print(groundAltitudes[roomIndex][x]);
		}
		System.out.println();
	}

	private boolean topLadderAt(int roomOffsetX, int roomOffsetY, int relativeX, int relativeY) {
		int x = roomOffsetX + relativeX;
		int y = roomOffsetY + relativeY;
		return ladderLayer.getCell(x, y) != null && ladderLayer.getCell(x, y).getTile().getId() == WORLD1_LADDER_TOP.id;
	}

	private boolean blockAt(int roomOffsetX, int roomOffsetY, int relativeX, int relativeY) {
		int x = roomOffsetX + relativeX;
		int y = roomOffsetY + relativeY;
		return blockAt(x, y);
	}

	private boolean blockAt(int x, int y) {
		return platformLayer.getCell(x, y) != null;
	}

	private void setLadder(int x, int y, boolean top) {
		if (x >= 0 && x < platformLayer.getWidth() && y >= 0 && y < platformLayer.getHeight()) {
			ladderLayer.setCell(x, y, top ? WORLD1_LADDER_TOP.toCell(tileSet) : WORLD1_LADDER.toCell(tileSet));
		}
	}

	private void setPlatform(int roomOffsetX, int roomOffsetY, int relativeX, int relativeY) {
		setPlatform(roomOffsetX, roomOffsetY, relativeX, relativeY, WORLD1_DIRT1);
	}

	private void setPlatform(int roomOffsetX, int roomOffsetY, int relativeX, int relativeY, JBTile tile) {
		int x = roomOffsetX + relativeX;
		int y = roomOffsetY + relativeY;
		setPlatform(x, y, tile);

	}

	private void setPlatform(int x, int y, JBTile tile) {
		if (x >= 0 && x < platformLayer.getWidth() && y >= 0 && y < platformLayer.getHeight()) {
			platformLayer.setCell(x, y, tile.toCell(tileSet));
		} else {
			throw new RuntimeException("setPlatform out of bounds" + x + "," + y + " => " + platformLayer.getWidth() + "," + platformLayer.getHeight());
		}
	}

	private void clearTiles(int roomOffsetX, int roomOffsetY, int relativeX, int relativeY) {
		int x = roomOffsetX + relativeX;
		int y = roomOffsetY + relativeY;
		platformLayer.setCell(x, y, null);
		ladderLayer.setCell(x, y, null);
	}

	public void tilingPostProcessing() {
		// Create platform tiles in the layer
		for (int y = platformLayer.getHeight() - 2; y > 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				if (blockAt(x, y) && !blockAt(x, y - 1) && !blockAt(x, y + 1)) {
					boolean blockLeft = blockAt(x - 1, y);
					boolean blockRight = blockAt(x + 1, y);

					if ((blockLeft && blockRight) || (!blockLeft && !blockRight)) {
						setPlatform(x, y, WORLD1_PLATFORM);
					} else if (blockLeft) {
						setPlatform(x, y, WORLD1_PLATFORM_RIGHT);
					} else if (blockRight) {
						setPlatform(x, y, WORLD1_PLATFORM_LEFT);
					}
				}
			}
		}

		for (int y = platformLayer.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				if (blockAt(x, y) && blockAt(x, y - 1) && !blockAt(x, y + 1)) {
					platformLayer.setCell(x, y, WORLD1_GROUND.toCell(tileSet));
				}
			}
		}
		for (int y = platformLayer.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				if (blockAt(x, y) && blockAt(x, y + 1) && !blockAt(x, y - 1)) {
					platformLayer.setCell(x, y, WORLD1_DIRT_HALF.toCell(tileSet));
				}
			}
		}

		// Grow ladders
		for (Ladder ladder : ladderList) {
			int ladderX = ladder.x, ladderY = ladder.y - 1;
			boolean groundFound = false;
			while (!groundFound && ladderY > 1) {
				Cell cell = platformLayer.getCell(ladderX, ladderY - 1);
				if (cell != null) {
					groundFound = true;
				} else {
					setLadder(ladderX, ladderY, false);
				}
				ladderY--;
			}
		}

	}

	public static TiledMap generateMap(TiledMap master, JBLevelLayout levelLayout) {

		TiledMap map = new TiledMap();

		TiledMapTileLayer parrallaxLayer = new TiledMapTileLayer(levelLayout.width, levelLayout.height, 32, 32);
		TiledMapTileLayer platformLayer = new TiledMapTileLayer(levelLayout.width, levelLayout.height, 32, 32);
		TiledMapTileLayer ladderLayer = new TiledMapTileLayer(levelLayout.width, levelLayout.height, 32, 32);
		TiledMapTileLayer spriteLayer = new TiledMapTileLayer(levelLayout.width, levelLayout.height, 32, 32);

		
		map.getLayers().add(parrallaxLayer);
		map.getLayers().add(platformLayer);
		map.getLayers().add(ladderLayer);
		map.getLayers().add(spriteLayer);
		TiledMapTileSet tileSet = master.getTileSets().getTileSet(0);
		Iterator<TiledMapTile> iterator = tileSet.iterator();
		for (TiledMapTile tiledMapTile : tileSet) {
			Object tileId = tiledMapTile.getProperties().get("id");
			if (tileId!=null)
				System.out.println(String.format("%s(%d),",tileId, tiledMapTile.getId()));
		}
		map.getTileSets().addTileSet(tileSet);
		map.getProperties().putAll(master.getProperties());

		ProceduralLevelGenerator proceduralArtGenerator = new ProceduralLevelGenerator(map, levelLayout.roomHeightInTiles, levelLayout.roomWidthInTiles);
		
		proceduralArtGenerator.fillBackground();
		
		for (int x = 0; x < levelLayout.hRooms; x++) {
			for (int y = 0; y < levelLayout.vRooms; y++) {
				Room room = levelLayout.rooms[x][y];
				if (room.roomType == RoomType.FILLED) {
					proceduralArtGenerator.fillRoom(room.offsetX, room.offsetY);
				} else {
					proceduralArtGenerator.createRandomRoom(room);
				}
			}
		}

		proceduralArtGenerator.tilingPostProcessing();

		return map;
	}

	private void fillBackground() {
		for (int x = 0; x < backgroundLayer.getWidth(); x++) {
			for (int y = 0; y <= backgroundLayer.getHeight(); y++) {
				backgroundLayer.setCell(x, y, randomTile());				
			}
		}
		int maxSize = backgroundLayer.getHeight() > backgroundLayer.getWidth() ? backgroundLayer.getHeight() : backgroundLayer.getWidth();		
		Amortized2DNoise noise = new Amortized2DNoise(maxSize);
		noise.generate2DNoise(backgroundLayer, tileSet, JBTile.BACK1_LIGHT1, JBTile.BACK1_LIGHT2, 5, 5, 0, 0);
		
		// Antialias tiles
		for (int x = 1; x < backgroundLayer.getWidth() - 1; x++) {
			for (int y = 1; y < backgroundLayer.getHeight() - 1; y++) {
				Cell cell = backgroundLayer.getCell(x, y);
				
				Cell top = backgroundLayer.getCell(x, y+1);
				Cell bottom = backgroundLayer.getCell(x, y-1);
				Cell left = backgroundLayer.getCell(x-1, y);
				Cell right = backgroundLayer.getCell(x+1, y);
				
				if (!isLight(cell)) {
					if (isLight(top) && isLight(left) && !isLight(right) && !isLight(bottom)) {
						cell.setTile(JBTile.BACK1_NORTH_WEST.fromTileSet(tileSet));
					}
					if (isLight(top) && isLight(right) && !isLight(left) && !isLight(bottom)) {
						cell.setTile(JBTile.BACK1_NORTH_EAST.fromTileSet(tileSet));
					}
					if (isLight(bottom) && isLight(left) && !isLight(right) && !isLight(top)) {
						cell.setTile(JBTile.BACK1_SOUTH_WEST.fromTileSet(tileSet));
					}
					if (isLight(bottom) && isLight(right) && !isLight(left) && !isLight(top)) {
						cell.setTile(JBTile.BACK1_SOUTH_EAST.fromTileSet(tileSet));
					}
				}
			}
		}
	}

	private Cell randomTile() {
		return rng.nextBoolean() ? BACK1_DARK1.toCell(tileSet) : BACK1_DARK2.toCell(tileSet);
	}

	private boolean isLight(Cell cell) {		
		return cell.getTile().getId() == BACK1_LIGHT1.id || cell.getTile().getId() == BACK1_LIGHT2.id;
	}

	
}