/*
 * ReferenceMap.java
 *
 * Created on December 11, 2006, 9:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jboss.el.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 *
 * @author jhook
 */
public abstract class ReferenceCache<K,V> {
    
	/*
	 * 
	 */
    public abstract class ReferenceFactory<K,V> {
        public abstract ReferenceKey<K> createKey(ReferenceQueue queue, K key);
        public abstract ReferenceValue<V> createValue(ReferenceQueue queue, V value);
    }
    
    private class StrongReferenceFactory extends ReferenceFactory<K,V> {
        public ReferenceValue<V> createValue(ReferenceQueue queue, final V value) {
            return new ReferenceValue<V>() {
                public V get() {
                    return value;
                }
            };
        }
        
        public ReferenceKey<K> createKey(ReferenceQueue queue, final K key) {
            return new ReferenceKey<K>(key) {
                public K get() {
                    return key;
                }
            };
        }
    }
    
    private class WeakReferenceFactory extends ReferenceFactory<K,V> {
    	private class WeakReferenceKey extends ReferenceKey<K> {
    		private final WeakReference<K> ref;
    		
    		public WeakReferenceKey(final ReferenceQueue queue, final K key) {
    			super(key);
    			this.ref = new WeakReference<K>(key, queue) {
    				public void clear() {
    					remove();
    					super.clear();
    				}
    			};
    		}
    		
    		public K get() {
    			return this.ref.get();
    		}
    	}
    	
        public ReferenceValue<V> createValue(final ReferenceQueue queue, final V value) {
            return new ReferenceValue<V>() {
                private final WeakReference<V> ref = new WeakReference<V>(value, queue);
                public V get() {
                    return ref.get();
                }
            };
        }
        
        public ReferenceKey<K> createKey(ReferenceQueue queue, K key) {
            return new WeakReferenceKey(queue, key);
        }
    }
    
    private class SoftReferenceFactory extends ReferenceFactory<K,V> {
    	private class SoftReferenceKey extends ReferenceKey<K> {
    		private final SoftReference<K> ref;
    		
    		public SoftReferenceKey(final ReferenceQueue queue, final K key) {
    			super(key);
    			this.ref = new SoftReference<K>(key, queue) {
    				public void clear() {
    					remove();
    					super.clear();
    				}
    			};
    		}
    		
    		public K get() {
    			return this.ref.get();
    		}
    	}
    	
    	public ReferenceValue<V> createValue(final ReferenceQueue queue, final V value) {
            return new ReferenceValue<V>() {
                private final SoftReference<V> ref = new SoftReference<V>(value, queue);
                public V get() {
                    return ref.get();
                }
            };
        }
        
        public ReferenceKey<K> createKey(final ReferenceQueue queue, final K key) {
            return new SoftReferenceKey(queue, key);
        }
    }
    
    public abstract class ReferenceKey<K> {
        private final int hashCode;
        
        public ReferenceKey(K key) {
            this.hashCode = key.hashCode();
        }
        
        protected abstract K get();
        
        public boolean equals(Object obj) {
        	if (this == obj) return true;
            K me = this.get();
            if (me != null) {
                if (obj == me) return true;
                if (obj instanceof ReferenceKey) {
                    K them = ((ReferenceKey<K>) obj).get();
                    return me == them || me.equals(them);
                }
            }
            return false;
        }
        
        public void remove() {
        	cache.remove(this);
        }
        
        public int hashCode() {
            return this.hashCode;
        }
    }
    
    public interface ReferenceValue<V> {
        public V get();
    }
    
    private class ReferenceQueueRunner extends ReferenceQueue implements Runnable {
        public void run() {
            Reference ref = null;
            while (true) {
                try {
                    ref = this.remove();
                    if (ref != null) {
                        ref.clear();
                    }
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }
    }
    
    private final ConcurrentMap<ReferenceKey<K>,Future<ReferenceValue<V>>> cache;
    private final ReferenceFactory keyFactory;
    private final ReferenceFactory valueFactory;
    private final ReferenceFactory lookupFactory;
    private final ReferenceQueueRunner queue;
    
    public static enum Type { Strong, Weak, Soft };
    
    /**
     * Creates a new instance of ReferenceMap
     */
    public ReferenceCache(Type keyType, Type valueType) {
        this(keyType, valueType, 0);
    }
    
    public ReferenceCache(Type keyType, Type valueType, int initialSize) {
        this.keyFactory = toFactory(keyType);
        this.valueFactory = toFactory(valueType);
        this.lookupFactory = new StrongReferenceFactory();
        this.cache = new ConcurrentHashMap<ReferenceKey<K>,Future<ReferenceValue<V>>>(initialSize);
        this.queue = new ReferenceQueueRunner();
        Thread t = new Thread(this.queue);
        t.setDaemon(true);
        t.start();
    }
    
    private final ReferenceFactory<K,V> toFactory(Type type) {
        switch (type) {
            case Strong : return new StrongReferenceFactory();
            case Weak : return new WeakReferenceFactory();
            case Soft : return new SoftReferenceFactory();
            default : throw new IllegalArgumentException("Invalid ReferenceType: " + type);
        }
    }
    
    protected abstract V create(K key);
    
    public V get(final Object key) {
        try {
            ReferenceKey<K> refKey = this.lookupFactory.createKey(this.queue, (K) key);
            Future<ReferenceValue<V>> f = this.cache.get(refKey);
            V value = dereferenceValue(f);
            if (value != null) {
            	return value;
            } else {
                Callable<ReferenceValue<V>> call = new Callable() {
                    public ReferenceValue<V> call() throws Exception {
                        V created = create((K) key);
                        if (created != null) {
                            return valueFactory.createValue(queue, created);
                        } else {
                        	throw new NullPointerException("Value created was Null");
                        }
                    }
                };
                FutureTask<ReferenceValue<V>> task = new FutureTask<ReferenceValue<V>>(call);
                refKey = this.keyFactory.createKey(this.queue, (K) key);
                f = this.cache.putIfAbsent(refKey, task);
                if (f == null) {
                    f = task;
                    task.run();
                }
                
                value = dereferenceValue(f);
                if (value == null) {
                	value = this.create((K) key);
                	this.put((K) key, value);
                }
                return value;
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new IllegalStateException(e);
        }
    }
    
    private V dereferenceValue(ReferenceValue<V> refValue) {
        return refValue == null ? null : refValue.get();
    }
    
    private V dereferenceValue(Future<ReferenceValue<V>> futureValue) {
        try {
            return futureValue == null ? null : dereferenceValue(futureValue.get());
        } catch (Exception e) {
            return null;
        }
    }
    
    public V put(K key, final V value) {
        ReferenceKey refKey = this.keyFactory.createKey(this.queue, key);
        Callable<ReferenceValue<V>> call = new Callable() {
            public ReferenceValue<V> call() throws Exception {
                return valueFactory.createValue(queue, value);
            }
        };
        FutureTask<ReferenceValue<V>> task = new FutureTask<ReferenceValue<V>>(call);
        Future f = this.cache.putIfAbsent(refKey, task);
        if (f == null) {
            f = task;
            task.run();
        }
        return value;
    }
    
    public V remove(Object key) {
    	ReferenceKey<K> keyRef = this.lookupFactory.createKey(this.queue, key);
        return this.dereferenceValue(this.cache.remove(keyRef));
    }
    
    public int size() {
    	return this.cache.size();
    }
    
    public void clear() {
        this.cache.clear();
    }
}
