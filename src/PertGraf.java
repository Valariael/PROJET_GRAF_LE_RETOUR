import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PertGraf extends Graf {
    static PertGraf create(String path) throws FileNotFoundException, InvalidFormatException {
        PertGraf pert = new PertGraf();
        Set<TaskRaw> tasks = new HashSet<>();

        FileReader input = new FileReader(path);
        BufferedReader reader = new BufferedReader(input);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] details = line.trim().split(",");
                if (details.length < 4) {
                    throw new InvalidFormatException();
                }
                String name = details[0].trim();
                String label = details[1].trim();
                int weight;
                try {
                    weight = Integer.parseInt(details[2].trim());
                } catch (NumberFormatException e) {
                    throw new InvalidFormatException();
                }
                Set<String> dependencies = new HashSet<>();
                for (int i = 3; i < details.length; i++) {
                    if (details[i].trim().equals("-")) {
                        continue;
                    }
                    dependencies.add(details[i].trim());
                }
                tasks.add(new TaskRaw(name, label, weight, dependencies));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (TaskRaw task : tasks) {
            pert.addNode(new Task(task.getName(), task.getLabel()));
        }
        
        for (TaskRaw task : tasks) {
            for (TaskRaw taskNext : tasks) {
                if (task == taskNext) {
                    continue;
                }
                if (taskNext.getDependencies().contains(task.getName())) {
                    Task fromTask = new Task(task.getName());
                    Task toTask = new Task(taskNext.getName(), taskNext.getLabel(), task.getWeight());
                    toTask.setToWeightActivated(true);
                    pert.addEdge(fromTask, toTask);
                }
            }
        }

        return pert; // Pas fini
    }

    List<Task> computeListScheduling(int numberOfWorkers) {
        HashSet<Task> working = new HashSet<>();
        HashSet<Task> done = new HashSet<>();
        HashSet<Node> remaining = new HashSet<>(this.adjList.keySet());
        LinkedList<Task> scheduling = new LinkedList<>();

        while(!remaining.isEmpty()) {
            while(working.size() < numberOfWorkers && remaining.size() > working.size()) {
                Task toWork = getHighestPriorityTask(
                        getPendingTasks(done, working)
                );
                working.add(toWork);
                scheduling.add(toWork);
            }

            for(Task t : working) {
                t.incrementWorkedTimes();
                if(t.getWorkedTimes() == t.getDuration()) {
                    working.remove(t);
                    remaining.remove(t);
                    done.add(t);
                }
            }
        }

        return scheduling;
    }

    Set<Node> getPendingTasks(HashSet<Task> done, HashSet<Task> working) {
        HashSet<Node> availableTasks = new HashSet<>(this.adjList.keySet());

        for (Map.Entry<Node, ArrayList<Node>> entry : this.adjList.entrySet()) {
            Node nodeFrom = entry.getKey();
            ArrayList<Node> nodeList = entry.getValue();
            nodeList.removeAll(done);

            if (done.contains((Task) nodeFrom)) continue;

            availableTasks.removeAll(nodeList);
        }

        availableTasks.removeAll(done);
        availableTasks.removeAll(working);

        return availableTasks;
    }

    boolean checkPertIsTree(){return true;} //TODO implement ? or not ? or checkTaskValid

    Task getHighestPriorityTask(Set<Node> pending) {
        //TODO : longest paths distance might be wrong
        //TODO : add final node to pert to use time of last task
        Map<Deque<Node>, Integer> longestPaths = new HashMap<>();

        for (Node currentTask : pending) {
            LongestPathInfo<Deque<Node>, Integer> currentLongestPath = computeLongestPathFrom(currentTask);
            longestPaths.put(currentLongestPath.list, currentLongestPath.dist);
        }

        //take the longest of the longest paths
        Map.Entry<Deque<Node>, Integer> longestEntry = null;
        for (Map.Entry<Deque<Node>, Integer> entry : longestPaths.entrySet()) {
            if (longestEntry == null || entry.getValue().compareTo(longestEntry.getValue()) > 0) {
                longestEntry = entry;
            }
        }

        //return its first task
        if (longestEntry != null) {
            return (Task) longestEntry.getKey().getFirst();
        }
        return null;
    }

    LongestPathInfo<Deque<Node>, Integer> computeLongestPathFrom(Node startingNode) {
        List<Edge> allEdges = this.getAllEdges();
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();
        int numberOfNodes = this.adjList.keySet().size();
        Deque<Node> longestPath = new LinkedList<>();

        // init Bellman-Ford
        this.adjList.forEach((node, successors) -> {
            distances.put(node, Integer.MIN_VALUE);
            predecessors.put(node, null);
        });
        distances.put(startingNode, 0);
        predecessors.put(startingNode, startingNode);
        int iter = 1;
        boolean modified = true;

        // processing shortest paths
        while (iter < numberOfNodes && modified) {
            modified = false;

            for (Edge e : allEdges) {
                if (distances.get(e.getTail()) < distances.get(e.getHead()) + e.getWeight()) {
                    distances.put(e.getTail(), distances.get(e.getHead()) + e.getWeight());
                    predecessors.put(e.getTail(), e.getHead());
                    modified = true;
                }
            }

            iter++;
        }

        // rebuilding longest path from start to end, ending at the furthest
        Map.Entry<Node, Integer> maxEntry = null;
        for (Map.Entry<Node, Integer> entry : distances.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        Node currentNode;
        if (maxEntry != null) {
            currentNode = maxEntry.getKey();
            int distance = 0;
            while (!predecessors.get(currentNode).equals(currentNode)) {
                longestPath.addFirst(currentNode);
                distance += distances.get(currentNode);
                currentNode = predecessors.get(currentNode);
            }

            return new LongestPathInfo<>(longestPath, distance);
        }

        return null;
    }

    /**
     * Gets a list of all the edges existing in the PERT graph.
     *
     * @return A List object containing once each edge of the PERT graph.
     */
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Map.Entry<Node, ArrayList<Node>> nodeEntry : adjList.entrySet()) {
            for (Node node : nodeEntry.getValue()) {
                if(node.isToWeightActivated()) {
                    edges.add(new PertEdge(nodeEntry.getKey(), node, node.getToLabel()));
                } else {
                    edges.add(new PertEdge(nodeEntry.getKey(), node));
                }
            }
        }

        return edges;
    }

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

    @Override
    public String toString() {
        return super.toString();
    }
}
