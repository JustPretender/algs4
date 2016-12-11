import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final char size;
    private final int hamming;
    private final int manhattan;
    private final boolean isGoal;

    private char[][] tiles;

    public Board(int[][] blocks) {           // construct a board from an n-by-n array of blocks
        if (null == blocks) {
            throw new NullPointerException("The argument is NULL");
        }

        // Save the dimension
        size = (char) blocks[0].length;

        // Construct a goal board
        char[][] goalBoard = new char[size][size];
        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                goalBoard[i][j] = (char) (i * size + j + 1);
            }
        }
        goalBoard[size - 1][size - 1] = 0;

        // Copy the blocks
        tiles = copy2d(blocks);

        // Determine if the board is the goal board
        isGoal = equals2d(tiles, goalBoard);

        // Calculate the hamming weight
        int h = 0;
        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                if (tiles[i][j] != 0 && (char) (i * size + j + 1) != tiles[i][j])
                    h++;
            }
        }
        hamming = h;

        // Calculate the manhattan weight
        int m = 0;
        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                if (tiles[i][j] != 0 && (char) (i * size + j + 1) != tiles[i][j]) {
                    char it = (char) ((tiles[i][j] - 1) / size);
                    char jt = (char) (tiles[i][j] - it * size - 1);

                    m += Math.abs(i - it) + Math.abs(j - jt);
                }
            }
        }
        manhattan = m;
    }

    // (where blocks[i][j] = block in row i, column j)
    public int dimension() {                 // board dimension n
        return size;
    }

    public int hamming() {                   // number of blocks out of place
        return hamming;
    }

    public int manhattan() {                 // sum of Manhattan distances between blocks and goal
        return manhattan;
    }

    public boolean isGoal() {                // is this board the goal board?
        return isGoal;
    }

    public Board twin() {                    // a board that is obtained by exchanging any pair of blocks
        int[][] twin = copy2dint(tiles);

        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size - 1; j++) {
                if (twin[i][j] != 0 &&
                    twin[i][j + 1] != 0) {
                    swap(twin, i, j, i, j + 1);
                    return new Board(twin);
                }
            }
        }

        return null;
    }

    public boolean equals(Object y) {        // does this board equal y?
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (this.size != that.size) return false;
        return equals2d(this.tiles, that.tiles);
    }

    private boolean equals2d(char[][] blocks1, char[][] blocks2) {
        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                if (blocks1[i][j] != blocks2[i][j])
                    return false;
            }
        }
        return true;
    }

    private int[][] copy2dint(char[][] blocks) {
        int[][] copy = new int[size][size];
        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                copy[i][j] = blocks[i][j];
            }
        }
        return copy;
    }

    private char[][] copy2d(int[][] blocks) {
        char[][] copy = new char[size][size];
        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                copy[i][j] = (char) blocks[i][j];
            }
        }
        return copy;
    }

    private void swap(int[][] a, int i1, int j1, int i2, int j2) {
        int tmp = a[i1][j1];
        a[i1][j1] = a[i2][j2];
        a[i2][j2] = tmp;
    }

    public Iterable<Board> neighbors() {     // all neighboring boards
        Stack<Board> neighbors = new Stack<Board>();
        char iz = 0;
        char jz = 0;

        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                if (tiles[i][j] == 0) {
                    iz = i;
                    jz = j;
                }
            }
        }

        if (iz > 0) {
            int[][] neighbor = copy2dint(tiles);
            swap(neighbor, iz, jz, (char) (iz - 1), jz);
            neighbors.push(new Board(neighbor));
        }

        if (iz < size - 1) {
            int[][] neighbor = copy2dint(tiles);
            swap(neighbor, iz, jz, (char) (iz + 1), jz);
            neighbors.push(new Board(neighbor));
        }

        if (jz > 0) {
            int[][] neighbor = copy2dint(tiles);
            swap(neighbor, iz, jz, iz, (char) (jz - 1));
            neighbors.push(new Board(neighbor));
        }

        if (jz < size - 1) {
            int[][] neighbor = copy2dint(tiles);
            swap(neighbor, iz, jz, iz, (char) (jz + 1));
            neighbors.push(new Board(neighbor));
        }

        return neighbors;
    }

    public String toString() {               // string representation of this board (in the output format specified below)
        StringBuilder s = new StringBuilder();
        s.append((int) size + "\n");
        for (char i = 0; i < size; i++) {
            for (char j = 0; j < size; j++) {
                s.append(String.format("%2d ", (int) tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    private void print() {
        StdOut.println("Size: " + size);
        StdOut.println("Is Goal: " + isGoal());
        StdOut.println("Hamming: " + hamming());
        StdOut.println("Manhattan: " + manhattan());
        StdOut.println(this);
        StdOut.println("");
    }

    public static void main(String[] args) { // unit tests (not graded)
        int size = 3;
        int[][] blocks = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                blocks[i][j] = i * size + j + 1;
            }
        }
        blocks[size - 1][size - 1] = 0;

        Board goal = new Board(blocks);
        goal.print();
        StdOut.println("#######Neigbors:#######");
        for (Board b : goal.neighbors()) {
            b.print();
        }

    }
}
