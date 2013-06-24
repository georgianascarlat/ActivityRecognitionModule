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

    public static int getActivitiesNumber() {

        return 6;
    }

    public static String getActivityNameByIndex(int index) {

        if (index == 0)
            return "no activity";
        else
            return getActivityByIndex(index).getName();

    }

    public static Activity getActivityByIndex(int index) {

        switch (index) {

            case 1:
                return Walking;
            case 2:
                return LyingDown;
            case 3:
                return StandingUp;
            case 4:
                return SittingDown;
            case 5:
                return Bending;
            case 6:
                return Falling;
            default:
                throw new IllegalArgumentException("No activity with index " + index);
        }
    }
}
