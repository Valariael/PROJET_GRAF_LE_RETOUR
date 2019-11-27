import java.util.List;

public class Task {

    String name;
    String label;
    int weight;
    List<String> dependencies;

    public Task(String name, String label, int weight, List<String> dependencies) {
        this.name = name;
        this.label = label;
        this.weight = weight;
        this.dependencies = dependencies;
    }

}
