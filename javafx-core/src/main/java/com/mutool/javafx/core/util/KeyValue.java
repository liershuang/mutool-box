package com.mutool.javafx.core.util;

import java.util.Objects;

public class KeyValue<K, V> {

    private K key;

    private V value;

    public KeyValue() {
    }

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public KeyValue(KeyValue<K, V> keyValue) {
        this(keyValue.key, keyValue.value);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(key);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        KeyValue<?, ?> keyValue = (KeyValue<?, ?>) object;
        return Objects.equals(value, keyValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
