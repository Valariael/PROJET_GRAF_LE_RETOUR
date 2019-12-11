public class Edge implements Comparable<Edge> {

    private int weight;
    private String label; //TODO: remove ?
    private Node head, tail;

    public int getWeight() {
        return weight;
    }

    public String getLabel() {
        return label;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public Edge(Node head, Node tail, int weight, String label) {
        this.weight = weight;
        this.label = label;
        this.head = head;
        this.tail = tail;
    }

    public Edge(Node head, Node tail, int weight) {
        this.weight = weight;
        this.head = head;
        this.tail = tail;
    }

    public Edge(Node head, Node tail) {
        this(head, tail, 1, ""+head.getId()+"->"+tail.getId());
    }

    @Override
    public int compareTo(Edge o) {
        if (o.getTail().getId() != getTail().getId()) {
            return getTail().getId() - o.getTail().getId();
        }
        return getHead().getId() - o.getHead().getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge edge = (Edge) obj;
        return edge.getHead().equals(head) && edge.getTail().equals(tail);
    }

    @Override
    public String toString() {
        String str = "Edge: from= " +
                head.toString() +
                ", to= " +
                tail.toString() +
                ", weight= " +
                weight;
        if(!label.isEmpty()) str += ", label= " + label;
        return str;
    }
}
