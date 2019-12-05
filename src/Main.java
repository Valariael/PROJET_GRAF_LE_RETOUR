import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            ///// Test /////
            PertGraf p = PertGraf.createFromPertFile("./src/testFilePdfExample");
            List<List<Node>> paths = Objects.requireNonNull(p).computeCriticalPaths();
            paths.forEach((list) -> {
                System.out.print("Path: ");
                list.forEach((elt) -> {
                    System.out.print(elt.getName() + " -> ");
                });
                System.out.println("-");
            });
            ////////////////
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
