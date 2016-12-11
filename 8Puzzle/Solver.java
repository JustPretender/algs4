import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;

public class Solver {
    private final int moves;
    private Stack<Board> solution;

    private class SearchNode implements Comparable<SearchNode> {
        private SearchNode prev;
        private int moves;
        private Board board;
        //        private int hamming;
        private int manhattan;

        private SearchNode(Board board, SearchNode prev, int moves) {
            this.board = board;
            this.prev = prev;
            this.moves = moves;
            //            this.hamming = moves + board.hamming();
            this.manhattan = moves + board.manhattan();
        }

        // public int compareTo(SearchNode that) {
        //     if (this.hamming == that.hamming)
        //         return 0;

        //     if (this.hamming < that.hamming)
        //         return -1;

        //     return +1;
        // }

        public int compareTo(SearchNode that) {
            if (this.manhattan == that.manhattan)
                return 0;

            if (this.manhattan < that.manhattan)
                return -1;

            return +1;
        }
    }

    public Solver(Board initial) {           // find a solution to the initial board (using the A* algorithm)
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        MinPQ<SearchNode> pqt = new MinPQ<SearchNode>();
        Board twin = initial.twin();

        SearchNode solutionNode = null;

        pq.insert(new SearchNode(initial, null, 0));
        pqt.insert(new SearchNode(twin, null, 0));
        while (!pq.isEmpty() && !pqt.isEmpty()) {
            SearchNode node = pq.delMin();
            SearchNode nodet = pqt.delMin();

            if (node.board.isGoal()) {
                solutionNode = node;
                break;
            }

            if (nodet.board.isGoal()) {
                break;
            }

            for (Board b:node.board.neighbors()) {
                SearchNode prev = node.prev;
                if (null == prev || !b.equals(prev.board))
                    pq.insert(new SearchNode(b, node, node.moves + 1));
            }

            for (Board b:nodet.board.neighbors()) {
                SearchNode prev = nodet.prev;
                if (null == prev || !b.equals(prev.board))
                    pqt.insert(new SearchNode(b, nodet, nodet.moves + 1));
            }
        }

        if (null != solutionNode) {
            solution = new Stack<Board>();
            for (SearchNode nd = solutionNode; nd != null; nd = nd.prev) {
                solution.push(nd.board);
            }
            moves = solutionNode.moves;
        }
        else {
            moves = -1;
            solution = null;
        }
    }

    public boolean isSolvable() {            // is the initial board solvable?
        return null != solution;
    }

    public int moves() {                     // min number of moves to solve initial board; -1 if unsolvable
        return moves;
    }

    public Iterable<Board> solution() {      // sequence of boards in a shortest solution; null if unsolvable
        return solution;
    }

    public static void main(String[] args) { // solve a slider puzzle (given below)
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
