import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordnet;

    public Outcast(WordNet wordnet) {         // constructor takes a WordNet object
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {   // given an array of WordNet nouns, return an outcast
        String outcast = "";
        int maxDist = Integer.MIN_VALUE;

        for (String noun : nouns) {
            int dist = 0;
            for (String s : nouns) {
                dist += wordnet.distance(noun, s);
            }
            if (maxDist < dist) {
                maxDist = dist;
                outcast = noun;
            }
        }

        return outcast;
    }

    public static void main(String[] args) { // see test client below
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
