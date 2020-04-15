import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GraphRepository {

    public List<Node> findAll() {
        Connection connection = new DbConnection().getConnection();
        List<Node> graph = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM graph ");
            while (resultSet.next()) {
                graph.add(Node.builder().source(resultSet.getString("source"))
                        .destination(resultSet.getString("destination")).build());
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return graph;
    }



    private List<String> destinations(String source) {
        List<String> destinations = new ArrayList<>();
        for (Node node : findAll()) {
            if (node.getSource().equals(source)) {
                destinations.add(node.getDestination());
            }
        }
        return destinations;
    }



    public double[][] matrix(int len, String[] nodes) {
        List<String> destinations;
        double[][] M = new double[len][len];
        for (int i = 0; i < len; i++) {
            destinations = destinations(nodes[i]);
            int amount = destinations.size();
            for (int j = 0; j < len; j++) {
                if (destinations.contains(nodes[j])) {
                    M[i][j] = (double) 1 / amount;
                }
            }
        }
        return M;
    }
}
