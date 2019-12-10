import java.io.*;
import java.util.*;

/**
 * This class class allows creation and manipulation of a PERT graph represented as an adjacency list.
 * It can be created empty or generated programmatically or from a file.
 * A Singleton Pattern was implemented as there was no need to have multiple PERT instances.
 * That way it can also be used easily throughout the project.
 *
 * @author Axel Ledermann, Augustin Bordy
 */
public class PertGraf extends Graf {
    private static final String PERT_START_NODE = "starting_node";
    private static final String PERT_END_NODE = "ending_node";
    private static final String DOT_SEPARATOR = "==";

    private static PertGraf pertInstance = null;

    private PertGraf() {}

    /**
     * Returns the current instance of PertGraf and creates it if null.
     *
     * @return the current instance of PertGraf
     */
    static PertGraf getInstance() {
        if (pertInstance == null) {
            pertInstance = new PertGraf();
        }
        return pertInstance;
    }

    /**
     * Returns an empty instance of PertGraf.
     *
     * @return a new instance of PertGraf
     */
    private static PertGraf newInstance() {
        return new PertGraf();
    }

    /**
     * Resets the current instance of PertGraf to an empty one.
     */
    static void resetInstance() {
        pertInstance = new PertGraf();
    }

    /**
     * Sets the current instance of PertGraf.
     *
     * @param p the instance of PertGraf to be used
     */
    static void setInstance(PertGraf p) {
        pertInstance = p;
    }

    /**
     * Creates a PertGraf by reading the PERT formatted file 'path' .
     *
     * @param path the path to the selected file
     * @return the corresponding PertGraf instance, null if error
     * @throws FileNotFoundException if said file could not be found
     * @throws InvalidFormatException if the data is not formatted as expected
     */
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

