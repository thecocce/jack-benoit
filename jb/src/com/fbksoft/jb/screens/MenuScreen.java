package com.fbksoft.jb.screens;

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
import com.fbksoft.engine.ActionResolver;
import com.fbksoft.jb.Art;
import com.fbksoft.jb.Constants;
import com.fbksoft.jb.objects.Jack;

public class MenuScreen extends AbstractScreen {

	private OrthographicCamera camera;
	private SpriteBatch spriteBatch = new SpriteBatch();
	private Stage stage = new Stage();
	private Skin skin = new Skin();
	
	public MenuScreen() {
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));
		skin.add("default", Art.bitmapFont);
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
				if (Gdx.app instanceof ActionResolver) {
					ActionResolver app = ((ActionResolver)(Gdx.app));
					if (!app.getSignedInGPGS()) {
						app.loginGPGS();
					}
				}
				fadeOut();
			}
		});
		table.add(button).pad(20);
		
		table.row();
		TextButton button2 = new TextButton("HI-SCORES", skin);
		button2.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (Gdx.app instanceof ActionResolver) {
				ActionResolver app = ((ActionResolver)(Gdx.app));
				if (app.getSignedInGPGS()) app.getLeaderboardGPGS();
				else app.loginGPGS();
				}
			}
			
		});
		table.add(button2).pad(20);
		
		table.row();
		TextButton button4 = new TextButton("ACHIEVEMENTS", skin);
		button4.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (Gdx.app instanceof ActionResolver) {
				ActionResolver app = ((ActionResolver)(Gdx.app));
				if (app.getSignedInGPGS()) app.getAchievementsGPGS();
				else app.loginGPGS();
				}
			}
			
		});
		table.add(button4).pad(20);
		
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

		int viewPortWidthInMeters = (int) ((Gdx.graphics.getWidth() / 32) * Constants.METERS_PER_TILE);
		int viewPortHeightInMeters = (int) ((Gdx.graphics.getHeight() / 32) * Constants.METERS_PER_TILE);

		camera = new OrthographicCamera(viewPortWidthInMeters, viewPortHeightInMeters);
		camera.position.x = viewPortWidthInMeters / 2;
		camera.position.y = viewPortHeightInMeters / 2;
		camera.update();
		
		Gdx.input.setInputProcessor(stage);
		
		fadeIn();
	}
	
	@Override
	protected void onFadeOutTermination() {
		Jack.life = 3;
		MenuScreen.this.transitionTo(new LevelScreen(1));
	}

	@Override
	public void render(float delta) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();

		spriteBatch.draw(Art.jackBenoitLogo, (Gdx.graphics.getWidth() - Art.jackBenoitLogo.getRegionWidth()) / 2, Gdx.graphics.getHeight() - 150);
		spriteBatch.end();
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		
		super.renderCurtain();
	}

}
