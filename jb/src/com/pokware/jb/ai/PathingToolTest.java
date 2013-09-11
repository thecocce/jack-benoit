package com.pokware.jb.ai;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class PathingToolTest {
	
	public static int[][] platforms = {
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,1,1,1,1,1,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,1,1,1,1,1,0,0,0,1,1,1,1},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
	};
	public static int[][] interactions = {
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,1,0,0,0,1,0,0,0},
			{0,0,0,0,0,0,0,1,0,0,0,1,0,0,0},
			{0,0,0,1,0,0,0,0,0,0,0,0,1,0,0},
			{0,0,0,1,0,0,0,0,0,0,0,0,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	};	
	
	public static void main(String[] args) {
		PathingTool pathingTool = new PathingTool(null, platforms, interactions);
//		pathingTool.dump();
					
		ArrayList<PathNode> wayPointsContainer = new ArrayList<PathNode>();
		Vector2 findWayPointTile = pathingTool.findRandomWayPointTile(wayPointsContainer, new Vector2(0f, 4f), 20);	
		
		System.out.println(findWayPointTile);
		System.out.println(wayPointsContainer);
		
		
	}
		
} 

