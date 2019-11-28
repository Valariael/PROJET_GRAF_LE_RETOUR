import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PertGraf extends Graf {

    @Override
    public String toString() {
        return super.toString();
    }

    /*public static PertGraf create(String path) throws FileNotFoundException, InvalidFormatException {
        PertGraf pert = new PertGraf();
        Set<Task> tasks = new HashSet<>();

        FileReader input = new FileReader(path);
        BufferedReader reader = new BufferedReader(input);
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length < 4) {
                    throw new InvalidFormatException();
                }
                String name = details[0];
                String label = details[1];
                int weight = 0;
                try {
                    weight = Integer.parseInt(details[2]);
                }catch (NumberFormatException e) {
                    throw new InvalidFormatException();
                }
                List<String> dependencies = new ArrayList<>();
                for (int i = 3; i < details.length; i++) {
                    dependencies.add(details[i]);
                }
                tasks.add(new Task(name, label, weight, dependencies));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Pas fini
    }*/

}
