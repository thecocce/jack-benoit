package com.pokware.jb.ai;

import static com.pokware.engine.tiles.JBTile.WORLD1_DIRT1;
import static com.pokware.engine.tiles.JBTile.WORLD1_PLATFORM;
import static com.pokware.engine.tiles.JBTile.WORLD1_PLATFORM_LEFT;
import static com.pokware.engine.tiles.JBTile.WORLD1_PLATFORM_RIGHT;

import java.util.Random;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.pokware.engine.tiles.JBLevelLayout;
import com.pokware.engine.tiles.JBTile;
import com.pokware.engine.tiles.Room;

public class ProceduralLevelGenerator {
	private TiledMapTileLayer platformLayer;
	private TiledMapTileLayer ladderLayer;
	private TiledMapTileSet tileSet;
	private int roomHeight;
	private int roomWidth;
	private Random rng;
	/** for each room, the relative altitude */
	private int[][] groundAltitudes;
	
	private Platform[] generatedPlatforms = new Platform[2000];
	private int generatedPlatformsNumber;

	public ProceduralLevelGenerator(TiledMapTileLayer platformLayer, TiledMapTileLayer ladderLayer, TiledMapTileSet tileSet, int roomHeight, int roomWidth) {
		super();
		this.rng = new Random();
		this.platformLayer = platformLayer;
		this.ladderLayer = ladderLayer;
		this.tileSet = tileSet;
		this.roomHeight = roomHeight;
		this.roomWidth = roomWidth;

		System.out.println(String.format("layer width: %d, height: %d", platformLayer.getWidth(), platformLayer.getHeight()));
		int horizontalRooms = (platformLayer.getWidth()) / roomWidth;
		int verticalRooms = (platformLayer.getHeight()) / roomHeight;
		System.out.println(String.format("Number of horizontal rooms: %d, vertical: %d", horizontalRooms, verticalRooms));
		this.groundAltitudes = new int[horizontalRooms * verticalRooms][roomWidth];
	}

	private void fillRoom(int id, int offsetX, int offsetY) {
		for (int y = 0; y < roomHeight; y++) {
			for (int x = 0; x < roomWidth; x++) {
				setPlatform(offsetX, offsetY, x, y, JBTile.WORLD1_DIRT1);
			}
		}
	}

	public void createRandomRoom(int roomIndex, int roomOffsetX, int roomOffsetY, boolean floor, boolean ceiling, boolean leftWall, boolean rightWall, boolean ground, boolean platforms) {
		System.out.println(roomIndex + " " + roomOffsetX + " " + roomOffsetY + " " + floor + " " + ceiling + " " + leftWall + " " + rightWall);

		if (ground) {
			generateGround(roomIndex, roomOffsetX, roomOffsetY);
		}

		if (platforms) {
			createRandomPlatforms(roomOffsetX, roomOffsetY, ground);
			createRandomLadders(roomOffsetX, roomOffsetY);
		}

		createRoomWalls(roomOffsetX, roomOffsetY, floor, ceiling, leftWall, rightWall);

	}

	private void createRoomWalls(int roomOffsetX, int roomOffsetY, boolean floor, boolean ceiling, boolean leftWall, boolean rightWall) {				
		for (int y = roomHeight - 1; y >= 0; y--) {
			if (leftWall) {
				setPlatform(roomOffsetX, roomOffsetY, 0, y, WORLD1_DIRT1);				
			}
			if (rightWall) {
				setPlatform(roomOffsetX, roomOffsetY, roomWidth - 1, y, WORLD1_DIRT1);
			}			
			if ((y == 0 && floor) || (y == roomHeight - 1 && ceiling)) {
				for (int x = 0; x < platformLayer.getWidth(); x++)
					platformLayer.setCell(roomOffsetX + x, roomOffsetY + y, WORLD1_DIRT1.toCell(tileSet));
			}
		}
	}

