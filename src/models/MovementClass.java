package models;


import utils.Pair;

public enum MovementClass {
    STAY(1),
    UP(2),
    RIGHT(3),
    DOWN(4),
    LEFT(5);

    public static final int NUM_MOVES = 5;

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

        i = lastPosition.getFirst();
        j = lastPosition.getSecond();
        new_i = newPosition.getFirst();
        new_j = newPosition.getSecond();

        if (Math.abs(j - new_j) < Math.abs(i - new_i)) {
            if (new_i < i)
                return UP;
            else
                return DOWN;
        } else {
            if (new_j < j)
                return LEFT;
            else
                return RIGHT;
        }

    }
}
