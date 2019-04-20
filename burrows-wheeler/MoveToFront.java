import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
        private static final int RADIX = 256;

        // apply move-to-front encoding, reading from standard input and writing to standard output
        public static void encode() {
                char[] alphabet = new char[RADIX]; // Extended ASCII
                for (char i = 0; i < alphabet.length; i++) {
                        alphabet[i] = i;
                }

                while (!BinaryStdIn.isEmpty()) {
                        char c = BinaryStdIn.readChar();

                        // First look for a match starting from the end of the alphabet
                        int index = 0;
                        for (; index < RADIX && !(c == alphabet[index]); index++);
                        // Output an index of a matching character
                        BinaryStdOut.write((char) index);
                        // Shift the alphabet right
                        System.arraycopy(alphabet, 0, alphabet, 1, index);
                        // and move the character to front
                        alphabet[0] = c;
                }

                BinaryStdOut.close();
        }

        // apply move-to-front decoding, reading from standard input and writing to standard output
        public static void decode() {
                char[] alphabet = new char[RADIX]; // Extended ASCII
                for (char i = 0; i < alphabet.length; i++) {
                        alphabet[i] = i;
                }

                while (!BinaryStdIn.isEmpty()) {
                        int index = BinaryStdIn.readChar();
                        char c = alphabet[index];

                        // Output a matching character
                        BinaryStdOut.write(c);
                        // Shift the alphabet right
                        while (index > 0) {
                                alphabet[index] = alphabet[--index];
                        }
                        // and move the character to front
                        alphabet[0] = c;
                }

                BinaryStdOut.close();
                BinaryStdIn.close();
        }

        // if args[0] is '-', apply move-to-front encoding
        // if args[0] is '+', apply move-to-front decoding
        public static void main(String[] args) {
                if (args[0].equals("-")) MoveToFront.encode();
                if (args[0].equals("+")) MoveToFront.decode();
        }
}