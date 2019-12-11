import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class allows the creation of a directed graph as an adjacency list.
 * There are two ways to create a graph : empty with {@link #Graf()}
 * or from a successor array in the form of an 'int' array with {@link #Graf(int...)}.
 * See {@link UndirectedGraf} for the creation of undirected graphs.
 *
 * @author Axel Ledermann, Augustin Bordy
 */
public class Graf {
    /**
     *
     */
    HashMap<Node, ArrayList<Node>> adjList;

    /**
     * Directed graph constructor from a successor array.
     * It is represented as an adjacency list, contained in a HashMap.
     *
     * @param successorArray Successor array represented by an undefined number of 'int' values.
     */
    public Graf(int... successorArray) {
        adjList = new HashMap<>();

        int n = 1;
        // creating first node
        Node currentNode = new Node(n);
        adjList.put(currentNode, new ArrayList<Node>() {});

        // looping through succesor array to create the other nodes
        for (int nodeNumber : successorArray) {
            //create new node if zero read
            if(nodeNumber == 0) {
                n++;
                currentNode = new Node(n);
                adjList.put(currentNode, new ArrayList<Node>() {});
            //add node to adjacency list otherwise
            } else {
                ArrayList<Node> nodes = adjList.get(currentNode);
                nodes.add(new Node(nodeNumber));
            }
        }
    }

    /**
     * Default directed graph constructor.
     */
    public Graf() {
        adjList = new HashMap<>();
    }

    /**
     * Adds the vertex in parameter to the graph.
     *
     * @param node The Node object to be added to the graph.
     */
    public void addNode(Node node) {
        adjList.put(node, new ArrayList<>());
    }

    /**
     * Adds a new vertex to the graph, created from the int parameter.
     *
     * @param n The 'int' value to be used to create a new vertex.
     */
    public void addNode(int n) {
        Node node = new Node(n);
        adjList.put(node, new ArrayList<>());
    } // TODO remove ? or overload every method

    /**
     * Removes the vertex in parameter from the graph.
     *
     * @param node The Node object to be removed.
     */
    public void removeNode(Node node) {
        adjList.remove(node);

        //removing all incident edges
        this.adjList.forEach((nodeFrom, nodeList) -> nodeList.forEach((nodeTo) -> {
            if(nodeTo.equals(node)) nodeList.remove(nodeTo);
        }));
    }

    /**
     * Checks if the graph contains the Node instance in parameter.
     *
     * @param node The vertex to be searched.
     * @return 'true' if the vertex is present, 'false' otherwise.
     */
    public boolean containsNode(Node node) {
        return adjList.containsKey(node);
    }

    /**
     * Adds an edge from vertex 'from' to vertex 'to'.
     * Concretely, adds the vertex 'to' to the list associated in value of the vertex 'from'.
     *
     * @param from The Node object representing the head of the edge.
     * @param to The Node object representing the tail of the edge.
     */
    public void addEdge(Node from, Node to) {
        if (containsNode(from) && containsNode(to)) {
            ArrayList<Node> adjNodes = adjList.get(from);
            adjNodes.add(to);
        }
    }

    /**
     * Adds an edge to the graph from an Edge object.
     * Same concept as {@link #addEdge(Node, Node)} but using the attributes head and tail of the edge.
     *
     * @param edge The edge to be added.
     */
    public void addEdge(Edge edge) {
        if (containsNode(edge.getHead()) && containsNode(edge.getTail())) {
            adjList.get(edge.getTail()).add(edge.getHead());
        }
    }

    /**
     * Removes an edge from vertex 'from' to vertex 'to' if both are present in the graph.
     *
     * @param from The Node object representing the head of the edge.
     * @param to The Node object representing the tail of the edge.
     */
    public void removeEdge(Node from, Node to) {
        if (containsNode(from) && containsNode(to)) {
            adjList.get(from).remove(to);
        }
    }

    /**
     * Checks if there is an edge from vertex 'from' to vertex 'to'.
     * Only if both vertices are present in the graph.
     *
     * @param from The Node object representing the head of the edge.
     * @param to The Node object representing the tail of the edge.
     * @return 'true' if the edge exists, 'false' otherwise.
     */
    public boolean hasEdge(Node from, Node to) {
        if (containsNode(from) && containsNode(to)) {
            return adjList.get(from).contains(to);
        }

        return false;
    }

    /**
     * Gets the list of successors of the vertex in parameter.
     *
     * @param node The Node object representing the predecessor.
     * @return A List object containing all the successors.
     */
    public List<Node> getSuccessors(Node node) {
        return adjList.get(node);
    }

    /**
     * Gets a list of all edges leaving the vertex in parameter.
     *
     * @param node The Node object that will be searched.
     * @return A List object containing all the edges leaving vertex 'node'.
     */
    public List<Edge> getOutEdges(Node node) {
        List<Node> outNodes = adjList.get(node);
        List<Edge> outEdges = new ArrayList<>();
        for (Node n : outNodes) {
            outEdges.add(new Edge(node, n));
        }
        return outEdges;
    }

    /**
     * Gets a list of all edges entering the vertex in parameter.
     *
     * @param node The Node object that will be searched.
     * @return A List object containing all the edges entering vertex 'node'.
     */
    public List<Edge> getInEdges(Node node) {
        List<Edge> inEdges = new ArrayList<>();
        for (Map.Entry<Node, ArrayList<Node>> nodeEntry : adjList.entrySet()) {
            if (nodeEntry.getValue().contains(node)) {
                inEdges.add(new Edge(nodeEntry.getKey(), node));
            }
        }
        return inEdges;
    }

    /**
     * Gets a list of all edges incident to the vertex in parameter.
     *
     * @param node The Node object that will be searched.
     * @return A List object containing all the edges incident to the vertex 'node'.
     */
    public List<Edge> getIncidentEdges(Node node) {
        List<Edge> edges = getInEdges(node);
        edges.addAll(getOutEdges(node));

        return edges;
    }

    /**
     * Gets a list of all the vertices in the graph.
     *
     * @return A List object containing once each Node of the graph.
     */
    public List<Node> getAllNodes() {
        return new ArrayList<>(this.adjList.keySet());
    }

    /**
     * Gets a list of all the edges existing in the graph.
     *
     * @return A List object containing once each edge of the graph.
     */
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Map.Entry<Node, ArrayList<Node>> nodeEntry : adjList.entrySet()) {
            for (Node node : nodeEntry.getValue()) {
                if(node.isToWeightActivated()) {
                    edges.add(new Edge(nodeEntry.getKey(), node, node.getToLabel()));
                } else {
                    edges.add(new Edge(nodeEntry.getKey(), node));
                }
            }
        }

        return edges;
    }

    /**
     * Lists all the possible edges that could be created in this graph, including the non-existent ones.
     *
     * @return A list containing all the possible edges.
     */
    public List<Edge> getAllPossibleEdges() {
        List<Edge> possible_edges = new ArrayList<>();
        for (Node node_from : adjList.keySet()) {
            for (Node node_to : adjList.keySet()) {
                if (!node_from.equals(node_to)) {
                    possible_edges.add(new Edge(node_to, node_from));
                }
            }
        }
        return possible_edges;
    }

    /**
     * Creates the successor array of the graph as an array of 'int' values.
     *
     * @return The array of 'int' values representing the graph.
     */
    public int[] getSuccessorArray() {
        List<Node> nodes = new ArrayList<>(this.getAllNodes());
        List<Integer> list = new ArrayList<>();

        nodes.sort(Comparator.comparing(Node::getId));

        nodes.forEach(node -> {
            final List<Node> successors = this.adjList.get(node);
            successors.forEach(nodeTo -> list.add(nodeTo.getId()));
            list.add(0);
        });
        list.remove(list.size()-1);

        int[] array = new int[list.size()];
        for(int i = 0; i<list.size(); i++) array[i] = list.get(i);

        return array;
    }

    /**
     * Computes a representation of the graph as an adjacency matrix.
     *
     * @return The two-dimension array of 'int' values representing the adjacency matrix. Its size is the number of vertices in the graph.
     */
    public int[][] getAdjMatrix() {
        int nodeCount = this.adjList.keySet().size();
        int[][] matrix = new int[nodeCount][nodeCount];
        List<Node> nodes = new ArrayList<>(this.adjList.keySet());

        // sorting nodes in case there is a gap between node numbers
        nodes.sort(Comparator.comparing(Node::getId));

        // writes 1s if not weighted, writes the weight otherwise
        this.adjList.forEach((nodeFrom, nodeList) -> nodeList.forEach((nodeTo) -> {
            if(nodeTo.isToWeightActivated()) matrix[nodes.indexOf(nodeFrom)][nodes.indexOf(nodeTo)] = nodeTo.getToLabel();
            else matrix[nodes.indexOf(nodeFrom)][nodes.indexOf(nodeTo)] = 1;
        }));

        return matrix;
    }

    /**
     * Computes the reversed graph.
     * It is basically the same graph but with all its edges' direction inverted
     *
     * @return The reversed Graf object.
     */
    public Graf getReverseGraph() {
        Graf reverse = new Graf();

        // adding all nodes to the soon-to-be reversed graph
        for(Node n : this.getAllNodes()) reverse.addNode(new Node(n.getId()));

        // recreating all edges but with head and tail inverted
        this.adjList.forEach((nodeFrom, nodeList) -> nodeList.forEach((nodeTo) -> {
            nodeFrom.setToLabel(nodeTo.getToLabel());
            nodeFrom.setToWeightActivated(true);
            nodeTo.setToLabel(1);
            nodeTo.setToWeightActivated(false);
            reverse.addEdge(nodeTo, nodeFrom);
        }));

        return reverse;
    }

    /**
     * Computes the transitive closure of the graph.
     * It is the same graph but with an edge added between each vertices that has a path to another.
     *
     * @return The Graf object representing the transitive closure of the graph.
     */
    public Graf getTransitiveClosure() {
        Graf t = this;

        // we use here the Roy-Warshall algorithm
        for(Node n : adjList.keySet()) {
            List<Edge> inEdges = t.getInEdges(n);

            for(Edge i : inEdges) {
                List<Edge> outEdges = t.getOutEdges(n);

                for(Edge o : outEdges) {
                    t.addEdge(i.getHead(), o.getTail());
                }
            }
        }

        return t;
    }


    /**
     * Computes a depth-first search starting at the vertex with the lowest 'id'.
     *
     * @return The List of Node objects in the order of the DFS.
     */
    public List<Node> getDFS() {
        Node startingNode = Collections.min(this.adjList.keySet(), Comparator.comparing(Node::getId));

        return getDFS(startingNode);
    }

    /**
     * Computes a depth-first search starting at the vertex given in parameter.
     *
     * @param startingNode The Node object at which the DFS starts.
     * @return The List of Node objects in the order of the DFS.
     */
    public List<Node> getDFS(Node startingNode) {
        // executing standard DFS if the parameter is 'null'.
        if(startingNode == null) {
            return getDFS();
        }

        List<Node> visited = new ArrayList<>();
        Stack<Node> stack = new Stack<>();

        stack.push(startingNode);

        while(!stack.isEmpty()) {
            Node currentNode = stack.pop();

            if(!visited.contains(currentNode)) {
                visited.add(currentNode);

                for(Node n : this.adjList.get(currentNode)) {
                    stack.push(n);
                }
            }
        }

        return visited;
    }

    /**
     * Computes a breadth-first search starting at the vertex with the lowest 'id'.
     *
     * @return The List of Node objects in the order of the BFS.
     */
    public List<Node> getBFS() {
        Node startingNode = Collections.min(this.adjList.keySet(), Comparator.comparing(Node::getId));

        return getBFS(startingNode);
    }

    /**
     * Computes a breadth-first search starting at the vertex given in parameter.
     *
     * @param startingNode The Node object at which the BFS starts.
     * @return The List of Node objects in the order of the BFS.
     */
    public List<Node> getBFS(Node startingNode) {
        // executing standard BFS if the parameter is 'null'.
        if(startingNode == null) {
            startingNode = Collections.min(this.adjList.keySet(), Comparator.comparing(Node::getId));
        }

        List<Node> visited = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(startingNode);
        visited.add(startingNode);

        while(!queue.isEmpty()) {
            Node currentNode = queue.poll();

            for(Node n : this.adjList.get(currentNode)) {
                if(!visited.contains(n)) {
                    visited.add(n);
                    queue.add(n);
                }
            }
        }

        return visited;
    }

    /**
     * Computes the representation of the graph in the DOT formalism.
     *
     * @return The String object containing the DOT representation of the graph.
     */
    public String toDotString() {
        StringBuilder sb = new StringBuilder();

        List<Node> lonelyNodes = getAllNodes();

        sb.append("digraph g {\n");

        this.adjList.forEach((nodeFrom, nodeList) -> nodeList.forEach((nodeTo -> {
            lonelyNodes.remove(nodeFrom);
            lonelyNodes.remove(nodeTo);

            sb.append(" ");

            sb.append(nodeFrom.getId());
            sb.append(" -> ");
            sb.append(nodeTo.getId());

            if(nodeTo.isToWeightActivated()) {
                sb.append(" [label=");
                sb.append(nodeTo.getToLabel());
                sb.append("]");
            }

            sb.append(";\n");
        })));

        lonelyNodes.forEach(node -> sb.append(" ").append(node.getId()).append(";\n"));

        sb.append("}");

        return sb.toString();
    }

    public void generateRender(String name) throws IOException, InterruptedException {
        File renderingDirectory = new File("renders");
        if (!renderingDirectory.exists()) {
            renderingDirectory.mkdir();
        }
        String tempPath = name + ".dot";
        toDotFile("renders/" + tempPath);
        ProcessBuilder pb = new ProcessBuilder("dot", "-O", tempPath, "-Tpng");
        pb.directory(renderingDirectory.getAbsoluteFile());
        Process process;
        try {
            process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            (new File("renders/"+tempPath)).delete();
            throw e;
        }
        (new File("renders/"+tempPath+".png")).renameTo(new File("renders/"+name+".png"));
        (new File("renders/"+tempPath)).delete();
    }

    /**
     * Writes the representation in the DOT formalism of the graph to a file.
     *
     * @param path The path to the target file.
     * @throws IOException In case of error when trying to open the file or to write on it.
     */
    public void toDotFile(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(toDotString());
        writer.close();
    }

    /**
     * Generates a random Directed Acyclic Graph.
     *
     * @param size The number of nodes in the generated graph.
     * @param edgeProbability A value between 0 and 1 setting the probablity to have more or less edges in the generated graph.
     * @return A random Dag instance.
     */
    public static Graf randomDagBuilder(int size, double edgeProbability) {

        Graf randomDag = new Graf();
        List<List<Node>> floors = new ArrayList<>();

        int depth = 0;
        int randomFloor;

        for (int i = 0; i < size; i++) { // putting the nodes into the floors
            Node newNode = new Node(i);
            randomDag.addNode(newNode);
            randomFloor = (int) (Math.random() * size);
            if (randomFloor >= depth) {
                floors.add(new ArrayList<>());
                floors.get(depth).add(newNode);
                depth++;
            }
            else {
                floors.get(randomFloor).add(newNode);
            }
        }

        int nextFloorIndex = 0;
        List<Node> nextFloor;
        for (List<Node> floor : floors) {
            nextFloorIndex++;
            if (nextFloorIndex == depth) {
                break;
            }
            for (Node node : floor) {
                while (Math.random() < edgeProbability) {
                    nextFloor = floors.get(nextFloorIndex + (int) (Math.random() * (depth - nextFloorIndex)));
                    Node nextNode = nextFloor.get((int)(Math.random() * nextFloor.size()));
                    if (randomDag.hasEdge(node, nextNode)) {
                        break;
                    }
                    randomDag.addEdge(node, nextNode);
                }
            }
        }

        return randomDag;
    }

    /**
     * Generates a random Directed Graph from specifications.
     *
     * @param size The number of nodes in the generated graph.
     * @param density A value between 0 and 1 setting being the ratio : number of edges in the generated graph / maximum number of edges with the chosen number of nodes.
     * @param strongly_connected Define whether the strong connection if forced or not, meaning we can go from each node to every other nodes.
     * @return A random directed graph instance.
     */
    public static Graf randomGrafBuilder(int size, double density, boolean strongly_connected) {

        Graf randomGraf = new Graf();

        List<Edge> possible_edges;

        if (strongly_connected) { // create a path containing all vertices
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
            Node firstNode = currentNode;  // Keep the first in memory to loop at the end
            unvisited.remove(currentNode);
            unvisitedSize--;
            while (unvisitedSize > 0) {
                int randomNextIndex = (int) (Math.random() * unvisitedSize);
                Node randomNextNode = unvisited.get(randomNextIndex);
                randomGraf.addEdge(currentNode, randomNextNode);
                unvisited.remove(randomNextNode);
                possible_edges.remove(new Edge(randomNextNode, currentNode));
                currentNode = randomNextNode;
                unvisitedSize--;
            }
            if (size > 1) {
                randomGraf.addEdge(currentNode, firstNode);  // Loop to the first node
                possible_edges.remove(new Edge(firstNode, currentNode));
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
        int needed_edges = (int) (size * (size - 1) * density);
        if (strongly_connected) {
            needed_edges = needed_edges - size;
        }
        if (needed_edges < 1) {
            return randomGraf;
        }

        int possible_edges_nb = possible_edges.size();
        while (needed_edges > 0) {
            Edge randomEdge = possible_edges.get((int)(Math.random() * possible_edges_nb));
            randomGraf.addEdge(randomEdge);
            System.out.println("Add: " + randomEdge.getTail().getId() + " -> " + randomEdge.getHead().getId());
            possible_edges.remove(randomEdge);
            possible_edges_nb--;
            needed_edges--;
        }

        return randomGraf;
    }

    /**
     * Computes the shortest path from one node to another using the algorithm of Bellman-Ford.
     *
     * @param startNode The Node object where the path should start.
     * @param finalNode The Node object where the path should end.
     * @return A ShortestPathInfo object containing the list of Node which is the shortest path and a Boolean at 'true' if there are negative cycles.
     */
    public ShortestPathInfo<Deque<Node>, Boolean, Integer> shortestPath(Node startNode, Node finalNode) {
        Deque<Node> shortestPath = new LinkedList<>();
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();
        List<Edge> allEdges = this.getAllEdges();
        int numberOfNodes = this.adjList.keySet().size();

        // init
        this.adjList.forEach((node, successors) -> {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        });
        distances.put(startNode, 0);
        predecessors.put(startNode, startNode);
        int iter = 1;
        boolean modified = true;

        // processing shortest paths
        while(iter < numberOfNodes && modified) {
            modified = false;

            for(Edge e : allEdges) {
                if(distances.get(e.getTail()) > distances.get(e.getHead()) + e.getWeight()) {
                    distances.put(e.getTail(), distances.get(e.getHead()) + e.getWeight());
                    predecessors.put(e.getTail(), e.getHead());
                    modified = true;
                }
            }
        }

        // rebuilding shortest path from start to end
        Node currentNode = finalNode;
        int distance = 0;
        while(!predecessors.get(currentNode).equals(currentNode)) {
            shortestPath.addFirst(currentNode);
            distance += distances.get(currentNode);
            currentNode = predecessors.get(currentNode);
        }
        shortestPath.addFirst(currentNode);
        if(!currentNode.equals(startNode)) return null;

        return new ShortestPathInfo<>(shortestPath, iter <= numberOfNodes+1, distance);
    }

    /**
     * Gives a String representation in the form of an adjacency list.
     * @return A String object representing the Graf.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("printing digraph :\n");
        if(adjList.isEmpty()) sb.append(" - empty - ");
        adjList.forEach((key, value) -> {
            sb.append(key.toString());
            sb.append(" |");
            value.forEach(node -> {
                sb.append(" -> ");
                sb.append(node.toString());
            });
            sb.append("\n");
        });

        return sb.toString();
    }
}
