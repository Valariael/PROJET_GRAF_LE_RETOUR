import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        try {
            PertGraf p = PertGraf.create("./src/testFile");
            System.out.println(p.toString());
            System.out.println(p.toDotString());

            HashSet<Task> pending = new HashSet<>();
            pending.add(new Task("B"));
            pending.add(new Task("D"));
            System.out.println(p.getHighestPriorityTask(pending));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
