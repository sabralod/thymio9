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

    private DijkstraGraph dijkstraGraph;
    private ArrayList<DijkstraVertex> openList;
    private ArrayList<DijkstraVertex> closedList;
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
        initGraph();
        initLists();
        testOutput();
//        runDijkstra();
        return fastestPath;
    }

    //TODO delete! prints vertexes and edges in graph
    private void testOutput() {
        int counter = 0;
        for(DijkstraVertex vertex : dijkstraGraph.dijkstraVertices) {
            counter++;
            System.out.println("========================================== " + counter + " =============================================");
            System.out.println("Vertex: { X: " + vertex.getPosition()[Main.POSITION_INDEX_X] + ", Y: " + vertex.getPosition()[Main.POSITION_INDEX_Y] + ", O: " + vertex.getOrientation() + "}");
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
        //iterate until openList is empty
        while (openList.size() > 0) {
            //TODO do dijkstra loop
        }
        //set fastestPath which is an ArrayList of Actions/Integers
        fastestPath = new ArrayList<>();
    }

    private void initLists() {
        openList = new ArrayList<>();
        closedList = new ArrayList<>();

        for(DijkstraVertex vertex : dijkstraGraph.getDijkstraVertices()) {
            //adds node to the openList or to closedList if it is the startPosition/Orientation
            if (vertex.getPosition() == startPosition && vertex.getOrientation() == startOrientation) {
                vertex.tryToSetCost(START_POSITION_COST);
                closedList.add(vertex);
            } else {
                openList.add(vertex);
            }
        }
    }

    private void initGraph() {
        int[] position;
        ArrayList<DijkstraVertex> vertices = new ArrayList<>();
        ArrayList<DijkstraEdge> edges = new ArrayList<>();
        DijkstraVertex vertex;
        DijkstraEdge edge;

        //creates possible vertices and edges
        //iterates through every map position (map is fixed 20x8)
        for(int y = 0; y < Main.MAP_HEIGHT; y++) {
            for(int x = 0; x < Main.MAP_WIDTH; x++) {
                position = new int[] {y, x};
                //throws out positions which are obstacles
                if(!isObstacle(position)) {
                    //iterates through every possible orientation (up,right,left,down)
                    for(int orientation : orientations) {
                        vertex = new DijkstraVertex(new int[] { position[Main.POSITION_INDEX_Y], position[Main.POSITION_INDEX_X] }, orientation);
                        vertices.add(vertex);
                        //iterates through every possible action (move, turn right, left, around)
                        for(int action : actions) {
                            //skips move actions which cannot be made
                            if(action == ACTION_MOVE_FORWARD && !canMoveForward(vertex)) {
                                continue;
                            }
                            edge = new DijkstraEdge(vertex, action);
                            edges.add(edge);
                        }
                    }
                }
            }
        }
        dijkstraGraph = new DijkstraGraph(vertices, edges);
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
    private DijkstraVertex nextVertex(DijkstraVertex sourceVertex, int action) {
        if(action == ACTION_MOVE_FORWARD) {
            return new DijkstraVertex(nextPosition(sourceVertex), sourceVertex.getOrientation());
        } else {
            return new DijkstraVertex(sourceVertex.getPosition(), nextOrientation(sourceVertex.getOrientation(), action));
        }
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
        position[Main.POSITION_INDEX_Y] -= 1;
        return position;
    }

    private int[] nextMoveRight(int[] position) {
        position[Main.POSITION_INDEX_X] += 1;
        return position;
    }

    private int[] nextMoveLeft(int[] position) {
        position[Main.POSITION_INDEX_X] -= 1;
        return position;
    }

    private int[] nextMoveDown(int[] position) {
        position[Main.POSITION_INDEX_Y] += 1;
        return position;
    }

    private class DijkstraGraph {
        private List<DijkstraVertex> dijkstraVertices;
        private List<DijkstraEdge> dijkstraEdges;

        public DijkstraGraph(List<DijkstraVertex> dijkstraVertices, List<DijkstraEdge> dijkstraEdges) {
            this.dijkstraVertices = dijkstraVertices;
            this.dijkstraEdges = dijkstraEdges;
        }

        public List<DijkstraVertex> getDijkstraVertices() {
            return dijkstraVertices;
        }

        public List<DijkstraEdge> getDijkstraEdges() {
            return dijkstraEdges;
        }
    }

    private class DijkstraVertex {
        //marks unvisited Nodes
        private int INITIAL_COST = Integer.MAX_VALUE;

        //TODO maybe add id?
        private int[] position;
        private int orientation;
        private int cost;

        public DijkstraVertex(int[] position, int orientation) {
            this.position = position;
            this.orientation = orientation;
            cost = INITIAL_COST;
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

        public void tryToSetCost(int newCost) {
            if(newCost < cost) {
                cost = newCost;
            }
        }

        public boolean equals(DijkstraVertex vertex) {
            return vertex.getPosition() == position && vertex.getOrientation() == orientation;
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
            destinationVertex = nextVertex(sourceVertex, action);
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