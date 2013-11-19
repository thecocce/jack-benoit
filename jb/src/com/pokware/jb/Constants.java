package com.pokware.jb;

public class Constants {

	public static float ZOOM_FACTOR = 1;
	
	public static int METERS_PER_TILE = 2;	
	public static int TILE_SIZE_IN_PIXELS = 32;
	public static int METERS_TO_PIXELS_RATIO = TILE_SIZE_IN_PIXELS / METERS_PER_TILE; // pixels per meters
		
	public static int[] PARALLAX_LAYERS = { 0 };
	public static int[] BACKGROUND_LAYERS = { 1, 2 };
	public static int[] SPRITE_LAYERS = { 3 };
	
}
