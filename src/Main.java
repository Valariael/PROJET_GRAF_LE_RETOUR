import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            ///// Test /////
            PertGraf p = PertGraf.createFromPertFile("./src/testFilePdfExample");
            Objects.requireNonNull(p).computeCriticalPaths();
            List<List<Node>> paths = p.computeCriticalPaths();
            ////////////////
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
