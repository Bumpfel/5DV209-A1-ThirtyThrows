package eren0045.assignment1;

public enum Score {

    LOW(0),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    ;

    private int value;

    Score(int n) {
        value = n;
    }

    public int getValue() {
        return value;
    }
}
