import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dennis on 24.05.16.
 */
// Reference Material:
// http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
public class Dijkstra {
    public static final int ACTION_MOVE_FORWARD = 0;
    public static final int ACTION_TURN_RIGHT = 90;
    public static final int ACTION_TURN_LEFT = -90;
    public static final int ACTION_TURN_AROUND = 180;
    private static final int START_POSITION_COST = 0;
    private static final int COST_MOVE_FORWARD = 1;
    private static final int COST_LEFT_RIGHT = 2;
    private static final int COST_TURN_AROUND = 3;

    //TODO replace dijkstraGraph with vertices + edges
    private DijkstraGraph dijkstraGraph;
    private List<DijkstraVertex> openList;
    private List<DijkstraVertex> closedList;
    private List<Integer> fastestPath;

    private int[] orientations = new int[] { Main.ORIENTATION_UP, Main.ORIENTATION_RIGHT, Main.ORIENTATION_LEFT, Main.ORIENTATION_DOWN };
    private int[] actions = new int[] { ACTION_MOVE_FORWARD, ACTION_TURN_RIGHT, ACTION_TURN_LEFT, ACTION_TURN_AROUND };
    private int[] startPosition;
    private int startOrientation;
    private int[] endPosition;
    private ArrayList<int[]> obstacles;

    public Dijkstra(int[] startPosition, int startOrientation, int[] endPosition, ArrayList<int[]> obstacles) {
        this.startPosition = startPosition;
        this.startOrientation = startOrientation;
        this.endPosition = endPosition;
        this.obstacles = obstacles;
    }

    public List<Integer> getPath() {
        initVertices();
        initEdges();
        initLists();
        runDijkstra();
//        System.out.println("openlist size: " + openList.size());
//        System.out.println("closedlist size: " + closedList.size());
        extractFastestPath();
//        for(int i = 1; i <= fastestPath.size(); i++) {
//            System.out.println("Action " + (fastestPath.size() + 1 - i) + ": " + fastestPath.get(i - 1));
//        }
        invertPathOrder();
        return fastestPath;
    }

    private void invertPathOrder() {
        List<Integer> invertedList = new ArrayList<>();
        for(int i = fastestPath.size(); i > 0; i--) {
            invertedList.add(fastestPath.get(i - 1));
        }
        fastestPath = invertedList;
    }

    private void extractFastestPath() {
        fastestPath = new ArrayList<>();
        DijkstraVertex tailVertex = findEndVertex();
        while(!(Arrays.equals(tailVertex.position, startPosition) && tailVertex.orientation == startOrientation)) {
            for(DijkstraEdge edge : dijkstraGraph.dijkstraEdges) {
                if(edge.destinationVertex.equals(tailVertex) && edge.sourceVertex.equals(tailVertex.prevVertex)) {
                    fastestPath.add(edge.action);
                    tailVertex = edge.sourceVertex;
                    break;
                }
            }
        }
    }

    private DijkstraVertex findEndVertex() {
        DijkstraVertex endVertex = null;
        int minCost = DijkstraVertex.INITIAL_COST;
        for(DijkstraVertex vertex : dijkstraGraph.dijkstraVertices) {
            if(Arrays.equals(vertex.position, endPosition) && vertex.cost < minCost) {
                minCost = vertex.cost;
                endVertex = vertex;
            }
        }
        return endVertex;
    }

    private void initEdges() {
        for(DijkstraVertex vertex : dijkstraGraph.dijkstraVertices) {
            for(int action : actions) {
                if(action == ACTION_MOVE_FORWARD && !canMoveForward(vertex)) {
                    continue;
                }
                dijkstraGraph.dijkstraEdges.add(new DijkstraEdge(vertex, action));
            }
        }
    }

    //TODO delete! prints vertexes and edges in graph
    private void testOutput() {
        int counter = 0;
        for(DijkstraVertex vertex : dijkstraGraph.dijkstraVertices) {
            counter++;
            System.out.println("========================================== " + counter + " =============================================");
            System.out.println("Vertex: { X: " + vertex.getPosition()[Main.POSITION_INDEX_X] + ", Y: " + vertex.getPosition()[Main.POSITION_INDEX_Y] + ", O: " + vertex.getOrientation() + ", cost: " + vertex.getCost() + " }");
            for(DijkstraEdge edge : dijkstraGraph.dijkstraEdges) {
                if(edge.sourceVertex.equals(vertex)) {
                    System.out.println("|__Edges: { Destination: { X:" + edge.destinationVertex.getPosition()[Main.POSITION_INDEX_X] + ", Y: " + edge.destinationVertex.getPosition()[Main.POSITION_INDEX_Y] + ", O: " + edge.destinationVertex.getOrientation() + "},");
                    System.out.println("            Action: " + edge.action + "}");
                }
            }
        }
        System.out.println("Number of Vertices: " + dijkstraGraph.dijkstraVertices.size());
        System.out.println("Number of Edges: " + dijkstraGraph.dijkstraEdges.size());
    }

