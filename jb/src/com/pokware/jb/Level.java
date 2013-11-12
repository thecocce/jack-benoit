package com.pokware.jb;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pokware.jb.ai.PathingTool;
import com.pokware.jb.objects.CollisionCategory;
import com.pokware.jb.objects.GameObject;
import com.pokware.jb.objects.GameObjectData;
import com.pokware.jb.objects.LevelObjectManager;

public class Level {

	public static int METERS_PER_TILE = 2;	
	public static int TILE_SIZE_IN_PIXELS = 32;
	public static int METERS_TO_PIXELS_RATIO = TILE_SIZE_IN_PIXELS / METERS_PER_TILE; // pixels per meters
		
	public static int[] PARALLAX_LAYERS = { 0 };
	public static int[] BACKGROUND_LAYERS = { 1, 2 };
	public static int[] SPRITE_LAYERS = { 3 };
	
	public boolean debugMode = false;
	public BitmapFont font;
	public World physicalWorld;	
	public TiledMap tiledMap;
	public TileMapRenderer tileMapRenderer;
	public TileAtlas tileAtlas;	
	public LevelObjectManager objectManager;
	public PathingTool pathingTool;
	
	// Camera stuff
	public LevelCamera camera;
	/*public OrthographicCamera camera;
	public OrthographicCamera parrallaxCamera;
	public OrthoCamController cameraController;*/	
	
	public Vector2 gravityVector = new Vector2(0f, -200f);
		
	public Level(String mapName, float zoom) {
		super();
		
		GameObject.ID_COUNTER = 0;
		
		this.font = new BitmapFont();
		font.setColor(Color.YELLOW);
		
		 
		this.physicalWorld = new World(gravityVector, true);		
		this.objectManager = new LevelObjectManager(this);
	
		int blockWidth = 10;
		int blockHeight = 12;

		String path = "data/output/";
		mapName = "layout_8x2";		
		FileHandle mapHandle = Gdx.files.internal(path + mapName + ".tmx");
		FileHandle baseDir = Gdx.files.internal(path);		
		tiledMap = TiledLoader.createMap(mapHandle);
		
		generateRoomsFor(tiledMap, 1, 4, 2);
				
		tileAtlas = new TileAtlas(tiledMap, baseDir);
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, blockWidth, blockHeight, METERS_PER_TILE, METERS_PER_TILE);		
		
		pathingTool = new PathingTool(tiledMap, tiledMap.layers.get(BACKGROUND_LAYERS[0]), tiledMap.layers.get(BACKGROUND_LAYERS[1]));
		
		createPhysicsWorld();		
		
		objectManager.populateLevel();
		
