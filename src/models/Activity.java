package models;


public enum Activity {


    Walking(1, "walking"),
    LyingDown(2, "lying down");

    private final String name;
    private final int index;

    private Activity(int index, String name) {
        this.name = name;
        this.index = index;
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
}
