/**
 * Created by dennis on 23.06.16.
 */
public enum TAction {
    //calibrate/tweak costs here (e.g. time to move one field forward)
    MOVE(0, 1), RIGHT(90, 2), LEFT(-90, 2), AROUND(180, 3);

    private int value;
    private int cost;

    TAction(int value, int cost) {
        this.value = value;
        this.cost = cost;
    }

    public int getValue() {
        return value;
    }

    public int getCost() {
        return cost;
    }
}
