package com.pokware.jb;

import static com.pokware.jb.Constants.BACKGROUND_LAYERS;
import static com.pokware.jb.Constants.METERS_PER_TILE;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pokware.engine.tiles.JBLevelLayout;
import com.pokware.jb.ai.PathingTool;
import com.pokware.jb.ai.ProceduralLevelGenerator;
import com.pokware.jb.objects.CollisionCategory;
import com.pokware.jb.objects.GameObject;
import com.pokware.jb.objects.GameObjectData;
import com.pokware.jb.objects.LevelObjectManager;

public class Level {
	
	public OrthogonalTiledMapRenderer tileMapRenderer;
	public TiledMap tiledMap;

	public boolean debugMode = false;
	public BitmapFont font;
	public World physicalWorld;	
	
	public LevelObjectManager objectManager;
	public PathingTool pathingTool;	
	public LevelCamera camera;	
	public Vector2 gravityVector = new Vector2(0f, -300f);
		
	public Level(String mapName) {
		super();
		
		GameObject.ID_COUNTER = 0;
		
		this.font = new BitmapFont();
		font.setColor(Color.YELLOW);
		
		this.physicalWorld = new World(gravityVector, true);		
		this.objectManager = new LevelObjectManager(this);
		
		JBLevelLayout jbLevelLayout = JBLevelLayout.random(16);
		/*JBLevelLayout jbLevelLayout = new JBLevelLayout(2, 2, 20, 16, 0, 1);
		jbLevelLayout.addRoom(0, 1, true, true, true, false, false);
		jbLevelLayout.addRoom(1, 1, true, false, false, true, false);		
		jbLevelLayout.addRoom(0, 0, true, true, true, false, false);
		jbLevelLayout.addRoom(1, 0, false, true, false, true, false);*/
						
		tiledMap = ProceduralLevelGenerator.generateMap(new TmxMapLoader().load("data/output/layout_16x1.tmx"), jbLevelLayout);
		
		tileMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f/32f);		
		
		pathingTool = new PathingTool((TiledMapTileLayer)tiledMap.getLayers().get(BACKGROUND_LAYERS[0]), 
									  (TiledMapTileLayer)tiledMap.getLayers().get(BACKGROUND_LAYERS[1]));
		
		createPhysicsWorld();		
		
		int initialX = jbLevelLayout.startRoomX*20*2+20;
		int initialY = jbLevelLayout.startRoomY*16*2+20;
		System.out.println("jack start at " + initialX +","+initialY);
		
		objectManager.populateLevel(initialX, initialY);
		
