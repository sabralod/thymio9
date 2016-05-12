import iw.ur.thymio.Thymio.Thymio;

/**
 * Created by simon on 12/05/16.
 */
public class Start {

    public static void main (String args[]) {
        Thymio thymio = new Thymio("192.168.10.1");
        thymio.move();
    }
}
