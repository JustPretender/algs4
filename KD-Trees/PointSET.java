import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

public class PointSET {
    private SET<Point2D> points;

    public PointSET() {                               // construct an empty set of points
        points = new SET<Point2D>();
    }

    public boolean isEmpty() {                      // is the set empty?
        return points.isEmpty();
    }

    public int size() {                         // number of points in the set
        return points.size();
    }

    public void insert(Point2D p) {              // add the point to the set (if it is not already in the set)
        if (null == p)
            throw new NullPointerException("an argument is null");

        points.add(p);
    }

    public boolean contains(Point2D p) {            // does the set contain point p?
        if (null == p)
            throw new NullPointerException("an argument is null");

        return points.contains(p);
    }

    public void draw() {                         // draw all points to standard draw
        for (Point2D p : points) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {             // all points that are inside the rectangle
        if (null == rect)
            throw new NullPointerException("an argument is null");

        Queue<Point2D> rangeQ = new Queue<Point2D>();
        for (Point2D p : points) {
            if (rect.contains(p))
                rangeQ.enqueue(p);
        }
        return rangeQ;
    }

    public Point2D nearest(Point2D p) {             // a nearest neighbor in the set to point p; null if the set is empty
        if (null == p)
            throw new NullPointerException("an argument is null");

        Point2D nearestP = null;
        double distanceMin = Double.POSITIVE_INFINITY;
        for (Point2D pt : points) {
            double distance = pt.distanceTo(p);
            if (distance < distanceMin) {
                nearestP = pt;
                distanceMin = distance;
            }
        }

        return nearestP;
    }

    public static void main(String[] args) {                 // unit testing of the methods (optional)

    }
}
