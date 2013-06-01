package models;


public enum JointPoint {
    HEAD(0),
    NECK(1),
    LEFT_SHOULDER(2),
    RIGHT_SHOULDER(3),
    LEFT_ELBOW(4),
    RIGHT_ELBOW(5),
    LEFT_HAND(6),
    RIGHT_HAND(7),
    TORSO(8),
    LEFT_HIP(9),
    RIGHT_HIP(10),
    LEFT_KNEE(11),
    RIGHT_KNEE(12),
    LEFT_FOOT(13),
    RIGHT_FOOT(14);

    private final int index;

    private JointPoint(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "JointPoint{" +
                "index=" + index +
                '}';
    }
}
