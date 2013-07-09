package tracking;

import models.JointPoint;

import javax.vecmath.Point3d;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class User {

    private Map<Integer, Point3d> skeleton;
    public static final int NUM_JOINTS = 15;
    private Point3d floorPoint, floorNormal;
    private long timestamp;

    public User() {
        skeleton = new HashMap<Integer, Point3d>();
    }


    public static User readUser(String fileName) throws IOException {

        String strLine, floorLine, timestampLine;
        List<String> strings;
        FileInputStream fstream;
        DataInputStream in;
        BufferedReader br = null;
        int error;

        try {

            fstream = new FileInputStream(fileName);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            strings = new LinkedList<String>();

        /* read error */
            strLine = readSingleLine(br);
            error = new Integer(strLine.substring(0, strLine.length() - 1));

            if (error == 1) {
                return new User();
            }


            floorLine = readSingleLine(br);
            timestampLine = readSingleLine(br);



        /* read each joint point line*/
            for (int i = 0; i < NUM_JOINTS; i++) {
                strLine = readSingleLine(br);
                strings.add(strLine);
            }

            return new User(strings, floorLine, timestampLine);

        } finally {

            if (null != br) {
                br.close();
            }
        }


    }

    public static String readSingleLine(BufferedReader br) throws IOException {
        String strLine;
        if (((strLine = br.readLine()) == null)) {
            throw new IllegalArgumentException("Invalid skeleton file format");
        }
        return strLine;
    }

    public User(List<String> points, String floorLine, String timestampLine) {

        skeleton = new HashMap<Integer, Point3d>();
        int n = points.size();
        String tokens[], line;
        double x, y, z;

        floorLine = floorLine.substring(0, floorLine.length() - 1);
        timestampLine = timestampLine.substring(0, timestampLine.length() - 1);

        tokens = floorLine.split(",");
        if (tokens.length != 7) {

            throw new IllegalArgumentException("Invalid parameters to create user from joints");
        }

        x = Double.parseDouble(tokens[0]);
        y = Double.parseDouble(tokens[1]);
        z = Double.parseDouble(tokens[2]);

        floorPoint = new Point3d(x, y, z);

        x = Double.parseDouble(tokens[3]);
        y = Double.parseDouble(tokens[4]);
        z = Double.parseDouble(tokens[5]);

        floorNormal = new Point3d(x, y, z);

        tokens = timestampLine.split(",");

        if (tokens.length != 2) {

            throw new IllegalArgumentException("Invalid parameters to create user from joints");
        }


        timestamp = Long.parseLong(tokens[1]);


        for (int i = 0; i < n; i++) {

            line = points.get(i);
            line = line.substring(0, line.length() - 1);
            tokens = line.split(",");

            if (tokens.length != 4) {
                System.err.println("Invalid input entry " + points.get(i));
                throw new IllegalArgumentException("Invalid parameters to create user from joints");
            } else {
                x = Double.parseDouble(tokens[0]);
                y = Double.parseDouble(tokens[1]);
                z = Double.parseDouble(tokens[2]);
                skeleton.put(i, new Point3d(x, y, z));
            }
        }

    }

    public Point3d getSkeletonElement(JointPoint jointPoint) {
        Point3d skeletonElement =  skeleton.get(jointPoint.getIndex());
        if(skeletonElement == null)
            return new Point3d(0,0,0);
        return skeletonElement;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public Point3d getFloorNormal() {
        return floorNormal;
    }

    public Point3d getFloorPoint() {
        return floorPoint;
    }
}
