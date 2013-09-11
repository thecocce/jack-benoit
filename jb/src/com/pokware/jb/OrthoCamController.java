/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.pokware.jb;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.pokware.jb.objects.Zombie;

public class OrthoCamController extends InputAdapter {
	final OrthographicCamera camera;
	final OrthographicCamera parrallaxCamera;
	final Vector3 curr = new Vector3();
	final Vector3 last = new Vector3(-1, -1, -1);
	final Vector3 delta = new Vector3();
	private Level level;

	public OrthoCamController(OrthographicCamera camera, OrthographicCamera parrallaxCamera, Level level) {
		this.camera = camera;
		this.parrallaxCamera = parrallaxCamera;		
		this.level = level;
	}
		
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {		
		/*camera.unproject(curr.set(x, y, 0)); // coords are now in opengl units (32u/tile)		
		float worldX = curr.x/5f;
		float worldY = (map.height*5-curr.y)/5f;
		int tileX = (int) worldX;
		int tileY = (int) worldY;
		
		int id = map.layers.get(0).tiles[tileY][tileX];
		String tileProperty = map.getTileProperty(id, "col");
		if (tileProperty != null) {
			System.out.println("tile id " + id + " at x,y " + tileX + ", "+ tileY+ " has col! "+tileProperty );
		}
		else {
			System.out.println("tile id " + id + " at x,y " + tileX + ", "+ tileY+ " has no col." );	
		}*/
				
		if (button == 0) {
//			camera.unproject(curr.set(x, y, 0));				
//			level.objectManager.getJack().body.setTransform(curr.x, curr.y, 0f);
			level.objectManager.getJack().jump();
		}
		else {
			camera.unproject(curr.set(x, y, 0));				
			level.objectManager.add(new Zombie(level, curr.x, curr.y));
		}
		
		return false;
	}
	
	

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		/*camera.unproject(curr.set(x, y, 0));

		if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
			camera.unproject(delta.set(last.x, last.y, 0));
			delta.sub(curr);
			camera.position.add(delta.x, delta.y, 0);			
			parrallaxCamera.position.add(delta.x/5f, delta.y/5f, 0);			
		}
		last.set(x, y, 0);*/
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		last.set(-1, -1, -1);
		return false;
	}
}
