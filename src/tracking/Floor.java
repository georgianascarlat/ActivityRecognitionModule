package tracking;

import javax.vecmath.Point3d;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;


public class Floor {

    private Point3d point1, point2, point3, point4;

    public Floor(Point3d point1, Point3d point2, Point3d point3, Point3d point4) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        this.point4 = point4;
    }

    public Floor(double floorWidth, double floorHeight) {

        this.point1 = new Point3d(0,0,0);
        this.point2 = new Point3d(floorWidth,0,0);
        this.point3 = new Point3d(floorWidth,0,floorHeight);
        this.point4 = new Point3d(0,0,floorHeight);

    }


    public Point3d getPoint1() {
        return point1;
    }

    public void setPoint1(Point3d point1) {
        this.point1 = point1;
    }

    public Point3d getPoint2() {
        return point2;
    }

    public Point3d getPoint3() {
        return point3;
    }



    public Point3d getPoint4() {
        return point4;
    }


    @Override
    public String toString() {
        return "Floor{" +
                "point1=" + point1 +
                ", point2=" + point2 +
                ", point3=" + point3 +
                ", point4=" + point4 +
                '}';
    }


}
