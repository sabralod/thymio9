import java.util.ArrayList;

/**
 * Created by dennis on 24.05.16.
 */
public class Dijkstra {
    public static final int ACTION_MOVE_FORWARD = 0;
    public static final int ACTION_TURN_RIGHT = 90;
    public static final int ACTION_TURN_LEFT = -90;
    public static final int ACTION_TURN_AROUND = 180;
    private static final int COST_MOVE_FORWARD = 1;
    private static final int COST_LEFT_RIGHT = 2;
    private static final int COST_TURN_AROUND = 3;

    private ArrayList<DijkstraVertex> openList;
    private ArrayList<DijkstraVertex> closedList;
    private int[] fastestPath;

    private int[] orientations = new int[] {Main.ORIENTATION_UP, Main.ORIENTATION_RIGHT, Main.ORIENTATION_LEFT, Main.ORIENTATION_DOWN};
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

    public int[] getPath() {
        initGraph();
        return fastestPath;
    }

    private void initGraph() {
        int[] position;

        for(int y = 0; y < Main.MAP_HEIGHT; y++) {
            for(int x = 0; x < Main.MAP_WIDTH; x++) {
                position = new int[] {y, x};
                if(!isObstacle(position)) {
                    for(int i = 0; i < orientations.length; i++) {
                        openList.add(new DijkstraVertex(position, orientations[i]));
                    }
                }
            }
        }
    }

    private boolean isObstacle(int[] position) {
        for(int[] obstacle : obstacles) {
            if(position == obstacle) {
                return true;
            }
        }
        return false;
    }

    private class DijkstraVertex {
        //marks unvisited Nodes
        private static final int INITIAL_COST = Integer.MAX_VALUE;

        int[] position;
        int orientation;
        int cost;

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
    }

    private class DijkstraEdge {
        int action;
        int cost;

        public DijkstraEdge(int action) {
            this.action = action;
            switch (action) {
                case ACTION_MOVE_FORWARD:
                    cost = COST_MOVE_FORWARD;
                    break;
                case ACTION_TURN_RIGHT:
                    cost = COST_LEFT_RIGHT;
                    break;
                case ACTION_TURN_LEFT:
                    cost = COST_LEFT_RIGHT;
                    break;
                case ACTION_TURN_AROUND:
                    cost = COST_TURN_AROUND;
                    break;
                default:
                    cost = Integer.MAX_VALUE;
                    break;
            }
        }

        public boolean canMoveForward() {
            return false;
        }
    }
}
