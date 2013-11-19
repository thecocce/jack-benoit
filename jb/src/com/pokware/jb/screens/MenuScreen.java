package com.pokware.jb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.pokware.jb.Art;
import com.pokware.jb.Constants;

public class MenuScreen extends AbstractScreen {

	private TileAtlas tileAtlas;
	private TileMapRenderer tileMapRenderer;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch = new SpriteBatch();
	private TiledMap tiledMap;
	
	public MenuScreen() {
		FileHandle mapHandle = Gdx.files.internal("data/output/menuscreen.tmx");
		FileHandle baseDir = Gdx.files.internal("data/output");	
		tiledMap = TiledLoader.createMap(mapHandle);		
		tiledMap = TiledLoader.createMap(mapHandle);
		
		tileAtlas = new TileAtlas(tiledMap, baseDir);
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 10, 12, Constants.METERS_PER_TILE, Constants.METERS_PER_TILE);
		int viewPortWidthInMeters = (int) ((Gdx.graphics.getWidth() / 32) * Constants.METERS_PER_TILE);
		int viewPortHeightInMeters = (int) ((Gdx.graphics.getHeight() / 32) * Constants.METERS_PER_TILE);
		//camera = new OrthographicCamera(viewPortWidthInMeters, viewPortHeightInMeters);
		camera = new OrthographicCamera(viewPortWidthInMeters, viewPortHeightInMeters);
	
		camera.position.x = viewPortWidthInMeters/2;
		camera.position.y = viewPortHeightInMeters/2;
		camera.update();
		
		createMenuBox();
	}
	
	private void createMenuBox() {		
	}

	@Override
	public void render() {
		tileMapRenderer.render(camera);
						
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();
		
		spriteBatch.draw(Art.jackBenoitLogo, (Gdx.graphics.getWidth()-Art.jackBenoitLogo.getRegionWidth())/2, Gdx.graphics.getHeight()-150);
		
		Art.bitmapFont.draw(spriteBatch, String.format("NEW GAME"), 320, Gdx.graphics.getHeight()-185);
		Art.bitmapFont.draw(spriteBatch, String.format("HI-SCORES"), 310, Gdx.graphics.getHeight()-250);
		Art.bitmapFont.draw(spriteBatch, String.format("EXIT"), 350, Gdx.graphics.getHeight()-315);
		
		spriteBatch.end();
	}

}
