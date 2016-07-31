import java.util.List;

public class TVertex {
    public static final int POSITION_INDEX_Y = 0;
    public static final int POSITION_INDEX_X = 1;
    public static final int START_POSITION_COST = 0;
    public static final int INITIAL_MIN_COST = Integer.MAX_VALUE;

    private final int[] position;
    private final int x;
    private final int y;
    private final TOrientation orientation;
    private int minCost;
    private int heuristicCost;
    private TVertex prevVertex;
    private List<TEdge> edges;

    public TVertex(int[] position, TOrientation orientation) {
        this.position = position;
        x = position[POSITION_INDEX_X];
        y = position[POSITION_INDEX_Y];
        this.orientation = orientation;
        minCost = INITIAL_MIN_COST;
        prevVertex = null;
        edges = null;
    }

    // Compares two vertices according to their x-, y- and orientation-value;
    public boolean equals(TVertex vertex) {
        return vertex.y == y && vertex.x == x && vertex.orientation == orientation;
    }

    public int[] getPosition() {
        return position;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TOrientation getOrientation() {
        return orientation;
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        this.minCost = minCost;
    }

    public int getHeuristicCost() {
        return heuristicCost;
    }

    public void setHeuristicCost(int heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    public TVertex getPrevVertex() {
        return prevVertex;
    }

    public void setPrevVertex(TVertex prevVertex) {
        this.prevVertex = prevVertex;
    }

    public List<TEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<TEdge> edges) {
        this.edges = edges;
    }
}
