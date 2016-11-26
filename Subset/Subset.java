import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Subset {
    public static void main(String[] args) {
        if (0 == args.length)
            throw new IllegalArgumentException("Wrong number of arguments!");
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            queue.enqueue(item);
        }

        if (k > queue.size())
            throw new IllegalArgumentException("Subset size is bigger than set size!");

        for (String item : queue) {
            if (0 == k) break;
            StdOut.println(item);
            --k;
        }
    }
}
