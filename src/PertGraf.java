import java.io.*;
import java.util.*;

public class PertGraf extends Graf implements Cloneable {
    private static final String PERT_START_NODE = "starting_node";
    private static final String PERT_END_NODE = "ending_node";
    private static final String DOT_SEPARATOR = "==";

    private static PertGraf pertInstance = null;

    private PertGraf() {}

    static PertGraf getInstance() {
        if (pertInstance == null)
        {   pertInstance = new PertGraf();
        }
        return pertInstance;
    }

    private static PertGraf newInstance() {
        return new PertGraf();
    }

    static void resetInstance() {
        pertInstance = new PertGraf();
    }

    static void setInstance(PertGraf p) {
        pertInstance = p;
    }

    static PertGraf createFromPertFile(String path) throws FileNotFoundException, InvalidFormatException {
        Set<TaskRaw> tasks = new HashSet<>();

        FileReader input = new FileReader(path);
        BufferedReader reader = new BufferedReader(input);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                tasks.add(computePertLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return createPertGrafFromTaskRawList(tasks); // TODO : Pas fini >> AH!
    }

    static PertGraf createFromDotFile(String path) throws FileNotFoundException, IndexOutOfBoundsException {
        StringBuilder sb = new StringBuilder();

        FileReader input = new FileReader(path);
        BufferedReader reader = new BufferedReader(input);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return createPertGrafFromDotString(sb.toString()); // TODO : Pas fini >> AH!
    }

    static PertGraf createPertGrafFromPertString(String pertString) {
        String[] pertStringLines = pertString.split("\n");
        Set<TaskRaw> tasks = new HashSet<>();

        try {
            for (String pertStringLine : pertStringLines) {
                tasks.add(computePertLine(pertStringLine));
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            return null;
        }

        return createPertGrafFromTaskRawList(tasks);
    }

    private static PertGraf createPertGrafFromTaskRawList(Set<TaskRaw> tasks) {
        PertGraf pert = new PertGraf();

        for (TaskRaw task : tasks) {
            if(task == null) continue;
            pert.addNode(new Task(task.getName(), task.getLabel(), task.getWeight()));
        }

        for (TaskRaw task : tasks) {
            if(task == null) continue;
            for (TaskRaw taskNext : tasks) {
                if(taskNext == null) continue;
                if (task == taskNext) {
                    continue;
                }
                if (taskNext.getDependencies().contains(task.getName())) {
                    Task fromTask = new Task(task.getName());
                    Task toTask = new Task(taskNext.getName(), taskNext.getLabel(), taskNext.getWeight(), task.getWeight());
                    toTask.setToWeightActivated(true);
                    pert.addEdge(fromTask, toTask);
                }
            }
        }

        return pert;
    }

    private static PertGraf createPertGrafFromDotString(String dotString) throws IndexOutOfBoundsException{
        PertGraf p = new PertGraf();
        String[] dotStringLines = dotString.split("\n");

        for(String s : dotStringLines) {
            if (s.length() <= 9) {
                System.out.println("> empty line read");
            } else if (s.contains("#")) {
                System.out.println("> commented line read");
            } else if (s.contains("digraph")) {
                p = new PertGraf();
            } else {
                String[] parts = s.split(" ");
                int start = 0;
                if (s.startsWith(" ")) {
                    start = 1;
                }

                String labelString = parts[start + 3].substring(7, parts[start + 3].length() - 2);
                String[] labelStringParts = labelString.split(DOT_SEPARATOR);
                Task from = new Task(parts[start], labelStringParts[1], Integer.parseInt(labelStringParts[0]));
                Task to;
                to = new Task(parts[start + 2], labelStringParts[3], Integer.parseInt(labelStringParts[2]), Integer.parseInt(labelStringParts[4]));

                if (!p.adjList.containsKey(from)) {
                    p.addNode(from);
                }

                if (!p.adjList.containsKey(to)) {
                    p.addNode(to);
                }

                to.setToWeightActivated(true);
                p.addEdge(from, to);
            }
        }

        return p;
    }

    private static TaskRaw computePertLine(String line) throws InvalidFormatException
    {
        if (line.startsWith("#")) {
            return null;
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
        return new TaskRaw(name, label, weight, dependencies);
    }


    Map<Node, Integer> computeEarlyTimes() {
        Map<Node, Integer> distances = new HashMap<>();
        Task startingNode = new Task(PERT_START_NODE);

        // init Bellman-Ford
        this.adjList.forEach((node, successors) -> distances.put(node, Integer.MIN_VALUE));
        distances.put(startingNode, 0);

        boolean modified = true;
        while(modified) {
            modified = false;

            for(Node nodeFrom : this.adjList.keySet()) {
                for(Node nodeTo : this.adjList.get(nodeFrom)) {
                    if(distances.get(nodeTo) < distances.get(nodeFrom) + nodeTo.getToLabel()){
                        distances.put(nodeTo, distances.get(nodeFrom) + nodeTo.getToLabel());
                        modified = true;
                    }
                }
            }
        }

        return distances;
    }

    Map<Node, Integer> computeLateTimesFromEnd(int endingTime) {
        Map<Node, Integer> distances = new HashMap<>();
        Task startingNode = new Task(PERT_END_NODE);

        // init Bellman-Ford
        this.adjList.forEach((node, successors) -> distances.put(node, endingTime));
        distances.put(startingNode, endingTime);

        boolean modified = true;
        while(modified) {
            modified = false;

            for(Node nodeFrom : this.adjList.keySet()) {
                for(Node nodeTo : this.adjList.get(nodeFrom)) {
                    if(distances.get(nodeTo) > distances.get(nodeFrom) - nodeTo.getToLabel()){
                        distances.put(nodeTo, distances.get(nodeFrom) - nodeTo.getToLabel());
                        modified = true;
                    }
                }
            }
        }

        return distances;
    }

    PertGraf getReversePert() {
        PertGraf reverse = new PertGraf();

        // adding all nodes to the soon-to-be reversed graph
        for(Node n : this.getAllNodes()) {
            Task task = (Task) n;
            reverse.addNode(new Task(task.getName(), task.getLabel(), task.getDuration()));
        }

        // recreating all edges but with head and tail inverted
        this.adjList.forEach((nodeFrom, nodeList) -> nodeList.forEach((nodeTo) -> {
            Task newNodeTo = new Task(nodeFrom.getName(), ((Task) nodeFrom).getLabel(), ((Task) nodeFrom).getDuration(), ((Task) nodeFrom).getDuration());
            newNodeTo.setToWeightActivated(true);
            reverse.addEdge(new Task(nodeTo.getName()), newNodeTo);
        }));

        return reverse;
    }

    private void computeCriticalPathsRec(List<List<Node>> paths, List<Node> currentPath, Map<Node, Integer> earliestTimes, Map<Node, Integer> latestTimes, List<Node> currentNode) {
        boolean found = false;

        for (Node node : currentNode) {
            if (earliestTimes.get(node).equals(latestTimes.get(node))) {
                if (!found) {
                    currentPath.add(node);
                    computeCriticalPathsRec(paths, currentPath, earliestTimes, latestTimes, adjList.get(node));
                    found = true;
                }
                else {
                    List<Node> newPath = new ArrayList<>(currentPath);
                    paths.add(newPath);
                    computeCriticalPathsRec(paths, newPath, earliestTimes, latestTimes, adjList.get(node));
                }
            }
        }
    }

    List<List<Node>> computeCriticalPaths() {
        Task startingNode = addStartingTask(getStartingTasks());
        Task endingNode = addEndingTask(getEndingTasks());

        Map<Node, Integer> earliestTimes = computeEarlyTimes();

        PertGraf reversed = getReversePert();
        Map<Node, Integer> latestTimes = reversed.computeLateTimesFromEnd(earliestTimes.get(endingNode));

        removeNode(startingNode);
        removeEndingTask();

        List<List<Node>> criticalsPaths = new ArrayList<>();
        ArrayList<Node> startingTasks = getStartingTasks();

        List<Node> path = new ArrayList<>();
        criticalsPaths.add(path);
        computeCriticalPathsRec(criticalsPaths, path, earliestTimes, latestTimes, startingTasks);

        return criticalsPaths;
    }

    Task addStartingTask(ArrayList<Node> children) {
        Task start = new Task(PERT_START_NODE);
        start.setDuration(0);

        for(Node n : children) {
            n.setToWeightActivated(true);
            n.setToLabel(start.getDuration());
        }

        this.addNode(start);
        this.adjList.put(start, children);

        return start;
    }

    Task addEndingTask(ArrayList<Task> parents) {
        Task end = new Task(PERT_END_NODE);

        this.addNode(end);
        for (Map.Entry<Node, ArrayList<Node>> entry : this.adjList.entrySet()) {
            Task parent = (Task) entry.getKey();

            if(parents.contains(parent)) {
                end.setToLabel(parent.getDuration());
                end.setToWeightActivated(true);
                entry.getValue().add(end);
            }
        }

        return end;
    }

    void removeEndingTask() {
        Task end = new Task(PERT_END_NODE);

        for (Map.Entry<Node, ArrayList<Node>> entry : this.adjList.entrySet()) {
            entry.getValue().remove(end);
        }

        this.adjList.remove(end);
    }

    ArrayList<Node> getStartingTasks() {
        ArrayList<Node> startingTasks = new ArrayList<>();
        HashSet<Node> known = new HashSet<>();

        for(Node n : getAllNodes()) {
            known.addAll(this.adjList.get(n));
        }

        for(Node n : getAllNodes()) {
            if(!known.contains(n)) startingTasks.add(n);
        }

        return startingTasks;
    }

    ArrayList<Task> getEndingTasks() {
        ArrayList<Task> endingTasks = new ArrayList<>();

        for (Map.Entry<Node, ArrayList<Node>> entry : this.adjList.entrySet()) {
            if(entry.getValue().isEmpty()) endingTasks.add((Task) entry.getKey());
        }

        return endingTasks;
    }

    List<Task> computeListScheduling(int numberOfWorkers, SchedulingStrategies schedulingStrategy) {
        ArrayList<Task> working = new ArrayList<>();
        HashSet<Task> done = new HashSet<>();
        HashSet<Node> remaining = new HashSet<>(this.adjList.keySet());
        LinkedList<Task> scheduling = new LinkedList<>();

        while(!remaining.isEmpty()) {
            while(working.size() < numberOfWorkers && remaining.size() > working.size()) {
                Task toWork = getHighestPriorityTask(
                        getPendingTasks(done, working),
                        schedulingStrategy
                );

                if(toWork == null) break;

                working.add(toWork);
                scheduling.add(toWork);
            }

            for(int i = 0; i < working.size(); ) {
                Task t = working.get(i);
                t.incrementWorkedTimes();
                if(t.getWorkedTimes() >= t.getDuration()) {
                    working.remove(t);
                    remaining.remove(t);
                    done.add(t);
                } else i++;
            }
        }

        resetWorkedTimes();

        return scheduling;
    }

    private Set<Node> getPendingTasks(HashSet<Task> done, ArrayList<Task> working) {
        HashSet<Node> availableTasks = new HashSet<>(this.adjList.keySet());

        for (Map.Entry<Node, ArrayList<Node>> entry : this.adjList.entrySet()) {
            Node nodeFrom = entry.getKey();
            ArrayList<Node> nodeList = entry.getValue();
            nodeList.removeAll(done);

            //noinspection RedundantCast
            if (done.contains((Task) nodeFrom)) continue;

            availableTasks.removeAll(nodeList);
        }

        availableTasks.removeAll(done);
        availableTasks.removeAll(working);

        return availableTasks;
    }

    boolean checkPertIsTree(){return true;} //TODO implement ? or not ? or checkTaskValid

    private Task getHighestPriorityTask(Set<Node> pending, SchedulingStrategies s) {
        switch(s) {
            case LONGEST_PATH:
                return highestPriorityTaskLongestPath(pending);
            case CRITICAL_PATH:
                return highestPriorityTaskCriticalPath(pending);
            case HEFT_ALGORITHM:
                return highestPriorityTaskHEFT(pending);
            case SHORTEST_PROCESSING_TIME:
                return highestPriorityTaskSPT(pending);
            case LONGEST_PROCESSING_TIME:
                return highestPriorityTaskLPT(pending);
        }

        return null;
    }

    private Task highestPriorityTaskLongestPath(Set<Node> pending) {
        Task startingNode = addStartingTask(getStartingTasks());
        addEndingTask(getEndingTasks());

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

        removeNode(startingNode);
        removeEndingTask();

        //return its first task
        if (longestEntry != null) {
            return (Task) longestEntry.getKey().getFirst();
        }
        return null;
    }

    private Task highestPriorityTaskHEFT(Set<Node> pending) {
        Task startingNode = addStartingTask(getStartingTasks()); //TODO keep starting node in pert ?
        addEndingTask(getEndingTasks());

        PertGraf reversed = getReversePert();
        Map<Node, Integer> endTimes = reversed.computeLateTimesFromEnd(Integer.MAX_VALUE);
        Integer latestEndTime = Integer.MAX_VALUE;
        Node latestEndNode = null;

        for(Node n : pending) {
            if(latestEndTime > endTimes.get(n)) {
                latestEndTime = endTimes.get(n);
                latestEndNode = n;
            }
        }

        removeNode(startingNode);
        removeEndingTask();

        System.out.println("here HEFT end");
        return (Task) latestEndNode;
    }

    private Task highestPriorityTaskLPT(Set<Node> pending) {
        int longestProcTime = Integer.MIN_VALUE;
        Task longestProcTimeNode = null;

        for(Node n : pending) {
            Task current = (Task) n;
            if(longestProcTime < current.getDuration()) {
                longestProcTime = current.getDuration();
                longestProcTimeNode = current;
            }
        }

        return longestProcTimeNode;
    }

    private Task highestPriorityTaskSPT(Set<Node> pending) {
        int shortestProcTime = Integer.MAX_VALUE;
        Task shortestProcTimeNode = null;

        for(Node n : pending) {
            Task current = (Task) n;
            if(shortestProcTime > current.getDuration()) {
                shortestProcTime = current.getDuration();
                shortestProcTimeNode = current;
            }
        }

        return shortestProcTimeNode;
    }

    private Task highestPriorityTaskCriticalPath(Set<Node> pending) {
        PertGraf currentPert = PertGraf.getInstance();
        PertGraf newPert = PertGraf.newInstance();

        for(Node n : currentPert.getAllNodes()) {
            newPert.addNode(n);
        }
        for(Edge e : currentPert.getAllEdges()) {
            newPert.addEdge(e);
        }

        newPert.removeAncestors(pending);

        List<Task> pendingList = new ArrayList<>();
        for(Node n : pending) {
            pendingList.add((Task) n);
        }
        pendingList.sort(Comparator.comparing(Task::getDuration));

        List<List<Node>> critPaths = newPert.computeCriticalPaths();
        for (int i = pendingList.size()-1; i >= 0; i--) {
            Node pendingNode = pendingList.get(i);
            for (List<Node> path : critPaths) {
                for (Node n : path) {
                    if (n.getName().equals(pendingNode.getName())) return (Task) n;
                }
            }
        }

        if(!pendingList.isEmpty()) return pendingList.get(pendingList.size()-1);

        return null;
    }

    LongestPathInfo<Deque<Node>, Integer> computeLongestPathFrom(Node startingNode) { //TODO support multiple longest paths
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
            while (!predecessors.get(currentNode).equals(currentNode)) {
                longestPath.addFirst(currentNode);
                currentNode = predecessors.get(currentNode);
            }
            longestPath.addFirst(currentNode);

            return new LongestPathInfo<>(longestPath, maxEntry.getValue());
        }

        return new LongestPathInfo<>(longestPath, null);
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
                edges.add(new PertEdge(nodeEntry.getKey(), node, node.getToLabel()));
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
    public List<Node> getDFS() { //TODO remove ?
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
                sb.append(((Task) nodeFrom).getDuration());
                sb.append(DOT_SEPARATOR);
                sb.append(((Task) nodeFrom).getLabel());
                sb.append(DOT_SEPARATOR);
                sb.append(((Task) nodeTo).getDuration());
                sb.append(DOT_SEPARATOR);
                sb.append(((Task) nodeTo).getLabel());
                sb.append(DOT_SEPARATOR);
                sb.append(nodeTo.getToLabel());
                sb.append("]");
            }

            sb.append(";\n");
        })));

        lonelyNodes.forEach(node -> sb.append(" ").append(node.getName()).append(";\n"));

        sb.append("}");

        return sb.toString();
    }

    String[] getPertSuccessorArray()
    {
        ArrayList<Node> nodes = new ArrayList<>(this.getAllNodes());
        List<String> list = new ArrayList<>();
        nodes.sort(Comparator.comparing(Node::hashCode));
        nodes.forEach((node) -> {
            ArrayList<Node> successors = this.adjList.get(node);
            successors.forEach((nodeTo) -> list.add(nodeTo.getName()));
            list.add("0");
        });
        list.remove(list.size() - 1);
        String[] array = new String[list.size()];

        for(int i = 0; i < list.size(); ++i) {
            array[i] = list.get(i);
        }

        return array;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Node, ArrayList<Node>> entry : adjList.entrySet()) {
            boolean first = true;
            sb.append(entry.getKey().getName()).append(" -> ");
            for (Node nextTask : entry.getValue()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(nextTask.getName());
                first = false;
            }
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    String toPertString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : getAllNodes()) {
            Task task = (Task) node;
            StringBuilder buildLine = new StringBuilder(task.getName() + ", " + task.getLabel() + ", " + task.getDuration());
            List<Edge> inEdges = getInEdges(node);
            if (inEdges.isEmpty()) {
                buildLine.append(", -");
            }
            for (Edge edge : inEdges) {
                buildLine.append(", ").append(edge.getHead().getName());
            }
            sb.append(buildLine);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    void toPertFile(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(this.toPertString());
        writer.close();
    }

    private void resetWorkedTimes() {
        adjList.forEach((nodeFrom, nodeList) -> ((Task) nodeFrom).resetWorkedTimes());
    }

    private void removeAncestors(Set<Node> pending) {
        for(Node n : pending) {
            List<Edge> inEdges = getInEdges(n);
            Set<Node> ancestors = new HashSet<>();
            for(Edge e : inEdges) {
                ancestors.add(e.getHead());
            }

            removeAncestors(ancestors);

            for(Node a : ancestors) {
                removeNode(a);
            }
        }
    }
}
