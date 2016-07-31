public enum TOrientation {
    UP(0), RIGHT(90), LEFT(-90), DOWN(180);

    private int value;
    private TOrientation right;
    private TOrientation left;
    private TOrientation around;

    // Models the relation between the orientations.
    static {
        UP.right = RIGHT;
        UP.left = LEFT;
        UP.around = DOWN;
        RIGHT.right = DOWN;
        RIGHT.left = UP;
        RIGHT.around = LEFT;
        LEFT.right = UP;
        LEFT.left = DOWN;
        LEFT.around = RIGHT;
        DOWN.right = LEFT;
        DOWN.left = RIGHT;
        DOWN.around = UP;
    }

    TOrientation(int value) {
        this.value = value;
    }

    // Finds the ensuing orientation according to an action.
    public TOrientation nextOrientation(TAction action) {
        switch (action) {
            case MOVE:
                return this;
            case RIGHT:
                return right;
            case LEFT:
                return left;
            case AROUND:
                return around;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public TOrientation right() {
        return right;
    }

    public TOrientation left() {
        return left;
    }

    public TOrientation around() {
        return around;
    }
}
