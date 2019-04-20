import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
        // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
        public static void transform() {
                String s = BinaryStdIn.readString();
                CircularSuffixArray cfa = new CircularSuffixArray(s);

                int first = 0;
                char[] result = new char[cfa.length()];
                for (int i = 0; i < cfa.length(); i++) {
                        int index = cfa.index(i);
                        if (index == 0) {
                                first = i;
                                result[i] = s.charAt(cfa.length() - 1);
                        } else {
                                result[i] = s.charAt(index - 1);
                        }
                }

                BinaryStdOut.write(first);
                for (int i = 0; i < result.length; i++) {
                        BinaryStdOut.write(result[i]);
                }
                BinaryStdOut.flush();
        }

        // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
        public static void inverseTransform() {
                int first = BinaryStdIn.readInt();
                String encoded = BinaryStdIn.readString();
                int[] next = new int[encoded.length()];
                int[] count = new int[256 + 1];

                // First count number of appearences of each character
                for (int i = 0; i < encoded.length(); i++) {
                        count[encoded.charAt(i) + 1]++;
                }

                // Now count the distance between distinct characters
                // in _next[]_
                for (int r = 0; r < count.length - 1; r++) {
                        count[r + 1] += count[r];
                }

                // build the _next[]_ from the encoded string (t)
                for (int i = 0; i < encoded.length(); i++) {
                        next[count[encoded.charAt(i)]++] = i;
                }

                // finally decode the string using _next[]_
                for (int i = 0; i < encoded.length(); i++) {
                        first = next[first];
                        BinaryStdOut.write(encoded.charAt(first));
                }

                BinaryStdOut.flush();
        }

        // if args[0] is '-', apply Burrows-Wheeler transform
        // if args[0] is '+', apply Burrows-Wheeler inverse transform
        public static void main(String[] args) {
                if (args[0].equals("-")) BurrowsWheeler.transform();
                if (args[0].equals("+")) BurrowsWheeler.inverseTransform();
        }
}