package tracking;

import models.JointPoint;

import javax.vecmath.Point3d;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User {

    private Map<Integer, Point3d> skeleton;

    @Override
    public String toString() {
        return "User{" +
                "skeleton=" + skeleton +
                '}';
    }

    public User(List<String> points) {

        skeleton = new HashMap<Integer, Point3d>();
        int n = points.size();
        String tokens[], line;
        double x, y, z;

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

    public Point3d getSkeletonElement(String name) {

        JointPoint jointPoint = JointPoint.valueOf(name);
        return skeleton.get(jointPoint.getIndex());
    }
}
