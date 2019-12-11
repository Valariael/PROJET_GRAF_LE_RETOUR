import java.util.ArrayList;
import java.util.List;

public class UndirectedGraf extends Graf {

    @Override
    public String toDotString() {
        StringBuilder sb = new StringBuilder();

        List<Node> lonelyNodes = getAllNodes();

        sb.append("graph g {\n");

        this.adjList.forEach((nodeFrom, nodeList) -> nodeList.forEach((nodeTo -> {
            lonelyNodes.remove(nodeFrom);
            lonelyNodes.remove(nodeTo);

            sb.append(" ");

            sb.append(nodeFrom.getId());
            sb.append(" -- ");
            sb.append(nodeTo.getId());

            if(nodeTo.isToWeightActivated()) {
                sb.append(" [label=");
                sb.append(nodeTo.getToLabel());
                sb.append("]");
            }

            sb.append(";\n");
        })));

        lonelyNodes.forEach(node -> {
            sb.append(" ").append(node.getId()).append(";\n");
        });

        sb.append("}");

        return sb.toString();
    }

    /**
     * Generates a random Undirected Graph from specifications.
     *
     * @param size The number of nodes in the generated graph.
     * @param density A value between 0 and 1 setting being the ratio : number of edges in the generated graph / maximum number of edges with the chosen number of nodes.
     * @param connected Define whether the connection if forced or not, meaning we can go from each node to every other nodes.
     * @return A random undirected graph instance.
     */
    public static UndirectedGraf randomGrafBuilder(int size, double density, boolean connected) {

        UndirectedGraf randomGraf = new UndirectedGraf();

        List<Edge> possible_edges;

        if (connected) { // create a path containing all vertices
            List<Node> unvisited = new ArrayList<>();
            for (int i = 0; i < size; i++) { // Adding vertices to the graf and to the unvisited list, used to trace the path
                Node newNode = new Node(i+1);
                randomGraf.addNode(newNode);
                unvisited.add(newNode);
            }
            possible_edges = randomGraf.getAllPossibleEdges();
            int unvisitedSize = unvisited.size();
            int firstIndex = (int) (Math.random() * unvisitedSize);
            Node currentNode = unvisited.get(firstIndex);
            unvisited.remove(currentNode);
            unvisitedSize--;
            while (unvisitedSize > 0) {
                int randomNextIndex = (int) (Math.random() * unvisitedSize);
                Node randomNextNode = unvisited.get(randomNextIndex);
                randomGraf.addEdge(currentNode, randomNextNode);
                unvisited.remove(randomNextNode);
                possible_edges.remove(new Edge(currentNode, randomNextNode));
                possible_edges.remove(new Edge(randomNextNode, currentNode));
                currentNode = randomNextNode;
                unvisitedSize--;
            }
        }
        else {
            for (int i = 0; i < size; i++) { // Just adding vertices to the graf
                Node newNode = new Node(i+1);
                randomGraf.addNode(newNode);
            }
            possible_edges = randomGraf.getAllPossibleEdges();
        }

        if (density < 0) {
            density = 0;
        }
        if (density > 1) {
            density = 1;
        }
        int needed_edges = (int) (size * (size - 1) * density / 2);
        if (connected) {
            needed_edges = needed_edges - size + 1;  // Because not a loop
        }
        if (needed_edges < 1) {
            return randomGraf;
        }

        int possible_edges_nb = possible_edges.size();
        while (needed_edges > 0) {
            Edge randomEdge = possible_edges.get((int)(Math.random() * possible_edges_nb));
            randomGraf.addEdge(randomEdge);
            possible_edges.remove(randomEdge);
            possible_edges_nb--;
            needed_edges--;
        }

        return randomGraf;

    }

    /**
     * Lists all the possible edges that could be created in this graph, including the non-existent ones.
     *
     * @return A list containing all the possible edges.
     */
    @Override
    public List<Edge> getAllPossibleEdges() {
        List<Edge> possible_edges = new ArrayList<>();
        for (Node node_from : adjList.keySet()) {
            for (Node node_to : adjList.keySet()) {
                if (!(possible_edges.contains(new Edge(node_to, node_from)) || node_from.equals(node_to))) {
                    possible_edges.add(new Edge(node_from, node_to));
                }
            }
        }
        System.out.println("size:" + possible_edges.size());
        return possible_edges;
    }

    /**
     * Gives a String representation in the form of an adjacency list.
     * @return A String object representing the UndirectedGraf.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("printing graph :");
        if(adjList.isEmpty()) sb.append(" - empty - ");
        adjList.forEach((key, value) -> {
            sb.append(key.toString());
            sb.append(" | ");
            value.forEach(node -> {
                sb.append(" -> ");
                sb.append(node.toString());
            });
        });

        return sb.toString();
    }
}
