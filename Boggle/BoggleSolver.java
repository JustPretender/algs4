import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver
{
        private SET<String> words;
        private int Rows;
        private int Cols;
        private boolean[][] marked;

        private static final int R = 26;        // A-Z


        private Node root;      // root of trie
        private int n;          // number of keys in trie
        private static final int OFF = 65;

        // R-way trie node
        private static class Node {
                private int val = -1;
                private Node[] next = new Node[R];
        }

        // Initializes the data structure using the given array of strings as the dictionary.
        // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
        public BoggleSolver(String[] dictionary) {
                for (String word : dictionary) {
                        if (word.length() <= 2)
                                dictAdd(word, 0);
                        else if (word.length() <= 4)
                                dictAdd(word, 1);
                        else if (word.length() <= 5)
                                dictAdd(word, 2);
                        else if (word.length() <= 6)
                                dictAdd(word, 3);
                        else if (word.length() <= 7)
                                dictAdd(word, 5);
                        else
                                dictAdd(word, 11);
                }
        }

        // Returns the set of all valid words in the given Boggle board, as an Iterable.
        public Iterable<String> getAllValidWords(BoggleBoard board) {
                Rows = board.rows();
                Cols = board.cols();
                words = new SET<String>();
                for (int i = 0; i < Rows; i++) {
                        for (int j = 0; j < Cols; j++) {
                                marked = new boolean[Rows][Cols];
                                dfs(board, i, j, root, new String());
                        }
                }
                return words;
        }
        // Returns the score of the given word if it is in the dictionary, zero otherwise.
        // (You can assume the word contains only the uppercase letters A through Z.)
        public int scoreOf(String word) {
                if (dictContains(word))
                        return dictGet(word);
                else return 0;
        }

        private void dfs(BoggleBoard board, int i, int j, Node prefix, String word) {
                char letter = board.getLetter(i,j);

                word += (letter == 'Q') ? "QU" : letter;

                prefix = prefix.next[letter-OFF];
                if (prefix == null) {
                        return;
                }

                if (letter == 'Q') {
                        if (prefix.next['U'-OFF] != null) {
                                prefix = prefix.next['U'-OFF];
                        } else {
                                return;
                        }
                }

                if (word.length() >= 3 && prefix.val > 0) {
                        words.add(word);
                }

                marked[i][j] = true;
                for (int row = i - 1; row <= i + 1; row++) {
                        for (int col = j - 1; col <= j + 1; col++) {
                                if (row >= 0 && row < Rows
                                    && col >= 0 && col < Cols
                                    && !(row == i && col == j)
                                    && (!marked[row][col])) {
                                        dfs(board, row, col, prefix, word);
                                }
                        }
                }
                marked[i][j] = false;
        }

        private void dictAdd(String key, int val) {
                root = dictAdd(root, key, val, 0);
        }

        private Node dictAdd(Node x, String key, int val, int d) {
                if (x == null) x = new Node();
                if (d == key.length()) {
                        if (x.val == -1) n++;
                        x.val = val;
                        return x;
                }
                char c = key.charAt(d);
                x.next[c - OFF] = dictAdd(x.next[c - OFF], key, val, d+1);
                return x;
        }

        private int dictGet(String key) {
                if (key == null) throw new IllegalArgumentException("argument to get() is null");
                Node x = dictGet(root, key, 0);
                if (x == null) return -1;
                return x.val;
        }

        private boolean dictContains(String key) {
                if (key == null) throw new IllegalArgumentException("argument to contains() is null");
                return dictGet(key) != -1;
        }

        private Node dictGet(Node x, String key, int d) {
                if (x == null) return null;
                if (d == key.length()) return x;
                char c = key.charAt(d);
                return dictGet(x.next[c-OFF], key, d+1);
        }

        public static void main(String[] args) {
                In in = new In(args[0]);
                String[] dictionary = in.readAllStrings();
                BoggleSolver solver = new BoggleSolver(dictionary);
                BoggleBoard board = new BoggleBoard(args[1]);
                int score = 0;
                for (String word : solver.getAllValidWords(board)) {
                        StdOut.println(word);
                        score += solver.scoreOf(word);
                }
                StdOut.println("Score = " + score);
        }
}
