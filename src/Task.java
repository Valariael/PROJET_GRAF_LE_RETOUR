public class Task extends Node {
    private String label;
    private int duration;//TODO implement
    private int workedTimes;

    Task(String name) {
        super(name);
    }

    Task(String name, String label) {
        super(name);
        this.label = label;
    }

    Task(String name, String label, int intValue, boolean isMainNode) {
        super(name);
        this.label = label;
        if(isMainNode) this.duration = intValue;
        else this.setToLabel(intValue);
    }

    String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    int getDuration() {
        return duration;
    }

    int getWorkedTimes() {
        return workedTimes;
    }

    void incrementWorkedTimes() {
        workedTimes++;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public String toString() {
        return "Task {name: " + this.getName() + " label: " + this.label + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        } else {
            Node otherNode = (Node)obj;
            return this.getName().equals(otherNode.getName());
        }
    }
}
