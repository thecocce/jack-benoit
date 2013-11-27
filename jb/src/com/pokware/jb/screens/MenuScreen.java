package com.pokware.jb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.pokware.engine.tiles.JBTile;
import com.pokware.jb.Art;
import com.pokware.jb.Constants;

public class MenuScreen extends AbstractScreen {

	private OrthographicCamera camera;
	private SpriteBatch spriteBatch = new SpriteBatch();
	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private TiledMap tiledMap;

	public MenuScreen() {
		tiledMap = new TmxMapLoader().load("data/output/menuscreen.tmx");

		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f / 32f);

		int viewPortWidthInMeters = (int) ((Gdx.graphics.getWidth() / 32) * Constants.METERS_PER_TILE);
		int viewPortHeightInMeters = (int) ((Gdx.graphics.getHeight() / 32) * Constants.METERS_PER_TILE);

		camera = new OrthographicCamera(viewPortWidthInMeters, viewPortHeightInMeters);
		camera.position.x = viewPortWidthInMeters / 2;
		camera.position.y = viewPortHeightInMeters / 2;
		camera.update();
		tiledMapRenderer.setView(camera);

		generateRandomDecorations();
	}

	private void generateRandomDecorations() {
		TiledMapTileSet tileSet = tiledMap.getTileSets().getTileSet(0);
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		layer.getCell(1, 1).setTile(tileSet.getTile(JBTile.WORLD1_LADDER.id));
	}

	@SuppressWarnings("unused")
	private void dump() {
		// generate Tiles.java
		TiledMapTileSet tileSet = tiledMap.getTileSets().getTileSet(0);
		for (TiledMapTile tiledMapTile : tileSet) {
			if (tiledMapTile.getProperties().get("id") != null) {
				System.out.println(tiledMapTile.getProperties().get("id") + "(" + tiledMapTile.getId() + "),");
			}
		}
	}

	private void createMenuBox(float centerX, float centerY, int width, int height) {
		
		
		
	}

	@Override
	public void render(float delta) {
		tiledMapRenderer.render();

		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();

		spriteBatch.draw(Art.jackBenoitLogo, (Gdx.graphics.getWidth() - Art.jackBenoitLogo.getRegionWidth()) / 2, Gdx.graphics.getHeight() - 150);

		Art.bitmapFont.draw(spriteBatch, String.format("NEW GAME"), 320, Gdx.graphics.getHeight() - 185);
		Art.bitmapFont.draw(spriteBatch, String.format("HI-SCORES"), 310, Gdx.graphics.getHeight() - 250);
		Art.bitmapFont.draw(spriteBatch, String.format("EXIT"), 350, Gdx.graphics.getHeight() - 315);

		spriteBatch.end();
	}

}
