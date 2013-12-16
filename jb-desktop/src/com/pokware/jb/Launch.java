package com.pokware.jb;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pokware.jb.procedural.TweenTest;


public class Launch {

	public static void main(String[] argv) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.vSyncEnabled = true;
		config.useGL20 = true;
		config.width = 1024;
		config.height = 768;
		config.title = "Tween-Engine tests";
		
		new LwjglApplication(new TweenTest(), config);
				
	}

}
