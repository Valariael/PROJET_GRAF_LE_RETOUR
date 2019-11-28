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

    private static PertGraf getPertFromFile(String pathToFile) throws IOException, InvalidFormatException {
        File file = new File(pathToFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        PertGraf p = new PertGraf();
        HashMap<Task, Integer> durations = new HashMap<>();

        String s;
        while ((s = br.readLine()) != null) {
            if (s.trim().startsWith("#")) {
                System.out.println("> commented line read");
            } else {
                String[] parts = s.split(",");
                if(parts.length < 4) throw new InvalidFormatException();

                Task task = new Task(parts[0].trim(), parts[1].trim());
                p.addNode(task);
                durations.put(task, Integer.parseInt(parts[2].trim()));

                if(!parts[3].trim().equals("-")) {
                    for(int i = 3; i < parts.length; i++) {
                        Task t = new Task(parts[i].trim());
                        Task toTask = new Task(task.getName(), task.getLabel(), durations.get(t));
                        toTask.setToWeightActivated(true);
                        p.addEdge(t, toTask);
                    }
                }
            }
        }

        return p;
    }
}
