import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            PertGraf p = PertGraf.createFromPertFile("./src/testFile");
            System.out.println(Objects.requireNonNull(p).toString());
            System.out.println(p.toDotString());

            ///// Test /////
            PertGraf p0 = PertGraf.create("./src/testFileSimple");
            p0.computeCriticalPaths();
            List<List<Node>> paths = p.computeCriticalPaths();
            ////////////////

            PertGraf t = PertGraf.getInstance();
            t.addNode(new Task("A"));

            /*List<List<Node>> paths = p.computeCriticalPaths();
            for (List<Node> list : paths) {
                System.out.println("Path: ");
                for (Node node : list) {
                    System.out.print(node.getName() + " -> ");
                }
            }*/
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