    private void runDijkstra() {
        //set fastestPath which is an ArrayList of Actions/Integers
        DijkstraVertex currentVertex = closedList.get(closedList.size() - 1);
        //iterate until openList is empty
        //TODO costs are working (?) only checked with very small map
        while (openList.size() > 0) {
            //TODO do dijkstra loop
//            int minCost = Integer.MAX_VALUE;
            DijkstraVertex newVertex = null;
            for(DijkstraEdge edge : dijkstraGraph.dijkstraEdges) {
                if(edge.sourceVertex.equals(currentVertex) && isInOpenList(edge.destinationVertex)) {
                    edge.destinationVertex.tryToSetCost(edge);
//                    if(edge.destinationVertex.cost < minCost) {
//                        minCost = edge.destinationVertex.cost;
//                        newVertex =  edge.destinationVertex;
//                    }
                }
            }
            newVertex = findShortestVertex();
            if(newVertex != null) {
                currentVertex = newVertex;
                closedList.add(newVertex);

                for(int i = 0; i < openList.size(); i++) {
                    if(openList.get(i).equals(newVertex)) {
                        openList.remove(i);
                        break;
                    }
                }
            }
        }
    }

    private DijkstraVertex findShortestVertex() {
        DijkstraVertex result = null;
        for(DijkstraVertex vertex : dijkstraGraph.dijkstraVertices) {
            if(vertex.cost != DijkstraVertex.INITIAL_COST && isInOpenList(vertex)) {
                if(result == null) {
                    result = vertex;
                } else if(vertex.cost < result.cost) {
                    result = vertex;
                }
            }
        }
        return result;
    }

    private boolean isInOpenList(DijkstraVertex checkVertex) {
        for(DijkstraVertex vertex : openList) {
            if(vertex.equals(checkVertex)) {
                return true;
            }
        }
        return false;
    }

    //TODO delete?
    private void initLists() {
        openList = new ArrayList<>();
        closedList = new ArrayList<>();

        for(DijkstraVertex vertex : dijkstraGraph.dijkstraVertices) {
            //adds node to the openList or to closedList if it is the startPosition/Orientation
            if (isStartVertex(vertex)) {
                vertex.cost = START_POSITION_COST;
                vertex.prevVertex = vertex;
                closedList.add(vertex);
            } else {
                openList.add(vertex);
            }
        }
    }

