package com.pokware.jb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pokware.jb.objects.Jack;

public class LevelCamera {

	public OrthographicCamera front;
	public OrthographicCamera parrallax;
	
	private int viewPortWidthInMeters;
	private int viewPortHeightInMeters;
	private float zoom;
	private int levelHeightInMeters;
	private int levelWidthInMeters;
	public Body cameraBody;
	
	public LevelCamera(float zoom, int levelWidthInMeters, int levelHeightInMeters, Level level) {
		this.viewPortWidthInMeters = (int) ((Gdx.graphics.getWidth() / 32) * Level.METERS_PER_TILE);
		this.viewPortHeightInMeters = (int) ((Gdx.graphics.getHeight() / 32) * Level.METERS_PER_TILE);
		this.levelWidthInMeters = levelWidthInMeters;
		this.levelHeightInMeters =levelHeightInMeters;
		this.zoom = zoom;
		this.front = new OrthographicCamera(viewPortWidthInMeters, viewPortHeightInMeters);
		this.parrallax = new OrthographicCamera(viewPortWidthInMeters, viewPortHeightInMeters);
		
		this.front.zoom = zoom;
		this.parrallax.zoom = zoom;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.x = front.position.x;
		bodyDef.position.y = front.position.y;				
		cameraBody = level.physicalWorld.createBody(bodyDef);		
	}

	public void update(Jack jack) {
		front.update();
		parrallax.update();		
		camTrackJack(jack);
	}

	Vector3 jackScreenPosition = new Vector3();
	
	private void camTrackJack(Jack jack) {
		Vector2 position = jack.body.getPosition();
		jackScreenPosition.x = position.x;
		jackScreenPosition.y = position.y;
		front.project(jackScreenPosition);
		
		int halfViewPortWidth = viewPortWidthInMeters / 2;
		int halfViewPortHeight = viewPortHeightInMeters / 2;
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
		Vector2 camVector = position.sub(front.position.x, front.position.y);
		
		cameraBody.setLinearVelocity(camVector);
		
		front.position.x = cameraBody.getPosition().x;
		front.position.y = cameraBody.getPosition().y;
				
		/*if (front.position.x < halfViewPortWidth * zoom) {
			front.position.x = halfViewPortWidth * zoom;			
		}
		if (front.position.x > levelWidthInMeters - halfViewPortWidth * zoom) {
			front.position.x = levelWidthInMeters - halfViewPortWidth * zoom;
		}
		if (front.position.y < halfViewPortHeight* zoom) {
			front.position.y = halfViewPortHeight* zoom;
		}
		if (front.position.y > levelHeightInMeters - halfViewPortHeight* zoom) {
			front.position.y = levelHeightInMeters - halfViewPortHeight* zoom;
		}*/
				
		parrallax.position.x = front.position.x / 2 + 20;
		parrallax.position.y = front.position.y / 2 + 8;						
	}
	
	/*private void camTrackJack(Jack jack) {
		Vector2 position = jack.body.getPosition();
		jackScreenPosition.x = position.x;
		jackScreenPosition.y = position.y;
		front.project(jackScreenPosition);
		
		int halfViewPortWidth = viewPortWidthInMeters / 2;
		int halfViewPortHeight = viewPortHeightInMeters / 2;
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
		if (jackScreenPosition.x < screenWidth/3) {			
			front.position.x-= 0.2f;	
		}		
		if (jackScreenPosition.x > screenWidth - (screenWidth/3)) {
			front.position.x+= 0.2f;
		}
		if (jackScreenPosition.y < screenHeight/4) {
			front.position.y-= 0.2f;
		}
		if (jackScreenPosition.y > screenHeight - (screenHeight/4)) {
			front.position.y+= 0.2f;
		}		
		
		if (front.position.x < halfViewPortWidth * zoom) {
			front.position.x = halfViewPortWidth * zoom;			
		}
		if (front.position.x > levelWidthInMeters - halfViewPortWidth * zoom) {
			front.position.x = levelWidthInMeters - halfViewPortWidth * zoom;
		}
		if (front.position.y < halfViewPortHeight* zoom) {
			front.position.y = halfViewPortHeight* zoom;
		}
		if (front.position.y > levelHeightInMeters - halfViewPortHeight* zoom) {
			front.position.y = levelHeightInMeters - halfViewPortHeight* zoom;
		}
				
		parrallax.position.x = front.position.x / 2 + 20;
		parrallax.position.y = front.position.y / 2 + 8;						
	}*/

}
