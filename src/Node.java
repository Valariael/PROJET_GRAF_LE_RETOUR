/**
 * This class is the representation of vertices as Objects. It is identified by an integer 'id' that is unique.
 */
public class Node {
    private int id;
    private String name;
    private boolean toWeightActivated = false;
    private int toWeight = 1;

    public Node(String name) {
        this.name = name;
    }

    public Node(int id) {
        this.id = id;
    }

    public Node(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Node(int id, int toWeight) {
        this.id = id;
        this.toWeight = toWeight;
    }

    public Node(int id, String name, int toWeight) {
        this.id = id;
        this.name = name;
        this.toWeight = toWeight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getToLabel() {
        return toWeight;
    }

    public void setToLabel(int toWeight) {
        this.toWeight = toWeight;
    }

    public boolean isToWeightActivated() {
        return toWeightActivated;
    }

    public void setToWeightActivated(boolean toWeightActivated) {
        this.toWeightActivated = toWeightActivated;
    }

    public String toString() {
        if(this.name == null) return "Node: id= " + this.id;
        return "Node: id= " + this.id + ", name= " + this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }
        Node otherNode = (Node) obj;
        return id == otherNode.getId();
    }

    @Override
    public int hashCode()
    {
        return this.id;
    }
}
