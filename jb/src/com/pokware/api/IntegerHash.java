package com.pokware.api;

public interface IntegerHash<T> {
	
	public int hash(T value);

	public int getAddressingSpaceSize();

}
