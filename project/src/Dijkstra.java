import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dennis on 24.05.16.
 */
// Reference Material:
// http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
// https://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode
public class Dijkstra {
    private List<TVertex> openList;
    private List<TVertex> closedList;
    private List<TAction> fastestPath;

    private final int[] startPosition;
    private final TOrientation startOrientation;
    private final int[] endPosition;
    private final ArrayList<int[]> obstacles;

    public Dijkstra(int[] startPosition, TOrientation startOrientation, int[] endPosition, ArrayList<int[]> obstacles) {
        this.startPosition = startPosition;
        this.startOrientation = startOrientation;
        this.endPosition = endPosition;
        this.obstacles = obstacles;
    }

    public List<TAction> getPath() {
        runDijkstra();
        return fastestPath;
    }

    private void runDijkstra() {
        openList = new ArrayList<>();
        closedList = new ArrayList<>();
        TVertex startVertex = new TVertex(startPosition, startOrientation);
        startVertex.setMinCost(TVertex.START_POSITION_COST);
        openList.add(startVertex);
        while(!openList.isEmpty()) {
            TVertex currentVertex = null;
            for(TVertex vertex : openList) {
                if(currentVertex == null) {
                    currentVertex = vertex;
                }
                else if(vertex.getMinCost() < currentVertex.getMinCost()) {
                    currentVertex = vertex;
                }
            }
            assert currentVertex != null;
            if(Arrays.equals(currentVertex.getPosition(), endPosition)) {
                reconstructPath(currentVertex);
                return;
            }
            openList.remove(currentVertex);
            closedList.add(currentVertex);
            currentVertex.setEdges(findAvailableEdges(currentVertex));
            for(TEdge edge : currentVertex.getEdges()) {
                TVertex neighbor = edge.getDestinationVertex();
                if(closedList.contains(neighbor)) {
                    continue;
                }
                if(!openList.contains(neighbor)) {
                    openList.add(neighbor);
                }
                else if(currentVertex.getMinCost() + edge.getAction().getCost() >= neighbor.getMinCost()) {
                    continue;
                }
                neighbor.setPrevVertex(currentVertex);
                neighbor.setMinCost(currentVertex.getMinCost() + edge.getAction().getCost());
            }
        }

    }

    private List<TEdge> findAvailableEdges(TVertex vertex) {
        List<TEdge> edges = new ArrayList<>();
        for(TAction action : TAction.values()) {
            if(isViableAction(vertex, action)) {
                edges.add(new TEdge(vertex, findDestinationVertex(vertex, action), action));
            }
        }
        return edges;
    }

    private void reconstructPath(TVertex vertex) {
        fastestPath = new ArrayList<>();
        while(vertex.getPrevVertex() != null) {
            for(TEdge edge : vertex.getPrevVertex().getEdges()) {
                if(edge.getDestinationVertex() == vertex) {
                    fastestPath.add(edge.getAction());
                }
            }
            vertex = vertex.getPrevVertex();
        }
        //inverts list so it is usable in the main method
        List<TAction> invertedList = new ArrayList<>();
        for(int i = fastestPath.size(); i > 0; i--) {
            invertedList.add(fastestPath.get(i - 1));
        }
        fastestPath = invertedList;
    }

    private boolean isViableAction(TVertex vertex, TAction action) {
        return action != TAction.MOVE
                || isInsideBounds(nextPosition(vertex))
                && !isObstacle(nextPosition(vertex));
    }

    private boolean isInsideBounds(int[] position) {
        return position[TVertex.POSITION_INDEX_X] >= Main.MAP_MINIMUM_W_H
                && position[TVertex.POSITION_INDEX_X] < Main.MAP_WIDTH
                && position[TVertex.POSITION_INDEX_Y] >= Main.MAP_MINIMUM_W_H
                && position[TVertex.POSITION_INDEX_Y] < Main.MAP_HEIGHT;
    }

    private boolean isObstacle(int[] position) {
        for(int[] obstacle : obstacles) {
            if(Arrays.equals(position, obstacle)) {
                return true;
            }
        }
        return false;
    }

    private TVertex findDestinationVertex(TVertex sourceVertex, TAction action) {
        TVertex newVertex;
        if(action == TAction.MOVE) {
            newVertex = new TVertex(nextPosition(sourceVertex), sourceVertex.getOrientation());
        }
        else {
            newVertex = new TVertex(sourceVertex.getPosition(), sourceVertex.getOrientation().nextOrientation(action));
        }

        for(TVertex vertex : openList) {
            if(vertex.equals(newVertex)) {
                return vertex;
            }
        }
        for(TVertex vertex : closedList) {
            if(vertex.equals(newVertex)) {
                return vertex;
            }
        }
        return newVertex;
    }

    private int[] nextPosition(TVertex sourceVertex) {
        switch (sourceVertex.getOrientation()) {
            case UP:
                return new int[] { sourceVertex.getY() - 1, sourceVertex.getX() };
            case RIGHT:
                return new int[] { sourceVertex.getY(), sourceVertex.getX() + 1 };
            case LEFT:
                return new int[] { sourceVertex.getY(), sourceVertex.getX() - 1 };
            case DOWN:
                return new int[] { sourceVertex.getY() + 1, sourceVertex.getX() };
            default:
                return null;
        }
    }
}