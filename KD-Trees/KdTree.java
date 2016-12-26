import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static final int OR_X = 0;
    private static final int OR_Y = 1;

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

        root = put(root, p, OR_X, 0.0, 0.0, 1.0, 1.0);
    }

    private Node put(Node x, Point2D p, int orientation, double xmin, double ymin, double xmax, double ymax) {
        // Create a new node if we reach the end
        if (null == x) return new Node(p, 1, new RectHV(xmin, ymin, xmax, ymax));

        // If points are equal - do not insert p
        if (p.equals(x.p)) return x;

        double nX = x.p.x();
        double nY = x.p.y();

        // Compare depending on the current orientation
        if (OR_X == orientation) {
            if (Double.compare(p.x(), nX) < 0) {
                x.lb = put(x.lb, p, OR_Y, xmin, ymin, nX, ymax);
            } else {
                x.rt = put(x.rt, p, OR_Y, nX, ymin, xmax, ymax);
            }
        } else {
            if (Double.compare(p.y(), nY) < 0) {
                x.lb = put(x.lb, p, OR_X, xmin, ymin, xmax, nY);
            } else {
                x.rt = put(x.rt, p, OR_X, xmin, nY, xmax, ymax);
            }
        }

        x.size = 1 + size(x.lb) + size(x.rt);
        return x;
    }

    private Point2D get(Node x, Point2D p, int orientation) {
        if (x == null) return null;

        if (p.equals(x.p)) return x.p;

        double nX = x.p.x();
        double nY = x.p.y();
        Node where = null;

        // Compare depending on the current orientation
        if (OR_X == orientation) {
            orientation = OR_Y;
            if (Double.compare(p.x(), nX) < 0) {
                where = x.lb;
            } else {
                where = x.rt;
            }
        } else {
            orientation = OR_X;
            if (Double.compare(p.y(), nY) < 0) {
                where = x.lb;
            } else {
                where = x.rt;
            }
        }

        return get(where, p, orientation);
    }

    public boolean contains(Point2D p) {            // does the set contain point p?
        if (null == p)
            throw new NullPointerException("an argument is null");

        return get(root, p, OR_X) != null;
    }

    public void draw() {                         // draw all points to standard draw
        draw(root, OR_X);
    }

    private void draw(Node x, int orientation) {
        if (null == x) return;

        StdDraw.setPenColor((OR_X == orientation) ? StdDraw.RED : StdDraw.BLUE);
        StdDraw.setPenRadius(0.005);

        if (OR_X == orientation) {
            StdDraw.line(x.p.x(), x.rect.ymin(), x.p.x(), x.rect.ymax());
            draw(x.lb, OR_Y);
            draw(x.rt, OR_Y);
        } else {
            StdDraw.line(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.p.y());
            draw(x.lb, OR_X);
            draw(x.rt, OR_X);
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

        if (isEmpty())
            return null;

        Point2D nearestP = nearest(root, p, root.p, OR_X);
        return nearestP;
    }

    private Point2D nearest(Node x, Point2D p, Point2D closest, int orientation) {
        if (null == x) return closest;

        if (Double.compare(x.rect.distanceSquaredTo(p), p.distanceSquaredTo(closest)) > 0) return closest;

        if (Double.compare(x.p.distanceSquaredTo(p), p.distanceSquaredTo(closest)) < 0) closest = x.p;

        if (OR_X == orientation) {
            if (Double.compare(p.x(), x.p.x()) < 0) {
                closest = nearest(x.lb, p, closest, OR_Y);
                closest = nearest(x.rt, p, closest, OR_Y);
            } else {
                closest = nearest(x.rt, p, closest, OR_Y);
                closest = nearest(x.lb, p, closest, OR_Y);
            }
        } else {
            if (Double.compare(p.y(), x.p.y()) < 0) {
                closest = nearest(x.lb, p, closest, OR_X);
                closest = nearest(x.rt, p, closest, OR_X);
            } else {
                closest = nearest(x.rt, p, closest, OR_X);
                closest = nearest(x.lb, p, closest, OR_X);
            }
        }

        return closest;
    }

    public static void main(String[] args) {                 // unit testing of the methods (optional)

    }
}
