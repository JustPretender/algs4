import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;
import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {
    private Stack<LineSegment> segments;

    // A helper "Comparable" Segment class to deal with duplicates
    // and subsegments
    private class Segment implements Comparable<Segment> {
        private double slope;
        private Point start;
        private Point stop;

        private Segment(Point start, Point stop, Double slope) {
            this.start = start;
            this.stop = stop;
            this.slope = slope;
        }

        // Return true if segments are equal
        // Two segments are equal if:
        // 1) Their slopes are equal;
        // 2) Their start/stop points are equal
        //    OR
        //    slopes from starts/ends to another start/stop
        //    are equal (the rule for determining if two points
        //    are on the same line)
        public boolean isEqual(Segment that) {
            if (Double.compare(this.slope, that.slope) != 0)
                return false;

            if (this.start.compareTo(that.start) == 0 && this.stop.compareTo(that.stop) == 0)
                return true;

            if (this.start.compareTo(that.stop) == 0 && this.stop.compareTo(that.start) == 0)
                return true;

            if (Double.compare(this.start.slopeTo(that.start), this.stop.slopeTo(that.start)) == 0)
                return true;

            if (Double.compare(this.stop.slopeTo(that.stop), this.start.slopeTo(that.stop)) == 0)
                return true;

            return false;
        }

        public String toString() {
            return "[" + start + ";" + stop + "] = " + slope;
        }

        // Compares two segments following the "natural" order:
        // Start points first, if equal then Stop points
        public int compareTo(Segment that) {
            if (this.start.compareTo(that.start) == 0 && this.stop.compareTo(that.stop) == 0)
                return 0;

            if (this.start.compareTo(that.stop) == 0 && this.stop.compareTo(that.start) == 0)
                return 0;

            if (this.start.compareTo(that.start) == 0 && this.stop.compareTo(that.stop) < 0)
                return -1;

            if (this.start.compareTo(that.start) < 0)
                return -1;

            return +1;
        }

        public Comparator<Segment> slopeOrder() {
            return new SlopeOrder();
        }

        private class SlopeOrder implements Comparator<Segment> {
            public int compare(Segment s1, Segment s2) {
                return Double.compare(s1.slope, s2.slope);
            }
        }

    }

    public FastCollinearPoints(Point[] points) {     // finds all line segments containing 4 or more points
        if (null == points)
            throw new NullPointerException("an argument is null");

        for (int i = 0; i < points.length; i++) {
            if (null == points[i])
                throw new NullPointerException("an argument is null");
        }

        // Get your own copy of data and sort it in the
        // natural order so we can filter duplicates in O(N)
        // time
        Point[] copyPoints = Arrays.copyOf(points, points.length);

        Arrays.sort(copyPoints);
        for (int i = 0; i < copyPoints.length - 1; i++) {
            if (0 == copyPoints[i].compareTo(copyPoints[i + 1])) {
                throw new IllegalArgumentException("A duplicate element detected");
            }
        }

        // Create a temporary Segment array to store all found segments.
        // The maximum possible size is chosen here, which is N(N-1)/2 + 1
        Segment[] aux = new Segment[copyPoints.length*(copyPoints.length - 1)/2 + 1];
        int index = 0;
        // Iterate through all points trying to find 4+ segments
        for (Point p: points) {
            // Sort points by their slopes to p
            Arrays.sort(copyPoints, p.slopeOrder());

            // And try to find 3 adjacent points with the same slope
            // Use start and stop as markers of a segment
            double slope = Double.NEGATIVE_INFINITY; // Start from p->p slope
            Point start = p;
            Point stop = p;
            int size = 0;

            for (int j = 1; j < copyPoints.length; j++) { // 0 is always the p point
                Point q = copyPoints[j];

                // Check if q has the same slope to p as the previous one
                // If yes - adjust start/stop and increase the counter
                if (Double.compare(slope, p.slopeTo(q)) == 0) {
                    if (start.compareTo(q) > 0) {
                        start = q;
                    }
                    if (stop.compareTo(q) < 0) {
                        stop = q;
                    }

                    size++;
                }
                else {
                    // If the size of a segment is already >= 3 - add it to the array
                    if (size >= 3) {
                        aux[index++] = new Segment(start, stop, slope);
                    }

                    // Otherwise reset the slope, reset the markers and the size
                    slope = p.slopeTo(q);
                    start = p;
                    stop = p;

                    if (start.compareTo(q) > 0) {
                        start = q;
                    }
                    if (stop.compareTo(q) < 0) {
                        stop = q;
                    }
                    // It's "1" because we have to include Q-point already
                    size = 1;
                }
            }

            // handle a corner case when we accumulated enough collinear points
            // but finished looping
            if (size >= 3) {
                aux[index++] = new Segment(start, stop, slope);
            }
        }

        // Fun part. Now we need to find and remove all the duplicates and subsegments
        // To do this we will sort accumulated segments twice:
        // in the natural order and then by their slopes. Arrays.sort is using MergeSort
        // so it's guaranteed that segments sorted by slopes will also remain sorted
        // in the natural order (within a slope).
        // After it's done we iterate over the sorted array and filter unique segments
        // Complexity: 2*NlogN + N - 1
        segments = new Stack<LineSegment>();
        if (aux[0] != null) {
            Arrays.sort(aux, 0, index);
            Arrays.sort(aux, 0, index, aux[0].slopeOrder());

            int j = 0;
            segments.push(new LineSegment(aux[j].start, aux[j].stop));
            for (int i = 1; i < index; i++) {
                if (!aux[i].isEqual(aux[j])) {
                    j++;
                    aux[j] = aux[i];
                    segments.push(new LineSegment(aux[j].start, aux[j].stop));
                }
                else {
                    aux[i] = null;
                }
            }
        }
    }

    public int numberOfSegments()        // the number of line segments
    {
        return segments.size();
    }

    public LineSegment[] segments()                // the line segments
    {
        LineSegment[] result = new LineSegment[segments.size()];
        int i = 0;
        for (LineSegment ls : segments) {
            result[i++] = ls;
        }
        return result;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.println(watch.elapsedTime());
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
