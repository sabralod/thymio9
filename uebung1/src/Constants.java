/**
 * Created by dennis on 09.05.16.
 */
public class Constants {
    public static final String IP_THYMIO = "192.168.10.1";

    //proximity sensor number values for getProxHorizontal()[x] (tested are correct)
    public static final int LEFT_SENSOR = 0;
    public static final int FRONT_LEFT_SENSOR = 1;
    public static final int FRONT_SENSOR = 2;
    public static final int FRONT_RIGHT_SENSOR = 3;
    public static final int RIGHT_SENSOR = 4;
    public static final int BACK_LEFT_SENSOR = 5;
    public static final int BACK_RIGHT_SENSOR = 6;

    //ground sensor number values for getGroundReflected()[x] (tested are correct)
    public static final int GROUND_LEFT_SENSOR = 0;
    public static final int GROUND_RIGHT_SENSOR = 1;

    //rotation values for rotation(x) (minus is turn left, plus is turn right)
    //Korrespondiert nicht mit Gradzahlen -90/90 entspricht nicht 90° links/rechts sondern mehr (wahrscheinlich auch bissl abhängig vom Boden)
    //Außerdem fährt er kurz rückwärts nach einer Rotation.
    public static final double TURN_LEFT = -80.0D; //-60.0D; //Näherungswerte mit dem PI und dem Akku oben drauf
    public static final double TURN_RIGHT = 75.0D;
    public static final double TURN_AROUND_LEFT = 2 * TURN_LEFT;
    public static final double TURN_AROUND_RIGHT = 2 * TURN_RIGHT;
    public static final double THREESIXTY_NOSCOPE_LEFT = 4 * TURN_LEFT;
    public static final double THREESIXTY_NOSCOPE_RIGHT = 4 * TURN_RIGHT;

    //stop/obstacle sensor values
    //hab ein paar tests gemacht:
    // 1. 0 entspricht keinem Hindernis/keine Sensordaten
    // 2. Wenn Sensordaten aufgenommen werden fangen sie bei ungefähr 1000+ an
    // 3. Es gibt große Unterschiede auf welche Entfernungen die Sensoren auf Hindernisse anschlagen (weiß und reflektierend wird früh erkannt ~15cm / schwarz und matt wird sehr spät erkannt ~5cm)
    // 4. Wenn die roten Lämpchen an den Sensoren aufläuchten werden Sensordaten/-werte geliefert
    public static final int LEFT_SENSOR_STOP_VALUE = 1000;
    public static final int FRONT_SENSOR_STOP_VALUE = 1000;
    public static final int RIGHT_SENSOR_STOP_VALUE = 1000;

    //ground sensor values
    //matte schwarze Tischoberfläche ~110 / Lämpchen springen ab ~1,5cm an
    //weißes Blatt Papier = ~940 / Lämpchen springen ab ~8,5cm an
    public static final int WHITE_VALUE = 940;
    public static final int BLACK_VALUE = 110;

    //startfield value
    public static final int START_FIELD_WHITE = 1;
    public static final int START_FIELD_BLACK = -1;
}
