import iw.ur.thymio.Map;

import java.util.ArrayList;

/**
 * Created by dennis on 20.05.16.
 */
public class Main {
    public static void main(String[] args) {
        //map.csv is created added edited manually TODO autocreate and populate
        Map map = new Map("map.csv");
        ArrayList<int[]> obstacles = map.getObstacles();

        System.out.println("Obstacle at (x,y)");
        for (int[] obstacle : obstacles) {
            System.out.println("Obstacle at (" + (obstacle[1] + 1) + "," + (obstacle[0] + 1) + ")");
        }

        double[][] probs =
            {{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};

        map.setOrientation(0);
        map.setProbs(probs);
        map.update();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map.setOrientation(90);
        probs[0][0] = 0;
        probs[0][1] = 1;
        map.setProbs(probs);
        map.update();
    }
}
