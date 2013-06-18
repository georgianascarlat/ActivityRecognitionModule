package tracking;

import models.JointPoint;
import utils.Pair;

import javax.vecmath.Point3d;
import java.io.*;
import java.util.LinkedList;
import java.util.List;


public class Snapshot {


    public static final int NUM_JOINTS = 15;
    private User user;
    private Floor floor;
    private int widthParts, heigthParts;
    private double EPSILON = 0.1;


    public Snapshot(String fileName, int widthParts, int heightParts, double floorWidth, double floorHeight) throws IOException {
        this(fileName, widthParts, heightParts, new Floor(floorWidth, floorHeight));
    }

    public Snapshot(String fileName, int widthParts, int heightParts, Floor floor) throws IOException {

        this.user = readUser(fileName);
        this.widthParts = widthParts;
        this.heigthParts = heightParts;
        this.floor = floor;
    }

    /**
     * Read skeleton joint points from file and store them in a User object.
     *
     * @param fileName file name
     * @return User object containing skeleton joint points
     * @throws IOException
     */
    private User readUser(String fileName) throws IOException {

        String strLine;
        List<String> strings;
        FileInputStream fstream = new FileInputStream(fileName);
        int error;

        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        strings = new LinkedList<String>();

        /* read error */
        strLine = readSingleLine(br);
        error = new Integer(strLine.substring(0, strLine.length() - 1));

        if (error == 1) {
            throw new IllegalArgumentException("Error in skeleton file");
        }


        /* throw away timestamp line and floor line*/
        for (int i = 0; i < 2; i++)
            readSingleLine(br);

        /* read each joint point line*/
        for (int i = 0; i < NUM_JOINTS; i++) {
            strLine = readSingleLine(br);
            strings.add(strLine);
        }

        return new User(strings);
    }

    private String readSingleLine(BufferedReader br) throws IOException {
        String strLine;
        if (((strLine = br.readLine()) == null)) {
            throw new IllegalArgumentException("Invalid skeleton file format");
        }
        return strLine;
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

    public User getUser() {
        return user;
    }

    public Floor getFloor() {
        return floor;
    }

    public int getWidthParts() {
        return widthParts;
    }

    public int getHeigthParts() {
        return heigthParts;
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

        public double getDist() {
            return distance;
        }


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
