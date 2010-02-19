/*
 * ConcurrentCache.java
 *
 * Created on December 16, 2006, 5:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jboss.el.util;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author jhook
 */
public final class ConcurrentCache<K,V> {

	private final int size;
	
	private final Map<K,V> eden;
	
	private final Map<K,V> longterm;
	
	public ConcurrentCache(int size) {
		this.size = size;
		this.eden = new ConcurrentHashMap<K,V>(size);
		this.longterm = new WeakHashMap<K,V>(size);
	}
	
	public V get(K k) {
		V v = this.eden.get(k);
		if (v == null) {
			v = this.longterm.get(k);
			if (v != null) {
				this.eden.put(k, v);
			}
		}
		return v;
	}
	
	public void put(K k, V v) {
		if (this.eden.size() >= size) {
			this.longterm.putAll(this.eden);
			this.eden.clear();
		}
		this.eden.put(k, v);
	}
}