        return createPertGrafFromTaskRawList(tasks);
    }

    /**
     * Creates a PertGraf by reading the DOT formatted file 'path' .
     * It buffers all the lines read in the file and passes them to 'createPertGrafFromDotString'.
     *
     * @param path the path to the selected file
     * @return the corresponding PertGraf instance, null if error
     * @throws FileNotFoundException if said file could not be found
     * @throws IndexOutOfBoundsException if the data is not formatted as expected
     */
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

        return createPertGrafFromDotString(sb.toString());
    }

    //TODO
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

    /**
     * Creates a PertGraf instance from a set of task data.
     *
     * @param tasks the set of TaskRaw objects
     * @return a PertGraf instance containing the corresponding tasks
     */
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

    /**
     * Creates a PertGraf instance from a text string of DOT formatted lines.
     *
     * @param dotString a text string of DOT formatted lines
     * @return the corresponding PertGraf instance
     * @throws IndexOutOfBoundsException if the data is not formatted as expected
     */
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

    /**
     * Creates a task data object from a line of characters in the PERT format.
     *
     * @param line the string of characters to be processed
     * @return a TaskRaw object or null if necessary
     * @throws InvalidFormatException if the data is not formatted as expected
     */
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


    /**
     * Computes the early start times of the current graph.
     *
     * @return a Map associating each task:Node to the start time:Integer
     */
    Map<Node, Integer> computeEarlyTimes() {//TODO not print start node
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

    /**
     * Computes the late start times of the current graph.
     *
     * @param endingTime the ending time of the early start time
     * @return a Map associating each task:Node to the start time:Integer
     */
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

    /**
     * Computes the reverse of the current graph.
     *
     * @return the reversed PertGraf instance
     */
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

    /**
     * @param paths
     * @param currentPath
     * @param earliestTimes
     * @param latestTimes
     * @param currentNode
     *///TODO jdoc
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

    /**
     * Computes a list of the critical paths in the PERT.
     *
     * @return the list of paths as task:Node lists
     */
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

    /**
     * Adds a generic starting task to the PERT graph.
     *
     * @param children a list of tasks:Node that will depend on the starting task
     * @return a reference to the Task added
     */
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

    /**
     * Adds a generic ending task to the PERT graph.
     *
     * @param parents a list of tasks:Node that will be the ending tasks' dependencies
     * @return a reference to the Task added
     */
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

    /**
     * Removes the generic ending task from the current graph.
     */
    void removeEndingTask() {
        Task end = new Task(PERT_END_NODE);

        for (Map.Entry<Node, ArrayList<Node>> entry : this.adjList.entrySet()) {
            entry.getValue().remove(end);
        }

        this.adjList.remove(end);
    }

    /**
     * Returns a list of all the starting tasks, meaning that they have no dependency.
     *
     * @return a list of task:Node
     */
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

    /**
     * Returns a list of all the ending tasks, meaning that they have no descendant.
     *
     * @return a list of task:Node
     */
    ArrayList<Task> getEndingTasks() {
        ArrayList<Task> endingTasks = new ArrayList<>();

        for (Map.Entry<Node, ArrayList<Node>> entry : this.adjList.entrySet()) {
            if(entry.getValue().isEmpty()) endingTasks.add((Task) entry.getKey());
        }

        return endingTasks;
    }

    /**
     * Computes a scheduling of the tasks of the PERT following a given strategy.
     *
     * @param numberOfWorkers the number of workers to use in the scheduling
     * @param schedulingStrategy the strategy to be used
     * @return a list of tasks:Task to be executed in order from start to end
     */
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

    /**
     * Gets the tasks available to unassigned workers.
     *
     * @param done the set of tasks already completed
     * @param working the set of tasks being currently proceeded
     * @return a set of tasks:Node waiting to be executed
     */
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

    /**
     * Gets the task with the highest priority from the assignable tasks according to the strategy.
     *
     * @param pendingTasks the set of tasks available to unassigned workers
     * @param selectedStrategy the selected SchedulingStrategies
     * @return the Task with the highest priority
     */
    private Task getHighestPriorityTask(Set<Node> pendingTasks, SchedulingStrategies selectedStrategy) {
        switch(selectedStrategy) {
            case LONGEST_PATH:
                return highestPriorityTaskLongestPath(pendingTasks);
            case CRITICAL_PATH:
                return highestPriorityTaskCriticalPath(pendingTasks);
            case HEFT_ALGORITHM:
                return highestPriorityTaskHEFT(pendingTasks);
            case SHORTEST_PROCESSING_TIME:
                return highestPriorityTaskSPT(pendingTasks);
            case LONGEST_PROCESSING_TIME:
                return highestPriorityTaskLPT(pendingTasks);
        }

        return null;
    }

    /**
     * Gets the highest priority task from a set of tasks with the longest path algorithm.
     * It is the one that has the highest finish time on its longest path.
     *
     * @param pending the set of tasks to be evaluated
     * @return the Task with the highest priority
     */
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

    /**
     * Gets the highest priority task from a set of tasks with an adaptation of the HEFT algorithm.
     * It is based on the computation on late start times and the selected task is the one that has the highest late start time.
     *
     * @param pending the set of tasks to be evaluated
     * @return the Task with the highest priority
     */
    private Task highestPriorityTaskHEFT(Set<Node> pending) {
        Task startingNode = addStartingTask(getStartingTasks()); //TODO option to add starting and end nodes in export
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

        return (Task) latestEndNode;
    }

    /**
     * Gets the highest priority task from a set of tasks with the longest processing time method.
     * It is the one that has the highest duration.
     *
     * @param pending the set of tasks to be evaluated
     * @return the Task with the highest priority
     */
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

    /**
     * Gets the highest priority task from a set of tasks with the shortest processing time method.
     * It is the one that has the lowest duration.
     *
     * @param pending the set of tasks to be evaluated
     * @return the Task with the highest priority
     */
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

    /**
     * Gets the highest priority task from a set of tasks with the critical path method.
     * It is the one that has the highest duration and is on a critical path, or not on a critical path if there are none.
     *
     * @param pending the set of tasks to be evaluated
     * @return the Task with the highest priority
     */
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

    /**
     * Computes a longest path from a designated node.
     *
     * @param startingNode the starting node fr the longest path
     * @return a pair containing the path as a Deque of tasks:Node and its duration
     */
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
     * Recursivly search if a path exists between 2 nodes.
     *
     * @param from The starting node.
     * @param to The ending node.
     * @param covered The covered nodes until now.
     * @return <code>true</code> if a path was found, <code>false</code> if not.
     */
    private boolean pathExistsRec(Node from, Node to, Set<Node> covered) {
        if (from.equals(to)) {
            return true;
        }
        if (covered.contains(from)) {
            return false;
        }
        covered.add(from);
        List<Edge> edges = getOutEdges(from);
        for (Edge edge : edges) {
            Node node = edge.getTail();
            if (pathExistsRec(node, to, covered)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search if a path exists between 2 nodes.
     *
     * @param from The starting node.
     * @param to The ending node we're searching for.
     * @return <code>true</code> if a path was found, <code>false</code> if not.
     */
    public boolean pathExists(Node from, Node to) {
        Set<Node> covered = new HashSet<>();
        return pathExistsRec(from, to, covered);
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

    /**
     * Computes a string array representing the PERT graph as a successor array.
     *
     * @return the successor array as a string array
     */
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

    /**
     * Computes a string representation in the PERT format of the current graph, as an adjacency list.
     *
     * @return the adjacency list as a string
     */
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

    /**
     * Saves the current graphs' representation in the PERT format to the file 'path'.
     *
     * @param path the path to the designated location
     * @throws IOException if an error occurred while creating the file
     */
    void toPertFile(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(this.toPertString());
        writer.close();
    }

    /**
     * Resets to 0 the amount of time worked on each task of the graph.
     */
    private void resetWorkedTimes() {
        adjList.forEach((nodeFrom, nodeList) -> ((Task) nodeFrom).resetWorkedTimes());
    }

    /**
     * Removes all the ancestors of the nodes in 'pending'.
     *
     * @param pending the set of designated tasks:Node
     */
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
}
