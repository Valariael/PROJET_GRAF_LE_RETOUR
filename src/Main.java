import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            PertGraf p = PertGraf.create("./src/testFile");
            System.out.println(p.toString());
            System.out.println(p.toDotString());

            List<List<Node>> paths = p.computeCriticalPaths();
            for (List<Node> list : paths) {
                System.out.println("Path: ");
                for (Node node : list) {
                    System.out.print(node.getName() + " -> ");
                }
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
