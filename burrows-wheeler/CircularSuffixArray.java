import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class CircularSuffixArray {
        private final Suffix[] suffixes;
        private final int N;

        // circular suffix array of s
        public CircularSuffixArray(String s) {
                if (s == null) {
                        throw new java.lang.IllegalArgumentException();
                }

                N = s.length();
                suffixes = new Suffix[N];
                for (int index = 0; index < N; index++) {
                        suffixes[index] = new Suffix(s, index);
                }
                Arrays.sort(suffixes);
        }

        private static class Suffix implements Comparable<Suffix> {
                private final String text;
                private final int index;

                private Suffix(String text, int index) {
                        this.text = text;
                        this.index = index;
                }
                private int length() {
                        return text.length() - index;
                }
                private char charAt(int i) {
                        return text.charAt((index + i) % text.length());
                }

                public int compareTo(Suffix that) {
                        if (this == that) return 0;  // optimization
                        int n = text.length();
                        for (int i = 0; i < n; i++) {
                                if (this.charAt(i) < that.charAt(i)) return -1;
                                if (this.charAt(i) > that.charAt(i)) return +1;
                        }
                        return this.length() - that.length();
                }

                public String toString() {
                        return text.substring(index) + text.substring(0, index);
                }
        }

        // length of s
        public int length() {
                return N;
        }

        // returns index of ith sorted suffix
        public int index(int i) {
                if (i < 0 || i >= N) {
                        throw new java.lang.IllegalArgumentException();
                }

                return N - suffixes[i].length();
        }

        // unit testing (required)
        public static void main(String[] args) {
                String s;
                if (args.length == 1) {
                        s = args[0];
                } else {
                        s = BinaryStdIn.readString();
                }

                StdOut.println("  i\tOriginal Suffixes\t\tSortedSuffixes\tindex[i]");
                StdOut.println("--------------------------------------------------");
                CircularSuffixArray cfa = new CircularSuffixArray(s);

                for (int index = 0; index < cfa.N; index++) {
                        StdOut.printf("%3d\t%3s\t\t\t\t%3s\t\t%3d\n", index, s.substring(index) + s.substring(0, index), cfa.suffixes[index], cfa.index(index));
                }

                StdOut.println(cfa.length());
        }
}