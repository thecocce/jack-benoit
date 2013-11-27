package com.pokware.engine.tiles;

import java.util.Arrays;
import java.util.Iterator;

public class JBLevelLayout implements Iterable<Room> {

	public int hRooms, vRooms;
	public int roomWidth, roomHeight;
	
	public Room[] rooms; // left to right, then bottom to top  
	public int width;
	public int height;
	public int index = 0;
	
	public JBLevelLayout(int hRooms, int vRooms, int roomWidth, int roomHeight) {
		this.hRooms = hRooms;
		this.vRooms = vRooms;
		this.roomWidth = roomWidth;
		this.roomHeight = roomHeight;
		this.rooms = new Room[hRooms*vRooms];
		this.width = roomWidth * hRooms;
		this.height = roomHeight * vRooms;
	}
		
	@Override
	public String toString() {
		return "JBLevelLayout [hRooms=" + hRooms + ", vRooms=" + vRooms + ", roomWidth=" + roomWidth + ", roomHeight=" + roomHeight + ", rooms=" + Arrays.toString(rooms)
				+ ", width=" + width + ", height=" + height + ", index=" + index + "]";
	}
	
	public void addFilledRoom() {
		Room room = new Room(index, (index%hRooms)*roomWidth, (index/hRooms)*roomHeight);
		rooms[index] = room;
		index++;
	}

	public void addRoom(boolean topWall, boolean bottomWall, boolean leftWall, boolean rightWall, boolean ground) {
		Room room = new Room(index, topWall, bottomWall, leftWall, rightWall, 
				(index%hRooms)*roomWidth, 
				(index/hRooms)*roomHeight, ground);
		rooms[index] = room;
		index++;
	}
	
	@Override
	public Iterator<Room> iterator() {
		return new Iterator<Room>() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < rooms.length;
			}
			
			@Override
			public Room next() {			
				return rooms[index++];
			}
			
			@Override
			public void remove() {				
			}
		};
	}


	public static void main(String[] args) {		
		JBLevelLayout jbLevelLayout = new JBLevelLayout(4, 2, 20, 16);
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
		}
		
	}
}
