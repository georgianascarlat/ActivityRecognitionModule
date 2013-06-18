package models;


public enum ObjectClass {
    NO_OBJECT(1),
    CHAIR(2),
    BICYCLE(3),
    BED(4);

    public static final int NUM_OBJECT_CLASSES = 3;

    private int index;

    private ObjectClass(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static ObjectClass getObjectByIndex(int index) {
        switch (index) {
            case 1:
                return NO_OBJECT;
            case 2:
                return CHAIR;
            case 3:
                return BICYCLE;
            case 4:
                return BED;
            default:
                throw new IllegalArgumentException("There is no object with index " + index);
        }
    }

    @Override
    public String toString() {
        return "ObjectClass {" +
                "index=" + index +
                '}';
    }
}
