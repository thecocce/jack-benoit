package com.pokware.jb.ai;

import static com.pokware.engine.tiles.JBTile.WORLD1_DIRT1;
import static com.pokware.engine.tiles.JBTile.WORLD1_PLATFORM;
import static com.pokware.engine.tiles.JBTile.WORLD1_PLATFORM_LEFT;
import static com.pokware.engine.tiles.JBTile.WORLD1_PLATFORM_RIGHT;

import java.util.ArrayList;
import java.util.List;
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
	
	private List<Platform>[] generatedPlatforms;
	private List<Ladder> ladderList;
	
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
		this.generatedPlatforms = new ArrayList[horizontalRooms * verticalRooms];
		this.ladderList = new ArrayList<Ladder>();
	}

	private void fillRoom(int offsetX, int offsetY) {
		for (int y = 0; y < roomHeight; y++) {
			for (int x = 0; x < roomWidth; x++) {
				setPlatform(offsetX, offsetY, x, y, JBTile.WORLD1_DIRT1);
			}
		}
	}

	public void createRandomRoom(Room room, boolean platforms) {
//		System.out.println(roomOffsetX + " " + roomOffsetY + " " + floor + " " + ceiling + " " + leftWall + " " + rightWall);

		if (room.ground) {
			generateGround(room.id, room.offsetX, room.offsetY);
		}

		if (platforms) {
			createRandomPlatforms(room.id, room.offsetX, room.offsetY, room.ground);
			createRandomLadders(room.id, room.offsetX, room.offsetY);
		}

		createRoomWalls(room);
		
		createSprites(room.id);

	}

	private void createSprites(int roomIndex) {
		List<Platform> platformList = generatedPlatforms[roomIndex];		
		int zombies = (int)(Math.random()*2);
		for (int i = 0; i < zombies; i++) {
//			Platform platform = platformList.get((int)(Math.random()*platformList.size()));
			
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
				platformLayer.setCell(groundX, groundY, JBTile.WORLD1_GROUND.toCell(tileSet));
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
				for (int x = 1; x < roomWidth-2; x++) {
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
							
							generatedPlatforms[roomIndex].add(new Platform(roomOffsetX+x, roomOffsetY+y, i));
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
			if (!blockAt(platform.x+ladderX, platform.y + 1) && !blockAt(platform.x+ladderX, platform.y - 1)) {  
				setLadder(platform.x+ladderX, platform.y, true);			
				ladderList.add(new Ladder(platform.x+ladderX, platform.y, 1));
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
		for (int y = platformLayer.getHeight() - 2; y > 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				if (blockAt(x, y) && !blockAt(x, y - 1) && !blockAt(x, y + 1)) {	
					boolean blockLeft = blockAt(x-1, y);
					boolean blockRight = blockAt(x+1, y);
					
					if ( (blockLeft && blockRight) || (!blockLeft && !blockRight) ) {
						setPlatform(x, y, WORLD1_PLATFORM);
					}
					else if (blockLeft) {
						setPlatform(x, y, WORLD1_PLATFORM_RIGHT);
					}
					else if (blockRight) {
						setPlatform(x, y, WORLD1_PLATFORM_LEFT);
					}		
				}
			}
		}
		

		for (int y = platformLayer.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				if (blockAt(x, y) && blockAt(x, y-1) && !blockAt(x, y+1)) {
					platformLayer.setCell(x, y, JBTile.WORLD1_GROUND.toCell(tileSet));
				}
			}
		}
		for (int y = platformLayer.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < platformLayer.getWidth(); x++) {
				if (blockAt(x, y) && blockAt(x, y+1) && !blockAt(x, y-1)) {
					platformLayer.setCell(x, y, JBTile.WORLD1_DIRT_HALF.toCell(tileSet));
				}
			}
		}
		
		
		// Grow ladders
		for (Ladder ladder : ladderList) {
			int ladderX = ladder.x, ladderY = ladder.y - 1;
			boolean groundFound = false;
			while(!groundFound && ladderY > 1) {
				Cell cell = platformLayer.getCell(ladderX, ladderY - 1);
				if (cell!=null) {
					groundFound = true;
				}
				else {
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
		map.getTileSets().addTileSet(tileSet);
		map.getProperties().putAll(master.getProperties());

		ProceduralLevelGenerator proceduralArtGenerator = new ProceduralLevelGenerator(platformLayer, ladderLayer, tileSet, levelLayout.roomHeight, levelLayout.roomWidth);
		for(int x=0; x < levelLayout.hRooms; x++) {
			for(int y=0; y < levelLayout.vRooms; y++) {
				Room room = levelLayout.rooms[x][y];
				if (room.filled) {
					proceduralArtGenerator.fillRoom(room.offsetX, room.offsetY);
				} else {
					boolean platforms = (levelLayout.startRoomX != x || levelLayout.startRoomY != y);
					proceduralArtGenerator.createRandomRoom(room, platforms);
				}	
			}	
		}
		

		proceduralArtGenerator.tilingPostProcessing();

		return map;
	}


}