		camera = new LevelCamera(zoom, tiledMap.width*METERS_PER_TILE, tiledMap.height*METERS_PER_TILE, this);		
	}


	private void generateRoomsFor(TiledMap tiledMap, int world, int hRooms, int vRooms) {
		FileHandle baseRoomDir = Gdx.files.internal("data/output/rooms");
		FileHandle[] roomFileList = baseRoomDir.list(world+".tmx");
		Map<Integer, TiledMap> mapCache = new HashMap<Integer, TiledMap>();
		
		for (int x = 0; x < hRooms; x++) {
			for (int y = 0; y < vRooms; y++) {
				int roomIndex = (int)Math.floor(Math.random()*roomFileList.length);
				TiledMap roomMap = null;
				if (!mapCache.containsKey(roomIndex)) {
					FileHandle mapHandle = roomFileList[roomIndex];
					roomMap = TiledLoader.createMap(mapHandle);
					mapCache.put(roomIndex, roomMap);					
				}
				else {
					roomMap = mapCache.get(roomIndex);					
				}
				
				for (int rx = 0; rx < 20; rx++) {
					for (int ry = 0; ry < 16; ry++) {
						for(int layer = 1; layer <= 3; layer++) {
							int globalTileY = ry+(y*16);
							int globalTileX = rx+(x*20);
							tiledMap.layers.get(layer).tiles[1+globalTileY][1+globalTileX]
									= roomMap.layers.get(layer).tiles[ry][rx];
						}
					}
				}
				
				// Create a hole
				for(int ty = 14; ty < 19; ty++) {
					tiledMap.layers.get(1).tiles[ty][hRooms*20-1]= 0;
					tiledMap.layers.get(1).tiles[ty][hRooms*20-2]= 0;
				}
				
			}
		}
	}



	private void createPhysicsWorld() {
		
		int[][] tiles = tiledMap.layers.get(BACKGROUND_LAYERS[0]).tiles;

		/*
		 * tileX, tileY coords: 
		 * +-----+-----+-----+-----+ 
		 * | 0,2 | 1,2 | 2,2 | 3,2 | tiles[tiles.length-3] 
		 * +-----+-----+-----+-----+ 
		 * | 0,1 | 1,1 | 2,1 | 3,1 | tiles[tiles.length-2]
		 * +-----+-----+-----+-----+ 
		 * | 0,0 | 1,0 | 2,0 | 3,0 | tiles[tiles.length-1] 
		 * +-----+-----+-----+-----+
		 */

		for (int y = tiles.length - 2, tileY = 1; y >= 1; y--, tileY++) {
			int[] row = tiles[y];
			int startRectTileX = -1;
			int startRectTileY = -1;		
			int firstColIndex = 1;			
			int lastColIndex = row.length - 1;			
			for (int tileX = firstColIndex; tileX < lastColIndex; tileX++) {
				int id = row[tileX];
				String tileProperty = tiledMap.getTileProperty(id, "col");
				if ("1".equals(tileProperty)) {
					if (startRectTileX == -1) {
						startRectTileX = tileX;
						startRectTileY = tileY;
					}
					if (tileX == lastColIndex - 1) {
						createRect(startRectTileX, startRectTileY, tileX, tileY);
						startRectTileX = -1;
						startRectTileY = -1;
					}
				}
				else if (startRectTileX != -1) {
					createRect(startRectTileX, startRectTileY, tileX-1, tileY);
					startRectTileX = -1;
					startRectTileY = -1;
				}
			}
		}
		
		// Ground		
		createRect(0, 0, tiles[0].length-1, 0);
		// Ceiling
		createRect(0, tiles.length-1, tiles[0].length-1, tiles.length-1);
		// Left wall
		createRect(0, 0, 0, tiles.length-1);
		// Right wall
		createRect(tiles[0].length-1, 0, tiles[0].length-1, tiles.length-1);
		
		System.out.println("Rectangles created : " + rectNumber);
		
		CollisionManager levelContactManager = new CollisionManager(objectManager);
		physicalWorld.setContactListener(levelContactManager);		
		physicalWorld.setContactFilter(levelContactManager);
	}
	
	private int rectNumber = 0;
	public int viewPortWidthInMeters;
	public int viewPortHeightInMeters;
	private Body cameraBody;
	

	private void createRect(int startTileX, int startTileY, int endTileX, int endTileY) {
		rectNumber++;
				
		int rectWidthInTiles = (endTileX - startTileX) + 1;
		int rectHeightInTiles = (endTileY - startTileY) + 1;

		PolygonShape groundPoly = new PolygonShape();
		float hx = rectWidthInTiles;
		float hy = rectHeightInTiles;
		Vector2 center = new Vector2(startTileX * METERS_PER_TILE + hx, startTileY * METERS_PER_TILE + hy + 0.2f);
		groundPoly.setAsBox(hx, hy-0.2f, center, 0f);
		
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;
		Body body = physicalWorld.createBody(groundBodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundPoly;
		fixtureDef.filter.groupIndex = 0;
		body.createFixture(fixtureDef);

		body.setUserData(new GameObjectData(-1, CollisionCategory.PLATFORM));
		groundPoly.dispose();
	}


	public void step(float deltaTime, int velocityIterations, int positionIterations) {		
		physicalWorld.step(deltaTime, velocityIterations, positionIterations);
	}
	
}
