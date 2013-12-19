package com.pokware.jb;

 import static com.pokware.jb.Constants.BACKGROUND_LAYERS;
import static com.pokware.jb.Constants.METERS_PER_TILE;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
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
import com.pokware.jb.objects.CollisionCategory;
import com.pokware.jb.objects.GameObjectData;
import com.pokware.jb.objects.LevelObjectManager;
import com.pokware.jb.procedural.ProceduralLevelGenerator;
import com.pokware.jb.screens.LevelScreen;

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
	public LevelScreen screen;

	public Level(LevelScreen levelScreen) {
		super();
		this.screen = levelScreen;

		this.font = new BitmapFont();
		font.setColor(Color.YELLOW);

		this.physicalWorld = new World(gravityVector, true);
		this.objectManager = new LevelObjectManager(this);

		JBLevelLayout jbLevelLayout = JBLevelLayout.random(14);
		
		Parameters params = new Parameters();
		params.textureMagFilter = TextureFilter.Nearest;
		params.textureMinFilter = TextureFilter.Nearest;
		TiledMap masterMap = new TmxMapLoader().load("data/output/layout_16x1.tmx", params);		
		
		
		tiledMap = ProceduralLevelGenerator.generateMap(masterMap, jbLevelLayout, 1);

		tileMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f / 32f);

		pathingTool = new PathingTool((TiledMapTileLayer) tiledMap.getLayers().get(BACKGROUND_LAYERS[0]), (TiledMapTileLayer) tiledMap.getLayers().get(BACKGROUND_LAYERS[1]));

		createPhysicsWorld();

		objectManager.populateLevel();

		Vector2 position = objectManager.getJack().body.getPosition();
		camera = new LevelCamera(this, position.x, position.y);
	}

	public void reset() {
		screen.reset();
	}
	
	private void createPhysicsWorld() {
		TiledMapTileLayer platforms = (TiledMapTileLayer) tiledMap.getLayers().get(BACKGROUND_LAYERS[0]);
		TiledMapTileLayer ladders = (TiledMapTileLayer) tiledMap.getLayers().get(BACKGROUND_LAYERS[1]);

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
		
		// Platforms

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

				if (Constants.SOLID_ZONE.equals(platformCollision) && !Constants.LADDER_ZONE.equals(ladderPresent)) {
					if (startRectTileX == -1) {
						startRectTileX = tileX;
						startRectTileY = tileY;
					}
					if (tileX == lastColIndex - 1) {
						createRect(startRectTileX, startRectTileY, tileX, tileY);
						startRectTileX = -1;
						startRectTileY = -1;
					}
				} else if (startRectTileX != -1) {
					createRect(startRectTileX, startRectTileY, tileX - 1, tileY);
					startRectTileX = -1;
					startRectTileY = -1;
				}
			}
		}

		// Create traversable top ladders 

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
					createTraversableEdge(tileX, tileY, tileX + 1, tileY);
				}
			}
		}

		// Ground
		// createRect(0, 0, platforms.getWidth()-1, 0);
		// Ceiling
		// createRect(0, platforms.getHeight()-1, platforms.getWidth()-1, platforms.getHeight()-1);
		// Left wall
		createRect(0, 0, 0, platforms.getHeight() - 1);
		// Right wall
		createRect(platforms.getWidth() - 1, 0, platforms.getWidth() - 1, platforms.getHeight() - 1);

		System.out.println("Rectangles created : " + rectNumber);

		CollisionManager levelContactManager = new CollisionManager(objectManager);
		physicalWorld.setContactListener(levelContactManager);
		physicalWorld.setContactFilter(levelContactManager);
	}

	private int rectNumber = 0;
	public int viewPortWidthInMeters;
	public int viewPortHeightInMeters;

	public HUD hud = new HUD();

	
	private void createRect(int startTileX, int startTileY, int endTileX, int endTileY) {
		rectNumber++;

		int rectWidthInTiles = (endTileX - startTileX) + 1;
		int rectHeightInTiles = (endTileY - startTileY) + 1;

		PolygonShape groundPoly = new PolygonShape();
		float hx = rectWidthInTiles;
		float hy = rectHeightInTiles;
		Vector2 center = new Vector2(startTileX * METERS_PER_TILE + hx, startTileY * METERS_PER_TILE + hy + 0.2f);
		groundPoly.setAsBox(hx, hy - 0.2f, center, 0f);	

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;
		Body body = physicalWorld.createBody(groundBodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundPoly;
		fixtureDef.friction = 0f;		
		fixtureDef.filter.groupIndex = 0;
		body.createFixture(fixtureDef);

		body.setUserData(new GameObjectData(-1, CollisionCategory.SOLID_PLATFORM));
		groundPoly.dispose();
	}

	private void createTraversableEdge(int startTileX, int startTileY, int endTileX, int endTileY) {
		EdgeShape edge = new EdgeShape();
		;
		edge.set(startTileX * METERS_PER_TILE, (startTileY + 1) * METERS_PER_TILE, endTileX * METERS_PER_TILE, (endTileY + 1) * METERS_PER_TILE);

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
		physicalWorld.step(deltaTime, velocityIterations, positionIterations);
	}

	public void onCompletion() {
		screen.transitionTo(new LevelScreen());
	}

}
