import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import java.util.*;

/**
 * BaseballElimination.java
 * 
 * Passos 1–5: Leitura de dados do arquivo, eliminação trivial e não-trivial
 *            implementadas com fluxo em rede.
 * Passo 6: Main para testes e análise de complexidade.
 *
 * Complexidade:
 * - n = número de times
 * - Número de pares de jogos: O(n^2)
 * - Vértices no fluxo: O(n^2)
 * - Arestas no fluxo: O(n^2)
 * - Complexidade do fluxo (Ford-Fulkerson): O(E * maxflow) na prática costuma ser eficiente,
 *   mas no pior caso teórico pode chegar a O(V * E^2) = O(n^6).
 */
public class BaseballElimination {
    private final int n;
    private final String[] names;
    private final int[] wins, losses, remaining;
    private final int[][] against;
    private final Map<String,Integer> idx;

    // Eliminação e certificados
    private final boolean[] eliminated;
    private final List<String>[] certificate;

    @SuppressWarnings("unchecked")
    public BaseballElimination(String filename) {
        // Passo 1: Leitura e parsing
        In in = new In(filename);
        this.n = in.readInt();
        names     = new String[n];
        wins      = new int[n];
        losses    = new int[n];
        remaining = new int[n];
        against   = new int[n][n];
        idx       = new HashMap<>();

        for (int i = 0; i < n; i++) {
            names[i]     = in.readString();
            idx.put(names[i], i);
            wins[i]      = in.readInt();
            losses[i]    = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < n; j++) {
                against[i][j] = in.readInt();
            }
        }
        in.close();

        // Inicialização dos arrays
        eliminated  = new boolean[n];
        certificate = (List<String>[]) new List[n];
        for (int i = 0; i < n; i++) {
            certificate[i] = new ArrayList<>();
        }

        // Passo 3: Eliminação trivial
        for (int i = 0; i < n; i++) {
            int maxWinPossible = wins[i] + remaining[i];
            for (int j = 0; j < n; j++) {
                if (wins[j] > maxWinPossible) {
                    eliminated[i] = true;
                    certificate[i].add(names[j]);
                }
            }
        }

        // Passos 4-5: Eliminação não-trivial via fluxo de rede
        for (int x = 0; x < n; x++) {
            if (eliminated[x]) continue;
            NetworkData nd = buildNetwork(x);
            FordFulkerson ff = new FordFulkerson(nd.net, nd.s, nd.t);
            if (ff.value() < nd.totalCapacity) {
                eliminated[x] = true;
                certificate[x] = new ArrayList<>();
                for (Map.Entry<Integer,Integer> entry : nd.teamVertex.entrySet()) {
                    if (ff.inCut(entry.getValue())) {
                        certificate[x].add(names[entry.getKey()]);
                    }
                }
            }
        }
    }

    // Estrutura auxiliar para rede de fluxo
    private static class NetworkData {
        FlowNetwork net;
        int s, t;
        double totalCapacity;
        Map<Integer,Integer> teamVertex;
    }

    // Passo 4: Constrói a rede de fluxo para time x
    private NetworkData buildNetwork(int x) {
        List<int[]> games = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            for (int j = i + 1; j < n; j++) {
                if (j == x) continue;
                if (against[i][j] > 0) games.add(new int[]{i, j});
            }
        }
        int gameCount = games.size();
        int teamCount = n - 1;
        int V = 2 + gameCount + teamCount;
        int s = 0, t = V - 1;
        FlowNetwork net = new FlowNetwork(V);
        double totalCap = 0;

        // Mapeamento de times
        Map<Integer,Integer> teamVertex = new HashMap<>();
        int idxV = 1 + gameCount;
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            teamVertex.put(i, idxV++);
        }

        // Arestas s -> jogo e jogo -> times
        for (int g = 0; g < games.size(); g++) {
            int[] pair = games.get(g);
            int gv = 1 + g;
            int cap = against[pair[0]][pair[1]];
            net.addEdge(new FlowEdge(s, gv, cap));
            totalCap += cap;
            net.addEdge(new FlowEdge(gv, teamVertex.get(pair[0]), Double.POSITIVE_INFINITY));
            net.addEdge(new FlowEdge(gv, teamVertex.get(pair[1]), Double.POSITIVE_INFINITY));
        }

        // Arestas times -> t
        for (Map.Entry<Integer,Integer> entry : teamVertex.entrySet()) {
            int teamIdx = entry.getKey();
            int cap = wins[x] + remaining[x] - wins[teamIdx];
            net.addEdge(new FlowEdge(entry.getValue(), t, Math.max(cap, 0)));
        }

        NetworkData nd = new NetworkData();
        nd.net = net;
        nd.s = s;
        nd.t = t;
        nd.totalCapacity = totalCap;
        nd.teamVertex = teamVertex;
        return nd;
    }

    // Passo 2: Métodos de acesso básicos
    private int checkTeam(String team) {
        Integer i = idx.get(team);
        if (i == null) throw new IllegalArgumentException("Team '" + team + "' not found");
        return i;
    }
    public int numberOfTeams()       { return n; }
    public Iterable<String> teams()  { return Collections.unmodifiableList(Arrays.asList(names)); }
    public int wins(String team)     { return wins[checkTeam(team)]; }
    public int losses(String team)   { return losses[checkTeam(team)]; }
    public int remaining(String team){ return remaining[checkTeam(team)]; }
    public int against(String t1, String t2) {
        return against[checkTeam(t1)][checkTeam(t2)];
    }

    // Passo 3-5: Métodos de eliminação
    public boolean isEliminated(String team) {
        return eliminated[checkTeam(team)];
    }
    public Iterable<String> certificateOfElimination(String team) {
        int i = checkTeam(team);
        if (!eliminated[i]) return null;
        return Collections.unmodifiableList(certificate[i]);
    }

    /**
     * Passo 6: Main para testes
     */
    public static void main(String[] args) {
        // Arquivos de teste no diretório project/baseball
        String dir = "project/baseball/";
        String[] tests = {"teams4.txt", "teams8.txt", "teams12.txt", "teams24.txt"};
        for (String file : tests) {
            String path = dir + file;
            System.out.println("==============================");
            System.out.println("Testing " + file);
            BaseballElimination be = new BaseballElimination(path);
            for (String team : be.teams()) {
                if (be.isEliminated(team)) {
                    System.out.print(team + " is eliminated by ");
                    for (String r : be.certificateOfElimination(team)) {
                        System.out.print(r + " ");
                    }
                    System.out.println();
                } else {
                    System.out.println(team + " is not eliminated");
                }
            }
        }
    }
}
