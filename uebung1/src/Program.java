import iw.ur.thymio.Thymio.Thymio;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by dennis on 09.05.16.
 */
public class Program {
    private static Thymio thymio;

    public static void main (String[] args) {
        thymio = new Thymio(Constants.IP_THYMIO);

        //Sensor Value Output (to cancel press CTRL+C in your ssh instance where ./ThymioServer is running)
//        while(true) {
//            System.out.println("============================================");
//            System.out.println("Left-Sensor:            " + thymio.getProxHorizontal()[Constants.LEFT_SENSOR]);
//            System.out.println("Front-Left-Sensor:      " + thymio.getProxHorizontal()[Constants.FRONT_LEFT_SENSOR]);
//            System.out.println("Front-Sensor:           " + thymio.getProxHorizontal()[Constants.FRONT_SENSOR]);
//            System.out.println("Front-Right-Sensor:     " + thymio.getProxHorizontal()[Constants.FRONT_RIGHT_SENSOR]);
//            System.out.println("Right-Sensor:           " + thymio.getProxHorizontal()[Constants.RIGHT_SENSOR]);
//            System.out.println("Back-Left-Sensor:       " + thymio.getProxHorizontal()[Constants.BACK_LEFT_SENSOR]);
//            System.out.println("Back-Right-Sensor:      " + thymio.getProxHorizontal()[Constants.BACK_RIGHT_SENSOR]);
//            System.out.println("Ground-Left-Sensor:     " + thymio.getGroundReflected()[Constants.GROUND_LEFT_SENSOR]);
//            System.out.println("Ground-Right-Sensor:    " + thymio.getGroundReflected()[Constants.GROUND_RIGHT_SENSOR]);
//        }

//        aufgabe_1();
//        aufgabe_2();
    }

    private static void aufgabe_2() {
        try {
            FileWriter LeftSensor = new FileWriter("LeftSensor.csv");
            FileWriter RightSensor = new FileWriter("RightSensor.csv");

            int numOfSensorValues = 8;
            for (int i = 0; i < numOfSensorValues - 1; i++) {
                LeftSensor.append("" + thymio.getGroundReflected()[Constants.GROUND_LEFT_SENSOR] + ",");
                RightSensor.append("" + thymio.getGroundReflected()[Constants.GROUND_RIGHT_SENSOR] + ",");

                thymio.move();
            }
            LeftSensor.append("" + thymio.getGroundReflected()[Constants.GROUND_LEFT_SENSOR] + ",");
            RightSensor.append("" + thymio.getGroundReflected()[Constants.GROUND_RIGHT_SENSOR] + ",");

            LeftSensor.flush();
            RightSensor.flush();
            LeftSensor.close();
            RightSensor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void aufgabe_1() {
        //1. 7 Felder fahren (sofern möglich, sonst stopp)
        int numberOfFields = 7;
        moveFields(numberOfFields);

        //2. umdrehen und ein Feld zurückfahren
        thymio.rotate(Constants.TURN_AROUND_LEFT);
        thymio.move();

        //3. Drehung:
        //3.1. Falls rechts ein Hindernis: Linksdrehung
        if (thymio.getProxHorizontal()[Constants.RIGHT_SENSOR] >= Constants.RIGHT_SENSOR_STOP_VALUE) {
            thymio.rotate(Constants.TURN_LEFT);
        }

        //3.2. Falls links ein Hindernis: Rechtsdrehung
        if (thymio.getProxHorizontal()[Constants.LEFT_SENSOR] >= Constants.LEFT_SENSOR_STOP_VALUE) {
            thymio.rotate(Constants.TURN_RIGHT);
        }

        //4. 3 Felder fahren (sofern möglich, sonst stopp)
        numberOfFields = 3;
        moveFields(numberOfFields);

        //5. Stopp
        thymio.stop();
    }

    private static void moveFields(int numberOfFields) {
        //only checks for obstacles before the move command not while it is moving
        System.out.println("Trying to move " + numberOfFields + " Fields.");

        for (int i = 0; i < numberOfFields; i++) {
            if (thymio.getProxHorizontal()[Constants.FRONT_SENSOR] >= Constants.FRONT_SENSOR_STOP_VALUE) {
                thymio.stop();
                System.out.println("Obstacle detected! Stopped!");
                break;
            }
            System.out.println("Moving to Field " + (i + 1));
            thymio.move();
        }

        System.out.println("Finished moving!");
    }
}
