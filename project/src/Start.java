import iw.ur.thymio.Thymio.Thymio;
import iw.ur.thymio.map.Map;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Start {

    private static String MAP_FILE_PATH = "map.csv";
    private static boolean USE_ASTAR = false;
    private static boolean USE_THYMIO = false;

    public static final int MAP_WIDTH = 20;
    public static final int MAP_HEIGHT = 8;
    public static final int MAP_MINIMUM_W_H = 0;
    private static final int FRONT_SENSOR = 2;
    private static final int FRONT_SENSOR_STOP_VALUE = 1000;
    private static final double ROTATE_RIGHT_VALUE = 75.0D;
    private static final double ROTATE_LEFT_VALUE = -80.0D;

    private Thymio thymio;
    private Map map;
    private double[][] probs;
    private TOrientation orientation;
    private double[][] startPosition;
    private TOrientation startOrientation;
    private double[][] endPosition;

    private List<TAction> path;

    private void initialize() {
        try {
            FileWriter obstacles = new FileWriter(MAP_FILE_PATH);
            obstacles.write(
                    "0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
                    "0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0\n" +
                    "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
                    "0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1\n" +
                    "0,0,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0\n" +
                    "0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
                    "0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
                    "0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0"
            );
//            obstacles.write(
//                    "0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
//                    "1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0\n" +
//                    "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
//                    "0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1\n" +
//                    "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
//                    "1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0\n" +
//                    "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n" +
//                    "0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1"
//            );
            obstacles.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set start position/orientation + endposition
        startPosition = new double[][]
                {{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
        startOrientation = TOrientation.UP;
        endPosition = new double[][]
                {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}};
        initMap();

        if (USE_THYMIO)
            initThymio();


        if(USE_ASTAR) {
            AStar aStar = new AStar(getStartPosition(), orientation, getEndPosition(), map.getObstacles());
            path = aStar.getPath();
        } else {
            Dijkstra dijkstra = new Dijkstra(getStartPosition(), orientation, getEndPosition(), map.getObstacles());
            path = dijkstra.getPath();
        }
    }

    private void initThymio() {
        thymio = new Thymio("192.168.10.1");
        //TODO test to find sensible value
        thymio.setMoveSensitivity(3000);
    }

    private void initMap() {
        map = new Map(MAP_FILE_PATH);
        probs = startPosition.clone();
        orientation = startOrientation;

        map.setOrientation(orientation.getValue());
        map.setProbs(probs);
        map.update();
    }

    private void runPath() {
        if (path != null) {
            for (int i = 0; i < path.size(); i++) {
                System.out.println("Vertex: Position X: " + getCurrentPosition()[TVertex.POSITION_INDEX_X] + " | Y: " + getCurrentPosition()[TVertex.POSITION_INDEX_Y] + " | Ori: " + orientation);
                System.out.println("Action " + i + ": " + path.get(i));
                switch (path.get(i)) {
                    case MOVE:
                        shortSleep();
                        moveForward();
                        break;
                    case RIGHT:
                        shortSleep();
                        turnRight();
                        break;
                    case LEFT:
                        shortSleep();
                        turnLeft();
                        break;
                    case AROUND:
                        shortSleep();
                        turnAround();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void moveForward() {
        int[] currentPosition = getCurrentPosition();
        probs[currentPosition[TVertex.POSITION_INDEX_Y]][currentPosition[TVertex.POSITION_INDEX_X]] = 0.0D;
        switch (orientation) {
            case UP:
                probs[currentPosition[TVertex.POSITION_INDEX_Y] - 1][currentPosition[TVertex.POSITION_INDEX_X]] = 1.0D;
                break;
            case RIGHT:
                probs[currentPosition[TVertex.POSITION_INDEX_Y]][currentPosition[TVertex.POSITION_INDEX_X] + 1] = 1.0D;
                break;
            case LEFT:
                probs[currentPosition[TVertex.POSITION_INDEX_Y]][currentPosition[TVertex.POSITION_INDEX_X] - 1] = 1.0D;
                break;
            case DOWN:
                probs[currentPosition[TVertex.POSITION_INDEX_Y] + 1][currentPosition[TVertex.POSITION_INDEX_X]] = 1.0D;
                break;
            default:
                probs[currentPosition[TVertex.POSITION_INDEX_Y]][currentPosition[TVertex.POSITION_INDEX_X]] = 1.0D;
                break;
        }
        map.setProbs(probs);
        map.update();

        if (USE_THYMIO) {
            //TODO maybe add check for white/black fields to keep consistency
            if (thymio.getProxHorizontal()[FRONT_SENSOR] >= FRONT_SENSOR_STOP_VALUE) {
                thymio.stop();
            } else {
                thymio.move();
            }
        }
    }

    private void turnRight() {
        switch (orientation) {
            case UP:
                orientation = TOrientation.UP.right();
                break;
            case RIGHT:
                orientation = TOrientation.RIGHT.right();
                break;
            case LEFT:
                orientation = TOrientation.LEFT.right();
                break;
            case DOWN:
                orientation = TOrientation.DOWN.right();
                break;
        }
        map.setOrientation(orientation.getValue());
        map.update();

        if (USE_THYMIO) {
            //TODO do safety checks
            thymio.rotate(ROTATE_RIGHT_VALUE);
        }
    }

    private void turnLeft() {
        switch (orientation) {
            case UP:
                orientation = TOrientation.UP.left();
                break;
            case RIGHT:
                orientation = TOrientation.RIGHT.left();
                break;
            case LEFT:
                orientation = TOrientation.LEFT.left();
                break;
            case DOWN:
                orientation = TOrientation.DOWN.left();
                break;
        }
        map.setOrientation(orientation.getValue());
        map.update();

        if (USE_THYMIO) {
            //TODO do safety checks
            thymio.rotate(ROTATE_LEFT_VALUE);
        }
    }

    private void turnAround() {
        switch (orientation) {
            case UP:
                orientation = TOrientation.UP.around();
                break;
            case RIGHT:
                orientation = TOrientation.RIGHT.around();
                break;
            case LEFT:
                orientation = TOrientation.LEFT.around();
                break;
            case DOWN:
                orientation = TOrientation.DOWN.around();
                break;
        }
        map.setOrientation(orientation.getValue());
        map.update();

        if (USE_THYMIO) {
            //TODO do safety checks
            thymio.rotate(ROTATE_RIGHT_VALUE * 2);
        }
    }

    private int[] getCurrentPosition() {
        return getPosition(probs);
    }

    private int[] getStartPosition() {
        return getPosition(startPosition);
    }

    private int[] getEndPosition() {
        return getPosition(endPosition);
    }

    public static void main(String[] args) {
        Start start = new Start();
        start.initialize();
        start.runPath();
    }

    private static int[] getPosition(double[][] position) {
        for (int y = 0; y < position.length; y++) {
            for (int x = 0; x < position[y].length; x++) {
                if (position[y][x] == 1.0D) {
                    return new int[] {y, x};
                }
            }
        }
        return null;
    }

    private static void shortSleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
