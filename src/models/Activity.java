package models;


public enum Activity {


    Walking(1, "walking", 3),
    LyingDown(2, "lying down", 3),
    StandingUp(3, "standing up", 3),
    SittingDown(4, "sitting down", 3),
    Bending(5, "bending", 3),
    Falling(6, "falling", 3);

    private final String name;
    private final int index;
    private final int internalStates;

    private Activity(int index, String name, int internalStates) {
        this.name = name;
        this.index = index;
        this.internalStates = internalStates;
    }

    public int getInternalStates() {
        return internalStates;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }

    public static String getActivityNameByIndex(int index) {

        switch (index) {
            case 0:
                return "no activity";
            case 1:
                return Walking.getName();
            case 2:
                return LyingDown.getName();
            case 3:
                return StandingUp.getName();
            case 4:
                return SittingDown.getName();
            case 5:
                return Bending.getName();
            case 6:
                return Falling.getName();
            default:
                throw new IllegalArgumentException("No activity with index " + index);
        }
    }
}
