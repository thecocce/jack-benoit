package com.pokware.api;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A GC-friendly implementation of {@link Map} that guarantees constant space and no post-instantiations allocations.
 * 
 * Not thread-safe.
 * 
 * @author Fabien Benoit-Koch <fabien.bk@gmail.com>
 *
 * @param <K>
 * @param <V>
 */
@SuppressWarnings("unchecked")
public class ArrayMap<K, V> implements Map<K, V> {
	
	private V[] values;
	private int size = 0;
	private IntegerHash<K> integerHash;
	
	public ArrayMap(IntegerHash<K> integerHash) {
		this.values = (V[]) new Object[integerHash.getAddressingSpaceSize()];
		this.integerHash = integerHash;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V get(Object key) {		
		final int hashCode = integerHash.hash((K)key);
		V value = values[hashCode];
		return value;
	}

	@Override
	public V put(K key, V value) {
		final int hashCode = integerHash.hash((K)key);
		V old = values[hashCode];
		values[hashCode] = value;
		return old;
	}

	@Override
	public V remove(Object key) {
		final int hashCode = integerHash.hash((K)key);
		V value = values[hashCode];
		return value;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		Set<?> entrySet = m.entrySet();
		for (Object entry : entrySet) {
			put((K)((Map.Entry)entry).getKey(), (V)((Map.Entry)entry).getValue()); 
		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < values.length; i++) {
			values[i] = null;
		}
		size = 0;	
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
