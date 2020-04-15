import java.util.*;

public class PageRank {
    public static void main(String[] args) {

        GraphRepository repository = new GraphRepository();
        List<Node> graph = repository.findAll();

        Set<String> nodesSet = new HashSet<>();
        graph.forEach(node -> nodesSet.add(node.getSource()));
        String[] nodes = nodesSet.toArray(new String[0]);
        int len = nodesSet.size();


        double[][] M = repository.matrix(len, nodes);
        double[] v = new double[len];
        for (int i = 0; i < len; i++) {
            v[i] = (double) 1 / len;
        }

        for (int i = 0; i < 100; i++) {
            v = sum(M, v);
        }


        System.out.println(Arrays.toString(v));
    }

    private static double[] sum(double[][] M, double[] v) {
        int len = v.length;
        double[] res = new double[len];
        for (int i = 0; i < len; i++) {
            double summ = 0;
            for (int j = 0; j < len; j++) {
                summ += v[j] * M[j][i];
            }
            res[i] = summ;
        }

        return res;
    }


}
