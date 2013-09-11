package com.pokware.jb;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tiledmappacker.TiledMapPacker;
import com.badlogic.gdx.tools.imagepacker.TexturePacker;
import com.badlogic.gdx.tools.imagepacker.TexturePacker.Settings;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Jack Benoit: Kakahuet Hunter";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		
		// Generate libgdx-friendly assets
		deleteFiles();
		processTiledMaps();
		processSprites();
		
		new LwjglApplication(new JackBenoitApplication(1f), cfg);
	}
	

	public static void deleteFiles() {
		File outputDir = new File("../jb/data/output");
		File[] listFiles = outputDir.listFiles();
		if (listFiles != null && listFiles.length > 0) {				
			for (File file : listFiles) {
				file.delete();
			}
		}
	}
	
	public static void processSprites() {
		Settings settings = new Settings();
		settings.padding = 2;
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		settings.incremental = true;
		TexturePacker.process(settings, "../jb/data/input/sprites", "../jb/data/output");
	}

	public static void processTiledMaps() {
		Settings settings = new Settings();
		TiledMapPacker packer = new TiledMapPacker();
		File inputDir = new File("../jb/data/input");
		File outputDir = new File("../jb/data/output");

		System.out.println(inputDir.getAbsolutePath());
		System.out.println(outputDir.getAbsolutePath());
		
		try {
			packer.processMap(inputDir, outputDir, settings);
		} catch (IOException e) {
			throw new RuntimeException("Error processing map: " + e.getMessage());
		}
	}
}