	private void createRandomLadders(int roomOffsetX, int roomOffsetY) {
				
		for (int i = 0; i < generatedPlatformsNumber; i++) {
			Platform platform = generatedPlatforms[i];			
			
			double dice = rng.nextDouble();
			int ladderNumber = 0;
			if (dice >= 0.2 && dice < 0.8) {
				// 1 ladder
				ladderNumber = 1;
			}
			else {
				ladderNumber = 2;
			}
						
			for (int ladder = 0; ladder < ladderNumber; ladder++) {
				int ladderX = rng.nextInt(platform.length);				
				setLadder(platform.x+ladderX, platform.y, true);
				int ladderLength = (int) (rng.nextDouble() * 5);
				for (int k = 0; k < ladderLength; k++) {
					if (platform.y-k < 1) {
						break;
					}
					setLadder(platform.x+ladderX, platform.y-k, blockAt(platform.x+ladderX, platform.y-k));
				}				
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
				platformLayer.setCell(groundX, groundY, JBTile.WORLD1_GROUND.toCell(tileSet));
			}			
		}

		if (!hasHole) { // Create at least one hole
			int randomIndex = 1 + (int) Math.random() * (roomWidth - 2);
			groundAltitudes[roomIndex][randomIndex] = 0;
		}

	}

	private void createRandomPlatforms(int roomOffsetX, int roomOffsetY, boolean ground) {
		generatedPlatformsNumber = 0;
		for (int y = ground ? 3 : 0; y < roomHeight - 2; y++) {
			if (Math.random() <= 0.7) {
				for (int x = 0; x < roomWidth; x++) {
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
						for (i = 0; i < platformLength; i++) {							
							setPlatform(roomOffsetX, roomOffsetY, x + i, y);
						}
						
						generatedPlatforms[generatedPlatformsNumber++] = new Platform(roomOffsetX+x, roomOffsetY+y, i+1);
						
						int variableSpaceLength = 1 + (int) rng.nextDouble() * 8;
						x += i + variableSpaceLength;
					}
				}
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
		return ladderLayer.getCell(x, y) != null && ladderLayer.getCell(x, y).getTile().getId() == JBTile.WORLD1_LADDER_TOP.id;
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
			ladderLayer.setCell(x, y, top ? JBTile.WORLD1_LADDER_TOP.toCell(tileSet) : JBTile.WORLD1_LADDER.toCell(tileSet));
		}
	}

	private void setPlatform(int roomOffsetX, int roomOffsetY, int relativeX, int relativeY) {
		setPlatform(roomOffsetX, roomOffsetY, relativeX, relativeY, JBTile.WORLD1_DIRT1);
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
		boolean platformStarted = false;
		for (int y = platformLayer.getHeight() - 2; y > 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				if (blockAt(x, y) && !blockAt(x, y - 1) && !blockAt(x, y + 1)) {
					if (!platformStarted) {
						platformStarted = true;
						setPlatform(x, y, WORLD1_PLATFORM_LEFT);
					} else {
						setPlatform(x, y, WORLD1_PLATFORM);
					}
				} else {
					if (platformStarted) {
						platformStarted = false;
						setPlatform(x - 1 >= 0 ? x - 1 : 0, y, WORLD1_PLATFORM_RIGHT);
					}
				}
			}
		}

		for (int y = platformLayer.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				Cell top = platformLayer.getCell(x, y);
				Cell bottom = platformLayer.getCell(x, y - 1);
				if (top != null && bottom != null && JBTile.fromCell(top).isPlatformTile() && JBTile.fromCell(bottom).isPlatformTile()) {
					top.setTile(tileSet.getTile(JBTile.WORLD1_GROUND.id));
					bottom.setTile(tileSet.getTile(JBTile.WORLD1_DIRT_HALF.id));
				}
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
		map.getTileSets().addTileSet(tileSet);
		map.getProperties().putAll(master.getProperties());

		ProceduralLevelGenerator proceduralArtGenerator = new ProceduralLevelGenerator(platformLayer, ladderLayer, tileSet, levelLayout.roomHeight, levelLayout.roomWidth);
		for(int x=0; x < levelLayout.hRooms; x++) {
			for(int y=0; y < levelLayout.vRooms; y++) {
				Room room = levelLayout.rooms[x][y];
				if (room.filled) {
					proceduralArtGenerator.fillRoom(room.id, room.offsetX, room.offsetY);
				} else {
					boolean platforms = (levelLayout.startRoomX != x || levelLayout.startRoomY != y);
					proceduralArtGenerator.createRandomRoom(room.id, room.offsetX, room.offsetY, room.bottomWall, room.topWall, room.leftWall, room.rightWall, room.ground, platforms);
				}	
			}	
		}
		

		proceduralArtGenerator.tilingPostProcessing();

		return map;
	}

}