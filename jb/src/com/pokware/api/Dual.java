package com.pokware.api;

public class Dual {
	
	public int x;
	public int y;
	
	public Dual() {
	}
		
	public Dual(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public Dual setX(int x) {
		this.x = x;
		return this;
	}

	public int getY() {
		return y;
	}

	public Dual setY(int y) {
		this.y = y;
		return this;
	}
	
	public Dual set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

}
