import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (null == G)
            throw new NullPointerException("an argument is null");

        this.G = new Digraph(G);
    }

    private void validate(int v) {
        if (v >= G.V() || v < 0)
            throw new IndexOutOfBoundsException();
    }

    private void validate(Iterable<Integer> v) {
        if (null == v)
            throw new NullPointerException();

        for (int i : v) {
            validate(i);
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validate(v);
        validate(w);

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int length = Integer.MAX_VALUE;

        for (int vertex = 0; vertex < G.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int distTo = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (length > distTo) {
                    length = distTo;
                }
            }
        }

        return (Integer.MAX_VALUE == length) ? -1 : length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validate(v);
        validate(w);

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int ancestor = -1;
        int length = Integer.MAX_VALUE;

        for (int vertex = 0; vertex < G.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int distTo = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (length > distTo) {
                    ancestor = vertex;
                    length = distTo;
                }
            }
        }

        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int length = Integer.MAX_VALUE;

        for (int vertex = 0; vertex < G.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int distTo = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (length > distTo) {
                    length = distTo;
                }
            }
        }

        return (Integer.MAX_VALUE == length) ? -1 : length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int ancestor = -1;
        int length = Integer.MAX_VALUE;

        for (int vertex = 0; vertex < G.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int distTo = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (length > distTo) {
                    ancestor = vertex;
                    length = distTo;
                }
            }
        }

        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
