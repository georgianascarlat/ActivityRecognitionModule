package tracking;

public class RoomInfo {

    private double width, height;
    private int widthParts, heightParts;
    private double widthChunk, heightChunk;
    private int highLighted;

    public RoomInfo(double width, double height, int widthParts, int heightParts) {
        this.width = width;
        this.height = height;
        this.widthParts = widthParts;
        this.heightParts = heightParts;
        this.widthChunk = width / widthParts;
        this.heightChunk = height / heightParts;
    }


    public int getWidthParts() {
        return widthParts;
    }

    public int getHeightParts() {
        return heightParts;
    }

    public double getWidthChunk() {
        return widthChunk;
    }

    public double getHeightChunk() {
        return heightChunk;
    }

    public int getHighLighted() {
        return highLighted;
    }

    public void setHighLighted(int highLighted) {
        this.highLighted = highLighted;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "width=" + width +
                ", height=" + height +
                ", widthParts=" + widthParts +
                ", heightParts=" + heightParts +
                '}';
    }
}
