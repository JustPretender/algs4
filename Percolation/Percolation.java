import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdOut;

public class Percolation {
    private WeightedQuickUnionUF qu;
    private WeightedQuickUnionUF quTop;
    private boolean[][] grid;
    private int gridN;
    
    public Percolation(int n) { // create n-by-n grid, with all sites blocked
        if (n < 1)
            throw new IllegalArgumentException("Wrong grid size!");

        qu = new WeightedQuickUnionUF(n*n + 2); // extra top and bottom sites
        quTop = new WeightedQuickUnionUF(n*n + 1);
        
        // Initialize the grid
        grid = new boolean[n][n];
        for (int i = 1; i < n; i++)
            for (int j = 0; j < n; j++)
            grid[i][j] = false;
        
        gridN = n;
        
        // Initialize QuickUnion alg with 2 extra elements (n^2 and n^2 + 1)
        // connected to the top and the bottom lines
        for (int i = 1; i <= n; i++) {
            qu.union(i, 0);
            quTop.union(i, 0);
            qu.union(n*n - i + 1, n*n + 1);
        }
    }
    
    private int xyTo1D(int x, int y)
    {
        return ((x - 1)*gridN + y);
    }
    
    private void checkIndexes(int row, int col)
    {
        if (row < 1 || col < 1 || row > gridN || col > gridN)
            throw new IndexOutOfBoundsException("row or col index is out of bounds");
    }
    
    public void open(int row, int col) {      // open site (row, col) if it is not open already
        
        checkIndexes(row, col);
        
        if (isOpen(row, col))
            return;
        
        grid[row - 1][col - 1] = true; // Mark it as open and forget
        
        int site = xyTo1D(row, col);
        
        if (row > 1 && isOpen(row - 1, col)) { // a top neighbor
            qu.union(site, xyTo1D(row - 1, col));
            quTop.union(site, xyTo1D(row - 1, col));
        }
        
        if (row < gridN && isOpen(row + 1, col)) { // a bottom neighbor
            qu.union(site, xyTo1D(row + 1, col));
            quTop.union(site, xyTo1D(row + 1, col));
        }
        
        if (col > 1 && isOpen(row, col - 1)) { // a left neighbor
            qu.union(site, xyTo1D(row, col - 1));
            quTop.union(site, xyTo1D(row, col-1));
        }
        
        if (col < gridN && isOpen(row, col + 1)) { // a right neighbor
            qu.union(site, xyTo1D(row, col + 1));
            quTop.union(site, xyTo1D(row, col + 1));
        }
    }
    
    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        checkIndexes(row, col);
        
        return grid[row - 1][col - 1];
    }
    
    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        checkIndexes(row, col);
        
        if (!isOpen(row, col))
            return false;
        
        if (!quTop.connected(xyTo1D(row, col), 0))
            return false;
        
        return true;
    }
    
    public boolean percolates()              // does the system percolate?
    {
        return qu.connected(0, gridN*gridN + 1);
    }
    
    public static void main(String[] args)   // test client (optional)
    {
    }
}