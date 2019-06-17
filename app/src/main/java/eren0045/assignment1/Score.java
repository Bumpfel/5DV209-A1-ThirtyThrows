package eren0045.assignment1;

public enum Score {

    LOW(1, 2, 3),
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
    private int[] values;

    Score(int n) {
        value = n;
    }

    Score(int...n) {
        values = n;
    }

    public int getValue() {
        return value;
    }
}
