import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        try {
            PertGraf p = getPertFromFile("./src/testFile");
            System.out.println(p.toString());
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
                String[] parts = s.split(", ");
                if(parts.length < 4) throw new InvalidFormatException();

                Task task = new Task(parts[0].trim(), parts[1]);
                // TODO demander si la liste est toujours ordonnée
                p.addNode(task);
                durations.put(task, Integer.parseInt(parts[2]));

                if(!parts[3].equals("-")) {
                    for(int i = 3; i < parts.length; i++) {
                        Task t = new Task(parts[i]);
                        Task toTask = new Task(task.getName(), task.getLabel(), durations.get(task));
                        p.addEdge(t, toTask);
                    }
                }
            }
        }

        return p;
    }
}
