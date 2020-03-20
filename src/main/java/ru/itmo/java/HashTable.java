package ru.itmo.java;

public class HashTable {

    private static final int DEFAULT_SIZE = 1000;
    private static final double DEFAULT_LOAD_FACTOR = 0.5;

    private int size = 0;
    private Entity[] array;
    private double loadFactor;

    public HashTable(int size, double loadFactor) {
        array = new Entity[size];
        this.loadFactor = loadFactor;
    }

    public HashTable() {
        this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(double loadFactor) {
        this(DEFAULT_SIZE, loadFactor);
    }

    public Object put(Object key, Object value) {
        int index = getIndex(key, array.length);
        Entity oldEntity = array[index];
        array[index] = new Entity(key, value);
        if (oldEntity == null || oldEntity.isDeleted()) {
            size++;
            checkAndResizeIfNeeded();
            return null;
        }
        return oldEntity.getValue();
    }

    public Object get(Object key) {
        int index = getIndex(key, array.length);
        if (entityExists(index)) {
            return null;
        }
        return array[index].getValue();
    }

    public Object remove(Object key) {
        int index = getIndex(key, array.length);
        if (entityExists(index)) {
            return null;
        }
        size--;
        array[index].setDeleted();
        return array[index].getValue();
    }

    public int size() {
        return size;
    }

    private boolean entityExists(int index){
        return array[index] == null || array[index].isDeleted();
    }

    private int getIndex(Object key, int arrayLength) {
        int hash = Math.abs(key.hashCode());
        int index = hash % arrayLength;
        while (array[index] != null && !array[index].getKey().equals(key)) {
            index++;
            if (index == array.length) {
                index = 0;
            }
            if (index == hash % arrayLength) {
                break;
            }
        }
        if (array[index] == null || array[index].getKey().equals(key)) {
            return index;
        }
        index = hash % arrayLength;
        while (array[index].notDeleted()) {
            index++;
            if (index == array.length) {
                index = 0;
            }
        }
        return index;
    }

    private int getThreshold() {
        return (int) (loadFactor * array.length);
    }

    private void checkAndResizeIfNeeded() {
        if (size >= getThreshold()) {
            var oldArray = array;
            array = new Entity[array.length * 2];
            for (Entity entity : oldArray) {
                if (entity != null && entity.notDeleted()) {
                    array[getIndex(entity.getKey(), array.length)] = entity;
                }
            }
        }
    }


    private static class Entity {
        private final Object key;
        private final Object value;
        private boolean deleted;

        public Entity(Object key, Object value) {
            this.key = key;
            this.value = value;
            deleted = false;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public boolean notDeleted() {
            return !deleted;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted() {
            deleted = true;
        }
    }
}
