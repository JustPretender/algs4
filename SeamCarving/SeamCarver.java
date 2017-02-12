import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private int[][] pic2D;
    private double[][] energy;

    public SeamCarver(Picture picture) {               // create a seam carver object based on the given picture
        int height = picture.height();
        int width = picture.width();

        pic2D = new int[height][width];

        // Save the raw data
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                pic2D[h][w] = picture.get(w, h).getRGB();
            }
        }

        recalculateEnergy();
    }

    public Picture picture() {                          // current picture
        int height = pic2D.length;
        int width = pic2D[0].length;
        Picture pic = new Picture(width, height);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                pic.set(w, h, new Color(pic2D[h][w]));
            }
        }

        return pic;
    }

    public int width() {                            // width of current picture
        return pic2D[0].length;
    }

    public int height() {                           // height of current picture
        return pic2D.length;
    }

    private int dx(int x, int y) {
        int red = ((pic2D[y][x + 1] & 0x0FF0000) >> 16) - ((pic2D[y][x - 1] & 0x0FF0000) >> 16);
        int green = ((pic2D[y][x + 1] & 0x0FF00) >> 8) - ((pic2D[y][x - 1] & 0x0FF00) >> 8);
        int blue = (pic2D[y][x + 1] & 0x0FF) - (pic2D[y][x - 1] & 0xFF);

        return (red * red + green * green + blue * blue);
    }

    private int dy(int x, int y) {
        int red = ((pic2D[y + 1][x] & 0x0FF0000) >> 16) - ((pic2D[y - 1][x] & 0xFF0000) >> 16);
        int green = ((pic2D[y + 1][x] & 0x0FF00) >> 8) - ((pic2D[y - 1][x] & 0xFF00) >> 8);
        int blue = (pic2D[y + 1][x] & 0x0FF) - (pic2D[y - 1][x] & 0xFF);

        return (red * red + green * green + blue * blue);
    }

    public double energy(int x, int y) {               // energy of pixel at column x and row y
        int height = pic2D.length;
        int width = pic2D[0].length;

        if (x < 0 || x >= width)
            throw new IndexOutOfBoundsException(x + "");

        if (y < 0 || y >= height)
            throw new IndexOutOfBoundsException(y + "");

        if (x == (width - 1) || x == 0 || y == (height - 1) || y == 0)
            return 1000;

        return Math.sqrt(dx(x, y) + dy(x, y));
    }

    private void recalculateEnergy() {
        int height = pic2D.length;
        int width = pic2D[0].length;

        energy = new double[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                energy[h][w] = energy(w, h);
            }
        }
    }

    private void transposeEnergy() {
        int height = energy.length;
        int width = energy[0].length;
        double[][] transposed = new double[width][height];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                transposed[w][h] = energy[h][w];
            }
        }

        energy = transposed;
    }

    private void transposePic() {
        int height = pic2D.length;
        int width = pic2D[0].length;

        int[][] transposed = new int[width][height];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                transposed[w][h] = pic2D[h][w];
            }
        }

        pic2D = transposed;
    }

    public int[] findHorizontalSeam() {               // sequence of indices for horizontal seam
        transposeEnergy();
        int[] ret = findVerticalSeam();
        transposeEnergy();

        return ret;
    }

    public int[] findVerticalSeam() {                 // sequence of indices for vertical seam
        int height = energy.length;
        int width = energy[0].length;
        double[][] distTo = new double[height][width];
        int[][] edgeTo = new int[height][width];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (h == 0) {
                    distTo[0][w] = 0.0;
                } else {
                    distTo[h][w] = Double.POSITIVE_INFINITY;
                }
                edgeTo[h][w] = -1;
            }
        }

        // Traversing an image from top to the bottom, calculating the distances
        // from every top to the bottom one row-by-row
        for (int row = 0; row < (height - 1); row++) {
            for (int col = 0; col < width; col++) {
                if (col > 0) {
                    if (distTo[row + 1][col - 1] > distTo[row][col] + energy[row][col]) {
                        distTo[row + 1][col - 1] = distTo[row][col] + energy[row][col];
                        edgeTo[row + 1][col - 1] = row * width + col;
                    }
                }

                if (distTo[row + 1][col] > distTo[row][col] + energy[row][col]) {
                    distTo[row + 1][col] = distTo[row][col] + energy[row][col];
                    edgeTo[row + 1][col] = row * width + col;
                }

                if (col < (width - 1)) {
                    if (distTo[row + 1][col + 1] > distTo[row][col] + energy[row][col]) {
                        distTo[row + 1][col + 1] = distTo[row][col] + energy[row][col];
                        edgeTo[row + 1][col + 1] = row * width + col;
                    }
                }
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int end = 0;
        for (int w = 0; w < width; w++) {
            if (minDist > distTo[height - 1][w]) {
                minDist = distTo[height - 1][w];
                end = w;
            }
        }

        int[] result = new int[height];
        int i = height - 1;
        for (int p = (height - 1) * width + end; p != -1; p = edgeTo[p / width][p % width]) {
            result[i--] = p % width;
        }

        return result;
    }

    private void removeSeam(int[] seam) {
        int height = pic2D.length;
        int width = pic2D[0].length;

        int[][] aux = new int[height][width - 1];

        for (int h = 0; h < height; h++) {
            int newWidth = 0;
            for (int w = 0; w < width; w++) {
                if (w != seam[h]) {
                    aux[h][newWidth++] = pic2D[h][w];
                }
            }
        }

        pic2D = aux;
    }

    public void removeHorizontalSeam(int[] seam) {   // remove horizontal seam from current picture
        int height = pic2D.length;
        int width = pic2D[0].length;

        if (null == seam)
            throw new NullPointerException();

        if (width != seam.length)
            throw new IllegalArgumentException(width + "!=" + seam.length);

        for (int w = 0; w < width; w++) {
            if (seam[w] >= height || seam[w] < 0)
                throw new IllegalArgumentException();

            if (w < (width - 1) && Math.abs(seam[w + 1] - seam[w]) > 1)
                throw new IllegalArgumentException();
        }

        transposePic();
        removeSeam(seam);
        transposePic();

        recalculateEnergy();
    }

    public void removeVerticalSeam(int[] seam) {    // remove vertical seam from current picture
        int height = pic2D.length;
        int width = pic2D[0].length;

        if (null == seam)
            throw new NullPointerException();

        if (height != seam.length)
            throw new IllegalArgumentException(height + "!=" + seam.length);

        for (int h = 0; h < height; h++) {
            if (seam[h] >= width || seam[h] < 0)
                throw new IllegalArgumentException();

            if (h < (height - 1) && Math.abs(seam[h + 1] - seam[h]) > 1)
                throw new IllegalArgumentException();
        }

        removeSeam(seam);
        recalculateEnergy();
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());

        SeamCarver sc = new SeamCarver(picture);

        StdOut.printf("Printing energy calculated for each pixel.\n");

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%9.0f ", sc.energy(col, row));
            StdOut.println();
        }

        sc.findVerticalSeam();
        sc.findHorizontalSeam();

        int[] seam = { 7, 8, 9, 9, 9, 7, 8, 8, 7, 6 };
        sc.removeVerticalSeam(seam);
    }
}
