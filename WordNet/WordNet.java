import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.DirectedCycle;

public class WordNet {
    private SAP sap;
    private ST<String, SET<Integer>> nouns;
    private ST<Integer, String> ids;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (null == synsets || null == hypernyms)
            throw new NullPointerException("an argument is null");

        In synsetsIn = new In(synsets);
        nouns = new ST<String, SET<Integer>>();
        ids = new ST<Integer, String>();
        while (!synsetsIn.isEmpty()) {
            String[] fields = synsetsIn.readLine().split(",");
            int id = Integer.parseInt(fields[0]);

            for (String s : fields[1].split(" ")) {
                SET<Integer> set = nouns.get(s);
                if (null == set)
                    set = new SET<Integer>();

                set.add(id);
                nouns.put(s, set);
            }

            ids.put(id, fields[1]);
        }

        Digraph G = new Digraph(ids.size());
        In hypernymsIn = new In(hypernyms);
        while (!hypernymsIn.isEmpty()) {
            String[] fields = hypernymsIn.readLine().split(",");
            int id = Integer.parseInt(fields[0]);
            for (int i = 1; i < fields.length; i++) {
                G.addEdge(id, Integer.parseInt(fields[i]));
            }
        }

        DirectedCycle cycle = new DirectedCycle(G);
        if (cycle.hasCycle())
            throw new IllegalArgumentException();

        int roots = 0;
        for (int v = 0; v < G.V(); v++) {
            if (!G.adj(v).iterator().hasNext()) {
                ++roots;
            }
        }

        if (roots != 1)
            throw new IllegalArgumentException("Not a rooted DAG");

        sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (null == word)
            throw new NullPointerException("an argument is null");

        return nouns.get(word) != null;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("an illegal argument!");

        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("an illegal argument!");

        return ids.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        String nounA = args[2];
        String nounB = args[3];

        StdOut.println("Ancestor: " + wordnet.sap(nounA, nounB));

    }
}
