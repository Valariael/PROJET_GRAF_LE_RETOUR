import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            PertGraf p = PertGraf.create("./src/testFile");
            System.out.println(p.toString());
            System.out.println(p.toDotString());

            /*Set<Node> pending = new HashSet<>();
            pending.add(new Task("E"));
            System.out.println("highest priority task : ");
            System.out.println(p.getHighestPriorityTask(pending));

            HashSet<Task> done = new HashSet<>();
            done.add(new Task("A"));
            done.add(new Task("B"));
            done.add(new Task("C"));
            done.add(new Task("D"));
            done.add(new Task("E"));
            HashSet<Task> working = new HashSet<>();
            working.add(new Task("D"));
            working.add(new Task("E"));
            Set<Node> pendingTest = p.getPendingTasks(done, working);
            System.out.println("pending tasks : ");
            for(Node n : pendingTest) System.out.println(n);
            System.out.println("highest priority task : ");
            System.out.println(p.getHighestPriorityTask(pendingTest));

            List<Task> scheduling = p.computeListScheduling(1);
            System.out.println("scheduling for 1 worker : ");
            for(Task n : scheduling) System.out.print(n + "  ");*/
            System.out.println("earliest times : ");
            for(Map.Entry e : p.computeEarliestTimes().entrySet()) System.out.print(e.getKey() + " > " + e.getValue() + "   ");

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
