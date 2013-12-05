package com.pokware.engine.tiles;

public class Room {
	public int id; 
	public boolean topWall, bottomWall, leftWall, rightWall;
	public int offsetX, offsetY;
	public boolean ground;
	public RoomType roomType;
	
	public Room(int id, boolean topWall, boolean bottomWall, boolean leftWall, boolean rightWall, int offsetX, int offsetY, boolean ground, RoomType roomType) {
		super();
		this.id = id;
		this.topWall = topWall;
		this.bottomWall = bottomWall;
		this.leftWall = leftWall;
		this.rightWall = rightWall;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.ground = ground;
		this.roomType = roomType;
	}

	public Room(int id, int offsetX, int offsetY) {
		this.id = id;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.roomType = RoomType.FILLED;
	}

	@Override
	public String toString() {
		return "Room [topWall=" + topWall + ", bottomWall=" + bottomWall + ", leftWall=" + leftWall + ", rightWall=" + rightWall + ", offsetX=" + offsetX
				+ ", offsetY=" + offsetY + "]";
	}
	
}