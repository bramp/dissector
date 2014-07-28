package net.bramp.dissector;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

import java.util.*;

/**
 * Simple order preserving map
 * O(N) performance for key lookups
 * @author bramp
 */
public class OrderPreservingMap<K,V> implements Map<K,V> {

    static final class MyEntry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;

        MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the key.
         *
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value associated with the key.
         *
         * @return the value associated with the key
         */
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value currently associated with the key with the given
         * value.
         *
         * @return the value associated with the key before this method was
         *         called
         */
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    final Function<Map.Entry<K, V>, V> getValue = new Function<Map.Entry<K, V>, V>() {

        @Override
        public V apply(Map.Entry<K, V> input) {
            return input.getValue();
        }
    };

    final Function<Map.Entry<K, V>, K> getKey = new Function<Map.Entry<K, V>, K>() {

        @Override
        public K apply(Map.Entry<K, V> input) {
            return input.getKey();
        }
    };

    List<Entry<K, V>> entries = new ArrayList<Entry<K, V>>();

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K,V> e : entries)
            if (e.getValue().equals(value))
                return true;
        return false;
    }

    @Override
    public V get(Object key) {
        for (Map.Entry<? extends K, ? extends V> e : entries)
            if (e.getKey().equals(key))
                return e.getValue();
        return null;
    }

    @Override
    public V put(K key, V value) {
        entries.add( new MyEntry<>(key, value) );
        return null;
    }

    @Override
    public V remove(Object key) {
        for (Entry<K, V> e : entries)
            if (e.getKey().equals(key)) {
                entries.remove(e);
                return e.getValue();
            }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public Set<K> keySet() {
        return FluentIterable
            .from(entries)
            .transform(getKey)
            .toSet();
    }

    @Override
    public Collection<V> values() {
        return FluentIterable
            .from(entries)
            .transform(getValue)
            .toSet();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return ImmutableSet.copyOf(entries);
    }

	public V firstValue() {
		if (entries.isEmpty())
			throw new IndexOutOfBoundsException();
		return entries.get(0).getValue();
	}

	public V lastValue() {
		if (entries.isEmpty())
			throw new IndexOutOfBoundsException();
		return entries.get(entries.size() - 1).getValue();
	}
}
