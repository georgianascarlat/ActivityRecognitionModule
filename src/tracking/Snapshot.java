package tracking;

import models.JointPoint;
import utils.Pair;

import javax.vecmath.Point3d;
import java.io.BufferedReader;
import java.io.IOException;


public class Snapshot {


    private User user;
    private Floor floor;
    private int widthParts, heigthParts;


    public Snapshot(String fileName, int widthParts, int heightParts, double floorWidth, double floorHeight) throws IOException {
        this(fileName, widthParts, heightParts, new Floor(floorWidth, floorHeight));
    }

    public Snapshot(String fileName, int widthParts, int heightParts, Floor floor) throws IOException {

        this.user = User.readUser(fileName);
        this.widthParts = widthParts;
        this.heigthParts = heightParts;
        this.floor = floor;
    }


    public Pair<Integer, Integer> getUserOnFloorPosition(JointPoint jointPoint) {

        Position position = new Position().calcPosition(jointPoint);

        int line = position.getLine();
        int column = position.getColumn();

        if (column > (widthParts - 1))
            column = widthParts - 1;
        if (line > (heigthParts - 1))
            line = heigthParts - 1;
        if (line < 0)
            line = 0;
        if (column < 0)
            column = 0;


        return new Pair<Integer, Integer>(line, column);

    }


    @Override
    public String toString() {
        return "Snapshot{" +
                "user=" + user +
                ", floor=" + floor +
                '}';
    }

    public class Position {
        private int line;
        private int column;
        private double distance;


        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public Position calcPosition(JointPoint jointPoint) {
            Point3d p1, p2, p3;
            Point3d floorNormal;
            Point3d projectionPoint;
            Point3d projection1, projection2;
            double lat, lung, dist, chunkLat, chunkLung;

            p1 = floor.getPoint1();
            p2 = floor.getPoint2();
            p3 = floor.getPoint3();


            lat = p2.distance(p3);
            lung = p1.distance(p2);
            chunkLat = lat / heigthParts;
            chunkLung = lung / widthParts;


            floorNormal = Geometry.planNormal(p1, p2, p3);
            projectionPoint = Geometry.projectPointOnPlan(floorNormal, p1, user.getSkeletonElement(jointPoint));
            projection1 = Geometry.projectPointOnLine(projectionPoint, p1, p2);
            projection2 = Geometry.projectPointOnLine(projectionPoint, p2, p3);


            dist = projection1.distance(p1);
            column = (int) (dist / chunkLung);
            this.distance = dist - column * chunkLung;

            dist = projection2.distance(p2);
            line = (int) (dist / chunkLat);
            this.distance = Math.min(this.distance, dist - line * chunkLat);


            return this;
        }
    }
}
