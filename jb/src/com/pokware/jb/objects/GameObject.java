package com.pokware.jb.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.pokware.jb.Level;
import com.pokware.jb.ai.PathNode;

public abstract class GameObject {
	
	public static final int NO_LADDER = 0;
	public static final int LADDER = 1;
	public static final int LADDER_BELOW = 2;
	public static final int NO_CLIFF = 0;
	public static int ID_COUNTER = 0;
	
	public static int MAX_NODE_LIST_SIZE = 1000;
	public static Vector2 FORCE_APPLICATION_POINT = new Vector2(32, 32);
	
	public int id = ID_COUNTER++;
	public Body body;
	public Fixture fixture;	
	public Level level;
	public float width = 2f;
	public float height = 2f;
	public GameObjectData userData;
	
	public GameObject(Level level, float x, float y, CollisionCategory category, boolean bullet) {				
		init(level, x, y, 32, 32, category, 0.2f, 0.5f, bullet);
	}

	public GameObject(Level level, float x, float y, int pixelWidth, int pixelHeight, CollisionCategory category, boolean bullet) {
		init(level, x, y, pixelWidth, pixelHeight, category, 0.2f, 0.5f, bullet);
	}

	public GameObject(Level level, float x, float y, int pixelWidth, int pixelHeight, CollisionCategory category, float widthRatio, float heightRatio, boolean bullet) {
		init(level, x, y, pixelWidth, pixelHeight, category, widthRatio, heightRatio, bullet);
	}
	
	private void init(Level level, float x, float y, int pixelWidth, int pixelHeight, CollisionCategory category, float widthRatio, float heightRatio, boolean bullet) {
		this.width = Level.METERS_PER_TILE * pixelWidth / 32f;
		this.height = Level.METERS_PER_TILE * pixelHeight / 32f;
					
		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(width*widthRatio, height*heightRatio);
		
		BodyDef bodyDef = getDefaultBodyDef(x, y);
		body = level.physicalWorld.createBody(bodyDef);
		userData = new GameObjectData(id, category);
		
		body.setUserData(userData);
		
		body.setBullet(bullet);		
		fixture = body.createFixture(polyShape, 5);					
		fixture.setFriction(0f);
		
		polyShape.dispose();
		
		this.level=level;
	}

	private BodyDef getDefaultBodyDef(float x, float y) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.x = x;
		bodyDef.position.y = y;
		bodyDef.gravityScale = 1.0f;	
		bodyDef.fixedRotation = true;
		bodyDef.linearDamping = 10f;
		
		return bodyDef;
	}

	public abstract TextureRegion getTextureRegion(float tick);

	public void renderDebugInfo() {
	}
	
	public void render(SpriteBatch spriteBatch, float tick) {
		GameObjectData userData = (GameObjectData)body.getUserData();
		if (userData!= null) {
			if (!userData.hidden) {			
				TextureRegion textureRegion = getTextureRegion(tick);
				Vector2 position = body.getPosition();
				spriteBatch.draw(textureRegion, position.x-(width/2), position.y-(height/2), width, height);
			}
			else {
				level.physicalWorld.destroyBody(body);
			}
		}
	}

	/**
	 * 0 : no ladder
	 * 1 : ladder + no ladder below
	 * 2 : no ladder + ladder below
	 * 3 : ladder + ladder below 
	 * 
	 * @param camera
	 * @return
	 */
	public int getLadderStatus() {
		Vector2 position = body.getPosition();		
		float y = position.y-1;
		
		Vector2 currentTile = getTile();
		int tileX = (int)currentTile.x;		
		int tileY = (int)currentTile.y;
		
		int[][] tiles = level.tiledMap.layers.get(2).tiles;
		int ladderStatus = 0;
		if (tileY >= 0 && tileY < tiles.length && tileX >= 0 && tileX < tiles[0].length) { 						
			if ("1".equals(level.tiledMap.getTileProperty(tiles[tileY][tileX], "ladder"))) {
				ladderStatus+=LADDER;
			}
		}
		
		y = position.y-1.2f;
		tileY = (int)((level.tiledMap.height*2-y)/Level.METERS_PER_TILE);
		
		if (tileY < tiles.length) { 						
			if ("1".equals(level.tiledMap.getTileProperty(tiles[tileY][tileX], "ladder"))) {
				ladderStatus+=LADDER_BELOW;
			}
		}
		return ladderStatus;
	}
	
	public boolean isTopOfTheLadder() {
		Vector2 position = body.getPosition();						
		Vector2 currentTile = getTile();
		int tileX = (int)currentTile.x;		
		int tileY = (int)currentTile.y - 1;		
		int[][] tiles = level.tiledMap.layers.get(2).tiles;						
		if (tileY < tiles.length) { 						
			if ("1".equals(level.tiledMap.getTileProperty(tiles[tileY][tileX], "ladder"))) {				
				return false;
			}
		}
		System.out.println("top ladder");
		return true;
	}
	
	private Vector2 getTileVector = new Vector2();
	public Vector2 getTile() {
		Vector2 position = body.getPosition();
		int tileX = (int) (position.x/Level.METERS_PER_TILE);		
		int tileY = (int)((level.tiledMap.height*2-position.y)/Level.METERS_PER_TILE);		
		return getTileVector.set(tileX, tileY);		
	}
	
	
	
	public Vector2 findWayPointTile(ArrayList<PathNode> wayPointContainer, Vector2 targetPosition) {					
		return level.pathingTool.findWayPointTile(wayPointContainer, getTile(), targetPosition);
	}
	
	public Vector2 findRandomWayPointTile(ArrayList<PathNode> wayPointContainer, int steps) {					
		Vector2 wayPointTile = level.pathingTool.findRandomWayPointTile(wayPointContainer, getTile(), steps);		
		return wayPointTile;
		
	}
	
	@Override
	public String toString() {	
		return "[" + String.valueOf(id) + "] " +userData.toString();
	}
}
