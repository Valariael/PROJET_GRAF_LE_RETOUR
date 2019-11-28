public class PertEdge extends Edge {
    PertEdge(Node head, Node tail, int weight) {
        super(head, tail, weight);
    }

    PertEdge(Node head, Node tail) {
        super(head, tail);
    }

    @Override
    public int compareTo(Edge o) {
        if (!o.getTail().getName().equals(getTail().getName())) {
            return getTail().hashCode() - o.getTail().hashCode();
        }
        return getHead().hashCode() - o.getHead().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PertEdge)) {
            return false;
        }
        PertEdge edge = (PertEdge) obj;
        return edge.getHead().equals(getHead()) && edge.getTail().equals(getTail());
    }

    @Override
    public String toString() {
        return "Edge {from: " + getHead().toString() + " to: " + getTail().toString() + " weight: " + getWeight() + "}";
    }
}
