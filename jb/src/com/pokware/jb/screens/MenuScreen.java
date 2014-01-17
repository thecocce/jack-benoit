package com.pokware.jb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pokware.jb.Art;
import com.pokware.jb.Constants;
import com.pokware.jb.objects.Jack;

public class MenuScreen extends AbstractScreen {

	private OrthographicCamera camera;
	private SpriteBatch spriteBatch = new SpriteBatch();
//	private OrthogonalTiledMapRenderer tiledMapRenderer;
//	private TiledMap tiledMap;
	private Stage stage = new Stage();
	private Skin skin = new Skin();
	
	public MenuScreen() {
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));
		skin.add("default", Art.bitmapFont);
		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);		
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);
		Table table = new Table();
		table.padTop(100);
		table.setFillParent(true);
		stage.addActor(table);
	
		TextButton button = new TextButton("NEW GAME!", skin);
		button.addListener(new ChangeListener() {			
			@Override
			public void changed (ChangeEvent event, Actor actor) {
//				Art.startSound.play();
				fadeOut();
			}
		});
		table.add(button).pad(20);
		
		table.row();
		TextButton button2 = new TextButton("HI-SCORES", skin);
		table.add(button2).pad(20);
		
		table.row();
		TextButton button3 = new TextButton("EXIT", skin);
		button3.addListener(new ChangeListener() {			
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		
		table.add(button3).pad(20);
		table.layout();
		
//		tiledMap = new TmxMapLoader().load("data/output/menuscreen.tmx");

//		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f / 32f);

		int viewPortWidthInMeters = (int) ((Gdx.graphics.getWidth() / 32) * Constants.METERS_PER_TILE);
		int viewPortHeightInMeters = (int) ((Gdx.graphics.getHeight() / 32) * Constants.METERS_PER_TILE);

		camera = new OrthographicCamera(viewPortWidthInMeters, viewPortHeightInMeters);
		camera.position.x = viewPortWidthInMeters / 2;
		camera.position.y = viewPortHeightInMeters / 2;
		camera.update();
//		tiledMapRenderer.setView(camera);

//		generateRandomDecorations();
		
		Gdx.input.setInputProcessor(stage);
		
		fadeIn();
	}
	
	@Override
	protected void onFadeOutTermination() {
		Jack.life = 3;
		MenuScreen.this.transitionTo(new LevelScreen());
	}

	private void generateRandomDecorations() {
//		TiledMapTileSet tileSet = tiledMap.getTileSets().getTileSet(0);
//		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
//		layer.getCell(1, 1).setTile(tileSet.getTile(JBTile.WORLD1_LADDER.id));
	}

	@SuppressWarnings("unused")
	private void dump() {
		// generate Tiles.java
//		TiledMapTileSet tileSet = tiledMap.getTileSets().getTileSet(0);
//		for (TiledMapTile tiledMapTile : tileSet) {
//			if (tiledMapTile.getProperties().get("id") != null) {
//				System.out.println(tiledMapTile.getProperties().get("id") + "(" + tiledMapTile.getId() + "),");
//			}
//		}
	}

	@Override
	public void render(float delta) {
//		tiledMapRenderer.render();

		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();

		spriteBatch.draw(Art.jackBenoitLogo, (Gdx.graphics.getWidth() - Art.jackBenoitLogo.getRegionWidth()) / 2, Gdx.graphics.getHeight() - 150);

		/*Art.bitmapFont.draw(spriteBatch, String.format("NEW GAME"), 320, Gdx.graphics.getHeight() - 185);
		Art.bitmapFont.draw(spriteBatch, String.format("HI-SCORES"), 310, Gdx.graphics.getHeight() - 250);
		Art.bitmapFont.draw(spriteBatch, String.format("EXIT"), 350, Gdx.graphics.getHeight() - 315);*/

		spriteBatch.end();
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		
		super.renderCurtain();
	}

}
