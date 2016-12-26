import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static final int ORIENTATION_X = 0;
    private static final int ORIENTATION_Y = 1;

    private Node root;

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node lb;
        private Node rt;
        private int size;

        public Node(Point2D p, int size, RectHV rect) {
            this.p = p;
            this.size = size;
            this.rect = rect;
        }

        public int size() {
            return size;
        }
    }

    public KdTree() {                               // construct an empty set of points
        root = null;
    }

    public boolean isEmpty() {                      // is the set empty?
        return null == root;
    }

    public int size() {                         // number of points in the set
        return size(root);
    }

    private int size(Node x) {
        if (null == x) return 0;
        return x.size();
    }

    public void insert(Point2D p) {              // add the point to the set (if it is not already in the set)
        if (null == p)
            throw new NullPointerException("an argument is null");

        // Start from the root, with X-asix orientation and a 1x1 grid
        root = put(root, p, ORIENTATION_X, 0.0, 0.0, 1.0, 1.0);
    }

    private Node put(Node x, Point2D p, int orientation, double xmin, double ymin, double xmax, double ymax) {
        // Create a new node if we reach the end
        if (null == x) return new Node(p, 1, new RectHV(xmin, ymin, xmax, ymax));

        // If points are equal - do not continue
        if (p.equals(x.p)) return x;

        double nX = x.p.x();
        double nY = x.p.y();

        // Compare using X-coordinates and define
        // a rectangle within P's X-coordinate bounds
        if (ORIENTATION_X == orientation) {
            if (Double.compare(p.x(), nX) < 0) {
                x.lb = put(x.lb, p, ORIENTATION_Y, xmin, ymin, nX, ymax);
            }
            else {
                x.rt = put(x.rt, p, ORIENTATION_Y, nX, ymin, xmax, ymax);
            }
        }
        else {
            if (Double.compare(p.y(), nY) < 0) {
                x.lb = put(x.lb, p, ORIENTATION_X, xmin, ymin, xmax, nY);
            }
            else {
                x.rt = put(x.rt, p, ORIENTATION_X, xmin, nY, xmax, ymax);
            }
        }

        x.size = 1 + size(x.lb) + size(x.rt);
        return x;
    }

    private boolean contains(Node x, Point2D p, int orientation) {
        if (x == null) return false;

        if (p.equals(x.p)) return true;

        double nX = x.p.x();
        double nY = x.p.y();
        Node from = null;
        boolean contains = false;

        // Compare according to the current orientation
        if (ORIENTATION_X == orientation) {
            if (Double.compare(p.x(), nX) < 0) {
                contains = contains(x.lb, p, ORIENTATION_Y);
            }
            else {
                contains = contains(x.rt, p, ORIENTATION_Y);
            }
        }
        else {
            if (Double.compare(p.y(), nY) < 0) {
                contains = contains(x.lb, p, ORIENTATION_X);
            }
            else {
                contains = contains(x.rt, p, ORIENTATION_X);
            }
        }

        return contains;
    }

    public boolean contains(Point2D p) {            // does the set contain point p?
        if (null == p)
            throw new NullPointerException("an argument is null");

        return contains(root, p, ORIENTATION_X);
    }

    public void draw() {                         // draw all points to standard draw
        draw(root, ORIENTATION_X);
    }

    private void draw(Node x, int orientation) {
        if (null == x) return;

        StdDraw.setPenRadius(0.005);

        if (ORIENTATION_X == orientation) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x.p.x(), x.rect.ymin(), x.p.x(), x.rect.ymax());
            draw(x.lb, ORIENTATION_Y);
            draw(x.rt, ORIENTATION_Y);
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.p.y());
            draw(x.lb, ORIENTATION_X);
            draw(x.rt, ORIENTATION_X);
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        x.p.draw();
    }

    public Iterable<Point2D> range(RectHV rect) {             // all points that are inside the rectangle
        if (null == rect)
            throw new NullPointerException("an argument is null");

        Queue<Point2D> rangeQ = new Queue<Point2D>();
        range(root, rangeQ, rect);
        return rangeQ;
    }

    private void range(Node x, Queue<Point2D> queue, RectHV rect) {
        if (x == null) return;

        // If there's no intersection we won't find any points there
        if (!rect.intersects(x.rect)) return;

        if (rect.contains(x.p))
            queue.enqueue(x.p);

        range(x.lb, queue, rect);
        range(x.rt, queue, rect);
    }

    public Point2D nearest(Point2D p) {             // a nearest neighbor in the set to point p; null if the set is empty
        if (null == p)
            throw new NullPointerException("an argument is null");

        // If there are no points - there's no nearest point either
        if (isEmpty())
            return null;

        return nearest(root, p, root.p, ORIENTATION_X);
    }

    private Point2D nearest(Node x, Point2D p, Point2D nearest, int orientation) {
        if (null == x) return nearest;

        // Don't check segments which are too far from the p
        if (Double.compare(x.rect.distanceSquaredTo(p), p.distanceSquaredTo(nearest)) > 0)
            return nearest;

        // Found a closer point
        if (Double.compare(x.p.distanceSquaredTo(p), p.distanceSquaredTo(nearest)) < 0)
            nearest = x.p;

        // Look for the nearest point according to the orientation
        if (ORIENTATION_X == orientation) {
            if (Double.compare(p.x(), x.p.x()) < 0) {
                nearest = nearest(x.lb, p, nearest, ORIENTATION_Y);
                nearest = nearest(x.rt, p, nearest, ORIENTATION_Y);
            }
            else {
                nearest = nearest(x.rt, p, nearest, ORIENTATION_Y);
                nearest = nearest(x.lb, p, nearest, ORIENTATION_Y);
            }
        }
        else {
            if (Double.compare(p.y(), x.p.y()) < 0) {
                nearest = nearest(x.lb, p, nearest, ORIENTATION_X);
                nearest = nearest(x.rt, p, nearest, ORIENTATION_X);
            }
            else {
                nearest = nearest(x.rt, p, nearest, ORIENTATION_X);
                nearest = nearest(x.lb, p, nearest, ORIENTATION_X);
            }
        }

        return nearest;
    }

    public static void main(String[] args) {                 // unit testing of the methods (optional)

    }
}
