import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;

public class BaseballElimination {
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] games;
    private ST<String, Integer> teams;
    private ST<Integer, Queue<String>> subsets;
    private final int numberOfTeams;

    public BaseballElimination(String filename) // create a baseball division from
        // given filename in format
        // specified below
    {
        In teamsIn = new In(filename);

        numberOfTeams = teamsIn.readInt();
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remaining = new int[numberOfTeams];
        games = new int[numberOfTeams][numberOfTeams];
        teams = new ST<String, Integer>();
        subsets = new ST<Integer, Queue<String>>();

        // Initialize internal structures
        for (int i = 0; i < numberOfTeams; i++) {
            teams.put(teamsIn.readString(), i);
            wins[i] = teamsIn.readInt();
            losses[i] = teamsIn.readInt();
            remaining[i] = teamsIn.readInt();

            for (int j = 0; j < numberOfTeams; j++) {
                games[i][j] = teamsIn.readInt();
            }
        }

        for (int team = 0; team < numberOfTeams; team++) {
            // Figure trivial eliminations
            for (String name : teams.keys()) {
                int other = teams.get(name);
                if (team != other && wins[team] + remaining[team] < wins[other]) {
                    Queue<String> subset = new Queue<String>();
                    subset.enqueue(name);
                    subsets.put(team, subset);
                    break;
                }
            }

            // Unless it was a trivial elimination
            if (!subsets.contains(team)) {
                FlowNetwork G = network(team);
                int s = G.V() - 2;
                int t = G.V() - 1;
                FordFulkerson maxflow = new FordFulkerson(G, s, t);

                for (FlowEdge e : G.adj(s)) {
                    // If there's still some capacity in the edge - then a team can be
                    // eliminated
                    if (Math.abs(e.flow() - e.capacity()) > 0) {
                        Queue<String> subset = new Queue<String>();
                        // Find a certificate of elimination
                        for (String name : teams.keys()) {
                            int other = teams.get(name);
                            if (team != other && maxflow.inCut(other)) {
                                subset.enqueue(name);
                            }
                        }
                        subsets.put(team, subset);
                        break;
                    }
                }
            }
        }
    }

    public int numberOfTeams() // number of teams
    {
        return numberOfTeams;
    }

    public Iterable<String> teams() // all teams
    {
        return teams.keys();
    }

    public int wins(String team) // number of wins for given team
    {
        if (!teams.contains(team))
            throw new IllegalArgumentException("No such team");

        return wins[teams.get(team)];
    }

    public int losses(String team) // number of losses for given team
    {
        if (!teams.contains(team))
            throw new IllegalArgumentException("No such team");

        return losses[teams.get(team)];
    }

    public int remaining(String team) // number of remaining games for given team
    {
        if (!teams.contains(team))
            throw new IllegalArgumentException("No such team");

        return remaining[teams.get(team)];
    }

    public int
        against(String team1,
                String team2) // number of remaining games between team1 and team2
    {
        if (!teams.contains(team1) || !teams.contains(team2))
            throw new IllegalArgumentException("No such team(s)");

        return games[teams.get(team1)][teams.get(team2)];
    }

    public boolean isEliminated(String team) // is given team eliminated?
    {
        if (!teams.contains(team))
            throw new IllegalArgumentException("No such team");

        return subsets.contains(teams.get(team));
    }

    public Iterable<String>
        certificateOfElimination(String team) // subset R of teams that eliminates
        // given team; null if not eliminated
    {
        if (!teams.contains(team))
            throw new IllegalArgumentException("No such team");

        if (!isEliminated(team))
            return null;

        return subsets.get(teams.get(team));
    }

    private FlowNetwork network(int team) {
        int num = (numberOfTeams - 1) * (numberOfTeams - 2) / 2;
        int total = 2 + numberOfTeams + num;
        int t = total - 1;
        int s = total - 2;
        int game = numberOfTeams - 1;
        FlowNetwork network = new FlowNetwork(total);

        for (int i = 0; i < numberOfTeams; i++) {
            if (i == team)
                continue;

            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == team)
                    continue;

                game++;
                // Edge from a source(s) to a game
                network.addEdge(new FlowEdge(s, game, games[i][j]));
                // Edge from a game to a team(s)
                network.addEdge(new FlowEdge(game, i, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(game, j, Double.POSITIVE_INFINITY));
            }

            // Edge from a team vertex to a sink(t)
            double capacity = wins[team] + remaining[team] - wins[i];
            network.addEdge(new FlowEdge(i, t, capacity));
        }
        return network;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
