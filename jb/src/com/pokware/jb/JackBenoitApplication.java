package com.pokware.jb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.pokware.jb.objects.Jack;

public class JackBenoitApplication extends InputAdapter implements ApplicationListener {

	private boolean preprocess = false;
	private TextureAtlas atlas;
	private SpriteBatch spriteBatch;
	private long lastKeyTime = System.currentTimeMillis();
	private float timer = 0f;
	private Box2DDebugRenderer box2dDebugRenderer;
	private Level level;
	private float zoom;

	public JackBenoitApplication(float zoom) {
		this.zoom = zoom;
	}
	
	public JackBenoitApplication(float zoom, boolean preprocess) {
		this.zoom = zoom;
		this.preprocess = preprocess;
	}
	
	@Override
	public void render() {

		if (Gdx.input.isKeyPressed(Input.Keys.F1) && (System.currentTimeMillis()-lastKeyTime)>100) {
			level.debugMode = !level.debugMode;			
			lastKeyTime=System.currentTimeMillis();
		}
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		timer += deltaTime;
		level.step(deltaTime, 8, 3);
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		level.camera.zoom = zoom;
		level.parrallaxCamera.zoom = zoom;
		level.camera.update();
		level.parrallaxCamera.update();

		// Render map
		level.tileMapRenderer.render(level.parrallaxCamera, Level.PARALLAX_LAYERS);
		level.tileMapRenderer.render(level.camera, Level.BACKGROUND_LAYERS);

		// Render sprites
		spriteBatch.getProjectionMatrix().set(level.camera.combined);
		spriteBatch.begin();
		level.objectManager.draw(spriteBatch, timer);
		spriteBatch.end();

		// Display info
		if (level.debugMode) {
			box2dDebugRenderer.render(level.physicalWorld, level.camera.combined);
			spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			spriteBatch.begin();
			level.font.draw(spriteBatch, getStatusString(), 20, 460);
			spriteBatch.end();
			
			if (level.debugMode) {
				level.objectManager.drawDebugInfo();
			}		
			
		}

		camTrackJack();
	}

	private String getStatusString() {
		Jack jack = level.objectManager.getJack();
		return jack.toString();
	}

	Vector3 jackScreenPosition = new Vector3();
	private void camTrackJack() {
		Jack jack = level.objectManager.getJack();
		Vector2 position = jack.body.getPosition();
		jackScreenPosition.x = position.x;
		jackScreenPosition.y = position.y;
		level.camera.project(jackScreenPosition);
		
		int halfViewPortWidth = level.viewPortWidthInMeters / 2;
		int halfViewPortHeight = level.viewPortHeightInMeters / 2;
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
		if (jackScreenPosition.x < screenWidth/3) {			
			level.camera.position.x-= 0.1f;	
		}		
		if (jackScreenPosition.x > screenWidth - (screenWidth/3)) {
			level.camera.position.x+= 0.1f;
		}
		if (jackScreenPosition.y < screenHeight/4) {
			level.camera.position.y-= 0.1f;
		}
		if (jackScreenPosition.y > screenHeight - (screenHeight/4)) {
			level.camera.position.y+= 0.1f;
		}		
		
		if (level.camera.position.x < halfViewPortWidth * zoom) {
			level.camera.position.x = halfViewPortWidth * zoom;			
		}
		if (level.camera.position.x > level.tiledMap.width*Level.METERS_PER_TILE - halfViewPortWidth * zoom) {
			level.camera.position.x = level.tiledMap.width*Level.METERS_PER_TILE - halfViewPortWidth * zoom;
		}
		if (level.camera.position.y < halfViewPortHeight* zoom) {
			level.camera.position.y = halfViewPortHeight* zoom;
		}
		if (level.camera.position.y > level.tiledMap.height*Level.METERS_PER_TILE - halfViewPortHeight* zoom) {
			level.camera.position.y = level.tiledMap.height*Level.METERS_PER_TILE - halfViewPortHeight* zoom;
		}
				
		level.parrallaxCamera.position.x = level.camera.position.x / 2 + 20;
		level.parrallaxCamera.position.y = level.camera.position.y / 2 + 8;						
	}

	@Override
	public void create() {
		// Load assets in static refs
		loadArt();

		level = new Level("level2", zoom);

		box2dDebugRenderer = new Box2DDebugRenderer();
		box2dDebugRenderer.setDrawJoints(true);
		box2dDebugRenderer.setDrawBodies(true);
		box2dDebugRenderer.setDrawInactiveBodies(true);
	}

	private void loadArt() {
		atlas = new TextureAtlas(Gdx.files.internal("data/output/pack"));
		spriteBatch = new SpriteBatch();
		Art.load(atlas);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
	}
}
