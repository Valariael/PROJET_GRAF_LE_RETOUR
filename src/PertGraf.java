import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PertGraf extends Graf {

    @Override
    public String toString() {
        return super.toString();
    }

    /*public static PertGraf create(String path) throws FileNotFoundException, InvalidFormatException {
        PertGraf pert = new PertGraf();
        Set<Task> tasks = new HashSet<>();

        FileReader input = new FileReader(path);
        BufferedReader reader = new BufferedReader(input);
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length < 4) {
                    throw new InvalidFormatException();
                }
                String name = details[0];
                String label = details[1];
                int weight = 0;
                try {
                    weight = Integer.parseInt(details[2]);
                }catch (NumberFormatException e) {
                    throw new InvalidFormatException();
                }
                List<String> dependencies = new ArrayList<>();
                for (int i = 3; i < details.length; i++) {
                    dependencies.add(details[i]);
                }
                tasks.add(new Task(name, label, weight, dependencies));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Pas fini
    }*/

    /**
     * Computes a depth-first search starting at the vertex with the lowest 'id'.
     *
     * @return The List of Node objects in the order of the DFS.
     */
    @Override
    public List<Node> getDFS() {
        Node startingNode = Collections.min(this.adjList.keySet(), Comparator.comparing(Node::hashCode));

        return getDFS(startingNode);
    }

    /**
     * Computes a breadth-first search starting at the vertex with the lowest 'id'.
     *
     * @return The List of Node objects in the order of the BFS.
     */
    @Override
    public List<Node> getBFS() {
        Node startingNode = Collections.min(this.adjList.keySet(), Comparator.comparing(Node::hashCode));

        return getBFS(startingNode);
    }

    /**
     * Computes the representation of the graph in the DOT formalism.
     *
     * @return The String object containing the DOT representation of the graph.
     */
    @Override
    public String toDotString() {
        StringBuilder sb = new StringBuilder();

        List<Node> lonelyNodes = getAllNodes();

        sb.append("digraph g {\n");

        this.adjList.forEach((nodeFrom, nodeList) -> nodeList.forEach((nodeTo -> {
            lonelyNodes.remove(nodeFrom);
            lonelyNodes.remove(nodeTo);

            sb.append(" ");

            sb.append(nodeFrom.getName());
            sb.append(" -> ");
            sb.append(nodeTo.getName());

            if(nodeTo.isToWeightActivated()) {
                sb.append(" [label=");
                sb.append(nodeTo.getToLabel());
                sb.append("]");
            }

            sb.append(";\n");
        })));

        lonelyNodes.forEach(node -> sb.append(" ").append(node.getName()).append(";\n"));

        sb.append("}");

        return sb.toString();
    }
}
