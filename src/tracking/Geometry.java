package tracking;

import javax.vecmath.Point3d;


public class Geometry {

    public static double dotProduct(Point3d p1, Point3d p2) {
        return p1.x * p2.x + p1.y * p2.y + p1.z * p2.z;
    }

    public static Point3d crossProduct(Point3d a, Point3d b) {
        double a1 = a.x, a2 = a.y, a3 = a.z;
        double b1 = b.x, b2 = b.y, b3 = b.z;
        double x, y, z;
        x = a2 * b3 - a3 * b2;
        y = a3 * b1 - a1 * b3;
        z = a1 * b2 - a2 * b1;
        return new Point3d(x, y, z);


    }

    public static Point3d sub(Point3d p1, Point3d p2) {
        double x, y, z;
        x = p1.x - p2.x;
        y = p1.y - p2.y;
        z = p1.z - p2.z;

        return new Point3d(x, y, z);
    }

    public static Point3d add(Point3d p1, Point3d p2) {
        double x, y, z;
        x = p1.x + p2.x;
        y = p1.y + p2.y;
        z = p1.z + p2.z;

        return new Point3d(x, y, z);
    }

    public static Point3d mul(Point3d p, double a) {
        return new Point3d(a * p.x, a * p.y, a * p.z);
    }

    // project P on AB
    public static Point3d projectPointOnLine(Point3d P, Point3d A, Point3d B) {
        Point3d AB = sub(B, A);
        Point3d AP = sub(P, A);
        double t = dotProduct(AB, AP) / dotProduct(AB, AB);
        return add(A, mul(AB, t));

    }

    public static Point3d planNormal(Point3d p1, Point3d p2, Point3d p3) {
        return crossProduct(sub(p2, p1), sub(p3, p1));

    }

    public static Point3d projectPointOnPlan(Point3d planNormal, Point3d planPoint, Point3d point) {

        double a = planNormal.x, b = planNormal.y, c = planNormal.z;
        double d = -(a * planPoint.x + b * planPoint.y + c * planPoint.z);
        double u = point.x, v = point.y, w = point.z;
        double t0 = -(a * u + b * v + c * w + d) / (a * a + b * b + c * c);
        double x, y, z;
        x = u + a * t0;
        y = v + b * t0;
        z = w + c * t0;

        return new Point3d(x, y, z);


    }

    public static boolean onLine(Point3d P, Point3d A, Point3d B) {

        return P.equals(projectPointOnLine(P, A, B));

    }



    public static boolean arePointsInOrder(Point3d p0, Point3d p1, Point3d p2, Point3d p3) {

        double d1, d2, d3;

        d1 = p1.distance(p0);
        d2 = p2.distance(p0);
        d3 = p3.distance(p0);

        return d1 <= d2 && d2 <= d3;
    }
}
