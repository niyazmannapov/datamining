import java.util.*;

public class PageRankIter {
    public static void main(String[] args) {

        GraphRepository repository = new GraphRepository();
        List<Node> graph = repository.findAll();
        final double d = 0.85;

        Set<String> nodesSet = new HashSet<>();
        graph.forEach(node -> nodesSet.add(node.getSource()));
        String[] nodes = nodesSet.toArray(new String[0]);

        Map<String, Double> prank = new HashMap<>();
        for (String node: nodes) {
            prank.put(node, 0d);
        }
        Map<String, Integer> count = count(nodes, graph);
        Map <String, List<String>> dests = destinations(nodes, graph);
        int len = nodesSet.size();

        while (check(prank.values().toArray(new Double[0]))) {
            for (String node: nodes) {
                double sum = 0;
                for (String src : dests.get(node)) {
                    sum += prank.get(src) / count.get(src);
                }
                double k = 1 - d + d * (sum);
                prank.replace(node, 1 - d + d * (sum));
            }
        }

        System.out.println(prank.toString());

    }


    private static boolean check(Double[] v) {
        double eps = 1e-15;
        double sum = 0;
        for (double aV : v) {
            sum += aV;
        }
        return (1 - sum/v.length) > eps;
    }

    private static Map<String, List<String>> destinations(String[] nodes, List<Node> graph) {
        Map <String, List<String>> map = new HashMap<>();
        for (String node : nodes) {
            for (Node el : graph) {
                if (el.getDestination().equals(node)) {
                    if (!map.containsKey(node)) {
                        map.put(node, new ArrayList<>());
                    }
                    map.get(node).add(el.getSource());
                }
            }
        }
        return map;
    }

    private static Map<String, Integer> count(String[] nodes, List<Node> graph) {
        Map <String, Integer> map = new HashMap<>();

        for (String node : nodes) {
            int count = 0;
            for (Node el : graph) {
                if (el.getSource().equals(node)) {
                    count ++;
                }
            }
            map.put(node, count);

        }
        return map;
    }

}