		camera = new LevelCamera(this, initialX+2*10, initialY+2*8);		
	}

	private void generateRoomsFor(TiledMap tiledMap, int world, int hRooms, int vRooms, int margin) {
		FileHandle baseRoomDir = Gdx.files.internal("data/output/rooms");
		FileHandle[] roomFileList = baseRoomDir.list(world+".tmx");
		Map<Integer, TiledMap> mapCache = new HashMap<Integer, TiledMap>();
		
		/*		
		TiledMapTileLayer platformLayer = (TiledMapTileLayer)tiledMap.getLayers().get(Constants.PLATFORM_LAYER);
		TiledMapTileLayer ladderLayer= (TiledMapTileLayer)tiledMap.getLayers().get(Constants.LADDER_LAYER);
		TiledMapTileSet tileSet = tiledMap.getTileSets().getTileSet(0);
		
		
		
		ProceduralArtGenerator proceduralGenerator = new ProceduralArtGenerator(platformLayer, ladderLayer, tileSet, 16, 20);
		int roomIndex = 0;
		for (int x = 0; x < hRooms; x++) {
			for (int y = 0; y < vRooms; y++) {
								
				proceduralGenerator.createRandomPlatforms(roomIndex++, x*20, y*16, false, true, false, false);
				
				int roomIndex = (int)Math.floor(Math.random()*roomFileList.length);
				TiledMap roomMap = null;
				if (!mapCache.containsKey(roomIndex)) {
					FileHandle mapHandle = roomFileList[roomIndex];
					roomMap = new TmxMapLoader().load(mapHandle.path());
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
							TiledMapTileLayer mapLayer = (TiledMapTileLayer)tiledMap.getLayers().get(layer);
							TiledMapTileLayer roomLayer = (TiledMapTileLayer)roomMap.getLayers().get(layer);
							mapLayer.setCell(margin+globalTileX, margin+globalTileY, roomLayer.getCell(rx, ry)); // Copy room cell to map cell									
						}
					}
				}				
			}
		}
		
		proceduralGenerator.tilingPostProcessing();
		*/
	}
	
	



	private void createPhysicsWorld() {
		TiledMapTileLayer platforms = (TiledMapTileLayer)tiledMap.getLayers().get(BACKGROUND_LAYERS[0]);
		TiledMapTileLayer ladders = (TiledMapTileLayer)tiledMap.getLayers().get(BACKGROUND_LAYERS[1]);
		
		/*
		 * tileX, tileY coords: 
		 * +-----+-----+-----+-----+ 
		 * | 0,2 | 1,2 | 2,2 | 3,2 |  
		 * +-----+-----+-----+-----+ 
		 * | 0,1 | 1,1 | 2,1 | 3,1 | 
		 * +-----+-----+-----+-----+ 
		 * | 0,0 | 1,0 | 2,0 | 3,0 |  
		 * +-----+-----+-----+-----+
		 */

		for (int tileY = 0; tileY < platforms.getHeight(); tileY++) {			
			int startRectTileX = -1;
			int startRectTileY = -1;		
			int firstColIndex = 1;			
			int lastColIndex = platforms.getWidth() - 1;			
			for (int tileX = firstColIndex; tileX < lastColIndex; tileX++) {
				Object platformCollision = null;
				if (platforms.getCell(tileX, tileY) != null) {
					platformCollision = platforms.getCell(tileX, tileY).getTile().getProperties().get("col"); 
				}
				Object ladderPresent = null;
				if (ladders.getCell(tileX, tileY) != null) {
					ladderPresent = ladders.getCell(tileX, tileY).getTile().getProperties().get("ladder"); 
				}
				
				if ("1".equals(platformCollision) && !"1".equals(ladderPresent)) {
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
		
		for (int y = platforms.getHeight() - 2, tileY = 1; y >= 1; y--, tileY++) {			
			int firstColIndex = 1;			
			int lastColIndex = platforms.getWidth() - 1;			
			for (int tileX = firstColIndex; tileX < lastColIndex; tileX++) {
				
				Object platformCollision = null;
				if (platforms.getCell(tileX, tileY) != null) {
					platformCollision = platforms.getCell(tileX, tileY).getTile().getProperties().get("col"); 
				}
				Object ladderPresent = null;
				if (ladders.getCell(tileX, tileY) != null) {
					ladderPresent = ladders.getCell(tileX, tileY).getTile().getProperties().get("ladder"); 
				}				
				
				if ("1".equals(platformCollision) && "1".equals(ladderPresent)) {
					createTraversableEdge(tileX, tileY, tileX+1, tileY);
				}				
			}
		}
				
		// Ground		
//		createRect(0, 0, platforms.getWidth()-1, 0);
		// Ceiling
//		createRect(0, platforms.getHeight()-1, platforms.getWidth()-1, platforms.getHeight()-1);
		// Left wall
		createRect(0, 0, 0, platforms.getHeight()-1);
		// Right wall
		createRect(platforms.getWidth()-1, 0, platforms.getWidth()-1, platforms.getHeight()-1);
		
		System.out.println("Rectangles created : " + rectNumber);
		
		CollisionManager levelContactManager = new CollisionManager(objectManager);
		physicalWorld.setContactListener(levelContactManager);		
		physicalWorld.setContactFilter(levelContactManager);
	}
	
	private int rectNumber = 0;
	public int viewPortWidthInMeters;
	public int viewPortHeightInMeters;
	private Body cameraBody;
	public HUD hud = new HUD();
	

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

		body.setUserData(new GameObjectData(-1, CollisionCategory.SOLID_PLATFORM));		
		groundPoly.dispose();
	}

	private void createTraversableEdge(int startTileX, int startTileY, int endTileX, int endTileY) {						
		EdgeShape edge = new EdgeShape();;
		edge.set(startTileX * METERS_PER_TILE, (startTileY+1) * METERS_PER_TILE, endTileX * METERS_PER_TILE, (endTileY+1) * METERS_PER_TILE);
				
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;
		Body body = physicalWorld.createBody(groundBodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = edge;
		fixtureDef.filter.groupIndex = 0;
		body.createFixture(fixtureDef);

		body.setUserData(new GameObjectData(-1, CollisionCategory.TRAVERSABLE_PLATFORM));
		edge.dispose();
	}
	
	public void step(float deltaTime, int velocityIterations, int positionIterations) {		
		physicalWorld.step(deltaTime*0.7f, velocityIterations, positionIterations);
	}
	
}