    private void initVertices() {
        dijkstraGraph = new DijkstraGraph();
        int[] position;

        //creates possible vertices and edges
        //iterates through every map position (map is fixed 20x8)
        for(int y = 0; y < Main.MAP_HEIGHT; y++) {
            for(int x = 0; x < Main.MAP_WIDTH; x++) {
                position = new int[] {y, x};
                //throws out positions which are obstacles
                if(!isObstacle(position)) {
                    for(int orientation : orientations) {
                        DijkstraVertex newVertex = new DijkstraVertex(new int[]{position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X]}, orientation);
                        dijkstraGraph.dijkstraVertices.add(newVertex);
                    }
//                    initVertices(position);
//                    //iterates through every possible orientation (up,right,left,down)
//                    for(int orientation : orientations) {
//                        vertex = new DijkstraVertex(new int[] { position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X] }, orientation);
//                        vertices.add(vertex);
//                        //iterates through every possible action (move, turn right, left, around)
//                        for(int action : actions) {
//                            //skips move actions which cannot be made
//                            if(action == ACTION_MOVE_FORWARD && !canMoveForward(vertex)) {
//                                continue;
//                            }
//                            edge = new DijkstraEdge(vertex, action);
//                            edges.add(edge);
//                        }
//                    }
                }
            }
        }
    }

    private void initVertices(int[] position) {
        for(int orientation : orientations) {
            DijkstraVertex newVertex = new DijkstraVertex(new int[] { position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X] }, orientation);
            dijkstraGraph.dijkstraVertices.add(newVertex);
//            if (isStartVertex(newVertex)) {
//                newVertex.tryToSetCost(START_POSITION_COST);
//                closedList.add(newVertex);
//            } else {
//                openList.add(newVertex);
//            }
        }
    }

    private boolean isStartVertex(DijkstraVertex vertex) {
        return vertex.position[Main.POSITION_INDEX_Y] == startPosition[Main.POSITION_INDEX_Y] && vertex.position[Main.POSITION_INDEX_X] == startPosition[Main.POSITION_INDEX_X] && vertex.orientation == startOrientation;
    }

    private void initEdges(DijkstraVertex dijkstraVertex) {
        for(int action : actions) {
            if(action == ACTION_MOVE_FORWARD && !canMoveForward(dijkstraVertex)) {
                continue;
            }
            dijkstraGraph.dijkstraEdges.add(new DijkstraEdge(dijkstraVertex, action));
        }
    }

    private boolean canMoveForward(DijkstraVertex vertex) {
        switch(vertex.getOrientation()) {
            case Main.ORIENTATION_UP:
                return canMoveUp(vertex.getPosition());
            case Main.ORIENTATION_RIGHT:
                return canMoveRight(vertex.getPosition());
            case Main.ORIENTATION_LEFT:
                return canMoveLeft(vertex.getPosition());
            case Main.ORIENTATION_DOWN:
                return canMoveDown(vertex.getPosition());
            default:
                return false;
        }
    }

    private boolean canMoveUp(int[] position) {
        return isValidPosition(new int[] { position[Main.POSITION_INDEX_Y] - 1, position[Main.POSITION_INDEX_X] });
    }

    private boolean canMoveRight(int[] position) {
        return isValidPosition(new int[] { position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X] + 1 });
    }

    private boolean canMoveLeft(int[] position) {
        return isValidPosition(new int[] { position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X] - 1 });
    }

    private boolean canMoveDown(int[] position) {
        return isValidPosition(new int[] { position[Main.POSITION_INDEX_Y] + 1, position[Main.POSITION_INDEX_X] });
    }

    private boolean isValidPosition(int[] position) {
        return !(position[Main.POSITION_INDEX_Y] < Main.MAP_MINIMUM_W_H || position[Main.POSITION_INDEX_Y] >= Main.MAP_HEIGHT ||
                position[Main.POSITION_INDEX_X] < Main.MAP_MINIMUM_W_H || position[Main.POSITION_INDEX_X] >= Main.MAP_WIDTH ||
                isObstacle(position));
    }

    private boolean isObstacle(int[] position) {
        for(int[] obstacle : obstacles) {
            if(Arrays.equals(position, obstacle)) {
                return true;
            }
        }
        return false;
    }

    //returns the next Vertex when executing a action on a vertex
    private int getDestinationVertex(DijkstraVertex sourceVertex, int action) {
        for(int i = 0; i < dijkstraGraph.dijkstraVertices.size(); i++) {
            if(action == ACTION_MOVE_FORWARD) {
                if(dijkstraGraph.dijkstraVertices.get(i).equals(new DijkstraVertex(nextPosition(sourceVertex), sourceVertex.orientation))) {
                    return i;
                }
            } else {
                if(dijkstraGraph.dijkstraVertices.get(i).equals(new DijkstraVertex(sourceVertex.position, nextOrientation(sourceVertex.orientation, action)))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int nextOrientation(int orientation, int action) {
        switch (action) {
            case ACTION_TURN_RIGHT:
                return nextTurnRight(orientation);
            case ACTION_TURN_LEFT:
                return nextTurnLeft(orientation);
            case ACTION_TURN_AROUND:
                return nextTurnAround(orientation);
            default:
                return Integer.MIN_VALUE;
        }
    }

    private int nextTurnRight(int orientation) {
        switch (orientation) {
            case Main.ORIENTATION_UP:
                return Main.ORIENTATION_RIGHT;
            case Main.ORIENTATION_RIGHT:
                return Main.ORIENTATION_DOWN;
            case Main.ORIENTATION_LEFT:
                return Main.ORIENTATION_UP;
            case Main.ORIENTATION_DOWN:
                return Main.ORIENTATION_LEFT;
            default:
                return Integer.MIN_VALUE;
        }
    }

    private int nextTurnLeft(int orientation) {
        switch (orientation) {
            case Main.ORIENTATION_UP:
                return Main.ORIENTATION_LEFT;
            case Main.ORIENTATION_RIGHT:
                return Main.ORIENTATION_UP;
            case Main.ORIENTATION_LEFT:
                return Main.ORIENTATION_DOWN;
            case Main.ORIENTATION_DOWN:
                return Main.ORIENTATION_RIGHT;
            default:
                return Integer.MIN_VALUE;
        }
    }

    private int nextTurnAround(int orientation) {
        switch (orientation) {
            case Main.ORIENTATION_UP:
                return Main.ORIENTATION_DOWN;
            case Main.ORIENTATION_RIGHT:
                return Main.ORIENTATION_LEFT;
            case Main.ORIENTATION_LEFT:
                return Main.ORIENTATION_RIGHT;
            case Main.ORIENTATION_DOWN:
                return Main.ORIENTATION_UP;
            default:
                return Integer.MIN_VALUE;
        }
    }

    private int[] nextPosition(DijkstraVertex sourceVertex) {
        switch (sourceVertex.getOrientation()) {
            case Main.ORIENTATION_UP:
                return nextMoveUp(sourceVertex.getPosition());
            case Main.ORIENTATION_RIGHT:
                return nextMoveRight(sourceVertex.getPosition());
            case Main.ORIENTATION_LEFT:
                return nextMoveLeft(sourceVertex.getPosition());
            case Main.ORIENTATION_DOWN:
                return nextMoveDown(sourceVertex.getPosition());
            default:
                return null;
        }
    }

    //TODO build in checks if(canMoveUp) etc
    private int[] nextMoveUp(int[] position) {
        return new int[] { position[Main.POSITION_INDEX_Y] - 1, position[Main.POSITION_INDEX_X] };
    }

    private int[] nextMoveRight(int[] position) {
        return new int[] { position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X] + 1 };
    }

    private int[] nextMoveLeft(int[] position) {
        return new int[] { position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X] - 1 };
    }

    private int[] nextMoveDown(int[] position) {
        return new int[] { position[Main.POSITION_INDEX_Y] + 1, position[Main.POSITION_INDEX_X] };
    }

    private class DijkstraGraph {
        private List<DijkstraVertex> dijkstraVertices;
        private List<DijkstraEdge> dijkstraEdges;

        public DijkstraGraph() {
            this.dijkstraVertices = new ArrayList<>();
            this.dijkstraEdges = new ArrayList<>();
        }
    }

    private class DijkstraVertex {
        //marks unvisited Nodes
        private static final int INITIAL_COST = Integer.MAX_VALUE;

        //TODO maybe add id?
        private int[] position;
        private int orientation;
        private int cost;
        private DijkstraVertex prevVertex;

        public DijkstraVertex(int[] position, int orientation) {
            this.position = position;
            this.orientation = orientation;
            cost = INITIAL_COST;
            prevVertex = null;
        }

        public int[] getPosition() {
            return position;
        }

        public int getOrientation() {
            return orientation;
        }

        public int getCost() {
            return cost;
        }

        public void tryToSetCost(DijkstraEdge newEdge) {
            if(newEdge.sourceVertex.cost == INITIAL_COST) {
                return;
            }

            int newCost = newEdge.sourceVertex.cost + newEdge.cost;
            if(newCost < cost) {
                cost = newCost;
                prevVertex = newEdge.sourceVertex;
            }
        }

        public boolean equals(DijkstraVertex vertex) {
            return vertex.position[Main.POSITION_INDEX_Y] == position[Main.POSITION_INDEX_Y] && vertex.position[Main.POSITION_INDEX_X] == position[Main.POSITION_INDEX_X] && vertex.orientation == orientation;
        }
    }

    private class DijkstraEdge {
        //TODO maybe add id?
        private final DijkstraVertex sourceVertex;
        private final DijkstraVertex destinationVertex;
        private final int action;
        private final int cost;

        public DijkstraEdge(DijkstraVertex sourceVertex, int action) {
            this.sourceVertex = sourceVertex;
            this.action = action;
            //returns the next Vertex when executing a action ("action") on a vertex ("sourceVertex")
            destinationVertex = dijkstraGraph.dijkstraVertices.get(getDestinationVertex(sourceVertex, action));
            cost = getActionCost(action);
        }

        private int getActionCost(int action) {
            switch (action) {
                case ACTION_MOVE_FORWARD:
                    return COST_MOVE_FORWARD;
                case ACTION_TURN_RIGHT:
                    return COST_LEFT_RIGHT;
                case ACTION_TURN_LEFT:
                    return COST_LEFT_RIGHT;
                case ACTION_TURN_AROUND:
                    return COST_TURN_AROUND;
                default:
                    return Integer.MAX_VALUE;
            }
        }
    }
}