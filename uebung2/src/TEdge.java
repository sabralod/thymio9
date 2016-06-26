/**
 * Created by dennis on 23.06.16.
 */
public class TEdge {
    private final TVertex sourceVertex;
    private final TVertex destinationVertex;
    private final TAction action;

    public TEdge(TVertex sourceVertex, TVertex destinationVertex, TAction action) {
        this.sourceVertex = sourceVertex;
        this.destinationVertex = destinationVertex;
        this.action = action;
    }

    public TVertex getSourceVertex() {
        return sourceVertex;
    }

    public TVertex getDestinationVertex() {
        return destinationVertex;
    }

    public TAction getAction() {
        return action;
    }
}
