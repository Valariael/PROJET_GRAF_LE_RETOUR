import java.util.Set;

class TaskRaw {

    private String name;
    private String label;
    private int weight;
    private Set<String> dependencies;

    TaskRaw(String name, String label, int weight, Set<String> dependencies) {
        this.name = name;
        this.label = label;
        this.weight = weight;
        this.dependencies = dependencies;
    }

    String getName() {
        return name;
    }

    String getLabel() {
        return label;
    }

    int getWeight() {
        return weight;
    }

    Set<String> getDependencies() {
        return dependencies;
    }
}
