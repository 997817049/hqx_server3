package com.zty.hqx.util.cachePool;

public interface CachePoolInterface<K, V> {

    public V get(K key);

    public void put(K key, V value);

    public V remove(K key);

    public void clear();
}
