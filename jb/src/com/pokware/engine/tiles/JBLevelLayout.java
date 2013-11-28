package com.pokware.engine.tiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JBLevelLayout {

	public int hRooms, vRooms;
	public int roomWidth, roomHeight;
	
	public Room[][] rooms; // left to right, then bottom to top  
	public int width;
	public int height;
	public int index = 0;
	public int startRoomY;
	public int startRoomX;
	
	public JBLevelLayout(int hRooms, int vRooms, int roomWidth, int roomHeight, int startRoomX, int startRoomY) {
		this.hRooms = hRooms;
		this.vRooms = vRooms;
		this.roomWidth = roomWidth;
		this.roomHeight = roomHeight;
		this.rooms = new Room[hRooms][vRooms];
		this.width = roomWidth * hRooms;
		this.height = roomHeight * vRooms;
		this.startRoomX = startRoomX;
		this.startRoomY = startRoomY;
	}
		
	@Override
	public String toString() {
		return "JBLevelLayout [hRooms=" + hRooms + ", vRooms=" + vRooms + ", roomWidth=" + roomWidth + ", roomHeight=" + roomHeight + ", rooms=" + Arrays.toString(rooms)
				+ ", width=" + width + ", height=" + height + ", index=" + index + "]";
	}
	
	public void addFilledRoom(int x, int y) {
		Room room = new Room(index, (index%hRooms)*roomWidth, (index/hRooms)*roomHeight);
		rooms[x][y] = room;
		index++;
	}

	public void addRoom(int x, int y, boolean topWall, boolean bottomWall, boolean leftWall, boolean rightWall, boolean ground) {
		Room room = new Room(index, topWall, bottomWall, leftWall, rightWall, 
				(index%hRooms)*roomWidth, 
				(index/hRooms)*roomHeight, ground);
		rooms[x][y] = room;
		index++;
	}
	
	
	
	public static enum Direction {
		NORTH, SOUTH, EAST, WEST
	}
	
	
	public static class Orientation {
		public Direction previous;
		public Direction current;
		public Orientation(Direction previous, Direction current) {
			super();
			this.previous = previous;
			this.current = current;
		}		
	}
	
	public static List<Direction> directionList = Arrays.asList(Direction.values());
	
	public static Orientation[][] directions = new Orientation[64][64];
	
	public static JBLevelLayout random(int nbRooms) {
		for (int x = 0; x < directions.length; x++) {
			for (int y = 0; y < directions[x].length; y++) {
				directions[x][y] = null;
			}
		}
		
		// 1. Random path search sequence
		
		int rx = directions.length/2, ry = directions.length/2;
		directions[directions.length/2][directions.length/2] = new Orientation(null, Direction.EAST);
		ArrayList<Direction> possibleDirections = new ArrayList<JBLevelLayout.Direction>(10);
		Direction lastDirection = Direction.EAST;
		int i = 0;		
		for(; i < nbRooms; i++) {			
			switch(lastDirection) {
				case NORTH: ry++;break;
				case SOUTH: ry--;break;
				case EAST: rx++;break;
				case WEST: rx--;break;
			}			
			possibleDirections.clear();
			possibleDirections.addAll(directionList);
			if (rx==0 || directions[rx-1][ry] != null) {
				possibleDirections.remove(Direction.WEST);
			}			
			if (ry==7 || directions[rx][ry+1] != null) {
				possibleDirections.remove(Direction.NORTH);
			}
			if (ry==0 || directions[rx][ry-1] != null) {
				possibleDirections.remove(Direction.SOUTH);
			}
			if (rx==7 || directions[rx+1][ry] != null) {
				possibleDirections.remove(Direction.EAST);
			}									
			double random = Math.random();
			int size = possibleDirections.size();
			if (size==0) {
				break;
			}
			
			int index2 = (int)(random*size);
			Direction newDirection = possibleDirections.get(index2);
			directions[rx][ry] = new Orientation(lastDirection, newDirection);
			lastDirection = newDirection;
		}
		
		/*
		 * 2. Bounding box search
		 *    +----------+ topX, topY
		 *    |          |
		 *    +----------+ 
		 *  botX, botY
		 */
		
		int topX = 0, topY = 0, bottomX = Integer.MAX_VALUE, bottomY = Integer.MAX_VALUE;
		for (int x = 0; x < directions.length; x++) {
			for (int y = 0; y < directions[x].length; y++) {
				if (directions[x][y] != null) {
					if (x < bottomX) {
						bottomX = x;
					}
					if (y < bottomY) {
						bottomY = y;
					}
					if (x > topX) {
						topX = x;
					}
					if (y > topY) {
						topY = y;
					}
				}
			}
		}
		
		// 3. Layout generation
		int hRooms = topX-bottomX;
		int vRooms = topY-bottomY;
		JBLevelLayout jbLevelLayout = new JBLevelLayout(hRooms, vRooms, 20, 16, directions.length/2-bottomX, directions.length/2-bottomY);		
		
		for (int x = 0; x < hRooms; x++) {
			for (int y = 0; y < vRooms; y++) {
				Orientation orientation = directions[x+bottomX][y+bottomY];
				
				if(orientation != null) {					
					boolean topWall = true;
					boolean bottomWall = true;
					boolean leftWall = true;
					boolean rightWall = true;
					boolean ground = false;					
					if (orientation.current == Direction.NORTH || orientation.previous == Direction.SOUTH) {
						topWall = false;						
					}
					else if (orientation.current == Direction.SOUTH || orientation.previous == Direction.NORTH) {
						bottomWall = false;
						ground = false;
					}
					else if (orientation.current == Direction.EAST || orientation.previous == Direction.WEST) {
						rightWall = false;
					}
					else if (orientation.current == Direction.WEST || orientation.previous == Direction.EAST) {
						leftWall = false;
					}				
					jbLevelLayout.addRoom(x,y, topWall, bottomWall, leftWall, rightWall, ground);
				}
				else {
					jbLevelLayout.addFilledRoom(x,y);
				}
			}
		}
		jbLevelLayout.dump();
		return jbLevelLayout;
		
	}

	
	private void dump() {
		System.out.println("start="+startRoomX + "," + startRoomY);
		
		
		for (int gy = 63; gy >= 0 ; gy--) {
			for (int gx = 0; gx < 64; gx++) {
				Orientation direction = directions[gx][gy];
				if (direction == null) {
					System.out.print(".");
				}
				else 
					switch(direction.current) {
						case NORTH: System.out.print("^");break;
						case SOUTH: System.out.print("v");break;
						case EAST: System.out.print(">");break;
						case WEST: System.out.print("<");break;
					}					
			}
			System.out.println();
		}
	}
	
	
	
	public static void main(String[] args) {		
		/*JBLevelLayout jbLevelLayout = new JBLevelLayout(4, 2, 20, 16);
		// First row
		jbLevelLayout.addRoom(true, false, true, false, false);
		jbLevelLayout.addRoom(true, false, true, false, false);
		jbLevelLayout.addRoom(true, false, true, false, false);
		jbLevelLayout.addRoom(true, false, true, false, false);
		// Second row
		jbLevelLayout.addRoom(false, true, true, false, true);
		jbLevelLayout.addRoom(false, true, false, true, true);
		jbLevelLayout.addRoom(false, true, true, false, true);
		jbLevelLayout.addRoom(false, true, false, true, true);
		
		for (Room room : jbLevelLayout) {
			System.out.println(room);
		}*/
		
		JBLevelLayout random = JBLevelLayout.random(16);
		System.out.println(random);
	}
}
