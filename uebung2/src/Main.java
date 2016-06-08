import iw.ur.thymio.Thymio.Thymio;
import iw.ur.thymio.map.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dennis on 20.05.16.
 */
public class Main {
    //20x8 Map ist vorgegeben
    public static final int MAP_WIDTH = 20;
    public static final int MAP_HEIGHT = 8;
    public static final int MAP_MINIMUM_W_H = 0;
    public static final int ORIENTATION_UP = 0;
    public static final int ORIENTATION_RIGHT = 90;
    public static final int ORIENTATION_LEFT = -90;
    public static final int ORIENTATION_DOWN = 180;
    public static final int POSITION_INDEX_Y = 0;
    public static final int POSITION_INDEX_X = 1;

    private static Thymio thymio;
    private static Map map;
    private static double[][] probs;
    private static double[][] startPosition;
    private static double[][] endPosition;

    private static int orientation;

    public static void main(String[] args) {
        initMap();
//        initThymio();

        Dijkstra dijkstra = new Dijkstra(getStartPosition(), orientation, getEndPosition(), map.getObstacles());
        List<Integer> path = dijkstra.getPath();
        runPath(path);
    }

    private static void shortSleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runPath(List<Integer> actions) {
        //TODO console output that tells the user what actions are being taken (to document the path)
        for (Integer action : actions) {
            switch (action) {
                case Dijkstra.ACTION_MOVE_FORWARD:
                    shortSleep();
                    moveForward();
                    break;
                case Dijkstra.ACTION_TURN_RIGHT:
                    shortSleep();
                    turnRight();
                    break;
                case Dijkstra.ACTION_TURN_LEFT:
                    shortSleep();
                    turnLeft();
                    break;
                case Dijkstra.ACTION_TURN_AROUND:
                    shortSleep();
                    turnAround();
                    break;
                default:
                    System.out.println("PAINC");
                    //TODO error handling
                    break;
            }
        }
    }

    private static void initMap() {
        //map.csv is created added edited manually TODO autocreate and populate for (map/obstacles, orientation, probs/start, endpoint)
        map = new Map("map.csv");
        ArrayList<int[]> obstacles = map.getObstacles();
        probs = new double[][]
            {{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
        startPosition = probs.clone();
        endPosition = new double[][]
            {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
        orientation = 0;

        map.setOrientation(orientation);
        map.setProbs(probs);
        map.update();
    }

    private static void initThymio() {
        thymio = new Thymio("192.168.10.1");
        //TODO test to find sensible value
        thymio.setMoveSensitivity(3000);
    }

    private static void moveForward() {
        int[] currentPosition = getCurrentPosition();
        probs[currentPosition[0]][currentPosition[1]] = 0.0D;
        switch (orientation) {
            case ORIENTATION_UP:
                probs[currentPosition[POSITION_INDEX_Y] - 1][currentPosition[POSITION_INDEX_X]] = 1.0D;
                break;
            case ORIENTATION_RIGHT:
                probs[currentPosition[POSITION_INDEX_Y]][currentPosition[POSITION_INDEX_X] + 1] = 1.0D;
                break;
            case ORIENTATION_LEFT:
                probs[currentPosition[POSITION_INDEX_Y]][currentPosition[POSITION_INDEX_X] - 1] = 1.0D;
                break;
            case ORIENTATION_DOWN:
                probs[currentPosition[POSITION_INDEX_Y] + 1][currentPosition[POSITION_INDEX_X]] = 1.0D;
                break;
            default:
                probs[currentPosition[POSITION_INDEX_Y]][currentPosition[POSITION_INDEX_X]] = 1.0D;
                break;
        }
        map.setProbs(probs);
        map.update();
        //TODO do safety checks
//        thymio.move();
    }

    private static void turnRight() {
        switch (orientation) {
            case 0:
                orientation = 90;
                break;
            case 90:
                orientation = 180;
                break;
            case -90:
                orientation = 0;
                break;
            case 180:
                orientation = -90;
                break;
            default:
                //TODO error handling
                break;
        }
        map.setOrientation(orientation);
        map.update();
        //TODO do safety checks
//        thymio.rotate(90);
    }

    private static void turnLeft() {
        switch (orientation) {
            case 0:
                orientation = -90;
                break;
            case 90:
                orientation = 0;
                break;
            case -90:
                orientation = 180;
                break;
            case 180:
                orientation = 90;
                break;
            default:
                //TODO error handling
                break;
        }
        map.setOrientation(orientation);
        map.update();
        //TODO do safety checks
//        thymio.rotate(-90);
    }

    private static void turnAround() {
        switch (orientation) {
            case 0:
                orientation = 180;
                break;
            case 90:
                orientation = -90;
                break;
            case -90:
                orientation = 90;
                break;
            case 180:
                orientation = 0;
                break;
            default:
                //TODO error handling
                break;
        }
        map.setOrientation(orientation);
        map.update();
        //TODO do safety checks
//        thymio.rotate(180);
    }

    private static int[] getPosition(double[][] position) {
        for (int y = 0; y < position.length; y++) {
            for (int x = 0; x < position[y].length; x++) {
                if (position[y][x] == 1.0D) {
//                    System.out.println("y: " + y + "\nx: " + x);
                    return new int[] {y, x};
                }
            }
        }
        return null;
    }

    private static int[] getCurrentPosition() {
        return getPosition(probs);
    }

    private static int[] getStartPosition() {
        return getPosition(startPosition);
    }

    private static int[] getEndPosition() {
        return getPosition(endPosition);
    }
}
