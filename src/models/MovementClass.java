package models;


import utils.Pair;

public enum MovementClass {
    STAY(1),
    MOVE(2);

    public static final int NUM_MOVES = 2;

    private int index;

    private MovementClass(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "MovementClass{" +
                "index=" + index +
                '}';
    }

    public static MovementClass getMovement(Pair<Integer, Integer> lastPosition, Pair<Integer, Integer> newPosition) {

        int i, j, new_i, new_j;

        if (lastPosition == null || newPosition == null || lastPosition.equals(newPosition))
            return STAY;
        return MOVE;

    }
}
