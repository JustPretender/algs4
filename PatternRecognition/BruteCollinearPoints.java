import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;
import java.util.Arrays;

public class BruteCollinearPoints {
    private Stack<LineSegment> segments;

    // Added a helper class to cope with duplicate segments
    private class Segment {
        private Point start;
        private Point end;

        private Segment(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        // Segments are equal if they have equal start and end points
        private boolean isEqual(Segment that) {
            if (start == that.start && end == that.end)
                return true;

            if (start == that.end && end == that.start)
                return true;

            return false;
        }

        public String toString() {
            return "[" + start + "," + end +"]";
        }
    }

    public BruteCollinearPoints(Point[] points) {   // finds all line segments containing 4 points
        if (null == points)
            throw new NullPointerException("an argument is null");

        for (int i = 0; i < points.length; i++) {
            if (null == points[i])
                throw new NullPointerException("an argument is null");

            for (int j = i + 1; j < points.length; j++) {
                if (0 == points[i].compareTo(points[j])) {
                    throw new IllegalArgumentException("A duplicate element detected");
                }
            }
        }

        Point[] copyPoints = Arrays.copyOf(points, points.length);
        Stack<Segment> aux = new Stack<Segment>();

        segments = new Stack<LineSegment>();
        Arrays.sort(copyPoints);
        // Iterate through all the points
        for (int p = 0; p < copyPoints.length - 3; p++) {
            for (int q = p + 1; q < copyPoints.length - 2; q++) {
                // Calculate the first slope to P
                double slopeToQ = copyPoints[p].slopeTo(copyPoints[q]);

                for (int r = q + 1; r < copyPoints.length - 1; r++) {
                    // Calculate the second slope P
                    double slopeToR = copyPoints[p].slopeTo(copyPoints[r]);

                    // If the first == the second, accumulate
                    // push them onto the stack and try to find
                    // the next one(s)
                    if (Double.compare(slopeToQ, slopeToR) == 0) {
                        Stack<Point> collinear = new Stack<Point>();

                        collinear.push(copyPoints[q]);
                        collinear.push(copyPoints[r]);

                        for (int s = r + 1; s < copyPoints.length; s++) {
                            // Calculate the next slope to P and check if
                            // it matches the previous ones, if yes -
                            // add this point onto the stack and continue
                            double slopeToS = copyPoints[p].slopeTo(copyPoints[s]);

                            if (Double.compare(slopeToQ, slopeToS) == 0) {
                                collinear.push(copyPoints[s]);
                            }
                        }

                        // Ones we finished with the last loop - check if we have
                        // enough points to make a segment, if yes - process them
                        if (collinear.size() >= 3) { // at least three collinear points + 1 p
                            Segment segment = processCollinear(collinear, copyPoints[p]);
                            boolean found = false;

                            for (Segment s:aux) {
                                if (s.isEqual(segment)) {
                                    found = true;
                                }
                            }

                            if (!found) {
                                aux.push(segment);
                                segments.push(new LineSegment(segment.start, segment.end));
                            }
                        }
                    }
                }
            }
        }
    }

    private Segment processCollinear(Stack<Point> collinear, Point p) {
        // Creating a segment
        Point start = p;
        Point stop = p;

        // Consider the natural order to find the beginning and the end
        // of a segment
        while (!collinear.isEmpty()) {
            Point point = collinear.pop();

            if (start.compareTo(point) > 0) {
                start = point;
            }

            if (stop.compareTo(point) < 0) {
                stop = point;
            }
        }

        return new Segment(start, stop);
    }

    public int numberOfSegments()        // the number of line segments
    {
        return segments.size();
    }

    public LineSegment[] segments()                // the line segments
    {
        LineSegment[] sgmnts = new LineSegment[segments.size()];
        int i = 0;

        for (LineSegment ls : segments)
            sgmnts[i++] = ls;

        return sgmnts;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        StdOut.println("Examining:" + args[0]);
        // print and draw the line segments
        Stopwatch watch = new Stopwatch();
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        StdOut.println(watch.elapsedTime());
        StdOut.println("Number of segments: " + collinear.numberOfSegments());
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }

        StdDraw.show();
    }
}
