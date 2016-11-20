import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {
    private double mean = 0.0;
    private double stddev = 0.0;
    private double confidenceLo = 0.0;
    private double confidenceHi = 0.0;

    public PercolationStats(int n, int trials)    // perform trials independent experiments on an n-by-n grid
    {
        if (n < 1 || trials < 1)
            throw new IllegalArgumentException("Wrong grid size or a number of trials!");

        double[] fractions = new double[trials];

        for (int i = 0; i < trials; i++)
        {
            Percolation p = new Percolation(n);
            double sites = 0;

            while (!p.percolates())
            {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);

                if (!p.isOpen(row, col)) {
                    p.open(row, col);
                    sites++;
                }
            }

            fractions[i] = sites/(n*n);
        }

        mean = StdStats.mean(fractions);
        stddev = StdStats.stddev(fractions);
        confidenceLo = mean - (1.96*stddev/Math.sqrt(trials));
        confidenceHi = mean + (1.96*stddev/Math.sqrt(trials));
    }

    public double mean()                          // sample mean of percolation threshold
    {
        return mean;
    }

    public double stddev()                        // sample standard deviation of percolation threshold
    {
        return stddev;
    }

    public double confidenceLo()                  // low  endpoint of 95% confidence interval
    {
        return confidenceLo;
    }

    public double confidenceHi()                  // high endpoint of 95% confidence interval
    {
        return confidenceHi;
    }

    public static void main(String[] args)    // test client (described below)
    {
        if (args.length != 2)
            throw new IllegalArgumentException("Wrong number of arguments!");

        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats ps = new PercolationStats(n, trials);

        StdOut.println("mean: " + ps.mean());
        StdOut.println("stddev: " + ps.stddev());
        StdOut.println("95% confidence interval: " + ps.confidenceLo() + ", " + ps.confidenceHi());
    }
}
