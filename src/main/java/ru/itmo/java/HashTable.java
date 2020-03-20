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
        int index = getIndex(key);
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
        int index = getIndex(key);
        if (entityExists(index)) {
            return null;
        }
        return array[index].getValue();
    }

    public Object remove(Object key) {
        int index = getIndex(key);
        if (entityExists(index)) {
            return null;
        }
        size--;
        var value = array[index].getValue();
        array[index] = Entity.DELETED;
        return value;
    }

    public int size() {
        return size;
    }

    private boolean entityExists(int index) {
        return array[index] == null || array[index].isDeleted();
    }

    private int getIndex(Object key) {
        int arrayLength = array.length;
        int index = Math.abs(key.hashCode() % arrayLength);
        int startIndex = index;
        boolean secondTurn = false;
        while (array[index] != null) {
            if (array[index].notDeleted() && array[index].getKey().equals(key)) {
                break;
            }
            if (secondTurn && array[index].isDeleted()) {
                break;
            }
            index++;
            if (index == array.length) {
                index = 0;
            }
            if (index == startIndex) {
                secondTurn = true;
            }
        }
        return index;
    }


    private int getThreshold() {
        return (int) (loadFactor * array.length);
    }

    private void checkAndResizeIfNeeded() {
        if (size >= getThreshold()) {
            Entity[] oldArray = array;
            array = new Entity[oldArray.length * 2];
            for (Entity entity : oldArray) {
                if (entity != null && entity.notDeleted()) {
                    array[getIndex(entity.getKey())] = entity;
                }
            }
        }
    }


    private static class Entity {
        private final Object key;
        private final Object value;

        private static final Entity DELETED = new Entity(null, null);

        public Entity(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public boolean notDeleted() {
            return this != DELETED;
        }

        public boolean isDeleted() {
            return this == DELETED;
        }

    }
}
